package aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_type_extension;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcherType;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.updating_strategies.BaseEnvironmentObservation;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.updating_strategies.Reasoning;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

/**
 * Extension of AgentWatcherType to BaseWatcherType
 */
@Getter
public class BaseWatcherType extends AgentWatcherType {

  private final BaseEnvironmentObservation baseEnvironmentObservation;

  @Builder
  private BaseWatcherType(AgentTypes agentType,
      Set<FactKey<?>> factSetsKeys,
      List<PlanWatcherInitializationStrategy> planWatchers, Reasoning reasoning,
      BaseEnvironmentObservation baseEnvironmentObservation) {
    super(agentType, factSetsKeys, planWatchers, reasoning);
    this.baseEnvironmentObservation = baseEnvironmentObservation;
  }

  /**
   * Builder with default values
   */
  public static class BaseWatcherTypeBuilder extends AgentWatcherTypeBuilder {
    private Set<FactKey<?>> factSetsKeys = new HashSet<>();
  }
}
