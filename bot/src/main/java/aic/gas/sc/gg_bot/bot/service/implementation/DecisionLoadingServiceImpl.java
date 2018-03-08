package aic.gas.sc.gg_bot.bot.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.DecisionConfiguration;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.MapSizeEnums;
import aic.gas.sc.gg_bot.abstract_bot.model.decision.Policy;
import aic.gas.sc.gg_bot.abstract_bot.model.decision.NextActionEnumerations;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import aic.gas.sc.gg_bot.abstract_bot.service.DecisionLoadingService;
import aic.gas.sc.gg_bot.abstract_bot.utils.SerializationUtil;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation for DecisionLoadingService
 */
@Slf4j
public class DecisionLoadingServiceImpl implements DecisionLoadingService {

  private static DecisionLoadingServiceImpl instance = new DecisionLoadingServiceImpl();
  private final Map<MapSizeEnums, Map<ARace, Map<AgentTypeID, Map<DesireKeyID, Policy>>>> cache = new ConcurrentHashMap<>();
  private Map<AgentTypeID, Map<DesireKeyID, Policy>> loaded = new HashMap<>();

  /**
   * Initialize cache (loads models from resources)
   */
  private DecisionLoadingServiceImpl() {
    Stream.of(MapSizeEnums.values()).forEach(mapSize -> Stream.of(ARace.values())
        .filter(race -> !race.equals(ARace.UNKNOWN))
        .forEach(race -> DecisionConfiguration.decisionsToLoad
            .forEach((agentTypeID, desireKeyIDS) -> desireKeyIDS
                .forEach(
                    desireKeyID -> loadDecisionPoint(agentTypeID, desireKeyID, mapSize, race))))
    );
  }

  /**
   * Only one instance
   */
  public synchronized static DecisionLoadingService getInstance() {
    if (instance == null) {
      instance = new DecisionLoadingServiceImpl();
    }
    return instance;
  }

  /**
   * Try to load decision points for given keys and put it in to cache
   */
  private void loadDecisionPoint(AgentTypeID agentTypeID, DesireKeyID desireKeyID,
      MapSizeEnums mapSize, ARace race) {
    try {
      String fileName =
          "/" + mapSize.name() + "/" + race.name() + "/" + agentTypeID.getName() + "/" + desireKeyID
              .getName() + ".db";
      Policy policy = SerializationUtil.deserialize(
          DecisionLoadingServiceImpl.class.getResourceAsStream(fileName));

      //set cache
      policy.init();

      //are all actions present
      Set<NextActionEnumerations> actions = policy.getStates().stream()
          .flatMap(stateWithPolicy -> stateWithPolicy.getNextActions().entrySet().stream()
              .filter(entry -> entry.getValue() > 0)
              .map(Entry::getKey))
          .distinct()
          .collect(Collectors.toSet());
      if (Arrays.stream(NextActionEnumerations.values())
          .anyMatch(nextActionEnumerations -> !actions.contains(nextActionEnumerations))) {
        log.error("Missing action in " + mapSize.name() + ", " + race.name() + ", " + agentTypeID
            .getName() + ", " + desireKeyID.getName());
      }

      cache.computeIfAbsent(mapSize, id -> new HashMap<>())
          .computeIfAbsent(race, id -> new HashMap<>())
          .computeIfAbsent(agentTypeID, id -> new HashMap<>())
          .put(desireKeyID, policy);
      if (!loaded.containsKey(agentTypeID)) {
        Map<DesireKeyID, Policy> toAdd = new HashMap<>();
        toAdd.put(desireKeyID, policy);
        loaded.put(agentTypeID, toAdd);
      } else {
        if (!loaded.get(agentTypeID).containsKey(desireKeyID)) {
          loaded.get(agentTypeID).put(desireKeyID, policy);
        }
      }

    } catch (Exception e) {
      log.error(e.getMessage() + " for combination " + mapSize.name()
          + ", " + race.name() + ", " + agentTypeID.getName()
          + ", " + desireKeyID.getName() + " when loading decision.");
    }
  }

  @Override
  public Policy getDecisionPoint(AgentTypeID agentTypeID,
      DesireKeyID desireKeyID) {
    try {
      Policy policy = cache
          .get(DecisionConfiguration.getMapSize())
          .get(DecisionConfiguration.getRace())
          .get(agentTypeID).get(desireKeyID);

      //TODO hack
      if (policy == null) {
        //try to load at least some
        if (loaded.containsKey(agentTypeID) && loaded.get(agentTypeID).containsKey(desireKeyID)) {
          return loaded.get(agentTypeID).get(desireKeyID);
        }
      }

      return policy;
    } catch (Exception e) {

      //try to load at least some
      if (loaded.containsKey(agentTypeID) && loaded.get(agentTypeID).containsKey(desireKeyID)) {
        return loaded.get(agentTypeID).get(desireKeyID);
      }

      log.error(
          "No models of decision for combination " + DecisionConfiguration.getMapSize().name()
              + ", " + DecisionConfiguration.getRace().name() + ", " + agentTypeID.getName()
              + ", " + desireKeyID.getName());
      return null;
    }
  }
}
