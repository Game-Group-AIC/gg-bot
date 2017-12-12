package aic.gas.sc.gg_bot.replay_parser.model.watcher.updating_strategies;

import aic.gas.sc.gg_bot.replay_parser.model.watcher.Beliefs;
import aic.gas.sc.gg_bot.replay_parser.service.WatcherMediatorService;

/**
 * Template for belief updating strategy
 */
public interface Reasoning {

  /**
   * Update beliefs
   */
  void updateBeliefs(Beliefs beliefs, WatcherMediatorService mediatorService);

}
