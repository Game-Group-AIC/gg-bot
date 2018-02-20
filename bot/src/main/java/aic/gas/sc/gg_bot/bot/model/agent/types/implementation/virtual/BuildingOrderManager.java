package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.virtual;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.*;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_MOVE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.HAS_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_OUR_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LOCATION;
import static aic.gas.sc.gg_bot.bot.model.agent.types.implementation.AgentTypeUtils.createConfigurationWithSharedDesireToBuildFromTemplate;
import static aic.gas.sc.gg_bot.bot.model.agent.types.implementation.AgentTypeUtils.createOwnConfigurationWithAbstractPlanToBuildFromTemplate;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
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

  public static final AgentType BUILDING_ORDER_MANAGER = AgentType.builder()
      .agentTypeID(AgentTypes.BUILDING_ORDER_MANAGER.getId())
      .usingTypesForFacts(Stream.of(BASE_TO_MOVE, LOCATION)
          .collect(Collectors.toSet()))
      .initializationStrategy(type -> {

        //build pool abstract top - make reservation + check conditions (no pool). unlocked only when pool was built
        ConfigurationWithAbstractPlan buildPoolOwn = createOwnConfigurationWithAbstractPlanToBuildFromTemplate(
            COUNT_OF_POOLS, DesiresKeys.ENABLE_GROUND_MELEE, AUnitTypeWrapper.SPAWNING_POOL_TYPE,
            FeatureContainerHeaders.ENABLE_GROUND_MELEE,
            Stream.empty(), AgentTypes.BUILDING_ORDER_MANAGER);
        type.addConfiguration(DesiresKeys.ENABLE_GROUND_MELEE, buildPoolOwn, true);

        //build pool abstract common (to meet dependencies) - make reservation + check conditions (no pool). unlocked only when pool was built
        //build pool if not present
        ConfigurationWithAbstractPlan buildPoolIfNotPresent = createConfigurationWithAbstractPlanIfBuildingIsMissingFromTemplate(
            COUNT_OF_POOLS, DesiresKeys.ENABLE_GROUND_MELEE, AUnitTypeWrapper.SPAWNING_POOL_TYPE,
            Stream.empty());

        //share desire to build pool with system - there is enough resources
        ConfigurationWithSharedDesire buildPoolShared = createConfigurationWithSharedDesireToBuildFromTemplate(
            DesiresKeys.MORPH_TO_POOL, AUnitTypeWrapper.SPAWNING_POOL_TYPE,
            new FindBaseForBuilding(),
            (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE));
        type.addConfiguration(DesiresKeys.ENABLE_GROUND_MELEE, DesiresKeys.ENABLE_GROUND_MELEE,
            buildPoolShared);

        //abstract extractor plan
        //build extractor if not present
        ConfigurationWithAbstractPlan buildExtractorIfNotPresent = ConfigurationWithAbstractPlan
            .builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !dataForDecision.madeDecisionToAny() &&
                        !BotFacade.RESOURCE_MANAGER
                            .hasMadeReservationOn(AUnitTypeWrapper.EXTRACTOR_TYPE,
                                memory.getAgentId())
                        && !BuildLockerService.getInstance()
                        .isLocked(AUnitTypeWrapper.EXTRACTOR_TYPE)
                        && dataForDecision.getFeatureValueGlobalBeliefSets(COUNT_OF_EXTRACTORS)
                        == 0)
                .globalBeliefSetTypesByAgentType(Collections.singleton(COUNT_OF_EXTRACTORS))
                .desiresToConsider(Collections.singleton(DesiresKeys.BUILD_EXTRACTOR))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    BuildLockerService.getInstance().isLocked(AUnitTypeWrapper.EXTRACTOR_TYPE)
                        || dataForDecision.getFeatureValueGlobalBeliefSets(COUNT_OF_EXTRACTORS) > 0)
                .globalBeliefSetTypesByAgentType(Collections.singleton(COUNT_OF_EXTRACTORS))
                .build())
            .reactionOnChangeStrategy(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.makeReservation(
                    AUnitTypeWrapper.EXTRACTOR_TYPE, memory.getAgentId()))
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> BotFacade.RESOURCE_MANAGER.removeReservation(
                    AUnitTypeWrapper.EXTRACTOR_TYPE, memory.getAgentId()))
            .desiresForOthers(Collections.singleton(DesiresKeys.BUILD_EXTRACTOR))
            .build();

        //share desire to build extractor with system
        ConfigurationWithSharedDesire buildExtractor = createConfigurationWithSharedDesireToBuildFromTemplate(
            DesiresKeys.MORPH_TO_EXTRACTOR, AUnitTypeWrapper.EXTRACTOR_TYPE,
            new FindBaseForBuilding(),
            (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE));
        type.addConfiguration(DesiresKeys.BUILD_EXTRACTOR, DesiresKeys.BUILD_EXTRACTOR,
            buildExtractor);

        //build lair abstract top - make reservation + check conditions (no lair). unlocked only when lair was built
        ConfigurationWithAbstractPlan upgradeToLairAbstract = createOwnConfigurationWithAbstractPlanToBuildFromTemplate(
            COUNT_OF_LAIRS, DesiresKeys.UPGRADE_TO_LAIR, AUnitTypeWrapper.LAIR_TYPE,
            FeatureContainerHeaders.UPGRADE_TO_LAIR,
            Stream.of(DesiresKeys.ENABLE_GROUND_MELEE, DesiresKeys.BUILD_EXTRACTOR),
            AgentTypes.BUILDING_ORDER_MANAGER);
        type.addConfiguration(DesiresKeys.UPGRADE_TO_LAIR, upgradeToLairAbstract, true);

        //build lair abstract common (to meet dependencies) - make reservation + check conditions (no lair). unlocked only when lair was built
        //build lair if not present
        ConfigurationWithAbstractPlan upgradeToLairIfNotPresentAbstract = createConfigurationWithAbstractPlanIfBuildingIsMissingFromTemplate(
            COUNT_OF_LAIRS, DesiresKeys.UPGRADE_TO_LAIR, AUnitTypeWrapper.LAIR_TYPE,
            Stream.of(DesiresKeys.ENABLE_GROUND_MELEE, DesiresKeys.BUILD_EXTRACTOR));

        //share desire to upgrade hatchery into lair with system - there is enough resources
        ConfigurationWithSharedDesire upgradeToLair = createConfigurationWithSharedDesireToBuildFromTemplate(
            DesiresKeys.UPGRADE_TO_LAIR, AUnitTypeWrapper.LAIR_TYPE, new FindBaseForBuilding(),
            (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE));
        type.addConfiguration(DesiresKeys.UPGRADE_TO_LAIR, DesiresKeys.UPGRADE_TO_LAIR,
            upgradeToLair);

        //tell system to build pool if needed
        type.addConfiguration(DesiresKeys.ENABLE_GROUND_MELEE, DesiresKeys.UPGRADE_TO_LAIR,
            buildPoolIfNotPresent);

        //tell system to build extractor if it is missing
        type.addConfiguration(DesiresKeys.BUILD_EXTRACTOR, DesiresKeys.UPGRADE_TO_LAIR,
            buildExtractorIfNotPresent);

        //hydralisk den as abstract plan. needs to meet dependencies - pool and at least one extractor
        ConfigurationWithAbstractPlan buildHydraliskDenAbstract = createOwnConfigurationWithAbstractPlanToBuildFromTemplate(
            COUNT_OF_HYDRALISK_DENS, DesiresKeys.ENABLE_GROUND_RANGED,
            AUnitTypeWrapper.HYDRALISK_DEN_TYPE,
            FeatureContainerHeaders.ENABLE_GROUND_RANGED,
            Stream.of(DesiresKeys.ENABLE_GROUND_MELEE, DesiresKeys.BUILD_EXTRACTOR),
            AgentTypes.BUILDING_ORDER_MANAGER);
        type.addConfiguration(DesiresKeys.ENABLE_GROUND_RANGED, buildHydraliskDenAbstract, true);

        //share desire to build hydralisk den with system - there is enough resources
        ConfigurationWithSharedDesire buildHydraliskDen = createConfigurationWithSharedDesireToBuildFromTemplate(
            DesiresKeys.MORPH_TO_HYDRALISK_DEN, AUnitTypeWrapper.HYDRALISK_DEN_TYPE,
            new FindBaseForBuilding(),
            (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE));
        type.addConfiguration(DesiresKeys.ENABLE_GROUND_RANGED, DesiresKeys.ENABLE_GROUND_RANGED,
            buildHydraliskDen);

        //tell system to build extractor if it is missing
        type.addConfiguration(DesiresKeys.BUILD_EXTRACTOR, DesiresKeys.ENABLE_GROUND_RANGED,
            buildExtractorIfNotPresent);

        //tell system to build pool if needed
        type.addConfiguration(DesiresKeys.ENABLE_GROUND_MELEE, DesiresKeys.ENABLE_GROUND_RANGED,
            buildPoolIfNotPresent);

        //spire as abstract plan. needs to meet dependencies - lair and at least one extractor
        ConfigurationWithAbstractPlan buildSpireAbstract = createOwnConfigurationWithAbstractPlanToBuildFromTemplate(
            COUNT_OF_SPIRES, DesiresKeys.ENABLE_AIR, AUnitTypeWrapper.SPIRE_TYPE,
            FeatureContainerHeaders.ENABLE_AIR,
            Stream.of(DesiresKeys.UPGRADE_TO_LAIR, DesiresKeys.BUILD_EXTRACTOR),
            AgentTypes.BUILDING_ORDER_MANAGER);
        type.addConfiguration(DesiresKeys.ENABLE_AIR, buildSpireAbstract, true);

        //share desire to build spire with system - there is enough resources
        ConfigurationWithSharedDesire buildSpire = createConfigurationWithSharedDesireToBuildFromTemplate(
            DesiresKeys.MORPH_TO_SPIRE, AUnitTypeWrapper.SPIRE_TYPE, new FindBaseForBuilding(),
            (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE));
        type.addConfiguration(DesiresKeys.ENABLE_AIR, DesiresKeys.ENABLE_AIR, buildSpire);

        //tell system to build extractor if missing
        type.addConfiguration(DesiresKeys.BUILD_EXTRACTOR, DesiresKeys.ENABLE_AIR,
            buildExtractorIfNotPresent);

        //tell system to upgrade to lair if missing
        type.addConfiguration(DesiresKeys.UPGRADE_TO_LAIR, DesiresKeys.ENABLE_AIR,
            upgradeToLairIfNotPresentAbstract);

        //evolution chamber as abstract plan
        ConfigurationWithAbstractPlan buildEvolutionChamberAbstract = createOwnConfigurationWithAbstractPlanToBuildFromTemplate(
            COUNT_OF_EVOLUTION_CHAMBERS, DesiresKeys.ENABLE_STATIC_ANTI_AIR,
            AUnitTypeWrapper.EVOLUTION_CHAMBER_TYPE, FeatureContainerHeaders.ENABLE_STATIC_ANTI_AIR,
            Stream.empty(),
            AgentTypes.BUILDING_ORDER_MANAGER);
        type.addConfiguration(DesiresKeys.ENABLE_STATIC_ANTI_AIR, buildEvolutionChamberAbstract,
            true);

        //build evolution chamber
        ConfigurationWithSharedDesire buildEvolutionChamber = createConfigurationWithSharedDesireToBuildFromTemplate(
            DesiresKeys.MORPH_TO_EVOLUTION_CHAMBER, AUnitTypeWrapper.EVOLUTION_CHAMBER_TYPE,
            new FindBaseForBuilding(),
            (memory, desireParameters) -> memory.eraseFactValueForGivenKey(BASE_TO_MOVE));
        type.addConfiguration(DesiresKeys.ENABLE_STATIC_ANTI_AIR,
            DesiresKeys.ENABLE_STATIC_ANTI_AIR,
            buildEvolutionChamber);

        //TODO other managers requirements?
        //other managers requirements
        //for melee
        //for sunken
        //for ranged
        //for air
      })
      .desiresWithAbstractIntention(new HashSet<>(Arrays.asList(DesiresKeys.UPGRADE_TO_LAIR,
          DesiresKeys.ENABLE_GROUND_RANGED, DesiresKeys.ENABLE_AIR, DesiresKeys.ENABLE_GROUND_MELEE,
          DesiresKeys.ENABLE_STATIC_ANTI_AIR)))
      .build();

  /**
   * Template to create desire initiated by desire to meet missing dependencies
   * Initialize abstract build plan - make reservation + check conditions (for building).
   * It is unlocked only when building is built
   */
  private static <T> ConfigurationWithAbstractPlan createConfigurationWithAbstractPlanIfBuildingIsMissingFromTemplate(
      FactWithSetOfOptionalValuesForAgentType<T> currentCount, DesireKey desireKey,
      AUnitTypeWrapper unitTypeWrapper, Stream<DesireKey> desireKeysWithAbstractIntentionStream) {
    return ConfigurationWithAbstractPlan.builder()
        .decisionInDesire(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) ->
                    !BotFacade.RESOURCE_MANAGER
                        .hasMadeReservationOn(unitTypeWrapper, memory.getAgentId())
                        && !dataForDecision.madeDecisionToAny()
                        && !BuildLockerService.getInstance().isLocked(unitTypeWrapper)
                        && dataForDecision.getFeatureValueGlobalBeliefs(currentCount) == 0)
            .globalBeliefTypesByAgentType(Collections.singleton(currentCount))
            .desiresToConsider(Collections.singleton(desireKey))
            .build())
        .decisionInIntention(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) ->
                    BuildLockerService.getInstance().isLocked(unitTypeWrapper)
                        //building exists
                        || dataForDecision.getFeatureValueGlobalBeliefs(currentCount) > 0)
            .globalBeliefTypesByAgentType(Collections.singleton(currentCount))
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
   * Template for finding base for building
   */
  private static class FindBaseForBuilding implements ReactionOnChangeStrategy {

    //find base to build building
    private static final ReactionOnChangeStrategy FIND_BASE_FOR_BUILDING = (memory, desireParameters) -> {
      List<ABaseLocationWrapper> ourBases = memory
          .getReadOnlyMemoriesForAgentType(AgentTypes.BASE_LOCATION.getId())
          .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_OUR_BASE).get())
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

    @Override
    public void updateBeliefs(WorkingMemory memory, DesireParameters desireParameters) {
      FIND_BASE_FOR_BUILDING.updateBeliefs(memory, desireParameters);
    }
  }

}
