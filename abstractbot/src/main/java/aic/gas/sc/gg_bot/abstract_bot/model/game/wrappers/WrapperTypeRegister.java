package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Register to keep registered types
 */
class WrapperTypeRegister<T, V extends AbstractWrapper<T>> {

  private final Map<T, V> types = new ConcurrentHashMap<>();
  private final StrategyToWrapType<T, V> strategyToWrapType;

  WrapperTypeRegister(StrategyToWrapType<T, V> strategyToWrapType) {
    this.strategyToWrapType = strategyToWrapType;
  }

  void addWrappedType(T t, V v) {
    types.put(t, v);
  }

  /**
   * Clear cache
   */
  void clear() {
    types.clear();
  }

  /**
   * Returns corresponding wrapper for type instance
   */
  V createFrom(T type) {
    if (!types.containsKey(type)) {
      return strategyToWrapType.createNewWrapper(type);
    }
    return types.get(type);
  }

}
