package aic.gas.sc.gg_bot.mas.service;

import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.planing.command.CommandInterface;

/**
 * Contract for CommandManager
 */
public interface CommandManager<T extends CommandInterface> {

  /**
   * Execute command and returns result of operation
   */
  default boolean executeCommand(T commandToExecute, WorkingMemory memory) {
    return commandToExecute.act(memory);
  }
}
