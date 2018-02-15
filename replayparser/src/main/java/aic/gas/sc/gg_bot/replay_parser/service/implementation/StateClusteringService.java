package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import aic.gas.sc.gg_bot.abstract_bot.utils.VectorNormalizer;
import aic.gas.sc.gg_bot.replay_parser.configuration.Configuration;
import aic.gas.sc.gg_bot.replay_parser.model.clustering.PairWithOccurrenceCount;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.State;
import aic.gas.sc.gg_bot.replay_parser.service.IStateClusteringService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import jsat.SimpleDataSet;
import jsat.classifiers.DataPoint;
import jsat.clustering.SeedSelectionMethods;
import jsat.clustering.kmeans.MiniBatchKMeans;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import lombok.extern.slf4j.Slf4j;

/**
 * Concrete implementation of IStateClusteringService
 */
@Slf4j
public class StateClusteringService implements IStateClusteringService {

  private static final SeedSelectionMethods.SeedSelection SEED_SELECTION_METHOD = SeedSelectionMethods.SeedSelection.MEAN_QUANTILES;

  @Override
  public List<Vec> computeStateRepresentatives(List<State> states,
      List<FeatureNormalizer> normalizers, Configuration configuration,
      Set<PairWithOccurrenceCount> differentConsecutivePairFindingServicePairs) {
    MiniBatchKMeans batchKMeans = new MiniBatchKMeans(VectorNormalizer.DISTANCE_FUNCTION,
        configuration.getBatchSize(), configuration.getIterations(), SEED_SELECTION_METHOD);
    List<DataPoint> dataSet = createDataSet(states, normalizers);
    batchKMeans.cluster(new SimpleDataSet(dataSet),
        (int) Math.min(configuration.getClusters(), states.stream()
            .map(State::getFeatureVector)
            .distinct()
            .count()));
    Map<Vec, List<DataPoint>> assignment = new HashMap<>();
    List<Vec> vecs = batchKMeans.getMeans().stream()
        .distinct()
        .collect(Collectors.toList());
    dataSet.forEach(point -> {
      Vec representant = vecs.stream()
          .min(Comparator.comparingDouble(
              o -> VectorNormalizer.DISTANCE_FUNCTION.dist(o, point.getNumericalValues())))
          .get();
      assignment.computeIfAbsent(representant, vec -> new ArrayList<>()).add(point);
    });
    return new ArrayList<>(assignment.keySet());
  }


  /**
   * Create data set
   */
  private static List<DataPoint> createDataSet(List<State> states,
      List<FeatureNormalizer> normalizers) {
    return states.stream()
        .map(State::getFeatureVector)
        .map(doubles -> VectorNormalizer.normalizeFeatureVector(doubles, normalizers))
        .map(doubles -> new DataPoint(new DenseVector(doubles)))
        .collect(Collectors.toList());
  }

}
