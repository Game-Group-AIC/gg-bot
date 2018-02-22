package aic.gas.sc.gg_bot.mas.service;

import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.planing.command.IObservingCommand;

/**
 * Contract for ObservingCommandManager
 */
public interface IObservingCommandManager<E, T extends IObservingCommand<E>> {

  /**
   * Execute command and returns result of operation
   */
  default boolean executeCommand(T commandToExecute, WorkingMemory memory, E environment) {
    return commandToExecute.observe(memory, environment);
  }

}
