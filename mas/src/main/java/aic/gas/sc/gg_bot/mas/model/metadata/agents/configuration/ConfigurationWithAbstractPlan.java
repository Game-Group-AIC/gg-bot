package aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration;

import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.ReactionOnChangeStrategy;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

/**
 * WithAbstractPlan configuration class
 */
@Getter
public class ConfigurationWithAbstractPlan extends CommonConfiguration {

  private Set<DesireKey> desiresForOthers;
  private Set<DesireKey> desiresWithAbstractIntention;
  private Set<DesireKey> desiresWithIntentionToAct;
  private Set<DesireKey> desiresWithIntentionToReason;

  @Builder
  private ConfigurationWithAbstractPlan(CommitmentDeciderInitializer decisionInDesire,
      CommitmentDeciderInitializer decisionInIntention,
      Set<DesireKey> typesOfDesiresToConsiderWhenCommitting,
      Set<DesireKey> typesOfDesiresToConsiderWhenRemovingCommitment,
      Set<DesireKey> desiresForOthers,
      Set<DesireKey> desiresWithAbstractIntention,
      Set<DesireKey> desiresWithIntentionToAct,
      Set<DesireKey> desiresWithIntentionToReason,
      ReactionOnChangeStrategy reactionOnChangeStrategy,
      ReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
    super(decisionInDesire, decisionInIntention, typesOfDesiresToConsiderWhenCommitting,
        typesOfDesiresToConsiderWhenRemovingCommitment, reactionOnChangeStrategy,
        reactionOnChangeStrategyInIntention);
    this.desiresForOthers = desiresForOthers;
    this.desiresWithAbstractIntention = desiresWithAbstractIntention;
    this.desiresWithIntentionToAct = desiresWithIntentionToAct;
    this.desiresWithIntentionToReason = desiresWithIntentionToReason;
  }

  //builder with default fields
  public static class ConfigurationWithAbstractPlanBuilder extends
      CommonConfiguration.CommonConfigurationBuilder {

    private Set<DesireKey> desiresForOthers = new HashSet<>();
    private Set<DesireKey> desiresWithAbstractIntention = new HashSet<>();
    private Set<DesireKey> desiresWithIntentionToAct = new HashSet<>();
    private Set<DesireKey> desiresWithIntentionToReason = new HashSet<>();
    private Set<DesireKey> typesOfDesiresToConsiderWhenCommitting = new HashSet<>();
    private Set<DesireKey> typesOfDesiresToConsiderWhenRemovingCommitment = new HashSet<>();
  }

}
