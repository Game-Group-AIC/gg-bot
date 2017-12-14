package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.virtual;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.CAN_TRANSIT_FROM_5_POOL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_HYDRALISK_DENS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_MINERALS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_POOLS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_SPIRES;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BOOSTING_AIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BOOSTING_GROUND_MELEE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BOOSTING_GROUND_RANGED;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.BOOST_AIR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.BOOST_GROUND_MELEE;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.BOOST_GROUND_RANGED;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.HOLD_AIR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.HOLD_GROUND;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureContainerHeader;
import aic.gas.sc.gg_bot.bot.model.Decider;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentType;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithAbstractPlan;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithSharedDesire;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UnitOrderManager {

  private static ConfigurationWithSharedDesire shareIntentionToTrainUnit(
      DesireKey boostingTypeDesire,
      DesireKeyID boostingTypeDesireID,
      FeatureContainerHeader featureContainerHeader) {
    return ConfigurationWithSharedDesire.builder()
        .sharedDesireKey(boostingTypeDesire)
        .counts(1)
        .decisionInDesire(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                    CAN_TRANSIT_FROM_5_POOL) != 0
                    && Decider.getDecision(AgentTypes.UNIT_ORDER_MANAGER, boostingTypeDesireID,
                    dataForDecision, featureContainerHeader)
                    || (dataForDecision.getFeatureValueGlobalBeliefs(
                    COUNT_OF_MINERALS) >= 350 && dataForDecision.getFeatureValueGlobalBeliefs(
                    COUNT_OF_GAS) >= 100)
            )
            .globalBeliefTypesByAgentType(Stream.concat(
                featureContainerHeader.getConvertersForFactsForGlobalBeliefsByAgentType().stream(),
                Stream.of(COUNT_OF_MINERALS, COUNT_OF_GAS)).collect(Collectors.toSet()))
            .globalBeliefSetTypesByAgentType(
                featureContainerHeader.getConvertersForFactSetsForGlobalBeliefsByAgentType())
            .globalBeliefTypes(new HashSet<>(Collections.singletonList(CAN_TRANSIT_FROM_5_POOL)))
            .build())
        .decisionInIntention(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) -> !Decider.getDecision(AgentTypes.UNIT_ORDER_MANAGER,
                    boostingTypeDesireID, dataForDecision, featureContainerHeader))
            .globalBeliefTypesByAgentType(
                featureContainerHeader.getConvertersForFactsForGlobalBeliefsByAgentType())
            .globalBeliefSetTypesByAgentType(
                featureContainerHeader.getConvertersForFactSetsForGlobalBeliefsByAgentType())
            .build())
        .build();
  }

  public static final AgentType UNIT_ORDER_MANAGER = AgentType.builder()
      .agentTypeID(AgentTypes.UNIT_ORDER_MANAGER)
      .initializationStrategy(type -> {

        //build zerglings or infrastructure
        type.addConfiguration(BOOST_GROUND_MELEE,
            shareIntentionToTrainUnit(BOOST_GROUND_MELEE, DesireKeys.BOOST_GROUND_MELEE,
                BOOSTING_GROUND_MELEE));

        //build hydras or infrastructure
        type.addConfiguration(BOOST_GROUND_RANGED,
            shareIntentionToTrainUnit(BOOST_GROUND_RANGED, DesireKeys.BOOST_GROUND_RANGED,
                BOOSTING_GROUND_RANGED));

        //build mutalisks or infrastructure
        type.addConfiguration(BOOST_AIR,
            shareIntentionToTrainUnit(BOOST_AIR, DesireKeys.BOOST_AIR, BOOSTING_AIR));

        //abstract plan to build units based on position requests
        ConfigurationWithAbstractPlan groundPosition = ConfigurationWithAbstractPlan.builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        CAN_TRANSIT_FROM_5_POOL) != 0
                        && (dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_POOLS) > 0
                        || dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_HYDRALISK_DENS) > 0)
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_MINERALS) >= 300
                )
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Arrays.asList(COUNT_OF_POOLS, COUNT_OF_HYDRALISK_DENS,
                        COUNT_OF_MINERALS)))
                .globalBeliefTypes(
                    new HashSet<>(Collections.singletonList(CAN_TRANSIT_FROM_5_POOL)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_POOLS) == 0
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_HYDRALISK_DENS) == 0)
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Arrays.asList(COUNT_OF_POOLS, COUNT_OF_HYDRALISK_DENS)))
                .build())
            .desiresForOthers(new HashSet<>(Arrays.asList(BOOST_GROUND_MELEE, BOOST_GROUND_RANGED)))
            .build();
        type.addConfiguration(HOLD_GROUND, groundPosition, false);
        ConfigurationWithSharedDesire buildLings = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(BOOST_GROUND_MELEE)
            .counts(1)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_HYDRALISK_DENS) == 0)
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Collections.singletonList(COUNT_OF_HYDRALISK_DENS)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> false)
                .build())
            .build();
        type.addConfiguration(BOOST_GROUND_MELEE, HOLD_GROUND, buildLings);
        ConfigurationWithSharedDesire buildHydras = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(BOOST_GROUND_RANGED)
            .counts(1)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_HYDRALISK_DENS) > 0)
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Collections.singletonList(COUNT_OF_HYDRALISK_DENS)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_HYDRALISK_DENS) == 0)
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Collections.singletonList(COUNT_OF_HYDRALISK_DENS)))
                .build())
            .build();
        type.addConfiguration(BOOST_GROUND_RANGED, HOLD_GROUND, buildHydras);
//
        //abstract plan to build units based on position requests
        ConfigurationWithAbstractPlan airPosition = ConfigurationWithAbstractPlan.builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_SPIRES) > 0
                )
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Collections.singletonList(COUNT_OF_SPIRES)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_SPIRES) == 0)
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Collections.singletonList(COUNT_OF_SPIRES)))
                .build())
            .desiresForOthers(new HashSet<>(Collections.singletonList(BOOST_AIR)))
            .build();
        type.addConfiguration(HOLD_AIR, airPosition, false);
        ConfigurationWithSharedDesire buildMutas = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(BOOST_AIR)
            .counts(1)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> false)
                .build())
            .build();
        type.addConfiguration(BOOST_AIR, HOLD_AIR, buildMutas);

      })
      .desiresForOthers(
          new HashSet<>(Arrays.asList(BOOST_GROUND_MELEE, BOOST_GROUND_RANGED, BOOST_AIR)))
      .build();
}
