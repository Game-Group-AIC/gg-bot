package aic.gas.sc.gg_bot.replay_parser.model.clustering.pso;

import jsat.linear.Vec;

public interface ICentroid {

  Vec getCenter();

  double isEnabledValue();

  default boolean isEnabled() {
    return isEnabledValue() >= 0.5;
  }

  boolean hasPointsInCluster();

}
