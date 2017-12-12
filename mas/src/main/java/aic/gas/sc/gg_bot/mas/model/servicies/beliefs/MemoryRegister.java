package aic.gas.sc.gg_bot.mas.model.servicies.beliefs;

import aic.gas.sc.gg_bot.mas.model.agents.Agent;
import aic.gas.sc.gg_bot.mas.model.knowledge.ReadOnlyMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.mas.model.servicies.Register;
import aic.gas.sc.gg_bot.mas.service.MASFacade;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

/**
 * MemoryRegister contains knowledge (internal beliefs) received from agents
 */
public class MemoryRegister extends Register<ReadOnlyMemory> implements IWorkingMemoryRegister {

  private final Map<Agent, Integer> decayMap = new HashMap<>();
  private final Map<AgentTypeID, Set<ReadOnlyMemory>> beliefsInSystem = new HashMap<>();
  private final Map<Integer, ReadOnlyMemory> beliefsInSystemByAgents = new HashMap<>();
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

  public MemoryRegister() {
    super(new HashMap<>());
  }

  @Override
  public void executeMaintenance() {
    try {
      lock.writeLock().lock();
      decayMap.forEach((v, integer) -> decayMap.put(v, integer + 1));
      decayMap.keySet().removeIf(
          v -> decayMap.get(v) >= MASFacade.howManyCyclesStayAgentsMemoryInRegisterWithoutUpdate);
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * Get ReadOnlyMemory by agent id
   */
  public Optional<ReadOnlyMemory> getReadOnlyMemoryForAgent(int agentId) {
    try {
      lock.readLock().lock();
      return Optional.ofNullable(beliefsInSystemByAgents.get(agentId));
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Get set of memories by agent type
   */
  public Stream<ReadOnlyMemory> getReadOnlyMemoriesForAgentType(AgentTypeID agentType) {
    try {
      lock.readLock().lock();
      return new ArrayList<>(beliefsInSystem.getOrDefault(agentType, new HashSet<>())).stream()
          .distinct();
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Get all beliefs in system
   */
  public Stream<ReadOnlyMemory> getReadOnlyMemories() {
    try {
      lock.readLock().lock();
      return new ArrayList<>(dataByOriginator.values()).stream().distinct();
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public boolean addAgentsMemory(ReadOnlyMemory readOnlyMemory, Agent owner) {
    try {
      lock.writeLock().lock();
      dataByOriginator.put(owner, readOnlyMemory);
      decayMap.put(owner, 1);
      removeMemoryFromSet(owner);
      beliefsInSystemByAgents.put(owner.getId(), readOnlyMemory);
      beliefsInSystem.computeIfAbsent(owner.getAgentType().getAgentTypeID(), id -> new HashSet<>())
          .add(readOnlyMemory);
      return true;
    } finally {
      lock.writeLock().unlock();
    }
  }

  private void removeMemoryFromSet(Agent owner) {
    Optional.ofNullable(beliefsInSystem.get(owner.getAgentType().getAgentTypeID()))
        .ifPresent(readOnlyMemories -> readOnlyMemories.stream()
            .filter(readOnlyMemory -> readOnlyMemory.getAgentId() == owner.getId())
            .findAny().ifPresent(readOnlyMemories::remove));
  }

  @Override
  public boolean removeAgentsMemory(Agent owner) {
    try {
      lock.writeLock().lock();
      dataByOriginator.remove(owner);
      decayMap.remove(owner);
      beliefsInSystemByAgents.remove(owner.getId());
      removeMemoryFromSet(owner);
      return true;
    } finally {
      lock.writeLock().unlock();
    }
  }

}
