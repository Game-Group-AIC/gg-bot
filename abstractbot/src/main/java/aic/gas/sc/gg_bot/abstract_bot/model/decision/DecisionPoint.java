package aic.gas.sc.gg_bot.abstract_bot.model.decision;

import static aic.gas.sc.gg_bot.abstract_bot.model.decision.NextActionEnumerations.NO;
import static aic.gas.sc.gg_bot.abstract_bot.model.decision.NextActionEnumerations.YES;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import aic.gas.sc.gg_bot.abstract_bot.utils.Configuration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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
    return closestState.get().commit();
  }


  /**
   * StateWithTransition to compute distance between instances and return next action (commitment)
   * based on policy
   */
  @Getter
  public static class StateWithTransition {

    private final double probOfYes;
    private final double probOfNo;
    private final Vec center;
    private transient static final Random RANDOM = new Random();

    private StateWithTransition(
        DecisionPointDataStructure.StateWithTransition stateWithTransition) {
      this.center = stateWithTransition.getFeatureVector();
      this.probOfYes = stateWithTransition.getNextActions().getOrDefault(YES, 0.0);
      this.probOfNo = stateWithTransition.getNextActions().getOrDefault(NO, 0.0);
    }

    boolean commit() {
      if (RANDOM.nextBoolean()) {
        if (RANDOM.nextDouble() <= probOfYes) {
          return YES.commit();
        }
        return NO.commit();
      } else {
        if (RANDOM.nextDouble() <= probOfNo) {
          return NO.commit();
        }
        return YES.commit();
      }
    }

    /**
     * Returns distance between center and passed instance
     */
    double distance(Vec anotherPoint) {
      return Configuration.DISTANCE_FUNCTION.dist(center, anotherPoint);
    }

  }

}
