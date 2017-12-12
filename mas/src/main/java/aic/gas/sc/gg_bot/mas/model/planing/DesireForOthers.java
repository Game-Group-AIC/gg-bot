package aic.gas.sc.gg_bot.mas.model.planing;

import aic.gas.sc.gg_bot.mas.model.agents.Agent;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;

/**
 * Desire class for agent's desires to be achieved by others
 */
public class DesireForOthers extends InternalDesire<IntentionWithDesireForOtherAgents> {

  private final DesireKey sharedDesireKey;
  private final int limitOnNumberOfAgentsToCommit;
  private final ReactionOnChangeStrategy reactionOnChangeStrategyInIntention;

  public DesireForOthers(DesireKey desireKey, WorkingMemory memory,
      CommitmentDeciderInitializer commitmentDecider,
      CommitmentDeciderInitializer removeCommitment, DesireKey sharedDesireKey,
      int limitOnNumberOfAgentsToCommit, ReactionOnChangeStrategy reactionOnChangeStrategy,
      ReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
    super(desireKey, memory, commitmentDecider, removeCommitment, false, reactionOnChangeStrategy);
    this.sharedDesireKey = sharedDesireKey;
    this.limitOnNumberOfAgentsToCommit = limitOnNumberOfAgentsToCommit;
    this.reactionOnChangeStrategyInIntention = reactionOnChangeStrategyInIntention;
  }

  public DesireForOthers(DesireKey desireKey, WorkingMemory memory,
      CommitmentDeciderInitializer commitmentDecider,
      CommitmentDeciderInitializer removeCommitment, DesireKey sharedDesireKey,
      int limitOnNumberOfAgentsToCommit, DesireParameters parentsDesireParameters,
      ReactionOnChangeStrategy reactionOnChangeStrategy,
      ReactionOnChangeStrategy reactionOnChangeStrategyInIntention) {
    super(desireKey, memory, commitmentDecider, removeCommitment, false, parentsDesireParameters,
        reactionOnChangeStrategy);
    this.sharedDesireKey = sharedDesireKey;
    this.limitOnNumberOfAgentsToCommit = limitOnNumberOfAgentsToCommit;
    this.reactionOnChangeStrategyInIntention = reactionOnChangeStrategyInIntention;
  }

  @Override
  public IntentionWithDesireForOtherAgents formIntention(Agent agent) {
    return new IntentionWithDesireForOtherAgents(this, agent, removeCommitment,
        limitOnNumberOfAgentsToCommit,
        sharedDesireKey, reactionOnChangeStrategyInIntention);
  }
}
