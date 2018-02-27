package aic.gas.sc.gg_bot.replay_parser.model.irl_rl;

import aic.gas.sc.gg_bot.replay_parser.configuration.Configuration;
import aic.gas.sc.gg_bot.replay_parser.model.irl.KFoldBatchIterator;
import burlap.behavior.functionapproximation.DifferentiableStateActionValue;
import burlap.behavior.functionapproximation.dense.ConcatenatedObjectFeatures;
import burlap.behavior.functionapproximation.dense.NumericVariableFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TileCodingFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TilingArrangement;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.Episode;
import burlap.domain.singleagent.lunarlander.LunarLanderDomain;
import burlap.domain.singleagent.lunarlander.state.LLAgent;
import burlap.domain.singleagent.lunarlander.state.LLBlock;
import burlap.domain.singleagent.lunarlander.state.LLState;
import burlap.mdp.singleagent.oo.OOSADomain;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import jsat.linear.DenseVector;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OurMLIRL {

  private static final Random RANDOM = new Random();

  public static double[][] learnReward(Configuration configuration,
      KFoldBatchIterator batchIterator,
      int numberOfFeatures, int indeterminateOfPolynomial) {
    long start = System.currentTimeMillis();

    IParticleInitializationStrategy particleInitializationStrategy = () ->
        new DenseVector(IntStream.range(0, numberOfFeatures * indeterminateOfPolynomial).boxed()
            .mapToDouble(integer -> getRandomRewardInInterval(configuration))
            .toArray());

    List<Particle> particles = IntStream
        .range(0, configuration.getPsoParameters().getCountOfParticles())
        .boxed()
        .parallel()
        .map(integer -> new Particle(particleInitializationStrategy, integer,
            configuration.getMinReward(), configuration.getMaxReward(),
            new EvaluationStrategy(batchIterator, numberOfFeatures, indeterminateOfPolynomial)))
        .collect(Collectors.toList());

    //initial solution
    Optional<Particle> bestSolution = particles.stream()
        .max(Comparator.comparing(Particle::getFitness));
    if (!bestSolution.isPresent()) {
      throw new IllegalArgumentException("No particle found!");
    }
    log.info("RF: " + bestSolution.get().getBestSolution().toString());
    log.info("Log likelihood: " + bestSolution.get().getFitness());

    int lastChange = 0;
    for (int i = 0; i < configuration.getSteps(); i++) {

      //upgrade and evaluate particles
      particles.forEach(particle -> particle
          .update(configuration.getPsoParameters().getLearningRateLoc(),
              configuration.getPsoParameters().getLearningRateGlo(), particles,
              configuration.getPsoParameters().getNeighborhoodSize()));
      particles.parallelStream().forEach(Particle::evaluateCurrentSolution);

      Optional<Particle> newBestSolution = particles.stream()
          .max(Comparator.comparing(Particle::getFitness));
      if (!newBestSolution.get().equals(bestSolution.get())) {
        log.info(i + ": Previous Best Log likelihood: " + bestSolution.get().getFitness()
            + ". New Best: " + newBestSolution.get().getFitness() + " (improvment: " + Math
            .abs(bestSolution.get().getFitness() - newBestSolution.get().getFitness()) + ")");
        bestSolution = newBestSolution;
        log.info("RF: " + bestSolution.get().getBestSolution().toString());
        lastChange = i;
      }

      if (System.currentTimeMillis() - start > configuration.getTimeBudget()
          || i - lastChange >= configuration.getIrlNoChangeStopCondition()) {
        break;
      }
      log.info("Iteration " + i + " finished.");
      batchIterator.next();
    }
    log.info("RF: " + bestSolution.get().getBestSolution().toString());
    return convertToTwoDimensionalArray(bestSolution.get().getBestSolution().arrayCopy(),
        numberOfFeatures, indeterminateOfPolynomial);
  }

  /**
   * Computes and returns the log-likelihood of all expert trajectories under the current reward function parameters.
   *
   * @return the log-likelihood of all expert trajectories under the current reward function parameters.
   */
  private static double logLikelihood(KFoldBatchIterator batchIterator,
      double[][] rewardCoefficients, OurGradientDescentSarsaLam<OurRewardFunction> planner) {

    try {
      //change reward to environment
      planner.getRf().updateCoefficients(rewardCoefficients);
      planner.resetSolver();

      //learn policy
      batchIterator.trainingData()
          .forEach(episode -> planner.learnFromEpisode(episode, planner.getRf()));

      return batchIterator.testingData()
          .mapToDouble(episode -> logLikelihoodOfTrajectory(episode, planner.getCurrentPolicy()))
          .sum();
    } finally {
      planner.resetSolver();
    }
  }

  private static double logLikelihoodOfTrajectory(Episode ep, Policy policy) {
    double logLike = 0.0D;
    for (int i = 0; i < ep.numTimeSteps() - 1; i++) {

      //prevent by playing action with small ppt
      double actProb = Math.max(policy.actionProb(ep.state(i), ep.action(i)), Math.exp(-100));
      logLike += Math.log(actProb);
    }
    return logLike;
  }

  private static double getRandomRewardInInterval(Configuration configuration) {
    return configuration.getMinReward()
        + (configuration.getMaxReward() - configuration.getMinReward()) * RANDOM.nextDouble();
  }

  //TODO get rid of dependencies with model + move parameters
  private static class EvaluationStrategy implements IFitnessFunctionEvaluationStrategy {

    private final OurGradientDescentSarsaLam<OurRewardFunction> planner;
    private final KFoldBatchIterator batchIterator;
    private final int numberOfFeatures;
    private final int indeterminateOfPolynomial;

    public EvaluationStrategy(KFoldBatchIterator batchIterator, int numberOfFeatures,
        int indeterminateOfPolynomial) {
      this.batchIterator = batchIterator;
      this.numberOfFeatures = numberOfFeatures;
      this.indeterminateOfPolynomial = indeterminateOfPolynomial;

      //create a reward function that is linear with respect to those features and has small random
      //parameter values to start
      OurRewardFunction rf = new OurRewardFunction(numberOfFeatures, indeterminateOfPolynomial);

      LunarLanderDomain lld = new LunarLanderDomain();
      lld.setRf(rf);
      OOSADomain domain = lld.generateDomain();

      LLState s = new LLState(new LLAgent(5, 0, 0), new LLBlock.LLPad(75, 95, 0, 10, "pad"));

      ConcatenatedObjectFeatures inputFeatures = new ConcatenatedObjectFeatures()
          .addObjectVectorizion(LunarLanderDomain.CLASS_AGENT, new NumericVariableFeatures());

      int nTilings = 5;
      double resolution = 10.;

      double xWidth = (lld.getXmax() - lld.getXmin()) / resolution;
      double yWidth = (lld.getYmax() - lld.getYmin()) / resolution;
      double velocityWidth = 2 * lld.getVmax() / resolution;
      double angleWidth = 2 * lld.getAngmax() / resolution;

      TileCodingFeatures tilecoding = new TileCodingFeatures(inputFeatures);
      tilecoding.addTilingsForAllDimensionsWithWidths(
          new double[]{xWidth, yWidth, velocityWidth, velocityWidth, angleWidth}, nTilings,
          TilingArrangement.RANDOM_JITTER);

      double defaultQ = 0.5;
      DifferentiableStateActionValue vfa = tilecoding.generateVFA(defaultQ / nTilings);
      this.planner = new OurGradientDescentSarsaLam<>(domain, 0.99, 5, vfa, 0.02, 0.5, 500, rf);
    }

    @Override
    public double evaluate(double[] vector) {
      return logLikelihood(batchIterator,
          convertToTwoDimensionalArray(vector, numberOfFeatures, indeterminateOfPolynomial),
          planner);
    }
  }

  private static double[][] convertToTwoDimensionalArray(double[] vector, int length, int width) {
    double[][] newArray = new double[length][width];
    for (int i = 0; i < length; i++) {
      for (int j = 0; j < width; j++) {
        newArray[i][j] = vector[(i + 1) * j];
      }
    }
    return newArray;
  }

}
