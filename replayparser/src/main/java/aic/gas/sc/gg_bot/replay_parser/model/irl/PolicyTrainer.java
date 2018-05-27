package aic.gas.sc.gg_bot.replay_parser.model.irl;

import aic.gas.sc.gg_bot.abstract_bot.model.decision.OurProbabilisticPolicy;
import burlap.behavior.functionapproximation.DifferentiableStateActionValue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PolicyTrainer implements IPolicyEvaluator {

  public static DifferentiableStateActionValue learnValueFunction(KFoldBatchIterator batchIterator,
      IPlanerInitializerStrategy planerInitializerStrategy, GPRewardFunction ourRewardFunction) {
    OurGradientDescentSARSA planner = planerInitializerStrategy.initPlanner();
    batchIterator.trainingData()
        .forEach(episode -> planner.learnFromEpisode(episode, ourRewardFunction));
    OurProbabilisticPolicy policy = planner.getCurrentPolicy();
    double bestLikelihood = batchIterator.testingData()
        .mapToDouble(episode -> IPolicyEvaluator.logLikelihoodOfTrajectory(episode, policy))
        .sum();
    DifferentiableStateActionValue bestVFA = planner.getCopyOfValueFunction();
    for (int i = 0; i < 100; i++) {
      if (planner.getMaxRelativeQValueChange() <= 0.1) {
        log.info("Q-function has converged in iteration {}...", i);
        break;
      }
      planner.resetMaxRelativeQValueChange();
      batchIterator.trainingData()
          .forEach(episode -> planner.learnFromEpisode(episode, ourRewardFunction));
      OurProbabilisticPolicy pol = planner.getCurrentPolicy();
      double likelihood = batchIterator.testingData()
          .mapToDouble(episode -> IPolicyEvaluator.logLikelihoodOfTrajectory(episode, pol))
          .sum();
      if (likelihood > bestLikelihood) {
        log.info("Found better policy with likelihood: {} vs {}", bestLikelihood, likelihood);
        bestLikelihood = likelihood;
        bestVFA = planner.getCopyOfValueFunction();
      }
    }
    log.info("Found best policy with likelihood: {}", bestLikelihood);
    return bestVFA;
  }

}
