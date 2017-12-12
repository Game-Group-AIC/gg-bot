package aic.gas.sc.gg_bot.mas.model.metadata;

import aic.gas.sc.gg_bot.mas.model.FactContainerInterface;
import aic.gas.sc.gg_bot.mas.model.knowledge.Fact;
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
public class DesireKey extends DesireKeyID implements FactContainerInterface {

  private final Map<FactKey<?>, Fact<?>> factParameterMap = new HashMap<>();
  private final Map<FactKey<?>, FactSet<?>> factSetParameterMap = new HashMap<>();

  @Getter
  private final Set<FactKey<?>> parametersTypesForFacts;

  @Getter
  private final Set<FactKey<?>> parametersTypesForFactSets;

  @Builder
  private DesireKey(DesireKeyID id, Set<Fact<?>> staticFactValues, Set<FactSet<?>> staticFactSets,
      Set<FactKey<?>> parametersTypesForFacts, Set<FactKey<?>> parametersTypesForFactSets) {
    super(id.getName(), id.getID());
    staticFactValues.forEach(fact -> factParameterMap.put(fact.getType(), fact));
    staticFactSets.forEach(factSet -> factSetParameterMap.put(factSet.getType(), factSet));
    this.parametersTypesForFacts = parametersTypesForFacts;
    this.parametersTypesForFactSets = parametersTypesForFactSets;
  }

  public Set<FactKey<?>> parametersTypesForStaticFacts() {
    return factParameterMap.keySet();
  }

  public Set<FactKey<?>> parametersTypesForStaticFactsSets() {
    return factSetParameterMap.keySet();
  }

  @Override
  public <K> Optional<K> returnFactValueForGivenKey(FactKey<K> factKey) {
    Fact<K> fact = (Fact<K>) factParameterMap.get(factKey);
    if (fact != null) {
      return Optional.ofNullable(fact.getContent());
    }
    log.error(factKey.getName() + " is not present in " + this.getName() + " type definition.");
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

    private Set<Fact<?>> staticFactValues = new HashSet<>();
    private Set<FactSet<?>> staticFactSets = new HashSet<>();
    private Set<FactKey<?>> parametersTypesForFacts = new HashSet<>();
    private Set<FactKey<?>> parametersTypesForFactSets = new HashSet<>();
  }

}
