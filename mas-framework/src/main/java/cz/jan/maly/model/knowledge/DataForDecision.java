package cz.jan.maly.model.knowledge;

import cz.jan.maly.model.metadata.DesireKey;
import cz.jan.maly.model.metadata.DesireKeyID;
import cz.jan.maly.model.metadata.DesireParameters;
import cz.jan.maly.model.metadata.FactConverter;
import cz.jan.maly.model.metadata.containers.*;
import cz.jan.maly.model.planing.CommitmentDeciderInitializer;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Container with data to be used to decide commitment
 * Created by Jan on 02-Mar-17.
 */
public class DataForDecision {
    //****beliefs for decision point****

    //what was already decided on same level - types of desires
    private final Map<DesireKeyID, FactConverter.BeliefFromKeyPresence> madeCommitmentToTypes = new HashMap<>();
    private final Map<DesireKeyID, FactConverter.BeliefFromKeyPresence> didNotMakeCommitmentToTypes = new HashMap<>();
    //desires/intention types to come
    private final Map<DesireKeyID, FactConverter.BeliefFromKeyPresence> typesAboutToMakeDecision = new HashMap<>();
    //static beliefs from desire key
    private final Map<FactWithOptionalValue<?>, FactConverter.BeliefFromKey<?>> staticBeliefs = new HashMap<>();
    private final Map<FactWithOptionalValueSet<?>, FactConverter.BeliefSetFromKey<?>> staticBeliefSets = new HashMap<>();
    //beliefs from desire parameters
    private final Map<FactWithOptionalValue<?>, FactConverter.BeliefFromDesire<?>> desireBeliefs = new HashMap<>();
    private final Map<FactWithOptionalValueSet<?>, FactConverter.BeliefSetFromDesire<?>> desireBeliefSets = new HashMap<>();
    //beliefs from agent beliefs
    private final Map<FactWithOptionalValue<?>, FactConverter.Belief<?>> beliefs = new HashMap<>();
    private final Map<FactWithOptionalValueSet<?>, FactConverter.BeliefSet<?>> beliefSets = new HashMap<>();
    //global beliefs
    private final Map<FactWithSetOfOptionalValues<?>, FactConverter.GlobalBelief<?>> globalBeliefs = new HashMap<>();
    private final Map<FactWithOptionalValueSets<?>, FactConverter.GlobalBeliefSet<?>> globalBeliefsSets = new HashMap<>();
    //global beliefs by agent type
    private final Map<FactWithSetOfOptionalValuesForAgentType<?>, FactConverter.GlobalBeliefForAgentType<?>> globalBeliefsByAgentType = new HashMap<>();
    private final Map<FactWithOptionalValueSetsForAgentType<?>, FactConverter.GlobalBeliefSetForAgentType<?>> globalBSetsByAgentType = new HashMap<>();
    @Getter
    private int numberOfCommittedAgents = 0;

    //****beliefs for decision point****
    @Getter
    @Setter
    private boolean beliefsChanged = true;

    /**
     * Constructor
     *
     * @param initializer
     * @param desireKey
     * @param desireParameters
     */
    public DataForDecision(DesireKey desireKey, DesireParameters desireParameters, CommitmentDeciderInitializer initializer) {

        initializer.getDesiresToConsider().forEach(key -> {
            madeCommitmentToTypes.put(key, new FactConverter.BeliefFromKeyPresence(this, key));
            didNotMakeCommitmentToTypes.put(key, new FactConverter.BeliefFromKeyPresence(this, key));
            typesAboutToMakeDecision.put(key, new FactConverter.BeliefFromKeyPresence(this, key));
        });

        //static values
        initializer.getStaticBeliefsTypes().forEach(factWithOptionalValue -> staticBeliefs.put(factWithOptionalValue, new FactConverter.BeliefFromKey<>(this, desireKey, factWithOptionalValue)));
        initializer.getStaticBeliefsSetTypes().forEach(factWithOptionalValueSet -> staticBeliefSets.put(factWithOptionalValueSet, new FactConverter.BeliefSetFromKey<>(this, desireKey, factWithOptionalValueSet)));

        //values from parameters
        initializer.getParameterValueTypes().forEach(factWithOptionalValue -> desireBeliefs.put(factWithOptionalValue, new FactConverter.BeliefFromDesire<>(this, desireParameters, factWithOptionalValue)));
        initializer.getParameterValueSetTypes().forEach(factWithOptionalValueSet -> desireBeliefSets.put(factWithOptionalValueSet, new FactConverter.BeliefSetFromDesire<>(this, desireParameters, factWithOptionalValueSet)));

        //values from beliefs
        initializer.getBeliefTypes().forEach(factWithOptionalValue -> beliefs.put(factWithOptionalValue, new FactConverter.Belief<>(this, factWithOptionalValue)));
        initializer.getBeliefSetTypes().forEach(factWithOptionalValueSet -> beliefSets.put(factWithOptionalValueSet, new FactConverter.BeliefSet<>(this, factWithOptionalValueSet)));

        //values from global beliefs
        initializer.getGlobalBeliefTypes().forEach(factWithSetOfOptionalValues -> globalBeliefs.put(factWithSetOfOptionalValues, new FactConverter.GlobalBelief<>(this, factWithSetOfOptionalValues)));
        initializer.getGlobalBeliefSetTypes().forEach(factWithOptionalValueSets -> globalBeliefsSets.put(factWithOptionalValueSets, new FactConverter.GlobalBeliefSet<>(this, factWithOptionalValueSets)));

        //values from global beliefs restricted to agent type
        initializer.getGlobalBeliefTypesByAgentType().forEach(factWithSetOfOptionalValuesForAgentType ->
                globalBeliefsByAgentType.put(factWithSetOfOptionalValuesForAgentType, new FactConverter.GlobalBeliefForAgentType<>(this, factWithSetOfOptionalValuesForAgentType)));
        initializer.getGlobalBeliefSetTypesByAgentType().forEach(factWithOptionalValueSetsForAgentType ->
                globalBSetsByAgentType.put(factWithOptionalValueSetsForAgentType, new FactConverter.GlobalBeliefSetForAgentType<>(this, factWithOptionalValueSetsForAgentType)));
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

    public double getFeatureValueStaticBeliefs(FactWithOptionalValue<?> factWithOptionalValue) {
        return staticBeliefs.get(factWithOptionalValue).getValue();
    }

    public double getFeatureValueStaticBeliefSets(FactWithOptionalValueSet<?> factWithOptionalValueSet) {
        return staticBeliefSets.get(factWithOptionalValueSet).getValue();
    }

    public double getFeatureValueDesireBeliefs(FactWithOptionalValue<?> factWithOptionalValue) {
        return desireBeliefs.get(factWithOptionalValue).getValue();
    }

    public double getFeatureValueDesireBeliefSets(FactWithOptionalValueSet<?> factWithOptionalValueSet) {
        return desireBeliefSets.get(factWithOptionalValueSet).getValue();
    }

    public double getFeatureValueBeliefs(FactWithOptionalValue<?> factWithOptionalValue) {
        return beliefs.get(factWithOptionalValue).getValue();
    }

    public double getFeatureValueBeliefSets(FactWithOptionalValueSet<?> factWithOptionalValueSet) {
        return beliefSets.get(factWithOptionalValueSet).getValue();
    }

    public double getFeatureValueGlobalBeliefs(FactWithSetOfOptionalValues<?> factWithSetOfOptionalValues) {
        return globalBeliefs.get(factWithSetOfOptionalValues).getValue();
    }

    public double getFeatureValueGlobalBeliefSets(FactWithOptionalValueSets<?> factWithOptionalValueSets) {
        return globalBeliefsSets.get(factWithOptionalValueSets).getValue();
    }

    public double getFeatureValueGlobalBeliefs(FactWithSetOfOptionalValuesForAgentType<?> factWithSetOfOptionalValuesForAgentType) {
        return globalBeliefsByAgentType.get(factWithSetOfOptionalValuesForAgentType).getValue();
    }

    public double getFeatureValueGlobalBeliefSets(FactWithOptionalValueSetsForAgentType<?> factWithOptionalValueSetsForAgentType) {
        return globalBSetsByAgentType.get(factWithOptionalValueSetsForAgentType).getValue();
    }

    /**
     * Update beliefs needed to make decision and set status of update - was any value changed?
     *
     * @param madeCommitmentToTypes
     * @param didNotMakeCommitmentToTypes
     * @param typesAboutToMakeDecision
     * @param memory
     */
    public void updateBeliefs(List<DesireKey> madeCommitmentToTypes, List<DesireKey> didNotMakeCommitmentToTypes,
                              List<DesireKey> typesAboutToMakeDecision, WorkingMemory memory) {
        this.madeCommitmentToTypes.values().forEach(beliefFromKeyPresence -> beliefFromKeyPresence.hasUpdatedValueFromRegisterChanged(madeCommitmentToTypes));
        this.didNotMakeCommitmentToTypes.values().forEach(beliefFromKeyPresence -> beliefFromKeyPresence.hasUpdatedValueFromRegisterChanged(didNotMakeCommitmentToTypes));
        this.typesAboutToMakeDecision.values().forEach(beliefFromKeyPresence -> beliefFromKeyPresence.hasUpdatedValueFromRegisterChanged(typesAboutToMakeDecision));

        this.beliefs.values().forEach(belief -> belief.hasUpdatedValueFromRegisterChanged(memory));
        this.beliefSets.values().forEach(belief -> belief.hasUpdatedValueFromRegisterChanged(memory));

        this.globalBeliefs.values().forEach(belief -> belief.hasUpdatedValueFromRegisterChanged(memory));
        this.globalBeliefsSets.values().forEach(belief -> belief.hasUpdatedValueFromRegisterChanged(memory));

        this.globalBeliefsByAgentType.values().forEach(globalBelief -> globalBelief.hasUpdatedValueFromRegisterChanged(memory));
        this.globalBSetsByAgentType.values().forEach(globalBeliefSet -> globalBeliefSet.hasUpdatedValueFromRegisterChanged(memory));
    }

    /**
     * Update beliefs needed to make decision and set status of update - was any value changed?
     *
     * @param madeCommitmentToTypes
     * @param didNotMakeCommitmentToTypes
     * @param typesAboutToMakeDecision
     * @param memory
     */
    public void updateBeliefs(List<DesireKey> madeCommitmentToTypes, List<DesireKey> didNotMakeCommitmentToTypes,
                              List<DesireKey> typesAboutToMakeDecision, WorkingMemory memory, int numberOfCommittedAgents) {
        updateBeliefs(madeCommitmentToTypes, didNotMakeCommitmentToTypes, typesAboutToMakeDecision, memory);
        if (numberOfCommittedAgents != this.numberOfCommittedAgents) {
            beliefsChanged = true;
            this.numberOfCommittedAgents = numberOfCommittedAgents;
        }
    }

}
