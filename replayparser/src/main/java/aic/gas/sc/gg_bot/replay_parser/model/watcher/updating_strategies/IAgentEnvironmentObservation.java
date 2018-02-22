package aic.gas.sc.gg_bot.replay_parser.model.watcher.updating_strategies;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitWithCommands;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.Beliefs;

public interface IAgentEnvironmentObservation {

  /**
   * Update beliefs by fields from unit
   */
  AUnitWithCommands updateBeliefs(AUnitWithCommands aUnit, Beliefs beliefs, int frame);

}
