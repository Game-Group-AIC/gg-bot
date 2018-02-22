package aic.gas.sc.gg_bot.mas.model;

import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import java.util.Map;
import java.util.Set;

/**
 * Contract for planning heap (and read only version of it shared with other agents) with methods to
 * get some metadata about planning
 */
public interface IPlanningTree {

  /**
   * Get set of desires type shared by other agents
   */
  Set<DesireParameters> committedSharedDesiresParametersByOtherAgents();

  /**
   * Get set of desires type shared by this agent
   */
  Set<DesireParameters> sharedDesiresParameters();

  /**
   * Return count of shared desires by other agents
   */
  int countOfCommittedSharedDesiresByOtherAgents();

  /**
   * Return count of shared desires
   */
  int countOfSharedDesires();

  /**
   * Method to get counts of types of intentions in heap
   */
  Map<DesireKey, Long> collectKeysOfCommittedDesiresInTreeCounts();

  /**
   * Method to get counts of types of desires in heap
   */
  Map<DesireKey, Long> collectKeysOfDesiresInTreeCounts();

  /**
   * Get parameters of desires agent is committed to on top level
   */
  Set<DesireParameters> getParametersOfCommittedDesiresOnTopLevel();

  /**
   * Get parameters of desires agent can commit to on top level
   */
  Set<DesireParameters> getParametersOfDesiresOnTopLevel();

}
