package aic.gas.sc.gg_bot.mas.model.planing;

import aic.gas.sc.gg_bot.mas.model.DesireKeyIdentificationInterface;
import aic.gas.sc.gg_bot.mas.model.knowledge.Memory;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;

/**
 * Class describing template for desire. Desire instance represents high level abstraction of what
 * agent may want to achieve. <p>
 */
public abstract class Desire implements DesireKeyIdentificationInterface {

  @Getter
  final DesireParameters desireParameters;

  @Getter
  final int originatorId;

  Desire(DesireKey desireKey, Memory memory) {
    this.desireParameters = new DesireParameters(memory, desireKey);
    this.originatorId = memory.getAgentId();
  }

  Desire(DesireParameters desireParameters, int originatorId) {
    this.desireParameters = desireParameters;
    this.originatorId = originatorId;
  }

  public DesireKey getDesireKey() {
    return desireParameters.getDesireKey();
  }

  public <V> Optional<V> returnFactValueForGivenKeyInParameters(FactKey<V> factKey) {
    return desireParameters.returnFactValueForGivenKey(factKey);
  }

  public <V, S extends Stream<V>> Optional<S> returnFactSetValueForGivenKeyInParameters(
      FactKey<V> factKey) {
    return desireParameters.returnFactSetValueForGivenKey(factKey);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Desire)) {
      return false;
    }

    Desire desire = (Desire) o;

    return originatorId == desire.originatorId
        && desireParameters.equals(desire.desireParameters);
  }

  @Override
  public int hashCode() {
    int result = desireParameters.hashCode();
    result = 31 * result + originatorId;
    return result;
  }
}
