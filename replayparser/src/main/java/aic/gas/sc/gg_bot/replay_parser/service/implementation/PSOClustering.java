package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import aic.gas.sc.gg_bot.abstract_bot.utils.VectorNormalizer;
import aic.gas.sc.gg_bot.replay_parser.configuration.Configuration;
import aic.gas.sc.gg_bot.replay_parser.model.clustering.PairWithOccurrenceCount;
import aic.gas.sc.gg_bot.replay_parser.model.clustering.pso.Bound;
import aic.gas.sc.gg_bot.replay_parser.model.clustering.pso.Centroid;
import aic.gas.sc.gg_bot.replay_parser.model.clustering.pso.DataPointWithDistanceCache;
import aic.gas.sc.gg_bot.replay_parser.model.clustering.pso.ICentroid;
import aic.gas.sc.gg_bot.replay_parser.model.clustering.pso.IFitnessFunctionEvaluationStrategy;
import aic.gas.sc.gg_bot.replay_parser.model.clustering.pso.Particle;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.State;
import aic.gas.sc.gg_bot.replay_parser.service.IStateClusteringService;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import jsat.classifiers.DataPoint;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PSOClustering implements IStateClusteringService {

  private static final Random RANDOM = new Random();
  private final IStateClusteringService stateClusteringService = new StateClusteringService();

  @Override
  public List<Vec> computeStateRepresentatives(List<State> states,
      List<FeatureNormalizer> normalizers, Configuration configuration,
      Set<PairWithOccurrenceCount> differentConsecutivePairFindingServicePairs) {

    //create data set
    List<DataPointWithDistanceCache> dataSet = createDataSet(states, normalizers);

    //get bound of search space
    List<Bound> bounds = IntStream.range(0, states.get(0).getFeatureVector().length)
        .boxed()
        .map(integer -> computeBound(dataSet, integer))
        .collect(Collectors.toList());

    //evaluation function
    double maxDistanceInSpace = computeLongestPossibleDistance(bounds);
    double maxDistanceBetweenPoints = maxDistanceBetweenPoints(dataSet);
    IFitnessFunctionEvaluationStrategy functionEvaluationStrategy = new FitnessFunctionEvaluation(
        differentConsecutivePairFindingServicePairs, maxDistanceInSpace, maxDistanceBetweenPoints,
        configuration.getClusters());

    //initial clusters
    List<Vec> clustersList = stateClusteringService
        .computeStateRepresentatives(states, normalizers, configuration,
            differentConsecutivePairFindingServicePairs);

    //init particles
    List<Particle> particles = IntStream
        .range(0, configuration.getPsoParameters().getCountOfParticles())
        .boxed()
        .map(integer -> new Particle(functionEvaluationStrategy, clustersList.stream()
            .map(Vec::clone)
            .map(vec -> new Centroid(vec, createVelocity(bounds), bounds))
            .collect(Collectors.toList()), dataSet))
        .collect(Collectors.toList());

    Particle bestParticle = particles.stream()
        .min(Comparator.comparingDouble(Particle::getFitness))
        .get();
    double bestFitness = bestParticle.getFitness();
    int noChange = 0;

    //main loop
    for (int i = 0; i < configuration.getIterations(); i++) {
      Particle currentBest = bestParticle;
      particles.forEach(particle -> particle
          .update(configuration.getPsoParameters(), currentBest, particles, bounds));
      bestParticle = particles.stream()
          .min(Comparator.comparingDouble(Particle::getFitness))
          .get();

      if (!currentBest.equals(bestParticle)) {
        log.info("In it. " + i + " best particle with fitness " + bestParticle.getFitness());
      }

      //termination condition
      if (bestFitness == bestParticle.getFitness()) {
        noChange++;
        if (noChange >= configuration.getPsoParameters().getNoImprovmentTerminationCondition()) {
          break;
        }
      } else {
        bestFitness = bestParticle.getFitness();
        noChange = 0;
      }
    }

    return bestParticle.getBestSolution().stream()
        .filter(ICentroid::isEnabled)
        .filter(ICentroid::hasPointsInCluster)
        .map(ICentroid::getCenter)
        .collect(Collectors.toList());
  }

  private double computeLongestPossibleDistance(List<Bound> bounds) {
    double[] first = new double[bounds.size()], second = new double[bounds.size()];
    for (int i = 0; i < bounds.size(); i++) {
      first[i] = bounds.get(i).getLowerBound();
      second[i] = bounds.get(i).getUpperBound();
    }
    return VectorNormalizer.DISTANCE_FUNCTION.dist(new DenseVector(first), new DenseVector(second));
  }

  private static Bound computeBound(List<DataPointWithDistanceCache> dataPoints, int index) {
    DoubleSummaryStatistics statistics = dataPoints.stream()
        .mapToDouble(value -> value.getDataPoint().getNumericalValues().get(index))
        .summaryStatistics();
    return new Bound(statistics.getMax(), statistics.getMin());
  }

  private static double maxDistanceBetweenPoints(List<DataPointWithDistanceCache> dataPoints) {
    List<DataPoint> points = dataPoints.stream()
        .map(DataPointWithDistanceCache::getDataPoint)
        .distinct()
        .collect(Collectors.toList());
    return IntStream.range(0, points.size())
        .boxed()
        .map(integer -> IntStream.range(integer + 1, points.size())
            .boxed()
            .mapToDouble(value -> VectorNormalizer.DISTANCE_FUNCTION
                .dist(points.get(value).getNumericalValues(),
                    points.get(integer).getNumericalValues()))
            .max())
        .filter(OptionalDouble::isPresent)
        .mapToDouble(OptionalDouble::getAsDouble)
        .max().getAsDouble();
  }

  private static double[] createVelocity(List<Bound> bounds) {
    double[] velocity = new double[bounds.size() + 1];
    for (int i = 0; i < bounds.size(); i++) {
      velocity[i] = bounds.get(i).getLowerBound()
          + (bounds.get(i).getUpperBound() - bounds.get(i).getLowerBound()) * RANDOM.nextDouble();
    }
    velocity[bounds.size()] = RANDOM.nextDouble();
    return velocity;
  }

  private static List<DataPointWithDistanceCache> createDataSet(List<State> states,
      List<FeatureNormalizer> normalizers) {
    return states.stream()
        .map(State::getFeatureVector)
        .map(doubles -> VectorNormalizer.normalizeFeatureVector(doubles, normalizers))
        .map(DenseVector::new)
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
        .entrySet().stream()
        .map(entry -> new DataPointWithDistanceCache(new DataPoint(entry.getKey()),
            entry.getValue().intValue()))
        .collect(Collectors.toList());
  }

  private static class FitnessFunctionEvaluation implements IFitnessFunctionEvaluationStrategy {

    private final Set<PairWithOccurrenceCount> differentConsecutivePairFindingServicePairs;
    private final double[] wights;

    private FitnessFunctionEvaluation(Set<PairWithOccurrenceCount> pairWithOccurrenceCounts,
        double longestPossibleDistanceForClusters, double longestPossibleDistanceForPoints,
        int maxClusters) {
      this.differentConsecutivePairFindingServicePairs = pairWithOccurrenceCounts;

      //compute wights to get everything to interval 0 - 1
      double[] values = new double[]{maxClusters, longestPossibleDistanceForPoints,
          longestPossibleDistanceForPoints, longestPossibleDistanceForClusters,
          longestPossibleDistanceForPoints, longestPossibleDistanceForPoints,
          differentConsecutivePairFindingServicePairs.stream()
              .mapToDouble(PairWithOccurrenceCount::getOccurrence)
              .sum(), 1, 1};
      double max = Arrays.stream(values).max().getAsDouble();
      this.wights = weightsToScale(values, max);
    }

    @Override
    public double evaluate(List<Centroid> centroids) {

      List<Centroid> enabledCentroids = centroids.stream()
          .filter(ICentroid::isEnabled)
          .filter(Centroid::hasPointsInCluster)
          .collect(Collectors.toList());

      if (enabledCentroids.isEmpty()) {
        log.error("Evaluating bad particle - no centroids enabled.");
        return Double.MAX_VALUE;
      }

      //enabled cluster - maximize - preserve as many meaningfull clusters we can get
      double countOfClusters = 1.0 - (((double) enabledCentroids.size()) * wights[0]);

      //distances of data points in clusters - max - minimize - cluster is good representative of points
      double dataPointsToClusterDistancesMax = enabledCentroids.stream()
          .mapToDouble(centroid -> centroid.getMaximalDistanceToCenter().orElse(0.0))
          .max().getAsDouble() *
          enabledCentroids.stream()
              .mapToDouble(centroid -> centroid.getMaximalDistanceToCenter().orElse(0.0))
              .average().getAsDouble() * wights[1] * wights[1];

      //distances of data points in clusters - avg - minimize - cluster is good representative of points
      double dataPointsToClusterDistances = enabledCentroids.stream()
          .mapToDouble(centroid -> centroid.getAverageDistanceToCenter().orElse(0.0))
          .max().getAsDouble() *
          enabledCentroids.stream()
              .mapToDouble(centroid -> centroid.getAverageDistanceToCenter().orElse(0.0))
              .average().getAsDouble() * wights[2] * wights[2];

      //distances of clusters - maximize - is easy to distinguish between them
      double clustersDistances = 1.0 - (getAverageDistanceBetweenClusters(enabledCentroids) *
          getMaximalDistanceBetweenClusters(enabledCentroids) * wights[3] * wights[3]);

//      //distance of points in cluster between each other - max - minimize - are as much similar as possible
//      double dataPointsDistancesMax = enabledCentroids.stream()
//          .mapToDouble(centroid -> centroid.getMaximalDistanceBetweenPoints().orElse(0.0))
//          .max().getAsDouble() *
//          enabledCentroids.stream()
//              .mapToDouble(centroid -> centroid.getMaximalDistanceBetweenPoints().orElse(0.0))
//              .average().getAsDouble() * wights[4] * wights[4];
//
//      //distance of points in cluster between each other - max - minimize - are as much similar as possible
//      double dataPointsDistancesAvg = enabledCentroids.stream()
//          .mapToDouble(centroid -> centroid.getAverageDistanceBetweenPoints().orElse(0.0))
//          .max().getAsDouble() *
//          enabledCentroids.stream()
//              .mapToDouble(centroid -> centroid.getAverageDistanceBetweenPoints().orElse(0.0))
//              .average().getAsDouble() * wights[5] * wights[5];

      //consecutive pair (with different states) are in different clusters - not to loose information - result of action should be different - minimize
      double pairsInSameCluster = differentConsecutivePairFindingServicePairs.stream()
          .filter(pair -> enabledCentroids.stream()
              .anyMatch(centroid -> centroid.hasPointInCluster(pair.getFirst().getNumericalValues())
                  && centroid.hasPointInCluster(pair.getSecond().getNumericalValues())))
          .mapToDouble(PairWithOccurrenceCount::getOccurrence)
          .sum() * wights[6];

      //structure of current model
      Map<Centroid, Map<Boolean, Map<Centroid, Long>>> compressedModel = new HashMap<>();

      //this is really great showcase of ineffectivity :D
      differentConsecutivePairFindingServicePairs.forEach(pair -> {
        Centroid firstC = enabledCentroids.stream()
            .filter(centroid -> centroid.hasPointInCluster(pair.getFirst().getNumericalValues()))
            .findAny().get();
        Centroid secondC = enabledCentroids.stream()
            .filter(centroid -> centroid.hasPointInCluster(pair.getSecond().getNumericalValues()))
            .findAny().get();
        Map<Boolean, Map<Centroid, Long>> mapTransition = compressedModel
            .computeIfAbsent(firstC, centroid -> new HashMap<>());
        Map<Centroid, Long> otherState = mapTransition
            .computeIfAbsent(pair.isCommittedWhenTransiting(), aBoolean -> new HashMap<>());
        Long count = otherState.get(secondC);
        if (count == null) {
          count = (long) pair.getOccurrence();
        } else {
          count = count + pair.getOccurrence();
        }
        otherState.put(secondC, count);
      });

      //transition after decision leads to minimal amount of clusters - ratio - (best case / to all cases) - maximize - to be as much deterministic as possible
      List<Double> transitionsRatios = compressedModel.values().stream()
          .flatMap(bm -> bm.values().stream()
              .map(Map::values)
              .map(this::getRatio))
          .collect(Collectors.toList());
      double transitionRatio = 1.0 - (transitionsRatios.stream()
          .mapToDouble(value -> value)
          .average().getAsDouble() * transitionsRatios.stream()
          .mapToDouble(value -> value)
          .max().getAsDouble() * wights[7]);
//
      //minimize ratio - where different decision leads to different clusters, common vs all - to be able to distinguish between results of actions
      List<Double> actionsRatios = compressedModel.values().stream()
          .filter(bm -> bm.size() > 1)
          .map(value -> getRatio(value.get(true), value.get(false)))
          .collect(Collectors.toList());
      double actionsRatio = actionsRatios.stream()
          .mapToDouble(value -> value)
          .average().orElse(1.0) * actionsRatios.stream()
          .mapToDouble(value -> value)
          .max().orElse(1.0) * wights[8];

//      double[] values = new double[]{countOfClusters, dataPointsToClusterDistancesMax,
//          dataPointsToClusterDistances, clustersDistances, dataPointsDistancesMax,
//          dataPointsDistancesAvg, pairsInSameCluster, transitionRatio, actionsRatio};
//      return Arrays.stream(values).average().getAsDouble() * Arrays.stream(values).max()
//          .getAsDouble();
      return Stream.of(dataPointsToClusterDistancesMax, countOfClusters, transitionRatio,
          dataPointsToClusterDistances, clustersDistances, actionsRatio)
          .mapToDouble(value -> value)
          .sum();
    }

    private static double[] weightsToScale(double[] maximalValues, double maximum) {
      double[] weights = new double[maximalValues.length];
      for (int i = 0; i < maximalValues.length; i++) {
        weights[i] = (maximum / maximalValues[i]) / maximum;
      }
      return weights;
    }

    private double getRatio(Map<Centroid, Long> firstDecisionType,
        Map<Centroid, Long> secondDecisionType) {
      double sumOfDifferentDecisionsLeadingToSameCluster = firstDecisionType.entrySet().stream()
          .filter(entry -> secondDecisionType.containsKey(entry.getKey()))
          .mapToDouble(entry -> entry.getValue() + secondDecisionType.get(entry.getKey()))
          .sum();
      return sumOfDifferentDecisionsLeadingToSameCluster / Stream
          .concat(firstDecisionType.values().stream(),
              secondDecisionType.values().stream())
          .mapToDouble(value -> value).sum();
    }

    private double getRatio(Collection<Long> values) {
      return values.stream()
          .mapToDouble(value -> value)
          .max().getAsDouble() / values.stream()
          .mapToDouble(value -> value)
          .sum();
    }

    public double getAverageDistanceBetweenClusters(List<Centroid> enabledCentroids) {
      return enabledCentroids.stream()
          .flatMap(value -> enabledCentroids.stream()
              .filter(centroid -> !centroid.equals(value))
              .map(centroid -> value.getDistance(centroid.getCenter())))
          .mapToDouble(value -> value)
          .average().getAsDouble();
    }

    public double getMaximalDistanceBetweenClusters(List<Centroid> enabledCentroids) {
      return enabledCentroids.stream()
          .flatMap(value -> enabledCentroids.stream()
              .filter(centroid -> !centroid.equals(value))
              .map(centroid -> value.getDistance(centroid.getCenter())))
          .mapToDouble(value -> value)
          .max().getAsDouble();
    }

  }


}
