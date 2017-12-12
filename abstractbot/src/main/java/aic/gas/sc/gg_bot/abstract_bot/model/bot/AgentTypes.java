package aic.gas.sc.gg_bot.abstract_bot.model.bot;

import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;

/**
 * Enumeration of all IDs for agents' types as static classes
 */
public class AgentTypes {

  //ground units
  public static final AgentTypeID ZERGLING = new AgentTypeID("ZERGLING", 1);
  public static final AgentTypeID EGG = new AgentTypeID("EGG", 2);
  public static final AgentTypeID LARVA = new AgentTypeID("LARVA", 3);
  public static final AgentTypeID DRONE = new AgentTypeID("DRONE", 4);
  public static final AgentTypeID HYDRALISK = new AgentTypeID("HYDRALISK", 5);

  //air units
  public static final AgentTypeID OVERLORD = new AgentTypeID("OVERLORD", 101);
  public static final AgentTypeID MUTALISK = new AgentTypeID("MUTALISK", 102);

  //buildings
  public static final AgentTypeID HATCHERY = new AgentTypeID("HATCHERY", 201);
  public static final AgentTypeID SPAWNING_POOL = new AgentTypeID("SPAWNING_POOL", 202);
  public static final AgentTypeID CREEP_COLONY = new AgentTypeID("CREEP_COLONY", 203);
  public static final AgentTypeID SUNKEN_COLONY = new AgentTypeID("SUNKEN_COLONY", 204);
  public static final AgentTypeID SPORE_COLONY = new AgentTypeID("SPORE_COLONY", 205);
  public static final AgentTypeID EXTRACTOR = new AgentTypeID("EXTRACTOR", 206);
  public static final AgentTypeID EVOLUTION_CHAMBER = new AgentTypeID("EVOLUTION_CHAMBER", 207);
  public static final AgentTypeID HYDRALISK_DEN = new AgentTypeID("HYDRALISK_DEN", 208);
  public static final AgentTypeID LAIR = new AgentTypeID("LAIR", 209);
  public static final AgentTypeID SPIRE = new AgentTypeID("SPIRE", 210);

  //related to player/abstract
  public static final AgentTypeID PLAYER = new AgentTypeID("PLAYER", 301);
  public static final AgentTypeID ECO_MANAGER = new AgentTypeID("ECO_MANAGER", 302);
  public static final AgentTypeID BUILDING_ORDER_MANAGER = new AgentTypeID("BUILDING_ORDER_MANAGER",
      303);
  public static final AgentTypeID UNIT_ORDER_MANAGER = new AgentTypeID("UNIT_ORDER_MANAGER", 304);

  //tight to place
  public static final AgentTypeID BASE_LOCATION = new AgentTypeID("BASE_LOCATION", 401);

}
