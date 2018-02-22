package aic.gas.sc.gg_bot.mas.model;

import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Interface to be implemented by each class working with facts. Returned values are references on
 * actual object (not copy of them) and is user responsibility to use them as read only as it can
 * lead to great mess (concurrency exception, unpredictable behaviour) as facts can be accessed by
 * various threads (copy is not returned due to performance issues)
 */
public interface IFactContainer {

  /**
   * Returns read only fact value for given fact key if it exists
   */
  <V> Optional<V> returnFactValueForGivenKey(FactKey<V> factKey);

  /**
   * Returns read only fact values as set for given fact key if it exists
   */
  <V, S extends Stream<V>> Optional<S> returnFactSetValueForGivenKey(FactKey<V> factKey);

}
