package aic.gas.sc.gg_bot.mas.model.metadata;

import aic.gas.sc.gg_bot.mas.model.FeatureRawValueObtainingStrategy;
import aic.gas.sc.gg_bot.mas.model.knowledge.DataForDecision;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;

/**
 * Template for feature
 */
public abstract class FactConverter<V, K> implements Converter {

  private final DataForDecision dataForDecision;
  private final FeatureRawValueObtainingStrategy<V> strategyToObtainValue;
  private final int id;
  @Getter
  private Double value = null;

  FactConverter(DataForDecision dataForDecision,
      FeatureRawValueObtainingStrategy<V> strategyToObtainValue, int id) {
    this.dataForDecision = dataForDecision;
    this.strategyToObtainValue = strategyToObtainValue;
    this.id = id;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    FactConverter<?, ?> that = (FactConverter<?, ?>) o;

    return id == that.id;
  }

  @Override
  public int hashCode() {
    return id;
  }

  /**
   * Returns true if local value is different from value updated by data from register
   */
  public abstract void hasUpdatedValueFromRegisterChanged(K register);

  /**
   * Return true if feature value has changed for new belief
   */
  void hasValueChanged(V belief) {
    Double oldValue = this.value;
    this.value = strategyToObtainValue.returnRawValue(belief);
    if (oldValue == null || (oldValue.doubleValue() != this.value && !dataForDecision
        .isBeliefsChanged())) {
      dataForDecision.setBeliefsChanged(true);
    }
  }

  /**
   * For belief
   */
  public static class BeliefFromKey<V> extends FactConverter<Optional<V>, DesireKey> {

    private final FactWithOptionalValue<V> converter;

    public BeliefFromKey(DataForDecision dataForDecision, DesireKey desireKey,
        FactWithOptionalValue<V> container) {
      super(dataForDecision, container.getStrategyToObtainValue(), container.getId());
      this.converter = container;
      hasUpdatedValueFromRegisterChanged(desireKey);
    }

    @Override
    public void hasUpdatedValueFromRegisterChanged(DesireKey register) {
      hasValueChanged(register.returnFactValueForGivenKey(converter.getFactKey()));
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      if (!super.equals(o)) {
        return false;
      }

      BeliefFromKey<?> that = (BeliefFromKey<?>) o;

      return converter.equals(that.converter);
    }

    @Override
    public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + converter.hashCode();
      return result;
    }

    @Override
    public String getName() {
      return converter.getName();
    }
  }

  /**
   * For belief set
   */
  public static class BeliefSetFromKey<V> extends FactConverter<Optional<Stream<V>>, DesireKey> {

    private final FactConverterID<V> converter;

    public BeliefSetFromKey(DataForDecision dataForDecision, DesireKey desireKey,
        FactWithOptionalValueSet<V> container) {
      super(dataForDecision, container.getStrategyToObtainValue(), container.getId());
      this.converter = container;
      hasUpdatedValueFromRegisterChanged(desireKey);
    }

    @Override
    public void hasUpdatedValueFromRegisterChanged(DesireKey register) {
      hasValueChanged(register.returnFactSetValueForGivenKey(converter.getFactKey()));
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      BeliefSetFromKey<?> that = (BeliefSetFromKey<?>) o;

      return converter.equals(that.converter);
    }

    @Override
    public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + converter.hashCode();
      return result;
    }

    @Override
    public String getName() {
      return converter.getName();
    }
  }

  /**
   * For belief
   */
  public static class BeliefFromDesire<V> extends FactConverter<Optional<V>, DesireParameters> {

    private final FactWithOptionalValue<V> converter;

    public BeliefFromDesire(DataForDecision dataForDecision, DesireParameters desireParameters,
        FactWithOptionalValue<V> container) {
      super(dataForDecision, container.getStrategyToObtainValue(), container.getId());
      this.converter = container;
      hasUpdatedValueFromRegisterChanged(desireParameters);
    }

    @Override
    public void hasUpdatedValueFromRegisterChanged(DesireParameters register) {
      hasValueChanged(register.returnFactValueForGivenKey(converter.getFactKey()));
    }

    @Override
    public String getName() {
      return converter.getName();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      BeliefFromDesire<?> that = (BeliefFromDesire<?>) o;

      return converter.equals(that.converter);
    }

    @Override
    public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + converter.hashCode();
      return result;
    }
  }

  /**
   * For belief set
   */
  public static class BeliefSetFromDesire<V> extends
      FactConverter<Optional<Stream<V>>, DesireParameters> {

    private final FactWithOptionalValueSet<V> converter;

    public BeliefSetFromDesire(DataForDecision dataForDecision, DesireParameters desireParameters,
        FactWithOptionalValueSet<V> container) {
      super(dataForDecision, container.getStrategyToObtainValue(), container.getId());
      this.converter = container;
      hasUpdatedValueFromRegisterChanged(desireParameters);
    }

    @Override
    public void hasUpdatedValueFromRegisterChanged(DesireParameters register) {
      hasValueChanged(register.returnFactSetValueForGivenKey(converter.getFactKey()));
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      BeliefSetFromDesire<?> that = (BeliefSetFromDesire<?>) o;

      return converter.equals(that.converter);
    }

    @Override
    public String getName() {
      return converter.getName();
    }

    @Override
    public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + converter.hashCode();
      return result;
    }
  }

  /**
   * For belief
   */
  public static class Belief<V> extends FactConverter<Optional<V>, WorkingMemory> {

    private final FactWithOptionalValue<V> converter;

    public Belief(DataForDecision dataForDecision, FactWithOptionalValue<V> container) {
      super(dataForDecision, container.getStrategyToObtainValue(), container.getId());
      this.converter = container;
    }

    @Override
    public void hasUpdatedValueFromRegisterChanged(WorkingMemory register) {
      hasValueChanged(register.returnFactValueForGivenKey(converter.getFactKey()));
    }

    @Override
    public String getName() {
      return converter.getName();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      Belief<?> that = (Belief<?>) o;

      return converter.equals(that.converter);
    }

    @Override
    public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + converter.hashCode();
      return result;
    }
  }

  /**
   * For global beliefs
   */
  public static class GlobalBelief<V> extends FactConverter<Stream<Optional<V>>, WorkingMemory> {

    private final FactWithSetOfOptionalValues<V> converter;

    public GlobalBelief(DataForDecision dataForDecision, FactWithSetOfOptionalValues<V> container) {
      super(dataForDecision, container.getStrategyToObtainValue(), container.getId());
      this.converter = container;
    }

    @Override
    public void hasUpdatedValueFromRegisterChanged(WorkingMemory register) {
      hasValueChanged(register.getReadOnlyMemories().filter(
          readOnlyMemory -> readOnlyMemory.isFactKeyForValueInMemory(converter.getFactKey()))
          .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(converter.getFactKey()))
      );
    }

    @Override
    public String getName() {
      return converter.getName();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      GlobalBelief<?> that = (GlobalBelief<?>) o;

      return converter.equals(that.converter);
    }

    @Override
    public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + converter.hashCode();
      return result;
    }
  }

  /**
   * For global beliefs of agent type
   */
  public static class GlobalBeliefForAgentType<V> extends
      FactConverter<Stream<Optional<V>>, WorkingMemory> {

    private final FactWithSetOfOptionalValuesForAgentType<V> converter;

    public GlobalBeliefForAgentType(DataForDecision dataForDecision,
        FactWithSetOfOptionalValuesForAgentType<V> container) {
      super(dataForDecision, container.getStrategyToObtainValue(), container.getId());
      this.converter = container;
    }

    @Override
    public void hasUpdatedValueFromRegisterChanged(WorkingMemory register) {
      hasValueChanged(register.getReadOnlyMemoriesForAgentType(converter.getAgentTypeID()).filter(
          readOnlyMemory -> readOnlyMemory.isFactKeyForValueInMemory(converter.getFactKey()))
          .map(
              readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(converter.getFactKey())));
    }

    @Override
    public String getName() {
      return converter.getName();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      GlobalBeliefForAgentType<?> that = (GlobalBeliefForAgentType<?>) o;

      return converter.equals(that.converter);
    }

    @Override
    public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + converter.hashCode();
      return result;
    }
  }

  /**
   * For belief set
   */
  public static class BeliefSet<V> extends FactConverter<Optional<Stream<V>>, WorkingMemory> {

    private final FactWithOptionalValueSet<V> converter;

    public BeliefSet(DataForDecision dataForDecision, FactWithOptionalValueSet<V> container) {
      super(dataForDecision, container.getStrategyToObtainValue(), container.getId());
      this.converter = container;
    }

    @Override
    public void hasUpdatedValueFromRegisterChanged(WorkingMemory register) {
      hasValueChanged(register.returnFactSetValueForGivenKey(converter.getFactKey()));
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      if (!super.equals(o)) {
        return false;
      }

      BeliefSet<?> beliefSet = (BeliefSet<?>) o;

      return converter.equals(beliefSet.converter);
    }

    @Override
    public String getName() {
      return converter.getName();
    }

    @Override
    public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + converter.hashCode();
      return result;
    }
  }

  /**
   * For global belief sets
   */
  public static class GlobalBeliefSet<V> extends
      FactConverter<Stream<Optional<Stream<V>>>, WorkingMemory> {

    private final FactWithOptionalValueSets<V> converter;

    public GlobalBeliefSet(DataForDecision dataForDecision,
        FactWithOptionalValueSets<V> container) {
      super(dataForDecision, container.getStrategyToObtainValue(), container.getId());
      this.converter = container;
    }

    @Override
    public void hasUpdatedValueFromRegisterChanged(WorkingMemory register) {
      hasValueChanged(register.getReadOnlyMemories()
          .filter(readOnlyMemory -> readOnlyMemory.isFactKeyForSetInMemory(converter.getFactKey()))
          .map(readOnlyMemory -> readOnlyMemory
              .returnFactSetValueForGivenKey(converter.getFactKey()))
      );
    }

    @Override
    public String getName() {
      return converter.getName();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      if (!super.equals(o)) {
        return false;
      }

      GlobalBeliefSet<?> that = (GlobalBeliefSet<?>) o;

      return converter.equals(that.converter);
    }

    @Override
    public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + converter.hashCode();
      return result;
    }
  }

  /**
   * For global belief sets of agent type
   */
  public static class GlobalBeliefSetForAgentType<V> extends
      FactConverter<Stream<Optional<Stream<V>>>, WorkingMemory> {

    private final FactWithOptionalValueSetsForAgentType<V> converter;

    public GlobalBeliefSetForAgentType(DataForDecision dataForDecision,
        FactWithOptionalValueSetsForAgentType<V> container) {
      super(dataForDecision, container.getStrategyToObtainValue(), container.getId());
      this.converter = container;
    }

    @Override
    public void hasUpdatedValueFromRegisterChanged(WorkingMemory register) {
      hasValueChanged(register.getReadOnlyMemoriesForAgentType(converter.getAgentType()).filter(
          readOnlyMemory -> readOnlyMemory.isFactKeyForSetInMemory(converter.getFactKey()))
          .map(readOnlyMemory -> readOnlyMemory
              .returnFactSetValueForGivenKey(converter.getFactKey())));
    }

    @Override
    public String getName() {
      return converter.getName();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      GlobalBeliefSetForAgentType<?> that = (GlobalBeliefSetForAgentType<?>) o;

      return converter.equals(that.converter);
    }

    @Override
    public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + converter.hashCode();
      return result;
    }
  }

  /**
   * For belief
   */
  public static class BeliefFromKeyPresence extends FactConverter<Boolean, List<DesireKey>> {

    private final DesireKey desireKey;

    public BeliefFromKeyPresence(DataForDecision dataForDecision, DesireKey desireKey) {
      super(dataForDecision, aBoolean -> {
        if (aBoolean) {
          return 1;
        }
        return 0;
      }, desireKey.getId());
      this.desireKey = desireKey;
    }

    @Override
    public void hasUpdatedValueFromRegisterChanged(List<DesireKey> list) {
      hasValueChanged(list.contains(desireKey));
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      if (!super.equals(o)) {
        return false;
      }

      BeliefFromKeyPresence that = (BeliefFromKeyPresence) o;

      return desireKey.equals(that.desireKey);
    }

    @Override
    public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + desireKey.hashCode();
      return result;
    }

    @Override
    public String getName() {
      return desireKey.getName();
    }
  }

}
