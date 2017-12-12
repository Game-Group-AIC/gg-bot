package aic.gas.sc.gg_bot.mas.model.knowledge;

import aic.gas.sc.gg_bot.mas.model.metadata.AgentType;
import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import java.util.Map;

/**
 * Represent another agent's memory - it is intended as read only
 */
public class ReadOnlyMemory extends Memory<PlanningTreeOfAnotherAgent> {

  ReadOnlyMemory(Map<FactKey, Fact> factParameterMap, Map<FactKey, FactSet> factSetParameterMap,
      PlanningTreeOfAnotherAgent tree, AgentType agentType, int agentId,
      StrategyToGetSetOfMemoriesByAgentType strategyToGetSetOfMemoriesByAgentType,
      StrategyToGetMemoryOfAgent strategyToGetMemoryOfAgent,
      StrategyToGetAllMemories strategyToGetAllMemories) {
    super(factParameterMap, factSetParameterMap, tree, agentType, agentId,
        strategyToGetSetOfMemoriesByAgentType, strategyToGetMemoryOfAgent,
        strategyToGetAllMemories);
  }
}
