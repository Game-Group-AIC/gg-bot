package aic.gas.sc.gg_bot.mas.service;

import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.planing.command.ICommand;

/**
 * Contract for CommandManager
 */
public interface ICommandManager<T extends ICommand> {

  /**
   * Execute command and returns result of operation
   */
  default boolean executeCommand(T commandToExecute, WorkingMemory memory) {
    return commandToExecute.act(memory);
  }
}
