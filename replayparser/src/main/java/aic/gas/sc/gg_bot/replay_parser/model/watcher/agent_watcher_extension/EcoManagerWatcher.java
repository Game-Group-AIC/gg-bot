package aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_OUR_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.REPRESENTS_UNIT;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcherType.PlanWatcherInitializationStrategy;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.Beliefs;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.FeatureContainer;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.PlanWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_type_extension.EcoManagerWatcherType;
import aic.gas.sc.gg_bot.replay_parser.service.IWatcherMediatorService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of watcher for eco. side of the game
 */
public class EcoManagerWatcher extends AgentWatcher<EcoManagerWatcherType> {

  public EcoManagerWatcher() {
    super(EcoManagerWatcherType.builder()
        .agentType(AgentTypes.ECO_MANAGER)
        .planWatchers(Arrays.asList(new PlanWatcherInitializationStrategy[]{

            //BUILD_EXTRACTOR
            () -> new AbstractAgentWatcherUtils.AbstractPlanWatcher(
                () -> new FeatureContainer(FeatureContainerHeaders.BUILD_EXTRACTOR),
                DesireKeys.BUILD_EXTRACTOR,
                AUnitTypeWrapper.EXTRACTOR_TYPE
            ),

            //BUILD_WORKER
            () -> new AbstractAgentWatcherUtils.AbstractPlanWatcher(
                () -> new FeatureContainer(FeatureContainerHeaders.BUILD_WORKER),
                DesireKeys.BUILD_WORKER,
                AUnitTypeWrapper.DRONE_TYPE),

            //TODO does not take new hatcheries in our base in to account
            //EXPAND
            () -> new AbstractAgentWatcherUtils.AbstractPlanWatcher(
                () -> new FeatureContainer(FeatureContainerHeaders.EXPAND),
                DesireKeys.EXPAND,
                AUnitTypeWrapper.HATCHERY_TYPE),

            //INCREASE_CAPACITY
            () -> new AbstractAgentWatcherUtils.AbstractPlanWatcher(
                () -> new FeatureContainer(FeatureContainerHeaders.INCREASE_CAPACITY),
                DesireKeys.INCREASE_CAPACITY,
                AUnitTypeWrapper.OVERLORD_TYPE)
        }))
        .build()
    );
  }

  //TODO check
  public static class ExpansionPlanWatcher extends PlanWatcher {

    private Set<Integer> committedAgents = new HashSet<>();

    public ExpansionPlanWatcher(
        FeatureContainerInitializationStrategy featureContainerInitializationStrategy,
        DesireKeys desireKey) {
      super(featureContainerInitializationStrategy, desireKey);
    }

    @Override
    protected boolean isAgentCommitted(IWatcherMediatorService mediatorService, Beliefs beliefs) {

      //exclude new hatcheries in old bases
      Set<ABaseLocationWrapper> bases = mediatorService.getStreamOfWatchers()
          .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
              .equals(BASE_LOCATION.name()))
          .filter(agentWatcher -> agentWatcher.getBeliefs().returnFactValueForGivenKey(IS_OUR_BASE)
              .orElse(false))
          .map(agentWatcher -> agentWatcher.getBeliefs()
              .returnFactValueForGivenKey(IS_BASE_LOCATION))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toSet());

      Set<Integer> agentsMorphingToType = mediatorService.getStreamOfWatchers()
          .filter(
              agentWatcher -> agentWatcher.getBeliefs().isFactKeyForSetInMemory(IS_MORPHING_TO))
          .filter(agentWatcher -> AUnitTypeWrapper.HATCHERY_TYPE.equals(agentWatcher.getBeliefs()
              .returnFactValueForGivenKey(IS_MORPHING_TO).orElse(null)))
          .filter(agentWatcher -> !bases.contains(
              agentWatcher.getBeliefs().returnFactValueForGivenKey(REPRESENTS_UNIT)
                  .map(AUnit::getNearestBaseLocation)
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .orElse(null)))
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
