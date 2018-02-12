package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import aic.gas.sc.gg_bot.abstract_bot.utils.VectorNormalizer;
import aic.gas.sc.gg_bot.replay_parser.configuration.Configuration;
import aic.gas.sc.gg_bot.replay_parser.model.clustering.PairWithOccurrenceCount;
import aic.gas.sc.gg_bot.replay_parser.model.clustering.pso.Bound;
import aic.gas.sc.gg_bot.replay_parser.model.clustering.pso.Centroid;
import aic.gas.sc.gg_bot.replay_parser.model.clustering.pso.ICentroid;
import aic.gas.sc.gg_bot.replay_parser.model.clustering.pso.IFitnessFunctionEvaluationStrategy;
import aic.gas.sc.gg_bot.replay_parser.model.clustering.pso.Particle;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.State;
import aic.gas.sc.gg_bot.replay_parser.service.IStateClusteringService;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
    List<DataPoint> dataSet = createDataSet(states, normalizers);

    //get bound of search space
    List<Bound> bounds = IntStream.range(0, states.get(0).getFeatureVector().length)
        .boxed()
        .map(integer -> computeBound(dataSet, integer))
        .collect(Collectors.toList());

    //evaluation function
    double maxDistance = computeLongestPossibleDistance(bounds);
    IFitnessFunctionEvaluationStrategy functionEvaluationStrategy = new FitnessFunctionEvaluation(
        differentConsecutivePairFindingServicePairs, maxDistance);

    //initial clusters
    List<List<Vec>> clustersList = IntStream
        .range(0, configuration.getPsoParameters().getClusteringRunsForInitialization())
        .boxed()
        .map(integer -> stateClusteringService
            .computeStateRepresentatives(states, normalizers, configuration,
                differentConsecutivePairFindingServicePairs))
        .collect(Collectors.toList());

    //init particles
    List<Particle> particles = IntStream
        .range(0, configuration.getPsoParameters().getCountOfParticles())
        .boxed()
        .map(integer -> new Particle(functionEvaluationStrategy,
            clustersList.get(RANDOM.nextInt(clustersList.size())).stream()
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
      log.info("In it. " + i + " best particle with fitness " + bestParticle.getFitness());
      particles.forEach(particle -> particle
          .update(configuration.getPsoParameters(), currentBest, particles, bounds));
      bestParticle = particles.stream()
          .min(Comparator.comparingDouble(Particle::getFitness))
          .get();

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

  private static Bound computeBound(List<DataPoint> dataPoints, int index) {
    DoubleSummaryStatistics statistics = dataPoints.stream()
        .mapToDouble(value -> value.getNumericalValues().get(index))
        .summaryStatistics();
    return new Bound(statistics.getMax(), statistics.getMin());
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

  private static List<DataPoint> createDataSet(List<State> states,
      List<FeatureNormalizer> normalizers) {
    return states.stream()
        .map(State::getFeatureVector)
        .map(doubles -> VectorNormalizer.normalizeFeatureVector(doubles, normalizers))
        .map(doubles -> new DataPoint(new DenseVector(doubles)))
        .collect(Collectors.toList());
  }

  private static class FitnessFunctionEvaluation implements IFitnessFunctionEvaluationStrategy {

    private final Set<PairWithOccurrenceCount> differentConsecutivePairFindingServicePairs;
    private final double longestPossibleDistance;
    private final double sumOfPairs;

    private FitnessFunctionEvaluation(
        Set<PairWithOccurrenceCount> differentConsecutivePairFindingServicePairs,
        double longestPossibleDistance) {
      this.differentConsecutivePairFindingServicePairs = differentConsecutivePairFindingServicePairs;
      this.longestPossibleDistance = longestPossibleDistance;
      this.sumOfPairs = differentConsecutivePairFindingServicePairs.stream()
          .mapToDouble(PairWithOccurrenceCount::getOccurrence)
          .sum();
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

      //enabled cluster
      double clusters = ((double) enabledCentroids.size()) / ((double) centroids.size());

      //distances of data points in clusters
      double dataPointDistance = enabledCentroids.stream()
          .mapToDouble(centroid -> centroid.getMaximalDistance().getAsDouble())
          .max().getAsDouble();

      //distances of clusters
      double clustersDistance = enabledCentroids.stream()
          .mapToDouble(centroid -> enabledCentroids.stream()
              .mapToDouble(value -> centroid.getDistance(value.getCenter()))
              .max().getAsDouble())
          .max().getAsDouble();

      //consecutive pair are in different clusters
      double pairsInSameCluster = differentConsecutivePairFindingServicePairs.stream()
          .filter(pair -> enabledCentroids.stream()
              .anyMatch(centroid -> centroid.hasPointInCluster(pair.getFirst())
                  && centroid.hasPointInCluster(pair.getSecond())))
          .mapToDouble(PairWithOccurrenceCount::getOccurrence)
          .sum();
      double pairRatio = pairsInSameCluster / sumOfPairs;

      if (pairRatio > 0 || clustersDistance > longestPossibleDistance) {
        log.info("Here");
      }

      return clusters + (dataPointDistance / longestPossibleDistance) + (1 - (clustersDistance
          / longestPossibleDistance)) + pairRatio;
    }
  }


}
