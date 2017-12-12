package aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.EGG;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.LARVA;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.BOOST_AIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.BOOST_GROUND_MELEE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.BOOST_GROUND_RANGED;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BOOSTING_AIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BOOSTING_GROUND_MELEE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BOOSTING_GROUND_RANGED;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcherType.PlanWatcherInitializationStrategy;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.Beliefs;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.FeatureContainer;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.PlanWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_type_extension.UnitOrderManagerWatcherType;
import aic.gas.sc.gg_bot.replay_parser.service.WatcherMediatorService;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Implementation of watcher for planing unit ordering
 */
public class UnitOrderManagerWatcher extends AgentWatcher<UnitOrderManagerWatcherType> {

  public UnitOrderManagerWatcher() {
    super(UnitOrderManagerWatcherType.builder()
        .agentTypeID(AgentTypes.UNIT_ORDER_MANAGER)
        .planWatchers(Arrays.asList(new PlanWatcherInitializationStrategy[]{

                //BOOST_AIR
                () -> new PlanWatcher(() -> new FeatureContainer(BOOSTING_AIR), BOOST_AIR) {
                  private long flayersBeingConstructs = 0;

                  @Override
                  protected boolean isAgentCommitted(WatcherMediatorService mediatorService,
                      Beliefs beliefs) {

                    //morphing to flayer (not overlord)
                    long flayersBeingConstructsCurrentNumber = mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher ->
                            agentWatcher.getAgentWatcherType().getName().equals(LARVA.getName())
                                || agentWatcher.getAgentWatcherType().getName().equals(EGG.getName()))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(IS_MORPHING_TO))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(typeWrapper -> typeWrapper.isFlyer() && !typeWrapper
                            .equals(AUnitTypeWrapper.OVERLORD_TYPE))
                        .count();
                    if (flayersBeingConstructsCurrentNumber != flayersBeingConstructs) {
                      boolean isGreater = flayersBeingConstructsCurrentNumber > flayersBeingConstructs;
                      flayersBeingConstructs = flayersBeingConstructsCurrentNumber;
                      return isGreater;
                    }
                    return false;
                  }

                  @Override
                  protected Stream<AgentWatcher<?>> streamOfAgentsToNotifyAboutCommitment() {
                    return Stream.empty();
                  }
                },

                //BOOST_GROUND_MELEE
                () -> new PlanWatcher(() -> new FeatureContainer(BOOSTING_GROUND_MELEE),
                    BOOST_GROUND_MELEE) {
                  private long lingsBeingConstructs = 0;

                  @Override
                  protected boolean isAgentCommitted(WatcherMediatorService mediatorService,
                      Beliefs beliefs) {

                    //morphing to ling
                    long lingsBeingConstructsCurrentNumber = mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher ->
                            agentWatcher.getAgentWatcherType().getName().equals(LARVA.getName())
                                || agentWatcher.getAgentWatcherType().getName().equals(EGG.getName()))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(IS_MORPHING_TO))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(typeWrapper -> typeWrapper.equals(AUnitTypeWrapper.ZERGLING_TYPE))
                        .count();
                    if (lingsBeingConstructsCurrentNumber != lingsBeingConstructs) {
                      boolean isGreater = lingsBeingConstructsCurrentNumber > lingsBeingConstructs;
                      lingsBeingConstructs = lingsBeingConstructsCurrentNumber;
                      return isGreater;
                    }
                    return false;
                  }

                  @Override
                  protected Stream<AgentWatcher<?>> streamOfAgentsToNotifyAboutCommitment() {
                    return Stream.empty();
                  }
                },

                //BOOST_GROUND_RANGED
                () -> new PlanWatcher(() -> new FeatureContainer(BOOSTING_GROUND_RANGED),
                    BOOST_GROUND_RANGED) {
                  private long rangedBeingConstructs = 0;

                  @Override
                  protected boolean isAgentCommitted(WatcherMediatorService mediatorService,
                      Beliefs beliefs) {

                    //morphing to any other ground attack unit except ling
                    long rangedBeingConstructsCurrentNumber = mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher ->
                            agentWatcher.getAgentWatcherType().getName().equals(LARVA.getName())
                                || agentWatcher.getAgentWatcherType().getName().equals(EGG.getName()))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(IS_MORPHING_TO))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(typeWrapper -> !typeWrapper.isFlyer()
                            && !typeWrapper.equals(AUnitTypeWrapper.ZERGLING_TYPE)
                            && !typeWrapper.isWorker())
                        .count();
                    if (rangedBeingConstructsCurrentNumber != rangedBeingConstructs) {
                      boolean isGreater = rangedBeingConstructsCurrentNumber > rangedBeingConstructs;
                      rangedBeingConstructs = rangedBeingConstructsCurrentNumber;
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
