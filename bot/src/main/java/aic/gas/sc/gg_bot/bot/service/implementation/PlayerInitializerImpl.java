package aic.gas.sc.gg_bot.bot.service.implementation;

import static aic.gas.sc.gg_bot.bot.model.agent.types.implementation.player.PlayerAgentType.PLAYER;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.sc.gg_bot.bot.model.agent.AgentPlayer;
import aic.gas.sc.gg_bot.bot.service.PlayerInitializer;
import bwapi.Race;

/**
 * Strategy to initialize agent representing "player"
 */
public class PlayerInitializerImpl implements PlayerInitializer {

  @Override
  public AgentPlayer createAgentForPlayer(APlayer player, BotFacade botFacade,
      Race enemyInitialRace) {
    return new AgentPlayer(PLAYER, botFacade, player, enemyInitialRace);
  }
}
