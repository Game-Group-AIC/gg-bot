package aic.gas.sc.gg_bot.abstract_bot.model.decision;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class DecisionInState {

  private final double[] featureVector;
  private final int madeInFrame;
  @Getter
  private final boolean commit;

  boolean canChangeDecision(double[] currentFeatureVector, int currentFrame,
      int forHowLongToCacheDecision) {
    return currentFrame - madeInFrame >= forHowLongToCacheDecision && !Arrays
        .equals(featureVector, currentFeatureVector);
  }

}
