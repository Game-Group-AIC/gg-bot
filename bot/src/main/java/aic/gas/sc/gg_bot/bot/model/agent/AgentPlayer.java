package aic.gas.sc.gg_bot.bot.model.agent;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_RACE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_PLAYER;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypePlayer;
import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import bwapi.Race;

/**
 * Agent to represent "player" - to access field of Player
 */
public class AgentPlayer extends AgentObservingGame<AgentTypePlayer> {

  public AgentPlayer(AgentTypePlayer agentType, BotFacade botFacade, APlayer aPlayer,
      Race enemyStartingRace) {
    super(agentType, botFacade);

    //add itself to knowledge
    beliefs.updateFact(IS_PLAYER, aPlayer);
    if (!enemyStartingRace.equals(Race.Unknown)) {
      beliefs.updateFact(ENEMY_RACE, ARace.getRace(enemyStartingRace));
    }
  }
}
