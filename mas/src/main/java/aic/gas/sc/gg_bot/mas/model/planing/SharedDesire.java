package aic.gas.sc.gg_bot.mas.model.planing;

import aic.gas.sc.gg_bot.mas.model.agents.Agent;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * Template class for tracking commitmentDecider to desire which issues some agent to be achieved by
 * other agents
 */
public abstract class SharedDesire extends Desire {

  @Getter
  protected final Agent originatedFromAgent;
  @Getter
  protected final int limitOnNumberOfAgentsToCommit;
  final Set<Agent> committedAgents = new HashSet<>();

  SharedDesire(DesireKey desireKey, Agent originatedFromAgent, int limitOnNumberOfAgentsToCommit) {
    super(desireKey, originatedFromAgent.getBeliefs());
    this.originatedFromAgent = originatedFromAgent;
    this.limitOnNumberOfAgentsToCommit = limitOnNumberOfAgentsToCommit;
  }

  SharedDesire(DesireParameters desireParameters, Agent originatedFromAgent,
      int limitOnNumberOfAgentsToCommit, Set<Agent> committedAgents) {
    super(desireParameters, originatedFromAgent.getId());
    this.originatedFromAgent = originatedFromAgent;
    this.limitOnNumberOfAgentsToCommit = limitOnNumberOfAgentsToCommit;
    this.committedAgents.addAll(committedAgents);
  }

  /**
   * Returns copy of set of committed agents
   */
  public Set<Agent> getCommittedAgents() {
    return new HashSet<>(committedAgents);
  }

  public int countOfCommittedAgents() {
    return committedAgents.size();
  }

  public Set<Integer> getIDsOfCommittedAgents(){
    return committedAgents.stream()
        .map(Agent::getId)
        .collect(Collectors.toSet());
  }

  /**
   * Returns if agent may try to commit to desire
   */
  public boolean mayTryToCommit() {
    return committedAgents.size() + 1 <= limitOnNumberOfAgentsToCommit;
  }

  /**
   * Returns if agent is committed to desire
   */
  public boolean isAgentCommittedToDesire(Agent agent) {
    return committedAgents.contains(agent);

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SharedDesire)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    SharedDesire that = (SharedDesire) o;

    if (limitOnNumberOfAgentsToCommit != that.limitOnNumberOfAgentsToCommit) {
      return false;
    }
    return originatedFromAgent.equals(that.originatedFromAgent);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + originatedFromAgent.hashCode();
    result = 31 * result + limitOnNumberOfAgentsToCommit;
    return result;
  }
}
