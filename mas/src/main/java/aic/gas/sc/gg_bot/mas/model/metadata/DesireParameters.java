package aic.gas.sc.gg_bot.mas.model.metadata;

import aic.gas.sc.gg_bot.mas.model.IDesireKeyIdentification;
import aic.gas.sc.gg_bot.mas.model.IFactContainer;
import aic.gas.sc.gg_bot.mas.model.knowledge.FactSet;
import aic.gas.sc.gg_bot.mas.model.knowledge.Memory;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to define container with parameters (facts) for desire classes to be accessed by it. As
 * this class is read only sharing it is thread safe
 */
@Slf4j
public class DesireParameters implements IFactContainer, IDesireKeyIdentification {

  private final Map<FactKey, FactSet<?>> factSetParameterMap = new HashMap<>();

  @Getter
  private final DesireKey desireKey;

  public DesireParameters(Memory memory, DesireKey desireKey) {
    this.desireKey = desireKey;

    desireKey.getParametersTypesForFactSets()
        .forEach(factKey -> {
          Optional<FactSet<?>> value = memory.returnFactSetCopyForGivenKey(factKey);
          value.ifPresent(factSet -> factSetParameterMap.put(factKey, factSet));
        });
  }

  public <V> Optional<V> returnFactValueForGivenKey(FactKey<V> factKey) {
    FactSet<V> factSet = (FactSet<V>) factSetParameterMap.get(factKey);
    if (factSet != null) {
      return factSet.getContent().stream().filter(Objects::nonNull).findFirst();
    }
    log.error(factKey.getName() + " is not present in parameters.");
    return Optional.empty();
  }

  public <V, S extends Stream<V>> Optional<S> returnFactSetValueForGivenKey(FactKey<V> factKey) {
    FactSet<V> factSet = (FactSet<V>) factSetParameterMap.get(factKey);
    if (factSet != null) {
      return Optional.ofNullable((S) factSet.getContent().stream());
    }
    log.error(factKey.getName() + " is not present in parameters.");
    return Optional.empty();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DesireParameters that = (DesireParameters) o;

    if (!factSetParameterMap.equals(that.factSetParameterMap)) {
      return false;
    }
    return desireKey.equals(that.desireKey);
  }

  @Override
  public int hashCode() {
    int result = factSetParameterMap.hashCode();
    result = 31 * result + desireKey.hashCode();
    return result;
  }
}
