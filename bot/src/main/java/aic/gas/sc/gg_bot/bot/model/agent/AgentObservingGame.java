package aic.gas.sc.gg_bot.bot.model.agent;

import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import aic.gas.sc.gg_bot.bot.service.implementation.GameCommandExecutor;
import aic.gas.sc.gg_bot.mas.model.IResponseReceiver;
import aic.gas.sc.gg_bot.mas.model.agents.Agent;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeMakingObservations;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import aic.gas.sc.gg_bot.mas.model.planing.command.IObservingCommand;
import bwapi.Game;

/**
 * AgentObservingGame is agent which makes observations of BW game
 */
class AgentObservingGame<K extends AgentTypeMakingObservations<Game>> extends
    Agent.MakingObservation<Game> {

  private final GameCommandExecutor gameCommandExecutor;

  AgentObservingGame(K agentType, BotFacade botFacade) {
    super(agentType, botFacade.getMasFacade());
    this.gameCommandExecutor = botFacade.getGameCommandExecutor();
  }

  @Override
  public boolean sendCommandToExecute(ActCommand<?> command,
      IResponseReceiver<Boolean> responseReceiver) {
    return gameCommandExecutor.addCommandToAct(command, beliefs, responseReceiver);
  }

  @Override
  protected boolean requestObservation(IObservingCommand<Game> observingCommand,
      IResponseReceiver<Boolean> responseReceiver) {
    return gameCommandExecutor.addCommandToObserve(observingCommand, beliefs, responseReceiver);
  }
}
