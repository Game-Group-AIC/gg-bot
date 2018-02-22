package aic.gas.sc.gg_bot.mas.model.metadata;

import aic.gas.sc.gg_bot.mas.model.FactContainerInterface;
import aic.gas.sc.gg_bot.mas.model.knowledge.FactSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Class describing metadata for desire - used for identification and parameter type definition.
 */
@Slf4j
public class DesireKey implements FactContainerInterface {

  private final Map<FactKey<?>, FactSet<?>> factSetParameterMap = new HashMap<>();

  @Getter
  private final Set<FactKey<?>> parametersTypesForFacts;

  @Getter
  private final Set<FactKey<?>> parametersTypesForFactSets;

  @Getter
  private final DesireKeyID desireKeyId;

  @Builder
  private DesireKey(
      DesireKeyID desireKeyId,
      Set<FactSet<?>> staticFactSets,
      Set<FactKey<?>> parametersTypesForFacts,
      Set<FactKey<?>> parametersTypesForFactSets) {
    this.desireKeyId = desireKeyId;
    staticFactSets.forEach(factSet -> factSetParameterMap.put(factSet.getType(), factSet));
    this.parametersTypesForFacts = parametersTypesForFacts;
    this.parametersTypesForFactSets = parametersTypesForFactSets;
  }

  public String getName() {
    return desireKeyId.getName();
  }

  public int getId() {
    return desireKeyId.getID();
  }

  public Set<FactKey<?>> parametersTypesForStaticFactsSets() {
    return factSetParameterMap.keySet();
  }

  public <V> Optional<V> returnFactValueForGivenKey(FactKey<V> factKey) {
    FactSet<V> factSet = (FactSet<V>) factSetParameterMap.get(factKey);
    if (factSet != null) {
      return factSet.getContent().stream().findFirst();
    }
    log.error(factKey.getName() + " is not present in parameters.");
    return Optional.empty();
  }

  @Override
  public <K, S extends Stream<K>> Optional<S> returnFactSetValueForGivenKey(FactKey<K> factKey) {
    FactSet<K> factSet = (FactSet<K>) factSetParameterMap.get(factKey);
    if (factSet != null) {
      return Optional.ofNullable((S) factSet.getContent().stream());
    }
    log.error(factKey.getName() + " is not present in " + this.getName() + " type definition.");
    return Optional.empty();
  }

  //builder with default fields
  public static class DesireKeyBuilder {
    private Set<FactSet<?>> staticFactSets = new HashSet<>();
    private Set<FactKey<?>> parametersTypesForFacts = new HashSet<>();
    private Set<FactKey<?>> parametersTypesForFactSets = new HashSet<>();
  }

}
