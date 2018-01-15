package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Common parent for all type wrappers
 */
@Slf4j
public abstract class AbstractWrapper<T> {

  @Getter
  final T type;

  @Getter
  private final String name;

  AbstractWrapper(T type, String name) {
    this.name = name;
    if (type == null) {
      log.error("Constructor: type is null.");
      throw new RuntimeException("Constructor: type is null.");
    }
    this.type = type;
  }

  /**
   * Returns true if given type equals to one of types passed as parameter.
   */
  boolean isType(T myType, T[] types) {
    for (T otherType : types) {
      if (myType.equals(otherType)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns true if wrapper represents bwapi type
   */
  public boolean isForType(T bwapiType) {
    return type.equals(bwapiType);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AbstractWrapper<?> that = (AbstractWrapper<?>) o;

    return name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return type.hashCode();
  }
}
