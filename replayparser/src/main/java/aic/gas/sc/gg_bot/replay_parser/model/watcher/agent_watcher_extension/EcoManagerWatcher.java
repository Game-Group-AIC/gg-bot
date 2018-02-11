package aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.BUILD_EXTRACTOR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.BUILD_WORKER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.EXPAND;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.INCREASE_CAPACITY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_OUR_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.REPRESENTS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_EXTRACTOR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.EXPANDING;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.INCREASING_CAPACITY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.TRAINING_WORKER;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
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
        .agentTypeID(AgentTypes.ECO_MANAGER)
        .planWatchers(Arrays.asList(new PlanWatcherInitializationStrategy[]{

            //BUILD_EXTRACTOR
            () -> new AbstractAgentWatcherUtils.AbstractPlanWatcher(
                () -> new FeatureContainer(BUILDING_EXTRACTOR), BUILD_EXTRACTOR,
                AUnitTypeWrapper.EXTRACTOR_TYPE),

            //TRAINING_WORKER
            () -> new AbstractAgentWatcherUtils.AbstractPlanWatcher(
                () -> new FeatureContainer(TRAINING_WORKER), BUILD_WORKER,
                AUnitTypeWrapper.DRONE_TYPE),

            //TODO does not take new hatcheries in our base in to account
            //EXPANDING
            () -> new AbstractAgentWatcherUtils.AbstractPlanWatcher(
                () -> new FeatureContainer(EXPANDING), EXPAND, AUnitTypeWrapper.HATCHERY_TYPE),

            //INCREASE_CAPACITY
            () -> new AbstractAgentWatcherUtils.AbstractPlanWatcher(
                () -> new FeatureContainer(INCREASING_CAPACITY), INCREASE_CAPACITY,
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
        DesireKeyID desireKey) {
      super(featureContainerInitializationStrategy, desireKey);
    }

    @Override
    protected boolean isAgentCommitted(IWatcherMediatorService mediatorService, Beliefs beliefs) {

      //exclude new hatcheries in old bases
      Set<ABaseLocationWrapper> bases = mediatorService.getStreamOfWatchers()
          .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
              .equals(BASE_LOCATION.getName()))
          .filter(agentWatcher -> agentWatcher.getBeliefs().returnFactValueForGivenKey(IS_OUR_BASE)
              .orElse(false))
          .map(agentWatcher -> agentWatcher.getBeliefs()
              .returnFactValueForGivenKey(IS_BASE_LOCATION))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toSet());

      Set<Integer> agentsMorphingToType = mediatorService.getStreamOfWatchers()
          .filter(
              agentWatcher -> agentWatcher.getBeliefs().isFactKeyForValueInMemory(IS_MORPHING_TO))
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
