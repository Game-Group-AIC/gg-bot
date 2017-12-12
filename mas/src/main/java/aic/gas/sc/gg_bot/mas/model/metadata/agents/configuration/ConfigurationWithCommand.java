package aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration;

import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.IntentionCommand;
import aic.gas.sc.gg_bot.mas.model.planing.ReactionOnChangeStrategy;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import aic.gas.sc.gg_bot.mas.model.planing.command.CommandForIntention;
import aic.gas.sc.gg_bot.mas.model.planing.command.CommandFormulationStrategy;
import aic.gas.sc.gg_bot.mas.model.planing.command.ReasoningCommand;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

/**
 * Template for configuration container with strategy to create command
 */
@Getter
public class ConfigurationWithCommand<K extends CommandFormulationStrategy<? extends CommandForIntention<?>, ? extends IntentionCommand<?, ?>>> extends
    CommonConfiguration {

  private K commandCreationStrategy;

  ConfigurationWithCommand(CommitmentDeciderInitializer decisionInDesire,
      CommitmentDeciderInitializer decisionInIntention,
      Set<DesireKey> typesOfDesiresToConsiderWhenCommitting,
      Set<DesireKey> typesOfDesiresToConsiderWhenRemovingCommitment,
      K commandCreationStrategy, ReactionOnChangeStrategy reactionOnChangeStrategy,
      ReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
    super(decisionInDesire, decisionInIntention, typesOfDesiresToConsiderWhenCommitting,
        typesOfDesiresToConsiderWhenRemovingCommitment,
        reactionOnChangeStrategy, reactionOnChangeStrategyInIntention);
    this.commandCreationStrategy = commandCreationStrategy;
  }

  //For acting command desired by another agent
  public static class WithActingCommandDesiredByOtherAgent extends
      ConfigurationWithCommand<CommandFormulationStrategy<ActCommand.DesiredByAnotherAgent, IntentionCommand.FromAnotherAgent>> {

    @Builder
    private WithActingCommandDesiredByOtherAgent(CommitmentDeciderInitializer decisionInDesire,
        CommitmentDeciderInitializer decisionInIntention,
        Set<DesireKey> typesOfDesiresToConsiderWhenCommitting,
        Set<DesireKey> typesOfDesiresToConsiderWhenRemovingCommitment,
        CommandFormulationStrategy<ActCommand.DesiredByAnotherAgent, IntentionCommand.FromAnotherAgent> commandCreationStrategy,
        ReactionOnChangeStrategy reactionOnChangeStrategy,
        ReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
      super(decisionInDesire, decisionInIntention, typesOfDesiresToConsiderWhenCommitting,
          typesOfDesiresToConsiderWhenRemovingCommitment, commandCreationStrategy,
          reactionOnChangeStrategy,
          reactionOnChangeStrategyInIntention);
    }

    //builder with default fields
    public static class WithActingCommandDesiredByOtherAgentBuilder extends
        CommonConfiguration.CommonConfigurationBuilder {

      private Set<DesireKey> typesOfDesiresToConsiderWhenCommitting = new HashSet<>();
      private Set<DesireKey> typesOfDesiresToConsiderWhenRemovingCommitment = new HashSet<>();
    }
  }

  //For acting command desired by itself
  public static class WithActingCommandDesiredBySelf extends
      ConfigurationWithCommand<CommandFormulationStrategy<ActCommand.Own, IntentionCommand.OwnActing>> {

    @Builder
    private WithActingCommandDesiredBySelf(CommitmentDeciderInitializer decisionInDesire,
        CommitmentDeciderInitializer decisionInIntention,
        Set<DesireKey> typesOfDesiresToConsiderWhenCommitting,
        Set<DesireKey> typesOfDesiresToConsiderWhenRemovingCommitment,
        CommandFormulationStrategy<ActCommand.Own, IntentionCommand.OwnActing> commandCreationStrategy,
        ReactionOnChangeStrategy reactionOnChangeStrategy,
        ReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
      super(decisionInDesire, decisionInIntention, typesOfDesiresToConsiderWhenCommitting,
          typesOfDesiresToConsiderWhenRemovingCommitment, commandCreationStrategy,
          reactionOnChangeStrategy,
          reactionOnChangeStrategyInIntention);
    }

    //builder with default fields
    public static class WithActingCommandDesiredBySelfBuilder extends
        CommonConfiguration.CommonConfigurationBuilder {

      private Set<DesireKey> typesOfDesiresToConsiderWhenCommitting = new HashSet<>();
      private Set<DesireKey> typesOfDesiresToConsiderWhenRemovingCommitment = new HashSet<>();
    }
  }

  //For reasoning command desired by itself
  public static class WithReasoningCommandDesiredBySelf extends
      ConfigurationWithCommand<CommandFormulationStrategy<ReasoningCommand, IntentionCommand.OwnReasoning>> {

    @Builder
    private WithReasoningCommandDesiredBySelf(CommitmentDeciderInitializer decisionInDesire,
        CommitmentDeciderInitializer decisionInIntention,
        Set<DesireKey> typesOfDesiresToConsiderWhenCommitting,
        Set<DesireKey> typesOfDesiresToConsiderWhenRemovingCommitment,
        CommandFormulationStrategy<ReasoningCommand, IntentionCommand.OwnReasoning> commandCreationStrategy,
        ReactionOnChangeStrategy reactionOnChangeStrategy,
        ReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
      super(decisionInDesire, decisionInIntention, typesOfDesiresToConsiderWhenCommitting,
          typesOfDesiresToConsiderWhenRemovingCommitment, commandCreationStrategy,
          reactionOnChangeStrategy,
          reactionOnChangeStrategyInIntention);
    }

    //builder with default fields
    public static class WithReasoningCommandDesiredBySelfBuilder extends
        CommonConfiguration.CommonConfigurationBuilder {

      private Set<DesireKey> typesOfDesiresToConsiderWhenCommitting = new HashSet<>();
      private Set<DesireKey> typesOfDesiresToConsiderWhenRemovingCommitment = new HashSet<>();
    }
  }

}
