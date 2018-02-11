package aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.BOOST_AIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.BOOST_GROUND_MELEE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.BOOST_GROUND_RANGED;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BOOSTING_AIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BOOSTING_GROUND_MELEE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BOOSTING_GROUND_RANGED;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcherType.PlanWatcherInitializationStrategy;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.Beliefs;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.FeatureContainer;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.PlanWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_type_extension.UnitOrderManagerWatcherType;
import aic.gas.sc.gg_bot.replay_parser.service.IWatcherMediatorService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
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
            () -> new UnitPlanWatcher(() -> new FeatureContainer(BOOSTING_AIR), BOOST_AIR,
                unitTypeWrapper -> unitTypeWrapper != null
                    && !unitTypeWrapper.equals(AUnitTypeWrapper.OVERLORD_TYPE)
                    && unitTypeWrapper.isFlyer()),

            //BOOST_GROUND_MELEE
            () -> new UnitPlanWatcher(() -> new FeatureContainer(BOOSTING_GROUND_MELEE),
                BOOST_GROUND_MELEE, unitTypeWrapper -> unitTypeWrapper != null && unitTypeWrapper
                .equals(AUnitTypeWrapper.ZERGLING_TYPE)),

            //BOOST_GROUND_RANGED
            () -> new UnitPlanWatcher(() -> new FeatureContainer(BOOSTING_GROUND_RANGED),
                BOOST_GROUND_RANGED, unitTypeWrapper -> unitTypeWrapper != null
                && !unitTypeWrapper.isFlyer() && !unitTypeWrapper.isNotActuallyUnit()
                && !unitTypeWrapper.isWorker()
                && !unitTypeWrapper.equals(AUnitTypeWrapper.ZERGLING_TYPE))
        }))
        .build()
    );
  }

  private static class UnitPlanWatcher extends PlanWatcher {

    private Set<Integer> committedAgents = new HashSet<>();
    private final DecideUnitTypeSatisfactionStrategy decideUnitTypeSatisfactionStrategy;

    UnitPlanWatcher(FeatureContainerInitializationStrategy featureContainerInitializationStrategy,
        DesireKeyID desireKey,
        DecideUnitTypeSatisfactionStrategy decideUnitTypeSatisfactionStrategy) {
      super(featureContainerInitializationStrategy, desireKey);
      this.decideUnitTypeSatisfactionStrategy = decideUnitTypeSatisfactionStrategy;
    }

    @Override
    protected boolean isAgentCommitted(IWatcherMediatorService mediatorService, Beliefs beliefs) {

      Set<Integer> agentsMorphingToType = mediatorService.getStreamOfWatchers()
          .filter(
              agentWatcher -> agentWatcher.getBeliefs().isFactKeyForValueInMemory(IS_MORPHING_TO))
          .filter(agentWatcher -> decideUnitTypeSatisfactionStrategy
              .satisfiesType(agentWatcher.getBeliefs()
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

  private interface DecideUnitTypeSatisfactionStrategy {

    boolean satisfiesType(AUnitTypeWrapper unitTypeWrapper);
  }

}
