package aic.gas.sc.gg_bot.mas.model.knowledge;

import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import aic.gas.sc.gg_bot.mas.service.MASFacade;
import lombok.Getter;

/**
 * Generic type of knowledge content
 */
public class Fact<V> {

  @Getter
  private final FactKey<V> type;
  private V content;
  private int decay = 0;

  public Fact(V content, FactKey<V> type) {
    this.content = content;
    this.type = type;
  }

  public V getContent() {
    return content;
  }

  public void removeFact() {
    this.content = type.getInitValue();
    decay = 0;
  }

  public void addFact(V factValue) {
    this.content = factValue;
    if (type.isFading()) {
      decay = 0;
    }
  }

  /**
   * Method erases no longer relevant information
   */
  public void forget() {
    if (type.isFading()) {
      decay++;
      if (decay >= type.getHowLongStayInMemoryWithoutUpdate()) {
        removeFact();
      }
    }
  }

  /**
   * Returns copy of fact. Content is cloned so using the content is thread safe
   */
  public Fact<V> copyFact() {
    return new Fact<>(content, type);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Fact<?> fact = (Fact<?>) o;

    if (!type.equals(fact.type)) {
      return false;
    }
    return content != null ? content.equals(fact.content) : fact.content == null;
  }

  @Override
  public int hashCode() {
    int result = type.hashCode();
    result = 31 * result + (content != null ? content.hashCode() : 0);
    return result;
  }
}
