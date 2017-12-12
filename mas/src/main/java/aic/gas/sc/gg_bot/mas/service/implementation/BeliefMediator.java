package aic.gas.sc.gg_bot.mas.service.implementation;

import aic.gas.sc.gg_bot.mas.model.QueuedItemInterfaceWithResponse;
import aic.gas.sc.gg_bot.mas.model.ResponseReceiverInterface;
import aic.gas.sc.gg_bot.mas.model.agents.Agent;
import aic.gas.sc.gg_bot.mas.model.knowledge.ReadOnlyMemory;
import aic.gas.sc.gg_bot.mas.model.servicies.beliefs.IReadOnlyMemoryRegister;
import aic.gas.sc.gg_bot.mas.model.servicies.beliefs.IWorkingMemoryRegister;
import aic.gas.sc.gg_bot.mas.model.servicies.beliefs.MemoryRegister;
import aic.gas.sc.gg_bot.mas.service.AMediatorTemplate;

/**
 * KnowledgeMediator instance enables agents to share knowledge of agents by keeping each agent's
 * internal knowledge. Class defines method to access queue.
 */
public class BeliefMediator extends
    AMediatorTemplate<IReadOnlyMemoryRegister, IWorkingMemoryRegister> {

  public BeliefMediator() {
    super(new MemoryRegister());
  }

  /**
   * Method to add item to queue with code to register knowledge
   */
  public boolean registerBelief(ReadOnlyMemory readOnlyMemory, Agent owner,
      ResponseReceiverInterface<Boolean> responseReceiver) {
    return addToQueue(new QueuedItemInterfaceWithResponse<Boolean>() {
      @Override
      public Boolean executeCode() {
        return workingRegister.addAgentsMemory(readOnlyMemory, owner);
      }

      @Override
      public ResponseReceiverInterface<Boolean> getReceiverOfResponse() {
        return responseReceiver;
      }
    });
  }

  /**
   * Method to add item to queue with code to remove agent
   */
  public boolean removeAgent(Agent owner, ResponseReceiverInterface<Boolean> responseReceiver) {
    return addToQueue(new QueuedItemInterfaceWithResponse<Boolean>() {
      @Override
      public Boolean executeCode() {
        return workingRegister.removeAgentsMemory(owner);
      }

      @Override
      public ResponseReceiverInterface<Boolean> getReceiverOfResponse() {
        return responseReceiver;
      }
    });
  }

}
