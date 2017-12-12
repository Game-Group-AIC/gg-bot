package aic.gas.sc.gg_bot.replay_parser.service;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.State;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.Trajectory;
import java.util.List;
import jsat.linear.Vec;

/**
 * Contract for clustering service
 */
public interface StateClusteringService {

  /**
   * Returns feature normalizers
   */
  List<FeatureNormalizer> computeFeatureNormalizersBasedOnStates(List<State> states,
      int cardinality);

  /**
   * Compute states representative (do compression)
   */
  List<Vec> computeStateRepresentatives(List<Trajectory> trajectories, List<State> states,
      List<FeatureNormalizer> normalizers);

}
