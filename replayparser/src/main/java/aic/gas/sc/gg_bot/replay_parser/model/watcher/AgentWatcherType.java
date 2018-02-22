package aic.gas.sc.gg_bot.replay_parser.model.watcher;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.updating_strategies.Reasoning;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;

/**
 * AgentWatcher type
 */
@Getter
public class AgentWatcherType {

  private final AgentTypes agentType;
  private final Set<FactKey<?>> factSetsKeys;
  private final List<PlanWatcherInitializationStrategy> planWatchers;
  private final Optional<Reasoning> reasoning;

  protected AgentWatcherType(
      AgentTypes agentType,
      Set<FactKey<?>> factSetsKeys,
      List<PlanWatcherInitializationStrategy> planWatchers, Reasoning reasoning) {
    this.agentType = agentType;
    this.factSetsKeys = factSetsKeys;
    this.planWatchers = planWatchers;
    this.reasoning = Optional.ofNullable(reasoning);
  }

  public Integer getID() {
    return agentType.ordinal();
  }

  public String getName() {
    return agentType.name();
  }

  @Override
  public String toString() {
    return agentType.name();
  }

  /**
   * Create instance of PlanWatcher
   */
  public interface PlanWatcherInitializationStrategy {

    /**
     * Create plan
     */
    PlanWatcher returnPlanWatcher();
  }

  /**
   * Builder with default values
   */
  public static class AgentWatcherTypeBuilder {
    private Set<FactKey<?>> factSetsKeys = new HashSet<>();
  }

}
