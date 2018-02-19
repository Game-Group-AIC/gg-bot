package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import aic.gas.sc.gg_bot.abstract_bot.utils.VectorNormalizer;
import aic.gas.sc.gg_bot.replay_parser.configuration.Configuration;
import aic.gas.sc.gg_bot.replay_parser.model.clustering.PairWithOccurrenceCount;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.State;
import aic.gas.sc.gg_bot.replay_parser.service.IStateClusteringService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jsat.linear.DenseVector;
import jsat.linear.Vec;

public class FullStateClusteringService implements IStateClusteringService {

  @Override
  public List<Vec> computeStateRepresentatives(List<State> states,
      List<FeatureNormalizer> normalizers, Configuration configuration,
      Set<PairWithOccurrenceCount> differentConsecutivePairFindingServicePairs) {
    return keepUniquePoints(states, normalizers);
  }

  private static List<Vec> keepUniquePoints(List<State> states,
      List<FeatureNormalizer> normalizers) {
    return states.stream()
        .map(State::getFeatureVector)
        .map(doubles -> VectorNormalizer.normalizeFeatureVector(doubles, normalizers))
        .map(DenseVector::new)
        .distinct()
        .collect(Collectors.toList());
  }

}
