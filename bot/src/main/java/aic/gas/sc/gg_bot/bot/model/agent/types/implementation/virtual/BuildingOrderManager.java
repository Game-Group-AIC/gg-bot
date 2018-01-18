package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.virtual;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_EVOLUTION_CHAMBERS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_EVOLUTION_CHAMBERS_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_EXTRACTORS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_EXTRACTORS_IN_CONSTRUCTION;
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
import static aic.gas.sc.gg_bot.bot.model.agent.types.implementation.AgentTypeUtils.createConfigurationWithSharedDesireToBuildFromTemplate;
import static aic.gas.sc.gg_bot.bot.model.agent.types.implementation.AgentTypeUtils.createOwnConfigurationWithAbstractPlanToBuildFromTemplate;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import aic.gas.sc.gg_bot.bot.service.implementation.BuildLockerService;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentType;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithAbstractPlan;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithSharedDesire;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactWithSetOfOptionalValuesForAgentType;
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

  /**
   * Template to create desire initiated by desire to meet missing dependencies
   * Initialize abstract build plan - make reservation + check conditions (for building).
   * It is unlocked only when building is built
   */
  private static <T, V> ConfigurationWithAbstractPlan createConfigurationWithAbstractPlanIfBuildingIsMissingFromTemplate(
      FactWithSetOfOptionalValuesForAgentType<T> currentCount,
      FactWithSetOfOptionalValuesForAgentType<V> currentCountInConstruction,
      DesireKey desireKey, AUnitTypeWrapper unitTypeWrapper,
      Stream<DesireKey> desireKeysWithAbstractIntentionStream) {
    return ConfigurationWithAbstractPlan.builder()
        .decisionInDesire(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) -> !dataForDecision.madeDecisionToAny()
                    && !BuildLockerService.getInstance().isLocked(unitTypeWrapper)
                    && dataForDecision.getFeatureValueGlobalBeliefs(currentCount) == 0
                    && dataForDecision
                    .getFeatureValueGlobalBeliefs(currentCountInConstruction) == 0)
            .globalBeliefTypesByAgentType(Stream.of(currentCountInConstruction, currentCount)
                .collect(Collectors.toSet()))
            .desiresToConsider(Collections.singleton(desireKey))
            .build())
        .decisionInIntention(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) ->
                    BuildLockerService.getInstance().isLocked(unitTypeWrapper)
                        //building exists
                        || dataForDecision.getFeatureValueGlobalBeliefs(currentCount) > 0
                        || dataForDecision.getFeatureValueGlobalBeliefs(currentCountInConstruction)
                        > 0)
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

  public static final AgentType BUILDING_ORDER_MANAGER = AgentType.builder()
      .agentTypeID(AgentTypes.BUILDING_ORDER_MANAGER)
      .usingTypesForFacts(new HashSet<>(Collections.singletonList(BASE_TO_MOVE)))
      .initializationStrategy(type -> {

        //build pool abstract top - make reservation + check conditions (no pool). unlocked only when pool was built
        ConfigurationWithAbstractPlan buildPoolOwn = createOwnConfigurationWithAbstractPlanToBuildFromTemplate(
            COUNT_OF_POOLS, COUNT_OF_POOLS_IN_CONSTRUCTION, ENABLE_GROUND_MELEE,
            AUnitTypeWrapper.SPAWNING_POOL_TYPE, BUILDING_POOL, Stream.empty(),
            AgentTypes.BUILDING_ORDER_MANAGER);
        type.addConfiguration(ENABLE_GROUND_MELEE, buildPoolOwn, true);

        //build pool abstract common (to meet dependencies) - make reservation + check conditions (no pool). unlocked only when pool was built
        //build pool if not present
        ConfigurationWithAbstractPlan buildPoolIfNotPresent = createConfigurationWithAbstractPlanIfBuildingIsMissingFromTemplate(
            COUNT_OF_POOLS, COUNT_OF_POOLS_IN_CONSTRUCTION, ENABLE_GROUND_MELEE,
            AUnitTypeWrapper.SPAWNING_POOL_TYPE, Stream.empty());

        //share desire to build pool with system - there is enough resources
        ConfigurationWithSharedDesire buildPoolShared = createConfigurationWithSharedDesireToBuildFromTemplate(
            MORPH_TO_POOL, AUnitTypeWrapper.SPAWNING_POOL_TYPE, new FindBaseForBuilding(),
            (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE));
        type.addConfiguration(ENABLE_GROUND_MELEE, ENABLE_GROUND_MELEE, buildPoolShared);

        //abstract extractor plan
        //build extractor if not present
        ConfigurationWithAbstractPlan buildExtractorIfNotPresent = ConfigurationWithAbstractPlan
            .builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !dataForDecision.madeDecisionToAny() &&
                        !BuildLockerService.getInstance().isLocked(AUnitTypeWrapper.EXTRACTOR_TYPE)
                        && dataForDecision.getFeatureValueGlobalBeliefSets(COUNT_OF_EXTRACTORS) == 0
                        && dataForDecision
                        .getFeatureValueGlobalBeliefs(COUNT_OF_EXTRACTORS_IN_CONSTRUCTION) == 0
                )
                .globalBeliefSetTypesByAgentType(Collections.singleton(COUNT_OF_EXTRACTORS))
                .globalBeliefTypesByAgentType(
                    Collections.singleton(COUNT_OF_EXTRACTORS_IN_CONSTRUCTION))
                .desiresToConsider(new HashSet<>(Collections.singleton(BUILD_EXTRACTOR)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    BuildLockerService.getInstance().isLocked(AUnitTypeWrapper.EXTRACTOR_TYPE)
                        || dataForDecision.getFeatureValueGlobalBeliefSets(COUNT_OF_EXTRACTORS) > 0
                        || dataForDecision
                        .getFeatureValueGlobalBeliefs(COUNT_OF_EXTRACTORS_IN_CONSTRUCTION) > 0
                )
                .globalBeliefSetTypesByAgentType(Collections.singleton(COUNT_OF_EXTRACTORS))
                .globalBeliefTypesByAgentType(
                    Collections.singleton(COUNT_OF_EXTRACTORS_IN_CONSTRUCTION))
                .build())
            .reactionOnChangeStrategy(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.makeReservation(
                    AUnitTypeWrapper.EXTRACTOR_TYPE, memory.getAgentId()))
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.removeReservation(
                    AUnitTypeWrapper.EXTRACTOR_TYPE, memory.getAgentId()))
            .desiresForOthers(Collections.singleton(BUILD_EXTRACTOR))
            .build();

        //share desire to build extractor with system
        ConfigurationWithSharedDesire buildExtractor = createConfigurationWithSharedDesireToBuildFromTemplate(
            BUILD_EXTRACTOR, AUnitTypeWrapper.EXTRACTOR_TYPE, new FindBaseForBuilding(),
            (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE));
        type.addConfiguration(BUILD_EXTRACTOR, BUILD_EXTRACTOR, buildExtractor);

        //build lair abstract top - make reservation + check conditions (no lair). unlocked only when lair was built
        ConfigurationWithAbstractPlan upgradeToLairAbstract = createOwnConfigurationWithAbstractPlanToBuildFromTemplate(
            COUNT_OF_LAIRS, COUNT_OF_LAIRS_IN_CONSTRUCTION, UPGRADE_TO_LAIR,
            AUnitTypeWrapper.LAIR_TYPE, UPGRADING_TO_LAIR,
            Stream.of(ENABLE_GROUND_MELEE, BUILD_EXTRACTOR), AgentTypes.BUILDING_ORDER_MANAGER);
        type.addConfiguration(UPGRADE_TO_LAIR, upgradeToLairAbstract, true);

        //build lair abstract common (to meet dependencies) - make reservation + check conditions (no lair). unlocked only when lair was built
        //build lair if not present
        ConfigurationWithAbstractPlan upgradeToLairIfNotPresentAbstract = createConfigurationWithAbstractPlanIfBuildingIsMissingFromTemplate(
            COUNT_OF_LAIRS, COUNT_OF_LAIRS_IN_CONSTRUCTION, UPGRADE_TO_LAIR,
            AUnitTypeWrapper.LAIR_TYPE, Stream.of(ENABLE_GROUND_MELEE, BUILD_EXTRACTOR));

        //share desire to upgrade hatchery into lair with system - there is enough resources
        ConfigurationWithSharedDesire upgradeToLair = createConfigurationWithSharedDesireToBuildFromTemplate(
            UPGRADE_TO_LAIR, AUnitTypeWrapper.LAIR_TYPE, new FindBaseForBuilding(),
            (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE));
        type.addConfiguration(UPGRADE_TO_LAIR, UPGRADE_TO_LAIR, upgradeToLair);

        //tell system to build pool if needed
        type.addConfiguration(ENABLE_GROUND_MELEE, UPGRADE_TO_LAIR, buildPoolIfNotPresent);

        //tell system to build extractor if it is missing
        type.addConfiguration(BUILD_EXTRACTOR, UPGRADE_TO_LAIR, buildExtractorIfNotPresent);

        //hydralisk den as abstract plan. needs to meet dependencies - pool and at least one extractor
        ConfigurationWithAbstractPlan buildHydraliskDenAbstract = createOwnConfigurationWithAbstractPlanToBuildFromTemplate(
            COUNT_OF_HYDRALISK_DENS_IN_CONSTRUCTION, COUNT_OF_HYDRALISK_DENS, ENABLE_GROUND_RANGED,
            AUnitTypeWrapper.HYDRALISK_DEN_TYPE, BUILDING_HYDRALISK_DEN,
            Stream.of(ENABLE_GROUND_MELEE, BUILD_EXTRACTOR), AgentTypes.BUILDING_ORDER_MANAGER);
        type.addConfiguration(ENABLE_GROUND_RANGED, buildHydraliskDenAbstract, true);

        //share desire to build hydralisk den with system - there is enough resources
        ConfigurationWithSharedDesire buildHydraliskDen = createConfigurationWithSharedDesireToBuildFromTemplate(
            MORPH_TO_HYDRALISK_DEN, AUnitTypeWrapper.HYDRALISK_DEN_TYPE, new FindBaseForBuilding(),
            (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE));
        type.addConfiguration(ENABLE_GROUND_RANGED, ENABLE_GROUND_RANGED, buildHydraliskDen);

        //tell system to build pool if needed
        type.addConfiguration(BUILD_EXTRACTOR, ENABLE_GROUND_RANGED, buildExtractorIfNotPresent);

        //tell system to build extractor if it is missing
        type.addConfiguration(ENABLE_GROUND_MELEE, ENABLE_GROUND_RANGED, buildPoolIfNotPresent);

        //spire as abstract plan. needs to meet dependencies - lair and at least one extractor
        ConfigurationWithAbstractPlan buildSpireAbstract = createOwnConfigurationWithAbstractPlanToBuildFromTemplate(
            COUNT_OF_SPIRES, COUNT_OF_SPIRES_IN_CONSTRUCTION, ENABLE_AIR,
            AUnitTypeWrapper.SPIRE_TYPE, BUILDING_SPIRE,
            Stream.of(UPGRADE_TO_LAIR, BUILD_EXTRACTOR), AgentTypes.BUILDING_ORDER_MANAGER);
        type.addConfiguration(ENABLE_AIR, buildSpireAbstract, true);

        //share desire to build spire with system - there is enough resources
        ConfigurationWithSharedDesire buildSpire = createConfigurationWithSharedDesireToBuildFromTemplate(
            MORPH_TO_SPIRE, AUnitTypeWrapper.SPIRE_TYPE, new FindBaseForBuilding(),
            (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE));
        type.addConfiguration(ENABLE_AIR, ENABLE_AIR, buildSpire);

        //tell system to build extractor if missing
        type.addConfiguration(BUILD_EXTRACTOR, ENABLE_AIR, buildExtractorIfNotPresent);

        //tell system to upgrade to lair if missing
        type.addConfiguration(UPGRADE_TO_LAIR, ENABLE_AIR, upgradeToLairIfNotPresentAbstract);

        //evolution chamber as abstract plan
        ConfigurationWithAbstractPlan buildEvolutionChamberAbstract = createOwnConfigurationWithAbstractPlanToBuildFromTemplate(
            COUNT_OF_EVOLUTION_CHAMBERS, COUNT_OF_EVOLUTION_CHAMBERS_IN_CONSTRUCTION,
            ENABLE_STATIC_ANTI_AIR, AUnitTypeWrapper.EVOLUTION_CHAMBER_TYPE,
            BUILDING_EVOLUTION_CHAMBER, Stream.empty(), AgentTypes.BUILDING_ORDER_MANAGER);
        type.addConfiguration(ENABLE_STATIC_ANTI_AIR, buildEvolutionChamberAbstract, true);

        //build evolution chamber
        ConfigurationWithSharedDesire buildEvolutionChamber = createConfigurationWithSharedDesireToBuildFromTemplate(
            MORPH_TO_EVOLUTION_CHAMBER, AUnitTypeWrapper.EVOLUTION_CHAMBER_TYPE,
            new FindBaseForBuilding(),
            (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE));
        type.addConfiguration(ENABLE_STATIC_ANTI_AIR, ENABLE_STATIC_ANTI_AIR,
            buildEvolutionChamber);

        //TODO other managers requirements?
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
