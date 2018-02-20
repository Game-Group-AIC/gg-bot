package aic.gas.sc.gg_bot.abstract_bot.model.bot;

import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enumeration of all IDs for desires as static classes
 */
public class DesireKeys {

  /**
   * LEARNT DESIRES
   */

  //ECO_MANAGER's desires
  public static final DesireKeyID EXPAND = new DesireKeyID("EXPAND", 1);
  public static final DesireKeyID BUILD_EXTRACTOR = new DesireKeyID("BUILD_EXTRACTOR", 2);
  public static final DesireKeyID BUILD_WORKER = new DesireKeyID("BUILD_WORKER", 3);
  public static final DesireKeyID INCREASE_CAPACITY = new DesireKeyID("INCREASE_CAPACITY", 4);

  //BUILDING_ORDER_MANAGER's desires
  public static final DesireKeyID ENABLE_AIR = new DesireKeyID("ENABLE_AIR", 11);
  public static final DesireKeyID ENABLE_GROUND_RANGED = new DesireKeyID("ENABLE_GROUND_RANGED",
      12);
  public static final DesireKeyID ENABLE_STATIC_ANTI_AIR = new DesireKeyID("ENABLE_STATIC_ANTI_AIR",
      13);
  public static final DesireKeyID ENABLE_GROUND_MELEE = new DesireKeyID("ENABLE_GROUND_MELEE", 14);
  public static final DesireKeyID UPGRADE_TO_LAIR = new DesireKeyID("UPGRADE_TO_LAIR", 15);

  //UNIT_ORDER_MANAGER's desires
  public static final DesireKeyID BOOST_AIR = new DesireKeyID("BOOST_AIR", 21);
  public static final DesireKeyID BOOST_GROUND_MELEE = new DesireKeyID("BOOST_GROUND_MELEE", 22);
  public static final DesireKeyID BOOST_GROUND_RANGED = new DesireKeyID("BOOST_GROUND_RANGED", 23);

  //BASE_LOCATION's desires
  public static final DesireKeyID HOLD_GROUND = new DesireKeyID("HOLD_GROUND", 30);
  public static final DesireKeyID HOLD_AIR = new DesireKeyID("HOLD_AIR", 31);

  //defend (base is present)
  public static final DesireKeyID BUILD_CREEP_COLONY = new DesireKeyID("BUILD_CREEP_COLONY", 33);
  public static final DesireKeyID BUILD_SUNKEN_COLONY = new DesireKeyID("BUILD_SUNKEN_COLONY", 34);
  public static final DesireKeyID BUILD_SPORE_COLONY = new DesireKeyID("BUILD_SPORE_COLONY", 35);

  /**
   * HARD-CODED DESIRES
   */

  //desires of agent representing player
  public static final DesireKeyID READ_PLAYERS_DATA = new DesireKeyID("READ_PLAYERS_DATA", 41);
  public static final DesireKeyID ESTIMATE_ENEMY_FORCE_IN_BUILDINGS = new DesireKeyID(
      "ESTIMATE_ENEMY_FORCE_IN_BUILDINGS", 42);
  public static final DesireKeyID ESTIMATE_ENEMY_FORCE_IN_UNITS = new DesireKeyID(
      "ESTIMATE_ENEMY_FORCE_IN_UNITS", 43);
  public static final DesireKeyID ESTIMATE_OUR_FORCE_IN_BUILDINGS = new DesireKeyID(
      "ESTIMATE_OUR_FORCE_IN_BUILDINGS", 44);
  public static final DesireKeyID ESTIMATE_OUR_FORCE_IN_UNITS = new DesireKeyID(
      "ESTIMATE_OUR_FORCE_IN_UNITS", 45);
  public static final DesireKeyID UPDATE_ENEMY_RACE = new DesireKeyID("UPDATE_ENEMY_RACE", 46);
  public static final DesireKeyID REASON_ABOUT_BASES = new DesireKeyID("REASON_ABOUT_BASES", 47);
  public static final DesireKeyID ESTIMATE_ARMY_SUPPLY_RATIO = new DesireKeyID(
      "ESTIMATE_ARMY_SUPPLY_RATIO", 48);

  //desires for agent representing base
  public static final DesireKeyID REASON_ABOUT_BASE_TYPE = new DesireKeyID("REASON_ABOUT_BASE_TYPE",
      51);
  public static final DesireKeyID ECO_STATUS_IN_LOCATION = new DesireKeyID("ECO_STATUS_IN_LOCATION",
      52);
  public static final DesireKeyID ESTIMATE_ENEMY_FORCE_IN_LOCATION = new DesireKeyID(
      "ESTIMATE_ENEMY_FORCE_IN_LOCATION", 53);
  public static final DesireKeyID ESTIMATE_OUR_FORCE_IN_LOCATION = new DesireKeyID(
      "ESTIMATE_OUR_FORCE_IN_LOCATION", 54);
  public static final DesireKeyID FRIENDLIES_IN_LOCATION = new DesireKeyID("FRIENDLIES_IN_LOCATION",
      55);
  public static final DesireKeyID ENEMIES_IN_LOCATION = new DesireKeyID("ENEMIES_IN_LOCATION", 56);
  public static final DesireKeyID MINE_MINERALS_IN_BASE = new DesireKeyID("MINE_MINERALS_IN_BASE",
      57);
  public static final DesireKeyID MINE_GAS_IN_BASE = new DesireKeyID("MINE_GAS_IN_BASE", 58);
  public static final DesireKeyID REASON_ABOUT_OUR_BASE = new DesireKeyID("REASON_ABOUT_OUR_BASE",
      59);
  public static final DesireKeyID REASON_ABOUT_ENEMY_BASE_WEAKNESSES = new DesireKeyID(
      "REASON_ABOUT_ENEMY_BASE_WEAKNESSES", 60);
  public static final DesireKeyID REASON_ABOUT_DISTANCES = new DesireKeyID(
      "REASON_ABOUT_DISTANCES", 61);
  public static final DesireKeyID REASON_ABOUT_SUFFERING_AND_INFLICTING_DMG = new DesireKeyID(
      "REASON_ABOUT_SUFFERING_AND_INFLICTING_DMG", 62);
  public static final DesireKeyID REASON_GLOBAL_SUPPLY_VS_LOCAL_ENEMIES_RATIO = new DesireKeyID(
      "REASON_GLOBAL_SUPPLY_VS_LOCAL_ENEMIES_RATIO", 63);

  //desires for agent's representing unit
  public static final DesireKeyID SURROUNDING_UNITS_AND_LOCATION = new DesireKeyID(
      "SURROUNDING_UNITS_AND_LOCATION", 101);

  //desire - morphing
  public static final DesireKeyID MORPHING_TO = new DesireKeyID("MORPHING_TO", 151);
  public static final DesireKeyID MORPH_TO_DRONE = new DesireKeyID("MORPH_TO_DRONE", 152);
  public static final DesireKeyID MORPH_TO_POOL = new DesireKeyID("MORPH_TO_POOL", 153);
  public static final DesireKeyID MORPH_TO_OVERLORD = new DesireKeyID("MORPH_TO_OVERLORD", 154);
  public static final DesireKeyID MORPH_TO_EXTRACTOR = new DesireKeyID("MORPH_TO_EXTRACTOR", 155);
  public static final DesireKeyID MORPH_TO_SPIRE = new DesireKeyID("MORPH_TO_SPIRE", 156);
  public static final DesireKeyID MORPH_TO_HYDRALISK_DEN = new DesireKeyID("MORPH_TO_HYDRALISK_DEN",
      157);
  public static final DesireKeyID MORPH_TO_SPORE_COLONY = new DesireKeyID("MORPH_TO_SPORE_COLONY",
      158);
  public static final DesireKeyID MORPH_TO_CREEP_COLONY = new DesireKeyID("MORPH_TO_CREEP_COLONY",
      159);
  public static final DesireKeyID MORPH_TO_SUNKEN_COLONY = new DesireKeyID("MORPH_TO_SUNKEN_COLONY",
      160);
  public static final DesireKeyID MORPH_TO_EVOLUTION_CHAMBER = new DesireKeyID(
      "MORPH_TO_EVOLUTION_CHAMBER", 161);

  //building desires
  public static final Set<DesireKeyID> BUILDING_DESIRE_KEYS = Stream
      .of(MORPH_TO_POOL, MORPH_TO_EXTRACTOR, MORPH_TO_SPIRE, MORPH_TO_HYDRALISK_DEN,
          MORPH_TO_CREEP_COLONY, MORPH_TO_EVOLUTION_CHAMBER).collect(Collectors.toSet());

  //desires for worker
  public static final DesireKeyID UPDATE_BELIEFS_ABOUT_WORKER_ACTIVITIES = new DesireKeyID(
      "UPDATE_BELIEFS_ABOUT_WORKER_ACTIVITIES", 201);
  public static final DesireKeyID GO_TO_BASE = new DesireKeyID("GO_TO_BASE", 202);
  public static final DesireKeyID RETURN_TO_BASE = new DesireKeyID("RETURN_TO_BASE", 200);
  public static final DesireKeyID STOP_BUILD = new DesireKeyID("STOP_BUILD", 199);
  public static final DesireKeyID FIND_PLACE_FOR_POOL = new DesireKeyID("FIND_PLACE_FOR_POOL", 203);
  public static final DesireKeyID FIND_PLACE_FOR_HATCHERY = new DesireKeyID(
      "FIND_PLACE_FOR_HATCHERY", 204);
  public static final DesireKeyID FIND_PLACE_FOR_EXTRACTOR = new DesireKeyID(
      "FIND_PLACE_FOR_EXTRACTOR", 205);
  public static final DesireKeyID MINE_MINERAL = new DesireKeyID("MINE_MINERAL", 206);
  public static final DesireKeyID SELECT_MINERAL = new DesireKeyID("SELECT_MINERAL", 207);
  public static final DesireKeyID UNSELECT_MINERAL = new DesireKeyID("UNSELECT_MINERAL", 208);
  public static final DesireKeyID MINE_GAS = new DesireKeyID("MINE_GAS", 209);
  public static final DesireKeyID FIND_PLACE_FOR_SPIRE = new DesireKeyID("FIND_PLACE_FOR_SPIRE",
      210);
  public static final DesireKeyID FIND_PLACE_FOR_HYDRALISK_DEN = new DesireKeyID(
      "FIND_PLACE_FOR_HYDRALISK_DEN", 211);
  public static final DesireKeyID FIND_PLACE_FOR_CREEP_COLONY = new DesireKeyID(
      "FIND_PLACE_FOR_CREEP_COLONY", 212);
  public static final DesireKeyID FIND_PLACE_FOR_EVOLUTION_CHAMBER = new DesireKeyID(
      "FIND_PLACE_FOR_EVOLUTION_CHAMBER", 213);
  public static final DesireKeyID BUILD = new DesireKeyID("BUILD", 215);

  //desires for buildings
  public static final DesireKeyID UPDATE_BELIEFS_ABOUT_CONSTRUCTION = new DesireKeyID(
      "UPDATE_BELIEFS_ABOUT_CONSTRUCTION", 251);

  //scouting
  public static final DesireKeyID VISIT = new DesireKeyID("VISIT", 351);
  public static final DesireKeyID WORKER_SCOUT = new DesireKeyID("WORKER_SCOUT", 352);

  //units
  public static final DesireKeyID MOVE_AWAY_FROM_DANGER = new DesireKeyID("MOVE_AWAY_FROM_DANGER",
      402);
  public static final DesireKeyID MOVE_TO_POSITION = new DesireKeyID("MOVE_TO_POSITION", 403);
  public static final DesireKeyID ATTACK = new DesireKeyID("ATTACK", 404);

  //learnt desires
  public static final Set<DesireKeyID> LEARNT_DESIRE_KEYS = Stream
      .of(EXPAND, BUILD_EXTRACTOR, BUILD_WORKER, INCREASE_CAPACITY, ENABLE_AIR,
          ENABLE_GROUND_RANGED, ENABLE_STATIC_ANTI_AIR, ENABLE_GROUND_MELEE, UPGRADE_TO_LAIR,
          BOOST_AIR, BOOST_GROUND_MELEE, BOOST_GROUND_RANGED, HOLD_GROUND, HOLD_AIR,
          BUILD_SUNKEN_COLONY, BUILD_SPORE_COLONY).collect(
          Collectors.toSet());
}
