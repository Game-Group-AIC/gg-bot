package aic.gas.sc.gg_bot.bot.model.agent.types.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureContainerHeader;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.bot.model.Decider;
import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import aic.gas.sc.gg_bot.bot.service.implementation.BuildLockerService;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithAbstractPlan;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithSharedDesire;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactWithSetOfOptionalValuesForAgentType;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.ReactionOnChangeStrategy;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Contains templates to initialize desires
 */
public class AgentTypeUtils {

  /**
   * Template to create desire initiated by learnt decision.
   * Initialize abstract build plan top - make reservation + check conditions (for building).
   * It is unlocked only when building is built
   */
  public static <T, V> ConfigurationWithAbstractPlan createOwnConfigurationWithAbstractPlanToBuildFromTemplate(
      FactWithSetOfOptionalValuesForAgentType<T> currentCount,
      FactWithSetOfOptionalValuesForAgentType<V> currentCountInConstruction,
      DesireKey desireKey, AUnitTypeWrapper unitTypeWrapper,
      FeatureContainerHeader featureContainerHeader,
      Stream<DesireKey> desireKeysWithAbstractIntentionStream, AgentTypeID agentTypeID) {
    return ConfigurationWithAbstractPlan.builder()
        .decisionInDesire(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) -> !dataForDecision.madeDecisionToAny()
                    && !BuildLockerService.getInstance().isLocked(unitTypeWrapper)
                    && dataForDecision.getFeatureValueGlobalBeliefs(currentCount) == 0
                    && dataForDecision
                    .getFeatureValueGlobalBeliefs(currentCountInConstruction) == 0
                    //learnt decision
                    && (Decider.getDecision(agentTypeID, desireKey.getId(),
                    dataForDecision, featureContainerHeader)))
            .globalBeliefTypesByAgentType(Stream.concat(
                featureContainerHeader.getConvertersForFactsForGlobalBeliefsByAgentType().stream(),
                Stream.of(currentCountInConstruction, currentCount))
                .collect(Collectors.toSet()))
            .globalBeliefSetTypesByAgentType(
                featureContainerHeader.getConvertersForFactSetsForGlobalBeliefsByAgentType())
            .globalBeliefTypes(featureContainerHeader.getConvertersForFactsForGlobalBeliefs())
            .desiresToConsider(Collections.singleton(desireKey))
            .build())
        .decisionInIntention(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) ->
                    BuildLockerService.getInstance().isLocked(unitTypeWrapper)
                        //building exists
                        || dataForDecision.getFeatureValueGlobalBeliefs(currentCount) > 0
                        || dataForDecision
                        .getFeatureValueGlobalBeliefs(currentCountInConstruction) > 0)
            .globalBeliefTypesByAgentType(Stream.of(currentCountInConstruction, currentCount)
                .collect(Collectors.toSet()))
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
      DesireKey desireKey, AUnitTypeWrapper unitTypeWrapper, AgentTypeID agentTypeID,
      FeatureContainerHeader featureContainerHeader) {
    return ConfigurationWithAbstractPlan.builder()
        .decisionInDesire(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) ->
                    !dataForDecision.madeDecisionToAny() &&
                        !BuildLockerService.getInstance().isLocked(unitTypeWrapper)
                        //learnt decision
                        && (Decider.getDecision(agentTypeID, desireKey.getId(), dataForDecision,
                        featureContainerHeader)))
            .globalBeliefTypesByAgentType(
                featureContainerHeader.getConvertersForFactsForGlobalBeliefsByAgentType())
            .globalBeliefSetTypesByAgentType(
                featureContainerHeader.getConvertersForFactSetsForGlobalBeliefsByAgentType())
            .globalBeliefTypes(featureContainerHeader.getConvertersForFactsForGlobalBeliefs())
            .desiresToConsider(Collections.singleton(desireKey))
            .build())
        .decisionInIntention(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) ->
                    //has been just trained
                    BuildLockerService.getInstance().isLocked(unitTypeWrapper))
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
      DesireKey desireToShareKey, AUnitTypeWrapper unitTypeWrapper,
      ReactionOnChangeStrategy findPlace,
      ReactionOnChangeStrategy removePlace) {
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
      DesireKey desireToShareKey, AUnitTypeWrapper unitTypeWrapper) {
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
