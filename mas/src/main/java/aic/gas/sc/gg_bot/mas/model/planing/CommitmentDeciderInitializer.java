package aic.gas.sc.gg_bot.mas.model.planing;

import aic.gas.sc.gg_bot.mas.model.knowledge.DataForDecision;
import aic.gas.sc.gg_bot.mas.model.knowledge.Memory;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactValueSet;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactValueSets;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactValueSetsForAgentType;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

/**
 * container with data to initialize decision point
 */
@Getter
public class CommitmentDeciderInitializer {

  private final IDecisionStrategy decisionStrategy;
  private final Set<DesireKey> desiresToConsider;
  private final Set<FactValueSet<?>> staticBeliefsSetTypes;
  private final Set<FactValueSet<?>> parameterValueSetTypes;
  private final Set<FactValueSet<?>> beliefSetTypes;
  private final Set<FactValueSets<?>> globalBeliefSetTypes;
  private final Set<FactValueSetsForAgentType<?>> globalBeliefSetTypesByAgentType;
  private final boolean useFactsInMemory;

  @Builder
  private CommitmentDeciderInitializer(IDecisionStrategy decisionStrategy,
      Set<DesireKey> desiresToConsider,
      Set<FactValueSet<?>> staticBeliefsSetTypes,
      Set<FactValueSet<?>> parameterValueSetTypes,
      Set<FactValueSet<?>> beliefSetTypes,
      Set<FactValueSets<?>> globalBeliefSetTypes,
      Set<FactValueSetsForAgentType<?>> globalBeliefSetTypesByAgentType,
      boolean useFactsInMemory) {
    this.decisionStrategy = decisionStrategy;
    this.desiresToConsider = desiresToConsider;
    this.staticBeliefsSetTypes = staticBeliefsSetTypes;
    this.parameterValueSetTypes = parameterValueSetTypes;
    this.beliefSetTypes = beliefSetTypes;
    this.globalBeliefSetTypes = globalBeliefSetTypes;
    this.globalBeliefSetTypesByAgentType = globalBeliefSetTypesByAgentType;
    this.useFactsInMemory = useFactsInMemory;
  }

  /**
   * Returns new instance of CommitmentDecider initilized by parameters from this instance
   */
  public CommitmentDecider initializeCommitmentDecider(DesireParameters desireParameters) {
    return new CommitmentDecider(this, desireParameters);
  }


  /**
   * DecisionStrategy
   */
  public interface IDecisionStrategy {

    /**
     * Returns if agent should commit to desire and make intention from it
     */
    boolean shouldCommit(DataForDecision dataForDecision, Memory<?> memory);
  }

  public static class CommitmentDeciderInitializerBuilder {
    private Set<DesireKey> desiresToConsider = new HashSet<>();
    private Set<FactValueSet<?>> staticBeliefsSetTypes = new HashSet<>();
    private Set<FactValueSet<?>> parameterValueSetTypes = new HashSet<>();
    private Set<FactValueSet<?>> beliefSetTypes = new HashSet<>();
    private Set<FactValueSets<?>> globalBeliefSetTypes = new HashSet<>();
    private Set<FactValueSetsForAgentType<?>> globalBeliefSetTypesByAgentType = new HashSet<>();
    private boolean useFactsInMemory = true;
  }
}
