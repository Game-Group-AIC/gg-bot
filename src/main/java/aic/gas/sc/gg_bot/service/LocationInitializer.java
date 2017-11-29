package aic.gas.sc.gg_bot.service;

import aic.gas.sc.gg_bot.model.agent.AgentBaseLocation;
import aic.gas.sc.gg_bot.service.implementation.BotFacade;
import bwta.BaseLocation;
import java.util.Optional;

/**
 * Interface to be implemented by user to create agent representing location - base/region. Created
 * by Jan on 05-Apr-17.
 */
public interface LocationInitializer {

  /**
   * Method to create agent from base location
   */
  Optional<AgentBaseLocation> createAgent(BaseLocation baseLocation, BotFacade botFacade);

}
