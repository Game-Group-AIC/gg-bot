package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.virtual;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_EVOLUTION_CHAMBERS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_EVOLUTION_CHAMBERS_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_EXTRACTORS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_HYDRALISK_DENS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_HYDRALISK_DENS_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_LAIRS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_LAIRS_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_POOLS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_POOLS_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_SPIRES;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_SPIRES_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_MOVE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.HAS_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_EVOLUTION_CHAMBER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_HYDRALISK_DEN;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_POOL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_SPIRE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.UPGRADING_TO_LAIR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.BUILD_EXTRACTOR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.ENABLE_AIR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.ENABLE_GROUND_MELEE;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.ENABLE_GROUND_RANGED;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.ENABLE_STATIC_ANTI_AIR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_EVOLUTION_CHAMBER;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_HYDRALISK_DEN;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_POOL;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.MORPH_TO_SPIRE;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.UPGRADE_TO_LAIR;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.bot.model.Decider;
import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import aic.gas.sc.gg_bot.bot.service.implementation.BuildLockerService;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentType;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithAbstractPlan;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithSharedDesire;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.ReactionOnChangeStrategy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BuildingOrderManager {

  //TODO make other templates...

  /**
   * Template for finding base for building
   */
  private static class FindBaseForBuilding implements ReactionOnChangeStrategy {

    @Override
    public void updateBeliefs(WorkingMemory memory, DesireParameters desireParameters) {
      FIND_BASE_FOR_BUILDING.updateBeliefs(memory, desireParameters);
    }

    //find base to build building
    private static final ReactionOnChangeStrategy FIND_BASE_FOR_BUILDING = (memory, desireParameters) -> {
      List<ABaseLocationWrapper> ourBases = memory
          .getReadOnlyMemoriesForAgentType(AgentTypes.BASE_LOCATION)
          .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_BASE).get())
          .filter(readOnlyMemory -> readOnlyMemory.returnFactSetValueForGivenKey(HAS_BASE).get()
              .anyMatch(aUnitOfPlayer -> !aUnitOfPlayer.isMorphing() && !aUnitOfPlayer
                  .isBeingConstructed()))
          .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_BASE_LOCATION).get())
          .collect(Collectors.toList());
      if (!ourBases.isEmpty()) {
        //prefer base on start location
        memory.updateFact(BASE_TO_MOVE, ourBases.stream()
            .filter(ABaseLocationWrapper::isStartLocation)
            .findAny().orElse(ourBases.get(0)));
      } else {
        memory.eraseFactValueForGivenKey(BASE_TO_MOVE);
      }
    };
  }

  public static final AgentType BUILDING_ORDER_MANAGER = AgentType.builder()
      .agentTypeID(AgentTypes.BUILDING_ORDER_MANAGER)
      .usingTypesForFacts(new HashSet<>(Collections.singletonList(BASE_TO_MOVE)))
      .initializationStrategy(type -> {

        //build pool abstract top - make reservation + check conditions (no pool). unlocked only when pool was built
        ConfigurationWithAbstractPlan buildPoolOwn = ConfigurationWithAbstractPlan.builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !dataForDecision.madeDecisionToAny()
                        && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_POOLS) == 0
                        && dataForDecision
                        .getFeatureValueGlobalBeliefs(COUNT_OF_POOLS_IN_CONSTRUCTION) == 0
                        //learnt decision
                        && (Decider.getDecision(AgentTypes.BUILDING_ORDER_MANAGER,
                        DesireKeys.ENABLE_GROUND_MELEE, dataForDecision, BUILDING_POOL,
                        memory.getCurrentClock())))
                .globalBeliefTypesByAgentType(Stream.concat(
                    BUILDING_POOL.getConvertersForFactsForGlobalBeliefsByAgentType().stream(),
                    Stream.of(COUNT_OF_POOLS_IN_CONSTRUCTION, COUNT_OF_POOLS))
                    .collect(Collectors.toSet()))
                .globalBeliefSetTypesByAgentType(
                    BUILDING_POOL.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .globalBeliefTypes(BUILDING_POOL.getConvertersForFactsForGlobalBeliefs())
                .desiresToConsider(Collections.singleton(ENABLE_GROUND_MELEE))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        //pool exists
                        dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_POOLS) > 0
                            || dataForDecision
                            .getFeatureValueGlobalBeliefs(COUNT_OF_POOLS_IN_CONSTRUCTION) > 0)
                .globalBeliefTypesByAgentType(
                    Stream.of(COUNT_OF_POOLS_IN_CONSTRUCTION, COUNT_OF_POOLS)
                        .collect(Collectors.toSet()))
                .build())
            .reactionOnChangeStrategy(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.makeReservation(
                    AUnitTypeWrapper.SPAWNING_POOL_TYPE, memory.getAgentId()))
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.removeReservation(
                    AUnitTypeWrapper.SPAWNING_POOL_TYPE, memory.getAgentId()))
            .desiresForOthers(Collections.singleton(ENABLE_GROUND_MELEE))
            .build();
        type.addConfiguration(ENABLE_GROUND_MELEE, buildPoolOwn, true);

        //build pool abstract common (to meet dependencies) - make reservation + check conditions (no pool). unlocked only when pool was built
        //build pool if not present
        ConfigurationWithAbstractPlan buildPoolIfNotPresent = ConfigurationWithAbstractPlan
            .builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !dataForDecision.madeDecisionToAny()
                        && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_POOLS) == 0 &&
                        dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_POOLS_IN_CONSTRUCTION)
                            == 0)
                .globalBeliefTypesByAgentType(
                    Stream.of(COUNT_OF_POOLS_IN_CONSTRUCTION, COUNT_OF_POOLS)
                        .collect(Collectors.toSet()))
                .desiresToConsider(Collections.singleton(ENABLE_GROUND_MELEE))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        //pool exists
                        dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_POOLS) > 0
                            || dataForDecision
                            .getFeatureValueGlobalBeliefs(COUNT_OF_POOLS_IN_CONSTRUCTION) > 0)
                .globalBeliefTypesByAgentType(
                    Stream.of(COUNT_OF_POOLS_IN_CONSTRUCTION, COUNT_OF_POOLS)
                        .collect(Collectors.toSet()))
                .build())
            .reactionOnChangeStrategy(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.makeReservation(
                    AUnitTypeWrapper.SPAWNING_POOL_TYPE, memory.getAgentId()))
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.removeReservation(
                    AUnitTypeWrapper.SPAWNING_POOL_TYPE, memory.getAgentId()))
            .desiresForOthers(Collections.singleton(ENABLE_GROUND_MELEE))
            .build();
        type.addConfiguration(ENABLE_GROUND_MELEE, buildPoolIfNotPresent, true);

        //share desire to build pool with system - there is enough resources
        ConfigurationWithSharedDesire buildPoolShared = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(MORPH_TO_POOL)
            .counts(1)
            .reactionOnChangeStrategy(new FindBaseForBuilding())
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE))
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        //it is not locked
                        !BuildLockerService.getInstance()
                            .isLocked(AUnitTypeWrapper.SPAWNING_POOL_TYPE)
                            //resources are available
                            && BotFacade.RESOURCE_MANAGER
                            .canSpendResourcesOn(AUnitTypeWrapper.SPAWNING_POOL_TYPE,
                                memory.getAgentId()))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        !memory.returnFactValueForGivenKey(BASE_TO_MOVE).isPresent()
                            //pool has been locked
                            || BuildLockerService.getInstance()
                            .isLocked(AUnitTypeWrapper.SPAWNING_POOL_TYPE)
                            //we do not have enough resources
                            || !BotFacade.RESOURCE_MANAGER
                            .canSpendResourcesOn(AUnitTypeWrapper.SPAWNING_POOL_TYPE,
                                memory.getAgentId()))
                .build())
            .build();
        type.addConfiguration(ENABLE_GROUND_MELEE, ENABLE_GROUND_MELEE, buildPoolShared);

        //TODO probably need to check extractor in construction as well
        //abstract extractor plan
        //build extractor if not present
        ConfigurationWithAbstractPlan buildExtractorIfNotPresent = ConfigurationWithAbstractPlan
            .builder()
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
            .reactionOnChangeStrategy(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.makeReservation(
                    AUnitTypeWrapper.EXTRACTOR_TYPE, memory.getAgentId()))
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.removeReservation(
                    AUnitTypeWrapper.EXTRACTOR_TYPE, memory.getAgentId()))
            .desiresForOthers(Collections.singleton(BUILD_EXTRACTOR))
            .build();
        type.addConfiguration(BUILD_EXTRACTOR, BUILD_EXTRACTOR, buildExtractorIfNotPresent);

        //share desire to build extractor with system
        ConfigurationWithSharedDesire buildExtractor = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(BUILD_EXTRACTOR)
            .counts(1)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        //it is not locked
                        !BuildLockerService.getInstance()
                            .isLocked(AUnitTypeWrapper.EXTRACTOR_TYPE)
                            //resources are available
                            && BotFacade.RESOURCE_MANAGER
                            .canSpendResourcesOn(AUnitTypeWrapper.EXTRACTOR_TYPE,
                                memory.getAgentId()))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        //pool has been locked
                        BuildLockerService.getInstance()
                            .isLocked(AUnitTypeWrapper.EXTRACTOR_TYPE)
                            //we do not have enough resources
                            || !BotFacade.RESOURCE_MANAGER
                            .canSpendResourcesOn(AUnitTypeWrapper.EXTRACTOR_TYPE,
                                memory.getAgentId()))
                .build())
            .build();
        type.addConfiguration(BUILD_EXTRACTOR, BUILD_EXTRACTOR, buildExtractor);

        //build lair abstract top - make reservation + check conditions (no lair). unlocked only when lair was built
        ConfigurationWithAbstractPlan upgradeToLairAbstract = ConfigurationWithAbstractPlan
            .builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !dataForDecision.madeDecisionToAny()
                        && Decider.getDecision(AgentTypes.BUILDING_ORDER_MANAGER,
                        DesireKeys.UPGRADE_TO_LAIR, dataForDecision, UPGRADING_TO_LAIR,
                        memory.getCurrentClock())
                        //there are no lairs
                        && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_LAIRS) == 0
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_LAIRS_IN_CONSTRUCTION) == 0)
                .globalBeliefTypesByAgentType(Stream.concat(
                    UPGRADING_TO_LAIR.getConvertersForFactsForGlobalBeliefsByAgentType().stream(),
                    Stream.of(COUNT_OF_LAIRS, COUNT_OF_LAIRS_IN_CONSTRUCTION)).collect(
                    Collectors.toSet()))
                .globalBeliefSetTypesByAgentType(
                    UPGRADING_TO_LAIR.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .globalBeliefTypes(UPGRADING_TO_LAIR.getConvertersForFactsForGlobalBeliefs())
                .desiresToConsider(Collections.singleton(UPGRADE_TO_LAIR))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        //liar exists
                        dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_LAIRS) > 0 ||
                            dataForDecision
                                .getFeatureValueGlobalBeliefs(COUNT_OF_LAIRS_IN_CONSTRUCTION) > 0)
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Arrays.asList(COUNT_OF_LAIRS, COUNT_OF_LAIRS_IN_CONSTRUCTION)))
                .build())
            .desiresWithAbstractIntention(Stream.of(ENABLE_GROUND_MELEE, BUILD_EXTRACTOR)
                .collect(Collectors.toSet()))
            .desiresForOthers(Collections.singleton(UPGRADE_TO_LAIR))
            .reactionOnChangeStrategy(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.makeReservation(
                    AUnitTypeWrapper.LAIR_TYPE, memory.getAgentId()))
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.removeReservation(
                    AUnitTypeWrapper.LAIR_TYPE, memory.getAgentId()))
            .build();
        type.addConfiguration(UPGRADE_TO_LAIR, upgradeToLairAbstract, true);

        //build lair abstract common (to meet dependencies) - make reservation + check conditions (no lair). unlocked only when lair was built
        //build lair if not present
        ConfigurationWithAbstractPlan upgradeToLairIfNotPresentAbstract = ConfigurationWithAbstractPlan
            .builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !dataForDecision.madeDecisionToAny()
                        //there are no lairs
                        && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_LAIRS) == 0
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_LAIRS_IN_CONSTRUCTION) == 0)
                .globalBeliefTypesByAgentType(
                    Stream.of(COUNT_OF_LAIRS, COUNT_OF_LAIRS_IN_CONSTRUCTION)
                        .collect(Collectors.toSet()))
                .desiresToConsider(Collections.singleton(UPGRADE_TO_LAIR))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        //liar exists
                        dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_LAIRS) > 0 ||
                            dataForDecision
                                .getFeatureValueGlobalBeliefs(COUNT_OF_LAIRS_IN_CONSTRUCTION) > 0)
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Arrays.asList(COUNT_OF_LAIRS, COUNT_OF_LAIRS_IN_CONSTRUCTION)))
                .build())
            .desiresWithAbstractIntention(Stream.of(ENABLE_GROUND_MELEE, BUILD_EXTRACTOR)
                .collect(Collectors.toSet()))
            .desiresForOthers(Collections.singleton(UPGRADE_TO_LAIR))
            .reactionOnChangeStrategy(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.makeReservation(
                    AUnitTypeWrapper.LAIR_TYPE, memory.getAgentId()))
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.removeReservation(
                    AUnitTypeWrapper.LAIR_TYPE, memory.getAgentId()))
            .build();
        type.addConfiguration(UPGRADE_TO_LAIR, UPGRADE_TO_LAIR, upgradeToLairIfNotPresentAbstract);

        //share desire to upgrade hatchery into lair with system - there is enough resources
        ConfigurationWithSharedDesire upgradeToLair = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(UPGRADE_TO_LAIR)
            .counts(1)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    //it is not locked
                    !BuildLockerService.getInstance()
                        .isLocked(AUnitTypeWrapper.LAIR_TYPE)
                        //resources are available
                        && BotFacade.RESOURCE_MANAGER
                        .canSpendResourcesOn(AUnitTypeWrapper.LAIR_TYPE,
                            memory.getAgentId()))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    //pool has been locked
                    BuildLockerService.getInstance()
                        .isLocked(AUnitTypeWrapper.LAIR_TYPE)
                        //we do not have enough resources
                        || !BotFacade.RESOURCE_MANAGER
                        .canSpendResourcesOn(AUnitTypeWrapper.LAIR_TYPE,
                            memory.getAgentId()))
                .build())
            .build();
        type.addConfiguration(UPGRADE_TO_LAIR, UPGRADE_TO_LAIR, upgradeToLair);

        //tell system to build pool if needed
        type.addConfiguration(ENABLE_GROUND_MELEE, UPGRADE_TO_LAIR, buildPoolIfNotPresent);

        //tell system to build extractor if it is missing
        type.addConfiguration(BUILD_EXTRACTOR, UPGRADE_TO_LAIR, buildExtractorIfNotPresent);

        //hydralisk den as abstract plan. needs to meet dependencies - pool and at least one extractor
        ConfigurationWithAbstractPlan buildHydraliskDenAbstract = ConfigurationWithAbstractPlan
            .builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !dataForDecision.madeDecisionToAny()
                        && Decider.getDecision(AgentTypes.BUILDING_ORDER_MANAGER,
                        DesireKeys.ENABLE_GROUND_RANGED, dataForDecision,
                        BUILDING_HYDRALISK_DEN, memory.getCurrentClock())
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
                .globalBeliefTypes(BUILDING_HYDRALISK_DEN.getConvertersForFactsForGlobalBeliefs())
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
            .desiresWithAbstractIntention(Stream.of(ENABLE_GROUND_MELEE, BUILD_EXTRACTOR)
                .collect(Collectors.toSet()))
            .desiresForOthers(Collections.singleton(ENABLE_GROUND_RANGED))
            .reactionOnChangeStrategy(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.makeReservation(
                    AUnitTypeWrapper.HYDRALISK_DEN_TYPE, memory.getAgentId()))
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.removeReservation(
                    AUnitTypeWrapper.HYDRALISK_DEN_TYPE, memory.getAgentId()))
            .build();
        type.addConfiguration(ENABLE_GROUND_RANGED, buildHydraliskDenAbstract, true);

        //hydralisk den abstract common (to meet dependencies) - make reservation + check conditions (no lair). unlocked only when hydralisk den was built
        //build hydralisk den if not present
        ConfigurationWithAbstractPlan buildHydraliskDenIfMissingAbstract = ConfigurationWithAbstractPlan
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
                        COUNT_OF_HYDRALISK_DENS) > 0 || dataForDecision
                        .getFeatureValueGlobalBeliefs(COUNT_OF_HYDRALISK_DENS_IN_CONSTRUCTION) > 0)
                .globalBeliefTypesByAgentType(new HashSet<>(Arrays.asList(COUNT_OF_HYDRALISK_DENS,
                    COUNT_OF_HYDRALISK_DENS_IN_CONSTRUCTION)))
                .build())
            .reactionOnChangeStrategy(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.makeReservation(
                    AUnitTypeWrapper.HYDRALISK_DEN_TYPE, memory.getAgentId()))
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.removeReservation(
                    AUnitTypeWrapper.HYDRALISK_DEN_TYPE, memory.getAgentId()))
            .desiresWithAbstractIntention(Stream.of(ENABLE_GROUND_MELEE, BUILD_EXTRACTOR)
                .collect(Collectors.toSet()))
            .desiresForOthers(Collections.singleton(ENABLE_GROUND_RANGED))
            .build();

        //share desire to build hydralisk den with system - there is enough resources
        ConfigurationWithSharedDesire buildHydraliskDen = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(MORPH_TO_HYDRALISK_DEN)
            .reactionOnChangeStrategy(new FindBaseForBuilding())
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE))
            .counts(1)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        //it is not locked
                        !BuildLockerService.getInstance()
                            .isLocked(AUnitTypeWrapper.HYDRALISK_DEN_TYPE)
                            //resources are available
                            && BotFacade.RESOURCE_MANAGER
                            .canSpendResourcesOn(AUnitTypeWrapper.HYDRALISK_DEN_TYPE,
                                memory.getAgentId()))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        !memory.returnFactValueForGivenKey(BASE_TO_MOVE).isPresent()
                            //pool has been locked
                            || BuildLockerService.getInstance()
                            .isLocked(AUnitTypeWrapper.HYDRALISK_DEN_TYPE)
                            //we do not have enough resources
                            || !BotFacade.RESOURCE_MANAGER
                            .canSpendResourcesOn(AUnitTypeWrapper.HYDRALISK_DEN_TYPE,
                                memory.getAgentId()))
                .build())
            .build();
        type.addConfiguration(ENABLE_GROUND_RANGED, ENABLE_GROUND_RANGED, buildHydraliskDen);

        //tell system to build pool if needed
        type.addConfiguration(BUILD_EXTRACTOR, ENABLE_GROUND_RANGED, buildExtractorIfNotPresent);

        //tell system to build extractor if it is missing
        type.addConfiguration(ENABLE_GROUND_MELEE, ENABLE_GROUND_RANGED, buildPoolIfNotPresent);

        //spire as abstract plan. needs to meet dependencies - lair and at least one extractor
        ConfigurationWithAbstractPlan buildSpireAbstract = ConfigurationWithAbstractPlan.builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !dataForDecision.madeDecisionToAny()
                        && Decider.getDecision(AgentTypes.BUILDING_ORDER_MANAGER,
                        DesireKeys.ENABLE_AIR, dataForDecision, BUILDING_SPIRE,
                        memory.getCurrentClock())
                        && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_SPIRES) == 0
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_SPIRES_IN_CONSTRUCTION) == 0)
                .globalBeliefTypesByAgentType(Stream.concat(
                    BUILDING_SPIRE.getConvertersForFactsForGlobalBeliefsByAgentType().stream(),
                    Stream.of(COUNT_OF_SPIRES, COUNT_OF_SPIRES_IN_CONSTRUCTION)).collect(
                    Collectors.toSet()))
                .globalBeliefSetTypesByAgentType(
                    BUILDING_SPIRE.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .globalBeliefTypes(BUILDING_SPIRE.getConvertersForFactsForGlobalBeliefs())
                .desiresToConsider(Collections.singleton(ENABLE_AIR))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        //spire exists
                        dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_SPIRES) > 0
                            || dataForDecision
                            .getFeatureValueGlobalBeliefs(COUNT_OF_SPIRES_IN_CONSTRUCTION) > 0)
                .globalBeliefTypesByAgentType(
                    Stream.of(COUNT_OF_SPIRES, COUNT_OF_SPIRES_IN_CONSTRUCTION)
                        .collect(Collectors.toSet()))
                .build())
            .desiresForOthers(Collections.singleton(ENABLE_AIR))
            .desiresWithAbstractIntention(Stream.of(UPGRADE_TO_LAIR, BUILD_EXTRACTOR)
                .collect(Collectors.toSet()))
            .reactionOnChangeStrategy(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.makeReservation(
                    AUnitTypeWrapper.SPIRE_TYPE, memory.getAgentId()))
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.removeReservation(
                    AUnitTypeWrapper.SPIRE_TYPE, memory.getAgentId()))
            .build();
        type.addConfiguration(ENABLE_AIR, buildSpireAbstract, true);

        //spire abstract common (to meet dependencies) - make reservation + check conditions. unlocked only when spire was built
        //build spire if not present
        ConfigurationWithAbstractPlan buildSpireIfMissingAbstract = ConfigurationWithAbstractPlan
            .builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !dataForDecision.madeDecisionToAny()
                        && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_SPIRES) == 0
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_SPIRES_IN_CONSTRUCTION) == 0)
                .globalBeliefTypesByAgentType(
                    Stream.of(COUNT_OF_SPIRES, COUNT_OF_SPIRES_IN_CONSTRUCTION)
                        .collect(Collectors.toSet()))
                .desiresToConsider(Collections.singleton(ENABLE_AIR))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        //spire exists
                        dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_SPIRES) > 0
                            || dataForDecision
                            .getFeatureValueGlobalBeliefs(COUNT_OF_SPIRES_IN_CONSTRUCTION) > 0)
                .globalBeliefTypesByAgentType(
                    Stream.of(COUNT_OF_SPIRES, COUNT_OF_SPIRES_IN_CONSTRUCTION)
                        .collect(Collectors.toSet()))
                .build())
            .desiresForOthers(Collections.singleton(ENABLE_AIR))
            .desiresWithAbstractIntention(Stream.of(UPGRADE_TO_LAIR, BUILD_EXTRACTOR)
                .collect(Collectors.toSet()))
            .reactionOnChangeStrategy(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.makeReservation(
                    AUnitTypeWrapper.SPIRE_TYPE, memory.getAgentId()))
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.removeReservation(
                    AUnitTypeWrapper.SPIRE_TYPE, memory.getAgentId()))
            .build();

        //share desire to build spire with system - there is enough resources
        ConfigurationWithSharedDesire buildSpire = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(MORPH_TO_SPIRE)
            .reactionOnChangeStrategy(new FindBaseForBuilding())
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE))
            .counts(1)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        //it is not locked
                        !BuildLockerService.getInstance()
                            .isLocked(AUnitTypeWrapper.SPIRE_TYPE)
                            //resources are available
                            && BotFacade.RESOURCE_MANAGER
                            .canSpendResourcesOn(AUnitTypeWrapper.SPIRE_TYPE, memory.getAgentId()))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        !memory.returnFactValueForGivenKey(BASE_TO_MOVE).isPresent()
                            //pool has been locked
                            || BuildLockerService.getInstance()
                            .isLocked(AUnitTypeWrapper.SPIRE_TYPE)
                            //we do not have enough resources
                            || !BotFacade.RESOURCE_MANAGER
                            .canSpendResourcesOn(AUnitTypeWrapper.SPIRE_TYPE, memory.getAgentId()))
                .build())
            .build();
        type.addConfiguration(ENABLE_AIR, ENABLE_AIR, buildSpire);

        //tell system to build extractor if missing
        type.addConfiguration(BUILD_EXTRACTOR, ENABLE_AIR, buildExtractorIfNotPresent);

        //tell system to upgrade to lair if missing
        type.addConfiguration(UPGRADE_TO_LAIR, ENABLE_AIR, upgradeToLairIfNotPresentAbstract);

        //evolution chamber as abstract plan
        ConfigurationWithAbstractPlan buildEvolutionChamberAbstract = ConfigurationWithAbstractPlan
            .builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !dataForDecision.madeDecisionToAny() &&
                        dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_EVOLUTION_CHAMBERS)
                            == 0 && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_EVOLUTION_CHAMBERS_IN_CONSTRUCTION) == 0
                        && Decider.getDecision(AgentTypes.BUILDING_ORDER_MANAGER,
                        DesireKeys.ENABLE_STATIC_ANTI_AIR, dataForDecision,
                        BUILDING_EVOLUTION_CHAMBER, memory.getCurrentClock()))
                .globalBeliefTypesByAgentType(Stream.concat(
                    BUILDING_EVOLUTION_CHAMBER.getConvertersForFactsForGlobalBeliefsByAgentType()
                        .stream(), Stream.of(COUNT_OF_EVOLUTION_CHAMBERS,
                        COUNT_OF_EVOLUTION_CHAMBERS_IN_CONSTRUCTION)).collect(Collectors.toSet()))
                .globalBeliefSetTypesByAgentType(
                    BUILDING_EVOLUTION_CHAMBER
                        .getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .globalBeliefTypes(
                    BUILDING_EVOLUTION_CHAMBER.getConvertersForFactsForGlobalBeliefs())
                .desiresToConsider(Collections.singleton(ENABLE_STATIC_ANTI_AIR))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        //es exists
                        dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_EVOLUTION_CHAMBERS)
                            > 0 || dataForDecision.getFeatureValueGlobalBeliefs(
                            COUNT_OF_EVOLUTION_CHAMBERS_IN_CONSTRUCTION) > 0)
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Arrays.asList(COUNT_OF_EVOLUTION_CHAMBERS,
                        COUNT_OF_EVOLUTION_CHAMBERS_IN_CONSTRUCTION)))
                .build())
            .desiresForOthers(Collections.singleton(ENABLE_STATIC_ANTI_AIR))
            .reactionOnChangeStrategy(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.makeReservation(
                    AUnitTypeWrapper.EVOLUTION_CHAMBER_TYPE, memory.getAgentId()))
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.removeReservation(
                    AUnitTypeWrapper.EVOLUTION_CHAMBER_TYPE, memory.getAgentId()))
            .build();
        type.addConfiguration(ENABLE_STATIC_ANTI_AIR, buildEvolutionChamberAbstract, true);

        //TODO if missing es

        //build evolution chamber
        ConfigurationWithSharedDesire buildEvolutionChamber = ConfigurationWithSharedDesire
            .builder()
            .counts(1)
            .reactionOnChangeStrategy(new FindBaseForBuilding())
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE))
            .sharedDesireKey(MORPH_TO_EVOLUTION_CHAMBER)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        //it is not locked
                        !BuildLockerService.getInstance()
                            .isLocked(AUnitTypeWrapper.SPIRE_TYPE)
                            //resources are available
                            && BotFacade.RESOURCE_MANAGER
                            .canSpendResourcesOn(AUnitTypeWrapper.EVOLUTION_CHAMBER_TYPE,
                                memory.getAgentId()))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        !memory.returnFactValueForGivenKey(BASE_TO_MOVE).isPresent()
                            //pool has been locked
                            || BuildLockerService.getInstance()
                            .isLocked(AUnitTypeWrapper.SPIRE_TYPE)
                            //we do not have enough resources
                            || !BotFacade.RESOURCE_MANAGER
                            .canSpendResourcesOn(AUnitTypeWrapper.EVOLUTION_CHAMBER_TYPE,
                                memory.getAgentId()))
                .build())
            .build();
        type.addConfiguration(ENABLE_STATIC_ANTI_AIR, ENABLE_STATIC_ANTI_AIR,
            buildEvolutionChamber);

        //TODO
        //other managers requirements
        //for melee
        //for sunken
        //for ranged
        //for air
      })
      .desiresWithAbstractIntention(new HashSet<>(Arrays.asList(UPGRADE_TO_LAIR,
          ENABLE_GROUND_RANGED, ENABLE_AIR, ENABLE_GROUND_MELEE, ENABLE_STATIC_ANTI_AIR)))
      .build();

}
