package aic.gas.sc.gg_bot.mas.model.planing.heap.visitors;

import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import aic.gas.sc.gg_bot.mas.model.planing.command.ReasoningCommand;
import aic.gas.sc.gg_bot.mas.model.planing.heap.DesireNodeInterface;
import aic.gas.sc.gg_bot.mas.model.planing.heap.HeapOfTrees;
import aic.gas.sc.gg_bot.mas.model.planing.heap.IntentionNodeAtTopLevel;
import aic.gas.sc.gg_bot.mas.model.planing.heap.IntentionNodeInterface;
import aic.gas.sc.gg_bot.mas.model.planing.heap.IntentionNodeNotTopLevel;
import aic.gas.sc.gg_bot.mas.model.planing.heap.Node;
import aic.gas.sc.gg_bot.mas.model.planing.heap.Parent;
import aic.gas.sc.gg_bot.mas.model.planing.heap.TreeVisitorInterface;
import aic.gas.sc.gg_bot.mas.model.planing.heap.VisitorAcceptor;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CommitmentDecider visitor traverse subtrees to decide commitments to intentions removal - for
 * given intention in root of subtree decide based on gate (and supplied arguments to it) if agent
 * should remove commitment to this intention (if so, intention is removed and replaced by desire).
 * If commitment to intention is not removed visitor move further in subtree else do backtrack to go
 * to other branches
 */
public class CommitmentRemovalDecider implements TreeVisitorInterface {

  private final HeapOfTrees heapOfTrees;

  public CommitmentRemovalDecider(HeapOfTrees heapOfTrees) {
    this.heapOfTrees = heapOfTrees;
  }


  @Override
  public void visitTree() {
    branch(heapOfTrees);
  }

  /**
   * Decides commitment removal of node and sends this visitor to subtree
   */
  private <K extends Node<?> & IntentionNodeInterface & VisitorAcceptor, V extends Node<?> & DesireNodeInterface<K>> void branch(
      Parent<V, K> parent) {
    List<K> intentionNodes = parent.getNodesWithIntention();

    //decide removal of commitment to intentions
    Iterator<K> it = intentionNodes.iterator();
    while (it.hasNext()) {
      K node = it.next();
      node.removeCommitment(
          parent.getNodesWithIntention().stream()
              .map(Node::getDesireKey)
              .collect(Collectors.toList()),
          parent.getNodesWithDesire().stream()
              .map(Node::getDesireKey)
              .collect(Collectors.toList()),
          intentionNodes.stream()
              .map(Node::getDesireKey)
              .collect(Collectors.toList()));
      it.remove();
    }

    //visit subtrees induced by remaining intentions
    parent.getNodesWithIntention().forEach(k -> k.accept(this));
  }

  @Override
  public void visit(IntentionNodeAtTopLevel.WithAbstractPlan<?, ?> node) {
    branch(node);
  }

  @Override
  public void visit(IntentionNodeNotTopLevel.WithAbstractPlan<?, ?, ?> node) {
    branch(node);
  }

  @Override
  public void visitNodeWithActingCommand(
      IntentionNodeNotTopLevel.WithCommand<?, ?, ActCommand.Own> node) {
    //do nothing, already decided
  }

  @Override
  public void visitNodeWithReasoningCommand(
      IntentionNodeNotTopLevel.WithCommand<?, ?, ReasoningCommand> node) {
    //do nothing, already decided
  }

  @Override
  public void visit(IntentionNodeAtTopLevel.WithDesireForOthers node) {
    //do nothing, already decided
  }

  @Override
  public void visit(IntentionNodeNotTopLevel.WithDesireForOthers<?> node) {
    //do nothing, already decided
  }

  @Override
  public void visit(IntentionNodeAtTopLevel.WithCommand.OwnReasoning node) {
    //do nothing, already decided
  }

  @Override
  public void visit(IntentionNodeAtTopLevel.WithCommand.OwnActing node) {
    //do nothing, already decided
  }

  @Override
  public void visit(IntentionNodeAtTopLevel.WithCommand.FromAnotherAgent node) {
    //do nothing, already decided
  }
}
