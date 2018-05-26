package aic.gas.sc.gg_bot.abstract_bot.model.decision;

import burlap.mdp.core.action.Action;
import java.util.HashMap;
import java.util.Map;

public class Policy {

  private final MetaPolicy metaPolicy;
  private final Map<Integer, DecisionInState> cache = new HashMap<>();
  private final OurProbabilisticPolicy policy;

  public Policy(MetaPolicy metaPolicy) {
    this.metaPolicy = metaPolicy;
    this.policy = metaPolicy.createPolicy();
  }

  /**
   * For given state (represented by feature vector) return optimal action based on policy
   */
  public boolean nextAction(double[] featureVector, int frame, int agentId,
      int forHowLongToCacheDecision) {
    DecisionInState decisionInState = cache.get(agentId);
    if (decisionInState == null || decisionInState.canChangeDecision(featureVector, frame,
        forHowLongToCacheDecision)) {
      OurState state = metaPolicy.buildState(featureVector);
      Action action = policy.selectActionInState(state);
      decisionInState = new DecisionInState(featureVector, frame,
          ((NextActionEnumerations) action).commit());
      cache.put(agentId, decisionInState);
    }
    return decisionInState.isCommit();
  }

}
