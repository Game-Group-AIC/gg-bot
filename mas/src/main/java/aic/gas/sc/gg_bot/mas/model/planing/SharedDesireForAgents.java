package aic.gas.sc.gg_bot.mas.model.planing;

import aic.gas.sc.gg_bot.mas.model.agents.Agent;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import java.util.Set;

/**
 * Concrete implementation of SharedDesire to be used by agents
 */
public class SharedDesireForAgents extends SharedDesire {

  SharedDesireForAgents(DesireKey desireKey, Agent originatedFromAgent,
      int limitOnNumberOfAgentsToCommit) {
    super(desireKey, originatedFromAgent, limitOnNumberOfAgentsToCommit);
  }

  SharedDesireForAgents(DesireParameters desireParameters, Agent originatedFromAgent,
      int limitOnNumberOfAgentsToCommit, Set<Agent> committedAgents) {
    super(desireParameters, originatedFromAgent, limitOnNumberOfAgentsToCommit, committedAgents);
  }

  /**
   * Updates internal set of committed agents by current one
   */
  public void updateCommittedAgentsSet(SharedDesireForAgents sharedDesireForAgentsInSystem) {
    committedAgents
        .removeIf(agent -> !sharedDesireForAgentsInSystem.committedAgents.contains(agent));
    committedAgents.addAll(sharedDesireForAgentsInSystem.committedAgents);
  }
}
