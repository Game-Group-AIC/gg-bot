package aic.gas.sc.gg_bot.service.implementation;

import aic.gas.abstract_bot.model.bot.AgentTypes;
import aic.gas.abstract_bot.model.bot.DecisionCombinations;
import aic.gas.abstract_bot.model.bot.DesireKeys;
import aic.gas.abstract_bot.model.decision.DecisionPoint;
import aic.gas.abstract_bot.service.DecisionLoadingService;
import aic.gas.abstract_bot.utils.SerializationUtil;
import aic.gas.mas.model.metadata.AgentTypeID;
import aic.gas.mas.model.metadata.DesireKeyID;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation for DecisionLoadingService
 */
@Slf4j
public class DecisionLoadingServiceImpl implements DecisionLoadingService {

  private static DecisionLoadingServiceImpl instance = null;
  private final Map<AgentTypeID, Map<DesireKeyID, DecisionPoint>> cache = new ConcurrentHashMap<>();

  /**
   * Initialize cache (loads models from resources)
   */
  private DecisionLoadingServiceImpl() {
    //todo - not effective as all combinations of agent - desire are tried
    DecisionCombinations.decisionsToLoad.forEach((agentTypeID, desireKeyIDS) -> desireKeyIDS
        .forEach(desireKeyID -> loadDecisionPoint(agentTypeID, desireKeyID)));
  }

  /**
   * Only one instance
   */
  public static DecisionLoadingService getInstance() {
    if (instance == null) {
      instance = new DecisionLoadingServiceImpl();
    }
    return instance;
  }

  /**
   * Map static fields of agentTypeId from AgentTypes to folders in storage
   */
  private Set<AgentTypeID> getAgentTypes() {
    return Arrays.stream(AgentTypes.class.getDeclaredFields())
        .filter(field -> Modifier.isStatic(field.getModifiers()))
        .filter(field -> field.getType().equals(AgentTypeID.class))
        .map(field -> {
              try {
                return (AgentTypeID) field.get(null);
              } catch (IllegalAccessException e) {
                log.error(e.getLocalizedMessage());
              }
              return null;
            }
        )
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  /**
   * Map static fields of desireKeyID from DesireKeys to folders in storage
   */
  private Set<DesireKeyID> getParsedDesireTypesForAgentTypeContainedInStorage() {
    return Arrays.stream(DesireKeys.class.getDeclaredFields())
        .filter(field -> Modifier.isStatic(field.getModifiers()))
        .filter(field -> field.getType().equals(DesireKeyID.class))
        .map(field -> {
              try {
                return (DesireKeyID) field.get(null);
              } catch (IllegalAccessException e) {
                log.error(e.getLocalizedMessage());
              }
              return null;
            }
        )
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  /**
   * Try to load decision points for given keys and put it in to cache
   */
  private void loadDecisionPoint(AgentTypeID agentTypeID, DesireKeyID desireKeyID) {
    try {
      String fileName = "/" + agentTypeID.getName() + "/" + desireKeyID.getName() + ".db";
      DecisionPoint decisionPoint = new DecisionPoint(SerializationUtil.deserialize(
          DecisionLoadingServiceImpl.class.getResourceAsStream(fileName)));
      cache.computeIfAbsent(agentTypeID, id -> new HashMap<>()).put(desireKeyID, decisionPoint);

//            System.out.println(desireKeyID.getName() + " " + decisionPoint.getStates().stream()
//                    .filter(stateWithTransition -> stateWithTransition.getNextAction().commit())
//                    .count() + "/" + decisionPoint.getStates().size());

    } catch (Exception ignored) {
      ignored.printStackTrace();
    }
  }

  @Override
  public DecisionPoint getDecisionPoint(AgentTypeID agentTypeID, DesireKeyID desireKeyID) {
    if (!cache.containsKey(agentTypeID)) {
      log.error("No models of decision for " + agentTypeID.getName() + " are present.");
      throw new RuntimeException(
          "No models of decision for " + agentTypeID.getName() + " are present.");
    }
    if (!cache.get(agentTypeID).containsKey(desireKeyID)) {
      log.error("No models of " + desireKeyID.getName() + " for " + agentTypeID.getName()
          + " are present.");
      throw new RuntimeException(
          "No models of " + desireKeyID.getName() + " for " + agentTypeID.getName()
              + " are present.");
    }
    return cache.get(agentTypeID).get(desireKeyID);
  }
}
