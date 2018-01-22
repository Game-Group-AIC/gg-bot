package aic.gas.sc.gg_bot.mas.model.servicies.desires;

import aic.gas.sc.gg_bot.mas.model.agents.Agent;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import aic.gas.sc.gg_bot.mas.model.planing.Desire;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesire;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesireForAgents;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesireInRegister;
import aic.gas.sc.gg_bot.mas.model.servicies.Register;
import aic.gas.sc.gg_bot.mas.service.MASFacade;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * DesireRegister contains desires received from agents
 */
@Slf4j
public class DesireRegister extends Register<Map<SharedDesire, SharedDesireInRegister>> implements
    IWorkingDesireRegister {

  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
  private final Map<SharedDesireInRegister, Integer> desireLock = new HashMap<>();

  public DesireRegister() {
    super(new HashMap<>());
  }

  public boolean addedDesire(SharedDesireInRegister desireForOthers) {
    try {
      lock.writeLock().lock();
      Map<SharedDesire, SharedDesireInRegister> desiresByAgent = dataByOriginator
          .computeIfAbsent(desireForOthers.getOriginatedFromAgent(), agent -> new HashMap<>());
      if (!desiresByAgent.containsKey(desireForOthers)) {
        desiresByAgent.put(desireForOthers, desireForOthers);
      }
      return true;
    } finally {
      lock.writeLock().unlock();
    }
  }

  public boolean removeAgent(Agent agentToRemove) {
    try {
      lock.writeLock().lock();
      dataByOriginator.remove(agentToRemove);
      return true;
    } finally {
      lock.writeLock().unlock();
    }
  }

  public boolean removedDesire(SharedDesire desireForOthers) {
    try {
      lock.writeLock().lock();
      if (dataByOriginator.containsKey(desireForOthers.getOriginatedFromAgent())) {
        Map<SharedDesire, SharedDesireInRegister> desiresByAgent = dataByOriginator
            .get(desireForOthers.getOriginatedFromAgent());
        desiresByAgent.remove(desireForOthers);
        if (desiresByAgent.isEmpty()) {
          dataByOriginator.remove(desireForOthers.getOriginatedFromAgent());
        }
      }
      return true;
    } finally {
      lock.writeLock().unlock();
    }
  }

  public Optional<SharedDesireForAgents> commitToDesire(Agent agentWhoWantsToCommitTo,
      SharedDesireForAgents desireForOthersHeWantsToCommitTo) {
    try {
      lock.writeLock().lock();
      if (dataByOriginator.containsKey(desireForOthersHeWantsToCommitTo.getOriginatedFromAgent())) {
        SharedDesireInRegister desire = dataByOriginator
            .get(desireForOthersHeWantsToCommitTo.getOriginatedFromAgent())
            .getOrDefault(desireForOthersHeWantsToCommitTo, null);

        //desire commitment is not locked
        if (desire != null && !desireLock.containsKey(desire)) {

          //lock desire for one agent only for few cycles
          if (desire.getLimitOnNumberOfAgentsToCommit() == 1) {
            desireLock.put(desire, 0);
          }

          //try to commit agent and return copy of current instance
          if (desire.commitToDesire(agentWhoWantsToCommitTo)){
            return Optional.of(desire.getCopyOfSharedDesireForAgents());
          }
        }
      }
      return Optional.empty();
    } finally {
      lock.writeLock().unlock();
    }
  }

  public boolean removeCommitmentToDesire(Agent agentWhoWantsToRemoveCommitment,
      SharedDesireForAgents desireHeWantsToRemoveCommitmentTo) {
    try {
      lock.writeLock().lock();
      if (dataByOriginator
          .containsKey(desireHeWantsToRemoveCommitmentTo.getOriginatedFromAgent())) {
        SharedDesireInRegister desire = dataByOriginator
            .get(desireHeWantsToRemoveCommitmentTo.getOriginatedFromAgent())
            .getOrDefault(desireHeWantsToRemoveCommitmentTo, null);
        if (desire != null) {

          //lock desire for one agent only for few cycles
          if (desire.getLimitOnNumberOfAgentsToCommit() == 1) {
            desireLock.put(desire, 0);
          }

          return (desire.removeCommitment(agentWhoWantsToRemoveCommitment));
        }
      }
      return true;
    } finally {
      lock.writeLock().unlock();
    }
  }

  public Set<SharedDesireForAgents> getOwnSharedDesires(Agent agent) {
    try {
      lock.readLock().lock();
      Map<SharedDesire, SharedDesireInRegister> agentsSharedDesires = dataByOriginator.get(agent);
      if (agentsSharedDesires != null) {
        return agentsSharedDesires.values().stream()
            .map(SharedDesireInRegister::getCopyOfSharedDesireForAgents)
            .collect(Collectors.toSet());
      }
      return new HashSet<>();
    } finally {
      lock.readLock().unlock();
    }
  }

  public Map<SharedDesire, SharedDesireForAgents> getSharedDesiresFromOtherAgents(Agent self) {
    try {
      lock.readLock().lock();
      return dataByOriginator.entrySet().stream()
          .filter(agentMapEntry -> !agentMapEntry.getKey().equals(self))
          .map(Map.Entry::getValue)
          .flatMap(registerMap -> registerMap.entrySet().stream()
              .filter(
                  desireInRegisterEntry -> self.getAgentType().getSupportedDesiresOfOtherAgents()
                      .contains(desireInRegisterEntry.getKey().getDesireKey())))
          .map(entry -> entry.getValue().getCopyOfSharedDesireForAgents())
          .collect(Collectors.toMap(Function.identity(), Function.identity()));
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void executeMaintenance() {
    try {
      lock.writeLock().lock();
      desireLock.forEach((v, integer) -> desireLock.put(v, integer + 1));
      desireLock.keySet()
          .removeIf(v -> desireLock.get(v) >= MASFacade.howManyCyclesIsDesireForOneAgentLocked);
    } finally {
      lock.writeLock().unlock();
    }
  }
}
