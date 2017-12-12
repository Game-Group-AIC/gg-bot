package aic.gas.sc.gg_bot.replay_parser.model.watcher.updating_strategies;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.Beliefs;

/**
 * Template interface for BaseEnvironmentObservation
 */
public interface BaseEnvironmentObservation {

  /**
   * Update beliefs by fields from baseLocation
   */
  void updateBeliefs(ABaseLocationWrapper baseLocation, Beliefs beliefs);

}
