package aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration;

import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.IReactionOnChangeStrategy;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

/**
 * WithSharedDesire configuration class
 */
@Getter
public class ConfigurationWithSharedDesire extends CommonConfiguration {

  private DesireKey sharedDesireKey;
  private int counts;

  @Builder
  private ConfigurationWithSharedDesire(CommitmentDeciderInitializer decisionInDesire,
      CommitmentDeciderInitializer decisionInIntention,
      Set<DesireKey> typesOfDesiresToConsiderWhenCommitting,
      Set<DesireKey> typesOfDesiresToConsiderWhenRemovingCommitment,
      DesireKey sharedDesireKey, int counts,
      IReactionOnChangeStrategy reactionOnChangeStrategy,
      IReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
    super(decisionInDesire, decisionInIntention, typesOfDesiresToConsiderWhenCommitting,
        typesOfDesiresToConsiderWhenRemovingCommitment, reactionOnChangeStrategy,
        reactionOnChangeStrategyInIntention);
    this.sharedDesireKey = sharedDesireKey;
    this.counts = counts;
  }

  //builder with default fields
  public static class ConfigurationWithSharedDesireBuilder extends
      CommonConfiguration.CommonConfigurationBuilder {

    private int counts = Integer.MAX_VALUE;
    private Set<DesireKey> typesOfDesiresToConsiderWhenCommitting = new HashSet<>();
    private Set<DesireKey> typesOfDesiresToConsiderWhenRemovingCommitment = new HashSet<>();
  }

}
