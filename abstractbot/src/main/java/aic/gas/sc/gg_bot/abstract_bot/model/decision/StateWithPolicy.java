package aic.gas.sc.gg_bot.abstract_bot.model.decision;

import aic.gas.sc.gg_bot.abstract_bot.utils.VectorNormalizer;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import jsat.linear.Vec;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Class containing feature vector describing state and probability over actions according to policy
 */
@Getter
@EqualsAndHashCode(of = {"featureVector"})
public class StateWithPolicy implements Serializable {

  private static final transient Random RANDOM = new Random();

  private final Vec featureVector;
  private final Map<NextActionEnumerations, Double> nextActions;

  /**
   * Create state with transition
   */
  public StateWithPolicy(Vec featureVector, Map<NextActionEnumerations, Double> nextActions) {
    this.nextActions = nextActions;
    this.featureVector = featureVector.clone();
  }

  /**
   * Sample action according to policy
   */
  public Optional<NextActionEnumerations> sampleNextActionAccordingToPolicy() {
    double randomThreshold = Math.random(), sumOfProbability = 0;
    for (Entry<NextActionEnumerations, Double> entry : nextActions.entrySet()) {
      sumOfProbability = sumOfProbability + entry.getValue();
      if (randomThreshold <= sumOfProbability) {
        return Optional.ofNullable(entry.getKey());
      }
    }
    return Optional.empty();
  }

  /**
   * Returns distance between center and passed instance
   */
  public double distance(Vec anotherPoint) {
    return VectorNormalizer.DISTANCE_FUNCTION.dist(featureVector, anotherPoint);
  }

}
