package aic.gas.sc.gg_bot.bot.service;

import aic.gas.sc.gg_bot.bot.model.agent.AgentBaseLocation;
import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import bwta.BaseLocation;
import java.util.Optional;

/**
 * Interface to be implemented by user to create agent representing location - base/region.
 */
public interface LocationInitializer {

  /**
   * Method to create agent from base location
   */
  Optional<AgentBaseLocation> createAgent(BaseLocation baseLocation, BotFacade botFacade);

}
