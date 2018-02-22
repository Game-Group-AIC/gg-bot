package aic.gas.sc.gg_bot.replay_parser.model.watcher;

import aic.gas.sc.gg_bot.mas.model.knowledge.FactSet;
import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactValueSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to represent agent's beliefs
 */
@Slf4j
public class Beliefs {

  //own beliefs
  private final Map<FactKey<?>, FactSet<?>> factSets = new ConcurrentHashMap<>();

  Beliefs(AgentWatcherType type) {
    type.getFactSetsKeys().forEach(factKey -> factSets.put(factKey, factKey.returnEmptyFactSet()));
  }

  public <K> Optional<K> returnFactValueForGivenKey(FactKey<K> factKey) {
    FactSet<K> factSet = (FactSet<K>) factSets.get(factKey);
    if (factSet != null) {
      return factSet.getContent().stream().findFirst();
    }
    log.error(factKey.getName() + " is not present.");
    return Optional.empty();
  }

  public <K, S extends Stream<K>> Optional<S> returnFactSetValueForGivenKey(FactKey<K> factKey) {
    FactSet<K> factSet = (FactSet<K>) factSets.get(factKey);
    if (factSet != null) {
      return Optional.ofNullable((S) factSet.getContent().stream());
    }
    log.error(factKey.getName() + " is not present.");
    return Optional.empty();
  }

  /**
   * Convert fact set to feature value
   */
  public <V> double getFeatureValueOfFactSet(FactValueSet<V> convertingStrategy) {
    if (!factSets.containsKey(convertingStrategy.getFactKey())) {
      log.error(convertingStrategy.getFactKey().getName() + " is not present in.");
      throw new RuntimeException(convertingStrategy.getFactKey().getName());
    }
    return convertingStrategy.getStrategyToObtainValue().returnRawValue(Optional.ofNullable(
        ((Set<V>) factSets.get(convertingStrategy.getFactKey()).getContent()).stream()));
  }

  /**
   * Update fact value
   */
  public <V> void updateFactSetByFact(FactKey<V> factKey, V value) {
    FactSet<V> factSet = (FactSet<V>) factSets.get(factKey);
    if (factSet != null) {
      factSet.addFact(value);
    } else {
      log.error(factKey.getName() + " is not present in.");
      throw new RuntimeException(factKey.getName());
    }
  }

  /**
   * Update fact value
   */
  public <V> void updateFactSetByFacts(FactKey<V> factKey, Set<V> values) {
    FactSet<V> factSet = (FactSet<V>) factSets.get(factKey);
    if (factSet != null) {
      factSet.eraseSet();
      values.forEach(factSet::addFact);
    } else {
      log.error(factKey.getName() + " is not present in.");
      throw new RuntimeException(factKey.getName());
    }
  }

  /**
   * Erase fact from set
   */
  public <V> void eraseFactFromFactSet(FactKey<V> factKey, V value) {
    FactSet<V> factSet = (FactSet<V>) factSets.get(factKey);
    if (factSet != null) {
      factSet.removeFact(value);
    } else {
      log.error(factKey.getName() + " is not present in.");
      throw new RuntimeException(factKey.getName());
    }
  }

  /**
   * Erase fact set under given key
   */
  public <V> void eraseFactSetForGivenKey(FactKey<V> factKey) {
    FactSet<V> factSet = (FactSet<V>) factSets.get(factKey);
    if (factSet != null) {
      factSet.eraseSet();
    } else {
      log.error(factKey.getName() + " is not present in.");
      throw new RuntimeException(factKey.getName());
    }
  }

  public boolean isFactKeyForSetInMemory(FactKey<?> factKey) {
    return factSets.containsKey(factKey);
  }

}
