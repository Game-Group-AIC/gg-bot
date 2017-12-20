package aic.gas.sc.gg_bot.abstract_bot.model.decision;

import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Enumeration of all possible commitments based on policy
 */
@Slf4j
public enum NextActionEnumerations implements Serializable, NextActionStrategy {
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

  //TODO nasty hack
  public static Map<NextActionEnumerations, Double> getActionMap(String commitment,
      double probability) {
    NextActionEnumerations nextAction = returnNextAction(commitment);
    return ImmutableMap
        .of(nextAction, probability, nextAction.equals(YES) ? NO : YES, 1.0 - probability);
  }

}
