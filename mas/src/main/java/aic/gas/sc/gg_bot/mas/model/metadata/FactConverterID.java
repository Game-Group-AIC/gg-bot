package aic.gas.sc.gg_bot.mas.model.metadata;

import lombok.Getter;

/**
 * Class to identify fact converter
 */
public class FactConverterID<V> implements Converter {

  private final int id;

  @Getter
  private final FactKey<V> factKey;

  public FactConverterID(int id, FactKey<V> factKey) {
    this.factKey = factKey;
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    FactConverterID that = (FactConverterID) o;

    return id == that.id && factKey.equals(that.factKey);
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + factKey.hashCode();
    return result;
  }

  @Override
  public int getID() {
    return id;
  }
}
