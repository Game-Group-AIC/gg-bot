package aic.gas.sc.gg_bot.bot.service.implementation;

import static aic.gas.sc.gg_bot.bot.model.agent.types.implementation.virtual.BuildingOrderManager.BUILDING_ORDER_MANAGER;
import static aic.gas.sc.gg_bot.bot.model.agent.types.implementation.virtual.EcoManagerAgentType.ECO_MANAGER;
import static aic.gas.sc.gg_bot.bot.model.agent.types.implementation.virtual.UnitOrderManager.UNIT_ORDER_MANAGER;

import aic.gas.sc.gg_bot.bot.model.agent.AbstractAgent;
import aic.gas.sc.gg_bot.bot.service.AbstractAgentsInitializer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of AbstractAgentsInitializer
 */
public class AbstractAgentsInitializerImpl implements AbstractAgentsInitializer {

  @Override
  public List<AbstractAgent> initializeAbstractAgents(BotFacade botFacade) {
    return Stream.of(new AbstractAgent(ECO_MANAGER, botFacade),
        new AbstractAgent(BUILDING_ORDER_MANAGER, botFacade),
        new AbstractAgent(UNIT_ORDER_MANAGER, botFacade)).collect(Collectors.toList());
  }

}
