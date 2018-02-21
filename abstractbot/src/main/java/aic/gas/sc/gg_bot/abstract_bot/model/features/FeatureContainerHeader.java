package aic.gas.sc.gg_bot.abstract_bot.model.features;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.mas.model.knowledge.DataForDecision;
import aic.gas.sc.gg_bot.mas.model.metadata.FactConverterID;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * Contains map of fact keys and their feature values to initialize various instances of
 * FeatureContainer types
 */
@Slf4j
@Getter
public class FeatureContainerHeader {

  private final Set<FactWithOptionalValue<?>> convertersForFacts;
  private final Set<FactWithOptionalValueSet<?>> convertersForFactSets;
  private final Set<FactWithSetOfOptionalValues<?>> convertersForFactsForGlobalBeliefs;
  private final Set<FactWithOptionalValueSets<?>> convertersForFactSetsForGlobalBeliefs;
  private final Set<FactWithSetOfOptionalValuesForAgentType<?>> convertersForFactsForGlobalBeliefsByAgentType;
  private final Set<FactWithOptionalValueSetsForAgentType<?>> convertersForFactSetsForGlobalBeliefsByAgentType;
  private final List<Integer> indexes;
  private final List<Integer> indexesForCommitment;
  private final int sizeOfFeatureVector;
  private final boolean trackCommittedOtherAgents;
  private final List<StrategyToFillValueInVectorUsingBeliefs<?>> strategiesToFillVectorOrdered = new ArrayList<>();
  private final List<DesireKeys> desiresToFillVectorOrdered;
  private final int forHowLongToCacheDecision;

  @Builder
  private FeatureContainerHeader(Set<FactWithOptionalValue<?>> convertersForFacts,
      Set<FactWithOptionalValueSet<?>> convertersForFactSets,
      Set<FactWithSetOfOptionalValues<?>> convertersForFactsForGlobalBeliefs,
      Set<FactWithOptionalValueSets<?>> convertersForFactSetsForGlobalBeliefs,
      Set<FactWithSetOfOptionalValuesForAgentType<?>> convertersForFactsForGlobalBeliefsByAgentType,
      Set<FactWithOptionalValueSetsForAgentType<?>> convertersForFactSetsForGlobalBeliefsByAgentType,
      Set<DesireKeys> interestedInCommitments,
      boolean trackCommittedOtherAgents,
      int forHowLongToCacheDecision) {
    this.convertersForFacts = convertersForFacts;
    this.convertersForFactSets = convertersForFactSets;
    this.convertersForFactsForGlobalBeliefs = convertersForFactsForGlobalBeliefs;
    this.convertersForFactSetsForGlobalBeliefs = convertersForFactSetsForGlobalBeliefs;
    this.convertersForFactsForGlobalBeliefsByAgentType = convertersForFactsForGlobalBeliefsByAgentType;
    this.convertersForFactSetsForGlobalBeliefsByAgentType = convertersForFactSetsForGlobalBeliefsByAgentType;
    this.trackCommittedOtherAgents = trackCommittedOtherAgents;
    this.forHowLongToCacheDecision = forHowLongToCacheDecision;

    Set<Integer> indexesSet = new HashSet<>();

    //add indexes
    addIndexes(convertersForFacts.stream().map(FactConverterID::getId).collect(Collectors.toList()),
        indexesSet);
    strategiesToFillVectorOrdered.addAll(convertersForFacts.stream()
        .map(StrategyToFillValueInVectorUsingBeliefs.FromBeliefs::new)
        .collect(Collectors.toList()));
    addIndexes(
        convertersForFactSets.stream().map(FactConverterID::getId).collect(Collectors.toList()),
        indexesSet);
    strategiesToFillVectorOrdered.addAll(convertersForFactSets.stream()
        .map(StrategyToFillValueInVectorUsingBeliefs.FromBeliefsSet::new)
        .collect(Collectors.toList()));
    addIndexes(convertersForFactsForGlobalBeliefs.stream().map(FactConverterID::getId)
        .collect(Collectors.toList()), indexesSet);
    strategiesToFillVectorOrdered.addAll(convertersForFactsForGlobalBeliefs.stream()
        .map(StrategyToFillValueInVectorUsingBeliefs.FromGlobalBeliefs::new)
        .collect(Collectors.toList()));
    addIndexes(convertersForFactSetsForGlobalBeliefs.stream().map(FactConverterID::getId)
        .collect(Collectors.toList()), indexesSet);
    strategiesToFillVectorOrdered.addAll(convertersForFactSetsForGlobalBeliefs.stream()
        .map(StrategyToFillValueInVectorUsingBeliefs.FromGlobalBeliefsSets::new)
        .collect(Collectors.toList()));
    addIndexes(convertersForFactsForGlobalBeliefsByAgentType.stream().map(FactConverterID::getId)
        .collect(Collectors.toList()), indexesSet);
    strategiesToFillVectorOrdered.addAll(convertersForFactsForGlobalBeliefsByAgentType.stream()
        .map(StrategyToFillValueInVectorUsingBeliefs.FromGlobalBeliefsByAgentType::new)
        .collect(Collectors.toList()));
    addIndexes(convertersForFactSetsForGlobalBeliefsByAgentType.stream().map(FactConverterID::getId)
        .collect(Collectors.toList()), indexesSet);
    strategiesToFillVectorOrdered.addAll(convertersForFactSetsForGlobalBeliefsByAgentType.stream()
        .map(StrategyToFillValueInVectorUsingBeliefs.FromGlobalBeliefsSetsByAgentType::new)
        .collect(Collectors.toList()));
    indexes = indexesSet.stream().sorted().collect(Collectors.toList());

    //add commitments
    indexesSet.clear();
    addIndexes(
        interestedInCommitments.stream().map(DesireKeys::ordinal).collect(Collectors.toList()),
        indexesSet);
    indexesForCommitment = indexesSet.stream().sorted().collect(Collectors.toList());

    //sort strategies to create vector
    Collections.sort(strategiesToFillVectorOrdered);
    desiresToFillVectorOrdered = interestedInCommitments.stream()
        .sorted(Comparator.comparingInt(DesireKeys::ordinal))
        .collect(Collectors.toList());

    //one additional dimension to track commitment by other agents
    if (trackCommittedOtherAgents) {
      this.sizeOfFeatureVector = indexes.size() + indexesForCommitment.size() + 1;
    } else {
      this.sizeOfFeatureVector = indexes.size() + indexesForCommitment.size();
    }
  }

  /**
   * Create feature vector using data for decision
   */
  public double[] formVector(DataForDecision dataForDecision) {
    double[] vector = new double[sizeOfFeatureVector];

    //do beliefs
    for (int i = 0; i < strategiesToFillVectorOrdered.size(); i++) {
      vector[i] = strategiesToFillVectorOrdered.get(i).getValue(dataForDecision);
    }

    //do commitments
    for (int i = 0; i < desiresToFillVectorOrdered.size(); i++) {
      vector[i + strategiesToFillVectorOrdered.size()] = dataForDecision
          .getFeatureValueMadeCommitmentToType(desiresToFillVectorOrdered.get(i).getId());
    }

    //do count of committed agents
    if (trackCommittedOtherAgents) {
      vector[vector.length - 1] = dataForDecision.getNumberOfCommittedAgents();
    }

    return vector;
  }

  public String[] getHeaders() {
    String[] headers = new String[sizeOfFeatureVector];

    //do beliefs
    for (int i = 0; i < strategiesToFillVectorOrdered.size(); i++) {
      headers[i] = strategiesToFillVectorOrdered.get(i).converterID.toString();
    }

    //do commitments
    for (int i = 0; i < desiresToFillVectorOrdered.size(); i++) {
      headers[i + strategiesToFillVectorOrdered.size()] = desiresToFillVectorOrdered.get(i)
          .toString();
    }

    //do count of committed agents
    if (trackCommittedOtherAgents) {
      headers[headers.length - 1] = "# commited agents";
    }

    return headers;
  }

  /**
   * Check for duplicity
   */
  private void addIndexes(List<Integer> indexes, Set<Integer> indexesSet) {
    for (Integer integer : indexes) {
      if (indexesSet.contains(integer)) {
        log.error("Found duplicity index.");
      }
      indexesSet.add(integer);
    }
  }

  /**
   * Default values
   */
  public static class FeatureContainerHeaderBuilder {

    private Set<FactWithOptionalValue<?>> convertersForFacts = new HashSet<>();
    private Set<FactWithOptionalValueSet<?>> convertersForFactSets = new HashSet<>();
    private Set<FactWithSetOfOptionalValues<?>> convertersForFactsForGlobalBeliefs = new HashSet<>();
    private Set<FactWithOptionalValueSets<?>> convertersForFactSetsForGlobalBeliefs = new HashSet<>();
    private Set<FactWithSetOfOptionalValuesForAgentType<?>> convertersForFactsForGlobalBeliefsByAgentType = new HashSet<>();
    private Set<FactWithOptionalValueSetsForAgentType<?>> convertersForFactSetsForGlobalBeliefsByAgentType = new HashSet<>();
    private Set<DesireKeys> interestedInCommitments = new HashSet<>();
    private boolean trackCommittedOtherAgents = false;
    private int forHowLongToCacheDecision = 10;
  }

  /**
   * Strategy to get value of feature using convertor
   */
  private static abstract class StrategyToFillValueInVectorUsingBeliefs<V extends FactConverterID<?>> implements
      Comparable<StrategyToFillValueInVectorUsingBeliefs<?>> {

    final V converterID;

    StrategyToFillValueInVectorUsingBeliefs(V converterID) {
      this.converterID = converterID;
    }

    /**
     * Get value of belief
     */
    public abstract double getValue(DataForDecision dataForDecision);

    @Override
    public int compareTo(@NotNull StrategyToFillValueInVectorUsingBeliefs<?> o) {
      return Integer.compare(converterID.getId(), o.converterID.getId());
    }

    /**
     * For agent's beliefs - single fact
     */
    static class FromBeliefs extends
        StrategyToFillValueInVectorUsingBeliefs<FactWithOptionalValue<?>> {

      FromBeliefs(FactWithOptionalValue<?> converterID) {
        super(converterID);
      }

      @Override
      public double getValue(DataForDecision dataForDecision) {
        return dataForDecision.getFeatureValueBeliefs(converterID);
      }
    }

    /**
     * For agent's beliefs - set of fact
     */
    static class FromBeliefsSet extends
        StrategyToFillValueInVectorUsingBeliefs<FactWithOptionalValueSet<?>> {

      FromBeliefsSet(FactWithOptionalValueSet<?> converterID) {
        super(converterID);
      }

      @Override
      public double getValue(DataForDecision dataForDecision) {
        return dataForDecision.getFeatureValueBeliefSets(converterID);
      }
    }

    /**
     * For global beliefs - single fact
     */
    static class FromGlobalBeliefs extends
        StrategyToFillValueInVectorUsingBeliefs<FactWithSetOfOptionalValues<?>> {

      FromGlobalBeliefs(FactWithSetOfOptionalValues<?> converterID) {
        super(converterID);
      }

      @Override
      public double getValue(DataForDecision dataForDecision) {
        return dataForDecision.getFeatureValueGlobalBeliefs(converterID);
      }
    }

    /**
     * For global beliefs - single fact
     */
    static class FromGlobalBeliefsSets extends
        StrategyToFillValueInVectorUsingBeliefs<FactWithOptionalValueSets<?>> {

      FromGlobalBeliefsSets(FactWithOptionalValueSets<?> converterID) {
        super(converterID);
      }

      @Override
      public double getValue(DataForDecision dataForDecision) {
        return dataForDecision.getFeatureValueGlobalBeliefSets(converterID);
      }
    }

    /**
     * For global beliefs - single fact
     */
    static class FromGlobalBeliefsByAgentType extends
        StrategyToFillValueInVectorUsingBeliefs<FactWithSetOfOptionalValuesForAgentType<?>> {

      FromGlobalBeliefsByAgentType(FactWithSetOfOptionalValuesForAgentType<?> converterID) {
        super(converterID);
      }

      @Override
      public double getValue(DataForDecision dataForDecision) {
        return dataForDecision.getFeatureValueGlobalBeliefs(converterID);
      }
    }

    /**
     * For global beliefs - single fact
     */
    static class FromGlobalBeliefsSetsByAgentType extends
        StrategyToFillValueInVectorUsingBeliefs<FactWithOptionalValueSetsForAgentType<?>> {

      FromGlobalBeliefsSetsByAgentType(FactWithOptionalValueSetsForAgentType<?> converterID) {
        super(converterID);
      }

      @Override
      public double getValue(DataForDecision dataForDecision) {
        return dataForDecision.getFeatureValueGlobalBeliefSets(converterID);
      }
    }

  }
}
