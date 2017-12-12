package aic.gas.sc.gg_bot.mas.model.planing.heap;

import aic.gas.sc.gg_bot.mas.model.ResponseReceiverInterface;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import aic.gas.sc.gg_bot.mas.model.planing.AbstractIntention;
import aic.gas.sc.gg_bot.mas.model.planing.DesireForOthers;
import aic.gas.sc.gg_bot.mas.model.planing.DesireFromAnotherAgent;
import aic.gas.sc.gg_bot.mas.model.planing.Intention;
import aic.gas.sc.gg_bot.mas.model.planing.IntentionCommand;
import aic.gas.sc.gg_bot.mas.model.planing.IntentionWithDesireForOtherAgents;
import aic.gas.sc.gg_bot.mas.model.planing.InternalDesire;
import aic.gas.sc.gg_bot.mas.model.planing.OwnDesire;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesireForAgents;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import aic.gas.sc.gg_bot.mas.model.planing.command.CommandForIntention;
import aic.gas.sc.gg_bot.mas.model.planing.command.ReasoningCommand;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Template for intention in top level
 */
@Slf4j
public abstract class IntentionNodeAtTopLevel<V extends Intention<? extends InternalDesire<?>>, T extends InternalDesire<V>> extends
    Node.TopLevel implements IntentionNodeInterface, VisitorAcceptor {

  final V intention;

  private IntentionNodeAtTopLevel(HeapOfTrees heapOfTrees, T desire) {
    super(heapOfTrees, desire.getDesireParameters());
    this.intention = desire.formIntention(heapOfTrees.getAgent());
  }

  abstract void formDesireNodeAndReplaceIntentionNode();

  @Override
  public void actOnRemoval() {
    intention.actOnRemoval();
  }

  /**
   * Class to extend template - to define intention node without child
   */
  public abstract static class WithCommand<V extends IntentionCommand<? extends InternalDesire<? extends IntentionCommand<?, ?>>, ?>, K extends CommandForIntention<? extends IntentionCommand<T, K>>, T extends InternalDesire<V>> extends
      IntentionNodeAtTopLevel<V, T> implements NodeWithCommand<K> {

    private WithCommand(HeapOfTrees heapOfTrees, T desire) {
      super(heapOfTrees, desire);
    }

    @Override
    public void collectKeysOfCommittedDesiresInSubtree(List<DesireKey> list) {
      list.add(getDesireKey());
    }

    @Override
    public void collectKeysOfDesiresInSubtree(List<DesireKey> list) {
      //skip
    }

    @Override
    public boolean removeCommitment(List<DesireKey> madeCommitmentToTypes,
        List<DesireKey> didNotMakeCommitmentToTypes,
        List<DesireKey> typesAboutToMakeDecision) {
      if (intention.shouldRemoveCommitment(madeCommitmentToTypes,
          didNotMakeCommitmentToTypes,
          typesAboutToMakeDecision
      )) {
        formDesireNodeAndReplaceIntentionNode();
        return true;
      }
      return false;
    }

    /**
     * Concrete implementation, intention's desire from another agent forms node
     */
    public static class FromAnotherAgent extends
        WithCommand<IntentionCommand.FromAnotherAgent, ActCommand.DesiredByAnotherAgent, DesireFromAnotherAgent.WithIntentionWithPlan> implements
        ResponseReceiverInterface<Boolean> {

      private final DesireFromAnotherAgent.WithIntentionWithPlan desire;
      private final Object lockMonitor = new Object();
      private Boolean registered = false;

      FromAnotherAgent(HeapOfTrees heapOfTrees,
          DesireFromAnotherAgent.WithIntentionWithPlan desire) {
        super(heapOfTrees, desire);
        this.desire = desire;
      }

      @Override
      public boolean removeCommitment(List<DesireKey> madeCommitmentToTypes,
          List<DesireKey> didNotMakeCommitmentToTypes,
          List<DesireKey> typesAboutToMakeDecision) {
        if (intention.shouldRemoveCommitment(madeCommitmentToTypes,
            didNotMakeCommitmentToTypes,
            typesAboutToMakeDecision,
            desire.countOfCommittedAgents()
        )) {

          //share desire and wait for response of registration
          SharedDesireForAgents sharedDesire = intention.getSharedDesireForAgents();
          synchronized (lockMonitor) {
            if (heapOfTrees.getAgent().getDesireMediator()
                .removeCommitmentToDesire(heapOfTrees.getAgent(),
                    sharedDesire,
                    this
                )) {
              try {
                lockMonitor.wait();
              } catch (InterruptedException e) {
                log.error(this.getClass().getSimpleName() + ": " + e.getLocalizedMessage());
              }

              //is desire register, if so, make intention out of it
              if (registered) {
                formDesireNodeAndReplaceIntentionNode();
                return true;
              } else {
                log.error(
                    this.getClass().getSimpleName() + ": desire for others was not registered.");
              }
            }
          }
        }
        return false;
      }

      @Override
      void formDesireNodeAndReplaceIntentionNode() {
        parent.replaceIntentionByDesire(this,
            new DesireNodeAtTopLevel.FromAnotherAgent.WithIntentionWithPlan(parent,
                heapOfTrees.getAgent().formDesireFromOtherAgentWithIntentionWithPlan(
                    desire.getDesireForAgents()).get()
            )
        );
      }

      @Override
      public ActCommand.DesiredByAnotherAgent getCommand() {
        return intention.getCommand();
      }

      @Override
      public void collectSharedDesiresForOtherAgentsInSubtree(
          Set<SharedDesireForAgents> sharedDesiresInSubtree) {
        //skip
      }

      @Override
      public void accept(TreeVisitorInterface treeVisitor) {
        treeVisitor.visit(this);
      }

      @Override
      public void receiveResponse(Boolean response) {

        //notify waiting method to decide commitment
        synchronized (lockMonitor) {
          this.registered = response;
          lockMonitor.notify();
        }
      }
    }

    /**
     * Concrete implementation, intention's desire is formed anew for intention with reasoning
     * command
     */
    public static class OwnReasoning extends
        WithCommand<IntentionCommand.OwnReasoning, ReasoningCommand, OwnDesire.Reasoning> {

      OwnReasoning(HeapOfTrees heapOfTrees, OwnDesire.Reasoning desire) {
        super(heapOfTrees, desire);
      }

      @Override
      void formDesireNodeAndReplaceIntentionNode() {
        parent.replaceIntentionByDesire(this,
            new DesireNodeAtTopLevel.Own.WithReasoningCommand(parent,
                heapOfTrees.getAgent().formOwnDesireWithReasoningCommand(
                    intention.getDesireKey())
            )
        );
      }

      @Override
      public ReasoningCommand getCommand() {
        return intention.getCommand();
      }

      @Override
      public void collectSharedDesiresForOtherAgentsInSubtree(
          Set<SharedDesireForAgents> sharedDesiresInSubtree) {
        //skip
      }

      @Override
      public void accept(TreeVisitorInterface treeVisitor) {
        treeVisitor.visit(this);
      }
    }

    /**
     * Concrete implementation, intention's desire is formed anew for intention with acting command
     */
    public static class OwnActing extends
        WithCommand<IntentionCommand.OwnActing, ActCommand.Own, OwnDesire.Acting> {

      OwnActing(HeapOfTrees heapOfTrees, OwnDesire.Acting desire) {
        super(heapOfTrees, desire);
      }

      @Override
      void formDesireNodeAndReplaceIntentionNode() {
        parent.replaceIntentionByDesire(this,
            new DesireNodeAtTopLevel.Own.WithActingCommand(parent,
                heapOfTrees.getAgent().formOwnDesireWithActingCommand(
                    intention.getDesireKey())
            )
        );
      }

      @Override
      public ActCommand.Own getCommand() {
        return intention.getCommand();
      }

      @Override
      public void collectSharedDesiresForOtherAgentsInSubtree(
          Set<SharedDesireForAgents> sharedDesiresInSubtree) {
        //skip
      }

      @Override
      public void accept(TreeVisitorInterface treeVisitor) {
        treeVisitor.visit(this);
      }
    }
  }

  /**
   * Concrete implementation, intention's desire for other agents is formed anew
   */
  public static class WithDesireForOthers extends
      IntentionNodeAtTopLevel<IntentionWithDesireForOtherAgents, DesireForOthers> {

    private final SharingDesireRemovalRoutine sharingDesireRemovalRoutine = new SharingDesireRemovalRoutine();

    WithDesireForOthers(HeapOfTrees heapOfTrees, DesireForOthers desire) {
      super(heapOfTrees, desire);
    }

    @Override
    public boolean removeCommitment(List<DesireKey> madeCommitmentToTypes,
        List<DesireKey> didNotMakeCommitmentToTypes,
        List<DesireKey> typesAboutToMakeDecision) {
      if (intention.shouldRemoveCommitment(madeCommitmentToTypes,
          didNotMakeCommitmentToTypes,
          typesAboutToMakeDecision,
          intention.getSharedDesire().countOfCommittedAgents()
      )) {

        //share desire and wait for response of registration
        if (sharingDesireRemovalRoutine
            .unregisterSharedDesire(intention.getSharedDesire(), heapOfTrees)) {
          formDesireNodeAndReplaceIntentionNode();
          return true;
        }
      }
      return false;
    }

    @Override
    public void collectKeysOfCommittedDesiresInSubtree(List<DesireKey> list) {
      list.add(getDesireKey());
    }

    @Override
    public void collectKeysOfDesiresInSubtree(List<DesireKey> list) {
      //skip
    }

    @Override
    void formDesireNodeAndReplaceIntentionNode() {
      parent.replaceIntentionByDesire(this,
          new DesireNodeAtTopLevel.ForOthers(parent,
              heapOfTrees.getAgent().formDesireForOthers(
                  getDesireKey())
          )
      );
    }

    @Override
    public void accept(TreeVisitorInterface treeVisitor) {
      treeVisitor.visit(this);
    }

    @Override
    public void collectSharedDesiresForOtherAgentsInSubtree(
        Set<SharedDesireForAgents> sharedDesiresInSubtree) {
      sharedDesiresInSubtree.add(intention.getSharedDesire());
    }
  }

  /**
   * Class to extend template - to define intention node with childes
   */
  public abstract static class WithAbstractPlan<V extends AbstractIntention<? extends InternalDesire<?>>, T extends InternalDesire<V>> extends
      IntentionNodeAtTopLevel<V, T> implements
      IntentionNodeWithChildes<IntentionNodeNotTopLevel.WithCommand.ReasoningAtTopLevelParent,
          IntentionNodeNotTopLevel.WithCommand.ActingAtTopLevelParent, IntentionNodeNotTopLevel.WithDesireForOthers.TopLevelParent,
          IntentionNodeNotTopLevel.WithAbstractPlan.TopLevelParent, DesireNodeNotTopLevel.WithCommand.ReasoningAtTopLevelParent,
          DesireNodeNotTopLevel.WithCommand.ActingAtTopLevelParent, DesireNodeNotTopLevel.ForOthers.TopLevelParent,
          DesireNodeNotTopLevel.WithAbstractPlan.TopLevelParent>,
      Parent<DesireNodeNotTopLevel<?, ?>, IntentionNodeNotTopLevel<?, ?, ?>> {

    private final SharingDesireRemovalInSubtreeRoutine sharingDesireRemovalInSubtreeRoutine = new SharingDesireRemovalInSubtreeRoutine();

    private WithAbstractPlan(HeapOfTrees heapOfTrees, T desire) {
      super(heapOfTrees, desire);
    }

    @Override
    public boolean removeCommitment(List<DesireKey> madeCommitmentToTypes,
        List<DesireKey> didNotMakeCommitmentToTypes,
        List<DesireKey> typesAboutToMakeDecision) {
      if (shouldRemoveCommitment(madeCommitmentToTypes, didNotMakeCommitmentToTypes,
          typesAboutToMakeDecision)) {

        //share desire and wait for response of registration
        Set<SharedDesireForAgents> sharedDesires = new HashSet<>();
        collectSharedDesiresForOtherAgentsInSubtree(sharedDesires);
        if (!sharedDesires.isEmpty()) {
          if (sharingDesireRemovalInSubtreeRoutine
              .unregisterSharedDesire(sharedDesires, heapOfTrees)) {
            getDesireUpdater().getNodesWithIntention()
                .forEach(IntentionNodeInterface::actOnRemoval);
            formDesireNodeAndReplaceIntentionNode();
            return true;
          }
        } else {
          getDesireUpdater().getNodesWithIntention().forEach(IntentionNodeInterface::actOnRemoval);
          formDesireNodeAndReplaceIntentionNode();
          return true;
        }
      }
      return false;
    }

    @Override
    public void actOnRemoval() {
      intention.actOnRemoval();
      getDesireUpdater().getNodesWithIntention().forEach(IntentionNodeInterface::actOnRemoval);
    }

    protected abstract boolean shouldRemoveCommitment(List<DesireKey> madeCommitmentToTypes,
        List<DesireKey> didNotMakeCommitmentToTypes,
        List<DesireKey> typesAboutToMakeDecision);

    @Override
    public void collectSharedDesiresForOtherAgentsInSubtree(
        Set<SharedDesireForAgents> sharedDesiresInSubtree) {
      getNodesWithIntention().forEach(node -> node.collectSharedDesiresForOtherAgentsInSubtree(
          sharedDesiresInSubtree));
    }

    @Override
    public List<DesireNodeNotTopLevel<?, ?>> getNodesWithDesire() {
      return getDesireUpdater().getNodesWithDesire();
    }

    @Override
    public List<IntentionNodeNotTopLevel<?, ?, ?>> getNodesWithIntention() {
      return getDesireUpdater().getNodesWithIntention();
    }

    @Override
    public Set<DesireParameters> getParametersOfCommittedDesires() {
      return getNodesWithIntention().stream()
          .map(intentionNode -> intentionNode.desireParameters)
          .collect(Collectors.toSet());
    }

    @Override
    public Set<DesireParameters> getParametersOfDesires() {
      return getNodesWithDesire().stream()
          .map(desiresNode -> desiresNode.desireParameters)
          .collect(Collectors.toSet());
    }

    @Override
    public void collectKeysOfCommittedDesiresInSubtree(List<DesireKey> list) {
      list.add(intention.getDesireKey());
      getNodesWithIntention().forEach(
          intentionNode -> intentionNode.collectKeysOfCommittedDesiresInSubtree(list));
    }

    @Override
    public void collectKeysOfDesiresInSubtree(List<DesireKey> list) {
      getNodesWithDesire().forEach(desireNode -> list.add(desireNode.getDesireKey()));
      getNodesWithIntention()
          .forEach(intentionNode -> intentionNode.collectKeysOfDesiresInSubtree(list));
    }

    @Override
    public void accept(TreeVisitorInterface treeVisitor) {
      treeVisitor.visit(this);
    }

    /**
     * Concrete implementation, abstract intention's desire from another agent forms node
     */
    static class FromAnotherAgent extends
        WithAbstractPlan<AbstractIntention<DesireFromAnotherAgent.WithAbstractIntention>, DesireFromAnotherAgent.WithAbstractIntention> {

      private final DesireUpdater<IntentionNodeNotTopLevel.WithCommand.ReasoningAtTopLevelParent,
          IntentionNodeNotTopLevel.WithCommand.ActingAtTopLevelParent, IntentionNodeNotTopLevel.WithDesireForOthers.TopLevelParent,
          IntentionNodeNotTopLevel.WithAbstractPlan.TopLevelParent, DesireNodeNotTopLevel.WithCommand.ReasoningAtTopLevelParent,
          DesireNodeNotTopLevel.WithCommand.ActingAtTopLevelParent, DesireNodeNotTopLevel.ForOthers.TopLevelParent,
          DesireNodeNotTopLevel.WithAbstractPlan.TopLevelParent, FromAnotherAgent> desireUpdater = new DesireUpdater<IntentionNodeNotTopLevel.WithCommand.ReasoningAtTopLevelParent,
          IntentionNodeNotTopLevel.WithCommand.ActingAtTopLevelParent, IntentionNodeNotTopLevel.WithDesireForOthers.TopLevelParent,
          IntentionNodeNotTopLevel.WithAbstractPlan.TopLevelParent, DesireNodeNotTopLevel.WithCommand.ReasoningAtTopLevelParent,
          DesireNodeNotTopLevel.WithCommand.ActingAtTopLevelParent, DesireNodeNotTopLevel.ForOthers.TopLevelParent,
          DesireNodeNotTopLevel.WithAbstractPlan.TopLevelParent, FromAnotherAgent>() {
        @Override
        protected void updateDesires(Set<DesireKey> desiresForOthers,
            Set<DesireKey> desiresWithAbstractIntention,
            Set<DesireKey> desiresWithIntentionToAct,
            Set<DesireKey> desiresWithIntentionToReason,
            FromAnotherAgent forNode) {
          desiresForOthers.stream()
              .map(key -> heapOfTrees.getAgent().formDesireForOthers(key,
                  intention.getDesireKey(),
                  intention.getParametersOfDesire()
              ))
              .forEach(desireForOthers -> this.addDesireWithDesireForOthers(
                  new DesireNodeNotTopLevel.ForOthers.TopLevelParent(
                      forNode,
                      desireForOthers
                  )));
          desiresWithIntentionToAct.stream()
              .map(key -> heapOfTrees.getAgent().formOwnDesireWithActingCommand(key,
                  intention.getDesireKey(),
                  intention.getParametersOfDesire()
              ))
              .forEach(acting -> this.addDesireActing(
                  new DesireNodeNotTopLevel.WithCommand.ActingAtTopLevelParent(
                      forNode,
                      acting
                  )));
          desiresWithIntentionToReason.stream()
              .map(key -> heapOfTrees.getAgent().formOwnDesireWithReasoningCommand(key,
                  intention.getDesireKey(),
                  intention.getParametersOfDesire()
              ))
              .forEach(reasoning -> this.addDesireReasoning(
                  new DesireNodeNotTopLevel.WithCommand.ReasoningAtTopLevelParent(
                      forNode,
                      reasoning
                  )));
          desiresWithAbstractIntention.stream()
              .map(key -> heapOfTrees.getAgent().formOwnDesireWithAbstractIntention(key,
                  intention.getDesireKey(),
                  intention.getParametersOfDesire()
              ))
              .forEach(withAbstractIntention -> this.addDesireWithAbstractPlan(
                  new DesireNodeNotTopLevel.WithAbstractPlan.TopLevelParent(
                      forNode,
                      withAbstractIntention
                  )));
        }
      };

      private final DesireFromAnotherAgent.WithAbstractIntention desire;

      FromAnotherAgent(HeapOfTrees heapOfTrees,
          DesireFromAnotherAgent.WithAbstractIntention desire) {
        super(heapOfTrees, desire);
        this.desire = desire;
        desireUpdater.initDesires(intention, this);
      }

      @Override
      protected boolean shouldRemoveCommitment(List<DesireKey> madeCommitmentToTypes,
          List<DesireKey> didNotMakeCommitmentToTypes,
          List<DesireKey> typesAboutToMakeDecision) {
        return intention.shouldRemoveCommitment(madeCommitmentToTypes,
            didNotMakeCommitmentToTypes,
            typesAboutToMakeDecision,
            desire.countOfCommittedAgents()
        );
      }

      @Override
      public void updateDesires() {
        desireUpdater.updateDesires(this);
      }

      @Override
      void formDesireNodeAndReplaceIntentionNode() {
        parent.replaceIntentionByDesire(this,
            new DesireNodeAtTopLevel.FromAnotherAgent.WithAbstractIntention(parent,
                desire
            )
        );
      }

      @Override
      public Optional<DesireKey> getDesireKeyAssociatedWithParent() {
        return Optional.of(getDesireKey());
      }

      @Override
      public DesireUpdater<IntentionNodeNotTopLevel.WithCommand.ReasoningAtTopLevelParent, IntentionNodeNotTopLevel.WithCommand.ActingAtTopLevelParent, IntentionNodeNotTopLevel.WithDesireForOthers.TopLevelParent, IntentionNodeNotTopLevel.WithAbstractPlan.TopLevelParent, DesireNodeNotTopLevel.WithCommand.ReasoningAtTopLevelParent, DesireNodeNotTopLevel.WithCommand.ActingAtTopLevelParent, DesireNodeNotTopLevel.ForOthers.TopLevelParent, DesireNodeNotTopLevel.WithAbstractPlan.TopLevelParent, ?> getDesireUpdater() {
        return desireUpdater;
      }
    }

    /**
     * Concrete implementation, abstract intention's desire is formed anew
     */
    static class Own extends
        WithAbstractPlan<AbstractIntention<OwnDesire.WithAbstractIntention>, OwnDesire.WithAbstractIntention> {

      private final DesireUpdater<IntentionNodeNotTopLevel.WithCommand.ReasoningAtTopLevelParent,
          IntentionNodeNotTopLevel.WithCommand.ActingAtTopLevelParent, IntentionNodeNotTopLevel.WithDesireForOthers.TopLevelParent,
          IntentionNodeNotTopLevel.WithAbstractPlan.TopLevelParent, DesireNodeNotTopLevel.WithCommand.ReasoningAtTopLevelParent,
          DesireNodeNotTopLevel.WithCommand.ActingAtTopLevelParent, DesireNodeNotTopLevel.ForOthers.TopLevelParent,
          DesireNodeNotTopLevel.WithAbstractPlan.TopLevelParent, Own> desireUpdater = new DesireUpdater<IntentionNodeNotTopLevel.WithCommand.ReasoningAtTopLevelParent,
          IntentionNodeNotTopLevel.WithCommand.ActingAtTopLevelParent, IntentionNodeNotTopLevel.WithDesireForOthers.TopLevelParent,
          IntentionNodeNotTopLevel.WithAbstractPlan.TopLevelParent, DesireNodeNotTopLevel.WithCommand.ReasoningAtTopLevelParent,
          DesireNodeNotTopLevel.WithCommand.ActingAtTopLevelParent, DesireNodeNotTopLevel.ForOthers.TopLevelParent,
          DesireNodeNotTopLevel.WithAbstractPlan.TopLevelParent, Own>() {
        @Override
        protected void updateDesires(Set<DesireKey> desiresForOthers,
            Set<DesireKey> desiresWithAbstractIntention,
            Set<DesireKey> desiresWithIntentionToAct,
            Set<DesireKey> desiresWithIntentionToReason,
            Own forNode) {
          desiresForOthers.stream()
              .map(key -> heapOfTrees.getAgent().formDesireForOthers(key,
                  intention.getDesireKey(),
                  intention.getParametersOfDesire()
              ))
              .forEach(desireForOthers -> this.addDesireWithDesireForOthers(
                  new DesireNodeNotTopLevel.ForOthers.TopLevelParent(
                      forNode,
                      desireForOthers
                  )));
          desiresWithIntentionToAct.stream()
              .map(key -> heapOfTrees.getAgent().formOwnDesireWithActingCommand(key,
                  intention.getDesireKey(),
                  intention.getParametersOfDesire()
              ))
              .forEach(acting -> this.addDesireActing(
                  new DesireNodeNotTopLevel.WithCommand.ActingAtTopLevelParent(
                      forNode,
                      acting
                  )));
          desiresWithIntentionToReason.stream()
              .map(key -> heapOfTrees.getAgent().formOwnDesireWithReasoningCommand(key,
                  intention.getDesireKey(),
                  intention.getParametersOfDesire()
              ))
              .forEach(reasoning -> this.addDesireReasoning(
                  new DesireNodeNotTopLevel.WithCommand.ReasoningAtTopLevelParent(
                      forNode,
                      reasoning
                  )));
          desiresWithAbstractIntention.stream()
              .map(key -> heapOfTrees.getAgent().formOwnDesireWithAbstractIntention(key,
                  intention.getDesireKey(),
                  intention.getParametersOfDesire()
              ))
              .forEach(withAbstractIntention -> this.addDesireWithAbstractPlan(
                  new DesireNodeNotTopLevel.WithAbstractPlan.TopLevelParent(
                      forNode,
                      withAbstractIntention
                  )));
        }
      };

      Own(HeapOfTrees heapOfTrees, OwnDesire.WithAbstractIntention desire) {
        super(heapOfTrees, desire);
        desireUpdater.initDesires(intention, this);
      }

      @Override
      protected boolean shouldRemoveCommitment(List<DesireKey> madeCommitmentToTypes,
          List<DesireKey> didNotMakeCommitmentToTypes,
          List<DesireKey> typesAboutToMakeDecision) {
        return intention.shouldRemoveCommitment(madeCommitmentToTypes,
            didNotMakeCommitmentToTypes,
            typesAboutToMakeDecision
        );
      }

      @Override
      public void updateDesires() {
        desireUpdater.updateDesires(this);
      }

      @Override
      void formDesireNodeAndReplaceIntentionNode() {
        parent.replaceIntentionByDesire(this,
            new DesireNodeAtTopLevel.Own.WithAbstractIntention(parent,
                heapOfTrees.getAgent().formOwnDesireWithAbstractIntention(
                    intention.getDesireKey())
            )
        );
      }

      @Override
      public DesireUpdater<IntentionNodeNotTopLevel.WithCommand.ReasoningAtTopLevelParent, IntentionNodeNotTopLevel.WithCommand.ActingAtTopLevelParent, IntentionNodeNotTopLevel.WithDesireForOthers.TopLevelParent, IntentionNodeNotTopLevel.WithAbstractPlan.TopLevelParent, DesireNodeNotTopLevel.WithCommand.ReasoningAtTopLevelParent, DesireNodeNotTopLevel.WithCommand.ActingAtTopLevelParent, DesireNodeNotTopLevel.ForOthers.TopLevelParent, DesireNodeNotTopLevel.WithAbstractPlan.TopLevelParent, ?> getDesireUpdater() {
        return desireUpdater;
      }

      @Override
      public Optional<DesireKey> getDesireKeyAssociatedWithParent() {
        return Optional.of(getDesireKey());
      }
    }

  }

}
