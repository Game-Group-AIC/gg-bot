package aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcherType.IPlanWatcherInitializationStrategy;
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
        .agentType(AgentTypes.UNIT_ORDER_MANAGER)
        .planWatchers(Arrays.asList(new IPlanWatcherInitializationStrategy[]{

            //BOOST_AIR
            () -> new UnitPlanWatcher(
                () -> new FeatureContainer(FeatureContainerHeaders.BOOST_AIR),
                DesireKeys.BOOST_AIR,
                unitTypeWrapper -> unitTypeWrapper != null
                    && !unitTypeWrapper.equals(AUnitTypeWrapper.OVERLORD_TYPE)
                    && unitTypeWrapper.isFlyer()),

            //BOOST_GROUND_MELEE
            () -> new UnitPlanWatcher(
                () -> new FeatureContainer(FeatureContainerHeaders.BOOST_GROUND_MELEE),
                DesireKeys.BOOST_GROUND_MELEE,
                unitTypeWrapper -> unitTypeWrapper != null && unitTypeWrapper
                    .equals(AUnitTypeWrapper.ZERGLING_TYPE)),

            //BOOST_GROUND_RANGED
            () -> new UnitPlanWatcher(
                () -> new FeatureContainer(FeatureContainerHeaders.BOOST_GROUND_RANGED),
                DesireKeys.BOOST_GROUND_RANGED,
                unitTypeWrapper -> unitTypeWrapper != null
                    && !unitTypeWrapper.isFlyer() && !unitTypeWrapper.isNotActuallyUnit()
                    && !unitTypeWrapper.isWorker()
                    && !unitTypeWrapper.equals(AUnitTypeWrapper.ZERGLING_TYPE))
        }))
        .build()
    );
  }

  private interface IDecideUnitTypeSatisfactionStrategy {

    boolean satisfiesType(AUnitTypeWrapper unitTypeWrapper);
  }

  private static class UnitPlanWatcher extends PlanWatcher {

    private final IDecideUnitTypeSatisfactionStrategy decideUnitTypeSatisfactionStrategy;
    private Set<Integer> committedAgents = new HashSet<>();

    UnitPlanWatcher(IFeatureContainerInitializationStrategy featureContainerInitializationStrategy,
        DesireKeys desireKey,
        IDecideUnitTypeSatisfactionStrategy decideUnitTypeSatisfactionStrategy) {
      super(featureContainerInitializationStrategy, desireKey);
      this.decideUnitTypeSatisfactionStrategy = decideUnitTypeSatisfactionStrategy;
    }

    @Override
    protected boolean isAgentCommitted(IWatcherMediatorService mediatorService, Beliefs beliefs) {

      Set<Integer> agentsMorphingToType = mediatorService.getStreamOfWatchers()
          .filter(
              agentWatcher -> agentWatcher.getBeliefs().isFactKeyForSetInMemory(IS_MORPHING_TO))
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

}
