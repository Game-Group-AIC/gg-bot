package aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration;

import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.ReactionOnChangeStrategy;
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
  private ReactionOnChangeStrategy reactionOnChangeStrategy;
  private ReactionOnChangeStrategy reactionOnChangeStrategyInIntention;

  //builder with default fields
  static class CommonConfigurationBuilder {

    private Set<DesireKey> typesOfDesiresToConsiderWhenCommitting = new HashSet<>();
    private Set<DesireKey> typesOfDesiresToConsiderWhenRemovingCommitment = new HashSet<>();
  }
}
