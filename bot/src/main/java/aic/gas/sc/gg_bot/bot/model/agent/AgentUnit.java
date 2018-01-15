package aic.gas.sc.gg_bot.bot.model.agent;


import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.REPRESENTS_UNIT;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitWithCommands;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import lombok.Getter;

/**
 * Agent for unit in game
 */
public class AgentUnit extends AgentObservingGame<AgentTypeUnit> {
  @Getter
  private final AUnit unit;

  public AgentUnit(AgentTypeUnit agentType, BotFacade botFacade, AUnitWithCommands unit) {
    super(agentType, botFacade);
    this.unit = unit;

    //add itself to knowledge
    beliefs.updateFact(IS_UNIT, unit);
    beliefs.updateFact(REPRESENTS_UNIT, unit);
  }
}
