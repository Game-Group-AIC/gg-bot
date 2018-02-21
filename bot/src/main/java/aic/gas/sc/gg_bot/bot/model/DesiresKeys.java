package aic.gas.sc.gg_bot.bot.model;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.*;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.mas.model.knowledge.Fact;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public class DesiresKeys {

  //for player
  public static final DesireKey READ_PLAYERS_DATA = DesireKey.builder()
      .desireKeyId(DesireKeys.READ_PLAYERS_DATA.getId())
      .build();
  public static final DesireKey ESTIMATE_ENEMY_FORCE_IN_BUILDINGS = DesireKey.builder()
      .desireKeyId(DesireKeys.ESTIMATE_ENEMY_FORCE_IN_BUILDINGS.getId())
      .build();
  public static final DesireKey ESTIMATE_ENEMY_FORCE_IN_UNITS = DesireKey.builder()
      .desireKeyId(DesireKeys.ESTIMATE_ENEMY_FORCE_IN_UNITS.getId())
      .build();
  public static final DesireKey ESTIMATE_OUR_FORCE_IN_BUILDINGS = DesireKey.builder()
      .desireKeyId(DesireKeys.ESTIMATE_OUR_FORCE_IN_BUILDINGS.getId())
      .build();
  public static final DesireKey ESTIMATE_OUR_FORCE_IN_UNITS = DesireKey.builder()
      .desireKeyId(DesireKeys.ESTIMATE_OUR_FORCE_IN_UNITS.getId())
      .build();
  public static final DesireKey UPDATE_ENEMY_RACE = DesireKey.builder()
      .desireKeyId(DesireKeys.UPDATE_ENEMY_RACE.getId())
      .build();
  public static final DesireKey REASON_ABOUT_BASES = DesireKey.builder()
      .desireKeyId(DesireKeys.REASON_ABOUT_BASES.getId())
      .build();
  public static final DesireKey ESTIMATE_ARMY_SUPPLY_RATIO = DesireKey.builder()
      .desireKeyId(DesireKeys.ESTIMATE_ARMY_SUPPLY_RATIO.getId())
      .build();

  //for eco manager
  public static final DesireKey BUILD_WORKER = DesireKey.builder()
      .desireKeyId(DesireKeys.BUILD_WORKER.getId())
      .build();
  public static final DesireKey INCREASE_CAPACITY = DesireKey.builder()
      .desireKeyId(DesireKeys.INCREASE_CAPACITY.getId())
      .build();
  public static final DesireKey EXPAND = DesireKey.builder()
      .desireKeyId(DesireKeys.EXPAND.getId())
      .parametersTypesForFacts(Collections.singleton(BASE_TO_MOVE))
      .build();
  public static final DesireKey BUILD_EXTRACTOR = DesireKey.builder()
      .desireKeyId(DesireKeys.BUILD_EXTRACTOR.getId())
      .build();

  //for unit order manager
  public static final DesireKey BOOST_GROUND_MELEE = DesireKey.builder()
      .desireKeyId(DesireKeys.BOOST_GROUND_MELEE.getId())
      .staticFactValues(Collections
          .singleton(new Fact<>(() -> AUnitTypeWrapper.ZERGLING_TYPE, FactKeys.MORPH_TO)))
      .build();
  public static final DesireKey BOOST_GROUND_RANGED = DesireKey.builder()
      .desireKeyId(DesireKeys.BOOST_GROUND_RANGED.getId())
      .staticFactValues(Collections
          .singleton(new Fact<>(() -> AUnitTypeWrapper.HYDRALISK_TYPE, FactKeys.MORPH_TO)))
      .build();
  public static final DesireKey BOOST_AIR = DesireKey.builder()
      .desireKeyId(DesireKeys.BOOST_AIR.getId())
      .staticFactValues(Collections
          .singleton(new Fact<>(() -> AUnitTypeWrapper.MUTALISK_TYPE, FactKeys.MORPH_TO)))
      .build();

  //for build order manager
  public static final DesireKey ENABLE_GROUND_MELEE = DesireKey.builder()
      .desireKeyId(DesireKeys.ENABLE_GROUND_MELEE.getId())
      .build();
  public static final DesireKey ENABLE_GROUND_RANGED = DesireKey.builder()
      .desireKeyId(DesireKeys.ENABLE_GROUND_RANGED.getId())
      .build();
  public static final DesireKey ENABLE_STATIC_ANTI_AIR = DesireKey.builder()
      .desireKeyId(DesireKeys.ENABLE_STATIC_ANTI_AIR.getId())
      .build();
  public static final DesireKey ENABLE_AIR = DesireKey.builder()
      .desireKeyId(DesireKeys.ENABLE_AIR.getId())
      .build();
  public static final DesireKey UPGRADE_TO_LAIR = DesireKey.builder()
      .desireKeyId(DesireKeys.UPGRADE_TO_LAIR.getId())
      .build();

  //attack
  public static final DesireKey HOLD_GROUND = DesireKey.builder()
      .desireKeyId(DesireKeys.HOLD_GROUND.getId())
      .parametersTypesForFacts(Collections.singleton(IS_BASE_LOCATION))
      .build();
  public static final DesireKey HOLD_AIR = DesireKey.builder()
      .desireKeyId(DesireKeys.HOLD_AIR.getId())
      .parametersTypesForFacts(Collections.singleton(IS_BASE_LOCATION))
      .build();

  //for base
  public static final DesireKey ECO_STATUS_IN_LOCATION = DesireKey.builder()
      .desireKeyId(DesireKeys.ECO_STATUS_IN_LOCATION.getId())
      .build();
  public static final DesireKey REASON_GLOBAL_SUPPLY_VS_LOCAL_ENEMIES_RATIO = DesireKey.builder()
      .desireKeyId(DesireKeys.REASON_GLOBAL_SUPPLY_VS_LOCAL_ENEMIES_RATIO.getId())
      .build();
  public static final DesireKey REASON_ABOUT_ENEMY_BASE_WEAKNESSES = DesireKey.builder()
      .desireKeyId(DesireKeys.REASON_ABOUT_ENEMY_BASE_WEAKNESSES.getId())
      .build();
  public static final DesireKey REASON_ABOUT_DISTANCES = DesireKey.builder()
      .desireKeyId(DesireKeys.REASON_ABOUT_DISTANCES.getId())
      .build();
  public static final DesireKey REASON_ABOUT_SUFFERING_AND_INFLICTING_DMG = DesireKey.builder()
      .desireKeyId(DesireKeys.REASON_ABOUT_SUFFERING_AND_INFLICTING_DMG.getId())
      .build();
  public static final DesireKey REASON_ABOUT_BASE_TYPE = DesireKey.builder()
      .desireKeyId(DesireKeys.REASON_ABOUT_BASE_TYPE.getId())
      .build();
  public static final DesireKey REASON_ABOUT_OUR_BASE = DesireKey.builder()
      .desireKeyId(DesireKeys.REASON_ABOUT_OUR_BASE.getId())
      .build();
  public static final DesireKey ESTIMATE_ENEMY_FORCE_IN_LOCATION = DesireKey.builder()
      .desireKeyId(DesireKeys.ESTIMATE_ENEMY_FORCE_IN_LOCATION.getId())
      .build();
  public static final DesireKey ESTIMATE_OUR_FORCE_IN_LOCATION = DesireKey.builder()
      .desireKeyId(DesireKeys.ESTIMATE_OUR_FORCE_IN_LOCATION.getId())
      .build();
  public static final DesireKey FRIENDLIES_IN_LOCATION = DesireKey.builder()
      .desireKeyId(DesireKeys.FRIENDLIES_IN_LOCATION.getId())
      .build();
  public static final DesireKey ENEMIES_IN_LOCATION = DesireKey.builder()
      .desireKeyId(DesireKeys.ENEMIES_IN_LOCATION.getId())
      .build();
  public static final DesireKey BUILD_SPORE_COLONY = DesireKey.builder()
      .desireKeyId(DesireKeys.BUILD_SPORE_COLONY.getId())
      .build();
  public static final DesireKey BUILD_CREEP_COLONY = DesireKey.builder()
      .desireKeyId(DesireKeys.BUILD_SPORE_COLONY.getId())
      .build();
  public static final DesireKey BUILD_SUNKEN_COLONY = DesireKey.builder()
      .desireKeyId(DesireKeys.BUILD_SUNKEN_COLONY.getId())
      .build();
  public static final DesireKey MINE_MINERALS_IN_BASE = DesireKey.builder()
      .desireKeyId(DesireKeys.MINE_MINERALS_IN_BASE.getId())
      .parametersTypesForFacts(Collections.singleton(IS_BASE_LOCATION))
      .parametersTypesForFactSets(Stream.of(MINERAL, HAS_BASE)
          .collect(Collectors.toSet()))
      .build();
  public static final DesireKey MINE_GAS_IN_BASE = DesireKey.builder()
      .desireKeyId(DesireKeys.MINE_GAS_IN_BASE.getId())
      .parametersTypesForFacts(Collections.singleton(IS_BASE_LOCATION))
      .parametersTypesForFactSets(Stream.of(HAS_EXTRACTOR, HAS_BASE)
          .collect(Collectors.toSet()))
      .build();

  //scouting
  public static final DesireKey VISIT = DesireKey.builder()
      .desireKeyId(DesireKeys.VISIT.getId())
      .parametersTypesForFacts(Collections.singleton(IS_BASE_LOCATION))
      .build();
  public static final DesireKey WORKER_SCOUT = DesireKey.builder()
      .desireKeyId(DesireKeys.WORKER_SCOUT.getId())
      .build();

  //morphing
  public static final DesireKey MORPHING_TO = DesireKey.builder()
      .desireKeyId(DesireKeys.MORPHING_TO.getId())
      .build();
  public static final DesireKey MORPH_TO_DRONE = DesireKey.builder()
      .desireKeyId(DesireKeys.MORPH_TO_DRONE.getId())
      .staticFactValues(Collections
          .singleton(new Fact<>(() -> AUnitTypeWrapper.DRONE_TYPE, FactKeys.MORPH_TO)))
      .build();
  public static final DesireKey MORPH_TO_OVERLORD = DesireKey.builder()
      .desireKeyId(DesireKeys.MORPH_TO_OVERLORD.getId())
      .staticFactValues(Collections
          .singleton(new Fact<>(() -> AUnitTypeWrapper.OVERLORD_TYPE, FactKeys.MORPH_TO)))
      .build();
  public static final DesireKey MORPH_TO_POOL = DesireKey.builder()
      .desireKeyId(DesireKeys.MORPH_TO_POOL.getId())
      .parametersTypesForFacts(Collections.singleton(BASE_TO_MOVE))
      .build();
  public static final DesireKey MORPH_TO_SPIRE = DesireKey.builder()
      .desireKeyId(DesireKeys.MORPH_TO_SPIRE.getId())
      .parametersTypesForFacts(Collections.singleton(BASE_TO_MOVE))
      .build();
  public static final DesireKey MORPH_TO_EVOLUTION_CHAMBER = DesireKey.builder()
      .desireKeyId(DesireKeys.MORPH_TO_EVOLUTION_CHAMBER.getId())
      .parametersTypesForFacts(Collections.singleton(BASE_TO_MOVE))
      .build();
  public static final DesireKey MORPH_TO_HYDRALISK_DEN = DesireKey.builder()
      .desireKeyId(DesireKeys.MORPH_TO_HYDRALISK_DEN.getId())
      .parametersTypesForFacts(Collections.singleton(BASE_TO_MOVE))
      .build();
  public static final DesireKey MORPH_TO_EXTRACTOR = DesireKey.builder()
      .desireKeyId(DesireKeys.MORPH_TO_EXTRACTOR.getId())
      .parametersTypesForFacts(Collections.singleton(BASE_TO_MOVE))
      .build();
  public static final DesireKey MORPH_TO_SPORE_COLONY = DesireKey.builder()
      .desireKeyId(DesireKeys.MORPH_TO_SPORE_COLONY.getId())
      .parametersTypesForFacts(Collections.singleton(BASE_TO_MOVE))
      .build();
  public static final DesireKey MORPH_TO_CREEP_COLONY = DesireKey.builder()
      .desireKeyId(DesireKeys.MORPH_TO_CREEP_COLONY.getId())
      .parametersTypesForFacts(Collections.singleton(BASE_TO_MOVE))
      .build();
  public static final DesireKey MORPH_TO_SUNKEN_COLONY = DesireKey.builder()
      .desireKeyId(DesireKeys.MORPH_TO_SUNKEN_COLONY.getId())
      .parametersTypesForFacts(Collections.singleton(BASE_TO_MOVE))
      .build();

  //for all units
  public static final DesireKey SURROUNDING_UNITS_AND_LOCATION = DesireKey.builder()
      .desireKeyId(DesireKeys.SURROUNDING_UNITS_AND_LOCATION.getId())
      .build();

  //for worker
  public static final DesireKey UPDATE_BELIEFS_ABOUT_WORKER_ACTIVITIES = DesireKey.builder()
      .desireKeyId(DesireKeys.UPDATE_BELIEFS_ABOUT_WORKER_ACTIVITIES.getId())
      .build();
  public static final DesireKey GO_TO_BASE = DesireKey.builder()
      .desireKeyId(DesireKeys.GO_TO_BASE.getId())
      .build();
  public static final DesireKey RETURN_TO_BASE = DesireKey.builder()
      .desireKeyId(DesireKeys.RETURN_TO_BASE.getId())
      .build();
  public static final DesireKey STOP_BUILD = DesireKey.builder()
      .desireKeyId(DesireKeys.STOP_BUILD.getId())
      .build();
  public static final DesireKey FIND_PLACE_FOR_POOL = DesireKey.builder()
      .desireKeyId(DesireKeys.FIND_PLACE_FOR_POOL.getId())
      .build();
  public static final DesireKey FIND_PLACE_FOR_CREEP_COLONY = DesireKey.builder()
      .desireKeyId(DesireKeys.FIND_PLACE_FOR_CREEP_COLONY.getId())
      .build();
  public static final DesireKey FIND_PLACE_FOR_HATCHERY = DesireKey.builder()
      .desireKeyId(DesireKeys.FIND_PLACE_FOR_HATCHERY.getId())
      .build();
  public static final DesireKey FIND_PLACE_FOR_EXTRACTOR = DesireKey.builder()
      .desireKeyId(DesireKeys.FIND_PLACE_FOR_EXTRACTOR.getId())
      .build();
  public static final DesireKey FIND_PLACE_FOR_SPIRE = DesireKey.builder()
      .desireKeyId(DesireKeys.FIND_PLACE_FOR_SPIRE.getId())
      .build();
  public static final DesireKey FIND_PLACE_FOR_EVOLUTION_CHAMBER = DesireKey.builder()
      .desireKeyId(DesireKeys.FIND_PLACE_FOR_EVOLUTION_CHAMBER.getId())
      .build();
  public static final DesireKey FIND_PLACE_FOR_HYDRALISK_DEN = DesireKey.builder()
      .desireKeyId(DesireKeys.FIND_PLACE_FOR_HYDRALISK_DEN.getId())
      .build();
  public static final DesireKey SELECT_MINERAL = DesireKey.builder()
      .desireKeyId(DesireKeys.SELECT_MINERAL.getId())
      .build();
  public static final DesireKey MINE_MINERALS = DesireKey.builder()
      .desireKeyId(DesireKeys.MINE_MINERAL.getId())
      .parametersTypesForFacts(Collections.singleton(IS_BASE_LOCATION))
      .parametersTypesForFacts(Collections.singleton(MINERAL_TO_MINE))
      .build();
  public static final DesireKey UNSELECT_MINERAL = DesireKey.builder()
      .desireKeyId(DesireKeys.UNSELECT_MINERAL.getId())
      .build();
  public static final DesireKey MINE_GAS = DesireKey.builder()
      .desireKeyId(DesireKeys.MINE_GAS.getId())
      .build();
  public static final DesireKey BUILD = DesireKey.builder()
      .desireKeyId(DesireKeys.BUILD.getId())
      .build();

  //for buildings
  public static final DesireKey UPDATE_BELIEFS_ABOUT_CONSTRUCTION = DesireKey.builder()
      .desireKeyId(DesireKeys.UPDATE_BELIEFS_ABOUT_CONSTRUCTION.getId())
      .build();

  //units
  public static final DesireKey MOVE_AWAY_FROM_DANGER = DesireKey.builder()
      .desireKeyId(DesireKeys.MOVE_AWAY_FROM_DANGER.getId())
      .build();
  public static final DesireKey MOVE_TO_POSITION = DesireKey.builder()
      .desireKeyId(DesireKeys.MOVE_TO_POSITION.getId())
      .build();
  public static final DesireKey ATTACK = DesireKey.builder()
      .desireKeyId(DesireKeys.ATTACK.getId())
      .build();

}
