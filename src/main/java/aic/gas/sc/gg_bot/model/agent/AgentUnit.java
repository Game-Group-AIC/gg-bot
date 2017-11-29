package aic.gas.sc.gg_bot.model.agent;

import static aic.gas.abstract_bot.model.bot.FactKeys.IS_UNIT;
import static aic.gas.abstract_bot.model.bot.FactKeys.REPRESENTS_UNIT;

import aic.gas.abstract_bot.model.game.wrappers.AUnitWithCommands;
import aic.gas.sc.gg_bot.model.agent.types.AgentTypeUnit;
import aic.gas.sc.gg_bot.service.implementation.BotFacade;

/**
 * Agent for unit in game
 */
public class AgentUnit extends AgentObservingGame<AgentTypeUnit> {

  public AgentUnit(AgentTypeUnit agentType, BotFacade botFacade, AUnitWithCommands unit) {
    super(agentType, botFacade);

    //add itself to knowledge
    beliefs.updateFact(IS_UNIT, unit);
    beliefs.updateFact(REPRESENTS_UNIT, unit);
  }
}
