package aic.gas.sc.gg_bot.replay_parser.model.irl_rl;

import aic.gas.sc.gg_bot.abstract_bot.utils.VectorNormalizer;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(of = {"ID"})
public class Particle implements Serializable {

  private static final Random RANDOM = new Random();

  @Getter
  private Vec particleAsVector;
  private final double[] velocity;
  private final int ID;
  private BestSolution bestSolution;
  private final IFitnessFunctionEvaluationStrategy evaluationStrategy;
  private final double lowerBound;
  private final double upperBound;

  public Particle(IParticleInitializationStrategy particleInitializationStrategy, int ID,
      double lowerBound, double upperBound, IFitnessFunctionEvaluationStrategy evaluationStrategy) {
    this.particleAsVector = particleInitializationStrategy.initializeParticleVac();
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.velocity = initVelocity(particleAsVector.length());
    this.ID = ID;
    this.evaluationStrategy = evaluationStrategy;
    updateByVelocity();
    this.bestSolution = new BestSolution(particleAsVector.clone(),
        evaluationStrategy.evaluate(normalize(particleAsVector.arrayCopy())));
  }

  public double getDistance(Particle other) {
    return VectorNormalizer.DISTANCE_FUNCTION.dist(particleAsVector, other.particleAsVector);
  }

  private double[] initVelocity(int size) {
    double[] velocity = new double[size];
    for (int i = 0; i < size; i++) {
      velocity[i] = lowerBound + (upperBound - lowerBound) * RANDOM.nextDouble();
    }
    return velocity;
  }

  public double getFitness() {
    if (bestSolution == null) {
      return Double.MIN_VALUE;
    }
    return bestSolution.fitness;
  }

  public Vec getBestSolution() {
    if (bestSolution == null) {
      return particleAsVector;
    }
    return bestSolution.particleAsVector;
  }

  public void evaluateCurrentSolution() {

    //change best solution if better
    double newFitness = evaluationStrategy.evaluate(particleAsVector.arrayCopy());
    if (newFitness > getFitness()) {
      bestSolution = new BestSolution(particleAsVector.clone(), newFitness);
    }
  }

  public void update(double learningRateLoc, double learningRateGlo, List<Particle> particles,
      int neighborhoodSize) {

    //find particles to be update by. Maximizing fitness
    Particle bestGlobal = particles.stream()
        .max(Comparator.comparing(Particle::getFitness)).orElse(this);
    Particle bestLocal = particles.stream()
        .sorted(Comparator.comparing(particle -> particle.getDistance(this)))
        .limit(neighborhoodSize)
        .max(Comparator.comparing(Particle::getFitness)).orElse(this);

    //update velocity
    for (int i = 0; i < particleAsVector.length(); i++) {
      velocity[i] =
          velocity[i] + (learningRateLoc * RANDOM.nextDouble() * (bestLocal.getBestSolution().get(i)
              - particleAsVector.get(i))) + (learningRateGlo * RANDOM.nextDouble() *
              (bestGlobal.getBestSolution().get(i) - particleAsVector.get(i)));
    }

    //update
    updateByVelocity();
  }

  private void updateByVelocity() {
    double[] newVec = new double[particleAsVector.length()];
    for (int i = 0; i < particleAsVector.length(); i++) {
      newVec[i] = particleAsVector.get(i) + velocity[i];
    }
    this.particleAsVector = new DenseVector(newVec);
  }

  @EqualsAndHashCode(of = "particleAsVector")
  private static class BestSolution {

    private final Vec particleAsVector;
    private final double fitness;

    private BestSolution(Vec particleAsVector, double fitness) {
      this.fitness = fitness;
      this.particleAsVector = new DenseVector(normalize(particleAsVector.arrayCopy()));
    }
  }

  private static double[] normalize(double[] toNorm) {
    double[] vector = new double[toNorm.length];
    double norm = computeNorm(toNorm);
    for (int i = 0; i < toNorm.length; i++) {
      vector[i] = toNorm[i] / norm;
    }
    return vector;
  }

  private static double computeNorm(double[] vector) {
    return Math.sqrt(Arrays.stream(vector).boxed()
        .mapToDouble(value -> Math.pow(value, 2))
        .sum());
  }

}
