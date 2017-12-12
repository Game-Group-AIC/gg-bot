package aic.gas.sc.gg_bot.mas.service;

import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.planing.command.ObservingCommand;

/**
 * Contract for ObservingCommandManager
 */
public interface ObservingCommandManager<E, T extends ObservingCommand<E>> {

  /**
   * Execute command and returns result of operation
   */
  default boolean executeCommand(T commandToExecute, WorkingMemory memory, E environment) {
    return commandToExecute.observe(memory, environment);
  }

}
