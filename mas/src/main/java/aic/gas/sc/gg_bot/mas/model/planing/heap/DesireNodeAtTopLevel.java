package aic.gas.sc.gg_bot.mas.model.planing.heap;

import aic.gas.sc.gg_bot.mas.model.ResponseReceiverInterface;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.planing.DesireForOthers;
import aic.gas.sc.gg_bot.mas.model.planing.DesireFromAnotherAgent;
import aic.gas.sc.gg_bot.mas.model.planing.Intention;
import aic.gas.sc.gg_bot.mas.model.planing.InternalDesire;
import aic.gas.sc.gg_bot.mas.model.planing.OwnDesire;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesireForAgents;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesireInRegister;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * Template for desire in top level
 */
@Slf4j
public abstract class DesireNodeAtTopLevel<T extends InternalDesire<? extends Intention>> extends
    Node.TopLevel implements DesireNodeInterface<IntentionNodeAtTopLevel<?, ?>> {

  final T desire;

  private DesireNodeAtTopLevel(HeapOfTrees heapOfTrees, T desire) {
    super(heapOfTrees, desire.getDesireParameters());
    this.desire = desire;
  }

  @Override
  public DesireKey getAssociatedDesireKey() {
    return getDesireKey();
  }

  /**
   * Implementation of top node with desire for other agents
   */
  static class ForOthers extends DesireNodeAtTopLevel<DesireForOthers> {

    private final SharingDesireRoutine sharingDesireRoutine = new SharingDesireRoutine();

    ForOthers(HeapOfTrees heapOfTrees, DesireForOthers desire) {
      super(heapOfTrees, desire);
    }

    @Override
    public Optional<IntentionNodeAtTopLevel<?, ?>> makeCommitment(
        List<DesireKey> madeCommitmentToTypes, List<DesireKey> didNotMakeCommitmentToTypes,
        List<DesireKey> typesAboutToMakeDecision) {
      if (desire.shouldCommit(madeCommitmentToTypes, didNotMakeCommitmentToTypes,
          typesAboutToMakeDecision)) {
        IntentionNodeAtTopLevel.WithDesireForOthers node = new IntentionNodeAtTopLevel.WithDesireForOthers(
            parent, desire);
        SharedDesireInRegister sharedDesire = node.intention.makeDesireToShare();
        if (sharingDesireRoutine.sharedDesire(sharedDesire, heapOfTrees)) {
          heapOfTrees.addSharedDesireForOtherAgents(node.intention.getSharedDesire());
          parent.replaceDesireByIntention(this, node);
          return Optional.of(node);
        }
      }
      return Optional.empty();
    }
  }

  /**
   * Template for nodes with desires from another agent
   */
  abstract static class FromAnotherAgent<V extends DesireFromAnotherAgent<? extends Intention>> extends
      DesireNodeAtTopLevel<V> implements
      ResponseReceiverInterface<Optional<SharedDesireForAgents>> {

    private final Object lockMonitor = new Object();

    FromAnotherAgent(HeapOfTrees heapOfTrees, V desire) {
      super(heapOfTrees, desire);
    }

    abstract IntentionNodeAtTopLevel<?, ?> formIntentionNodeAndReplaceSelfInParent();

    @Override
    public Optional<IntentionNodeAtTopLevel<?, ?>> makeCommitment(
        List<DesireKey> madeCommitmentToTypes, List<DesireKey> didNotMakeCommitmentToTypes,
        List<DesireKey> typesAboutToMakeDecision) {
      if (desire.getDesireForAgents().mayTryToCommit() && desire.shouldCommit(madeCommitmentToTypes,
          didNotMakeCommitmentToTypes, typesAboutToMakeDecision, desire.committedAgents())) {

        synchronized (lockMonitor) {
          if (heapOfTrees.getAgent().getDesireMediator()
              .addCommitmentToDesire(parent.getAgent(), desire.getDesireForAgents(), this)) {

            //wait for registered
            try {
              lockMonitor.wait();
            } catch (InterruptedException e) {
              log.error(this.getClass().getSimpleName() + ": " + e.getLocalizedMessage());
            }

            //if agent is committed according to system...
            if (desire.getDesireForAgents().isAgentCommittedToDesire(parent.getAgent())) {
              return Optional.of(formIntentionNodeAndReplaceSelfInParent());
            }
          }
        }
      }
      return Optional.empty();
    }

    @Override
    public void receiveResponse(Optional<SharedDesireForAgents> response) {

      //update desire if registered is nonempty and notify waiting method to decide commitment
      synchronized (lockMonitor) {
        response.ifPresent(sharedDesireForAgents -> desire.getDesireForAgents()
            .updateCommittedAgentsSet(sharedDesireForAgents));
        lockMonitor.notify();
      }
    }

    /**
     * Concrete implementation, desire forms intention with abstract plan
     */
    static class WithAbstractIntention extends
        FromAnotherAgent<DesireFromAnotherAgent.WithAbstractIntention> {

      WithAbstractIntention(HeapOfTrees heapOfTrees,
          DesireFromAnotherAgent.WithAbstractIntention desire) {
        super(heapOfTrees, desire);
      }

      @Override
      IntentionNodeAtTopLevel<?, ?> formIntentionNodeAndReplaceSelfInParent() {
        IntentionNodeAtTopLevel.WithAbstractPlan.FromAnotherAgent node = new IntentionNodeAtTopLevel.WithAbstractPlan.FromAnotherAgent(
            parent, desire);
        parent.replaceDesireByIntention(this, node);
        return node;
      }
    }

    /**
     * Concrete implementation, desire forms intention with concrete plan (command)
     */
    static class WithIntentionWithPlan extends
        FromAnotherAgent<DesireFromAnotherAgent.WithIntentionWithPlan> {

      WithIntentionWithPlan(HeapOfTrees heapOfTrees,
          DesireFromAnotherAgent.WithIntentionWithPlan desire) {
        super(heapOfTrees, desire);
      }

      @Override
      IntentionNodeAtTopLevel<?, ?> formIntentionNodeAndReplaceSelfInParent() {
        IntentionNodeAtTopLevel.WithCommand.FromAnotherAgent node = new IntentionNodeAtTopLevel.WithCommand.FromAnotherAgent(
            parent, desire);
        parent.replaceDesireByIntention(this, node);
        return node;
      }
    }

  }

  /**
   * Template for nodes with own desires
   */
  abstract static class Own<V extends OwnDesire<? extends Intention>> extends
      DesireNodeAtTopLevel<V> {

    Own(HeapOfTrees heapOfTrees, V desire) {
      super(heapOfTrees, desire);
    }

    @Override
    public Optional<IntentionNodeAtTopLevel<?, ?>> makeCommitment(
        List<DesireKey> madeCommitmentToTypes, List<DesireKey> didNotMakeCommitmentToTypes,
        List<DesireKey> typesAboutToMakeDecision) {
      if (desire.shouldCommit(madeCommitmentToTypes, didNotMakeCommitmentToTypes,
          typesAboutToMakeDecision)) {
        return Optional.of(formIntentionNodeAndReplaceSelfInParent());
      }
      return Optional.empty();
    }

    abstract IntentionNodeAtTopLevel<?, ?> formIntentionNodeAndReplaceSelfInParent();

    /**
     * Concrete implementation, desire forms intention with abstract plan
     */
    static class WithAbstractIntention extends Own<OwnDesire.WithAbstractIntention> {

      WithAbstractIntention(HeapOfTrees heapOfTrees, OwnDesire.WithAbstractIntention desire) {
        super(heapOfTrees, desire);
      }

      @Override
      IntentionNodeAtTopLevel<?, ?> formIntentionNodeAndReplaceSelfInParent() {
        IntentionNodeAtTopLevel.WithAbstractPlan.Own node = new IntentionNodeAtTopLevel.WithAbstractPlan.Own(
            parent, desire);
        parent.replaceDesireByIntention(this, node);
        return node;
      }
    }

    /**
     * Concrete implementation, desire forms intention with acting command
     */
    static class WithActingCommand extends Own<OwnDesire.Acting> {

      WithActingCommand(HeapOfTrees heapOfTrees, OwnDesire.Acting desire) {
        super(heapOfTrees, desire);
      }

      @Override
      IntentionNodeAtTopLevel<?, ?> formIntentionNodeAndReplaceSelfInParent() {
        IntentionNodeAtTopLevel.WithCommand.OwnActing node = new IntentionNodeAtTopLevel.WithCommand.OwnActing(
            parent, desire);
        parent.replaceDesireByIntention(this, node);
        return node;
      }
    }

    /**
     * Concrete implementation, desire forms intention with reasoning command
     */
    static class WithReasoningCommand extends Own<OwnDesire.Reasoning> {

      WithReasoningCommand(HeapOfTrees heapOfTrees, OwnDesire.Reasoning desire) {
        super(heapOfTrees, desire);
      }

      @Override
      IntentionNodeAtTopLevel<?, ?> formIntentionNodeAndReplaceSelfInParent() {
        IntentionNodeAtTopLevel.WithCommand.OwnReasoning node = new IntentionNodeAtTopLevel.WithCommand.OwnReasoning(
            parent, desire);
        parent.replaceDesireByIntention(this, node);
        return node;
      }
    }

  }

}
