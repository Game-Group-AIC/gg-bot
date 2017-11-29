package aic.gas.sc.gg_bot.service;

import aic.gas.sc.gg_bot.model.agent.AbstractAgent;
import aic.gas.sc.gg_bot.service.implementation.BotFacade;
import java.util.List;

/**
 * Interface to be implemented by user to create all additional abstract agents with no direct
 * representation in game
 */
public interface AbstractAgentsInitializer {

  /**
   * Method to create abstract agents
   */
  List<AbstractAgent> initializeAbstractAgents(BotFacade botFacade);

}
