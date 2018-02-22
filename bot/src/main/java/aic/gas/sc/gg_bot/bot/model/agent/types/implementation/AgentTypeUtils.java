package aic.gas.sc.gg_bot.bot.model.agent.types.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureContainerHeader;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.bot.model.Decider;
import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import aic.gas.sc.gg_bot.bot.service.implementation.BuildLockerService;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithAbstractPlan;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithSharedDesire;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactValueSetsForAgentType;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.IReactionOnChangeStrategy;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * Contains templates to initialize desires
 */
@Slf4j
public class AgentTypeUtils {

  /**
   * Template to create desire initiated by learnt decision.
   * Initialize abstract build plan top - make reservation + check conditions (for building).
   * It is unlocked only when building is built
   */
  public static <T> ConfigurationWithAbstractPlan createOwnConfigurationWithAbstractPlanToBuildFromTemplate(
      FactValueSetsForAgentType<T> currentCount,
      DesireKey desireKey,
      AUnitTypeWrapper unitTypeWrapper,
      FeatureContainerHeader featureContainerHeader,
      Stream<DesireKey> desireKeysWithAbstractIntentionStream,
      AgentTypes agentType) {
    return ConfigurationWithAbstractPlan.builder()
        .decisionInDesire(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) ->
                    !BotFacade.RESOURCE_MANAGER
                        .hasMadeReservationOn(unitTypeWrapper, memory.getAgentId())
                        && !dataForDecision.madeDecisionToAny()
                        && !BuildLockerService.getInstance().isLocked(unitTypeWrapper)
                        && dataForDecision.getFeatureValueGlobalBeliefSets(currentCount) == 0
                        //learnt decision
                        && Decider.getDecision(agentType, DesireKeys.values[desireKey.getId()],
                        dataForDecision, featureContainerHeader, memory.getCurrentClock(),
                        memory.getAgentId()))
            .globalBeliefSetTypesByAgentType(Stream.concat(
                featureContainerHeader.getConvertersForFactSetsForGlobalBeliefsByAgentType()
                    .stream(),
                Stream.of(currentCount))
                .collect(Collectors.toSet()))
            .globalBeliefSetTypesByAgentType(
                featureContainerHeader.getConvertersForFactSetsForGlobalBeliefsByAgentType())
            .globalBeliefSetTypes(featureContainerHeader.getConvertersForFactSetsForGlobalBeliefs())
            .desiresToConsider(Collections.singleton(desireKey))
            .build())
        .decisionInIntention(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) ->
                    (!Decider.getDecision(agentType, DesireKeys.values[desireKey.getId()],
                        dataForDecision, featureContainerHeader, memory.getCurrentClock(),
                        memory.getAgentId())
                        && !BotFacade.RESOURCE_MANAGER
                        .canSpendResourcesOn(unitTypeWrapper, memory.getAgentId()))
                        || BuildLockerService.getInstance().isLocked(unitTypeWrapper)
                        //building exists
                        || dataForDecision.getFeatureValueGlobalBeliefSets(currentCount) > 0)
            .globalBeliefSetTypesByAgentType(Stream.concat(
                featureContainerHeader.getConvertersForFactSetsForGlobalBeliefsByAgentType()
                    .stream(),
                Stream.of(currentCount))
                .collect(Collectors.toSet()))
            .globalBeliefSetTypesByAgentType(
                featureContainerHeader.getConvertersForFactSetsForGlobalBeliefsByAgentType())
            .globalBeliefSetTypes(featureContainerHeader.getConvertersForFactSetsForGlobalBeliefs())
            .build())
        .reactionOnChangeStrategy((memory, desireParameters) -> BotFacade.RESOURCE_MANAGER
            .makeReservation(unitTypeWrapper, memory.getAgentId()))
        .reactionOnChangeStrategyInIntention(
            (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER
                .removeReservation(unitTypeWrapper, memory.getAgentId()))
        .desiresForOthers(Collections.singleton(desireKey))
        .desiresWithAbstractIntention(
            desireKeysWithAbstractIntentionStream.collect(Collectors.toSet()))
        .build();
  }

  /**
   * Template to create desire initiated by learnt decision.
   * Initialize abstract training plan top
   */
  public static ConfigurationWithAbstractPlan createOwnConfigurationWithAbstractPlanToTrainFromTemplate(
      DesireKey desireKey,
      AUnitTypeWrapper unitTypeWrapper,
      AgentTypes agentType,
      FeatureContainerHeader featureContainerHeader) {
    return ConfigurationWithAbstractPlan.builder()
        .decisionInDesire(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) ->
                    !BotFacade.RESOURCE_MANAGER
                        .hasMadeReservationOn(unitTypeWrapper, memory.getAgentId())
                        && !dataForDecision.madeDecisionToAny() &&
                        !BuildLockerService.getInstance().isLocked(unitTypeWrapper)
                        //learnt decision
                        && Decider.getDecision(agentType, DesireKeys.values[desireKey.getId()],
                        dataForDecision,
                        featureContainerHeader, memory.getCurrentClock(), memory.getAgentId()))
            .globalBeliefSetTypesByAgentType(
                featureContainerHeader.getConvertersForFactSetsForGlobalBeliefsByAgentType())
            .globalBeliefSetTypesByAgentType(
                featureContainerHeader.getConvertersForFactSetsForGlobalBeliefsByAgentType())
            .globalBeliefSetTypes(featureContainerHeader.getConvertersForFactSetsForGlobalBeliefs())
            .desiresToConsider(Collections.singleton(desireKey))
            .build())
        .decisionInIntention(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) ->
                    (!Decider.getDecision(agentType, DesireKeys.values[desireKey.getId()],
                        dataForDecision,
                        featureContainerHeader, memory.getCurrentClock(), memory.getAgentId())
                        && !BotFacade.RESOURCE_MANAGER
                        .canSpendResourcesOn(unitTypeWrapper, memory.getAgentId()))
                        //has been just trained
                        || BuildLockerService.getInstance().isLocked(unitTypeWrapper))
            .globalBeliefSetTypesByAgentType(
                featureContainerHeader.getConvertersForFactSetsForGlobalBeliefsByAgentType())
            .globalBeliefSetTypesByAgentType(
                featureContainerHeader.getConvertersForFactSetsForGlobalBeliefsByAgentType())
            .globalBeliefSetTypes(featureContainerHeader.getConvertersForFactSetsForGlobalBeliefs())
            .build())
        .reactionOnChangeStrategy((memory, desireParameters) -> BotFacade.RESOURCE_MANAGER
            .makeReservation(unitTypeWrapper, memory.getAgentId()))
        .reactionOnChangeStrategyInIntention(
            (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER
                .removeReservation(unitTypeWrapper, memory.getAgentId()))
        .desiresForOthers(Collections.singleton(desireKey))
        .build();
  }

  /**
   * Template to create shared desire to build building
   */
  public static ConfigurationWithSharedDesire createConfigurationWithSharedDesireToBuildFromTemplate(
      DesireKey desireToShareKey,
      AUnitTypeWrapper unitTypeWrapper,
      IReactionOnChangeStrategy findPlace,
      IReactionOnChangeStrategy removePlace) {
    return ConfigurationWithSharedDesire.builder()
        .sharedDesireKey(desireToShareKey)
        .counts(1)
        //TODO desires probably should not share same building place
        .reactionOnChangeStrategy(findPlace)
        .reactionOnChangeStrategyInIntention(removePlace)
        .decisionInDesire(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) ->
                    //resources are available
                    BotFacade.RESOURCE_MANAGER
                        .canSpendResourcesOn(unitTypeWrapper, memory.getAgentId()))
            .build())
        .decisionInIntention(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) ->
                    //TODO check validity of the place
                    //we do not have enough resources
                    !BotFacade.RESOURCE_MANAGER
                        .canSpendResourcesOn(unitTypeWrapper, memory.getAgentId()))
            .build())
        .build();
  }

  /**
   * Template to create shared desire to train unit
   */
  public static ConfigurationWithSharedDesire createConfigurationWithSharedDesireToTrainFromTemplate(
      DesireKey desireToShareKey,
      AUnitTypeWrapper unitTypeWrapper) {
    return ConfigurationWithSharedDesire.builder()
        .sharedDesireKey(desireToShareKey)
        .counts(1)
        .decisionInDesire(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) ->
                    //resources are available
                    BotFacade.RESOURCE_MANAGER
                        .canSpendResourcesOn(unitTypeWrapper, memory.getAgentId()))
            .build())
        .decisionInIntention(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) ->
                    //we do not have enough resources
                    !BotFacade.RESOURCE_MANAGER
                        .canSpendResourcesOn(unitTypeWrapper, memory.getAgentId()))
            .build())
        .build();
  }
}
