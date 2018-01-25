package aic.gas.sc.gg_bot.bot.model.agent;


import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.REPRESENTS_UNIT;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitWithCommands;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import java.util.stream.Collectors;

/**
 * Agent for unit in game
 */
public class AgentUnit extends AgentObservingGame<AgentTypeUnit> {

  private final AUnit unit;

  public AgentUnit(AgentTypeUnit agentType, BotFacade botFacade, AUnitWithCommands unit) {
    super(agentType, botFacade);
    this.unit = unit;

    //add itself to knowledge
    beliefs.updateFact(IS_UNIT, unit);
    beliefs.updateFact(REPRESENTS_UNIT, unit);
  }

  public AUnit getUnit() {
    return beliefs.returnFactValueForGivenKey(IS_UNIT)
        .map(aUnitWithCommands -> (AUnit) aUnitWithCommands).orElse(unit);
  }

  public String getCommitmentsAsText() {
    return "Unit: " + unit.getUnitId() + "\n"
        + getTopCommitments().entrySet().stream()
        .map(entry -> (entry.getValue() ? "C" : "D") + " : " + entry.getKey().getName())
        .collect(Collectors.joining("\n"));
  }
}
