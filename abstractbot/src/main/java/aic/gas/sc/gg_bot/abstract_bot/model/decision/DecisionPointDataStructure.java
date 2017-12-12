package aic.gas.sc.gg_bot.abstract_bot.model.decision;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
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
    final NextActionEnumerations nextAction;

    /**
     * Create state with transition
     */
    public StateWithTransition(double[] featureVector, NextActionEnumerations nextAction) {
      this.nextAction = nextAction;
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

      if (!Arrays.equals(featureVector, that.featureVector)) {
        return false;
      }
      return nextAction == that.nextAction;
    }

    @Override
    public int hashCode() {
      int result = Arrays.hashCode(featureVector);
      result = 31 * result + nextAction.hashCode();
      return result;
    }
  }

}
