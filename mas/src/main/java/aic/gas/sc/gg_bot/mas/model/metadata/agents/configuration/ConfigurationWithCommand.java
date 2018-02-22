package aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration;

import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.IReactionOnChangeStrategy;
import aic.gas.sc.gg_bot.mas.model.planing.IntentionCommand;
import aic.gas.sc.gg_bot.mas.model.planing.IntentionCommand.FromAnotherAgent;
import aic.gas.sc.gg_bot.mas.model.planing.IntentionCommand.OwnActing;
import aic.gas.sc.gg_bot.mas.model.planing.IntentionCommand.OwnReasoning;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand.DesiredByAnotherAgent;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand.Own;
import aic.gas.sc.gg_bot.mas.model.planing.command.CommandForIntention;
import aic.gas.sc.gg_bot.mas.model.planing.command.ICommandFormulationStrategy;
import aic.gas.sc.gg_bot.mas.model.planing.command.ReasoningCommand;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

/**
 * Template for configuration container with strategy to create command
 */
@Getter
public class ConfigurationWithCommand<K extends ICommandFormulationStrategy<? extends CommandForIntention<?>, ? extends IntentionCommand<?, ?>>> extends
    CommonConfiguration {

  private K commandCreationStrategy;

  ConfigurationWithCommand(CommitmentDeciderInitializer decisionInDesire,
      CommitmentDeciderInitializer decisionInIntention,
      Set<DesireKey> typesOfDesiresToConsiderWhenCommitting,
      Set<DesireKey> typesOfDesiresToConsiderWhenRemovingCommitment,
      K commandCreationStrategy, IReactionOnChangeStrategy reactionOnChangeStrategy,
      IReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
    super(decisionInDesire, decisionInIntention, typesOfDesiresToConsiderWhenCommitting,
        typesOfDesiresToConsiderWhenRemovingCommitment,
        reactionOnChangeStrategy, reactionOnChangeStrategyInIntention);
    this.commandCreationStrategy = commandCreationStrategy;
  }

  //For acting command desired by another agent
  public static class WithActingCommandDesiredByOtherAgent extends
      ConfigurationWithCommand<ICommandFormulationStrategy<DesiredByAnotherAgent, FromAnotherAgent>> {

    @Builder
    private WithActingCommandDesiredByOtherAgent(CommitmentDeciderInitializer decisionInDesire,
        CommitmentDeciderInitializer decisionInIntention,
        Set<DesireKey> typesOfDesiresToConsiderWhenCommitting,
        Set<DesireKey> typesOfDesiresToConsiderWhenRemovingCommitment,
        ICommandFormulationStrategy<DesiredByAnotherAgent, FromAnotherAgent> commandCreationStrategy,
        IReactionOnChangeStrategy reactionOnChangeStrategy,
        IReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
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
      ConfigurationWithCommand<ICommandFormulationStrategy<Own, OwnActing>> {

    @Builder
    private WithActingCommandDesiredBySelf(CommitmentDeciderInitializer decisionInDesire,
        CommitmentDeciderInitializer decisionInIntention,
        Set<DesireKey> typesOfDesiresToConsiderWhenCommitting,
        Set<DesireKey> typesOfDesiresToConsiderWhenRemovingCommitment,
        ICommandFormulationStrategy<Own, OwnActing> commandCreationStrategy,
        IReactionOnChangeStrategy reactionOnChangeStrategy,
        IReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
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
      ConfigurationWithCommand<ICommandFormulationStrategy<ReasoningCommand, OwnReasoning>> {

    @Builder
    private WithReasoningCommandDesiredBySelf(CommitmentDeciderInitializer decisionInDesire,
        CommitmentDeciderInitializer decisionInIntention,
        Set<DesireKey> typesOfDesiresToConsiderWhenCommitting,
        Set<DesireKey> typesOfDesiresToConsiderWhenRemovingCommitment,
        ICommandFormulationStrategy<ReasoningCommand, OwnReasoning> commandCreationStrategy,
        IReactionOnChangeStrategy reactionOnChangeStrategy,
        IReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
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
