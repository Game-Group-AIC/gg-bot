package aic.gas.sc.gg_bot.replay_parser.service;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.MapSizeEnums;
import aic.gas.sc.gg_bot.abstract_bot.model.decision.DecisionPointDataStructure;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.Trajectory;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.TrajectoryWrapper;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * StorageService contract - to store/load entities
 */
public interface StorageService {


  /**
   * Save trajectories of given agent type for desire id
   */
  void saveTrajectory(AgentTypeID agentTypeID, DesireKeyID desireKeyID,
      List<Trajectory> trajectories);

  /**
   * Get all parsed agent types with their desires types contained in storage
   */
  Map<AgentTypeID, Set<DesireKeyID>> getParsedAgentTypesWithDesiresTypesContainedInStorage(
      MapSizeEnums mapSize, ARace race);

  /**
   * Get stored trajectories for given parameters
   */
  List<TrajectoryWrapper> getRandomListOfTrajectories(AgentTypeID agentTypeID,
      DesireKeyID desireKeyID,
      MapSizeEnums mapSize, ARace race, int limit);

  /**
   * Store learnt DecisionPointDataStructure
   */
  void storeLearntDecision(DecisionPointDataStructure structure, AgentTypeID agentTypeID,
      DesireKeyID desireKeyID, MapSizeEnums mapSize, ARace race) throws Exception;

  String getLearntDecisionPath(AgentTypeID agentTypeID, DesireKeyID desireKeyID,
      MapSizeEnums mapSize, ARace race);
}
