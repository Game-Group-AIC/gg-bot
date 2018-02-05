package aic.gas.sc.gg_bot.bot.model;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_MOVE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.HAS_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.HAS_EXTRACTOR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.MINERAL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.MINERAL_TO_MINE;

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
      .id(DesireKeys.READ_PLAYERS_DATA)
      .build();
  public static final DesireKey ESTIMATE_ENEMY_FORCE_IN_BUILDINGS = DesireKey.builder()
      .id(DesireKeys.ESTIMATE_ENEMY_FORCE_IN_BUILDINGS)
      .build();
  public static final DesireKey ESTIMATE_ENEMY_FORCE_IN_UNITS = DesireKey.builder()
      .id(DesireKeys.ESTIMATE_ENEMY_FORCE_IN_UNITS)
      .build();
  public static final DesireKey ESTIMATE_OUR_FORCE_IN_BUILDINGS = DesireKey.builder()
      .id(DesireKeys.ESTIMATE_OUR_FORCE_IN_BUILDINGS)
      .build();
  public static final DesireKey ESTIMATE_OUR_FORCE_IN_UNITS = DesireKey.builder()
      .id(DesireKeys.ESTIMATE_OUR_FORCE_IN_UNITS)
      .build();
  public static final DesireKey UPDATE_ENEMY_RACE = DesireKey.builder()
      .id(DesireKeys.UPDATE_ENEMY_RACE)
      .build();
  public static final DesireKey REASON_ABOUT_BASES = DesireKey.builder()
      .id(DesireKeys.REASON_ABOUT_BASES)
      .build();

  //for eco manager
  public static final DesireKey BUILD_WORKER = DesireKey.builder()
      .id(DesireKeys.BUILD_WORKER)
      .build();
  public static final DesireKey INCREASE_CAPACITY = DesireKey.builder()
      .id(DesireKeys.INCREASE_CAPACITY)
      .build();
  public static final DesireKey EXPAND = DesireKey.builder()
      .id(DesireKeys.EXPAND)
      .parametersTypesForFacts(Collections.singleton(BASE_TO_MOVE))
      .build();
  public static final DesireKey BUILD_EXTRACTOR = DesireKey.builder()
      .id(DesireKeys.BUILD_EXTRACTOR)
      .build();

  //for unit order manager
  public static final DesireKey BOOST_GROUND_MELEE = DesireKey.builder()
      .id(DesireKeys.BOOST_GROUND_MELEE)
      .staticFactValues(Collections
          .singleton(new Fact<>(() -> AUnitTypeWrapper.ZERGLING_TYPE, FactKeys.MORPH_TO)))
      .build();
  public static final DesireKey BOOST_GROUND_RANGED = DesireKey.builder()
      .id(DesireKeys.BOOST_GROUND_RANGED)
      .staticFactValues(Collections
          .singleton(new Fact<>(() -> AUnitTypeWrapper.HYDRALISK_TYPE, FactKeys.MORPH_TO)))
      .build();
  public static final DesireKey BOOST_AIR = DesireKey.builder()
      .id(DesireKeys.BOOST_AIR)
      .staticFactValues(Collections
          .singleton(new Fact<>(() -> AUnitTypeWrapper.MUTALISK_TYPE, FactKeys.MORPH_TO)))
      .build();

  //for build order manager
  public static final DesireKey ENABLE_GROUND_MELEE = DesireKey.builder()
      .id(DesireKeys.ENABLE_GROUND_MELEE)
      .build();
  public static final DesireKey ENABLE_GROUND_RANGED = DesireKey.builder()
      .id(DesireKeys.ENABLE_GROUND_RANGED)
      .build();
  public static final DesireKey ENABLE_STATIC_ANTI_AIR = DesireKey.builder()
      .id(DesireKeys.ENABLE_STATIC_ANTI_AIR)
      .build();
  public static final DesireKey ENABLE_AIR = DesireKey.builder()
      .id(DesireKeys.ENABLE_AIR)
      .build();
  public static final DesireKey UPGRADE_TO_LAIR = DesireKey.builder()
      .id(DesireKeys.UPGRADE_TO_LAIR)
      .build();

  //attack
  public static final DesireKey HOLD_GROUND = DesireKey.builder()
      .id(DesireKeys.HOLD_GROUND)
      .build();
  public static final DesireKey HOLD_AIR = DesireKey.builder()
      .id(DesireKeys.HOLD_AIR)
      .build();
  public static final DesireKey DEFEND = DesireKey.builder()
      .id(DesireKeys.DEFEND)
      .build();

  //for base
  public static final DesireKey ECO_STATUS_IN_LOCATION = DesireKey.builder()
      .id(DesireKeys.ECO_STATUS_IN_LOCATION)
      .build();
  public static final DesireKey REASON_ABOUT_BASE_TYPE = DesireKey.builder()
      .id(DesireKeys.REASON_ABOUT_BASE_TYPE)
      .build();
  public static final DesireKey REASON_ABOUT_OUR_BASE = DesireKey.builder()
      .id(DesireKeys.REASON_ABOUT_OUR_BASE)
      .build();
  public static final DesireKey ESTIMATE_ENEMY_FORCE_IN_LOCATION = DesireKey.builder()
      .id(DesireKeys.ESTIMATE_ENEMY_FORCE_IN_LOCATION)
      .build();
  public static final DesireKey ESTIMATE_OUR_FORCE_IN_LOCATION = DesireKey.builder()
      .id(DesireKeys.ESTIMATE_OUR_FORCE_IN_LOCATION)
      .build();
  public static final DesireKey FRIENDLIES_IN_LOCATION = DesireKey.builder()
      .id(DesireKeys.FRIENDLIES_IN_LOCATION)
      .build();
  public static final DesireKey ENEMIES_IN_LOCATION = DesireKey.builder()
      .id(DesireKeys.ENEMIES_IN_LOCATION)
      .build();
  public static final DesireKey BUILD_SPORE_COLONY = DesireKey.builder()
      .id(DesireKeys.BUILD_SPORE_COLONY)
      .build();
  public static final DesireKey BUILD_CREEP_COLONY = DesireKey.builder()
      .id(DesireKeys.BUILD_SPORE_COLONY)
      .build();
  public static final DesireKey BUILD_SUNKEN_COLONY = DesireKey.builder()
      .id(DesireKeys.BUILD_SUNKEN_COLONY)
      .build();
  public static final DesireKey MINE_MINERALS_IN_BASE = DesireKey.builder()
      .id(DesireKeys.MINE_MINERALS_IN_BASE)
      .parametersTypesForFacts(Collections.singleton(IS_BASE_LOCATION))
      .parametersTypesForFactSets(Stream.of(MINERAL, HAS_BASE)
          .collect(Collectors.toSet()))
      .build();
  public static final DesireKey MINE_GAS_IN_BASE = DesireKey.builder()
      .id(DesireKeys.MINE_GAS_IN_BASE)
      .parametersTypesForFacts(Collections.singleton(IS_BASE_LOCATION))
      .parametersTypesForFactSets(Stream.of(HAS_EXTRACTOR, HAS_BASE)
          .collect(Collectors.toSet()))
      .build();

  //scouting
  public static final DesireKey VISIT = DesireKey.builder()
      .id(DesireKeys.VISIT)
      .parametersTypesForFacts(Collections.singleton(IS_BASE_LOCATION))
      .build();
  public static final DesireKey WORKER_SCOUT = DesireKey.builder()
      .id(DesireKeys.WORKER_SCOUT)
      .build();

  //morphing
  public static final DesireKey MORPHING_TO = DesireKey.builder()
      .id(DesireKeys.MORPHING_TO)
      .build();
  public static final DesireKey MORPH_TO_DRONE = DesireKey.builder()
      .id(DesireKeys.MORPH_TO_DRONE)
      .staticFactValues(Collections
          .singleton(new Fact<>(() -> AUnitTypeWrapper.DRONE_TYPE, FactKeys.MORPH_TO)))
      .build();
  public static final DesireKey MORPH_TO_OVERLORD = DesireKey.builder()
      .id(DesireKeys.MORPH_TO_OVERLORD)
      .staticFactValues(Collections
          .singleton(new Fact<>(() -> AUnitTypeWrapper.OVERLORD_TYPE, FactKeys.MORPH_TO)))
      .build();
  public static final DesireKey MORPH_TO_POOL = DesireKey.builder()
      .id(DesireKeys.MORPH_TO_POOL)
      .parametersTypesForFacts(Collections.singleton(BASE_TO_MOVE))
      .build();
  public static final DesireKey MORPH_TO_SPIRE = DesireKey.builder()
      .id(DesireKeys.MORPH_TO_SPIRE)
      .parametersTypesForFacts(Collections.singleton(BASE_TO_MOVE))
      .build();
  public static final DesireKey MORPH_TO_EVOLUTION_CHAMBER = DesireKey.builder()
      .id(DesireKeys.MORPH_TO_EVOLUTION_CHAMBER)
      .parametersTypesForFacts(Collections.singleton(BASE_TO_MOVE))
      .build();
  public static final DesireKey MORPH_TO_HYDRALISK_DEN = DesireKey.builder()
      .id(DesireKeys.MORPH_TO_HYDRALISK_DEN)
      .parametersTypesForFacts(Collections.singleton(BASE_TO_MOVE))
      .build();
  public static final DesireKey MORPH_TO_EXTRACTOR = DesireKey.builder()
      .id(DesireKeys.MORPH_TO_EXTRACTOR)
      .parametersTypesForFacts(Collections.singleton(BASE_TO_MOVE))
      .build();
  public static final DesireKey MORPH_TO_SPORE_COLONY = DesireKey.builder()
      .id(DesireKeys.MORPH_TO_SPORE_COLONY)
      .parametersTypesForFacts(Collections.singleton(BASE_TO_MOVE))
      .build();
  public static final DesireKey MORPH_TO_CREEP_COLONY = DesireKey.builder()
      .id(DesireKeys.MORPH_TO_CREEP_COLONY)
      .parametersTypesForFacts(Collections.singleton(BASE_TO_MOVE))
      .build();
  public static final DesireKey MORPH_TO_SUNKEN_COLONY = DesireKey.builder()
      .id(DesireKeys.MORPH_TO_SUNKEN_COLONY)
      .parametersTypesForFacts(Collections.singleton(BASE_TO_MOVE))
      .build();

  //for all units
  public static final DesireKey SURROUNDING_UNITS_AND_LOCATION = DesireKey.builder()
      .id(DesireKeys.SURROUNDING_UNITS_AND_LOCATION)
      .build();

  //for worker
  public static final DesireKey UPDATE_BELIEFS_ABOUT_WORKER_ACTIVITIES = DesireKey.builder()
      .id(DesireKeys.UPDATE_BELIEFS_ABOUT_WORKER_ACTIVITIES)
      .build();
  public static final DesireKey GO_TO_BASE = DesireKey.builder()
      .id(DesireKeys.GO_TO_BASE)
      .build();
  public static final DesireKey RETURN_TO_BASE = DesireKey.builder()
      .id(DesireKeys.RETURN_TO_BASE)
      .build();
  public static final DesireKey STOP_BUILD = DesireKey.builder()
      .id(DesireKeys.STOP_BUILD)
      .build();
  public static final DesireKey FIND_PLACE_FOR_POOL = DesireKey.builder()
      .id(DesireKeys.FIND_PLACE_FOR_POOL)
      .build();
  public static final DesireKey FIND_PLACE_FOR_CREEP_COLONY = DesireKey.builder()
      .id(DesireKeys.FIND_PLACE_FOR_CREEP_COLONY)
      .build();
  public static final DesireKey FIND_PLACE_FOR_HATCHERY = DesireKey.builder()
      .id(DesireKeys.FIND_PLACE_FOR_HATCHERY)
      .build();
  public static final DesireKey FIND_PLACE_FOR_EXTRACTOR = DesireKey.builder()
      .id(DesireKeys.FIND_PLACE_FOR_EXTRACTOR)
      .build();
  public static final DesireKey FIND_PLACE_FOR_SPIRE = DesireKey.builder()
      .id(DesireKeys.FIND_PLACE_FOR_SPIRE)
      .build();
  public static final DesireKey FIND_PLACE_FOR_EVOLUTION_CHAMBER = DesireKey.builder()
      .id(DesireKeys.FIND_PLACE_FOR_EVOLUTION_CHAMBER)
      .build();
  public static final DesireKey FIND_PLACE_FOR_HYDRALISK_DEN = DesireKey.builder()
      .id(DesireKeys.FIND_PLACE_FOR_HYDRALISK_DEN)
      .build();
  public static final DesireKey SELECT_MINERAL = DesireKey.builder()
      .id(DesireKeys.SELECT_MINERAL)
      .build();
  public static final DesireKey MINE_MINERALS = DesireKey.builder()
      .id(DesireKeys.MINE_MINERAL)
      .parametersTypesForFacts(Collections.singleton(IS_BASE_LOCATION))
      .parametersTypesForFacts(Collections.singleton(MINERAL_TO_MINE))
      .build();
  public static final DesireKey UNSELECT_MINERAL = DesireKey.builder()
      .id(DesireKeys.UNSELECT_MINERAL)
      .build();
  public static final DesireKey MINE_GAS = DesireKey.builder()
      .id(DesireKeys.MINE_GAS)
      .build();
  public static final DesireKey BUILD = DesireKey.builder()
      .id(DesireKeys.BUILD)
      .build();

  //for buildings
  public static final DesireKey UPDATE_BELIEFS_ABOUT_CONSTRUCTION = DesireKey.builder()
      .id(DesireKeys.UPDATE_BELIEFS_ABOUT_CONSTRUCTION)
      .build();

  //units
  public static final DesireKey MOVE_AWAY_FROM_DANGER = DesireKey.builder()
      .id(DesireKeys.MOVE_AWAY_FROM_DANGER)
      .build();
  public static final DesireKey MOVE_TO_POSITION = DesireKey.builder()
      .id(DesireKeys.MOVE_TO_POSITION)
      .build();
  public static final DesireKey ATTACK = DesireKey.builder()
      .id(DesireKeys.ATTACK)
      .build();

}
