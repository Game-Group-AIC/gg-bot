package aic.gas.sc.gg_bot.mas.model.planing.heap.visitors;

import aic.gas.sc.gg_bot.mas.model.ResponseReceiverInterface;
import aic.gas.sc.gg_bot.mas.model.agents.Agent;
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
import lombok.extern.slf4j.Slf4j;

/**
 * CommandExecutor visitor traverse heapOfTrees to get to leafs. When agent is committed to desire
 * in leaf and it contains command to be executed it sends command to agent to handle it
 */
@Slf4j
public class CommandExecutor implements TreeVisitorInterface, ResponseReceiverInterface<Boolean> {

  private final Object lockMonitor = new Object();
  private final HeapOfTrees heapOfTrees;
  private final Agent<?> agent;

  //TODO hack for debug - printing failing commands
  private String lastCommandName = "";

  public CommandExecutor(HeapOfTrees heapOfTrees, Agent<?> agent) {
    this.heapOfTrees = heapOfTrees;
    this.agent = agent;
  }

  @Override
  public void visitTree() {
    branch(heapOfTrees);
  }

  /**
   * Visit subtrees induced by intentions
   */
  private <K extends Node<?> & IntentionNodeInterface & VisitorAcceptor, V extends Node<?> & DesireNodeInterface<K>> void branch(
      Parent<V, K> parent) {
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
    sendActingCommandForExecution(node.getCommand());
  }

  private void sendActingCommandForExecution(ActCommand<?> command) {
    synchronized (lockMonitor) {
      lastCommandName = command.getDesireName();
      if (agent.sendCommandToExecute(command, this)) {
        try {
          lockMonitor.wait();
        } catch (InterruptedException e) {
          log.error(this.getClass().getSimpleName() + ": " + e.getLocalizedMessage());
        }
      }
    }
  }

  @Override
  public void visitNodeWithReasoningCommand(
      IntentionNodeNotTopLevel.WithCommand<?, ?, ReasoningCommand> node) {
    agent.executeCommand(node.getCommand());
  }

  @Override
  public void visit(IntentionNodeAtTopLevel.WithDesireForOthers node) {
    //no action to execute
  }

  @Override
  public void visit(IntentionNodeNotTopLevel.WithDesireForOthers<?> node) {
    //no action to execute
  }

  @Override
  public void visit(IntentionNodeAtTopLevel.WithCommand.OwnReasoning node) {
    agent.executeCommand(node.getCommand());
  }

  @Override
  public void visit(IntentionNodeAtTopLevel.WithCommand.OwnActing node) {
    sendActingCommandForExecution(node.getCommand());
  }

  @Override
  public void visit(IntentionNodeAtTopLevel.WithCommand.FromAnotherAgent node) {
    sendActingCommandForExecution(node.getCommand());
  }

  @Override
  public void receiveResponse(Boolean response) {

    //notify waiting method
    synchronized (lockMonitor) {
      if (!response) {
        log.info(this.agent.getAgentType().getName() + " could not execute acting command "
            + lastCommandName);
      }
      lockMonitor.notify();
    }
  }
}
