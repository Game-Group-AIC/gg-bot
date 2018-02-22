package aic.gas.sc.gg_bot.mas.model.metadata;

import aic.gas.sc.gg_bot.mas.model.knowledge.FactSet;
import lombok.Getter;

/**
 * Simple class describing metadata for fact - used for identification. It contains factory method
 * do instantiate new fact of this type.
 */
public abstract class FactKey<V> extends Key {

  @Getter
  private final int howLongStayInMemoryWithoutUpdate;

  @Getter
  private final boolean isFading;

  @Getter
  private final boolean isPrivate;

  public FactKey(String name, int howLongStayInMemoryWithoutUpdate, boolean isPrivate) {
    super(name, FactKey.class);
    this.howLongStayInMemoryWithoutUpdate = howLongStayInMemoryWithoutUpdate;
    this.isPrivate = isPrivate;
    this.isFading = true;
  }

  public FactKey(String name, boolean isPrivate) {
    super(name, FactKey.class);
    this.isPrivate = isPrivate;
    this.howLongStayInMemoryWithoutUpdate = Integer.MAX_VALUE;
    this.isFading = false;
  }


  public abstract V getInitValue();

  /**
   * Returns new instance of fact set of this type with initialization value
   */
  public FactSet<V> returnEmptyFactSet() {
    return new FactSet<>(this);
  }

}
