package aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.DRONE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.EGG;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.LARVA;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.BUILD_CREEP_COLONY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.BUILD_SPORE_COLONY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.BUILD_SUNKEN_COLONY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.HOLD_AIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.HOLD_GROUND;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.AIR_DISTANCE_TO_ENEMY_CLOSEST_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.AIR_DISTANCE_TO_OUR_CLOSEST_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.DAMAGE_AIR_CAN_INFLICT_TO_AIR_VS_SUFFER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.DAMAGE_AIR_CAN_INFLICT_TO_GROUND_VS_SUFFER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.DAMAGE_GROUND_CAN_INFLICT_TO_AIR_VS_SUFFER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.DAMAGE_GROUND_CAN_INFLICT_TO_GROUND_VS_SUFFER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.DPS_OF_ANTI_AIR_UNITS_ON_ENEMY_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.DPS_OF_ANTI_GROUND_UNITS_ON_ENEMY_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_AIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_AIR_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_BUILDING;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_BUILDING_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_GROUND;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_STATIC_AIR_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_STATIC_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.GEYSER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.GROUND_DISTANCE_TO_ENEMY_CLOSEST_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.GROUND_DISTANCE_TO_OUR_CLOSEST_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.HAS_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.HAS_EXTRACTOR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.HOLD_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_ENEMY_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_GATHERING_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_GATHERING_MINERALS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_ISLAND;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MINERAL_ONLY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_OUR_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_START_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LOCKED_BUILDINGS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LOCKED_UNITS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.MINERAL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_AIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_AIR_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_BUILDING;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_BUILDING_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_GROUND;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_STATIC_AIR_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_STATIC_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.RATIO_GLOBAL_AIR_VS_ANTI_AIR_ON_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.RATIO_GLOBAL_GROUND_VS_ANTI_GROUND_ON_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.REPRESENTS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.STATIC_DEFENSE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.WORKER_MINING_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.WORKER_MINING_MINERALS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.WORKER_ON_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.DEFENSE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.HOLDING_BY_AIR_UNITS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.HOLDING_BY_GROUND_UNITS;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper.MAX_DISTANCE;

import aic.gas.sc.gg_bot.abstract_bot.model.UnitTypeStatus;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.game.util.Utils;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitOfPlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AWeaponTypeWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.UnitWrapperFactory;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import aic.gas.sc.gg_bot.replay_parser.model.AgentMakingObservations;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.Trajectory;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcherType.PlanWatcherInitializationStrategy;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.Beliefs;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.FeatureContainer;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.PlanWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_type_extension.BaseWatcherType;
import aic.gas.sc.gg_bot.replay_parser.service.IWatcherMediatorService;
import bwapi.Game;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of watcher for base
 */
@Slf4j
public class BaseWatcher extends AgentWatcher<BaseWatcherType> implements AgentMakingObservations {

  private final ABaseLocationWrapper baseLocation;
  private final UpdateChecksStrategy updateChecksStrategy;

  public BaseWatcher(ABaseLocationWrapper baseLocation, Game game,
      UpdateChecksStrategy updateChecksStrategy) {
    super(BaseWatcherType.builder()
            .factKeys(Stream.of(IS_OUR_BASE, IS_ENEMY_BASE, IS_MINERAL_ONLY, IS_ISLAND,
                IS_START_LOCATION, IS_BASE_LOCATION, DPS_OF_ANTI_AIR_UNITS_ON_ENEMY_BASE,
                DPS_OF_ANTI_GROUND_UNITS_ON_ENEMY_BASE, AIR_DISTANCE_TO_ENEMY_CLOSEST_BASE,
                AIR_DISTANCE_TO_OUR_CLOSEST_BASE, GROUND_DISTANCE_TO_ENEMY_CLOSEST_BASE,
                GROUND_DISTANCE_TO_OUR_CLOSEST_BASE, DAMAGE_AIR_CAN_INFLICT_TO_GROUND_VS_SUFFER,
                RATIO_GLOBAL_AIR_VS_ANTI_AIR_ON_BASE, DAMAGE_GROUND_CAN_INFLICT_TO_GROUND_VS_SUFFER,
                RATIO_GLOBAL_GROUND_VS_ANTI_GROUND_ON_BASE, DAMAGE_AIR_CAN_INFLICT_TO_AIR_VS_SUFFER,
                DAMAGE_GROUND_CAN_INFLICT_TO_AIR_VS_SUFFER)
                .collect(Collectors.toSet()))
            .factSetsKeys(Stream.of(MINERAL, WORKER_ON_BASE, GEYSER, ENEMY_BUILDING, ENEMY_AIR,
                ENEMY_GROUND, HAS_BASE, HAS_EXTRACTOR, OWN_BUILDING, OWN_AIR, OWN_GROUND,
                WORKER_MINING_MINERALS, WORKER_MINING_GAS, OWN_AIR_FORCE_STATUS, OWN_BUILDING_STATUS,
                OWN_GROUND_FORCE_STATUS, ENEMY_AIR_FORCE_STATUS, ENEMY_BUILDING_STATUS,
                ENEMY_GROUND_FORCE_STATUS, LOCKED_UNITS, LOCKED_BUILDINGS,
                ENEMY_STATIC_AIR_FORCE_STATUS, ENEMY_STATIC_GROUND_FORCE_STATUS,
                OWN_STATIC_AIR_FORCE_STATUS, OWN_STATIC_GROUND_FORCE_STATUS, STATIC_DEFENSE, ENEMY_UNIT)
                .collect(Collectors.toSet()))
            .reasoning((bl, ms) -> {
              ABaseLocationWrapper base = bl.returnFactValueForGivenKey(IS_BASE_LOCATION).get();

              //enemy's units
              Set<AUnit.Enemy> enemies = UnitWrapperFactory.getStreamOfAllAliveEnemyUnits()
                  .filter(enemy -> {
                    Optional<ABaseLocationWrapper> bL = enemy.getNearestBaseLocation();
                    return bL.isPresent() && bL.get().equals(base);
                  })
                  .collect(Collectors.toSet());
              bl.updateFactSetByFacts(ENEMY_UNIT, enemies);
              bl.updateFactSetByFacts(ENEMY_BUILDING,
                  enemies.stream().filter(enemy -> enemy.getType().isBuilding())
                      .collect(Collectors.toSet()));
              bl.updateFactSetByFacts(ENEMY_GROUND, enemies.stream()
                  .filter(enemy -> !enemy.getType().isBuilding() && !enemy.getType().isFlyer())
                  .collect(Collectors.toSet()));
              bl.updateFactSetByFacts(ENEMY_AIR, enemies.stream()
                  .filter(enemy -> !enemy.getType().isBuilding() && enemy.getType().isFlyer())
                  .collect(Collectors.toSet()));

              //player's units
              Set<AUnitOfPlayer> playersUnits = UnitWrapperFactory.getStreamOfAllAlivePlayersUnits()
                  .filter(enemy -> {
                    Optional<ABaseLocationWrapper> bL = enemy.getNearestBaseLocation();
                    return bL.isPresent() && bL.get().equals(base);
                  })
                  .collect(Collectors.toSet());
              bl.updateFactSetByFacts(OWN_BUILDING,
                  playersUnits.stream().filter(own -> own.getType().isBuilding())
                      .collect(Collectors.toSet()));
              bl.updateFactSetByFacts(OWN_GROUND, playersUnits.stream()
                  .filter(own -> !own.getType().isBuilding() && !own.getType().isFlyer())
                  .collect(Collectors.toSet()));
              bl.updateFactSetByFacts(OWN_AIR, playersUnits.stream()
                  .filter(own -> !own.getType().isBuilding() && own.getType().isFlyer())
                  .collect(Collectors.toSet()));
              bl.updateFactSetByFacts(STATIC_DEFENSE, playersUnits.stream()
                  .filter(aUnitOfPlayer ->
                      aUnitOfPlayer.getType().equals(AUnitTypeWrapper.SUNKEN_COLONY_TYPE)
                          || aUnitOfPlayer.getType().equals(AUnitTypeWrapper.CREEP_COLONY_TYPE)
                          || aUnitOfPlayer.getType().equals(AUnitTypeWrapper.SPORE_COLONY_TYPE))
                  .collect(Collectors.toSet()));

              //estimate enemy force
              Set<UnitTypeStatus> enemyBuildingsTypes = UnitWrapperFactory
                  .getStreamOfAllAliveEnemyUnits()
                  .filter(enemy -> enemy.getType().isBuilding())
                  .filter(enemy -> enemy.getNearestBaseLocation().isPresent())
                  .filter(enemy -> enemy.getNearestBaseLocation().get().equals(base))
                  .collect(Collectors.groupingBy(AUnit::getType)).entrySet().stream()
                  .map(entry -> new UnitTypeStatus(entry.getKey(), entry.getValue().stream()))
                  .collect(Collectors.toSet());
              bl.updateFactSetByFacts(ENEMY_BUILDING_STATUS, enemyBuildingsTypes);
              bl.updateFactSetByFacts(ENEMY_STATIC_AIR_FORCE_STATUS, enemyBuildingsTypes.stream()
                  .filter(
                      unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().isMilitaryBuildingAntiAir())
                  .collect(Collectors.toSet()));
              bl.updateFactSetByFacts(ENEMY_STATIC_GROUND_FORCE_STATUS, enemyBuildingsTypes.stream()
                  .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper()
                      .isMilitaryBuildingAntiGround())
                  .collect(Collectors.toSet()));
              Set<UnitTypeStatus> enemyUnitsTypes = UnitWrapperFactory.getStreamOfAllAliveEnemyUnits()
                  .filter(
                      enemy -> !enemy.getType().isNotActuallyUnit() && !enemy.getType().isBuilding())
                  .collect(Collectors.groupingBy(AUnit::getType)).entrySet().stream()
                  .map(entry -> new UnitTypeStatus(entry.getKey(), entry.getValue().stream()))
                  .collect(Collectors.toSet());
              bl.updateFactSetByFacts(ENEMY_AIR_FORCE_STATUS, enemyUnitsTypes.stream()
                  .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().canAttackAirUnits())
                  .collect(Collectors.toSet()));
              bl.updateFactSetByFacts(ENEMY_GROUND_FORCE_STATUS, enemyUnitsTypes.stream()
                  .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().canAttackGroundUnits())
                  .collect(Collectors.toSet()));

              //estimate our force
              Set<UnitTypeStatus> ownBuildingsTypes = UnitWrapperFactory
                  .getStreamOfAllAlivePlayersUnits()
                  .filter(enemy -> enemy.getType().isBuilding())
                  .filter(enemy -> enemy.getNearestBaseLocation().isPresent())
                  .filter(enemy -> enemy.getNearestBaseLocation().get().equals(base))
                  .collect(Collectors.groupingBy(AUnit::getType)).entrySet().stream()
                  .map(entry -> new UnitTypeStatus(entry.getKey(), entry.getValue().stream()))
                  .collect(Collectors.toSet());
              bl.updateFactSetByFacts(OWN_BUILDING_STATUS, ownBuildingsTypes);
              bl.updateFactSetByFacts(OWN_STATIC_AIR_FORCE_STATUS, ownBuildingsTypes.stream()
                  .filter(
                      unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().isMilitaryBuildingAntiAir())
                  .collect(Collectors.toSet()));
              bl.updateFactSetByFacts(OWN_STATIC_GROUND_FORCE_STATUS, ownBuildingsTypes.stream()
                  .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper()
                      .isMilitaryBuildingAntiGround())
                  .collect(Collectors.toSet()));
              Set<UnitTypeStatus> ownUnitsTypes = UnitWrapperFactory.getStreamOfAllAlivePlayersUnits()
                  .filter(
                      enemy -> !enemy.getType().isNotActuallyUnit() && !enemy.getType().isBuilding())
                  .filter(enemy -> enemy.getNearestBaseLocation().isPresent())
                  .filter(enemy -> enemy.getNearestBaseLocation().get().equals(base))
                  .collect(Collectors.groupingBy(AUnit::getType)).entrySet().stream()
                  .map(entry -> new UnitTypeStatus(entry.getKey(), entry.getValue().stream()))
                  .collect(Collectors.toSet());
              bl.updateFactSetByFacts(OWN_AIR_FORCE_STATUS, ownUnitsTypes.stream()
                  .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().canAttackAirUnits())
                  .collect(Collectors.toSet()));
              bl.updateFactSetByFacts(OWN_GROUND_FORCE_STATUS, ownUnitsTypes.stream()
                  .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().canAttackGroundUnits())
                  .collect(Collectors.toSet()));

              //eco concerns
              Set<AgentWatcher<?>> workersAroundBase = ms.getStreamOfWatchers()
                  .filter(agentWatcher ->
                      agentWatcher.getAgentWatcherType().getName().equals(DRONE.getName())
                          || agentWatcher.getAgentWatcherType().getName().equals(EGG.getName())
                          || agentWatcher.getAgentWatcherType().getName().equals(LARVA.getName())
                  )
                  .filter(agentWatcher ->
                      agentWatcher.getBeliefs().returnFactValueForGivenKey(REPRESENTS_UNIT).get()
                          .getType().isWorker()
                          || (
                          !agentWatcher.getBeliefs().returnFactValueForGivenKey(REPRESENTS_UNIT).get()
                              .getTrainingQueue().isEmpty()
                              && agentWatcher.getBeliefs().returnFactValueForGivenKey(REPRESENTS_UNIT)
                              .get().getTrainingQueue().get(0).isWorker()))
                  .filter(agentWatcher ->
                      agentWatcher.getBeliefs().returnFactValueForGivenKey(LOCATION).isPresent() &&
                          agentWatcher.getBeliefs().returnFactValueForGivenKey(LOCATION).get()
                              .equals(base))
                  .collect(Collectors.toSet());
              bl.updateFactSetByFacts(WORKER_ON_BASE, workersAroundBase.stream()
                  .map(agentWatcher -> agentWatcher.getBeliefs()
                      .returnFactValueForGivenKey(REPRESENTS_UNIT).get())
                  .collect(Collectors.toSet()));
              workersAroundBase = workersAroundBase.stream()
                  .filter(agentWatcher -> agentWatcher.getBeliefs()
                      .returnFactValueForGivenKey(REPRESENTS_UNIT).get().getType().isWorker())
                  .collect(Collectors.toSet());
              bl.updateFactSetByFacts(WORKER_MINING_MINERALS, workersAroundBase.stream()
                  .filter(agentWatcher -> agentWatcher.getBeliefs()
                      .returnFactValueForGivenKey(IS_GATHERING_MINERALS).get())
                  .map(agentWatcher -> agentWatcher.getBeliefs()
                      .returnFactValueForGivenKey(REPRESENTS_UNIT).get())
                  .collect(Collectors.toSet()));
              bl.updateFactSetByFacts(WORKER_MINING_GAS, workersAroundBase.stream()
                  .filter(agentWatcher -> agentWatcher.getBeliefs()
                      .returnFactValueForGivenKey(IS_GATHERING_GAS).get())
                  .map(agentWatcher -> agentWatcher.getBeliefs()
                      .returnFactValueForGivenKey(REPRESENTS_UNIT).get())
                  .collect(Collectors.toSet()));
              bl.updateFactSetByFacts(HAS_BASE,
                  bl.returnFactSetValueForGivenKey(OWN_BUILDING).orElse(Stream.empty())
                      .filter(aUnitOfPlayer ->
                          aUnitOfPlayer.getType().equals(AUnitTypeWrapper.HATCHERY_TYPE)
                              || aUnitOfPlayer.getType().equals(AUnitTypeWrapper.LAIR_TYPE))
                      .collect(Collectors.toSet()));
              bl.updateFactSetByFacts(HAS_EXTRACTOR,
                  bl.returnFactSetValueForGivenKey(OWN_BUILDING).orElse(Stream.empty())
                      .filter(aUnitOfPlayer -> aUnitOfPlayer.getType().isGasBuilding())
                      .collect(Collectors.toSet()));

              //status
              boolean isOurBase = bl.returnFactSetValueForGivenKey(HAS_BASE)
                  .orElse(Stream.empty()).findAny().isPresent();
              bl.updateFact(IS_OUR_BASE, isOurBase);
              boolean isEnemyBase = !isOurBase && bl.returnFactSetValueForGivenKey(ENEMY_BUILDING)
                  .orElse(Stream.empty()).findAny().isPresent();
              bl.updateFact(IS_ENEMY_BASE, isEnemyBase);

              //update checks
              updateChecksStrategy.updateChecks(isOurBase, isEnemyBase);

              if (isEnemyBase) {

                //count anti-air and anti-ground
                bl.updateFact(DPS_OF_ANTI_AIR_UNITS_ON_ENEMY_BASE, enemies.stream()
                    .map(AUnit::getType)
                    .filter(AUnitTypeWrapper::canAttackAirUnits)
                    .mapToDouble(value -> value.getAirWeapon().getDamagePerSecondNormalized())
                    .sum());
                bl.updateFact(DPS_OF_ANTI_GROUND_UNITS_ON_ENEMY_BASE, enemies.stream()
                    .map(AUnit::getType)
                    .filter(AUnitTypeWrapper::canAttackGroundUnits)
                    .mapToDouble(value -> value.getGroundWeapon().getDamagePerSecondNormalized())
                    .sum());
              } else {

                //we do not care whether base is unprotected against unit type if it does not belong to enemy
                bl.eraseFactValueForGivenKey(DPS_OF_ANTI_AIR_UNITS_ON_ENEMY_BASE);
                bl.eraseFactValueForGivenKey(DPS_OF_ANTI_GROUND_UNITS_ON_ENEMY_BASE);
              }

              //distances
              bl.updateFact(GROUND_DISTANCE_TO_ENEMY_CLOSEST_BASE, ms.getStreamOfWatchers()
                  .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                      .equals(AgentTypes.BASE_LOCATION.getName()))
                  .filter(agentWatcher -> agentWatcher.getBeliefs()
                      .returnFactValueForGivenKey(IS_ENEMY_BASE)
                      .orElse(false))
                  .map(agentWatcher -> agentWatcher.getBeliefs()
                      .returnFactValueForGivenKey(IS_BASE_LOCATION))
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .filter(locationWrapper -> !base.equals(locationWrapper))
                  .map(locationWrapper -> locationWrapper.getGroundDistanceToBase(base))
                  .min(Double::compareTo)
                  .orElse(MAX_DISTANCE));
              bl.updateFact(AIR_DISTANCE_TO_ENEMY_CLOSEST_BASE, ms.getStreamOfWatchers()
                  .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                      .equals(AgentTypes.BASE_LOCATION.getName()))
                  .filter(
                      agentWatcher -> agentWatcher.getBeliefs()
                          .returnFactValueForGivenKey(IS_ENEMY_BASE)
                          .orElse(false))
                  .map(agentWatcher -> agentWatcher.getBeliefs()
                      .returnFactValueForGivenKey(IS_BASE_LOCATION))
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .filter(locationWrapper -> !base.equals(locationWrapper))
                  .map(locationWrapper -> locationWrapper.getAirDistanceToBase(base))
                  .min(Double::compareTo)
                  .orElse(MAX_DISTANCE));
              bl.updateFact(GROUND_DISTANCE_TO_OUR_CLOSEST_BASE, ms.getStreamOfWatchers()
                  .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                      .equals(AgentTypes.BASE_LOCATION.getName()))
                  .filter(agentWatcher -> agentWatcher.getBeliefs().returnFactValueForGivenKey(
                      IS_OUR_BASE)
                      .orElse(false))
                  .map(agentWatcher -> agentWatcher.getBeliefs()
                      .returnFactValueForGivenKey(IS_BASE_LOCATION))
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .filter(locationWrapper -> !base.equals(locationWrapper))
                  .map(locationWrapper -> locationWrapper.getGroundDistanceToBase(base))
                  .min(Double::compareTo)
                  .orElse(MAX_DISTANCE));
              bl.updateFact(AIR_DISTANCE_TO_OUR_CLOSEST_BASE, ms.getStreamOfWatchers()
                  .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                      .equals(AgentTypes.BASE_LOCATION.getName()))
                  .filter(agentWatcher -> agentWatcher.getBeliefs().returnFactValueForGivenKey(
                      IS_OUR_BASE)
                      .orElse(false))
                  .map(agentWatcher -> agentWatcher.getBeliefs()
                      .returnFactValueForGivenKey(IS_BASE_LOCATION))
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .filter(locationWrapper -> !base.equals(locationWrapper))
                  .map(locationWrapper -> locationWrapper.getAirDistanceToBase(base))
                  .min(Double::compareTo)
                  .orElse(MAX_DISTANCE));

              //damage to air by enemy
              double dpsToAirsByEnemy = bl.returnFactSetValueForGivenKey(ENEMY_UNIT)
                  .orElse(Stream.empty())
                  .map(AUnit::getType)
                  .filter(AUnitTypeWrapper::canAttackAirUnits)
                  .map(AUnitTypeWrapper::getAirWeapon)
                  .mapToDouble(AWeaponTypeWrapper::getDamagePerSecondNormalized)
                  .sum();

              //damage to ground by enemy
              double dpsToGroundsByEnemy = bl.returnFactSetValueForGivenKey(ENEMY_UNIT)
                  .orElse(Stream.empty())
                  .map(AUnit::getType)
                  .filter(AUnitTypeWrapper::canAttackGroundUnits)
                  .map(AUnitTypeWrapper::getGroundWeapon)
                  .mapToDouble(AWeaponTypeWrapper::getDamagePerSecondNormalized)
                  .sum();

              //air
              bl.updateFact(DAMAGE_AIR_CAN_INFLICT_TO_GROUND_VS_SUFFER,
                  Utils.computeRatio(
                      bl.returnFactSetValueForGivenKey(OWN_AIR).orElse(Stream.empty())
                          .map(AUnit::getType)
                          .filter(AUnitTypeWrapper::canAttackGroundUnits)
                          .map(AUnitTypeWrapper::getGroundWeapon)
                          .mapToDouble(AWeaponTypeWrapper::getDamagePerSecondNormalized)
                          .sum(), dpsToAirsByEnemy));
              bl.updateFact(DAMAGE_AIR_CAN_INFLICT_TO_AIR_VS_SUFFER,
                  Utils.computeRatio(
                      bl.returnFactSetValueForGivenKey(OWN_AIR).orElse(Stream.empty())
                          .map(AUnit::getType)
                          .filter(AUnitTypeWrapper::canAttackAirUnits)
                          .map(AUnitTypeWrapper::getAirWeapon)
                          .mapToDouble(AWeaponTypeWrapper::getDamagePerSecondNormalized)
                          .sum(), dpsToAirsByEnemy));

              //ground
              bl.updateFact(DAMAGE_GROUND_CAN_INFLICT_TO_GROUND_VS_SUFFER,
                  Utils.computeRatio(
                      bl.returnFactSetValueForGivenKey(OWN_GROUND).orElse(Stream.empty())
                          .map(AUnit::getType)
                          .filter(AUnitTypeWrapper::canAttackGroundUnits)
                          .map(AUnitTypeWrapper::getGroundWeapon)
                          .mapToDouble(AWeaponTypeWrapper::getDamagePerSecondNormalized)
                          .sum(), dpsToGroundsByEnemy));
              bl.updateFact(DAMAGE_GROUND_CAN_INFLICT_TO_AIR_VS_SUFFER,
                  Utils.computeRatio(
                      bl.returnFactSetValueForGivenKey(OWN_GROUND).orElse(Stream.empty())
                          .map(AUnit::getType)
                          .filter(AUnitTypeWrapper::canAttackAirUnits)
                          .map(AUnitTypeWrapper::getAirWeapon)
                          .mapToDouble(AWeaponTypeWrapper::getDamagePerSecondNormalized)
                          .sum(), dpsToGroundsByEnemy));

              //supply of enemy anti-air
              double supplyOfAntiAir = bl.returnFactSetValueForGivenKey(ENEMY_UNIT)
                  .orElse(Stream.empty())
                  .map(AUnit::getType)
                  .filter(AUnitTypeWrapper::canAttackAirUnits)
                  .mapToDouble(AUnitTypeWrapper::supplyRequired)
                  .sum();

              //supply of enemy anti-bl
              double supplyOfAntiGround = bl.returnFactSetValueForGivenKey(ENEMY_UNIT)
                  .orElse(Stream.empty())
                  .map(AUnit::getType)
                  .filter(unitTypeWrapper -> !unitTypeWrapper.isWorker())
                  .filter(AUnitTypeWrapper::canAttackGroundUnits)
                  .mapToDouble(AUnitTypeWrapper::supplyRequired)
                  .sum();

              //player's units
              Set<AUnitOfPlayer> playersUnitsGlobal = UnitWrapperFactory
                  .getStreamOfAllAlivePlayersUnits()
                  .filter(aUnitOfPlayer -> !aUnitOfPlayer.getType().isNotActuallyUnit())
                  .filter(aUnitOfPlayer -> !aUnitOfPlayer.getType().isWorker())
                  .filter(aUnitOfPlayer -> !aUnitOfPlayer.getType().isBuilding())
                  .collect(Collectors.toSet());

              //air
              bl.updateFact(RATIO_GLOBAL_AIR_VS_ANTI_AIR_ON_BASE,
                  Utils.computeRatio(playersUnitsGlobal.stream()
                      .map(AUnit::getType)
                      .filter(AUnitTypeWrapper::isFlyer)
                      .mapToDouble(AUnitTypeWrapper::supplyRequired)
                      .sum(), supplyOfAntiAir));

              //ground
              bl.updateFact(DAMAGE_GROUND_CAN_INFLICT_TO_GROUND_VS_SUFFER,
                  Utils.computeRatio(playersUnitsGlobal.stream()
                      .map(AUnit::getType)
                      .filter(unitTypeWrapper -> !unitTypeWrapper.isFlyer())
                      .mapToDouble(AUnitTypeWrapper::supplyRequired)
                      .sum(), supplyOfAntiGround));

            })
            .baseEnvironmentObservation(
                (aBaseLocation, beliefs) -> makeObservation(aBaseLocation, beliefs, game))
            .agentTypeID(AgentTypes.BASE_LOCATION)
            .planWatchers(Arrays.asList(new PlanWatcherInitializationStrategy[]{

                //TODO handle relly points

                //HOLD_GROUND
                () -> new PlanWatcher(() -> new FeatureContainer(HOLDING_BY_GROUND_UNITS),
                    HOLD_GROUND) {

                  @Override
                  protected boolean isAgentCommitted(IWatcherMediatorService mediatorService,
                      Beliefs beliefs) {
                    ABaseLocationWrapper me = beliefs.returnFactValueForGivenKey(IS_BASE_LOCATION)
                        .get();
                    long groundUnitsTargetingBase = mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher -> agentWatcher.getBeliefs()
                            .isFactKeyForValueInMemory(HOLD_LOCATION))
                        .filter(agentWatcher -> me.equals(agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(HOLD_LOCATION).orElse(null)))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(REPRESENTS_UNIT))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(AUnit::getType)
                        .filter(typeWrapper -> !typeWrapper.isFlyer() && !typeWrapper.isWorker()
                            && !typeWrapper.isNotActuallyUnit())
                        .count();
                    if (groundUnitsTargetingBase < 3) {
                      return false;
                    }
                    long ourGroundArmy = UnitWrapperFactory.getStreamOfAllAlivePlayersUnits()
                        .map(AUnit::getType)
                        .filter(typeWrapper -> !typeWrapper.isFlyer() && !typeWrapper.isWorker()
                            && !typeWrapper.isNotActuallyUnit())
                        .count();
                    if (((double) groundUnitsTargetingBase / (double) ourGroundArmy) < 1 / 3) {
                      return false;
                    }
//                log.info("Holding " + me.toString() + " by GROUND.");
                    return true;
                  }

                  @Override
                  protected Stream<AgentWatcher<?>> streamOfAgentsToNotifyAboutCommitment() {
                    return Stream.empty();
                  }
                },

                //HOLD_AIR
                () -> new PlanWatcher(() -> new FeatureContainer(HOLDING_BY_AIR_UNITS), HOLD_AIR) {

                  @Override
                  protected boolean isAgentCommitted(IWatcherMediatorService mediatorService,
                      Beliefs beliefs) {
                    ABaseLocationWrapper me = beliefs.returnFactValueForGivenKey(IS_BASE_LOCATION)
                        .get();
                    long groundUnitsTargetingBase = mediatorService.getStreamOfWatchers()
                        .filter(agentWatcher -> agentWatcher.getBeliefs()
                            .isFactKeyForValueInMemory(HOLD_LOCATION))
                        .filter(agentWatcher -> me.equals(agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(HOLD_LOCATION).orElse(null)))
                        .map(agentWatcher -> agentWatcher.getBeliefs()
                            .returnFactValueForGivenKey(REPRESENTS_UNIT))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(AUnit::getType)
                        .filter(typeWrapper -> typeWrapper.isFlyer()
                            && !typeWrapper.equals(AUnitTypeWrapper.OVERLORD_TYPE))
                        .count();
                    if (groundUnitsTargetingBase < 3) {
                      return false;
                    }
                    long ourAirArmy = UnitWrapperFactory.getStreamOfAllAlivePlayersUnits()
                        .map(AUnit::getType)
                        .filter(typeWrapper -> typeWrapper.isFlyer() && !typeWrapper
                            .equals(AUnitTypeWrapper.OVERLORD_TYPE))
                        .count();
                    if (((double) groundUnitsTargetingBase / (double) ourAirArmy) < 1 / 3) {
                      return false;
                    }
//                log.info("Holding " + me.toString() + " by AIR.");
                    return true;
                  }

                  @Override
                  protected Stream<AgentWatcher<?>> streamOfAgentsToNotifyAboutCommitment() {
                    return Stream.empty();
                  }
                },

                //BUILD_CREEP_COLONY
                () -> new ColonyPlanWatcher(() -> new FeatureContainer(DEFENSE), BUILD_CREEP_COLONY,
                    AUnitTypeWrapper.CREEP_COLONY_TYPE),

                //BUILD_SUNKEN_COLONY
                () -> new ColonyPlanWatcher(() -> new FeatureContainer(DEFENSE), BUILD_SUNKEN_COLONY,
                    AUnitTypeWrapper.SUNKEN_COLONY_TYPE),

                //BUILD_SPORE_COLONY
                () -> new ColonyPlanWatcher(() -> new FeatureContainer(DEFENSE), BUILD_SPORE_COLONY,
                    AUnitTypeWrapper.SPORE_COLONY_TYPE)
            }))
            .build()
    );
    this.baseLocation = baseLocation;
    beliefs.updateFact(IS_MINERAL_ONLY, baseLocation.isMineralOnly());
    beliefs.updateFact(IS_ISLAND, baseLocation.isIsland());
    beliefs.updateFact(IS_START_LOCATION, baseLocation.isStartLocation());
    beliefs.updateFact(IS_BASE_LOCATION, baseLocation);

    this.updateChecksStrategy = updateChecksStrategy;
  }

  private static class ColonyPlanWatcher extends PlanWatcher {

    private Set<Integer> committedAgents = new HashSet<>();
    private final AUnitTypeWrapper typeToWatchFor;

    ColonyPlanWatcher(FeatureContainerInitializationStrategy featureContainerInitializationStrategy,
        DesireKeyID desireKey, AUnitTypeWrapper typeToWatchFor) {
      super(featureContainerInitializationStrategy, desireKey);
      this.typeToWatchFor = typeToWatchFor;
    }

    @Override
    protected boolean isAgentCommitted(IWatcherMediatorService mediatorService, Beliefs beliefs) {

      //building colony only in base
      if (!beliefs.returnFactValueForGivenKey(IS_OUR_BASE).orElse(false)) {
        committedAgents.clear();
        return false;
      }

      ABaseLocationWrapper me = beliefs.returnFactValueForGivenKey(IS_BASE_LOCATION).get();

      Set<Integer> agentsMorphingToType = mediatorService.getStreamOfWatchers()
          .filter(
              agentWatcher -> agentWatcher.getBeliefs().isFactKeyForValueInMemory(IS_MORPHING_TO))
          .filter(agentWatcher -> typeToWatchFor.equals(agentWatcher.getBeliefs()
              .returnFactValueForGivenKey(IS_MORPHING_TO).orElse(null)))
          .filter(agentWatcher ->
              agentWatcher.getBeliefs().returnFactValueForGivenKey(LOCATION).isPresent()
                  && agentWatcher.getBeliefs().returnFactValueForGivenKey(LOCATION).get()
                  .equals(me))
          .map(AgentWatcher::getID)
          .collect(Collectors.toSet());

      boolean isCommitted = agentsMorphingToType.stream()
          .anyMatch(integer -> !committedAgents.contains(integer));
      committedAgents = agentsMorphingToType;
      return isCommitted;
    }

    @Override
    protected Stream<AgentWatcher<?>> streamOfAgentsToNotifyAboutCommitment() {
      return Stream.empty();
    }
  }

  /**
   * Method to make observation
   */
  private static void makeObservation(ABaseLocationWrapper location, Beliefs beliefs, Game game) {

    //resources
    Set<AUnit> minerals = location.getWrappedPosition().getMinerals().stream()
        .map(unit -> UnitWrapperFactory.wrapResourceUnits(unit, game.getFrameCount(), false))
        .collect(Collectors.toSet());
    beliefs.updateFactSetByFacts(MINERAL, minerals);
    Set<AUnit> geysers = location.getWrappedPosition().getGeysers().stream()
        .map(unit -> UnitWrapperFactory.wrapResourceUnits(unit, game.getFrameCount(), false))
        .collect(Collectors.toSet());
    beliefs.updateFactSetByFacts(GEYSER, geysers);
  }

  @Override
  public Stream<Map.Entry<DesireKeyID, List<Trajectory>>> getTrajectories() {
    Map<DesireKeyID, List<Trajectory>> map = plansToWatch.stream()
        .collect(Collectors.groupingBy(PlanWatcher::getDesireKey,
            Collectors.mapping(PlanWatcher::getTrajectory, Collectors.toList())));

    //filter trajectories
    map.forEach((desireKeyID, trajectories) -> {

      //building colony outside of base
      if (desireKeyID.equals(DesireKeys.BUILD_CREEP_COLONY) || desireKeyID
          .equals(DesireKeys.BUILD_SPORE_COLONY)
          || desireKeyID.equals(DesireKeys.BUILD_SUNKEN_COLONY)) {
        if (!updateChecksStrategy.isWasEverOurBase()) {
          trajectories.clear();
        }
      }

      //holding position outside of our or enemy base
      if (desireKeyID.equals(DesireKeys.HOLD_AIR) || desireKeyID.equals(DesireKeys.HOLD_GROUND)) {
        if (!updateChecksStrategy.isWasEverOurBase() && !updateChecksStrategy
            .isWasEverEnemyBase()) {
          trajectories.clear();
        }
      }

    });

    return map.entrySet().stream();
  }

  public void makeObservation() {
    agentWatcherType.getBaseEnvironmentObservation().updateBeliefs(baseLocation, beliefs);
  }

  /**
   * Hack - to check if base was ever ours or enemy's
   */
  @Getter
  public static class UpdateChecksStrategy {

    private boolean wasEverOurBase = false, wasEverEnemyBase = false;

    /**
     * Updates checks
     */
    void updateChecks(boolean isOurBase, boolean isEnemyBase) {
      if (!wasEverEnemyBase) {
        wasEverEnemyBase = isEnemyBase;
      }
      if (!wasEverOurBase) {
        wasEverOurBase = isOurBase;
      }
    }
  }
}
