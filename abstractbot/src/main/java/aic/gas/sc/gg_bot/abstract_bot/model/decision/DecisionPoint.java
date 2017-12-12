package aic.gas.sc.gg_bot.abstract_bot.model.decision;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import aic.gas.sc.gg_bot.abstract_bot.utils.Configuration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * DecisionPoint decide next action based on current state. It is initialized from
 * DecisionPointDataStructure
 */
@Getter
@Slf4j
public class DecisionPoint {

  private final List<StateWithTransition> states;
  private final List<FeatureNormalizer> normalizers;

  public DecisionPoint(DecisionPointDataStructure dataStructure) {
    this.states = dataStructure.states.stream()
        .map(StateWithTransition::new)
        .collect(Collectors.toList());
    this.normalizers = dataStructure.normalizers;
  }

  /**
   * For given state (represented by feature vector) return optimal action based on policy
   */
  public boolean nextAction(double[] featureVector) {
    Vec anotherInstance = new DenseVector((Configuration
        .normalizeFeatureVector(featureVector, normalizers)));
    Optional<StateWithTransition> closestState = states.stream()
        .min(Comparator.comparingDouble(o -> o.distance(anotherInstance)));
    if (!closestState.isPresent()) {
      log.error("No state is present.");
      return false;
    }
    return closestState.get().nextAction.commit();
  }


  /**
   * StateWithTransition to compute distance between instances and return next action (commitment)
   * based on policy
   */
  @Getter
  public static class StateWithTransition {

    final NextActionEnumerations nextAction;
    private final Vec center;

    private StateWithTransition(
        DecisionPointDataStructure.StateWithTransition stateWithTransition) {
      this.center = stateWithTransition.getFeatureVector();
      this.nextAction = stateWithTransition.nextAction;
    }

    /**
     * Returns distance between center and passed instance
     */
    double distance(Vec anotherPoint) {
      return Configuration.DISTANCE_FUNCTION.dist(center, anotherPoint);
    }

  }

}
