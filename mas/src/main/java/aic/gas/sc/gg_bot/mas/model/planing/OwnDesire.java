package aic.gas.sc.gg_bot.mas.model.planing;

import aic.gas.sc.gg_bot.mas.model.agents.Agent;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import aic.gas.sc.gg_bot.mas.model.planing.IntentionCommand.OwnActing;
import aic.gas.sc.gg_bot.mas.model.planing.IntentionCommand.OwnReasoning;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand.Own;
import aic.gas.sc.gg_bot.mas.model.planing.command.ICommandFormulationStrategy;
import aic.gas.sc.gg_bot.mas.model.planing.command.ReasoningCommand;
import java.util.Set;

/**
 * Template for agent's own desires
 */
public abstract class OwnDesire<T extends Intention<? extends OwnDesire<?>>> extends
    InternalDesire<T> {

  final IReactionOnChangeStrategy reactionOnChangeStrategyInIntention;

  OwnDesire(DesireKey desireKey, WorkingMemory memory,
      CommitmentDeciderInitializer commitmentDecider,
      CommitmentDeciderInitializer removeCommitment, boolean isAbstract,
      IReactionOnChangeStrategy reactionOnChangeStrategy,
      IReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
    super(desireKey, memory, commitmentDecider, removeCommitment, isAbstract,
        reactionOnChangeStrategy);
    this.reactionOnChangeStrategyInIntention = reactionOnChangeStrategyInIntention;
  }

  OwnDesire(DesireKey desireKey, WorkingMemory memory,
      CommitmentDeciderInitializer commitmentDecider,
      CommitmentDeciderInitializer removeCommitment, boolean isAbstract,
      DesireParameters parentsDesireParameters, IReactionOnChangeStrategy reactionOnChangeStrategy,
      IReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
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
        IReactionOnChangeStrategy reactionOnChangeStrategy,
        IReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
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
        IReactionOnChangeStrategy reactionOnChangeStrategy,
        IReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
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

    private final ICommandFormulationStrategy<ReasoningCommand, OwnReasoning> commandCreationStrategy;

    public Reasoning(DesireKey desireKey, WorkingMemory memory,
        CommitmentDeciderInitializer commitmentDecider,
        CommitmentDeciderInitializer removeCommitment,
        ICommandFormulationStrategy<ReasoningCommand, OwnReasoning> commandCreationStrategy,
        IReactionOnChangeStrategy reactionOnChangeStrategy,
        IReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
      super(desireKey, memory, commitmentDecider, removeCommitment, false, reactionOnChangeStrategy,
          reactionOnChangeStrategyInIntention);
      this.commandCreationStrategy = commandCreationStrategy;
    }

    public Reasoning(DesireKey desireKey, WorkingMemory memory,
        CommitmentDeciderInitializer commitmentDecider,
        CommitmentDeciderInitializer removeCommitment,
        ICommandFormulationStrategy<ReasoningCommand, OwnReasoning> commandCreationStrategy,
        DesireParameters parentsDesireParameters,
        IReactionOnChangeStrategy reactionOnChangeStrategy,
        IReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
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

    private final ICommandFormulationStrategy<Own, OwnActing> commandCreationStrategy;

    public Acting(DesireKey desireKey, WorkingMemory memory,
        CommitmentDeciderInitializer commitmentDecider,
        CommitmentDeciderInitializer removeCommitment,
        ICommandFormulationStrategy<Own, OwnActing> commandCreationStrategy,
        IReactionOnChangeStrategy reactionOnChangeStrategy,
        IReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
      super(desireKey, memory, commitmentDecider, removeCommitment, false, reactionOnChangeStrategy,
          reactionOnChangeStrategyInIntention);
      this.commandCreationStrategy = commandCreationStrategy;
    }

    public Acting(DesireKey desireKey, WorkingMemory memory,
        CommitmentDeciderInitializer commitmentDecider,
        CommitmentDeciderInitializer removeCommitment,
        ICommandFormulationStrategy<Own, OwnActing> commandCreationStrategy,
        DesireParameters parentsDesireParameters,
        IReactionOnChangeStrategy reactionOnChangeStrategy,
        IReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
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
