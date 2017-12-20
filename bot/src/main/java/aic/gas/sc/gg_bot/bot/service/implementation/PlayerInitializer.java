package aic.gas.sc.gg_bot.bot.service.implementation;

import static aic.gas.sc.gg_bot.bot.model.agent.types.implementation.player.PlayerAgentType.PLAYER;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import aic.gas.sc.gg_bot.bot.model.agent.AgentPlayer;
import aic.gas.sc.gg_bot.bot.service.IPlayerInitializer;

/**
 * Strategy to initialize agent representing "player"
 */
public class PlayerInitializer implements IPlayerInitializer {

  @Override
  public AgentPlayer createAgentForPlayer(APlayer player, BotFacade botFacade,
      ARace enemyInitialRace) {
    return new AgentPlayer(PLAYER, botFacade, player, enemyInitialRace);
  }
}
