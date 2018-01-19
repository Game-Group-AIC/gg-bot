package aic.gas.sc.gg_bot.mas.model.planing;

import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import java.util.Optional;

/**
 * Interface with default method to add functionality to react on commitment change
 */
interface OnChangeActor {

  /**
   * React on commitment
   */
  default void actOnRemoval(WorkingMemory memory, DesireParameters desireParameters,
      Optional<ReactionOnChangeStrategy> providedReaction) {
    providedReaction.ifPresent(reaction -> reaction.updateBeliefs(memory, desireParameters));
  }

}
