package aic.gas.sc.gg_bot.abstract_bot.model.decision;

import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.Map;

/**
 * Enumeration of all possible commitments based on policy
 */
public enum NextActionEnumerations implements Serializable, INextActionStrategy {
  YES {
    @Override
    public boolean commit() {
      return true;
    }
  },
  NO {
    @Override
    public boolean commit() {
      return false;
    }
  };

  /**
   * Return action corresponding to label
   */
  public static NextActionEnumerations returnNextAction(boolean commitment) {
    if (commitment) {
      return YES;
    }
    return NO;
  }

  /**
   * Return action corresponding to label
   */
  public static NextActionEnumerations returnNextAction(String commitment) {
    if (commitment.equals("YES")) {
      return YES;
    }
    return NO;
  }

  public static Map<NextActionEnumerations, Double> getActionMap(String commitment, double prob) {
    NextActionEnumerations nextAction = returnNextAction(commitment);
    if (prob == 1.0) {
      return ImmutableMap.of(nextAction, prob);
    }
    return ImmutableMap.of(nextAction, prob, nextAction.equals(YES) ? NO : YES, 1.0 - prob);
  }

}
