package aic.gas.sc.gg_bot.mas.model.planing.heap;

import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.planing.AbstractIntention;
import aic.gas.sc.gg_bot.mas.model.planing.InternalDesire;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Desire updater for intermediate nodes with abstract plan
 */
public abstract class DesireUpdater<A extends IntentionNodeNotTopLevel.WithCommand<?, ?, ?>, B extends IntentionNodeNotTopLevel.WithCommand<?, ?, ?>,
    C extends IntentionNodeNotTopLevel.WithDesireForOthers<?>, D extends IntentionNodeNotTopLevel.WithAbstractPlan<?, ?, ?>,
    E extends DesireNodeNotTopLevel.WithCommand<?, ?, ?>, F extends DesireNodeNotTopLevel.WithCommand<?, ?, ?>,
    G extends DesireNodeNotTopLevel.ForOthers<?>, H extends DesireNodeNotTopLevel.WithAbstractPlan<?, ?>,
    K extends Node & IntentionNodeWithChildes<A, B, C, D, E, F, G, H> & Parent<?, ?>> {

  //registers of nodes with own desires
  private final ChildNodeManipulation<A, E> manipulatorWithReasoningCommands = new ChildNodeManipulation<>();
  private final ChildNodeManipulation<B, F> manipulatorWithActingCommands = new ChildNodeManipulation<>();
  private final ChildNodeManipulation<C, G> manipulatorWithDesiresForOthers = new ChildNodeManipulation<>();
  private final ChildNodeManipulation<D, H> manipulatorWithOwnAbstractPlan = new ChildNodeManipulation<>();

  List<DesireNodeNotTopLevel<?, ?>> getNodesWithDesire() {
    List<DesireNodeNotTopLevel<?, ?>> desireNodes = new ArrayList<>(
        manipulatorWithReasoningCommands.desiresNodesByKey.values());
    desireNodes.addAll(manipulatorWithActingCommands.desiresNodesByKey.values());
    desireNodes.addAll(manipulatorWithDesiresForOthers.desiresNodesByKey.values());
    desireNodes.addAll(manipulatorWithOwnAbstractPlan.desiresNodesByKey.values());
    return desireNodes;
  }

  List<IntentionNodeNotTopLevel<?, ?, ?>> getNodesWithIntention() {
    List<IntentionNodeNotTopLevel<?, ?, ?>> intentionNodes = new ArrayList<>(
        manipulatorWithReasoningCommands.intentionNodesByKey.values());
    intentionNodes.addAll(manipulatorWithActingCommands.intentionNodesByKey.values());
    intentionNodes.addAll(manipulatorWithDesiresForOthers.intentionNodesByKey.values());
    intentionNodes.addAll(manipulatorWithOwnAbstractPlan.intentionNodesByKey.values());
    return intentionNodes;
  }

  /**
   * Method update top level nodes
   */
  public void updateDesires(K forNode) {
    updateDesires(
        manipulatorWithDesiresForOthers.removeDesiresForUncommittedNodesAndReturnTheirKeys(),
        manipulatorWithOwnAbstractPlan.removeDesiresForUncommittedNodesAndReturnTheirKeys(),
        manipulatorWithActingCommands.removeDesiresForUncommittedNodesAndReturnTheirKeys(),
        manipulatorWithReasoningCommands.removeDesiresForUncommittedNodesAndReturnTheirKeys(),
        forNode);
    manipulatorWithOwnAbstractPlan.intentionNodesByKey.values()
        .forEach(IntentionNodeWithChildes::updateDesires);
  }

  /**
   * Method to initialize desires
   */
  void initDesires(AbstractIntention<? extends InternalDesire<?>> intention, K forNode) {
    updateDesires(intention.returnPlanAsSetOfDesiresForOthers(),
        intention.returnPlanAsSetOfDesiresWithAbstractIntention(),
        intention.returnPlanAsSetOfDesiresWithIntentionToAct(),
        intention.returnPlanAsSetOfDesiresWithIntentionToReason(), forNode);
  }

  /**
   * Method update nodes
   */
  protected abstract void updateDesires(Set<DesireKey> desiresForOthers,
      Set<DesireKey> desiresWithAbstractIntention,
      Set<DesireKey> desiresWithIntentionToAct, Set<DesireKey> desiresWithIntentionToReason,
      K forNode);

  void addDesireReasoning(E desireNode) {
    manipulatorWithReasoningCommands.addDesireNode(desireNode);
  }

  void addDesireActing(F desireNode) {
    manipulatorWithActingCommands.addDesireNode(desireNode);
  }

  void addDesireWithDesireForOthers(G desireNode) {
    manipulatorWithDesiresForOthers.addDesireNode(desireNode);
  }

  void addDesireWithAbstractPlan(H desireNode) {
    manipulatorWithOwnAbstractPlan.addDesireNode(desireNode);
  }

  void replaceDesireByIntentionReasoning(E desireNode, A intentionNode) {
    manipulatorWithReasoningCommands.replaceDesireByIntention(desireNode, intentionNode);
  }

  void replaceIntentionByDesireReasoning(A intentionNode, E desireNode) {
    manipulatorWithReasoningCommands.replaceIntentionByDesire(intentionNode, desireNode);
  }

  void replaceDesireByIntentionActing(F desireNode, B intentionNode) {
    manipulatorWithActingCommands.replaceDesireByIntention(desireNode, intentionNode);
  }

  void replaceIntentionByDesireActing(B intentionNode, F desireNode) {
    manipulatorWithActingCommands.replaceIntentionByDesire(intentionNode, desireNode);
  }

  void replaceDesireByIntentionWithDesireForOthers(G desireNode, C intentionNode) {
    manipulatorWithDesiresForOthers.replaceDesireByIntention(desireNode, intentionNode);
  }

  void replaceIntentionByDesireWithDesireForOthers(C intentionNode, G desireNode) {
    manipulatorWithDesiresForOthers.replaceIntentionByDesire(intentionNode, desireNode);
  }

  void replaceDesireByIntentionWithAbstractPlan(H desireNode, D intentionNode) {
    manipulatorWithOwnAbstractPlan.replaceDesireByIntention(desireNode, intentionNode);
  }

  void replaceIntentionByDesireWithAbstractPlan(D intentionNode, H desireNode) {
    manipulatorWithOwnAbstractPlan.replaceIntentionByDesire(intentionNode, desireNode);
  }

}
