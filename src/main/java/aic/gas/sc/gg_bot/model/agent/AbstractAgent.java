package aic.gas.sc.gg_bot.model.agent;

import aic.gas.mas.model.ResponseReceiverInterface;
import aic.gas.mas.model.agents.Agent;
import aic.gas.mas.model.metadata.AgentType;
import aic.gas.mas.model.planing.command.ActCommand;
import aic.gas.mas.utils.MyLogger;
import aic.gas.sc.gg_bot.service.implementation.BotFacade;

/**
 * AbstractAgent is agent which makes no observation and send no commands to game. It only reasons
 * and share desires
 */
public class AbstractAgent extends Agent<AgentType> {

  public AbstractAgent(AgentType agentType, BotFacade botFacade) {
    super(agentType, botFacade.getMasFacade());
  }

  @Override
  public boolean sendCommandToExecute(ActCommand<?> command,
      ResponseReceiverInterface<Boolean> responseReceiver) {
    MyLogger.getLogger().warning("Trying to send command on behalf of abstract agent.");
    throw new RuntimeException("Trying to send command on behalf of abstract agent.");
  }
}
