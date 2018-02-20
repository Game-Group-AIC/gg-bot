package aic.gas.sc.gg_bot.abstract_bot.model.bot;

import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import java.io.Serializable;

/**
 * Enumeration of all IDs for agents' types as static classes
 */
public enum AgentTypes implements Serializable {
  //ground units
  ZERGLING,
  EGG,
  LARVA,
  DRONE,
  HYDRALISK,

  //air units
  OVERLORD,
  MUTALISK,

  //buildings
  HATCHERY,
  SPAWNING_POOL,
  CREEP_COLONY,
  SUNKEN_COLONY,
  SPORE_COLONY,
  EXTRACTOR,
  EVOLUTION_CHAMBER,
  HYDRALISK_DEN,
  LAIR,
  SPIRE,

  //related to player/abstract
  PLAYER,
  ECO_MANAGER,
  BUILDING_ORDER_MANAGER,
  UNIT_ORDER_MANAGER,

  //tight to place
  BASE_LOCATION;


  public AgentTypeID getId() {
    return new AgentTypeID(this.name(), this.ordinal());
  }
}
