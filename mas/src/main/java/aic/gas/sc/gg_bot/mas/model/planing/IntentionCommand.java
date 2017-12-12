package aic.gas.sc.gg_bot.mas.model.planing;

import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import aic.gas.sc.gg_bot.mas.model.planing.command.CommandForIntention;
import aic.gas.sc.gg_bot.mas.model.planing.command.CommandFormulationStrategy;
import aic.gas.sc.gg_bot.mas.model.planing.command.ReasoningCommand;
import lombok.Getter;

/**
 * Template for intention which returns instance of CommandForIntention
 */
public abstract class IntentionCommand<V extends InternalDesire<? extends IntentionCommand<?, ?>>, T extends CommandForIntention<? extends IntentionCommand<V, T>>> extends
    Intention<V> {

  IntentionCommand(V originalDesire, CommitmentDeciderInitializer removeCommitment,
      ReactionOnChangeStrategy reactionOnChangeStrategy) {
    super(originalDesire, removeCommitment, reactionOnChangeStrategy);
  }

  /**
   * Method returns command which will be executed by framework
   */
  public abstract T getCommand();

  /**
   * From another agent's desire
   */
  public static class FromAnotherAgent extends
      IntentionCommand<DesireFromAnotherAgent.WithIntentionWithPlan, ActCommand.DesiredByAnotherAgent> {

    @Getter
    private final SharedDesireForAgents sharedDesireForAgents;
    private final ActCommand.DesiredByAnotherAgent command;

    FromAnotherAgent(DesireFromAnotherAgent.WithIntentionWithPlan originalDesire,
        CommitmentDeciderInitializer removeCommitment,
        CommandFormulationStrategy<ActCommand.DesiredByAnotherAgent, FromAnotherAgent> commandCreationStrategy,
        ReactionOnChangeStrategy reactionOnChangeStrategy) {
      super(originalDesire, removeCommitment, reactionOnChangeStrategy);
      this.sharedDesireForAgents = originalDesire.getDesireForAgents();
      this.command = commandCreationStrategy.formCommand(this);
    }

    @Override
    public ActCommand.DesiredByAnotherAgent getCommand() {
      return command;
    }
  }


  /**
   * Own command for reasoning
   */
  public static class OwnReasoning extends IntentionCommand<OwnDesire.Reasoning, ReasoningCommand> {

    private final ReasoningCommand command;

    OwnReasoning(OwnDesire.Reasoning originalDesire, CommitmentDeciderInitializer removeCommitment,
        CommandFormulationStrategy<ReasoningCommand, OwnReasoning> commandCreationStrategy,
        ReactionOnChangeStrategy reactionOnChangeStrategy) {
      super(originalDesire, removeCommitment, reactionOnChangeStrategy);
      this.command = commandCreationStrategy.formCommand(this);
    }

    @Override
    public ReasoningCommand getCommand() {
      return command;
    }
  }

  /**
   * Own command for acting
   */
  public static class OwnActing extends IntentionCommand<OwnDesire.Acting, ActCommand.Own> {

    private final ActCommand.Own command;

    OwnActing(OwnDesire.Acting originalDesire, CommitmentDeciderInitializer removeCommitment,
        CommandFormulationStrategy<ActCommand.Own, OwnActing> commandCreationStrategy,
        ReactionOnChangeStrategy reactionOnChangeStrategy) {
      super(originalDesire, removeCommitment, reactionOnChangeStrategy);
      this.command = commandCreationStrategy.formCommand(this);
    }


    @Override
    public ActCommand.Own getCommand() {
      return command;
    }
  }


}
