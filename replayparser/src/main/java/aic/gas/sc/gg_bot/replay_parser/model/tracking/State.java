package aic.gas.sc.gg_bot.replay_parser.model.tracking;

import java.io.Serializable;
import lombok.Getter;

/**
 * Class describing state as vector with transition (taken action - committed - true/false) and next
 * state
 */
public class State implements Serializable {

  @Getter
  private final double[] featureVector;
  @Getter
  private final boolean committedWhenTransiting;

  public State(double[] featureVector, boolean committedWhenTransiting) {
    this.featureVector = featureVector;
    this.committedWhenTransiting = committedWhenTransiting;
  }
}
