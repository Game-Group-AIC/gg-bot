package aic.gas.sc.gg_bot.mas.model.planing.command;

import aic.gas.sc.gg_bot.mas.model.IFactContainer;
import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import aic.gas.sc.gg_bot.mas.model.planing.IntentionCommand;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Template class for CommandForIntention defines common data structure for some executable object
 * by agent
 */
public abstract class CommandForIntention<T extends IntentionCommand<?, ? extends CommandForIntention<T>>> implements
    ICommand, IFactContainer {

  private final T intention;

  protected CommandForIntention(T intention) {
    this.intention = intention;
  }

  @Override
  public <V> Optional<V> returnFactValueForGivenKey(FactKey<V> factKey) {
    return intention.returnFactValueForGivenKey(factKey);
  }

  @Override
  public <V, S extends Stream<V>> Optional<S> returnFactSetValueForGivenKey(FactKey<V> factKey) {
    return intention.returnFactSetValueForGivenKey(factKey);
  }


  /**
   * Get set of content of fact for given key from desire
   */
  protected <V> Optional<Stream<V>> returnFactSetValueForGivenKeyInDesireParameters(
      FactKey<V> factKey) {
    return intention.returnFactSetValueForGivenKeyInDesireParameters(factKey);
  }

}
