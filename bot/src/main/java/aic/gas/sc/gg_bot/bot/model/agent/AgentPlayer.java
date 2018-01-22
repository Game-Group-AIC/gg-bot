package aic.gas.sc.gg_bot.bot.model.agent;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_RACE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_PLAYER;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypePlayer;
import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import lombok.extern.slf4j.Slf4j;

/**
 * Agent to represent "player" - to access field of Player
 */
@Slf4j
public class AgentPlayer extends AgentObservingGame<AgentTypePlayer> {

  public AgentPlayer(AgentTypePlayer agentType, BotFacade botFacade, APlayer aPlayer,
      ARace enemyStartingRace) {
    super(agentType, botFacade);

    //add itself to knowledge
    beliefs.updateFact(IS_PLAYER, aPlayer);
    if (!enemyStartingRace.equals(ARace.UNKNOWN)) {
      beliefs.updateFact(ENEMY_RACE, enemyStartingRace);
    } else {
      beliefs.updateFact(ENEMY_RACE, ARace.getRandomRace());
    }
  }
}
