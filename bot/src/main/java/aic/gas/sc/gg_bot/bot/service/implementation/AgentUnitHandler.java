package aic.gas.sc.gg_bot.bot.service.implementation;

import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.CREEP_COLONY_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.DRONE_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.EGG_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.EVOLUTION_CHAMBER_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.EXTRACTOR_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.HATCHERY_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.HYDRALISK_DEN_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.HYDRALISK_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.LAIR_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.LARVA_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.MUTALISK_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.OVERLORD_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.SPAWNING_POOL_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.SPIRE_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.SPORE_COLONY_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.SUNKEN_COLONY_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.ZERGLING_TYPE;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitWithCommands;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.UnitWrapperFactory;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.WrapperTypeFactory;
import aic.gas.sc.gg_bot.bot.model.agent.AgentUnit;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import aic.gas.sc.gg_bot.bot.model.agent.types.implementation.buildings.CreepColonyAgentType;
import aic.gas.sc.gg_bot.bot.model.agent.types.implementation.buildings.EvolutionChamberAgentType;
import aic.gas.sc.gg_bot.bot.model.agent.types.implementation.buildings.ExtractorAgentType;
import aic.gas.sc.gg_bot.bot.model.agent.types.implementation.buildings.HatcheryAgentType;
import aic.gas.sc.gg_bot.bot.model.agent.types.implementation.buildings.HydraliskDenAgentType;
import aic.gas.sc.gg_bot.bot.model.agent.types.implementation.buildings.LairAgentType;
import aic.gas.sc.gg_bot.bot.model.agent.types.implementation.buildings.SpawningPoolAgentType;
import aic.gas.sc.gg_bot.bot.model.agent.types.implementation.buildings.SpireAgentType;
import aic.gas.sc.gg_bot.bot.model.agent.types.implementation.buildings.SporeColonyAgentType;
import aic.gas.sc.gg_bot.bot.model.agent.types.implementation.buildings.SunkenColonyAgentType;
import aic.gas.sc.gg_bot.bot.model.agent.types.implementation.units.DroneAgentType;
import aic.gas.sc.gg_bot.bot.model.agent.types.implementation.units.EggAgentType;
import aic.gas.sc.gg_bot.bot.model.agent.types.implementation.units.HydraliskAgentType;
import aic.gas.sc.gg_bot.bot.model.agent.types.implementation.units.LarvaAgentType;
import aic.gas.sc.gg_bot.bot.model.agent.types.implementation.units.MutaliskAgentType;
import aic.gas.sc.gg_bot.bot.model.agent.types.implementation.units.OverlordAgentType;
import aic.gas.sc.gg_bot.bot.model.agent.types.implementation.units.ZerglingAgentType;
import aic.gas.sc.gg_bot.bot.service.IAgentUnitHandler;
import bwapi.Unit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory to create agent for given unit
 */
@Slf4j
public class AgentUnitHandler implements IAgentUnitHandler {

  private final Map<AUnitTypeWrapper, AgentTypeUnit> agentConfigurationForUnitType = new HashMap<>();

  @Override
  public Optional<AgentUnit> createAgentForUnit(Unit unit, BotFacade botFacade, int frameCount) {
    Optional<AgentTypeUnit> agentTypeUnit = getFromConfiguration(
        WrapperTypeFactory.createFrom(unit.getType()));
    if (agentTypeUnit.isPresent()) {
      Optional<AUnitWithCommands> wrappedUnit = Optional.ofNullable(
          UnitWrapperFactory.getCurrentWrappedUnitToCommand(unit, frameCount, false));
      if (!wrappedUnit.isPresent()) {
        log.error("Could not initiate unit " + unit.getType());
        throw new RuntimeException("Could not initiate unit " + unit.getType());
      }
      AgentUnit agent = new AgentUnit(agentTypeUnit.get(), botFacade, wrappedUnit.get());
      return Optional.of(agent);
    }
    return Optional.empty();
  }

  private Optional<AgentTypeUnit> getFromConfiguration(AUnitTypeWrapper unitTypeWrapper) {
    AgentTypeUnit agentTypeUnit = agentConfigurationForUnitType.get(unitTypeWrapper);
    if (agentTypeUnit == null) {
      if (unitTypeWrapper.equals(DRONE_TYPE)) {
        agentConfigurationForUnitType.put(DRONE_TYPE, DroneAgentType.DRONE);
        agentTypeUnit = DroneAgentType.DRONE;
      } else if (unitTypeWrapper.equals(HATCHERY_TYPE)) {
        agentConfigurationForUnitType.put(HATCHERY_TYPE, HatcheryAgentType.HATCHERY);
        agentTypeUnit = HatcheryAgentType.HATCHERY;
      } else if (unitTypeWrapper.equals(LARVA_TYPE)) {
        agentConfigurationForUnitType.put(LARVA_TYPE, LarvaAgentType.LARVA);
        agentTypeUnit = LarvaAgentType.LARVA;
      } else if (unitTypeWrapper.equals(EGG_TYPE)) {
        agentConfigurationForUnitType.put(EGG_TYPE, EggAgentType.EGG);
        agentTypeUnit = EggAgentType.EGG;
      } else if (unitTypeWrapper.equals(SPAWNING_POOL_TYPE)) {
        agentConfigurationForUnitType.put(SPAWNING_POOL_TYPE, SpawningPoolAgentType.SPAWNING_POOL);
        agentTypeUnit = SpawningPoolAgentType.SPAWNING_POOL;
      } else if (unitTypeWrapper.equals(OVERLORD_TYPE)) {
        agentConfigurationForUnitType.put(OVERLORD_TYPE, OverlordAgentType.OVERLORD);
        agentTypeUnit = OverlordAgentType.OVERLORD;
      } else if (unitTypeWrapper.equals(ZERGLING_TYPE)) {
        agentConfigurationForUnitType.put(ZERGLING_TYPE, ZerglingAgentType.ZERGLING);
        agentTypeUnit = ZerglingAgentType.ZERGLING;
      } else if (unitTypeWrapper.equals(EXTRACTOR_TYPE)) {
        agentConfigurationForUnitType.put(EXTRACTOR_TYPE, ExtractorAgentType.EXTRACTOR);
        agentTypeUnit = ExtractorAgentType.EXTRACTOR;
      } else if (unitTypeWrapper.equals(LAIR_TYPE)) {
        agentConfigurationForUnitType.put(LAIR_TYPE, LairAgentType.LAIR);
        agentTypeUnit = LairAgentType.LAIR;
      } else if (unitTypeWrapper.equals(SPIRE_TYPE)) {
        agentConfigurationForUnitType.put(SPIRE_TYPE, SpireAgentType.SPIRE);
        agentTypeUnit = SpireAgentType.SPIRE;
      } else if (unitTypeWrapper.equals(EVOLUTION_CHAMBER_TYPE)) {
        agentConfigurationForUnitType
            .put(EVOLUTION_CHAMBER_TYPE, EvolutionChamberAgentType.EVOLUTION_CHAMBER);
        agentTypeUnit = EvolutionChamberAgentType.EVOLUTION_CHAMBER;
      } else if (unitTypeWrapper.equals(HYDRALISK_DEN_TYPE)) {
        agentConfigurationForUnitType.put(HYDRALISK_DEN_TYPE, HydraliskDenAgentType.HYDRALISK_DEN);
        agentTypeUnit = HydraliskDenAgentType.HYDRALISK_DEN;
      } else if (unitTypeWrapper.equals(SUNKEN_COLONY_TYPE)) {
        agentConfigurationForUnitType.put(SUNKEN_COLONY_TYPE, SunkenColonyAgentType.SUNKEN_COLONY);
        agentTypeUnit = SunkenColonyAgentType.SUNKEN_COLONY;
      } else if (unitTypeWrapper.equals(CREEP_COLONY_TYPE)) {
        agentConfigurationForUnitType.put(CREEP_COLONY_TYPE, CreepColonyAgentType.CREEP_COLONY);
        agentTypeUnit = CreepColonyAgentType.CREEP_COLONY;
      } else if (unitTypeWrapper.equals(SPORE_COLONY_TYPE)) {
        agentConfigurationForUnitType.put(SPORE_COLONY_TYPE, SporeColonyAgentType.SPORE_COLONY);
        agentTypeUnit = SporeColonyAgentType.SPORE_COLONY;
      } else if (unitTypeWrapper.equals(MUTALISK_TYPE)) {
        agentConfigurationForUnitType.put(MUTALISK_TYPE, MutaliskAgentType.MUTALISK);
        agentTypeUnit = MutaliskAgentType.MUTALISK;
      } else if (unitTypeWrapper.equals(HYDRALISK_TYPE)) {
        agentConfigurationForUnitType.put(HYDRALISK_TYPE, HydraliskAgentType.HYDRALISK);
        agentTypeUnit = HydraliskAgentType.HYDRALISK;
      }
    }
    return Optional.ofNullable(agentTypeUnit);
  }


}
