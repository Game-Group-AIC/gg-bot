package aic.gas.sc.gg_bot.mas.model.planing;

import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;

/**
 * Interface with template for reaction on change strategy
 */
public interface IReactionOnChangeStrategy {

  /**
   * Strategy to update beliefs
   */
  void updateBeliefs(WorkingMemory memory, DesireParameters desireParameters);

}
