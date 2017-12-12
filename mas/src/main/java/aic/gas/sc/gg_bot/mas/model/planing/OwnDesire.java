package aic.gas.sc.gg_bot.mas.model.planing;

import aic.gas.sc.gg_bot.mas.model.agents.Agent;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import aic.gas.sc.gg_bot.mas.model.planing.command.CommandFormulationStrategy;
import aic.gas.sc.gg_bot.mas.model.planing.command.ReasoningCommand;
import java.util.Set;

/**
 * Template for agent's own desires
 */
public abstract class OwnDesire<T extends Intention<? extends OwnDesire<?>>> extends
    InternalDesire<T> {

  final ReactionOnChangeStrategy reactionOnChangeStrategyInIntention;

  OwnDesire(DesireKey desireKey, WorkingMemory memory,
      CommitmentDeciderInitializer commitmentDecider,
      CommitmentDeciderInitializer removeCommitment, boolean isAbstract,
      ReactionOnChangeStrategy reactionOnChangeStrategy,
      ReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
    super(desireKey, memory, commitmentDecider, removeCommitment, isAbstract,
        reactionOnChangeStrategy);
    this.reactionOnChangeStrategyInIntention = reactionOnChangeStrategyInIntention;
  }

  OwnDesire(DesireKey desireKey, WorkingMemory memory,
      CommitmentDeciderInitializer commitmentDecider,
      CommitmentDeciderInitializer removeCommitment, boolean isAbstract,
      DesireParameters parentsDesireParameters, ReactionOnChangeStrategy reactionOnChangeStrategy,
      ReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
    super(desireKey, memory, commitmentDecider, removeCommitment, isAbstract,
        parentsDesireParameters,
        reactionOnChangeStrategy);
    this.reactionOnChangeStrategyInIntention = reactionOnChangeStrategyInIntention;
  }

  /**
   * Desire to initialize abstract intention
   */
  public static class WithAbstractIntention extends
      OwnDesire<AbstractIntention<WithAbstractIntention>> {

    private final Set<DesireKey> desiresForOthers;
    private final Set<DesireKey> desiresWithAbstractIntention;
    private final Set<DesireKey> desiresWithIntentionToAct;
    private final Set<DesireKey> desiresWithIntentionToReason;

    public WithAbstractIntention(DesireKey desireKey, WorkingMemory memory,
        CommitmentDeciderInitializer commitmentDecider,
        CommitmentDeciderInitializer removeCommitment, Set<DesireKey> desiresForOthers,
        Set<DesireKey> desiresWithAbstractIntention,
        Set<DesireKey> desiresWithIntentionToAct, Set<DesireKey> desiresWithIntentionToReason,
        ReactionOnChangeStrategy reactionOnChangeStrategy,
        ReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
      super(desireKey, memory, commitmentDecider, removeCommitment, true, reactionOnChangeStrategy,
          reactionOnChangeStrategyInIntention);
      this.desiresForOthers = desiresForOthers;
      this.desiresWithAbstractIntention = desiresWithAbstractIntention;
      this.desiresWithIntentionToAct = desiresWithIntentionToAct;
      this.desiresWithIntentionToReason = desiresWithIntentionToReason;
    }

    public WithAbstractIntention(DesireKey desireKey, WorkingMemory memory,
        CommitmentDeciderInitializer commitmentDecider,
        CommitmentDeciderInitializer removeCommitment, Set<DesireKey> desiresForOthers,
        Set<DesireKey> desiresWithAbstractIntention, Set<DesireKey> desiresWithIntentionToAct,
        Set<DesireKey> desiresWithIntentionToReason, DesireParameters parentsDesireParameters,
        ReactionOnChangeStrategy reactionOnChangeStrategy,
        ReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
      super(desireKey, memory, commitmentDecider, removeCommitment, true, parentsDesireParameters,
          reactionOnChangeStrategy, reactionOnChangeStrategyInIntention);
      this.desiresForOthers = desiresForOthers;
      this.desiresWithAbstractIntention = desiresWithAbstractIntention;
      this.desiresWithIntentionToAct = desiresWithIntentionToAct;
      this.desiresWithIntentionToReason = desiresWithIntentionToReason;
    }

    @Override
    public AbstractIntention<OwnDesire.WithAbstractIntention> formIntention(Agent agent) {
      return new AbstractIntention<>(this, removeCommitment, desiresForOthers,
          desiresWithAbstractIntention, desiresWithIntentionToAct, desiresWithIntentionToReason,
          reactionOnChangeStrategyInIntention);
    }
  }

  /**
   * Desire to initialize intention with reasoning command
   */
  public static class Reasoning extends OwnDesire<IntentionCommand.OwnReasoning> {

    private final CommandFormulationStrategy<ReasoningCommand, IntentionCommand.OwnReasoning> commandCreationStrategy;

    public Reasoning(DesireKey desireKey, WorkingMemory memory,
        CommitmentDeciderInitializer commitmentDecider,
        CommitmentDeciderInitializer removeCommitment,
        CommandFormulationStrategy<ReasoningCommand, IntentionCommand.OwnReasoning> commandCreationStrategy,
        ReactionOnChangeStrategy reactionOnChangeStrategy,
        ReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
      super(desireKey, memory, commitmentDecider, removeCommitment, false, reactionOnChangeStrategy,
          reactionOnChangeStrategyInIntention);
      this.commandCreationStrategy = commandCreationStrategy;
    }

    public Reasoning(DesireKey desireKey, WorkingMemory memory,
        CommitmentDeciderInitializer commitmentDecider,
        CommitmentDeciderInitializer removeCommitment,
        CommandFormulationStrategy<ReasoningCommand, IntentionCommand.OwnReasoning> commandCreationStrategy,
        DesireParameters parentsDesireParameters, ReactionOnChangeStrategy reactionOnChangeStrategy,
        ReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
      super(desireKey, memory, commitmentDecider, removeCommitment, false, parentsDesireParameters,
          reactionOnChangeStrategy, reactionOnChangeStrategyInIntention);
      this.commandCreationStrategy = commandCreationStrategy;
    }

    @Override
    public IntentionCommand.OwnReasoning formIntention(Agent agent) {
      return new IntentionCommand.OwnReasoning(this, removeCommitment, commandCreationStrategy,
          reactionOnChangeStrategyInIntention);
    }
  }

  /**
   * Desire to initialize intention with acting command
   */
  public static class Acting extends OwnDesire<IntentionCommand.OwnActing> {

    private final CommandFormulationStrategy<ActCommand.Own, IntentionCommand.OwnActing> commandCreationStrategy;

    public Acting(DesireKey desireKey, WorkingMemory memory,
        CommitmentDeciderInitializer commitmentDecider,
        CommitmentDeciderInitializer removeCommitment,
        CommandFormulationStrategy<ActCommand.Own, IntentionCommand.OwnActing> commandCreationStrategy,
        ReactionOnChangeStrategy reactionOnChangeStrategy,
        ReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
      super(desireKey, memory, commitmentDecider, removeCommitment, false, reactionOnChangeStrategy,
          reactionOnChangeStrategyInIntention);
      this.commandCreationStrategy = commandCreationStrategy;
    }

    public Acting(DesireKey desireKey, WorkingMemory memory,
        CommitmentDeciderInitializer commitmentDecider,
        CommitmentDeciderInitializer removeCommitment,
        CommandFormulationStrategy<ActCommand.Own, IntentionCommand.OwnActing> commandCreationStrategy,
        DesireParameters parentsDesireParameters, ReactionOnChangeStrategy reactionOnChangeStrategy,
        ReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
      super(desireKey, memory, commitmentDecider, removeCommitment, false, parentsDesireParameters,
          reactionOnChangeStrategy, reactionOnChangeStrategyInIntention);
      this.commandCreationStrategy = commandCreationStrategy;
    }

    @Override
    public IntentionCommand.OwnActing formIntention(Agent agent) {
      return new IntentionCommand.OwnActing(this, removeCommitment, commandCreationStrategy,
          reactionOnChangeStrategyInIntention);
    }
  }

}
