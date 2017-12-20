package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.replay_parser.model.irl.DecisionState;
import aic.gas.sc.gg_bot.replay_parser.model.irl.OurMLIRL;
import aic.gas.sc.gg_bot.replay_parser.service.PolicyLearningService;
import burlap.behavior.functionapproximation.dense.DenseStateFeatures;
import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.learnfromdemo.mlirl.MLIRL;
import burlap.behavior.singleagent.learnfromdemo.mlirl.MLIRLRequest;
import burlap.behavior.singleagent.learnfromdemo.mlirl.commonrfs.LinearStateDifferentiableRF;
import burlap.behavior.singleagent.learnfromdemo.mlirl.differentiableplanners.DifferentiableSparseSampling;
import burlap.behavior.valuefunction.QProvider;
import burlap.debugtools.RandomFactory;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import java.util.List;

/**
 * Implementation of PolicyLearningService. To learn policy IRL is used. Policy is learnt using
 * provided "experts'" episodes
 */
public class PolicyLearningServiceImpl implements PolicyLearningService {

  private static final double beta = 10;
  private static final boolean doNotPrintDebug = false;
  private static final int steps = 100;
  //set last "dummy state" to large negative number as we do not want to go there
  private static final int minReward = -1, maxReward = 1;
  private static final double learningRate = 0.01;
  private static final double maxLikelihoodChange = 0.1;
  //set time budget to 30 minutes
  //TODO increase
  private static final long timeBudget = 1000 * 60 * 30;

  @Override
  public Policy learnPolicy(SADomain domain, List<Episode> episodes, int numberOfStates,
      int numberOfSamplesToUse) {

    //create reward function features to use
    LocationFeatures features = new LocationFeatures(numberOfStates);

    //create a reward function that is linear with respect to those features and has small random
    //parameter values to start
    LinearStateDifferentiableRF rf = new LinearStateDifferentiableRF(features, numberOfStates);
    for (int i = 0; i < rf.numParameters() - 1; i++) {
      rf.setParameter(i, RandomFactory.getMapped(0).nextDouble() * 0.2 - 0.1);
    }

    rf.setParameter(rf.numParameters() - 1, minReward);

    //use either DifferentiableVI or DifferentiableSparseSampling for planning. The latter enables receding horizon IRL,
    //but you will probably want to use a fairly large horizon for this kind of reward function.
    HashableStateFactory hashingFactory = new SimpleHashableStateFactory();
//        DifferentiableVI dplanner = new DifferentiableVI(domain, rf, 0.99, beta, hashingFactory, 0.01, 100);
    DifferentiableSparseSampling dplanner = new DifferentiableSparseSampling(domain, rf, 0.99,
        hashingFactory, (int) Math.sqrt(numberOfStates), numberOfSamplesToUse, beta);

    dplanner.toggleDebugPrinting(doNotPrintDebug);

    //define the IRL problem
    MLIRLRequest request = new MLIRLRequest(domain, dplanner, episodes, rf);
    request.setBoltzmannBeta(beta);

    //run MLIRL on it
    MLIRL irl = new OurMLIRL(request, learningRate, maxLikelihoodChange, steps, minReward,
        maxReward, timeBudget);
    irl.performIRL();

    return new GreedyQPolicy((QProvider) request.getPlanner());
  }

  /**
   * A state feature vector generator that create a binary feature vector where each element
   * indicates whether the agent is in a cell of a different type. All zeros indicates that the
   * agent is in an empty cell.
   */
  private static class LocationFeatures implements DenseStateFeatures {

    private int numLocations;

    LocationFeatures(int numLocations) {
      this.numLocations = numLocations;
    }

    @Override
    public double[] features(State s) {

      double[] fv = new double[this.numLocations];

      int location = ((DecisionState) s).getState();
      if (location != -1) {
        fv[location] = 1.0;
      }

      return fv;
    }

    @Override
    public DenseStateFeatures copy() {
      return new LocationFeatures(numLocations);
    }
  }
}
