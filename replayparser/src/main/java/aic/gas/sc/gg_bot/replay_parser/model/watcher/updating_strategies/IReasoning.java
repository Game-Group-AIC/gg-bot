package aic.gas.sc.gg_bot.replay_parser.model.watcher.updating_strategies;

import aic.gas.sc.gg_bot.replay_parser.model.watcher.Beliefs;
import aic.gas.sc.gg_bot.replay_parser.service.IWatcherMediatorService;

/**
 * Template for belief updating strategy
 */
public interface IReasoning {

  /**
   * Update beliefs
   */
  void updateBeliefs(Beliefs beliefs, IWatcherMediatorService mediatorService);

}
