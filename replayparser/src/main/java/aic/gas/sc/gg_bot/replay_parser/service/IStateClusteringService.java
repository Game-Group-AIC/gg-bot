package aic.gas.sc.gg_bot.replay_parser.service;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import aic.gas.sc.gg_bot.replay_parser.configuration.Configuration;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.State;
import java.util.List;
import jsat.linear.Vec;

/**
 * Contract for clustering service
 */
public interface IStateClusteringService {

  /**
   * Compute states representative (do compression)
   */
  List<Vec> computeStateRepresentatives(List<State> states, List<FeatureNormalizer> normalizers,
      Configuration configuration);

}
