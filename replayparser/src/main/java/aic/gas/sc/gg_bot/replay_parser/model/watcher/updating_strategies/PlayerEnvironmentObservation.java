package aic.gas.sc.gg_bot.replay_parser.model.watcher.updating_strategies;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.Beliefs;

/**
 * Template interface for PlayerEnvironmentObservation
 */
public interface PlayerEnvironmentObservation {

  /**
   * Update beliefs by fields from unit
   */
  APlayer updateBeliefs(APlayer aPlayer, Beliefs beliefs);

}
