package aic.gas.sc.gg_bot.mas.model.planing;

import aic.gas.sc.gg_bot.mas.model.knowledge.DataForDecision;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import java.util.List;

/**
 * Decision point to decide agent's commitmentDecider to task
 */
public class CommitmentDecider {

  private final CommitmentDeciderInitializer.DecisionStrategy decisionStrategy;
  private final DataForDecision dataForDecision;

  CommitmentDecider(CommitmentDeciderInitializer commitmentDeciderInitializer,
      DesireParameters desireParameters) {
    this.dataForDecision = new DataForDecision(desireParameters.getDesireKey(), desireParameters,
        commitmentDeciderInitializer);
    this.decisionStrategy = commitmentDeciderInitializer.getDecisionStrategy();
  }

  /**
   * Returns if agent should commit to desire and make intention from it
   */
  public boolean shouldCommit(List<DesireKey> madeCommitmentToTypes,
      List<DesireKey> didNotMakeCommitmentToTypes,
      List<DesireKey> typesAboutToMakeDecision, WorkingMemory memory) {
    dataForDecision
        .updateBeliefs(madeCommitmentToTypes, didNotMakeCommitmentToTypes, typesAboutToMakeDecision,
            memory);
    if (dataForDecision.isBeliefsChanged() || dataForDecision.isUseFactsInMemory()) {
      dataForDecision.setBeliefsChanged(false);
      return decisionStrategy.shouldCommit(dataForDecision, memory);
    } else {
      return false;
    }
  }

  /**
   * Returns if agent should commit to desire and make intention from it
   */
  public boolean shouldCommit(List<DesireKey> madeCommitmentToTypes,
      List<DesireKey> didNotMakeCommitmentToTypes,
      List<DesireKey> typesAboutToMakeDecision, WorkingMemory memory, int numberOfCommittedAgents) {
    dataForDecision
        .updateBeliefs(madeCommitmentToTypes, didNotMakeCommitmentToTypes, typesAboutToMakeDecision,
            memory,
            numberOfCommittedAgents);
    if (dataForDecision.isBeliefsChanged() || dataForDecision.isUseFactsInMemory()) {
      dataForDecision.setBeliefsChanged(false);
      return decisionStrategy.shouldCommit(dataForDecision, memory);
    } else {
      return false;
    }
  }

}
