package aic.gas.sc.gg_bot.abstract_bot.model.decision;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import aic.gas.sc.gg_bot.abstract_bot.utils.VectorNormalizer;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Serializable data structure containing data of decision point
 */
@AllArgsConstructor
@Getter
public class MDPForDecisionWithPolicy implements Serializable {

  private final List<StateWithPolicy> states;
  private final List<FeatureNormalizer> normalizers;

  private transient final Map<Integer, DecisionInState> cache = new HashMap<>();
  private static final transient Random RANDOM = new Random();

  /**
   * For given state (represented by feature vector) return optimal action based on policy
   */
  public boolean nextAction(double[] featureVector, int frame, int agentId,
      int forHowLongToCacheDecision) {
    DecisionInState decisionInState = cache.get(agentId);
    if (decisionInState == null || decisionInState
        .canChangeDecision(featureVector, frame, forHowLongToCacheDecision)) {
      decisionInState = new DecisionInState(featureVector, frame,
          getState(featureVector).sampleNextActionAccordingToPolicy()
              .orElse(RANDOM.nextBoolean() ? NextActionEnumerations.NO : NextActionEnumerations.YES)
              .commit());
      cache.put(agentId, decisionInState);
    }
    return decisionInState.isCommit();
  }

  private StateWithPolicy getState(double[] featureVector) {
    Vec anotherInstance = new DenseVector(
        VectorNormalizer.normalizeFeatureVector(featureVector, normalizers));
    return states.stream()
        .min(Comparator.comparingDouble(o -> o.distance(anotherInstance)))
        .get();
  }

}
