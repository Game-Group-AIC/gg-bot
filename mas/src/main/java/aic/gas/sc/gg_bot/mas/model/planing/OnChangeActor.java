package aic.gas.sc.gg_bot.mas.model.planing;

import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import java.util.Optional;

/**
 * Interface with default method to add functionality to react on commitment change
 */
interface OnChangeActor {

  ReactionOnChangeStrategy DEFAULT_REACTION_DO_NOTHING = (memory, desireParameters) -> {
  };

  Optional<ReactionOnChangeStrategy> getReactionOnChangeStrategy();

  /**
   * React on commitment
   *
   * @return always true
   */
  default boolean actOnChange(WorkingMemory memory, DesireParameters desireParameters) {

    //execute provided strategy or the default one
    getReactionOnChangeStrategy().orElse(DEFAULT_REACTION_DO_NOTHING)
        .updateBeliefs(memory, desireParameters);

    //TODO - !!!HACK!!! - always return true as it is used in condition
    return true;
  }

}
