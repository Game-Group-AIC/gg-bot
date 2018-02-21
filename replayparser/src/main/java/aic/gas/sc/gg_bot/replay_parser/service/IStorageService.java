package aic.gas.sc.gg_bot.replay_parser.service;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.MapSizeEnums;
import aic.gas.sc.gg_bot.abstract_bot.model.decision.MDPForDecisionWithPolicy;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.Trajectory;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.TrajectoryWrapper;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * IStorageService contract - to store/load entities
 */
public interface IStorageService {


  /**
   * Save trajectories of given agent type for desire id
   */
  void saveTrajectory(AgentTypes agentType, DesireKeys desireKey,
      List<Trajectory> trajectories);

  /**
   * Get all parsed agent types with their desires types contained in storage
   */
  Map<AgentTypes, Set<DesireKeys>> getParsedAgentTypesWithDesiresTypesContainedInStorage(
      MapSizeEnums mapSize, ARace race);

  /**
   * Get stored trajectories for given parameters
   */
  List<TrajectoryWrapper> getRandomListOfTrajectories(AgentTypes agentType,
      DesireKeys desireKEy,
      MapSizeEnums mapSize, ARace race, int limit);

  /**
   * Store learnt MDPForDecisionWithPolicy
   */
  void storeLearntDecision(MDPForDecisionWithPolicy structure, AgentTypes agentType,
      DesireKeys desireKEy, MapSizeEnums mapSize, ARace race) throws Exception;

  String getLearntDecisionPath(AgentTypes agentType, DesireKeys desireKEy,
      MapSizeEnums mapSize, ARace race);
}
