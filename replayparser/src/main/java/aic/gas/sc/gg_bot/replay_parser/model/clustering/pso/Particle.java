package aic.gas.sc.gg_bot.replay_parser.model.clustering.pso;

import aic.gas.sc.gg_bot.abstract_bot.utils.VectorNormalizer;
import aic.gas.sc.gg_bot.replay_parser.configuration.PSOParameters;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(of = "particleAsVector")
public class Particle {

  private final List<Centroid> centroids;
  private final IFitnessFunctionEvaluationStrategy fitnessFunctionStrategy;
  private BestSolutionSoFar bestSolutionSoFar;
  private Vec particleAsVector;

  private static final Random RANDOM = new Random();

  public Particle(IFitnessFunctionEvaluationStrategy fitnessFunctionStrategy,
      List<Centroid> centroids, List<DataPointWithDistanceCache> dataSet) {
    this.centroids = centroids;
    this.fitnessFunctionStrategy = fitnessFunctionStrategy;

    //add points to clusters
    dataSet.forEach(point -> centroids.stream()
        .filter(ICentroid::isEnabled)
        .min(Comparator.comparingDouble(o -> o.getDistance(point)))
        .get().addToCluster(point));
    centroids.forEach(Centroid::nextIteration);

    this.bestSolutionSoFar = new BestSolutionSoFar(centroids.stream()
        .map(centroid -> new StaticCentroid(centroid.getCenter(), centroid.isEnabledValue(),
            centroid.hasPointsInCluster()))
        .collect(Collectors.toList()), fitnessFunctionStrategy.evaluate(centroids));
    this.particleAsVector = this.bestSolutionSoFar.particleAsVector.clone();
  }

  public List<? extends ICentroid> getBestSolution() {
    return bestSolutionSoFar.centroids;
  }

  public double getFitness() {
    return this.bestSolutionSoFar.fitness;
  }

  private static Vec createVector(List<? extends ICentroid> centroids) {
    int lengthForCentroid = centroids.get(0).getCenter().length();
    double[] vec = new double[centroids.size() * (lengthForCentroid + 1)];
    for (int i = 0; i < centroids.size(); i++) {
      for (int j = 0; j < lengthForCentroid; j++) {
        vec[(i + 1) * j] = centroids.get(i).getCenter().get(j);
      }
      //add if centroid is enabled
      vec[(i + 1) * lengthForCentroid] = centroids.get(i).isEnabledValue();
    }
    return new DenseVector(vec);
  }

  public double getDistance(Particle particle) {
    return VectorNormalizer.DISTANCE_FUNCTION.dist(particleAsVector, particle.particleAsVector);
  }

  public void update(PSOParameters parameters, Particle globalBest, List<Particle> allParticles,
      List<Bound> bounds) {

    Particle localBest = allParticles.stream()
        .sorted(Comparator.comparingDouble(this::getDistance))
        .limit(parameters.getNeighborhoodSize())
        //minimize fitness
        .min(Comparator.comparingDouble(Particle::getFitness))
        .get();
    for (int i = 0; i < centroids.size(); i++) {
      centroids.get(i).update(parameters.getLearningRateLoc(), RANDOM.nextDouble(),
          localBest.bestSolutionSoFar.centroids.get(i), parameters.getLearningRateGlo(),
          RANDOM.nextDouble(), globalBest.bestSolutionSoFar.centroids.get(i), bounds);
    }

    //make new assignment
    List<Centroid> enabledCentroids = centroids.stream()
        .filter(ICentroid::isEnabled)
        .collect(Collectors.toList());
    centroids.forEach(centroid -> centroid.movePointsToDifferentCluster(enabledCentroids));
    centroids.forEach(Centroid::nextIteration);

    //recompute fitness
    double fitness = fitnessFunctionStrategy.evaluate(enabledCentroids);

    //do we have new local best?
    if (fitness < bestSolutionSoFar.fitness) {
      this.bestSolutionSoFar = new BestSolutionSoFar(centroids.stream()
          .map(centroid -> new StaticCentroid(centroid.getCenter(), centroid.isEnabledValue(),
              centroid.hasPointsInCluster()))
          .collect(Collectors.toList()), fitness);
      this.particleAsVector = this.bestSolutionSoFar.particleAsVector.clone();
    } else {
      this.particleAsVector = createVector(centroids);
    }
  }

  @EqualsAndHashCode(of = {"centroids", "fitness", "particleAsVector"})
  private static class BestSolutionSoFar {

    private final List<StaticCentroid> centroids;
    private final double fitness;

    @Getter
    private final Vec particleAsVector;


    private BestSolutionSoFar(List<StaticCentroid> centroids, double fitness) {
      this.centroids = centroids;
      this.fitness = fitness;
      this.particleAsVector = createVector(centroids);
    }
  }

  @EqualsAndHashCode(of = {"center", "isEnabled", "hasPointsInCluster"})
  @AllArgsConstructor
  private static class StaticCentroid implements ICentroid {

    private final Vec center;
    private final double isEnabled;
    private final boolean hasPointsInCluster;

    @Override
    public Vec getCenter() {
      return center;
    }

    @Override
    public double isEnabledValue() {
      return isEnabled;
    }

    @Override
    public boolean hasPointsInCluster() {
      return hasPointsInCluster;
    }
  }

}
