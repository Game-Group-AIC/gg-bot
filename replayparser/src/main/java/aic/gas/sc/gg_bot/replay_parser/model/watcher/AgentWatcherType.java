package aic.gas.sc.gg_bot.replay_parser.model.watcher;

import aic.gas.mas.model.metadata.AgentTypeID;
import aic.gas.mas.model.metadata.FactKey;
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
public class AgentWatcherType extends AgentTypeID {

  private final Set<FactKey<?>> factKeys;
  private final Set<FactKey<?>> factSetsKeys;
  private final List<PlanWatcherInitializationStrategy> planWatchers;
  private final Optional<Reasoning> reasoning;

  protected AgentWatcherType(AgentTypeID agentTypeID, Set<FactKey<?>> factKeys,
      Set<FactKey<?>> factSetsKeys,
      List<PlanWatcherInitializationStrategy> planWatchers, Reasoning reasoning) {
    super(agentTypeID.getName(), agentTypeID.getID());
    this.factKeys = factKeys;
    this.factSetsKeys = factSetsKeys;
    this.planWatchers = planWatchers;
    this.reasoning = Optional.ofNullable(reasoning);
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

    private Set<FactKey<?>> factKeys = new HashSet<>();
    private Set<FactKey<?>> factSetsKeys = new HashSet<>();
  }

}
