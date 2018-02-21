package aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.Beliefs;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.PlanWatcher;
import aic.gas.sc.gg_bot.replay_parser.service.IWatcherMediatorService;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AbstractAgentWatcherUtils {

  public static class AbstractPlanWatcher extends PlanWatcher {

    private Set<Integer> committedAgents = new HashSet<>();
    private final AUnitTypeWrapper typeToWatchFor;

    public AbstractPlanWatcher(
        FeatureContainerInitializationStrategy featureContainerInitializationStrategy,
        DesireKeys desireKey, AUnitTypeWrapper typeToWatchFor) {
      super(featureContainerInitializationStrategy, desireKey);
      this.typeToWatchFor = typeToWatchFor;
    }

    @Override
    protected boolean isAgentCommitted(IWatcherMediatorService mediatorService, Beliefs beliefs) {

      Set<Integer> agentsMorphingToType = mediatorService.getStreamOfWatchers()
          .filter(
              agentWatcher -> agentWatcher.getBeliefs().isFactKeyForValueInMemory(IS_MORPHING_TO))
          .filter(agentWatcher -> typeToWatchFor.equals(agentWatcher.getBeliefs()
              .returnFactValueForGivenKey(IS_MORPHING_TO).orElse(null)))
          .map(AgentWatcher::getID)
          .collect(Collectors.toSet());

      boolean isCommitted = agentsMorphingToType.stream()
          .anyMatch(integer -> !committedAgents.contains(integer));
      committedAgents = agentsMorphingToType;
      return isCommitted;
    }

    @Override
    protected Stream<AgentWatcher<?>> streamOfAgentsToNotifyAboutCommitment() {
      return Stream.empty();
    }
  }

}
