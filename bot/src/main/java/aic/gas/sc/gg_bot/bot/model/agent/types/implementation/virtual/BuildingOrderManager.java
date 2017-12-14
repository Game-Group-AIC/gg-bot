package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.virtual;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.CAN_TRANSIT_FROM_5_POOL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_EVOLUTION_CHAMBERS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_EVOLUTION_CHAMBERS_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_EXTRACTORS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_HYDRALISK_DENS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_HYDRALISK_DENS_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_LAIRS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_LAIRS_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_MINERALS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_POOLS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_POOLS_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_SPIRES;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_SPIRES_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_MOVE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_EVOLUTION_CHAMBER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_HYDRALISK_DEN;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_POOL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_SPIRE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.UPGRADING_TO_LAIR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.BOOST_AIR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.BOOST_GROUND_MELEE;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.BOOST_GROUND_RANGED;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.BUILD_EXTRACTOR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.ENABLE_AIR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.ENABLE_GROUND_MELEE;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.ENABLE_GROUND_RANGED;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.ENABLE_STATIC_ANTI_AIR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_EVOLUTION_CHAMBER;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_HYDRALISK_DEN;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_POOL;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_SPIRE;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_SPORE_COLONY;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_SUNKEN_COLONY;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.UPGRADE_TO_LAIR;
import static aic.gas.sc.gg_bot.bot.model.agent.AgentPlayer.FIND_MAIN_BASE;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.bot.model.Decider;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentType;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithAbstractPlan;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithSharedDesire;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuildingOrderManager {

  public static final AgentType BUILDING_ORDER_MANAGER = AgentType.builder()
      .agentTypeID(AgentTypes.BUILDING_ORDER_MANAGER)
      .usingTypesForFacts(new HashSet<>(Collections.singletonList(BASE_TO_MOVE)))
      .initializationStrategy(type -> {

        ConfigurationWithSharedDesire buildPool = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(MORPH_TO_POOL)
            .counts(1)
            .reactionOnChangeStrategy(FIND_MAIN_BASE)
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE))
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        CAN_TRANSIT_FROM_5_POOL) != 0
                        && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_POOLS) == 0
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_POOLS_IN_CONSTRUCTION) == 0
                        && Decider.getDecision(AgentTypes.BUILDING_ORDER_MANAGER,
                        DesireKeys.ENABLE_GROUND_MELEE, dataForDecision, BUILDING_POOL)
//                                                || dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_MINERALS) > 200)
                )
                .globalBeliefTypesByAgentType(Stream.concat(
                    BUILDING_POOL.getConvertersForFactsForGlobalBeliefsByAgentType().stream(),
                    Stream.of(COUNT_OF_POOLS_IN_CONSTRUCTION, COUNT_OF_POOLS,
                        COUNT_OF_MINERALS)).collect(Collectors.toSet()))
                .globalBeliefSetTypesByAgentType(
                    BUILDING_POOL.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .globalBeliefTypes(
                    Stream.concat(BUILDING_POOL.getConvertersForFactsForGlobalBeliefs().stream(),
                        Stream.of(CAN_TRANSIT_FROM_5_POOL)).collect(Collectors.toSet()))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_POOLS) > 0
                        || dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_POOLS_IN_CONSTRUCTION) > 0)
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Arrays.asList(COUNT_OF_POOLS_IN_CONSTRUCTION, COUNT_OF_POOLS)))
                .build())
            .build();
        type.addConfiguration(ENABLE_GROUND_MELEE, buildPool);

        //common plans
        //build pool if not present
        ConfigurationWithSharedDesire buildPoolCommon = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(MORPH_TO_POOL)
            .reactionOnChangeStrategy(FIND_MAIN_BASE)
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE))
            .counts(1)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_POOLS) == 0
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_POOLS_IN_CONSTRUCTION) == 0)
                .globalBeliefTypesByAgentType(
                    Stream.of(COUNT_OF_POOLS_IN_CONSTRUCTION, COUNT_OF_POOLS).collect(
                        Collectors.toSet()))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_POOLS) > 0
                        || dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_POOLS_IN_CONSTRUCTION) > 0)
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Arrays.asList(COUNT_OF_POOLS_IN_CONSTRUCTION, COUNT_OF_POOLS)))
                .build())
            .build();
        //tell system to build extractor if it is missing
        ConfigurationWithSharedDesire buildExtractorCommon = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(BUILD_EXTRACTOR)
            .counts(1)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !dataForDecision.madeDecisionToAny() &&
                        dataForDecision.getFeatureValueGlobalBeliefSets(COUNT_OF_EXTRACTORS) == 0)
                .globalBeliefSetTypesByAgentType(Collections.singleton(COUNT_OF_EXTRACTORS))
                .desiresToConsider(new HashSet<>(Collections.singleton(BUILD_EXTRACTOR)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    dataForDecision.getFeatureValueGlobalBeliefSets(COUNT_OF_EXTRACTORS) > 0)
                .globalBeliefSetTypesByAgentType(Collections.singleton(COUNT_OF_EXTRACTORS))
                .build())
            .build();
        //common abstract plan to upgrade to spire
        ConfigurationWithAbstractPlan upgradeToLairAbstractCommon = ConfigurationWithAbstractPlan
            .builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> !dataForDecision.madeDecisionToAny()
                    && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_LAIRS) == 0
                    && dataForDecision.getFeatureValueGlobalBeliefs(
                    COUNT_OF_LAIRS_IN_CONSTRUCTION) == 0)
                .globalBeliefTypesByAgentType(
                    Stream.of(COUNT_OF_LAIRS, COUNT_OF_LAIRS_IN_CONSTRUCTION).collect(
                        Collectors.toSet()))
                .desiresToConsider(new HashSet<>(Collections.singleton(UPGRADE_TO_LAIR)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_LAIRS) > 0
                        || dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_LAIRS_IN_CONSTRUCTION) > 0)
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Arrays.asList(COUNT_OF_LAIRS_IN_CONSTRUCTION, COUNT_OF_LAIRS)))
                .build())
            .desiresForOthers(new HashSet<>(Arrays.asList(ENABLE_GROUND_MELEE, UPGRADE_TO_LAIR,
                BUILD_EXTRACTOR)))
            .build();
        type.addConfiguration(UPGRADE_TO_LAIR, upgradeToLairAbstractCommon, false);
        //tell system to build pool if needed
        type.addConfiguration(ENABLE_GROUND_MELEE, UPGRADE_TO_LAIR, buildPoolCommon);
        //tell system to build extractor if it is missing
        type.addConfiguration(BUILD_EXTRACTOR, UPGRADE_TO_LAIR, buildExtractorCommon);
        //tell system to upgrade hatchery to lair
        ConfigurationWithSharedDesire upgradeToLair = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(UPGRADE_TO_LAIR)
            .counts(1)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> false)
                .build())
            .build();
        type.addConfiguration(UPGRADE_TO_LAIR, UPGRADE_TO_LAIR, upgradeToLair);

        //hydralisk den. as abstract plan, meet dependencies - pool and at least one extractor
        ConfigurationWithAbstractPlan buildHydraliskDen = ConfigurationWithAbstractPlan.builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        CAN_TRANSIT_FROM_5_POOL) != 0
                        && !dataForDecision.madeDecisionToAny()
                        && Decider.getDecision(AgentTypes.BUILDING_ORDER_MANAGER,
                        DesireKeys.ENABLE_GROUND_RANGED, dataForDecision,
                        BUILDING_HYDRALISK_DEN)
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_HYDRALISK_DENS) == 0
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_HYDRALISK_DENS_IN_CONSTRUCTION) == 0
                )
                .desiresToConsider(new HashSet<>(Collections.singleton(ENABLE_GROUND_RANGED)))
                .globalBeliefTypesByAgentType(Stream.concat(
                    BUILDING_HYDRALISK_DEN.getConvertersForFactsForGlobalBeliefsByAgentType()
                        .stream(),
                    Stream.of(COUNT_OF_HYDRALISK_DENS,
                        COUNT_OF_HYDRALISK_DENS_IN_CONSTRUCTION)).collect(Collectors.toSet()))
                .globalBeliefSetTypesByAgentType(
                    BUILDING_HYDRALISK_DEN.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .globalBeliefTypes(Stream.concat(
                    BUILDING_HYDRALISK_DEN.getConvertersForFactsForGlobalBeliefs().stream(),
                    Stream.of(CAN_TRANSIT_FROM_5_POOL)).collect(Collectors.toSet()))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_HYDRALISK_DENS) > 0
                        || dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_HYDRALISK_DENS_IN_CONSTRUCTION) > 0)
                .globalBeliefTypesByAgentType(new HashSet<>(Arrays.asList(COUNT_OF_HYDRALISK_DENS,
                    COUNT_OF_HYDRALISK_DENS_IN_CONSTRUCTION)))
                .build())
            .desiresForOthers(new HashSet<>(Arrays.asList(ENABLE_GROUND_RANGED, BUILD_EXTRACTOR,
                ENABLE_GROUND_MELEE)))
            .build();
        type.addConfiguration(ENABLE_GROUND_RANGED, buildHydraliskDen, true);
        ConfigurationWithSharedDesire bdDen = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(MORPH_TO_HYDRALISK_DEN)
            .reactionOnChangeStrategy(FIND_MAIN_BASE)
            .reactionOnChangeStrategyInIntention((memory, desireParameters) -> {
              memory.eraseFactValueForGivenKey(BASE_TO_MOVE);
            })
            .counts(1)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> false)
                .build())
            .build();
        type.addConfiguration(ENABLE_GROUND_RANGED, ENABLE_GROUND_RANGED, bdDen);
        type.addConfiguration(BUILD_EXTRACTOR, ENABLE_GROUND_RANGED, buildExtractorCommon);
        type.addConfiguration(ENABLE_GROUND_MELEE, ENABLE_GROUND_RANGED, buildPoolCommon);
        //hydralisk den

        //upgrade to lair. as abstract plan, meet dependencies - pool and at least one extractor
        ConfigurationWithAbstractPlan upgradeToLairAbstract = ConfigurationWithAbstractPlan
            .builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        CAN_TRANSIT_FROM_5_POOL) != 0
                        && !dataForDecision.madeDecisionToAny()
                        && Decider.getDecision(AgentTypes.BUILDING_ORDER_MANAGER,
                        DesireKeys.UPGRADE_TO_LAIR, dataForDecision, UPGRADING_TO_LAIR)
                        && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_LAIRS) == 0
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_LAIRS_IN_CONSTRUCTION) == 0)
                .globalBeliefTypesByAgentType(Stream.concat(
                    UPGRADING_TO_LAIR.getConvertersForFactsForGlobalBeliefsByAgentType().stream(),
                    Stream.of(COUNT_OF_LAIRS, COUNT_OF_LAIRS_IN_CONSTRUCTION)).collect(
                    Collectors.toSet()))
                .globalBeliefSetTypesByAgentType(
                    UPGRADING_TO_LAIR.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .globalBeliefTypes(Stream.concat(
                    UPGRADING_TO_LAIR.getConvertersForFactsForGlobalBeliefs().stream(),
                    Stream.of(CAN_TRANSIT_FROM_5_POOL)).collect(Collectors.toSet()))
                .desiresToConsider(new HashSet<>(Collections.singleton(UPGRADE_TO_LAIR)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_LAIRS) > 0
                        || dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_LAIRS_IN_CONSTRUCTION) > 0)
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Arrays.asList(COUNT_OF_LAIRS, COUNT_OF_LAIRS_IN_CONSTRUCTION)))
                .build())
            .desiresForOthers(new HashSet<>(Arrays.asList(ENABLE_GROUND_MELEE, UPGRADE_TO_LAIR,
                BUILD_EXTRACTOR)))
            .build();
        type.addConfiguration(UPGRADE_TO_LAIR, upgradeToLairAbstract, true);
        //upgrade to lair

        //build spire. as abstract plan, meet dependencies - upgrade to lair
        ConfigurationWithAbstractPlan buildSpire = ConfigurationWithAbstractPlan.builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        CAN_TRANSIT_FROM_5_POOL) != 0
                        && !dataForDecision.madeDecisionToAny()
                        && Decider.getDecision(AgentTypes.BUILDING_ORDER_MANAGER,
                        DesireKeys.ENABLE_AIR, dataForDecision, BUILDING_SPIRE)
                        && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_SPIRES) == 0
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_SPIRES_IN_CONSTRUCTION) == 0)
                .globalBeliefTypesByAgentType(Stream.concat(
                    BUILDING_SPIRE.getConvertersForFactsForGlobalBeliefsByAgentType().stream(),
                    Stream.of(COUNT_OF_SPIRES, COUNT_OF_SPIRES_IN_CONSTRUCTION)).collect(
                    Collectors.toSet()))
                .globalBeliefSetTypesByAgentType(
                    BUILDING_SPIRE.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .globalBeliefTypes(
                    Stream.concat(BUILDING_SPIRE.getConvertersForFactsForGlobalBeliefs().stream(),
                        Stream.of(CAN_TRANSIT_FROM_5_POOL)).collect(Collectors.toSet()))
                .desiresToConsider(new HashSet<>(Collections.singleton(ENABLE_AIR)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_SPIRES) > 0
                        || dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_SPIRES_IN_CONSTRUCTION) > 0)
                .globalBeliefTypesByAgentType(
                    Stream.of(COUNT_OF_SPIRES, COUNT_OF_SPIRES_IN_CONSTRUCTION).collect(
                        Collectors.toSet()))
                .build())
            .desiresForOthers(new HashSet<>(Collections.singletonList(ENABLE_AIR)))
            .desiresWithAbstractIntention(new HashSet<>(Collections.singleton(UPGRADE_TO_LAIR)))
            .build();
        type.addConfiguration(ENABLE_AIR, buildSpire, true);
        ConfigurationWithSharedDesire bdSpire = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(MORPH_TO_SPIRE)
            .reactionOnChangeStrategy(FIND_MAIN_BASE)
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE))
            .counts(1)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> false)
                .build())
            .build();
        type.addConfiguration(ENABLE_AIR, ENABLE_AIR, bdSpire);
        type.addConfiguration(UPGRADE_TO_LAIR, ENABLE_AIR, upgradeToLairAbstractCommon);
        //build spire

        //build evolution chamber
        ConfigurationWithSharedDesire buildEvolutionChamber = ConfigurationWithSharedDesire
            .builder()
            .sharedDesireKey(MORPH_TO_EVOLUTION_CHAMBER)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        CAN_TRANSIT_FROM_5_POOL) != 0
                        && !dataForDecision.madeDecisionToAny() &&
                        dataForDecision.getFeatureValueGlobalBeliefs(
                            COUNT_OF_EVOLUTION_CHAMBERS) == 0
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_EVOLUTION_CHAMBERS_IN_CONSTRUCTION) == 0
                        && Decider.getDecision(AgentTypes.BUILDING_ORDER_MANAGER,
                        DesireKeys.ENABLE_STATIC_ANTI_AIR, dataForDecision,
                        BUILDING_EVOLUTION_CHAMBER)
                )
                .globalBeliefTypesByAgentType(Stream.concat(
                    BUILDING_EVOLUTION_CHAMBER.getConvertersForFactsForGlobalBeliefsByAgentType()
                        .stream(),
                    Stream.of(COUNT_OF_EVOLUTION_CHAMBERS,
                        COUNT_OF_EVOLUTION_CHAMBERS_IN_CONSTRUCTION)).collect(
                    Collectors.toSet()))
                .globalBeliefSetTypesByAgentType(
                    BUILDING_EVOLUTION_CHAMBER
                        .getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .globalBeliefTypes(Stream.concat(
                    BUILDING_EVOLUTION_CHAMBER.getConvertersForFactsForGlobalBeliefs().stream(),
                    Stream.of(CAN_TRANSIT_FROM_5_POOL)).collect(Collectors.toSet()))
                .desiresToConsider(new HashSet<>(Collections.singleton(ENABLE_STATIC_ANTI_AIR)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_EVOLUTION_CHAMBERS) > 0
                        || dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_EVOLUTION_CHAMBERS_IN_CONSTRUCTION) > 0)
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Arrays.asList(COUNT_OF_EVOLUTION_CHAMBERS,
                        COUNT_OF_EVOLUTION_CHAMBERS_IN_CONSTRUCTION)))
                .build())
            .build();
        type.addConfiguration(ENABLE_STATIC_ANTI_AIR, buildEvolutionChamber);

        //other managers requirements

        //abstract plan to build pool if it is not present
        ConfigurationWithAbstractPlan buildPoolIfMissing = ConfigurationWithAbstractPlan.builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !dataForDecision.madeDecisionToAny() &&
                        dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_POOLS) == 0
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_POOLS_IN_CONSTRUCTION) == 0)
                .globalBeliefTypesByAgentType(
                    Stream.of(COUNT_OF_POOLS_IN_CONSTRUCTION, COUNT_OF_POOLS).collect(
                        Collectors.toSet()))
                .desiresToConsider(new HashSet<>(Collections.singleton(ENABLE_GROUND_MELEE)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_POOLS) > 0
                        || dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_POOLS_IN_CONSTRUCTION) > 0)
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Arrays.asList(COUNT_OF_POOLS_IN_CONSTRUCTION, COUNT_OF_POOLS)))
                .build())
            .build();

        //for melee
        type.addConfiguration(BOOST_GROUND_MELEE, buildPoolIfMissing, false);
        type.addConfiguration(ENABLE_GROUND_MELEE, BOOST_GROUND_MELEE, buildPoolCommon);

        //for sunken
        type.addConfiguration(MORPH_TO_SUNKEN_COLONY, buildPoolIfMissing, false);
        type.addConfiguration(ENABLE_GROUND_MELEE, MORPH_TO_SUNKEN_COLONY, buildPoolCommon);

        //abstract plan to build hydralisk den if it is not present
        ConfigurationWithAbstractPlan buildHydraliskDenIfMissing = ConfigurationWithAbstractPlan
            .builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !dataForDecision.madeDecisionToAny() &&
                        dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_HYDRALISK_DENS) == 0
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_HYDRALISK_DENS_IN_CONSTRUCTION) == 0
                )
                .desiresToConsider(new HashSet<>(Collections.singleton(ENABLE_GROUND_RANGED)))
                .globalBeliefTypesByAgentType(
                    Stream.of(COUNT_OF_HYDRALISK_DENS, COUNT_OF_HYDRALISK_DENS_IN_CONSTRUCTION)
                        .collect(Collectors.toSet()))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_HYDRALISK_DENS) > 0
                        || dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_HYDRALISK_DENS_IN_CONSTRUCTION) > 0)
                .globalBeliefTypesByAgentType(new HashSet<>(Arrays.asList(COUNT_OF_HYDRALISK_DENS,
                    COUNT_OF_HYDRALISK_DENS_IN_CONSTRUCTION)))
                .build())
            .desiresForOthers(new HashSet<>(Arrays.asList(ENABLE_GROUND_RANGED, BUILD_EXTRACTOR,
                ENABLE_GROUND_MELEE)))
            .build();
        type.addConfiguration(BOOST_GROUND_RANGED, buildHydraliskDenIfMissing, false);
        type.addConfiguration(ENABLE_GROUND_RANGED, BOOST_GROUND_RANGED, bdDen);
        type.addConfiguration(BUILD_EXTRACTOR, BOOST_GROUND_RANGED, buildExtractorCommon);
        type.addConfiguration(ENABLE_GROUND_MELEE, BOOST_GROUND_RANGED, buildPoolCommon);

        //abstract plan to build spire if it is not present
        ConfigurationWithAbstractPlan buildSpireIfMissing = ConfigurationWithAbstractPlan.builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !dataForDecision.madeDecisionToAny() &&
                        dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_SPIRES) == 0
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_SPIRES_IN_CONSTRUCTION) == 0)
                .globalBeliefTypesByAgentType(
                    Stream.of(COUNT_OF_SPIRES, COUNT_OF_SPIRES_IN_CONSTRUCTION).collect(
                        Collectors.toSet()))
                .desiresToConsider(new HashSet<>(Collections.singleton(ENABLE_AIR)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_SPIRES) > 0
                        || dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_SPIRES_IN_CONSTRUCTION) > 0)
                .globalBeliefTypesByAgentType(
                    Stream.of(COUNT_OF_SPIRES, COUNT_OF_SPIRES_IN_CONSTRUCTION).collect(
                        Collectors.toSet()))
                .build())
            .desiresForOthers(new HashSet<>(Collections.singletonList(ENABLE_AIR)))
            .desiresWithAbstractIntention(new HashSet<>(Collections.singleton(UPGRADE_TO_LAIR)))
            .build();
        type.addConfiguration(BOOST_AIR, buildSpireIfMissing, false);
        type.addConfiguration(ENABLE_AIR, BOOST_AIR, bdSpire);
        type.addConfiguration(UPGRADE_TO_LAIR, BOOST_AIR, upgradeToLairAbstractCommon);

        //abstract plan to build evolution chamber if it is not present
        ConfigurationWithAbstractPlan buildECIfMissing = ConfigurationWithAbstractPlan.builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    !dataForDecision.madeDecisionToAny() &&
                        dataForDecision.getFeatureValueGlobalBeliefs(
                            COUNT_OF_EVOLUTION_CHAMBERS) == 0
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_EVOLUTION_CHAMBERS_IN_CONSTRUCTION) == 0)
                .globalBeliefTypesByAgentType(Stream.of(COUNT_OF_EVOLUTION_CHAMBERS,
                    COUNT_OF_EVOLUTION_CHAMBERS_IN_CONSTRUCTION)
                    .collect(Collectors.toSet()))
                .desiresToConsider(new HashSet<>(Collections.singleton(ENABLE_STATIC_ANTI_AIR)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_EVOLUTION_CHAMBERS) > 0
                        || dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_EVOLUTION_CHAMBERS_IN_CONSTRUCTION) > 0)
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Arrays.asList(COUNT_OF_EVOLUTION_CHAMBERS,
                        COUNT_OF_EVOLUTION_CHAMBERS_IN_CONSTRUCTION)))
                .build())
            .desiresForOthers(new HashSet<>(Collections.singletonList(ENABLE_STATIC_ANTI_AIR)))
            .build();
        ConfigurationWithSharedDesire buildEC = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(MORPH_TO_EVOLUTION_CHAMBER)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> false)
                .build())
            .build();
        type.addConfiguration(MORPH_TO_SPORE_COLONY, buildECIfMissing, false);
        type.addConfiguration(ENABLE_STATIC_ANTI_AIR, MORPH_TO_SPORE_COLONY, buildEC);

      })
      .desiresForOthers(new HashSet<>(Arrays.asList(ENABLE_GROUND_MELEE, ENABLE_STATIC_ANTI_AIR)))
      .desiresWithAbstractIntention(
          new HashSet<>(Arrays.asList(UPGRADE_TO_LAIR, ENABLE_GROUND_RANGED,
              ENABLE_AIR)))
      .build();

}
