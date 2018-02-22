package aic.gas.sc.gg_bot.replay_parser.model.watcher.updating_strategies;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.Beliefs;

public interface IPlayerEnvironmentObservation {

  /**
   * Update beliefs by fields from unit
   */
  APlayer updateBeliefs(APlayer aPlayer, Beliefs beliefs);

}
