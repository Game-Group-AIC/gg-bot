package aic.gas.sc.gg_bot.mas.model.metadata.agents;

import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.CommonConfiguration;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithAbstractPlan;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithCommand;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.IReactionOnChangeStrategy;
import aic.gas.sc.gg_bot.mas.model.planing.IntentionCommand;
import aic.gas.sc.gg_bot.mas.model.planing.command.CommandForIntention;
import aic.gas.sc.gg_bot.mas.model.planing.command.ICommandFormulationStrategy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Template for desire formulations of specific type (child dependant), this contains only common
 * structures and methods
 */
public abstract class DesireFormulation {

  private final Map<DesireKey, CommitmentDeciderInitializer> decisionsByDesire = new HashMap<>();
  private final Map<DesireKey, CommitmentDeciderInitializer> decisionsByIntention = new HashMap<>();
  private final Map<DesireKey, IReactionOnChangeStrategy> reactionsByIntention = new HashMap<>();
  private final Map<DesireKey, IReactionOnChangeStrategy> reactionsByDesire = new HashMap<>();

  CommitmentDeciderInitializer getDecisionInIntention(DesireKey key) {
    return decisionsByIntention.get(key);
  }

  CommitmentDeciderInitializer getDecisionInDesire(DesireKey key) {
    return decisionsByDesire.get(key);
  }

  IReactionOnChangeStrategy getReactionInIntention(DesireKey key) {
    return reactionsByIntention.get(key);
  }

  IReactionOnChangeStrategy getReactionInDesire(DesireKey key) {
    return reactionsByDesire.get(key);
  }

  /**
   * Add configuration for desire
   */
  void addDesireFormulationConfiguration(DesireKey key, CommonConfiguration configuration) {
    this.decisionsByDesire.put(key, configuration.getDecisionInDesire());
    this.decisionsByIntention.put(key, configuration.getDecisionInIntention());
    this.reactionsByIntention.put(key, configuration.getReactionOnChangeStrategyInIntention());
    this.reactionsByDesire.put(key, configuration.getReactionOnChangeStrategy());
  }

  /**
   * Returns true if desire key is supported
   */
  public abstract boolean supportsDesireType(DesireKey desireKey);

  protected boolean supportsType(DesireKey desireKey) {
    return decisionsByDesire.containsKey(desireKey);
  }

  public Set<DesireKey> supportedDesireTypes() {
    return decisionsByDesire.keySet();
  }

  /**
   * Defines common structure to add configuration for abstract plan
   */
  protected static abstract class WithAbstractPlan extends DesireFormulation {

    final Map<DesireKey, Set<DesireKey>> desiresForOthersByKey = new HashMap<>();
    final Map<DesireKey, Set<DesireKey>> desiresWithAbstractIntentionByKey = new HashMap<>();
    final Map<DesireKey, Set<DesireKey>> desiresWithIntentionToActByKey = new HashMap<>();
    final Map<DesireKey, Set<DesireKey>> desiresWithIntentionToReasonByKey = new HashMap<>();

    public Map<DesireKey, Set<DesireKey>> desiresForOthers() {
      return desiresForOthersByKey;
    }

    public Map<DesireKey, Set<DesireKey>> desiresWithAbstractIntention() {
      return desiresWithAbstractIntentionByKey;
    }

    public Map<DesireKey, Set<DesireKey>> desiresWithIntentionToAct() {
      return desiresWithIntentionToActByKey;
    }

    public Map<DesireKey, Set<DesireKey>> desiresWithIntentionToReason() {
      return desiresWithIntentionToReasonByKey;
    }

    /**
     * Add configuration for desire
     */
    public void addDesireFormulationConfiguration(DesireKey key,
        ConfigurationWithAbstractPlan configuration) {
      addDesireFormulationConfiguration(key, (CommonConfiguration) configuration);
      desiresForOthersByKey.put(key, configuration.getDesiresForOthers());
      desiresWithAbstractIntentionByKey.put(key, configuration.getDesiresWithAbstractIntention());
      desiresWithIntentionToActByKey.put(key, configuration.getDesiresWithIntentionToAct());
      desiresWithIntentionToReasonByKey.put(key, configuration.getDesiresWithIntentionToReason());
    }
  }

  /**
   * Defines common structure to add configuration for intention with command
   */
  static abstract class WithCommand<K extends ICommandFormulationStrategy<? extends CommandForIntention<?>, ? extends IntentionCommand<?, ?>>> extends
      DesireFormulation {

    final Map<DesireKey, K> commandsByKey = new HashMap<>();

    /**
     * Add configuration for desire
     */
    public void addDesireFormulationConfiguration(DesireKey key,
        ConfigurationWithCommand<K> configuration) {
      addDesireFormulationConfiguration(key, (CommonConfiguration) configuration);
      commandsByKey.put(key, configuration.getCommandCreationStrategy());
    }
  }

}
