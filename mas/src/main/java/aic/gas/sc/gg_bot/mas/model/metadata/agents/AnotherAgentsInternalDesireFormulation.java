package aic.gas.sc.gg_bot.mas.model.metadata.agents;

import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.planing.DesireFromAnotherAgent;
import aic.gas.sc.gg_bot.mas.model.planing.Intention;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesireForAgents;
import java.util.Optional;

/**
 * Contract for desire formulations - which forms desires from shared desire by another agent
 */
interface AnotherAgentsInternalDesireFormulation<T extends DesireFromAnotherAgent<? extends Intention<T>>> {

  /**
   * Form desire of given desire from another agent
   */
  Optional<T> formDesire(SharedDesireForAgents desireForAgents, WorkingMemory memory);

}
