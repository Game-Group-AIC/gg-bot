package aic.gas.sc.gg_bot.mas.model.planing.heap;

import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.planing.*;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import aic.gas.sc.gg_bot.mas.model.planing.command.CommandForIntention;
import aic.gas.sc.gg_bot.mas.model.planing.command.ReasoningCommand;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * Template for desire not in top level
 */
@Slf4j
public abstract class DesireNodeNotTopLevel<T extends InternalDesire<? extends Intention>, K extends Node & IIntentionNodeWithChildes & IParent<?, ?>> extends
    Node.NotTopLevel<K> implements IDesireNode<IntentionNodeNotTopLevel<?, ?, ?>> {

  final T desire;

  private DesireNodeNotTopLevel(K parent, T desire) {
    super(parent, desire.getDesireParameters());
    this.desire = desire;
  }

  abstract IntentionNodeNotTopLevel<?, ?, ?> formDesireNodeAndReplaceIntentionNode();

  public Optional<IntentionNodeNotTopLevel<?, ?, ?>> makeCommitment(
      List<DesireKey> madeCommitmentToTypes, List<DesireKey> didNotMakeCommitmentToTypes,
      List<DesireKey> typesAboutToMakeDecision) {
    if (desire.shouldCommit(madeCommitmentToTypes, didNotMakeCommitmentToTypes,
        typesAboutToMakeDecision)) {
      return Optional.of(formDesireNodeAndReplaceIntentionNode());
    }
    return Optional.empty();
  }

  @Override
  public DesireKey getAssociatedDesireKey() {
    return getDesireKey();
  }

  /**
   * Implementation of top node with desire for other agents
   */
  static abstract class ForOthers<K extends Node & IIntentionNodeWithChildes & IParent<?, ?>> extends
      DesireNodeNotTopLevel<DesireForOthers, K> {

    final SharingDesireRoutine sharingDesireRoutine = new SharingDesireRoutine();

    private ForOthers(K parent, DesireForOthers desire) {
      super(parent, desire);
    }

    @Override
    IntentionNodeNotTopLevel<?, ?, ?> formDesireNodeAndReplaceIntentionNode() {
      log.error("Accessing method which should be never used.");
      throw new RuntimeException("Accessing method which should be never used.");
    }

    /**
     * Parent is in top level
     */
    static class TopLevelParent extends ForOthers<IntentionNodeAtTopLevel.WithAbstractPlan<?, ?>> {

      TopLevelParent(IntentionNodeAtTopLevel.WithAbstractPlan parent, DesireForOthers desire) {
        super(parent, desire);
      }

      @Override
      public Optional<IntentionNodeNotTopLevel<?, ?, ?>> makeCommitment(
          List<DesireKey> madeCommitmentToTypes, List<DesireKey> didNotMakeCommitmentToTypes,
          List<DesireKey> typesAboutToMakeDecision) {
        if (desire.shouldCommit(madeCommitmentToTypes, didNotMakeCommitmentToTypes,
            typesAboutToMakeDecision)) {
          IntentionNodeNotTopLevel.WithDesireForOthers.TopLevelParent node = new IntentionNodeNotTopLevel.WithDesireForOthers.TopLevelParent(
              parent, desire);
          SharedDesireInRegister sharedDesire = node.intention.makeDesireToShare();
          if (sharingDesireRoutine.sharedDesire(sharedDesire, heapOfTrees)) {
            heapOfTrees.addSharedDesireForOtherAgents(node.intention.getSharedDesire());
            parent.replaceDesireByIntentionWithDesireForOthers(this, node);
            return Optional.of(node);
          }
        }
        return Optional.empty();
      }
    }

    /**
     * Parent is not in top level
     */
    static class NotTopLevelParent extends
        ForOthers<IntentionNodeNotTopLevel.WithAbstractPlan<?, ?, ?>> {

      NotTopLevelParent(IntentionNodeNotTopLevel.WithAbstractPlan parent, DesireForOthers desire) {
        super(parent, desire);
      }

      @Override
      public Optional<IntentionNodeNotTopLevel<?, ?, ?>> makeCommitment(
          List<DesireKey> madeCommitmentToTypes, List<DesireKey> didNotMakeCommitmentToTypes,
          List<DesireKey> typesAboutToMakeDecision) {
        if (desire.shouldCommit(madeCommitmentToTypes, didNotMakeCommitmentToTypes,
            typesAboutToMakeDecision)) {
          IntentionNodeNotTopLevel.WithDesireForOthers.NotTopLevelParent node = new IntentionNodeNotTopLevel.WithDesireForOthers.NotTopLevelParent(
              parent, desire);
          SharedDesireInRegister sharedDesire = node.intention.makeDesireToShare();
          if (sharingDesireRoutine.sharedDesire(sharedDesire, heapOfTrees)) {
            heapOfTrees.addSharedDesireForOtherAgents(node.intention.getSharedDesire());
            parent.replaceDesireByIntentionWithDesireForOthers(this, node);
            return Optional.of(node);
          }
        }
        return Optional.empty();
      }

    }

  }

  /**
   * Implementation of top node with desire for other agents
   */
  static abstract class WithAbstractPlan<T extends InternalDesire<? extends Intention>, K extends Node & IIntentionNodeWithChildes & IParent<?, ?>> extends
      DesireNodeNotTopLevel<T, K> {

    private WithAbstractPlan(K parent, T desire) {
      super(parent, desire);
    }

    /**
     * Parent is in top level
     */
    static class TopLevelParent extends
        WithAbstractPlan<OwnDesire.WithAbstractIntention, IntentionNodeAtTopLevel.WithAbstractPlan<?, ?>> {

      TopLevelParent(IntentionNodeAtTopLevel.WithAbstractPlan parent,
          OwnDesire.WithAbstractIntention desire) {
        super(parent, desire);
      }

      @Override
      IntentionNodeNotTopLevel<?, ?, ?> formDesireNodeAndReplaceIntentionNode() {
        IntentionNodeNotTopLevel.WithAbstractPlan.TopLevelParent node = new IntentionNodeNotTopLevel.WithAbstractPlan.TopLevelParent(
            parent, desire);
        parent.replaceDesireByIntentionWithAbstractPlan(this, node);
        return node;
      }
    }

    /**
     * Parent is in top level
     */
    static class NotTopLevelParent extends
        WithAbstractPlan<OwnDesire.WithAbstractIntention, IntentionNodeNotTopLevel.WithAbstractPlan<?, ?, ?>> {

      NotTopLevelParent(IntentionNodeNotTopLevel.WithAbstractPlan parent,
          OwnDesire.WithAbstractIntention desire) {
        super(parent, desire);
      }

      @Override
      IntentionNodeNotTopLevel<?, ?, ?> formDesireNodeAndReplaceIntentionNode() {
        IntentionNodeNotTopLevel.WithAbstractPlan.NotTopLevelParent node = new IntentionNodeNotTopLevel.WithAbstractPlan.NotTopLevelParent(
            parent, desire);
        parent.replaceDesireByIntentionWithAbstractPlan(this, node);
        return node;
      }
    }

  }

  /**
   * Class to extend template - to define desire node without child
   */
  abstract static class WithCommand<K extends Node & IIntentionNodeWithChildes & IParent<?, ?>, V extends InternalDesire<? extends IntentionCommand<V, T>>, T extends CommandForIntention<? extends IntentionCommand<V, T>>> extends
      WithAbstractPlan<V, K> {

    private WithCommand(K parent, V desire) {
      super(parent, desire);
    }

    /**
     * Concrete implementation, intention's desire is formed anew with acting command
     */
    static class ActingAtTopLevelParent extends
        WithCommand<IntentionNodeAtTopLevel.WithAbstractPlan<?, ?>, OwnDesire.Acting, ActCommand.Own> {

      ActingAtTopLevelParent(IntentionNodeAtTopLevel.WithAbstractPlan parent,
          OwnDesire.Acting desire) {
        super(parent, desire);
      }

      @Override
      IntentionNodeNotTopLevel<?, ?, ?> formDesireNodeAndReplaceIntentionNode() {
        IntentionNodeNotTopLevel.WithCommand.ActingAtTopLevelParent node = new IntentionNodeNotTopLevel.WithCommand.ActingAtTopLevelParent(
            parent, desire);
        parent.replaceDesireByIntentionActing(this, node);
        return node;
      }
    }

    /**
     * Concrete implementation, intention's desire is formed anew with acting command
     */
    static class ReasoningAtTopLevelParent extends
        WithCommand<IntentionNodeAtTopLevel.WithAbstractPlan<?, ?>, OwnDesire.Reasoning, ReasoningCommand> {

      ReasoningAtTopLevelParent(IntentionNodeAtTopLevel.WithAbstractPlan parent,
          OwnDesire.Reasoning desire) {
        super(parent, desire);
      }

      @Override
      IntentionNodeNotTopLevel<?, ?, ?> formDesireNodeAndReplaceIntentionNode() {
        IntentionNodeNotTopLevel.WithCommand.ReasoningAtTopLevelParent node = new IntentionNodeNotTopLevel.WithCommand.ReasoningAtTopLevelParent(
            parent, desire);
        parent.replaceDesireByIntentionReasoning(this, node);
        return node;
      }

    }

    /**
     * Concrete implementation, intention's desire is formed anew with acting command
     */
    static class ActingNotTopLevelParent extends
        WithCommand<IntentionNodeNotTopLevel.WithAbstractPlan<?, ?, ?>, OwnDesire.Acting, ActCommand.Own> {

      ActingNotTopLevelParent(IntentionNodeNotTopLevel.WithAbstractPlan parent,
          OwnDesire.Acting desire) {
        super(parent, desire);
      }

      @Override
      IntentionNodeNotTopLevel<?, ?, ?> formDesireNodeAndReplaceIntentionNode() {
        IntentionNodeNotTopLevel.WithCommand.ActingNotTopLevelParent node = new IntentionNodeNotTopLevel.WithCommand.ActingNotTopLevelParent(
            parent, desire);
        parent.replaceDesireByIntentionActing(this, node);
        return node;
      }
    }

    /**
     * Concrete implementation, intention's desire is formed anew with reasoning command
     */
    static class ReasoningNotTopLevelParent extends
        WithCommand<IntentionNodeNotTopLevel.WithAbstractPlan<?, ?, ?>, OwnDesire.Reasoning, ReasoningCommand> {

      ReasoningNotTopLevelParent(IntentionNodeNotTopLevel.WithAbstractPlan parent,
          OwnDesire.Reasoning desire) {
        super(parent, desire);
      }

      @Override
      IntentionNodeNotTopLevel<?, ?, ?> formDesireNodeAndReplaceIntentionNode() {
        IntentionNodeNotTopLevel.WithCommand.ReasoningNotTopLevelParent node = new IntentionNodeNotTopLevel.WithCommand.ReasoningNotTopLevelParent(
            parent, desire);
        parent.replaceDesireByIntentionReasoning(this, node);
        return node;
      }

    }

  }

}
