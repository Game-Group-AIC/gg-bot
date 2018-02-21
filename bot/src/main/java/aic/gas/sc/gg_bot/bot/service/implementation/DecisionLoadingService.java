package aic.gas.sc.gg_bot.bot.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DecisionConfiguration;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.MapSizeEnums;
import aic.gas.sc.gg_bot.abstract_bot.model.decision.MDPForDecisionWithPolicy;
import aic.gas.sc.gg_bot.abstract_bot.model.decision.NextActionEnumerations;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import aic.gas.sc.gg_bot.abstract_bot.service.IDecisionLoadingService;
import aic.gas.sc.gg_bot.abstract_bot.utils.SerializationUtil;
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
public class DecisionLoadingService implements IDecisionLoadingService {

  private static DecisionLoadingService instance = new DecisionLoadingService();
  private final Map<MapSizeEnums, Map<ARace, Map<AgentTypes, Map<DesireKeys, MDPForDecisionWithPolicy>>>> cache = new ConcurrentHashMap<>();
  private Map<AgentTypes, Map<DesireKeys, MDPForDecisionWithPolicy>> loaded = new HashMap<>();

  /**
   * Initialize cache (loads models from resources)
   */
  private DecisionLoadingService() {
    Stream.of(MapSizeEnums.values()).forEach(mapSize -> Stream.of(ARace.values())
        .filter(race -> !race.equals(ARace.UNKNOWN))
        .forEach(race -> DecisionConfiguration.decisionsToLoad
            .forEach((agentType, desireKeys) -> desireKeys
                .forEach(desireKey -> loadDecisionPoint(agentType, desireKey, mapSize, race))))
    );
  }

  /**
   * Only one instance
   */
  public synchronized static IDecisionLoadingService getInstance() {
    if (instance == null) {
      instance = new DecisionLoadingService();
    }
    return instance;
  }

  /**
   * Try to load decision points for given keys and put it in to cache
   */
  private void loadDecisionPoint(AgentTypes agentType, DesireKeys desireKeys,
      MapSizeEnums mapSize, ARace race) {
    try {
      String fileName =
          "/" + mapSize + "/" + race + "/" + agentType + "/" + desireKeys + ".db";
      MDPForDecisionWithPolicy mdpForDecisionWithPolicy = SerializationUtil.deserialize(
          DecisionLoadingService.class.getResourceAsStream(fileName));

      //set cache
      mdpForDecisionWithPolicy.initCache();

      //are all actions present
      Set<NextActionEnumerations> actions = mdpForDecisionWithPolicy.getStates().stream()
          .flatMap(stateWithPolicy -> stateWithPolicy.getNextActions().entrySet().stream()
              .filter(entry -> entry.getValue() > 0)
              .map(Entry::getKey))
          .distinct()
          .collect(Collectors.toSet());
      if (Arrays.stream(NextActionEnumerations.values())
          .anyMatch(nextActionEnumerations -> !actions.contains(nextActionEnumerations))) {
        log.error(
            "Missing action in " + mapSize + ", " + race + ", " + agentType + ", " + desireKeys);
      }

      cache.computeIfAbsent(mapSize, id -> new HashMap<>())
          .computeIfAbsent(race, id -> new HashMap<>())
          .computeIfAbsent(agentType, id -> new HashMap<>())
          .put(desireKeys, mdpForDecisionWithPolicy);
      if (!loaded.containsKey(agentType)) {
        Map<DesireKeys, MDPForDecisionWithPolicy> toAdd = new HashMap<>();
        toAdd.put(desireKeys, mdpForDecisionWithPolicy);
        loaded.put(agentType, toAdd);
      } else {
        if (!loaded.get(agentType).containsKey(desireKeys)) {
          loaded.get(agentType).put(desireKeys, mdpForDecisionWithPolicy);
        }
      }

    } catch (Exception e) {
      log.error(e.getMessage() + " for combination " +
          mapSize.name() + ", " + race.name() + ", " + agentType + ", " + desireKeys +
          " when loading decision.");
    }
  }

  @Override
  public MDPForDecisionWithPolicy getDecisionPoint(AgentTypes agentType, DesireKeys desireKeys) {
    try {
      MDPForDecisionWithPolicy mdpForDecisionWithPolicy = cache
          .get(DecisionConfiguration.getMapSize())
          .get(DecisionConfiguration.getRace())
          .get(agentType).get(desireKeys);

      //TODO hack
      if (mdpForDecisionWithPolicy == null) {
        //try to load at least some
        if (loaded.containsKey(agentType) && loaded.get(agentType).containsKey(desireKeys)) {
          return loaded.get(agentType).get(desireKeys);
        }
      }

      return mdpForDecisionWithPolicy;
    } catch (Exception e) {

      //try to load at least some
      if (loaded.containsKey(agentType) && loaded.get(agentType).containsKey(desireKeys)) {
        return loaded.get(agentType).get(desireKeys);
      }

      log.error(
          "No models of decision for combination " + DecisionConfiguration.getMapSize().name()
              + ", " + DecisionConfiguration.getRace().name() + ", " + agentType + ", "
              + desireKeys);
      return null;
    }
  }
}
