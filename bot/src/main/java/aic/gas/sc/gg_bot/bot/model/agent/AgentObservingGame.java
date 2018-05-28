package aic.gas.sc.gg_bot.bot.model.agent;

import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import aic.gas.sc.gg_bot.bot.service.implementation.GameCommandExecutor;
import aic.gas.sc.gg_bot.mas.model.ResponseReceiverInterface;
import aic.gas.sc.gg_bot.mas.model.agents.Agent;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeMakingObservations;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import aic.gas.sc.gg_bot.mas.model.planing.command.ObservingCommand;
import bwapi.Game;
import lombok.extern.log4j.Log4j;

/**
 * AgentObservingGame is agent which makes observations of BW game
 */
@Log4j
class AgentObservingGame<K extends AgentTypeMakingObservations<Game>> extends
    Agent.MakingObservation<Game> {
  private final GameCommandExecutor gameCommandExecutor;

  AgentObservingGame(K agentType, BotFacade botFacade) {
    super(agentType, botFacade.getMasFacade());
    this.gameCommandExecutor = botFacade.getGameCommandExecutor();
  }

  @Override
  public boolean sendCommandToExecute(ActCommand<?> command,
      ResponseReceiverInterface<Boolean> responseReceiver) {
    return gameCommandExecutor.addCommandToAct(command, beliefs, responseReceiver);
  }

  @Override
  protected boolean requestObservation(ObservingCommand<Game> observingCommand,
      ResponseReceiverInterface<Boolean> responseReceiver) {
    return gameCommandExecutor.addCommandToObserve(observingCommand, beliefs, responseReceiver);
  }
}
