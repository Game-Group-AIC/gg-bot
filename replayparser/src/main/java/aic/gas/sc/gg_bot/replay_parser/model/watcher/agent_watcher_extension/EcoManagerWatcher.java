package aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.DRONE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.EGG;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.EXTRACTOR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.HATCHERY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.LARVA;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.BUILD_EXTRACTOR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.BUILD_WORKER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.EXPAND;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.INCREASE_CAPACITY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BEING_CONSTRUCTED;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_EXTRACTOR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.EXPANDING;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.INCREASING_CAPACITY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.TRAINING_WORKER;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcherType.PlanWatcherInitializationStrategy;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.Beliefs;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.FeatureContainer;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.PlanWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_type_extension.EcoManagerWatcherType;
import aic.gas.sc.gg_bot.replay_parser.service.IWatcherMediatorService;
import java.util.Arrays;
import java.util.Optional;
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
                () -> new PlanWatcher(() -> new FeatureContainer(BUILDING_EXTRACTOR), BUILD_EXTRACTOR) {

                  @Override
                  protected boolean isAgentCommitted(IWatcherMediatorService mediatorService,
                      Beliefs beliefs) {

                    //extractors being build
                    return mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                            .equals(DRONE.getName()))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(IS_MORPHING_TO))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .anyMatch(typeWrapper -> typeWrapper.equals(AUnitTypeWrapper.EXTRACTOR_TYPE))
                        || mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                            .equals(EXTRACTOR.getName()))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(IS_BEING_CONSTRUCTED))
                        .filter(Optional::isPresent)
                        .anyMatch(Optional::get);
                  }

                  @Override
                  protected Stream<AgentWatcher<?>> streamOfAgentsToNotifyAboutCommitment() {
                    return Stream.empty();
                  }
                },

                //TRAINING_WORKER
                () -> new PlanWatcher(() -> new FeatureContainer(TRAINING_WORKER), BUILD_WORKER) {
                  private long workersBeingConstructs = 0;

                  @Override
                  protected boolean isAgentCommitted(IWatcherMediatorService mediatorService,
                      Beliefs beliefs) {

                    //morphing to worker
                    long workersBeingConstructsCurrentNumber = mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher ->
                            agentWatcher.getAgentWatcherType().getName().equals(LARVA.getName())
                                || agentWatcher.getAgentWatcherType().getName().equals(EGG.getName()))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(IS_MORPHING_TO))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(AUnitTypeWrapper::isWorker)
                        .count();
                    if (workersBeingConstructsCurrentNumber != workersBeingConstructs) {
                      boolean isGreater = workersBeingConstructsCurrentNumber > workersBeingConstructs;
                      workersBeingConstructs = workersBeingConstructsCurrentNumber;
                      return isGreater;
                    }
                    return false;
                  }

                  @Override
                  protected Stream<AgentWatcher<?>> streamOfAgentsToNotifyAboutCommitment() {
                    return Stream.empty();
                  }
                },

                //EXPANDING
                () -> new PlanWatcher(() -> new FeatureContainer(EXPANDING), EXPAND) {

                  @Override
                  protected boolean isAgentCommitted(IWatcherMediatorService mediatorService,
                      Beliefs beliefs) {

                    //hatchery is being build
                    return mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                            .equals(DRONE.getName()))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(IS_MORPHING_TO))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .anyMatch(typeWrapper -> typeWrapper.equals(AUnitTypeWrapper.HATCHERY_TYPE))
                        || mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                            .equals(HATCHERY.getName()))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(IS_BEING_CONSTRUCTED))
                        .filter(Optional::isPresent)
                        .anyMatch(Optional::get);
                  }

                  @Override
                  protected Stream<AgentWatcher<?>> streamOfAgentsToNotifyAboutCommitment() {
                    return Stream.empty();
                  }
                },

                //INCREASE_CAPACITY
                () -> new PlanWatcher(() -> new FeatureContainer(INCREASING_CAPACITY),
                    INCREASE_CAPACITY) {
                  private long overlordsBeingConstructs = 0;

                  @Override
                  protected boolean isAgentCommitted(IWatcherMediatorService mediatorService,
                      Beliefs beliefs) {

                    //overlord being morphed
                    long overlordsBeingConstructsCurrentNumber = mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher ->
                            agentWatcher.getAgentWatcherType().getName().equals(LARVA.getName()) ||
                                agentWatcher.getAgentWatcherType().getName().equals(EGG.getName()))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(IS_MORPHING_TO))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(typeWrapper -> typeWrapper.equals(AUnitTypeWrapper.OVERLORD_TYPE))
                        .count();
                    if (overlordsBeingConstructs != overlordsBeingConstructsCurrentNumber) {
                      boolean isGreater =
                          overlordsBeingConstructsCurrentNumber > overlordsBeingConstructs;
                      overlordsBeingConstructs = overlordsBeingConstructsCurrentNumber;
                      return isGreater;
                    }
                    return false;
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
