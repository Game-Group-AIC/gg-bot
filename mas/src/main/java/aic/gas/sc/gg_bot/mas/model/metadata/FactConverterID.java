package aic.gas.sc.gg_bot.mas.model.metadata;

import lombok.Getter;

/**
 * Class to identify fact converter
 */
@Getter
public class FactConverterID<V> implements Converter {

  private final int id;
  private final String name;
  private final FactKey<V> factKey;

  public FactConverterID(int id, FactKey<V> factKey, String name) {
    this.factKey = factKey;
    this.id = id;
    this.name = name;
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
  public String toString() {
    return "FactConverter: " + "id=" + id + ", name='" + name;
  }
}
