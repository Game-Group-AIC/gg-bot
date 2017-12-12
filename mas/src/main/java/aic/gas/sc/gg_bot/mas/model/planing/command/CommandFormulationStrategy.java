package aic.gas.sc.gg_bot.mas.model.planing.command;

import aic.gas.sc.gg_bot.mas.model.planing.IntentionCommand;

/**
 * Contract for acting command creation strategy
 */
public interface CommandFormulationStrategy<V extends CommandForIntention<?>, T extends IntentionCommand<?, ?>> {

  /**
   * Form command from intention
   */
  V formCommand(T intention);
}
