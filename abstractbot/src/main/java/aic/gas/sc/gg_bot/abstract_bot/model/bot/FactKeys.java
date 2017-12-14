package aic.gas.sc.gg_bot.abstract_bot.model.bot;

import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import aic.gas.sc.gg_bot.abstract_bot.model.TypeWrapperStrategy;
import aic.gas.sc.gg_bot.abstract_bot.model.UnitTypeStatus;
import aic.gas.sc.gg_bot.abstract_bot.model.UpgradeTypeStatus;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APosition;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ATechTypeWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ATilePosition;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitOfPlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitWithCommands;

/**
 * Basic fact keys - used in agent to relate them with representation
 */
public class FactKeys {

  //for eco manager
  public static final FactKey<Integer> LAST_TIME_EXTRACTOR_BUILD = new FactKey<Integer>(
      "LAST_TIME_EXTRACTOR_BUILD", false) {
    @Override
    public Integer getInitValue() {
      return null;
    }
  };
  public static final FactKey<Integer> LAST_TIME_HATCHERY_BUILD = new FactKey<Integer>(
      "LAST_TIME_HATCHERY_BUILD", false) {
    @Override
    public Integer getInitValue() {
      return null;
    }
  };
  public static final FactKey<Integer> LAST_TIME_OVERLORD_TRAINED = new FactKey<Integer>(
      "LAST_TIME_OVERLORD_TRAINED", false) {
    @Override
    public Integer getInitValue() {
      return null;
    }
  };

  //Facts for base
  public static final FactKey<Integer> LAST_TIME_GAS_COMMAND = new FactKey<Integer>(
      "LAST_TIME_GAS_COMMAND", false) {
    @Override
    public Integer getInitValue() {
      return null;
    }
  };
  public static final FactKey<AUnitOfPlayer> WORKER_ON_BASE = new FactKey<AUnitOfPlayer>(
      "WORKER_ON_BASE", false) {
    @Override
    public AUnitOfPlayer getInitValue() {
      return null;
    }
  };
  public static final FactKey<AUnitOfPlayer> WORKER_MINING_GAS = new FactKey<AUnitOfPlayer>(
      "WORKER_MINING_GAS", false) {
    @Override
    public AUnitOfPlayer getInitValue() {
      return null;
    }
  };
  public static final FactKey<AUnitOfPlayer> WORKER_MINING_MINERALS = new FactKey<AUnitOfPlayer>(
      "WORKER_MINING_GAS", false) {
    @Override
    public AUnitOfPlayer getInitValue() {
      return null;
    }
  };
  public static final FactKey<AUnit> MINERAL = new FactKey<AUnit>("MINERAL", false) {
    @Override
    public AUnit getInitValue() {
      return null;
    }
  };
  public static final FactKey<Boolean> IS_BASE = new FactKey<Boolean>("IS_BASE", false) {
    @Override
    public Boolean getInitValue() {
      return false;
    }
  };
  public static final FactKey<AUnitOfPlayer> HAS_BASE = new FactKey<AUnitOfPlayer>("HAS_BASE",
      false) {
    @Override
    public AUnitOfPlayer getInitValue() {
      return null;
    }
  };
  public static final FactKey<Boolean> IS_ENEMY_BASE = new FactKey<Boolean>("IS_ENEMY_BASE",
      false) {
    @Override
    public Boolean getInitValue() {
      return false;
    }
  };
  public static final FactKey<AUnitOfPlayer> HAS_EXTRACTOR = new FactKey<AUnitOfPlayer>(
      "HAS_EXTRACTOR", false) {
    @Override
    public AUnitOfPlayer getInitValue() {
      return null;
    }
  };
  public static final FactKey<Boolean> IS_MINERAL_ONLY = new FactKey<Boolean>("IS_MINERAL_ONLY",
      false) {
    @Override
    public Boolean getInitValue() {
      return false;
    }
  };
  public static final FactKey<Boolean> IS_START_LOCATION = new FactKey<Boolean>("IS_START_LOCATION",
      false) {
    @Override
    public Boolean getInitValue() {
      return false;
    }
  };
  public static final FactKey<Boolean> IS_ISLAND = new FactKey<Boolean>("IS_ISLAND", false) {
    @Override
    public Boolean getInitValue() {
      return false;
    }
  };
  public static final FactKey<ABaseLocationWrapper> IS_BASE_LOCATION = new FactKey<ABaseLocationWrapper>(
      "IS_BASE_LOCATION", false) {
    @Override
    public ABaseLocationWrapper getInitValue() {
      return null;
    }
  };
  public static final FactKey<AUnitOfPlayer> STATIC_DEFENSE = new FactKey<AUnitOfPlayer>(
      "STATIC_DEFENSE", false) {
    @Override
    public AUnitOfPlayer getInitValue() {
      return null;
    }
  };
  public static final FactKey<AUnit.Enemy> ENEMY_UNIT = new FactKey<AUnit.Enemy>("ENEMY_UNIT",
      false) {
    @Override
    public AUnit.Enemy getInitValue() {
      return null;
    }
  };
  public static final FactKey<AUnitOfPlayer> OUR_UNIT = new FactKey<AUnitOfPlayer>("OUR_UNIT",
      false) {
    @Override
    public AUnitOfPlayer getInitValue() {
      return null;
    }
  };
  public static final FactKey<Integer> SUNKEN_COLONY_COUNT = new FactKey<Integer>(
      "SUNKEN_COLONY_COUNT", false) {
    @Override
    public Integer getInitValue() {
      return null;
    }
  };
  public static final FactKey<Integer> CREEP_COLONY_COUNT = new FactKey<Integer>(
      "CREEP_COLONY_COUNT", false) {
    @Override
    public Integer getInitValue() {
      return null;
    }
  };
  public static final FactKey<Integer> SPORE_COLONY_COUNT = new FactKey<Integer>(
      "SPORE_COLONY_COUNT", false) {
    @Override
    public Integer getInitValue() {
      return null;
    }
  };
  public static final FactKey<Integer> LAST_SUNKEN_COLONY_BUILDING_TIME = new FactKey<Integer>(
      "LAST_SUNKEN_COLONY_BUILDING_TIME", false) {
    @Override
    public Integer getInitValue() {
      return null;
    }
  };
  public static final FactKey<Integer> LAST_CREEP_COLONY_BUILDING_TIME = new FactKey<Integer>(
      "LAST_CREEP_COLONY_BUILDING_TIME", false) {
    @Override
    public Integer getInitValue() {
      return null;
    }
  };
  public static final FactKey<Integer> LAST_SPORE_COLONY_BUILDING_TIME = new FactKey<Integer>(
      "LAST_SPORE_COLONY_BUILDING_TIME", false) {
    @Override
    public Integer getInitValue() {
      return null;
    }
  };
  public static final FactKey<AUnit> GEYSER = new FactKey<AUnit>("GEYSER", false) {
    @Override
    public AUnit getInitValue() {
      return null;
    }
  };

  //facts for workers
  public static final FactKey<Boolean> IS_GATHERING_MINERALS = new FactKey<Boolean>(
      "IS_GATHERING_MINERALS", false) {
    @Override
    public Boolean getInitValue() {
      return false;
    }
  };
  public static final FactKey<Boolean> IS_GATHERING_GAS = new FactKey<Boolean>("IS_GATHERING_GAS",
      false) {
    @Override
    public Boolean getInitValue() {
      return false;
    }
  };
  public static final FactKey<Integer> IDLE_SINCE = new FactKey<Integer>("IDLE_SINCE", false) {
    @Override
    public Integer getInitValue() {
      return null;
    }
  };

  //surrounding enemy units for agent
  public static final FactKey<AUnit.Enemy> ENEMY_BUILDING = new FactKey<AUnit.Enemy>(
      "ENEMY_BUILDING", false) {
    @Override
    public AUnit.Enemy getInitValue() {
      return null;
    }
  };
  public static final FactKey<AUnit.Enemy> ENEMY_AIR = new FactKey<AUnit.Enemy>("ENEMY_AIR",
      false) {
    @Override
    public AUnit.Enemy getInitValue() {
      return null;
    }
  };
  public static final FactKey<AUnit.Enemy> ENEMY_GROUND = new FactKey<AUnit.Enemy>("ENEMY_GROUND",
      false) {
    @Override
    public AUnit.Enemy getInitValue() {
      return null;
    }
  };
  //surrounding own units for agent
  public static final FactKey<AUnitOfPlayer> OWN_BUILDING = new FactKey<AUnitOfPlayer>(
      "OWN_BUILDING", false) {
    @Override
    public AUnitOfPlayer getInitValue() {
      return null;
    }
  };
  public static final FactKey<AUnitOfPlayer> OWN_AIR = new FactKey<AUnitOfPlayer>("OWN_AIR",
      false) {
    @Override
    public AUnitOfPlayer getInitValue() {
      return null;
    }
  };
  public static final FactKey<AUnitOfPlayer> OWN_GROUND = new FactKey<AUnitOfPlayer>("OWN_GROUND",
      false) {
    @Override
    public AUnitOfPlayer getInitValue() {
      return null;
    }
  };

  //for eggs
  public static final FactKey<AUnitTypeWrapper> IS_MORPHING_TO = new FactKey<AUnitTypeWrapper>(
      "IS_MORPHING_TO", false) {
    @Override
    public AUnitTypeWrapper getInitValue() {
      return null;
    }
  };
  public static final FactKey<TypeWrapperStrategy> MORPH_TO = new FactKey<TypeWrapperStrategy>(
      "MORPH_TO_DRONE", false) {
    @Override
    public TypeWrapperStrategy getInitValue() {
      return null;
    }
  };

  //for buildings
  public static final FactKey<Boolean> IS_BEING_CONSTRUCTED = new FactKey<Boolean>(
      "IS_BEING_CONSTRUCTED", false) {
    @Override
    public Boolean getInitValue() {
      return false;
    }
  };

  //for player - general facts about the game
  public static final FactKey<Double> AVAILABLE_MINERALS = new FactKey<Double>("AVAILABLE_MINERALS",
      false) {
    @Override
    public Double getInitValue() {
      return null;
    }
  };
  public static final FactKey<Double> AVAILABLE_GAS = new FactKey<Double>("AVAILABLE_GAS", false) {
    @Override
    public Double getInitValue() {
      return null;
    }
  };
  public static final FactKey<Double> POPULATION_LIMIT = new FactKey<Double>("POPULATION_LIMIT",
      false) {
    @Override
    public Double getInitValue() {
      return null;
    }
  };
  public static final FactKey<Double> POPULATION = new FactKey<Double>("POPULATION", false) {
    @Override
    public Double getInitValue() {
      return null;
    }
  };
  public static final FactKey<ARace> ENEMY_RACE = new FactKey<ARace>("ENEMY_RACE", false) {
    @Override
    public ARace getInitValue() {

      //start with randomly picked race
      return ARace.getRandomRace();
    }
  };
  public static final FactKey<APlayer> IS_PLAYER = new FactKey<APlayer>("IS_PLAYER", false) {
    @Override
    public APlayer getInitValue() {
      return null;
    }
  };
  public static final FactKey<ABaseLocationWrapper> OUR_BASE = new FactKey<ABaseLocationWrapper>(
      "OUR_BASE", false) {
    @Override
    public ABaseLocationWrapper getInitValue() {
      return null;
    }
  };
  public static final FactKey<ABaseLocationWrapper> ENEMY_BASE = new FactKey<ABaseLocationWrapper>(
      "ENEMY_BASE", false) {
    @Override
    public ABaseLocationWrapper getInitValue() {
      return null;
    }
  };
  public static final FactKey<UpgradeTypeStatus> UPGRADE_STATUS = new FactKey<UpgradeTypeStatus>(
      "UPGRADE_STATUS", false) {
    @Override
    public UpgradeTypeStatus getInitValue() {
      return null;
    }
  };
  public static final FactKey<ATechTypeWrapper> TECH_TO_RESEARCH = new FactKey<ATechTypeWrapper>(
      "TECH_TO_RESEARCH", false) {
    @Override
    public ATechTypeWrapper getInitValue() {
      return null;
    }
  };
  public static final FactKey<UnitTypeStatus> ENEMY_BUILDING_STATUS = new FactKey<UnitTypeStatus>(
      "ENEMY_BUILDING_STATUS", false) {
    @Override
    public UnitTypeStatus getInitValue() {
      return null;
    }
  };
  public static final FactKey<UnitTypeStatus> ENEMY_AIR_FORCE_STATUS = new FactKey<UnitTypeStatus>(
      "ENEMY_AIR_FORCE_STATUS", false) {
    @Override
    public UnitTypeStatus getInitValue() {
      return null;
    }
  };
  public static final FactKey<UnitTypeStatus> ENEMY_GROUND_FORCE_STATUS = new FactKey<UnitTypeStatus>(
      "ENEMY_GROUND_FORCE_STATUS", false) {
    @Override
    public UnitTypeStatus getInitValue() {
      return null;
    }
  };
  public static final FactKey<UnitTypeStatus> OWN_BUILDING_STATUS = new FactKey<UnitTypeStatus>(
      "OWN_BUILDING_STATUS", false) {
    @Override
    public UnitTypeStatus getInitValue() {
      return null;
    }
  };
  public static final FactKey<UnitTypeStatus> OWN_AIR_FORCE_STATUS = new FactKey<UnitTypeStatus>(
      "OWN_AIR_FORCE_STATUS", false) {
    @Override
    public UnitTypeStatus getInitValue() {
      return null;
    }
  };
  public static final FactKey<UnitTypeStatus> OWN_GROUND_FORCE_STATUS = new FactKey<UnitTypeStatus>(
      "OWN_GROUND_FORCE_STATUS", false) {
    @Override
    public UnitTypeStatus getInitValue() {
      return null;
    }
  };
  public static final FactKey<UnitTypeStatus> ENEMY_STATIC_AIR_FORCE_STATUS = new FactKey<UnitTypeStatus>(
      "ENEMY_STATIC_AIR_FORCE_STATUS", false) {
    @Override
    public UnitTypeStatus getInitValue() {
      return null;
    }
  };
  public static final FactKey<UnitTypeStatus> ENEMY_STATIC_GROUND_FORCE_STATUS = new FactKey<UnitTypeStatus>(
      "ENEMY_STATIC_GROUND_FORCE_STATUS", false) {
    @Override
    public UnitTypeStatus getInitValue() {
      return null;
    }
  };
  public static final FactKey<UnitTypeStatus> OWN_STATIC_AIR_FORCE_STATUS = new FactKey<UnitTypeStatus>(
      "OWN_STATIC_AIR_FORCE_STATUS", false) {
    @Override
    public UnitTypeStatus getInitValue() {
      return null;
    }
  };
  public static final FactKey<UnitTypeStatus> OWN_STATIC_GROUND_FORCE_STATUS = new FactKey<UnitTypeStatus>(
      "OWN_STATIC_GROUND_FORCE_STATUS", false) {
    @Override
    public UnitTypeStatus getInitValue() {
      return null;
    }
  };
  public static final FactKey<AUnitTypeWrapper> LOCKED_UNITS = new FactKey<AUnitTypeWrapper>(
      "LOCKED_UNITS", false) {
    @Override
    public AUnitTypeWrapper getInitValue() {
      return null;
    }
  };
  public static final FactKey<AUnitTypeWrapper> LOCKED_BUILDINGS = new FactKey<AUnitTypeWrapper>(
      "LOCKED_BUILDINGS", false) {
    @Override
    public AUnitTypeWrapper getInitValue() {
      return null;
    }
  };


  //worker
  public static final FactKey<APosition> PLACE_TO_GO = new FactKey<APosition>("PLACE_TO_GO", true) {
    @Override
    public APosition getInitValue() {
      return null;
    }
  };
  public static final FactKey<ATilePosition> PLACE_FOR_CREEP_COLONY = new FactKey<ATilePosition>(
      "PLACE_FOR_CREEP_COLONY", false) {
    @Override
    public ATilePosition getInitValue() {
      return null;
    }
  };
  public static final FactKey<ATilePosition> PLACE_FOR_SPIRE = new FactKey<ATilePosition>(
      "PLACE_FOR_SPIRE", false) {
    @Override
    public ATilePosition getInitValue() {
      return null;
    }
  };
  public static final FactKey<ATilePosition> PLACE_FOR_HYDRALISK_DEN = new FactKey<ATilePosition>(
      "PLACE_FOR_HYDRALISK_DEN", false) {
    @Override
    public ATilePosition getInitValue() {
      return null;
    }
  };
  public static final FactKey<ATilePosition> PLACE_FOR_POOL = new FactKey<ATilePosition>(
      "PLACE_FOR_POOL", false) {
    @Override
    public ATilePosition getInitValue() {
      return null;
    }
  };
  public static final FactKey<ATilePosition> PLACE_FOR_EVOLUTION_CHAMBER = new FactKey<ATilePosition>(
      "PLACE_FOR_EVOLUTION_CHAMBER", false) {
    @Override
    public ATilePosition getInitValue() {
      return null;
    }
  };
  public static final FactKey<ATilePosition> PLACE_FOR_EXTRACTOR = new FactKey<ATilePosition>(
      "PLACE_FOR_EXTRACTOR", false) {
    @Override
    public ATilePosition getInitValue() {
      return null;
    }
  };
  public static final FactKey<ATilePosition> PLACE_FOR_EXPANSION = new FactKey<ATilePosition>(
      "PLACE_FOR_EXPANSION", false) {
    @Override
    public ATilePosition getInitValue() {
      return null;
    }
  };
  public static final FactKey<ABaseLocationWrapper> BASE_TO_MOVE = new FactKey<ABaseLocationWrapper>(
      "BASE_TO_MOVE", false) {
    @Override
    public ABaseLocationWrapper getInitValue() {
      return null;
    }
  };
  public static final FactKey<ABaseLocationWrapper> BASE_TO_SCOUT_BY_WORKER = new FactKey<ABaseLocationWrapper>(
      "BASE_TO_SCOUT_BY_WORKER", true) {
    @Override
    public ABaseLocationWrapper getInitValue() {
      return null;
    }
  };
  public static final FactKey<AUnit> MINING_MINERAL = new FactKey<AUnit>("MINING_MINERAL", false) {
    @Override
    public AUnit getInitValue() {
      return null;
    }
  };
  public static final FactKey<AUnit> MINERAL_TO_MINE = new FactKey<AUnit>("MINERAL_TO_MINE", true) {
    @Override
    public AUnit getInitValue() {
      return null;
    }
  };
  public static final FactKey<AUnitOfPlayer> MINING_IN_EXTRACTOR = new FactKey<AUnitOfPlayer>(
      "MINING_IN_EXTRACTOR", false) {
    @Override
    public AUnitOfPlayer getInitValue() {
      return null;
    }
  };
  public static final FactKey<Integer> BUILDING_LAST_CHECK = new FactKey<Integer>(
      "BUILDING_LAST_CHECK", false) {
    @Override
    public Integer getInitValue() {
      return null;
    }
  };
  public static final FactKey<Boolean> HAS_ENOUGH_RESOURCES = new FactKey<Boolean>(
      "HAS_ENOUGH_RESOURCES", false) {
    @Override
    public Boolean getInitValue() {
      return false;
    }
  };

  //scouting
  public static final FactKey<Integer> LAST_TIME_SCOUTED = new FactKey<Integer>("LAST_TIME_SCOUTED",
      false) {
    @Override
    public Integer getInitValue() {
      return null;
    }
  };

  //units
  public static final FactKey<Boolean> IS_UNDER_ATTACK = new FactKey<Boolean>("IS_UNDER_ATTACK",
      false) {
    @Override
    public Boolean getInitValue() {
      return false;
    }
  };
  public static final FactKey<APosition> PLACE_TO_REACH = new FactKey<APosition>("PLACE_TO_REACH",
      false) {
    @Override
    public APosition getInitValue() {
      return null;
    }
  };
  public static final FactKey<Integer> TIME_OF_HOLD_COMMAND = new FactKey<Integer>(
      "TIME_OF_HOLD_COMMAND", false) {
    @Override
    public Integer getInitValue() {
      return null;
    }
  };

  //general
  public static final FactKey<ABaseLocationWrapper> LOCATION = new FactKey<ABaseLocationWrapper>(
      "LOCATION", false) {
    @Override
    public ABaseLocationWrapper getInitValue() {
      return null;
    }
  };
  public static final FactKey<AUnitOfPlayer> REPRESENTS_UNIT = new FactKey<AUnitOfPlayer>(
      "REPRESENTS_UNIT", false) {
    @Override
    public AUnitOfPlayer getInitValue() {
      return null;
    }
  };
  public static final FactKey<Integer> MADE_OBSERVATION_IN_FRAME = new FactKey<Integer>(
      "MADE_OBSERVATION_IN_FRAME", false) {
    @Override
    public Integer getInitValue() {
      return null;
    }
  };
  public static final FactKey<ABaseLocationWrapper> HOLD_LOCATION = new FactKey<ABaseLocationWrapper>(
      "HOLD_LOCATION", false) {
    @Override
    public ABaseLocationWrapper getInitValue() {
      return null;
    }
  };
  public static final FactKey<AUnitWithCommands> IS_UNIT = new FactKey<AUnitWithCommands>("IS_UNIT",
      true) {
    @Override
    public AUnitWithCommands getInitValue() {
      return null;
    }
  };

  ////////HACK - 5 POOL, this may help bot in some scenarios (but in most it is counter productive)
  public static final FactKey<Integer> MORPHED_WORKERS = new FactKey<Integer>("MORPHED_WORKERS",
      true) {
    @Override
    public Integer getInitValue() {
      return 0;
    }
  };
  public static final FactKey<Boolean> TRANSIT_FROM_5_POOL = new FactKey<Boolean>(
      "TRANSIT_FROM_5_POOL", false) {
    @Override
    public Boolean getInitValue() {
      //set true to turn off 5 pool
      return true;
    }
  };
  public static final FactKey<Boolean> WERE_6_LINGS_MORPHED = new FactKey<Boolean>(
      "WERE_6_LINGS_MORPHED", true) {
    @Override
    public Boolean getInitValue() {
      return false;
    }
  };
  public static final FactKey<AUnitOfPlayer> LING = new FactKey<AUnitOfPlayer>("LING", true) {
    @Override
    public AUnitOfPlayer getInitValue() {
      return null;
    }
  };
  public static final FactKey<Integer> TIME_OF_LAST_DRONE = new FactKey<Integer>(
      "TIME_OF_LAST_DRONE", true) {
    @Override
    public Integer getInitValue() {
      return 0;
    }
  };

}
