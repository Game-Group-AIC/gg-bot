package aic.gas.sc.gg_bot.bot.service.implementation;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.PLAYER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.CAN_TRANSIT_FROM_5_POOL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_EVOLUTION_CHAMBERS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_EVOLUTION_CHAMBERS_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_EXTRACTORS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_HATCHERIES;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_HATCHERIES_BEGINNING_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_HATCHERIES_BEING_CONSTRUCT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_HATCHERIES_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_HETCH;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_HYDRALISK_DENS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_HYDRALISK_DENS_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_LAIRS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_LAIRS_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_MINERALS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_MINERALS_TO_MINE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_MORPHING_OVERLORDS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_POOLS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_POOLS_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_SPIRES;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_SPIRES_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_WORKERS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.CURRENT_POPULATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.MAX_POPULATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_MOVE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.HAS_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_ENEMY_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_ISLAND;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MINERAL_ONLY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LAST_TIME_EXTRACTOR_BUILD;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LAST_TIME_HATCHERY_BUILD;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.MADE_OBSERVATION_IN_FRAME;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.REPRESENTS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BOOSTING_AIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BOOSTING_GROUND_MELEE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BOOSTING_GROUND_RANGED;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_EVOLUTION_CHAMBER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_EXTRACTOR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_HYDRALISK_DEN;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_POOL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_SPIRE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.EXPANDING;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.INCREASING_CAPACITY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.TRAINING_WORKER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.UPGRADING_TO_LAIR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.BOOST_AIR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.BOOST_GROUND_MELEE;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.BOOST_GROUND_RANGED;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.BUILD_EXTRACTOR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.BUILD_WORKER;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.ENABLE_AIR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.ENABLE_GROUND_MELEE;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.ENABLE_GROUND_RANGED;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.ENABLE_STATIC_ANTI_AIR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.EXPAND;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.HOLD_AIR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.HOLD_GROUND;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.INCREASE_CAPACITY;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_DRONE;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_EVOLUTION_CHAMBER;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_EXTRACTOR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_HYDRALISK_DEN;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_OVERLORD;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_POOL;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_SPIRE;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_SPORE_COLONY;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_SUNKEN_COLONY;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.UPGRADE_TO_LAIR;

import aic.gas.mas.model.metadata.AgentType;
import aic.gas.mas.model.metadata.DesireKey;
import aic.gas.mas.model.metadata.DesireKeyID;
import aic.gas.mas.model.metadata.agents.configuration.ConfigurationWithAbstractPlan;
import aic.gas.mas.model.metadata.agents.configuration.ConfigurationWithSharedDesire;
import aic.gas.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.mas.model.planing.ReactionOnChangeStrategy;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureContainerHeader;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.UnitWrapperFactory;
import aic.gas.sc.gg_bot.bot.model.Decider;
import aic.gas.sc.gg_bot.bot.model.agent.AbstractAgent;
import aic.gas.sc.gg_bot.bot.service.AbstractAgentsInitializer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of AbstractAgentsInitializer
 */
public class AbstractAgentsInitializerImpl implements AbstractAgentsInitializer {

  public static final ReactionOnChangeStrategy FIND_MAIN_BASE = (memory, desireParameters) -> {
    Optional<ABaseLocationWrapper> ourBase = memory
        .getReadOnlyMemoriesForAgentType(AgentTypes.BASE_LOCATION)
        .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_BASE).get())
        .filter(readOnlyMemory -> readOnlyMemory.returnFactSetValueForGivenKey(HAS_BASE).get()
            .anyMatch(aUnitOfPlayer -> !aUnitOfPlayer.isMorphing() && !aUnitOfPlayer
                .isBeingConstructed()))
        .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_BASE_LOCATION).get())
        .filter(ABaseLocationWrapper::isStartLocation)
        .findAny();
    if (ourBase.isPresent()) {
      memory.updateFact(BASE_TO_MOVE, ourBase.get());
    } else {
      memory.eraseFactValueForGivenKey(BASE_TO_MOVE);
    }
  };
  private static final AgentType ECO_MANAGER = AgentType.builder()
      .agentTypeID(AgentTypes.ECO_MANAGER)
      .usingTypesForFacts(
          new HashSet<>(
              Arrays.asList(BASE_TO_MOVE, LAST_TIME_HATCHERY_BUILD, LAST_TIME_EXTRACTOR_BUILD)))
      .initializationStrategy(type -> {

        //train drone
        ConfigurationWithSharedDesire trainDrone = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(MORPH_TO_DRONE)
            .counts(1)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    dataForDecision.getFeatureValueGlobalBeliefs(CAN_TRANSIT_FROM_5_POOL) != 0
                        && (Decider.getDecision(AgentTypes.ECO_MANAGER, DesireKeys.BUILD_WORKER,
                        dataForDecision, TRAINING_WORKER)
                        //or we are idle and there is still room for drone
                        || (!dataForDecision.madeDecisionToAny()
                        && (dataForDecision.getFeatureValueGlobalBeliefSets(
                        COUNT_OF_EXTRACTORS) * 3) + (
                        dataForDecision.getFeatureValueGlobalBeliefSets(
                            COUNT_OF_MINERALS_TO_MINE) * 2.5)
                        > dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_WORKERS) && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_MINERALS) >= 100))
                )
                .globalBeliefTypesByAgentType(Stream.concat(
                    TRAINING_WORKER.getConvertersForFactsForGlobalBeliefsByAgentType().stream(),
                    Stream.of(COUNT_OF_MINERALS)).collect(Collectors.toSet()))
                .globalBeliefSetTypesByAgentType(Stream.concat(
                    TRAINING_WORKER.getConvertersForFactSetsForGlobalBeliefsByAgentType().stream(),
                    Stream.of(COUNT_OF_EXTRACTORS, COUNT_OF_MINERALS_TO_MINE)).collect(
                    Collectors.toSet()))
                .globalBeliefTypes(
                    Stream.concat(TRAINING_WORKER.getConvertersForFactsForGlobalBeliefs().stream(),
                        Stream.of(COUNT_OF_WORKERS, CAN_TRANSIT_FROM_5_POOL)).collect(
                        Collectors.toSet()))
                .desiresToConsider(
                    new HashSet<>(Arrays.asList(INCREASE_CAPACITY, EXPAND, BUILD_EXTRACTOR)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !Decider.getDecision(AgentTypes.ECO_MANAGER,
                        DesireKeys.BUILD_WORKER, dataForDecision, TRAINING_WORKER))
                .globalBeliefTypesByAgentType(
                    TRAINING_WORKER.getConvertersForFactsForGlobalBeliefsByAgentType())
                .globalBeliefSetTypesByAgentType(
                    TRAINING_WORKER.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .globalBeliefTypes(TRAINING_WORKER.getConvertersForFactsForGlobalBeliefs())
                .build())
            .build();
        type.addConfiguration(BUILD_WORKER, trainDrone);

        //morph to overlord
        ConfigurationWithSharedDesire trainOverlord = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(MORPH_TO_OVERLORD)
            .counts(1)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_MORPHING_OVERLORDS) == 0
                        && (dataForDecision.getFeatureValueGlobalBeliefs(
                        CURRENT_POPULATION) >= dataForDecision.getFeatureValueGlobalBeliefs(
                        MAX_POPULATION)
                        || Decider.getDecision(AgentTypes.ECO_MANAGER,
                        DesireKeys.INCREASE_CAPACITY, dataForDecision, INCREASING_CAPACITY)))
                .globalBeliefTypesByAgentType(Stream.concat(
                    INCREASING_CAPACITY.getConvertersForFactsForGlobalBeliefsByAgentType().stream(),
                    Stream.of(CURRENT_POPULATION, MAX_POPULATION)).collect(Collectors.toSet()))
                .globalBeliefSetTypesByAgentType(
                    INCREASING_CAPACITY.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .globalBeliefTypes(Stream.concat(
                    INCREASING_CAPACITY.getConvertersForFactsForGlobalBeliefs().stream(),
                    Stream.of(COUNT_OF_MORPHING_OVERLORDS, CAN_TRANSIT_FROM_5_POOL)).collect(
                    Collectors.toSet()))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    !Decider.getDecision(AgentTypes.ECO_MANAGER, DesireKeys.INCREASE_CAPACITY,
                        dataForDecision, INCREASING_CAPACITY)
                        || dataForDecision.getFeatureValueGlobalBeliefs(CURRENT_POPULATION)
                        < dataForDecision.getFeatureValueGlobalBeliefs(MAX_POPULATION))
                .globalBeliefTypesByAgentType(Stream.concat(
                    INCREASING_CAPACITY.getConvertersForFactsForGlobalBeliefsByAgentType().stream(),
                    Stream.of(CURRENT_POPULATION, MAX_POPULATION)).collect(Collectors.toSet()))
                .globalBeliefSetTypesByAgentType(
                    INCREASING_CAPACITY.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .globalBeliefTypes(Stream.concat(
                    INCREASING_CAPACITY.getConvertersForFactsForGlobalBeliefs().stream(),
                    Stream.of(COUNT_OF_MORPHING_OVERLORDS, CAN_TRANSIT_FROM_5_POOL)).collect(
                    Collectors.toSet()))
                .build())
            .build();
        type.addConfiguration(INCREASE_CAPACITY, trainOverlord);

        //expand to another base
        ConfigurationWithSharedDesire expand = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(EXPAND)
            .counts(1)
            .reactionOnChangeStrategy((memory, desireParameters) -> {

              //find nearest free base closest to any of our bases
              Set<ABaseLocationWrapper> ourBases = memory.getReadOnlyMemoriesForAgentType(
                  AgentTypes.BASE_LOCATION)
                  .filter(
                      readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_BASE).get())
                  .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                      IS_BASE_LOCATION).get())
                  .collect(Collectors.toSet());

              //places where base is currently build
              Set<ABaseLocationWrapper> placesWhereBaseIsCurrentlyBuild = UnitWrapperFactory
                  .getStreamOfAllAlivePlayersUnits()
                  .filter(aUnitOfPlayer -> aUnitOfPlayer.getType().isWorker())
                  .filter(aUnitOfPlayer -> !aUnitOfPlayer.getTrainingQueue().isEmpty())
                  .filter(aUnitOfPlayer -> aUnitOfPlayer.getTrainingQueue().get(0).isBase())
                  .map(AUnit::getNearestBaseLocation)
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .filter(aBaseLocationWrapper -> !ourBases.contains(aBaseLocationWrapper))
                  .collect(Collectors.toSet());

              Optional<ABaseLocationWrapper> basesToExpand = memory.getReadOnlyMemoriesForAgentType(
                  AgentTypes.BASE_LOCATION)
                  .filter(readOnlyMemory -> !readOnlyMemory.returnFactValueForGivenKey(
                      IS_ENEMY_BASE).get()
                      && !readOnlyMemory.returnFactValueForGivenKey(IS_ISLAND).get())
                  .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                      IS_BASE_LOCATION).get())
                  .filter(aBaseLocationWrapper -> !ourBases.contains(aBaseLocationWrapper)
                      && !placesWhereBaseIsCurrentlyBuild.contains(aBaseLocationWrapper))
                  .min(Comparator.comparingDouble(value -> ourBases.stream()
                      .mapToDouble(other -> other.distanceTo(value))
                      .min().orElse(Integer.MAX_VALUE)));

              if (basesToExpand.isPresent()) {
                memory.updateFact(BASE_TO_MOVE, basesToExpand.get());
              } else {
                memory.eraseFactValueForGivenKey(BASE_TO_MOVE);
              }

            })
            .reactionOnChangeStrategyInIntention((memory, desireParameters) -> {
              memory.eraseFactValueForGivenKey(BASE_TO_MOVE);
              memory.updateFact(LAST_TIME_HATCHERY_BUILD,
                  memory.getReadOnlyMemoriesForAgentType(PLAYER)
                      .mapToInt(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                          MADE_OBSERVATION_IN_FRAME).orElse(0))
                      .max().orElse(0));
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        CAN_TRANSIT_FROM_5_POOL) != 0
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_HATCHERIES_IN_CONSTRUCTION) == 0
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_HATCHERIES_BEING_CONSTRUCT) == 0
                        && (memory.returnFactValueForGivenKey(LAST_TIME_HATCHERY_BUILD).orElse(
                        0) + 100
                        < memory.getReadOnlyMemoriesForAgentType(PLAYER)
                        .mapToInt(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                            MADE_OBSERVATION_IN_FRAME).orElse(0))
                        .max().orElse(0))
                        && (Decider.getDecision(AgentTypes.ECO_MANAGER, DesireKeys.EXPAND,
                        dataForDecision, EXPANDING))
//                                        || (dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_MINERALS) > 350
//                                        && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_HATCHERIES) == 2))
                )
                .globalBeliefTypes(
                    Stream.concat(EXPANDING.getConvertersForFactsForGlobalBeliefs().stream(),
                        Stream.of(COUNT_OF_HATCHERIES_IN_CONSTRUCTION,
                            CAN_TRANSIT_FROM_5_POOL)).collect(Collectors.toSet()))
                .globalBeliefTypesByAgentType(Stream.concat(
                    EXPANDING.getConvertersForFactsForGlobalBeliefsByAgentType().stream(),
                    Stream.of(COUNT_OF_HATCHERIES_BEING_CONSTRUCT, COUNT_OF_HETCH,
                        COUNT_OF_MINERALS, COUNT_OF_HATCHERIES)).collect(Collectors.toSet()))
                .globalBeliefSetTypesByAgentType(
                    EXPANDING.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    !Decider.getDecision(AgentTypes.ECO_MANAGER, DesireKeys.EXPAND, dataForDecision,
                        EXPANDING)
                        || !memory.returnFactValueForGivenKey(BASE_TO_MOVE).isPresent()
                        || dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_HATCHERIES_IN_CONSTRUCTION) > 0
                        || dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_HATCHERIES_BEING_CONSTRUCT) > 0
                        || dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_HATCHERIES_BEGINNING_CONSTRUCTION) > 0
                )
                .globalBeliefTypes(
                    Stream.concat(EXPANDING.getConvertersForFactsForGlobalBeliefs().stream(),
                        Stream.of(COUNT_OF_HATCHERIES_IN_CONSTRUCTION,
                            CAN_TRANSIT_FROM_5_POOL)).collect(Collectors.toSet()))
                .globalBeliefTypesByAgentType(Stream.concat(
                    EXPANDING.getConvertersForFactsForGlobalBeliefsByAgentType().stream(),
                    Stream.of(COUNT_OF_HATCHERIES_BEING_CONSTRUCT, COUNT_OF_HETCH,
                        COUNT_OF_MINERALS, COUNT_OF_HATCHERIES,
                        COUNT_OF_HATCHERIES_BEGINNING_CONSTRUCTION)).collect(
                    Collectors.toSet()))
                .globalBeliefSetTypesByAgentType(
                    EXPANDING.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .build())
            .build();
        type.addConfiguration(EXPAND, expand);

        ConfigurationWithSharedDesire buildExtractor = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(MORPH_TO_EXTRACTOR)
            .counts(1)
            .reactionOnChangeStrategy((memory, desireParameters) -> {

              //extractors bases
              Set<ABaseLocationWrapper> extractorsBases = memory.getReadOnlyMemoriesForAgentType(
                  AgentTypes.EXTRACTOR)
                  .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                      REPRESENTS_UNIT).get())
                  .filter(
                      aUnitWithCommands -> aUnitWithCommands.getNearestBaseLocation().isPresent())
                  .map(aUnitWithCommands -> aUnitWithCommands.getNearestBaseLocation().get())
                  .collect(Collectors.toSet());

              //gas bases
              Optional<ABaseLocationWrapper> ourBase = memory.getReadOnlyMemoriesForAgentType(
                  AgentTypes.BASE_LOCATION)
                  .filter(
                      readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_BASE).get())
                  .filter(readOnlyMemory -> !readOnlyMemory.returnFactValueForGivenKey(
                      IS_MINERAL_ONLY).get())
                  .filter(readOnlyMemory -> readOnlyMemory.returnFactSetValueForGivenKey(
                      HAS_BASE).isPresent())
                  .filter(readOnlyMemory -> readOnlyMemory.returnFactSetValueForGivenKey(
                      HAS_BASE).get()
                      .anyMatch(
                          aUnitOfPlayer -> !aUnitOfPlayer.isMorphing() && !aUnitOfPlayer
                              .isBeingConstructed()))
                  .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                      IS_BASE_LOCATION).get())
                  .filter(aBaseLocationWrapper -> !extractorsBases.contains(aBaseLocationWrapper))
                  .findAny();

              if (ourBase.isPresent()) {
                memory.updateFact(BASE_TO_MOVE, ourBase.get());
              } else {
                memory.eraseFactValueForGivenKey(BASE_TO_MOVE);
              }

            })
            .reactionOnChangeStrategyInIntention((memory, desireParameters) -> {
              memory.eraseFactValueForGivenKey(BASE_TO_MOVE);
              memory.updateFact(LAST_TIME_EXTRACTOR_BUILD,
                  memory.getReadOnlyMemoriesForAgentType(PLAYER)
                      .mapToInt(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                          MADE_OBSERVATION_IN_FRAME).orElse(0))
                      .max().orElse(0));
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        CAN_TRANSIT_FROM_5_POOL) != 0
                        && !dataForDecision.madeDecisionToAny()
                        && (memory.returnFactValueForGivenKey(LAST_TIME_EXTRACTOR_BUILD).orElse(
                        0) + 100
                        < memory.getReadOnlyMemoriesForAgentType(PLAYER)
                        .mapToInt(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                            MADE_OBSERVATION_IN_FRAME).orElse(0))
                        .max().orElse(0))
                        && Decider.getDecision(AgentTypes.ECO_MANAGER,
                        DesireKeys.BUILD_EXTRACTOR, dataForDecision, BUILDING_EXTRACTOR))
                .globalBeliefTypes(Stream.concat(
                    BUILDING_EXTRACTOR.getConvertersForFactsForGlobalBeliefs().stream(),
                    Stream.of(CAN_TRANSIT_FROM_5_POOL)).collect(Collectors.toSet()))
                .globalBeliefTypesByAgentType(
                    BUILDING_EXTRACTOR.getConvertersForFactsForGlobalBeliefsByAgentType())
                .globalBeliefSetTypesByAgentType(
                    BUILDING_EXTRACTOR.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .desiresToConsider(new HashSet<>(Collections.singleton(BUILD_EXTRACTOR)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> !memory.returnFactValueForGivenKey(
                    BASE_TO_MOVE).isPresent()
                    || !Decider.getDecision(AgentTypes.ECO_MANAGER, DesireKeys.BUILD_EXTRACTOR,
                    dataForDecision, BUILDING_EXTRACTOR))
                .globalBeliefTypes(BUILDING_EXTRACTOR.getConvertersForFactsForGlobalBeliefs())
                .globalBeliefTypesByAgentType(
                    BUILDING_EXTRACTOR.getConvertersForFactsForGlobalBeliefsByAgentType())
                .globalBeliefSetTypesByAgentType(
                    BUILDING_EXTRACTOR.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .useFactsInMemory(true)
                .build())
            .build();
        type.addConfiguration(BUILD_EXTRACTOR, buildExtractor);

        //abstract plan from desire by another agent to build extractor (to meet requirements for building)
        ConfigurationWithAbstractPlan buildExtractorFromOtherAgent = ConfigurationWithAbstractPlan
            .builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> !dataForDecision.madeDecisionToAny())
                .desiresToConsider(new HashSet<>(Collections.singleton(BUILD_EXTRACTOR)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> false)
                .build())
            .desiresForOthers(new HashSet<>(Collections.singleton(BUILD_EXTRACTOR)))
            .build();
        type.addConfiguration(BUILD_EXTRACTOR, buildExtractorFromOtherAgent, false);
        ConfigurationWithSharedDesire buildExtractorForAbstractPlan = ConfigurationWithSharedDesire
            .builder()
            .sharedDesireKey(MORPH_TO_EXTRACTOR)
            .counts(1)
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE))
            .reactionOnChangeStrategy((memory, desireParameters) -> {

              //extractors bases
              Set<ABaseLocationWrapper> extractorsBases = memory.getReadOnlyMemoriesForAgentType(
                  AgentTypes.EXTRACTOR)
                  .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                      REPRESENTS_UNIT).get())
                  .filter(
                      aUnitWithCommands -> aUnitWithCommands.getNearestBaseLocation().isPresent())
                  .map(aUnitWithCommands -> aUnitWithCommands.getNearestBaseLocation().get())
                  .collect(Collectors.toSet());

              //gas bases
              Optional<ABaseLocationWrapper> ourBase = memory.getReadOnlyMemoriesForAgentType(
                  AgentTypes.BASE_LOCATION)
                  .filter(
                      readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_BASE).get())
                  .filter(readOnlyMemory -> !readOnlyMemory.returnFactValueForGivenKey(
                      IS_MINERAL_ONLY).get())
                  .filter(readOnlyMemory -> readOnlyMemory.returnFactSetValueForGivenKey(
                      HAS_BASE).isPresent())
                  .filter(readOnlyMemory -> readOnlyMemory.returnFactSetValueForGivenKey(
                      HAS_BASE).get()
                      .anyMatch(
                          aUnitOfPlayer -> !aUnitOfPlayer.isMorphing() && !aUnitOfPlayer
                              .isBeingConstructed()))
                  .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                      IS_BASE_LOCATION).get())
                  .filter(aBaseLocationWrapper -> !extractorsBases.contains(aBaseLocationWrapper))
                  .findAny();

              if (ourBase.isPresent()) {
                memory.updateFact(BASE_TO_MOVE, ourBase.get());
              } else {
                memory.eraseFactValueForGivenKey(BASE_TO_MOVE);
              }

            })
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE))
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> false)
                .build())
            .build();
        type.addConfiguration(BUILD_EXTRACTOR, BUILD_EXTRACTOR, buildExtractorForAbstractPlan);

      })
      .desiresForOthers(
          new HashSet<>(Arrays.asList(BUILD_WORKER, INCREASE_CAPACITY, EXPAND, BUILD_EXTRACTOR)))
      .build();

  private static final AgentType UNIT_ORDER_MANAGER = AgentType.builder()
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
  private static final AgentType BUILDING_ORDER_MANAGER = AgentType.builder()
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

  @Override
  public List<AbstractAgent> initializeAbstractAgents(BotFacade botFacade) {
    List<AbstractAgent> abstractAgents = new ArrayList<>();
    abstractAgents.add(new AbstractAgent(ECO_MANAGER, botFacade));
    abstractAgents.add(new AbstractAgent(BUILDING_ORDER_MANAGER, botFacade));
    abstractAgents.add(new AbstractAgent(UNIT_ORDER_MANAGER, botFacade));
    return abstractAgents;
  }

}
