package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.virtual;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_HATCHERIES;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_HATCHERIES_BEGINNING_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_HATCHERIES_BEING_CONSTRUCT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_HATCHERIES_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_HETCH;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_MINERALS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_MORPHING_OVERLORDS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.CURRENT_POPULATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.MAX_POPULATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_MOVE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.HAS_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_ENEMY_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_ISLAND;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MINERAL_ONLY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.REPRESENTS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_EXTRACTOR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.EXPANDING;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.INCREASING_CAPACITY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.TRAINING_WORKER;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.DRONE_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.EXTRACTOR_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.HATCHERY_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.OVERLORD_TYPE;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.BUILD_EXTRACTOR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.BUILD_WORKER;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.EXPAND;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.INCREASE_CAPACITY;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_DRONE;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_EXTRACTOR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_OVERLORD;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.UnitWrapperFactory;
import aic.gas.sc.gg_bot.bot.model.Decider;
import aic.gas.sc.gg_bot.bot.service.implementation.BuildLockerService;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentType;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithAbstractPlan;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithSharedDesire;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

//TODO makes sense to replace workers?
//TODO everything as abstract plan - reservation is made, shared desire is sub-plan - checking if there is enough resources
@Slf4j
public class EcoManagerAgentType {

  public static final AgentType ECO_MANAGER = AgentType.builder()
      .agentTypeID(AgentTypes.ECO_MANAGER)
      .usingTypesForFacts(
          new HashSet<>(Collections.singletonList(BASE_TO_MOVE)))
      .initializationStrategy(type -> {

        //train drone
        ConfigurationWithSharedDesire trainDrone = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(MORPH_TO_DRONE)
            .counts(1)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> Decider
                    .getDecision(AgentTypes.ECO_MANAGER, DesireKeys.BUILD_WORKER,
                        dataForDecision, TRAINING_WORKER, memory.getCurrentClock())
                    && !BuildLockerService.getInstance().isLocked(DRONE_TYPE)
                )
                .globalBeliefTypesByAgentType(
                    TRAINING_WORKER.getConvertersForFactsForGlobalBeliefsByAgentType())
                .globalBeliefSetTypesByAgentType(
                    TRAINING_WORKER.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .globalBeliefTypes(TRAINING_WORKER.getConvertersForFactsForGlobalBeliefs())
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !Decider.getDecision(AgentTypes.ECO_MANAGER,
                        DesireKeys.BUILD_WORKER, dataForDecision, TRAINING_WORKER,
                        memory.getCurrentClock()) || BuildLockerService.getInstance()
                        .isLocked(DRONE_TYPE))
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
                    !BuildLockerService.getInstance().isLocked(OVERLORD_TYPE)
                        && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_MORPHING_OVERLORDS)
                        == 0 && (dataForDecision.getFeatureValueGlobalBeliefs(CURRENT_POPULATION)
                        >= dataForDecision.getFeatureValueGlobalBeliefs(
                        MAX_POPULATION) || Decider.getDecision(AgentTypes.ECO_MANAGER,
                        DesireKeys.INCREASE_CAPACITY, dataForDecision, INCREASING_CAPACITY,
                        memory.getCurrentClock())))
                .globalBeliefTypesByAgentType(Stream.concat(
                    INCREASING_CAPACITY.getConvertersForFactsForGlobalBeliefsByAgentType().stream(),
                    Stream.of(CURRENT_POPULATION, MAX_POPULATION)).collect(Collectors.toSet()))
                .globalBeliefSetTypesByAgentType(
                    INCREASING_CAPACITY.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .globalBeliefTypes(Stream.concat(
                    INCREASING_CAPACITY.getConvertersForFactsForGlobalBeliefs().stream(),
                    Stream.of(COUNT_OF_MORPHING_OVERLORDS)).collect(
                    Collectors.toSet()))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    !Decider.getDecision(AgentTypes.ECO_MANAGER, DesireKeys.INCREASE_CAPACITY,
                        dataForDecision, INCREASING_CAPACITY, memory.getCurrentClock())
                        || dataForDecision.getFeatureValueGlobalBeliefs(CURRENT_POPULATION)
                        < dataForDecision.getFeatureValueGlobalBeliefs(MAX_POPULATION)
                        || BuildLockerService.getInstance().isLocked(OVERLORD_TYPE)
                        || dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_MORPHING_OVERLORDS)
                        > 0)
                .globalBeliefTypesByAgentType(Stream.concat(
                    INCREASING_CAPACITY.getConvertersForFactsForGlobalBeliefsByAgentType().stream(),
                    Stream.of(CURRENT_POPULATION, MAX_POPULATION)).collect(Collectors.toSet()))
                .globalBeliefSetTypesByAgentType(
                    INCREASING_CAPACITY.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .globalBeliefTypes(Stream.concat(
                    INCREASING_CAPACITY.getConvertersForFactsForGlobalBeliefs().stream(),
                    Stream.of(COUNT_OF_MORPHING_OVERLORDS)).collect(
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
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_HATCHERIES_IN_CONSTRUCTION) == 0
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_HATCHERIES_BEING_CONSTRUCT) == 0
                        // Hatchery has not been built recently
                        && !BuildLockerService.getInstance().isLocked(HATCHERY_TYPE)
                        && (Decider.getDecision(AgentTypes.ECO_MANAGER, DesireKeys.EXPAND,
                        dataForDecision, EXPANDING, memory.getCurrentClock())
                        || (dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_MINERALS) > 450
                        && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_HATCHERIES) < 2))
                )
                .globalBeliefTypes(
                    Stream.concat(EXPANDING.getConvertersForFactsForGlobalBeliefs().stream(),
                        Stream.of(COUNT_OF_HATCHERIES_IN_CONSTRUCTION)).collect(Collectors.toSet()))
                .globalBeliefTypesByAgentType(Stream.concat(
                    EXPANDING.getConvertersForFactsForGlobalBeliefsByAgentType().stream(),
                    Stream.of(COUNT_OF_HATCHERIES_BEING_CONSTRUCT, COUNT_OF_HETCH,
                        COUNT_OF_MINERALS, COUNT_OF_HATCHERIES)).collect(Collectors.toSet()))
                .globalBeliefSetTypesByAgentType(
                    EXPANDING.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    !memory.returnFactValueForGivenKey(BASE_TO_MOVE).isPresent() ||
                        BuildLockerService.getInstance().isLocked(HATCHERY_TYPE)
                        || !Decider
                        .getDecision(AgentTypes.ECO_MANAGER, DesireKeys.EXPAND, dataForDecision,
                            EXPANDING, memory.getCurrentClock())
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
                        Stream.of(COUNT_OF_HATCHERIES_IN_CONSTRUCTION)).collect(Collectors.toSet()))
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
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        Decider.getDecision(AgentTypes.ECO_MANAGER,
                            DesireKeys.BUILD_EXTRACTOR, dataForDecision, BUILDING_EXTRACTOR,
                            memory.getCurrentClock())
                            && !BuildLockerService.getInstance().isLocked(EXTRACTOR_TYPE)
                )
                .globalBeliefTypes(BUILDING_EXTRACTOR.getConvertersForFactsForGlobalBeliefs())
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
                    dataForDecision, BUILDING_EXTRACTOR, memory.getCurrentClock())
                    || BuildLockerService.getInstance().isLocked(EXTRACTOR_TYPE)
                )
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
}
