package aic.gas.sc.gg_bot.replay_parser.model.watcher.updating_strategies;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitWithCommands;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.Beliefs;

/**
 * Template interface for AgentEnvironmentObservation
 */
public interface AgentEnvironmentObservation {

  /**
   * Update beliefs by fields from unit
   */
  AUnitWithCommands updateBeliefs(AUnitWithCommands aUnit, Beliefs beliefs, int frame);

}
