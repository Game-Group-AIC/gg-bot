package aic.gas.sc.gg_bot.mas.model.servicies.beliefs;

import aic.gas.sc.gg_bot.mas.model.agents.Agent;
import aic.gas.sc.gg_bot.mas.model.knowledge.ReadOnlyMemory;
import aic.gas.sc.gg_bot.mas.model.servicies.WorkingRegister;

/**
 * Concrete implementation of MemoryRegister. This class is intended as working register - register
 * keeps up to date information about agents' internal memories and is intended for mediator use
 * only.
 */
public interface IWorkingMemoryRegister extends WorkingRegister<IReadOnlyMemoryRegister>,
    IReadOnlyMemoryRegister {

  @Override
  default IReadOnlyMemoryRegister getAsReadonlyRegister() {
    return this;
  }

  /**
   * Add memory of agent in register
   */
  boolean addAgentsMemory(ReadOnlyMemory readOnlyMemory, Agent owner);

  /**
   * Remove memory of agent in register
   */
  boolean removeAgentsMemory(Agent owner);
}
