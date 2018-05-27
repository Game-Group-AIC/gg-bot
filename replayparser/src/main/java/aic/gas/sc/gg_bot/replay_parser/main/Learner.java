package aic.gas.sc.gg_bot.replay_parser.main;

import aic.gas.sc.gg_bot.replay_parser.service.implementation.DecisionLearnerService;
import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
public class Learner {

  private static final DecisionLearnerService learnerService = new DecisionLearnerService();

  public static void main(String[] args) throws Exception {

    //TODO increase
    int num_proc = 4;
    String NUM_PROC = System.getenv("NUM_PROC");
    if (NUM_PROC != null) {
      num_proc = Integer.parseInt(NUM_PROC);
    }
    log.info("Using numproc " + num_proc);
    learnerService.learnDecisionMakers(num_proc);
  }

}
