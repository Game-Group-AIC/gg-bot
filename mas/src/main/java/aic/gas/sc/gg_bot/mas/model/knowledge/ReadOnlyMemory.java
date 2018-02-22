package aic.gas.sc.gg_bot.mas.model.knowledge;

import aic.gas.sc.gg_bot.mas.model.InternalClockObtainingStrategy;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentType;
import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import java.util.Map;

/**
 * Represent another agent's memory - it is intended as read only
 */
public class ReadOnlyMemory extends Memory<PlanningTreeOfAnotherAgent> {

  ReadOnlyMemory(
      Map<FactKey, FactSet> factSetParameterMap,
      PlanningTreeOfAnotherAgent tree, AgentType agentType, int agentId,
      IStrategyToGetSetOfMemoriesByAgentType strategyToGetSetOfMemoriesByAgentType,
      IStrategyToGetMemoryOfAgent strategyToGetMemoryOfAgent,
      IStrategyToGetAllMemories strategyToGetAllMemories,
      InternalClockObtainingStrategy internalClockObtainingStrategy) {
    super(factSetParameterMap, tree, agentType, agentId,
        strategyToGetSetOfMemoriesByAgentType, strategyToGetMemoryOfAgent,
        strategyToGetAllMemories, internalClockObtainingStrategy);
  }
}
