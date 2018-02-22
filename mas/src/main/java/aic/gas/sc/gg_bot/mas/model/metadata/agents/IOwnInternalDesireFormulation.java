package aic.gas.sc.gg_bot.mas.model.metadata.agents;

import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.planing.Intention;
import aic.gas.sc.gg_bot.mas.model.planing.InternalDesire;
import java.util.Optional;

/**
 * Contract for desire formulations - which forms desires from agent's memory
 */
interface IOwnInternalDesireFormulation<T extends InternalDesire<? extends Intention<T>>> {

  /**
   * Form desire of given key with data initialized from memory
   */
  Optional<T> formDesire(DesireKey key, WorkingMemory memory);
}
