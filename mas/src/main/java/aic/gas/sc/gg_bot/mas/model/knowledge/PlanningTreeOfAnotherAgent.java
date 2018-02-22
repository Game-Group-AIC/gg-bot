package aic.gas.sc.gg_bot.mas.model.knowledge;

import aic.gas.sc.gg_bot.mas.model.IPlanningTree;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import java.util.Map;
import java.util.Set;

/**
 * Read only copy of agent's planning heap to be shared with other agents
 */
public class PlanningTreeOfAnotherAgent implements IPlanningTree {

  private final Map<DesireKey, Long> keysOfCommittedDesiresInTreeCounts;
  private final Map<DesireKey, Long> keysOfDesiresInTreeCounts;
  private final Set<DesireParameters> parametersOfCommittedDesiresOnTopLevel;
  private final Set<DesireParameters> parametersOfDesiresOnTopLevel;
  private final Set<DesireParameters> sharedDesiresParametersByOtherAgents;
  private final Set<DesireParameters> sharedDesiresParameters;

  public PlanningTreeOfAnotherAgent(Map<DesireKey, Long> keysOfCommittedDesiresInTreeCounts,
      Map<DesireKey, Long> keysOfDesiresInTreeCounts,
      Set<DesireParameters> parametersOfCommittedDesiresOnTopLevel,
      Set<DesireParameters> parametersOfDesiresOnTopLevel,
      Set<DesireParameters> sharedDesiresParametersByOtherAgents,
      Set<DesireParameters> sharedDesiresParameters) {
    this.keysOfCommittedDesiresInTreeCounts = keysOfCommittedDesiresInTreeCounts;
    this.keysOfDesiresInTreeCounts = keysOfDesiresInTreeCounts;
    this.parametersOfCommittedDesiresOnTopLevel = parametersOfCommittedDesiresOnTopLevel;
    this.parametersOfDesiresOnTopLevel = parametersOfDesiresOnTopLevel;
    this.sharedDesiresParametersByOtherAgents = sharedDesiresParametersByOtherAgents;
    this.sharedDesiresParameters = sharedDesiresParameters;
  }

  @Override
  public Set<DesireParameters> committedSharedDesiresParametersByOtherAgents() {
    return sharedDesiresParametersByOtherAgents;
  }

  @Override
  public Set<DesireParameters> sharedDesiresParameters() {
    return sharedDesiresParameters;
  }

  @Override
  public int countOfCommittedSharedDesiresByOtherAgents() {
    return sharedDesiresParametersByOtherAgents.size();
  }

  @Override
  public int countOfSharedDesires() {
    return sharedDesiresParameters.size();
  }

  @Override
  public Map<DesireKey, Long> collectKeysOfCommittedDesiresInTreeCounts() {
    return keysOfCommittedDesiresInTreeCounts;
  }

  @Override
  public Map<DesireKey, Long> collectKeysOfDesiresInTreeCounts() {
    return keysOfDesiresInTreeCounts;
  }

  @Override
  public Set<DesireParameters> getParametersOfCommittedDesiresOnTopLevel() {
    return parametersOfCommittedDesiresOnTopLevel;
  }

  @Override
  public Set<DesireParameters> getParametersOfDesiresOnTopLevel() {
    return parametersOfDesiresOnTopLevel;
  }
}
