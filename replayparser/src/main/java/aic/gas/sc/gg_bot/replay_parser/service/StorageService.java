package aic.gas.sc.gg_bot.replay_parser.service;

import aic.gas.mas.model.metadata.AgentTypeID;
import aic.gas.mas.model.metadata.DesireKeyID;
import aic.gas.sc.gg_bot.abstract_bot.model.decision.DecisionPointDataStructure;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.Replay;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.Trajectory;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * StorageService contract - to store/load entities
 */
public interface StorageService {

  /**
   * Load replays associated with given files if exists
   */
  Set<File> filterNotPlayedReplays(Set<File> files);

  /**
   * Save or update given replay
   */
  void markReplayAsParsed(Replay replay);

  /**
   * Save trajectories of given agent type for desire id
   */
  void saveTrajectory(AgentTypeID agentTypeID, DesireKeyID desireKeyID,
      List<Trajectory> trajectories);

  /**
   * Get all parsed agent types with their desires types contained in storage
   */
  Map<AgentTypeID, Set<DesireKeyID>> getParsedAgentTypesWithDesiresTypesContainedInStorage();

  /**
   * Get stored trajectories for given parameters
   */
  List<Trajectory> getRandomListOfTrajectories(AgentTypeID agentTypeID, DesireKeyID desireKeyID,
      int limit);

  /**
   * Store learnt DecisionPointDataStructure
   */
  void storeLearntDecision(DecisionPointDataStructure structure, AgentTypeID agentTypeID,
      DesireKeyID desireKeyID) throws Exception;

}
