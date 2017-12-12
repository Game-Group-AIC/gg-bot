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
import aic.gas.sc.gg_bot.bot.model.AgentsUnitTypes;
import aic.gas.sc.gg_bot.bot.model.agent.AgentUnit;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import aic.gas.sc.gg_bot.bot.service.AgentUnitHandler;
import bwapi.Unit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory to create agent for given unit
 */
@Slf4j
public class AgentUnitHandlerImpl implements AgentUnitHandler {

  private final Map<AUnitTypeWrapper, AgentTypeUnit> agentConfigurationForUnitType = new HashMap<>();

  {
    agentConfigurationForUnitType.put(DRONE_TYPE, AgentsUnitTypes.DRONE);
    agentConfigurationForUnitType.put(HATCHERY_TYPE, AgentsUnitTypes.HATCHERY);
    agentConfigurationForUnitType.put(LARVA_TYPE, AgentsUnitTypes.LARVA);
    agentConfigurationForUnitType.put(EGG_TYPE, AgentsUnitTypes.EGG);
    agentConfigurationForUnitType.put(SPAWNING_POOL_TYPE, AgentsUnitTypes.SPAWNING_POOL);
    agentConfigurationForUnitType.put(OVERLORD_TYPE, AgentsUnitTypes.OVERLORD);
    agentConfigurationForUnitType.put(ZERGLING_TYPE, AgentsUnitTypes.ZERGLING);
    agentConfigurationForUnitType.put(EXTRACTOR_TYPE, AgentsUnitTypes.EXTRACTOR);
    agentConfigurationForUnitType.put(LAIR_TYPE, AgentsUnitTypes.LAIR);
    agentConfigurationForUnitType.put(SPIRE_TYPE, AgentsUnitTypes.SPIRE);
    agentConfigurationForUnitType.put(EVOLUTION_CHAMBER_TYPE, AgentsUnitTypes.EVOLUTION_CHAMBER);
    agentConfigurationForUnitType.put(HYDRALISK_DEN_TYPE, AgentsUnitTypes.HYDRALISK_DEN);
    agentConfigurationForUnitType.put(SUNKEN_COLONY_TYPE, AgentsUnitTypes.SUNKEN_COLONY);
    agentConfigurationForUnitType.put(CREEP_COLONY_TYPE, AgentsUnitTypes.CREEP_COLONY);
    agentConfigurationForUnitType.put(SPORE_COLONY_TYPE, AgentsUnitTypes.SPORE_COLONY);
    agentConfigurationForUnitType.put(MUTALISK_TYPE, AgentsUnitTypes.MUTALISK);
    agentConfigurationForUnitType.put(HYDRALISK_TYPE, AgentsUnitTypes.HYDRALISK);
  }

  @Override
  public Optional<AgentUnit> createAgentForUnit(Unit unit, BotFacade botFacade, int frameCount) {
    Optional<AgentTypeUnit> agentTypeUnit = Optional.ofNullable(
        agentConfigurationForUnitType.get(WrapperTypeFactory.createFrom(unit.getType())));
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


}
