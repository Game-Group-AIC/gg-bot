package aic.gas.sc.gg_bot.mas.model.planing;

import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import aic.gas.sc.gg_bot.mas.model.planing.command.CommandForIntention;
import aic.gas.sc.gg_bot.mas.model.planing.command.ICommandFormulationStrategy;
import aic.gas.sc.gg_bot.mas.model.planing.command.ReasoningCommand;
import lombok.Getter;

/**
 * Template for intention which returns instance of CommandForIntention
 */
public abstract class IntentionCommand<V extends InternalDesire<? extends IntentionCommand<?, ?>>, T extends CommandForIntention<? extends IntentionCommand<V, T>>> extends
    Intention<V> {

  IntentionCommand(V originalDesire, CommitmentDeciderInitializer removeCommitment,
      IReactionOnChangeStrategy IReactionOnChangeStrategy) {
    super(originalDesire, removeCommitment, IReactionOnChangeStrategy);
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
        ICommandFormulationStrategy<ActCommand.DesiredByAnotherAgent, FromAnotherAgent> commandCreationStrategy,
        IReactionOnChangeStrategy IReactionOnChangeStrategy) {
      super(originalDesire, removeCommitment, IReactionOnChangeStrategy);
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
        ICommandFormulationStrategy<ReasoningCommand, OwnReasoning> commandCreationStrategy,
        IReactionOnChangeStrategy IReactionOnChangeStrategy) {
      super(originalDesire, removeCommitment, IReactionOnChangeStrategy);
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
        ICommandFormulationStrategy<ActCommand.Own, OwnActing> commandCreationStrategy,
        IReactionOnChangeStrategy IReactionOnChangeStrategy) {
      super(originalDesire, removeCommitment, IReactionOnChangeStrategy);
      this.command = commandCreationStrategy.formCommand(this);
    }


    @Override
    public ActCommand.Own getCommand() {
      return command;
    }
  }


}
