package aic.gas.sc.gg_bot.abstract_bot.model.decision;

import burlap.mdp.core.action.Action;
import java.io.Serializable;

/**
 * Enumeration of all possible commitments based on policy
 */
public enum NextActionEnumerations implements Serializable, NextActionStrategy, Action {
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

  @Override
  public String actionName() {
    return this.name();
  }

  @Override
  public Action copy() {
    return this;
  }
}
