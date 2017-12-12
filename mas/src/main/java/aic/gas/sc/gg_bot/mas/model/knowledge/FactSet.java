package aic.gas.sc.gg_bot.mas.model.knowledge;

import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import aic.gas.sc.gg_bot.mas.service.MASFacade;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Getter;

/**
 * Generic type to store simple facts. Similarly to memory old content is removed from it - this
 * emulates decay/forgetting. The typical use case is for example to forget some enemies, allies met
 * long time ago.
 */
public class FactSet<V> {

  private final Map<V, Integer> decayMap;

  @Getter
  private final FactKey<V> type;

  public FactSet(Map<V, Integer> decayMap, FactKey<V> type) {
    this.decayMap = decayMap;
    this.type = type;
  }

  public FactSet(FactKey<V> type) {
    this.type = type;
    this.decayMap = new HashMap<>();
  }

  public Set<V> getContent() {
    return decayMap.keySet();
  }

  public void removeFact(V factValue) {
    decayMap.remove(factValue);
  }

  public void addFact(V factValue) {
    decayMap.put(factValue, 0);
  }

  public void eraseSet() {
    decayMap.clear();
  }

  /**
   * Returns copy of fact set. Content is cloned so using the content is thread safe
   */
  public FactSet<V> copyFact() {
    return new FactSet<>(MASFacade.CLONER.deepClone(decayMap), type);
  }

  /**
   * Method erases no longer relevant information
   */
  public void forget() {
    if (type.isFading()) {
      decayMap.forEach((v, integer) -> decayMap.put(v, integer + 1));
      decayMap.keySet()
          .removeIf(v -> decayMap.get(v) >= type.getHowLongStayInMemoryWithoutUpdate());
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    FactSet<?> factSet = (FactSet<?>) o;

    return decayMap.keySet().equals(factSet.decayMap.keySet()) && type.equals(factSet.type);
  }

  @Override
  public int hashCode() {
    int result = decayMap.keySet().hashCode();
    result = 31 * result + type.hashCode();
    return result;
  }
}
