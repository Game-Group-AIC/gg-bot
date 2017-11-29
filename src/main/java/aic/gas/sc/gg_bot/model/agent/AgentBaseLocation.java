package aic.gas.sc.gg_bot.model.agent;

import static aic.gas.abstract_bot.model.bot.FactKeys.BASE_TO_MOVE;
import static aic.gas.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.abstract_bot.model.bot.FactKeys.IS_ISLAND;
import static aic.gas.abstract_bot.model.bot.FactKeys.IS_MINERAL_ONLY;
import static aic.gas.abstract_bot.model.bot.FactKeys.IS_START_LOCATION;
import static aic.gas.abstract_bot.model.bot.FactKeys.MADE_OBSERVATION_IN_FRAME;

import aic.gas.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.model.agent.types.AgentTypeBaseLocation;
import aic.gas.sc.gg_bot.service.implementation.BotFacade;
import bwta.BaseLocation;

/**
 * Agent for base location in game INSTANCE OF THIS AGENT SHOULD NOT SEND ANY COMMAND TO GAME. ONLY
 * REASON AND OBSERVE
 */
public class AgentBaseLocation extends AgentObservingGame<AgentTypeBaseLocation> {

  public AgentBaseLocation(AgentTypeBaseLocation agentType, BotFacade botFacade,
      BaseLocation location) {
    super(agentType, botFacade);
    //add itself to knowledge
    beliefs.updateFact(IS_BASE_LOCATION, ABaseLocationWrapper.wrap(location));
    AgentTypeBaseLocation.updateKnowledgeAboutResources(location, beliefs, 0);
    beliefs.updateFact(MADE_OBSERVATION_IN_FRAME, 0);
    beliefs.updateFact(IS_MINERAL_ONLY, location.isMineralOnly());
    beliefs.updateFact(IS_ISLAND, location.isIsland());
    beliefs.updateFact(IS_START_LOCATION, location.isStartLocation());
    beliefs.updateFact(BASE_TO_MOVE, beliefs.returnFactValueForGivenKey(IS_BASE_LOCATION).get());
  }
}
