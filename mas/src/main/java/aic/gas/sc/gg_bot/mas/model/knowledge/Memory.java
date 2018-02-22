package aic.gas.sc.gg_bot.mas.model.knowledge;

import aic.gas.sc.gg_bot.mas.model.FactContainerInterface;
import aic.gas.sc.gg_bot.mas.model.InternalClockObtainingStrategy;
import aic.gas.sc.gg_bot.mas.model.PlanningTreeInterface;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentType;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Template class for memory - it stores facts, sets of facts, agent type and commitment process in
 * form of heap, and provide access to this data
 */
@Slf4j
public abstract class Memory<V extends PlanningTreeInterface>
    implements FactContainerInterface, PlanningTreeInterface {

  final Map<FactKey<?>, FactSet<?>> factSetParameterMap = new HashMap<>();
  final V tree;
  @Getter
  final AgentType agentType;
  @Getter
  final int agentId;
  final StrategyToGetSetOfMemoriesByAgentType strategyToGetSetOfMemoriesByAgentType;
  final StrategyToGetMemoryOfAgent strategyToGetMemoryOfAgent;
  final StrategyToGetAllMemories strategyToGetAllMemories;
  final InternalClockObtainingStrategy internalClockObtainingStrategy;

  Memory(V tree, AgentType agentType, int agentId,
      StrategyToGetSetOfMemoriesByAgentType strategyToGetSetOfMemoriesByAgentType,
      StrategyToGetMemoryOfAgent strategyToGetMemoryOfAgent,
      StrategyToGetAllMemories strategyToGetAllMemories,
      InternalClockObtainingStrategy internalClockObtainingStrategy) {
    this.tree = tree;
    this.agentType = agentType;
    this.agentId = agentId;
    this.strategyToGetSetOfMemoriesByAgentType = strategyToGetSetOfMemoriesByAgentType;
    this.strategyToGetMemoryOfAgent = strategyToGetMemoryOfAgent;
    this.strategyToGetAllMemories = strategyToGetAllMemories;
    this.internalClockObtainingStrategy = internalClockObtainingStrategy;
    agentType.getUsingTypesForFactSets()
        .forEach(factKey -> this.factSetParameterMap.put(factKey, factKey.returnEmptyFactSet()));
  }

  /**
   * To make read only copy...
   */
  Memory(Map<FactKey, FactSet> factSetParameterMap, V tree,
      AgentType agentType, int agentId,
      StrategyToGetSetOfMemoriesByAgentType strategyToGetSetOfMemoriesByAgentType,
      StrategyToGetMemoryOfAgent strategyToGetMemoryOfAgent,
      StrategyToGetAllMemories strategyToGetAllMemories,
      InternalClockObtainingStrategy internalClockObtainingStrategy) {
    this.tree = tree;
    this.agentType = agentType;
    this.agentId = agentId;
    this.strategyToGetSetOfMemoriesByAgentType = strategyToGetSetOfMemoriesByAgentType;
    this.strategyToGetMemoryOfAgent = strategyToGetMemoryOfAgent;
    this.strategyToGetAllMemories = strategyToGetAllMemories;
    this.internalClockObtainingStrategy = internalClockObtainingStrategy;
    factSetParameterMap
        .forEach((factKey, set) -> this.factSetParameterMap.put(factKey, set.copyFact()));
  }

  //TODO hack
  public int getCurrentClock() {
    return internalClockObtainingStrategy.internalClockCounter();
  }

  public boolean isFactKeyForSetInMemory(FactKey<?> factKey) {
    return factSetParameterMap.containsKey(factKey);
  }

  public Optional<ReadOnlyMemory> getReadOnlyMemoryForAgent(int agentId) {
    return strategyToGetMemoryOfAgent.getReadOnlyMemoryForAgent(agentId);
  }

  public Stream<ReadOnlyMemory> getReadOnlyMemoriesForAgentType(AgentTypeID agentType) {
    return strategyToGetSetOfMemoriesByAgentType.getReadOnlyMemoriesForAgentType(agentType);
  }

  public Stream<ReadOnlyMemory> getReadOnlyMemoriesForAgentType(AgentType agentType) {
    return strategyToGetSetOfMemoriesByAgentType
        .getReadOnlyMemoriesForAgentType(agentType.getAgentTypeID());
  }

  public Stream<ReadOnlyMemory> getReadOnlyMemories() {
    return strategyToGetAllMemories.getReadOnlyMemories();
  }

  @Override
  public <K> Optional<K> returnFactValueForGivenKey(FactKey<K> factKey) {
    FactSet<K> factSet = (FactSet<K>) factSetParameterMap.get(factKey);
    if (factSet != null) {
      return factSet.getContent().stream().findFirst();
    }
    log.error(
        factKey.getName() + " is not present in " + agentType.getName() + " type definition.");
    return Optional.empty();
  }

  @Override
  public <K, S extends Stream<K>> Optional<S> returnFactSetValueForGivenKey(FactKey<K> factKey) {
    FactSet<K> factSet = (FactSet<K>) factSetParameterMap.get(factKey);
    if (factSet != null) {
      return Optional.ofNullable((S) factSet.getContent().stream());
    }
    log.error(
        factKey.getName() + " is not present in " + agentType.getName() + " type definition.");
    return Optional.empty();
  }

  /**
   * Returns copy of fact set
   */
  public <K> Optional<FactSet<K>> returnFactSetCopyForGivenKey(FactKey<K> factKey) {
    FactSet<K> factSet = (FactSet<K>) factSetParameterMap.get(factKey);
    if (factSet != null) {
      return Optional.ofNullable(factSet.copyFact());
    }
    log.error(
        factKey.getName() + " is not present in " + agentType.getName() + " type definition.");
    return Optional.empty();
  }

  @Override
  public Set<DesireParameters> committedSharedDesiresParametersByOtherAgents() {
    return tree.committedSharedDesiresParametersByOtherAgents();
  }

  @Override
  public Set<DesireParameters> sharedDesiresParameters() {
    return tree.sharedDesiresParameters();
  }

  @Override
  public int countOfCommittedSharedDesiresByOtherAgents() {
    return tree.countOfCommittedSharedDesiresByOtherAgents();
  }

  @Override
  public int countOfSharedDesires() {
    return tree.countOfSharedDesires();
  }

  @Override
  public Map<DesireKey, Long> collectKeysOfCommittedDesiresInTreeCounts() {
    return tree.collectKeysOfCommittedDesiresInTreeCounts();
  }

  @Override
  public Map<DesireKey, Long> collectKeysOfDesiresInTreeCounts() {
    return tree.collectKeysOfDesiresInTreeCounts();
  }

  @Override
  public Set<DesireParameters> getParametersOfCommittedDesiresOnTopLevel() {
    return tree.getParametersOfCommittedDesiresOnTopLevel();
  }

  @Override
  public Set<DesireParameters> getParametersOfDesiresOnTopLevel() {
    return tree.getParametersOfDesiresOnTopLevel();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Memory)) {
      return false;
    }

    Memory<?> memory = (Memory<?>) o;

    return agentId == memory.agentId && agentType.equals(memory.agentType);
  }

  @Override
  public int hashCode() {
    int result = agentType.hashCode();
    result = 31 * result + agentId;
    return result;
  }

  /**
   * Obtaining strategy
   */
  public interface StrategyToGetSetOfMemoriesByAgentType {

    /**
     * Get set of memories by agent type
     */
    Stream<ReadOnlyMemory> getReadOnlyMemoriesForAgentType(AgentTypeID agentType);
  }

  /**
   * Obtaining strategy
   */
  public interface StrategyToGetMemoryOfAgent {

    /**
     * Get ReadOnlyMemory by agent id
     */
    Optional<ReadOnlyMemory> getReadOnlyMemoryForAgent(int agentId);
  }

  /**
   * Obtaining strategy
   */
  public interface StrategyToGetAllMemories {

    /**
     * Get all beliefs in system
     */
    Stream<ReadOnlyMemory> getReadOnlyMemories();
  }

}

