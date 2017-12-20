package aic.gas.sc.gg_bot.bot.service;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import aic.gas.sc.gg_bot.bot.model.agent.AgentPlayer;
import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;

/**
 * Interface to be implemented by user to create agent representing player. This agent make
 * observation of player stats
 */
public interface IPlayerInitializer {

  /**
   * Method to create agent from player
   */
  AgentPlayer createAgentForPlayer(APlayer player, BotFacade botFacade, ARace enemyInitialRace);

}
