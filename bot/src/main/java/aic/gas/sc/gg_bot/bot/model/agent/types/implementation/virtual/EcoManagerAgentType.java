package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.virtual;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.CURRENT_POPULATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.MAX_POPULATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_MOVE;
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
import static aic.gas.sc.gg_bot.bot.model.agent.types.implementation.AgentTypeUtils.createConfigurationWithSharedDesireToBuildFromTemplate;
import static aic.gas.sc.gg_bot.bot.model.agent.types.implementation.AgentTypeUtils.createConfigurationWithSharedDesireToTrainFromTemplate;
import static aic.gas.sc.gg_bot.bot.model.agent.types.implementation.AgentTypeUtils.createOwnConfigurationWithAbstractPlanToTrainFromTemplate;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.UnitWrapperFactory;
import aic.gas.sc.gg_bot.bot.model.Decider;
import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import aic.gas.sc.gg_bot.bot.service.implementation.BuildLockerService;
import aic.gas.sc.gg_bot.mas.model.knowledge.Memory;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentType;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithAbstractPlan;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithSharedDesire;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

//TODO makes sense to replace workers?
@Slf4j
public class EcoManagerAgentType {

  /**
   * Finds base without extractor given the beliefs
   */
  private static Optional<ABaseLocationWrapper> getOurBaseWithoutExtractor(Memory<?> memory) {

    //bases with extractor
    Set<ABaseLocationWrapper> extractorsBases = memory
        .getReadOnlyMemoriesForAgentType(AgentTypes.EXTRACTOR)
        .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(REPRESENTS_UNIT).get())
        .map(AUnit::getNearestBaseLocation)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toSet());

    return memory.getReadOnlyMemoriesForAgentType(AgentTypes.BASE_LOCATION)
        .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_BASE).get())
        .filter(readOnlyMemory -> !readOnlyMemory.returnFactValueForGivenKey(IS_MINERAL_ONLY).get())
        .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_BASE_LOCATION).get())
        .filter(aBaseLocationWrapper -> !extractorsBases.contains(aBaseLocationWrapper))
        .findAny();
  }

  public static final AgentType ECO_MANAGER = AgentType.builder()
      .agentTypeID(AgentTypes.ECO_MANAGER)
      .usingTypesForFacts(
          new HashSet<>(Collections.singletonList(BASE_TO_MOVE)))
      .initializationStrategy((AgentType type) -> {

        //train drone
        ConfigurationWithAbstractPlan trainDroneAbstractPlan = createOwnConfigurationWithAbstractPlanToTrainFromTemplate(
            BUILD_WORKER, AUnitTypeWrapper.DRONE_TYPE, AgentTypes.ECO_MANAGER, TRAINING_WORKER);
        type.addConfiguration(BUILD_WORKER, trainDroneAbstractPlan, true);
        ConfigurationWithSharedDesire trainDroneShared = createConfigurationWithSharedDesireToTrainFromTemplate(
            MORPH_TO_DRONE, AUnitTypeWrapper.DRONE_TYPE);
        type.addConfiguration(BUILD_WORKER, BUILD_WORKER, trainDroneShared);

        //expand
        ConfigurationWithAbstractPlan expand = ConfigurationWithAbstractPlan.builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        !BuildLockerService.getInstance().isLocked(AUnitTypeWrapper.HATCHERY_TYPE)
                            && !BotFacade.RESOURCE_MANAGER
                            .hasMadeReservationOn(HATCHERY_TYPE, memory.getAgentId())
                            // Hatchery has not been built recently
                            && Decider.getDecision(AgentTypes.ECO_MANAGER, DesireKeys.EXPAND,
                            dataForDecision, EXPANDING, memory.getCurrentClock(),
                            memory.getAgentId())
                )
                .globalBeliefTypes(EXPANDING.getConvertersForFactsForGlobalBeliefs())
                .globalBeliefTypesByAgentType(
                    EXPANDING.getConvertersForFactsForGlobalBeliefsByAgentType())
                .globalBeliefSetTypesByAgentType(
                    EXPANDING.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    !Decider.getDecision(AgentTypes.ECO_MANAGER, DesireKeys.EXPAND,
                        dataForDecision, EXPANDING, memory.getCurrentClock(), memory.getAgentId())
                        || BuildLockerService.getInstance()
                        .isLocked(AUnitTypeWrapper.HATCHERY_TYPE))
                .globalBeliefTypes(EXPANDING.getConvertersForFactsForGlobalBeliefs())
                .globalBeliefTypesByAgentType(
                    EXPANDING.getConvertersForFactsForGlobalBeliefsByAgentType())
                .globalBeliefSetTypesByAgentType(
                    EXPANDING.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .build())
            .reactionOnChangeStrategy((memory, desireParameters) -> BotFacade.RESOURCE_MANAGER
                .makeReservation(HATCHERY_TYPE, memory.getAgentId()))
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER
                    .removeReservation(HATCHERY_TYPE, memory.getAgentId()))
            .desiresForOthers(Collections.singleton(EXPAND))
            .build();
        type.addConfiguration(EXPAND, expand, true);

        ConfigurationWithSharedDesire buildExpansion = createConfigurationWithSharedDesireToBuildFromTemplate(
            EXPAND, AUnitTypeWrapper.HATCHERY_TYPE, (memory, desireParameters) -> {

              //find our bases
              Stream<ABaseLocationWrapper> ourBases = memory.getReadOnlyMemoriesForAgentType(
                  AgentTypes.BASE_LOCATION)
                  .filter(
                      readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_BASE).get())
                  .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_BASE_LOCATION)
                      .get());

              //places where base is currently built
              Stream<ABaseLocationWrapper> placesWhereBaseIsCurrentlyBuild = UnitWrapperFactory
                  .getStreamOfAllAlivePlayersUnits()
                  .filter(aUnitOfPlayer -> aUnitOfPlayer.getType().isWorker())
                  .filter(AUnit::isConstructing)
                  .filter(aUnitOfPlayer -> !aUnitOfPlayer.getTrainingQueue().isEmpty())
                  .filter(aUnitOfPlayer -> aUnitOfPlayer.getTrainingQueue().get(0).isBase())
                  .map(AUnit::getNearestBaseLocation)
                  .filter(Optional::isPresent)
                  .map(Optional::get);

              Set<ABaseLocationWrapper> locationsToExclude = Stream
                  .concat(ourBases, placesWhereBaseIsCurrentlyBuild)
                  .collect(Collectors.toSet());

              //find free closest base to expand
              Optional<ABaseLocationWrapper> basesToExpand = memory.getReadOnlyMemoriesForAgentType(
                  AgentTypes.BASE_LOCATION)
                  .filter(readOnlyMemory ->
                      !readOnlyMemory.returnFactValueForGivenKey(IS_ENEMY_BASE).get()
                          && !readOnlyMemory.returnFactValueForGivenKey(IS_ISLAND).get())
                  .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_BASE_LOCATION)
                      .get())
                  .filter(
                      aBaseLocationWrapper -> !locationsToExclude.contains(aBaseLocationWrapper))
                  .min(Comparator.comparingDouble(value -> locationsToExclude.stream()
                      .mapToDouble(other -> other.distanceTo(value)).min()
                      .orElse(Integer.MAX_VALUE)));

              if (basesToExpand.isPresent()) {
                memory.updateFact(BASE_TO_MOVE, basesToExpand.get());
              } else {
                memory.eraseFactValueForGivenKey(BASE_TO_MOVE);
              }
            }, (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE));
        type.addConfiguration(EXPAND, EXPAND, buildExpansion);

        //build extractor
        ConfigurationWithAbstractPlan buildExtractorAbstract = ConfigurationWithAbstractPlan
            .builder()
            .reactionOnChangeStrategy((memory, desireParameters) -> BotFacade.RESOURCE_MANAGER
                .makeReservation(EXTRACTOR_TYPE, memory.getAgentId()))
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER
                    .removeReservation(EXTRACTOR_TYPE, memory.getAgentId()))
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        !dataForDecision.madeDecisionToAny()
                            && !BotFacade.RESOURCE_MANAGER
                            .hasMadeReservationOn(AUnitTypeWrapper.EXTRACTOR_TYPE,
                                memory.getAgentId())
                            && !BuildLockerService.getInstance()
                            .isLocked(AUnitTypeWrapper.EXTRACTOR_TYPE)
                            //there is base without extractor
                            && getOurBaseWithoutExtractor(memory).isPresent()
                            //there are no extractors in construction
                            && Decider
                            .getDecision(AgentTypes.ECO_MANAGER, DesireKeys.BUILD_EXTRACTOR,
                                dataForDecision, BUILDING_EXTRACTOR, memory.getCurrentClock(),
                                memory.getAgentId())
                )
                .globalBeliefTypes(BUILDING_EXTRACTOR.getConvertersForFactsForGlobalBeliefs())
                .globalBeliefTypesByAgentType(
                    BUILDING_EXTRACTOR.getConvertersForFactsForGlobalBeliefsByAgentType())
                .globalBeliefSetTypesByAgentType(
                    BUILDING_EXTRACTOR.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .desiresToConsider(Collections.singleton(BUILD_EXTRACTOR))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        !Decider.getDecision(AgentTypes.ECO_MANAGER, DesireKeys.BUILD_EXTRACTOR,
                            dataForDecision, BUILDING_EXTRACTOR, memory.getCurrentClock(),
                            memory.getAgentId()) || BuildLockerService.getInstance()
                            .isLocked(AUnitTypeWrapper.EXTRACTOR_TYPE)
                            || !getOurBaseWithoutExtractor(memory).isPresent())
                .globalBeliefTypes(BUILDING_EXTRACTOR.getConvertersForFactsForGlobalBeliefs())
                .globalBeliefTypesByAgentType(
                    BUILDING_EXTRACTOR.getConvertersForFactsForGlobalBeliefsByAgentType())
                .globalBeliefSetTypesByAgentType(
                    BUILDING_EXTRACTOR.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .build())
            .desiresForOthers(Collections.singleton(BUILD_EXTRACTOR))
            .build();
        type.addConfiguration(BUILD_EXTRACTOR, buildExtractorAbstract, true);

        //share desire to build extractor with the system
        ConfigurationWithSharedDesire buildExtractorShared = createConfigurationWithSharedDesireToBuildFromTemplate(
            MORPH_TO_EXTRACTOR, AUnitTypeWrapper.EXTRACTOR_TYPE, (memory, desireParameters) -> {
              Optional<ABaseLocationWrapper> baseWithoutExtractor = getOurBaseWithoutExtractor(
                  memory);
              if (baseWithoutExtractor.isPresent()) {
                memory.updateFact(BASE_TO_MOVE, baseWithoutExtractor.get());
              } else {
                memory.eraseFactValueForGivenKey(BASE_TO_MOVE);
              }
            }, (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE));
        type.addConfiguration(BUILD_EXTRACTOR, BUILD_EXTRACTOR, buildExtractorShared);

        //morph to overlord abstract plan
        ConfigurationWithAbstractPlan trainOverlord = ConfigurationWithAbstractPlan
            .builder()
            .reactionOnChangeStrategy((memory, desireParameters) -> BotFacade.RESOURCE_MANAGER
                .makeReservation(OVERLORD_TYPE, memory.getAgentId()))
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER
                    .removeReservation(OVERLORD_TYPE, memory.getAgentId()))
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    !BotFacade.RESOURCE_MANAGER
                        .hasMadeReservationOn(AUnitTypeWrapper.OVERLORD_TYPE, memory.getAgentId())
                        && !BuildLockerService.getInstance()
                        .isLocked(AUnitTypeWrapper.OVERLORD_TYPE)
                        && (dataForDecision.getFeatureValueGlobalBeliefs(CURRENT_POPULATION) >=
                        dataForDecision.getFeatureValueGlobalBeliefs(MAX_POPULATION)
                        || (
                        Decider.getDecision(AgentTypes.ECO_MANAGER, DesireKeys.INCREASE_CAPACITY,
                            dataForDecision, INCREASING_CAPACITY, memory.getCurrentClock(),
                            memory.getAgentId()) &&
                            //do not build overlord when there is gap
                            dataForDecision.getFeatureValueGlobalBeliefs(MAX_POPULATION)
                                - dataForDecision.getFeatureValueGlobalBeliefs(CURRENT_POPULATION)
                                < AUnitTypeWrapper.OVERLORD_TYPE.getSupplyProvided())))
                .globalBeliefTypesByAgentType(Stream.concat(
                    INCREASING_CAPACITY.getConvertersForFactsForGlobalBeliefsByAgentType().stream(),
                    Stream.of(CURRENT_POPULATION, MAX_POPULATION))
                    .collect(Collectors.toSet()))
                .globalBeliefSetTypesByAgentType(
                    INCREASING_CAPACITY.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .globalBeliefTypes(INCREASING_CAPACITY.getConvertersForFactsForGlobalBeliefs())
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> BuildLockerService.getInstance()
                    .isLocked(AUnitTypeWrapper.OVERLORD_TYPE)
                    || !Decider.getDecision(AgentTypes.ECO_MANAGER, DesireKeys.INCREASE_CAPACITY,
                    dataForDecision, INCREASING_CAPACITY, memory.getCurrentClock(),
                    memory.getAgentId()))
                .globalBeliefTypesByAgentType(
                    INCREASING_CAPACITY.getConvertersForFactsForGlobalBeliefsByAgentType())
                .globalBeliefSetTypesByAgentType(
                    INCREASING_CAPACITY.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .globalBeliefTypes(INCREASING_CAPACITY.getConvertersForFactsForGlobalBeliefs())
                .build())
            .desiresForOthers(Collections.singleton(INCREASE_CAPACITY))
            .build();
        type.addConfiguration(INCREASE_CAPACITY, trainOverlord, true);

        ConfigurationWithSharedDesire trainOverlordShared = createConfigurationWithSharedDesireToTrainFromTemplate(
            MORPH_TO_OVERLORD, AUnitTypeWrapper.OVERLORD_TYPE);
        type.addConfiguration(INCREASE_CAPACITY, INCREASE_CAPACITY, trainOverlordShared);
      })
      .desiresWithAbstractIntention(
          Stream.of(BUILD_WORKER, EXPAND, BUILD_EXTRACTOR, INCREASE_CAPACITY)
              .collect(Collectors.toSet()))
      .build();
}
