package aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration;

import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.IReactionOnChangeStrategy;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Common configuration class
 */
@Getter
@AllArgsConstructor
public class CommonConfiguration {

  private CommitmentDeciderInitializer decisionInDesire;
  private CommitmentDeciderInitializer decisionInIntention;
  private Set<DesireKey> typesOfDesiresToConsiderWhenCommitting;
  private Set<DesireKey> typesOfDesiresToConsiderWhenRemovingCommitment;
  private IReactionOnChangeStrategy reactionOnChangeStrategy;
  private IReactionOnChangeStrategy reactionOnChangeStrategyInIntention;

  //builder with default fields
  static class CommonConfigurationBuilder {

    private Set<DesireKey> typesOfDesiresToConsiderWhenCommitting = new HashSet<>();
    private Set<DesireKey> typesOfDesiresToConsiderWhenRemovingCommitment = new HashSet<>();
  }
}
