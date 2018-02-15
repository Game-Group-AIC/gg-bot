package aic.gas.sc.gg_bot.replay_parser.model.irl;

import static aic.gas.sc.gg_bot.replay_parser.model.irl.DecisionDomainGenerator.getRandomRewardInInterval;

import aic.gas.sc.gg_bot.replay_parser.configuration.Configuration;
import burlap.behavior.functionapproximation.FunctionGradient;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.learnfromdemo.CustomRewardModel;
import burlap.behavior.singleagent.learnfromdemo.mlirl.MLIRL;
import burlap.behavior.singleagent.learnfromdemo.mlirl.MLIRLRequest;
import burlap.behavior.singleagent.learnfromdemo.mlirl.support.DifferentiableRF;
import burlap.behavior.valuefunction.QProvider;
import burlap.datastructures.HashedAggregator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;

/**
 * Extension of MLIRL to log run
 */
@Slf4j
public class OurMLIRL extends MLIRL {

  private final int minReward, maxReward, multiplierOfRewardForDeadEnd;
  private final long start = System.currentTimeMillis(), timeBudget;
  private final BatchIterator batchIterator;
  private final Configuration configuration;
  private double currentLearningRate;

  public OurMLIRL(MLIRLRequest request, Configuration configuration, BatchIterator batchIterator) {
    super(request, configuration.getInitialLearningRate(), configuration.getMaxLikelihoodChange(),
        configuration.getSteps());
    this.minReward = configuration.getMinReward();
    this.maxReward = configuration.getMaxReward();
    this.timeBudget = configuration.getTimeBudget();
    this.batchIterator = batchIterator;
    this.configuration = configuration;

    //init learning rate
    this.currentLearningRate = configuration.getInitialLearningRate();

    //scales dead end relatively to all bad states
    this.multiplierOfRewardForDeadEnd = configuration.getMultiplierOfRewardForDeadEnd();
  }

  /**
   * Runs gradient ascent. Copy of original method with debug and guard for boundaries
   */
  public void performIRL() {

    DifferentiableRF rf = this.request.getRf();

    //reset valueFunction
    this.request.getPlanner().resetSolver();
    this.request.getPlanner().setModel(new CustomRewardModel(request.getDomain().getModel(), rf));
    double lastLikelihood = this.logLikelihood();
//    log.info("RF: " + this.request.getRf().toString());
//    log.info("Log likelihood: " + lastLikelihood);

    Map<Integer, Double> nextValuesOfParameters = new HashMap<>(), bestValuesOfParameters = new HashMap<>();
    double bestLikelihood = lastLikelihood;
    int bestIt = -1;

    int i;
    for (i = 0; i < this.maxSteps; i++) {
      nextValuesOfParameters.clear();

      //move up gradient
      double maxValue = -Double.MAX_VALUE, minValue = Double.MAX_VALUE;
      FunctionGradient gradient = this.logLikelihoodGradient();
      for (FunctionGradient.PartialDerivative pd : gradient.getNonZeroPartialDerivatives()) {
        double curVal = rf.getParameter(pd.parameterId);
        double nexVal = curVal + this.currentLearningRate * pd.value;

        //on strange number
        if (Double.isNaN(nexVal)) {
          nexVal = getRandomRewardInInterval(configuration);
          log.error("Replacing reward in " + pd.parameterId);
        }

        if (pd.parameterId != rf.numParameters() - 1) {
          if (nexVal > maxValue) {
            maxValue = nexVal;
          }
          if (nexVal < minValue) {
            minValue = nexVal;
          }
        }

        nextValuesOfParameters.put(pd.parameterId, nexVal);
      }

      //scale and compute change
      double maxChange = 0.;
      for (Entry<Integer, Double> entry : nextValuesOfParameters.entrySet()) {
        double curVal = rf.getParameter(entry.getKey());
        //always set dummy state to lowest reward
        double nexVal =
            (entry.getKey() == rf.numParameters() - 1) ? minReward * multiplierOfRewardForDeadEnd
                : scale(minReward, maxReward, minValue, maxValue, entry.getValue());
        rf.setParameter(entry.getKey(),
            Double.isNaN(nexVal) ? getRandomRewardInInterval(configuration) : nexVal);
        double delta = Math.abs(curVal - nexVal);
        maxChange = Math.max(maxChange, delta);
      }

      //reset valueFunction
      this.request.getPlanner().resetSolver();
      this.request.getPlanner().setModel(new CustomRewardModel(request.getDomain().getModel(), rf));

      double newLikelihood = this.logLikelihood();
      double likelihoodChange = newLikelihood - lastLikelihood;
      lastLikelihood = newLikelihood;

      if (newLikelihood > bestLikelihood) {
        log.info(i + ": Previous Best Log likelihood: " + bestLikelihood + ". New Best: "
            + newLikelihood + " (improvment: " + Math.abs(bestLikelihood - newLikelihood) + ")");
        bestLikelihood = newLikelihood;
        bestValuesOfParameters.clear();
        for (int j = 0; j < rf.numParameters(); j++) {
          bestValuesOfParameters.put(j, rf.getParameter(j));
        }
        bestIt = i;
      }

      //step decay on learning rate
      if (!configuration.isStaticLearningRate() && i != 0
          && i % configuration.getDropAfterIterations() == 0) {
        this.currentLearningRate = this.currentLearningRate * configuration.getDropLearningRate();

        //change to best one
        bestValuesOfParameters
            .put(rf.numParameters() - 1, (double) (minReward * multiplierOfRewardForDeadEnd));
        for (Entry<Integer, Double> entry : bestValuesOfParameters.entrySet()) {
          rf.setParameter(entry.getKey(), entry.getValue());
        }
      }

      if ((Math.abs(likelihoodChange) < this.maxLikelihoodChange
          && lastLikelihood == bestLikelihood) || Double.isNaN(likelihoodChange)
          || System.currentTimeMillis() - start > timeBudget
          || configuration.getIrlNoChangeStopCondition() <= i - bestIt) {
        i++;
        break;
      }
    }

    //change to best one
    bestValuesOfParameters
        .put(rf.numParameters() - 1, (double) (minReward * multiplierOfRewardForDeadEnd));
    for (Entry<Integer, Double> entry : bestValuesOfParameters.entrySet()) {
      rf.setParameter(entry.getKey(), entry.getValue());
    }

    log.info("\nNum gradient ascent steps: " + i + " using reward from iteration: " + bestIt
        + " with Log likelihood: " + bestLikelihood);
    log.info("RF: " + this.request.getRf().toString());
  }


  /**
   * Computes and returns the log-likelihood of all expert trajectories under the current reward function parameters.
   *
   * @return the log-likelihood of all expert trajectories under the current reward function parameters.
   */
  public double logLikelihood() {
    double[] weights = this.request.getEpisodeWeights();
    List<Episode> exampleTrajectories = batchIterator.sampleBatchFromEpisodes();

    double sum = 0.;
    for (int i = 0; i < exampleTrajectories.size(); i++) {
      sum += this.logLikelihoodOfTrajectory(exampleTrajectories.get(i), weights[i]);
    }
    return sum;

  }

  public double logLikelihoodOfTrajectory(Episode ea, double weight) {
    double logLike = 0.0D;
    Policy p = new OurBoltzmannQPolicy((QProvider) this.request.getPlanner(),
        1.0D / this.request.getBoltzmannBeta(), configuration.getNoiseForLearningReward());

    for (int i = 0; i < ea.numTimeSteps() - 1; ++i) {
      this.request.getPlanner().planFromState(ea.state(i));
      double actProb = p.actionProb(ea.state(i), ea.action(i));
      logLike += Math.log(actProb);
    }

    logLike *= weight;
    return logLike;
  }

  /**
   * Computes and returns the gradient of the log-likelihood of all trajectories
   *
   * @return the gradient of the log-likelihood of all trajectories
   */
  public FunctionGradient logLikelihoodGradient() {
    HashedAggregator<Integer> gradientSum = new HashedAggregator<>();

    double[] weights = this.request.getEpisodeWeights();
    List<Episode> exampleTrajectories = batchIterator.sampleBatchFromEpisodes();

    for (int i = 0; i < exampleTrajectories.size(); i++) {
      Episode ea = exampleTrajectories.get(i);
      double weight = weights[i];
      for (int t = 0; t < ea.numTimeSteps() - 1; t++) {
        this.request.getPlanner().planFromState(ea.state(t));
        FunctionGradient policyGrad = this.logPolicyGrad(ea.state(t), ea.action(t));
        //weigh it by trajectory strength
        for (FunctionGradient.PartialDerivative pd : policyGrad.getNonZeroPartialDerivatives()) {
          double newVal = pd.value * weight;
          gradientSum.add(pd.parameterId, newVal);
        }

      }
    }

    FunctionGradient gradient = new FunctionGradient.SparseGradient(gradientSum.size());
    for (Map.Entry<Integer, Double> e : gradientSum.entrySet()) {
      gradient.put(e.getKey(), e.getValue());
    }
    return gradient;
  }

  /**
   * Scale values in the dataset between any arbitrary points a and b
   */
  private double scale(int a, int b, double min, double max, double value) {
    return ((b - a) * ((value - min) / (max - min))) + a;
  }

}
