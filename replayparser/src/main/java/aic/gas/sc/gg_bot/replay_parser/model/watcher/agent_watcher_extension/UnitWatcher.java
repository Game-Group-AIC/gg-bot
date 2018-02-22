package aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitWithCommands;
import aic.gas.sc.gg_bot.replay_parser.model.IAgentMakingObservations;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_type_extension.UnitWatcherType;
import bwapi.Game;
import bwapi.Unit;

/**
 * UnitWatcher
 */
public class UnitWatcher extends AgentWatcher<UnitWatcherType> implements IAgentMakingObservations {

  protected final Unit unit;
  private final Game game;
  private AUnitWithCommands unitWithCommands;

  public UnitWatcher(UnitWatcherType agentWatcherType, Game game,
      AUnitWithCommands aUnitWithCommands, Unit unit) {
    super(agentWatcherType);
    this.game = game;
    this.unit = unit;
    this.unitWithCommands = aUnitWithCommands;
  }

  @Override
  public void makeObservation() {
    unitWithCommands = UnitWatcherType.getAgentEnvironmentObservation()
        .updateBeliefs(unitWithCommands, beliefs, game.getFrameCount());
  }
}
