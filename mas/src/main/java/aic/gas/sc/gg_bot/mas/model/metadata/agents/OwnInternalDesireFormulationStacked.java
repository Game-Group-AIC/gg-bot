package aic.gas.sc.gg_bot.mas.model.metadata.agents;

import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import aic.gas.sc.gg_bot.mas.model.planing.Intention;
import aic.gas.sc.gg_bot.mas.model.planing.InternalDesire;
import java.util.Optional;

/**
 * Contract for stacked desire formulations - which forms desires from agent's memory and based on
 * parent's type
 */
public interface OwnInternalDesireFormulationStacked<T extends InternalDesire<? extends Intention<T>>> {

  /**
   * Form desire of given key with data initialized from memory depending on parent's key. If no
   * parent is specified method simple desire will be formed instead
   */
  Optional<T> formDesire(DesireKey parentKey, DesireKey key, WorkingMemory memory,
      DesireParameters parentsDesireParameters);

  /**
   * Returns true if desire can be instantiated
   */
  boolean supportsDesireType(DesireKey parent, DesireKey key);

}
