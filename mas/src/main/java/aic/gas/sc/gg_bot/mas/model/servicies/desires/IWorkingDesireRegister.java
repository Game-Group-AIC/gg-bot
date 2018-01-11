package aic.gas.sc.gg_bot.mas.model.servicies.desires;

import aic.gas.sc.gg_bot.mas.model.agents.Agent;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesire;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesireForAgents;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesireInRegister;
import aic.gas.sc.gg_bot.mas.model.servicies.WorkingRegister;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Concrete implementation of DesireRegister. This class is intended as working register - register
 * keeps up to date information about desires and is intended for mediator use only.
 */
public interface IWorkingDesireRegister extends WorkingRegister<IReadOnlyDesireRegister>,
    IReadOnlyDesireRegister {

  @Override
  default IReadOnlyDesireRegister getAsReadonlyRegister() {
    return this;
  }

  /**
   * Try to add desire to register. Returns true if desire is registered in register
   */
  boolean addedDesire(SharedDesireInRegister desireForOthers);

  /**
   * Remove agent from register
   */
  boolean removeAgent(Agent agentToRemove);

  /**
   * Removes desire from register and returns status of this operation
   */
  boolean removedDesire(SharedDesire desireForOthers);

  /**
   * Tries to commit agent to desire. If it is successful returns DesireFromAnotherAgent
   */
  Optional<SharedDesireForAgents> commitToDesire(Agent agentWhoWantsToCommitTo,
      SharedDesireForAgents desireForOthersHeWantsToCommitTo);

  /**
   * Tries to remove commitment of agent to desire.
   */
  boolean removeCommitmentToDesire(Agent agentWhoWantsToRemoveCommitment,
      SharedDesireForAgents desireHeWantsToRemoveCommitmentTo);

}
