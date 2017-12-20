package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import static aic.gas.sc.gg_bot.abstract_bot.utils.Configuration.DISTANCE_FUNCTION;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import aic.gas.sc.gg_bot.abstract_bot.utils.Configuration;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.State;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.Trajectory;
import aic.gas.sc.gg_bot.replay_parser.service.StateClusteringService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import jsat.SimpleDataSet;
import jsat.classifiers.DataPoint;
import jsat.clustering.SeedSelectionMethods;
import jsat.clustering.kmeans.GMeans;
import jsat.clustering.kmeans.HamerlyKMeans;
import jsat.clustering.kmeans.MiniBatchKMeans;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import lombok.extern.slf4j.Slf4j;

/**
 * Concrete implementation of StateClusteringService
 */
@Slf4j
public class StateClusteringServiceImpl implements StateClusteringService {

  //configuration
  private static final int sampleStates = 20000;
  private static final int batchSize = 2000;
  private static final int iterations = 250;
  private static final int clusters = 1500;

  private static final SeedSelectionMethods.SeedSelection SEED_SELECTION_METHOD = SeedSelectionMethods.SeedSelection.MEAN_QUANTILES;

  private static Collector<Trajectory, List<List<Trajectory>>, List<List<Trajectory>>> splitByBatchSize(
      int statesPerBatch) {
    final List<Trajectory> current = new ArrayList<>();
    return Collector.of(ArrayList::new,
        (lists, trajectory) -> {
          current.add(trajectory);
          if (current.stream().mapToInt(t -> t.getStates().size()).sum() > statesPerBatch) {
            lists.add(new ArrayList<>(current));
            current.clear();
          }
        },
        (l1, l2) -> {
          throw new RuntimeException("Should not run this in parallel");
        },
        l -> {
          if (current.size() != 0) {
            l.add(current);
            return l;
          }
          return new ArrayList<>();
        }
    );
  }

  @Override
  public List<FeatureNormalizer> computeFeatureNormalizersBasedOnStates(List<State> states,
      int cardinality) {
    List<FeatureNormalizer> normalizers = new ArrayList<>();
    for (int i = 0; i < cardinality; i++) {
      final int index = i;
      List<Double> possibleValues = states.stream()
          .map(State::getFeatureVector)
          .mapToDouble(doubles -> doubles[index])
          .boxed()
          .collect(Collectors.toList());
      normalizers.add(new FeatureNormalizer(possibleValues));
    }
    return normalizers;
  }

  @Override
  public List<Vec> computeStateRepresentatives(List<Trajectory> trajectories, List<State> states,
      List<FeatureNormalizer> normalizers) {
    if (states.size() > sampleStates) {
//            int clusterNumberEstimation = estimateClusters(trajectories, normalizers);
//            log.info("Estimated #" + clusterNumberEstimation + " clusters.");
      int num_clusters = clusters;

      while(num_clusters > 1) {
        try {
          MiniBatchKMeans batchKMeans = new MiniBatchKMeans(DISTANCE_FUNCTION, batchSize,
              iterations,
              SEED_SELECTION_METHOD);
          batchKMeans.cluster(createDataSet(states, normalizers), clusters);
          return batchKMeans.getMeans();
        } catch (IndexOutOfBoundsException e) {
          num_clusters /= 2;
          log.warn("decreasing cluster size to "+num_clusters);
        }
      }
    }
    return computeStateRepresentatives(states, normalizers);
  }

  private int estimateClusters(List<Trajectory> trajectories, List<FeatureNormalizer> normalizers) {
    List<Trajectory> copy = new ArrayList<>(trajectories);
    Collections.shuffle(copy);
    List<List<Trajectory>> batches = trajectories.stream()
        .collect(splitByBatchSize(sampleStates));
    Collections.shuffle(batches);
    return ((int) batches.subList(0, Math.min(4, batches.size())).stream()
        .mapToInt(value -> computeStateRepresentatives(
            value.stream().flatMap(trajectory -> trajectory.getStates().stream())
                .collect(Collectors.toList()), normalizers).size())
        .average().orElse(1000));
//        return computeStateRepresentatives(trajectories.stream().flatMap(trajectory -> trajectory.getStates().stream()).limit(sampleStates).collect(Collectors.toList()), normalizers).size();
  }

  /**
   * Create data set
   */

  private SimpleDataSet createDataSet(List<State> states, List<FeatureNormalizer> normalizers) {
    List<DataPoint> dataPoints = states.stream()
        .map(State::getFeatureVector)
        .map(doubles -> Configuration.normalizeFeatureVector(doubles, normalizers))
        .map(doubles -> new DataPoint(new DenseVector(doubles)))
        .collect(Collectors.toList());
    return new SimpleDataSet(dataPoints);
  }

  /**
   * Do clustering + cluster number estimation, returns means
   */
  private List<Vec> computeStateRepresentatives(List<State> states,
      List<FeatureNormalizer> normalizers) {
    GMeans kMeans = new GMeans(new HamerlyKMeans(DISTANCE_FUNCTION, SEED_SELECTION_METHOD));

    //do clustering
    kMeans.cluster(createDataSet(states, normalizers));

    //get means and return them
    return kMeans.getMeans();
  }

}
