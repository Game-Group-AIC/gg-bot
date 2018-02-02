package aic.gas.sc.gg_bot.replay_parser.service;

import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension.UnitWatcher;
import bwapi.Game;
import bwapi.Unit;
import java.util.Optional;

/**
 * Contract for factory to initiate agents for unit
 */
public interface IAgentUnitHandler {

  /**
   * Create agent watcher for unit if possible
   */
  Optional<UnitWatcher> createAgentForUnit(Unit unit, Game game);

}
