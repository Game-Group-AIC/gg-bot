package aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_type_extension;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcherType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;

/**
 * Extension of AgentWatcherType to UnitOrderManagerWatcherType
 */
public class UnitOrderManagerWatcherType extends AgentWatcherType {

  @Builder
  private UnitOrderManagerWatcherType(AgentTypes agentType, Set<FactKey<?>> factKeys,
      Set<FactKey<?>> factSetsKeys,
      List<PlanWatcherInitializationStrategy> planWatchers) {
    super(agentType, factKeys, factSetsKeys, planWatchers, null);
  }

  /**
   * Builder with default values
   */
  public static class UnitOrderManagerWatcherTypeBuilder extends AgentWatcherTypeBuilder {

    private Set<FactKey<?>> factKeys = new HashSet<>();
    private Set<FactKey<?>> factSetsKeys = new HashSet<>();
  }
}
