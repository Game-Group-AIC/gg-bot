package aic.gas.sc.gg_bot.abstract_bot.model.decision;

import burlap.mdp.core.action.Action;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * Serializable data structure containing data of decision point
 */
@Getter
public class Policy implements Serializable {

  private static final transient List<Action> ACTIONS = Arrays
      .stream(NextActionEnumerations.values())
      .collect(Collectors.toList());
//  private static final transient IVFInitializer INITIALIZER = () -> {
//
//  };

  private transient Map<Integer, DecisionInState> cache = new HashMap<>();
  private transient OurProbabilisticPolicy policy;

  /**
   * A map from feature identifiers to function weights
   */
  private final Map<Integer, Double> weights;

  public Policy(Map<Integer, Double> weights) {
    this.weights = weights;
  }

  /**
   * For given state (represented by feature vector) return optimal action based on policy
   */
  public boolean nextAction(double[] featureVector, int frame, int agentId,
      int forHowLongToCacheDecision) {
    DecisionInState decisionInState = cache.get(agentId);
    if (decisionInState == null || decisionInState
        .canChangeDecision(featureVector, frame, forHowLongToCacheDecision)) {
//      decisionInState = new DecisionInState(featureVector, frame,
//          getState(featureVector).sampleNextActionAccordingToPolicy()
//              .orElse(RANDOM.nextBoolean() ? NextActionEnumerations.NO : NextActionEnumerations.YES)
//              .commit());
//      cache.put(agentId, decisionInState);
    }
    return decisionInState.isCommit();
  }

  public void init() {
    this.cache = new HashMap<>();
  }

}
