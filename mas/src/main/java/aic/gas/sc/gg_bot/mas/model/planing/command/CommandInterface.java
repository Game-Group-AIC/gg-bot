package aic.gas.sc.gg_bot.mas.model.planing.command;

import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;

/**
 * Contract for command classes
 */
public interface CommandInterface {

  /**
   * Method to be called by CommandForIntention Executor to execute command
   */
  boolean act(WorkingMemory memory);
}
