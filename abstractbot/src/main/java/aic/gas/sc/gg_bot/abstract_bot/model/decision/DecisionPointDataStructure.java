package aic.gas.sc.gg_bot.abstract_bot.model.decision;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import lombok.Getter;

/**
 * Serializable data structure containing data of decision point
 */
public class DecisionPointDataStructure implements Serializable {

  final Set<StateWithTransition> states;
  final List<FeatureNormalizer> normalizers;

  public DecisionPointDataStructure(Set<StateWithTransition> states,
      List<FeatureNormalizer> normalizers) {
    this.states = states;
    this.normalizers = normalizers;
  }

  /**
   * Class containing feature vector describing state and optimal action (according to policy) to
   * make transition
   */
  public static class StateWithTransition implements Serializable {

    final double[] featureVector;

    @Getter
    final Map<NextActionEnumerations, Double> nextActions;

    /**
     * Create state with transition
     */
    public StateWithTransition(double[] featureVector,
        Map<NextActionEnumerations, Double> nextActions) {
      this.nextActions = nextActions;
      this.featureVector = featureVector.clone();
    }

    public Vec getFeatureVector() {
      return new DenseVector(featureVector);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      StateWithTransition that = (StateWithTransition) o;
      return Arrays.equals(featureVector, that.featureVector) &&
          Objects.equals(nextActions, that.nextActions);
    }

    @Override
    public int hashCode() {

      int result = Objects.hash(nextActions);
      result = 31 * result + Arrays.hashCode(featureVector);
      return result;
    }
  }

}
