package aic.gas.sc.gg_bot.service;

import aic.gas.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.sc.gg_bot.model.agent.AgentPlayer;
import aic.gas.sc.gg_bot.service.implementation.BotFacade;
import bwapi.Race;

/**
 * Interface to be implemented by user to create agent representing player. This agent make
 * observation of player stats
 */
public interface PlayerInitializer {

  /**
   * Method to create agent from player
   */
  AgentPlayer createAgentForPlayer(APlayer player, BotFacade botFacade, Race enemyInitialRace);

}
