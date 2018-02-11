package aic.gas.sc.gg_bot.replay_parser.service;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import aic.gas.sc.gg_bot.abstract_bot.utils.VectorNormalizer;
import aic.gas.sc.gg_bot.replay_parser.model.clustering.PairWithOccurrenceCount;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.State;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.Trajectory;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import jsat.classifiers.DataPoint;
import jsat.linear.DenseVector;

public interface IPairFindingService {

  Set<PairWithOccurrenceCount> findPairs(Stream<Trajectory> trajectories,
      List<FeatureNormalizer> normalizers);

  default DataPoint createDataPoint(State state, List<FeatureNormalizer> normalizers) {
    return new DataPoint(new DenseVector(
        VectorNormalizer.normalizeFeatureVector(state.getFeatureVector(), normalizers)));
  }

}
