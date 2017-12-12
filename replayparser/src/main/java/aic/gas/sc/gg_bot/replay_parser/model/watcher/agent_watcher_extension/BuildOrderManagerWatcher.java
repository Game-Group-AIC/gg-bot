package aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.DRONE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.EVOLUTION_CHAMBER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.HYDRALISK_DEN;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.LAIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.SPAWNING_POOL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.SPIRE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.ENABLE_AIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.ENABLE_GROUND_MELEE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.ENABLE_GROUND_RANGED;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.ENABLE_STATIC_ANTI_AIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.UPGRADE_TO_LAIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BEING_CONSTRUCT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_EVOLUTION_CHAMBER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_HYDRALISK_DEN;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_POOL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_SPIRE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.UPGRADING_TO_LAIR;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcherType.PlanWatcherInitializationStrategy;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.Beliefs;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.FeatureContainer;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.PlanWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_type_extension.BuildOrderManagerWatcherType;
import aic.gas.sc.gg_bot.replay_parser.service.WatcherMediatorService;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Implementation of watcher for planing build construction
 */
public class BuildOrderManagerWatcher extends AgentWatcher<BuildOrderManagerWatcherType> {

  public BuildOrderManagerWatcher() {
    super(BuildOrderManagerWatcherType.builder()
        .agentTypeID(AgentTypes.BUILDING_ORDER_MANAGER)
        .planWatchers(Arrays.asList(new PlanWatcherInitializationStrategy[]{

                //ENABLE_GROUND_MELEE
                () -> new PlanWatcher(() -> new FeatureContainer(BUILDING_POOL), ENABLE_GROUND_MELEE) {

                  @Override
                  protected boolean isAgentCommitted(WatcherMediatorService mediatorService,
                      Beliefs beliefs) {

                    //pool being build
                    return mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                            .equals(DRONE.getName()))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(IS_MORPHING_TO))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .anyMatch(
                            typeWrapper -> typeWrapper.equals(AUnitTypeWrapper.SPAWNING_POOL_TYPE))
                        || mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                            .equals(SPAWNING_POOL.getName()))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(IS_BEING_CONSTRUCT))
                        .filter(Optional::isPresent)
                        .anyMatch(Optional::get);
                  }

                  @Override
                  protected Stream<AgentWatcher<?>> streamOfAgentsToNotifyAboutCommitment() {
                    return Stream.empty();
                  }
                },

                //UPGRADE_TO_LAIR
                () -> new PlanWatcher(() -> new FeatureContainer(UPGRADING_TO_LAIR), UPGRADE_TO_LAIR) {

                  @Override
                  protected boolean isAgentCommitted(WatcherMediatorService mediatorService,
                      Beliefs beliefs) {

                    //lair being build
                    return mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                            .equals(LAIR.getName()))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(IS_BEING_CONSTRUCT))
                        .filter(Optional::isPresent)
                        .anyMatch(Optional::get);
                  }

                  @Override
                  protected Stream<AgentWatcher<?>> streamOfAgentsToNotifyAboutCommitment() {
                    return Stream.empty();
                  }
                },

                //ENABLE_AIR
                () -> new PlanWatcher(() -> new FeatureContainer(BUILDING_SPIRE), ENABLE_AIR) {

                  @Override
                  protected boolean isAgentCommitted(WatcherMediatorService mediatorService,
                      Beliefs beliefs) {

                    //spire being build
                    return mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                            .equals(DRONE.getName()))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(IS_MORPHING_TO))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .anyMatch(typeWrapper -> typeWrapper.equals(AUnitTypeWrapper.SPIRE_TYPE))
                        || mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                            .equals(SPIRE.getName()))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(IS_BEING_CONSTRUCT))
                        .filter(Optional::isPresent)
                        .anyMatch(Optional::get);
                  }

                  @Override
                  protected Stream<AgentWatcher<?>> streamOfAgentsToNotifyAboutCommitment() {
                    return Stream.empty();
                  }
                },

                //ENABLE_GROUND_RANGED
                () -> new PlanWatcher(() -> new FeatureContainer(BUILDING_HYDRALISK_DEN),
                    ENABLE_GROUND_RANGED) {

                  @Override
                  protected boolean isAgentCommitted(WatcherMediatorService mediatorService,
                      Beliefs beliefs) {

                    //spire being build
                    return mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                            .equals(DRONE.getName()))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(IS_MORPHING_TO))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .anyMatch(
                            typeWrapper -> typeWrapper.equals(AUnitTypeWrapper.HYDRALISK_DEN_TYPE))
                        || mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                            .equals(HYDRALISK_DEN.getName()))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(IS_BEING_CONSTRUCT))
                        .filter(Optional::isPresent)
                        .anyMatch(Optional::get);
                  }

                  @Override
                  protected Stream<AgentWatcher<?>> streamOfAgentsToNotifyAboutCommitment() {
                    return Stream.empty();
                  }
                },

                //ENABLE_STATIC_ANTI_AIR
                () -> new PlanWatcher(() -> new FeatureContainer(BUILDING_EVOLUTION_CHAMBER),
                    ENABLE_STATIC_ANTI_AIR) {

                  @Override
                  protected boolean isAgentCommitted(WatcherMediatorService mediatorService,
                      Beliefs beliefs) {

                    //spire being build
                    return mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                            .equals(DRONE.getName()))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(IS_MORPHING_TO))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .anyMatch(
                            typeWrapper -> typeWrapper.equals(AUnitTypeWrapper.EVOLUTION_CHAMBER_TYPE))
                        || mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                            .equals(EVOLUTION_CHAMBER.getName()))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(IS_BEING_CONSTRUCT))
                        .filter(Optional::isPresent)
                        .anyMatch(Optional::get);
                  }

                  @Override
                  protected Stream<AgentWatcher<?>> streamOfAgentsToNotifyAboutCommitment() {
                    return Stream.empty();
                  }
                }
            }
            )
        )
        .build()
    );
  }
}
