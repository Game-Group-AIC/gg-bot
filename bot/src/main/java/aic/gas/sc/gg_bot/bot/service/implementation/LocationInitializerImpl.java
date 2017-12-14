package aic.gas.sc.gg_bot.bot.service.implementation;

import static aic.gas.sc.gg_bot.bot.model.agent.types.implementation.location.BaseLocationAgentType.BASE_LOCATION;

import aic.gas.sc.gg_bot.bot.model.agent.AgentBaseLocation;
import aic.gas.sc.gg_bot.bot.service.LocationInitializer;
import bwta.BaseLocation;
import java.util.Optional;

/**
 * Strategy to initialize player
 */
public class LocationInitializerImpl implements LocationInitializer {

  @Override
  public Optional<AgentBaseLocation> createAgent(BaseLocation baseLocation, BotFacade botFacade) {
    return Optional.of(new AgentBaseLocation(BASE_LOCATION, botFacade, baseLocation));
  }
}
