package aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_type_extension;

import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcherType;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.updating_strategies.PlayerEnvironmentObservation;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.updating_strategies.Reasoning;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

/**
 * Extension of AgentWatcherType to WatcherPlayerType
 */
@Getter
public class WatcherPlayerType extends AgentWatcherType {

  private final PlayerEnvironmentObservation playerEnvironmentObservation;

  @Builder
  private WatcherPlayerType(AgentTypeID agentTypeID, Set<FactKey<?>> factKeys,
      Set<FactKey<?>> factSetsKeys,
      List<PlanWatcherInitializationStrategy> planWatchers, Reasoning reasoning,
      PlayerEnvironmentObservation playerEnvironmentObservation) {
    super(agentTypeID, factKeys, factSetsKeys, planWatchers, reasoning);
    this.playerEnvironmentObservation = playerEnvironmentObservation;
  }

  /**
   * Builder with default values
   */
  public static class WatcherPlayerTypeBuilder extends AgentWatcherTypeBuilder {

    private Set<FactKey<?>> factKeys = new HashSet<>();
    private Set<FactKey<?>> factSetsKeys = new HashSet<>();
  }
}
