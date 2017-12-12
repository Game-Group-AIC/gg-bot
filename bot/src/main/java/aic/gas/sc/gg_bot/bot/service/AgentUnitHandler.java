package aic.gas.sc.gg_bot.bot.service;

import aic.gas.sc.gg_bot.bot.model.agent.AgentUnit;
import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import bwapi.Unit;
import java.util.Optional;

/**
 * Interface to be implemented by user to provide factory for creating agents for own units on their
 * creation
 */
public interface AgentUnitHandler {

  /**
   * Method to create agent from unit
   */
  Optional<AgentUnit> createAgentForUnit(Unit unit, BotFacade botFacade, int frameCount);

}
