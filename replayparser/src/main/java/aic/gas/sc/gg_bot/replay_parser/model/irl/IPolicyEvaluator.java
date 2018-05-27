package aic.gas.sc.gg_bot.replay_parser.model.irl;

import aic.gas.sc.gg_bot.abstract_bot.model.decision.OurProbabilisticPolicy;
import burlap.behavior.singleagent.Episode;

public interface IPolicyEvaluator {

  static double logLikelihoodOfTrajectory(Episode ep, OurProbabilisticPolicy policy) {
    double logLike = 0.0D;
    for (int i = 0; i < ep.numTimeSteps() - 1; i++) {

      //prevent by playing action with small ppt
      double actProb = policy.getProbabilityOfActionInState(ep.state(i), ep.action(i));
      logLike += Math.log(actProb);
    }
    return logLike;
  }

}
