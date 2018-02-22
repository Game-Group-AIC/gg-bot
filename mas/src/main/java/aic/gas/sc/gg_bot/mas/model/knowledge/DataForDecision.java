package aic.gas.sc.gg_bot.mas.model.knowledge;

import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import aic.gas.sc.gg_bot.mas.model.metadata.FactConverter;
import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactValueSet;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactValueSets;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactValueSetsForAgentType;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;

/**
 * Container with data to be used to decide commitment
 */
public class DataForDecision {
  //****beliefs for decision point****

  //what was already decided on same level - types of desires
  private final Map<DesireKeyID, FactConverter.BeliefFromKeyPresence> madeCommitmentToTypes = new HashMap<>();
  private final Map<DesireKeyID, FactConverter.BeliefFromKeyPresence> didNotMakeCommitmentToTypes = new HashMap<>();
  //desires/intention types to come
  private final Map<DesireKeyID, FactConverter.BeliefFromKeyPresence> typesAboutToMakeDecision = new HashMap<>();
  //static beliefs from desire key
  private final Map<FactValueSet<?>, FactConverter.BeliefSetFromKey<?>> staticBeliefSets = new HashMap<>();
  //beliefs from desire parameters
  private final Map<FactValueSet<?>, FactConverter.BeliefSetFromDesire<?>> desireBeliefSets = new HashMap<>();
  //beliefs from agent beliefs
  private final Map<FactValueSet<?>, FactConverter.BeliefSet<?>> beliefSets = new HashMap<>();
  //global beliefs
  private final Map<FactValueSets<?>, FactConverter.GlobalBeliefSet<?>> globalBeliefsSets = new HashMap<>();
  //global beliefs by agent type
  private final Map<FactValueSetsForAgentType<?>, FactConverter.GlobalBeliefSetForAgentType<?>> globalBeliefsSetsByAgentType = new HashMap<>();
  @Getter
  private final boolean useFactsInMemory;
  private final DesireParameters desireParameters;
  @Getter
  private int numberOfCommittedAgents = 0;
  //****beliefs for decision point****
  @Setter
  @Getter
  private boolean beliefsChanged = true;

  /**
   * Constructor
   */
  public DataForDecision(DesireKey desireKey, DesireParameters desireParameters,
      CommitmentDeciderInitializer initializer) {
    this.useFactsInMemory = initializer.isUseFactsInMemory();
    this.desireParameters = desireParameters;

    initializer.getDesiresToConsider().forEach(key -> {
      madeCommitmentToTypes
          .put(key.getDesireKeyId(), new FactConverter.BeliefFromKeyPresence(this, key));
      didNotMakeCommitmentToTypes
          .put(key.getDesireKeyId(), new FactConverter.BeliefFromKeyPresence(this, key));
      typesAboutToMakeDecision
          .put(key.getDesireKeyId(), new FactConverter.BeliefFromKeyPresence(this, key));
    });

    //static values
    initializer.getStaticBeliefsSetTypes().forEach(factWithOptionalValueSet -> staticBeliefSets
        .put(factWithOptionalValueSet,
            new FactConverter.BeliefSetFromKey<>(this, desireKey, factWithOptionalValueSet)));

    //values from parameters
    initializer.getParameterValueSetTypes().forEach(factWithOptionalValueSet -> desireBeliefSets
        .put(factWithOptionalValueSet,
            new FactConverter.BeliefSetFromDesire<>(this, desireParameters,
                factWithOptionalValueSet)));

    //values from beliefs
    initializer.getBeliefSetTypes().forEach(factWithOptionalValueSet -> beliefSets
        .put(factWithOptionalValueSet,
            new FactConverter.BeliefSet<>(this, factWithOptionalValueSet)));

    //values from global beliefs
    initializer.getGlobalBeliefSetTypes().forEach(factWithOptionalValueSets -> globalBeliefsSets
        .put(factWithOptionalValueSets,
            new FactConverter.GlobalBeliefSet<>(this, factWithOptionalValueSets)));

    //values from global beliefs restricted to agent type
    initializer.getGlobalBeliefSetTypesByAgentType()
        .forEach(factWithOptionalValueSetsForAgentType ->
            globalBeliefsSetsByAgentType.put(factWithOptionalValueSetsForAgentType,
                new FactConverter.GlobalBeliefSetForAgentType<>(this,
                    factWithOptionalValueSetsForAgentType)));
  }

  public <V> Optional<V> returnFactValueForGivenKey(FactKey<V> factKey) {
    return desireParameters.returnFactValueForGivenKey(factKey);
  }

  public <V, S extends Stream<V>> Optional<S> returnFactSetValueForGivenKey(FactKey<V> factKey) {
    return desireParameters.returnFactSetValueForGivenKey(factKey);
  }

  public double getFeatureValueMadeCommitmentToType(DesireKeyID desireKey) {
    return madeCommitmentToTypes.get(desireKey).getValue();
  }

  public double getFeatureValueDidNotMakeCommitmentToType(DesireKeyID desireKey) {
    return didNotMakeCommitmentToTypes.get(desireKey).getValue();
  }

  public double getFeatureValueTypesAboutToMakeDecision(DesireKeyID desireKey) {
    return typesAboutToMakeDecision.get(desireKey).getValue();
  }

  public boolean madeDecisionToAny() {
    return madeCommitmentToTypes.values().stream()
        .mapToDouble(FactConverter::getValue)
        .anyMatch(value -> value > 0);
  }

  public double getFeatureValueStaticBeliefSets(
      FactValueSet<?> factValueSet) {
    return staticBeliefSets.get(factValueSet).getValue();
  }

  public double getFeatureValueDesireBeliefSets(
      FactValueSet<?> factValueSet) {
    return desireBeliefSets.get(factValueSet).getValue();
  }

  public double getFeatureValueBeliefSets(FactValueSet<?> factValueSet) {
    return beliefSets.get(factValueSet).getValue();
  }

  public double getFeatureValueGlobalBeliefSets(
      FactValueSets<?> factValueSets) {
    return globalBeliefsSets.get(factValueSets).getValue();
  }

  public double getFeatureValueGlobalBeliefSets(
      FactValueSetsForAgentType<?> factWithOptionalValueSetsForAgentType) {
    return globalBeliefsSetsByAgentType.get(factWithOptionalValueSetsForAgentType).getValue();
  }

  /**
   * Update beliefs needed to make decision and set status of update - was any value changed?
   */
  public void updateBeliefs(List<DesireKey> madeCommitmentToTypes,
      List<DesireKey> didNotMakeCommitmentToTypes,
      List<DesireKey> typesAboutToMakeDecision, WorkingMemory memory) {
    this.madeCommitmentToTypes.values().forEach(beliefFromKeyPresence -> beliefFromKeyPresence
        .hasUpdatedValueFromRegisterChanged(madeCommitmentToTypes));
    this.didNotMakeCommitmentToTypes.values().forEach(beliefFromKeyPresence -> beliefFromKeyPresence
        .hasUpdatedValueFromRegisterChanged(didNotMakeCommitmentToTypes));
    this.typesAboutToMakeDecision.values().forEach(beliefFromKeyPresence -> beliefFromKeyPresence
        .hasUpdatedValueFromRegisterChanged(typesAboutToMakeDecision));

    this.beliefSets.values().forEach(belief -> belief.hasUpdatedValueFromRegisterChanged(memory));

    this.globalBeliefsSets.values()
        .forEach(belief -> belief.hasUpdatedValueFromRegisterChanged(memory));

    this.globalBeliefsSetsByAgentType.values()
        .forEach(globalBeliefSet -> globalBeliefSet.hasUpdatedValueFromRegisterChanged(memory));
  }

  /**
   * Update beliefs needed to make decision and set status of update - was any value changed?
   */
  public void updateBeliefs(List<DesireKey> madeCommitmentToTypes,
      List<DesireKey> didNotMakeCommitmentToTypes,
      List<DesireKey> typesAboutToMakeDecision, WorkingMemory memory, int numberOfCommittedAgents) {
    updateBeliefs(madeCommitmentToTypes, didNotMakeCommitmentToTypes, typesAboutToMakeDecision,
        memory);
    if (numberOfCommittedAgents != this.numberOfCommittedAgents) {
      beliefsChanged = true;
      this.numberOfCommittedAgents = numberOfCommittedAgents;
    }
  }

}
