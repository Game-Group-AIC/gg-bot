package aic.gas.sc.gg_bot.bot.service.implementation;

import aic.gas.mas.model.metadata.AgentTypeID;
import aic.gas.mas.model.metadata.DesireKeyID;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DecisionConfiguration;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.MapSizeEnums;
import aic.gas.sc.gg_bot.abstract_bot.model.decision.DecisionPoint;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import aic.gas.sc.gg_bot.abstract_bot.service.DecisionLoadingService;
import aic.gas.sc.gg_bot.abstract_bot.utils.SerializationUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation for DecisionLoadingService
 */
@Slf4j
public class DecisionLoadingServiceImpl implements DecisionLoadingService {

  private static DecisionLoadingServiceImpl instance = null;
  private final Map<MapSizeEnums, Map<ARace, Map<AgentTypeID, Map<DesireKeyID, DecisionPoint>>>> cache = new ConcurrentHashMap<>();

  /**
   * Initialize cache (loads models from resources)
   */
  private DecisionLoadingServiceImpl() {
    Stream.of(MapSizeEnums.values()).forEach(mapSize -> Stream.of(ARace.values()).
        forEach(race -> DecisionConfiguration.decisionsToLoad
            .forEach((agentTypeID, desireKeyIDS) -> desireKeyIDS
                .forEach(
                    desireKeyID -> loadDecisionPoint(agentTypeID, desireKeyID, mapSize, race))))
    );
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
   * Try to load decision points for given keys and put it in to cache
   */
  private void loadDecisionPoint(AgentTypeID agentTypeID, DesireKeyID desireKeyID,
      MapSizeEnums mapSize, ARace race) {
    try {
      String fileName =
          "/" + mapSize.name() + "/" + race.name() + "/" + agentTypeID.getName() + "/" + desireKeyID
              .getName() + ".db";
      DecisionPoint decisionPoint = new DecisionPoint(SerializationUtil.deserialize(
          DecisionLoadingServiceImpl.class.getResourceAsStream(fileName)));
      cache.computeIfAbsent(mapSize, id -> new HashMap<>())
          .computeIfAbsent(race, id -> new HashMap<>())
          .computeIfAbsent(agentTypeID, id -> new HashMap<>()).put(desireKeyID, decisionPoint);
    } catch (Exception e) {
      log.error(e.getLocalizedMessage());
    }
  }

  @Override
  public DecisionPoint getDecisionPoint(AgentTypeID agentTypeID, DesireKeyID desireKeyID) {
    try {
      return cache.get(DecisionConfiguration.getMapSize()).get(DecisionConfiguration.getRace())
          .get(agentTypeID).get(desireKeyID);
    } catch (Exception e) {
      log.error(e.getLocalizedMessage());
      throw new RuntimeException(
          "No models of decision for combination " + DecisionConfiguration.getMapSize().name()
              + ", " + DecisionConfiguration.getRace().name() + ", " + agentTypeID.getName() + ", "
              + ", " + desireKeyID.getName());
    }
  }
}
