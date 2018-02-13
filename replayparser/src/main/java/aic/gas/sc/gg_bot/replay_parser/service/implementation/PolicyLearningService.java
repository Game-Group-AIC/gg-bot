package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.replay_parser.configuration.Configuration;
import aic.gas.sc.gg_bot.replay_parser.model.irl.BatchIterator;
import aic.gas.sc.gg_bot.replay_parser.model.irl.DecisionDomainGenerator;
import aic.gas.sc.gg_bot.replay_parser.model.irl.DecisionState;
import aic.gas.sc.gg_bot.replay_parser.model.irl.OurMLIRL;
import aic.gas.sc.gg_bot.replay_parser.service.IPolicyLearningService;
import burlap.behavior.functionapproximation.dense.DenseStateFeatures;
import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.learnfromdemo.mlirl.MLIRL;
import burlap.behavior.singleagent.learnfromdemo.mlirl.MLIRLRequest;
import burlap.behavior.singleagent.learnfromdemo.mlirl.commonrfs.LinearStateDifferentiableRF;
import burlap.behavior.singleagent.learnfromdemo.mlirl.differentiableplanners.DifferentiableSparseSampling;
import burlap.behavior.valuefunction.QProvider;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import java.util.List;

/**
 * Implementation of IPolicyLearningService. To learn policy IRL is used. Policy is learnt using
 * provided "experts'" episodes
 */
public class PolicyLearningService implements IPolicyLearningService {

  private static final boolean doNotPrintDebug = false;

  @Override
  public Policy learnPolicy(SADomain domain, List<Episode> episodes, Configuration configuration,
      int numberOfClusters) {

    //create reward function features to use
    LocationFeatures features = new LocationFeatures(numberOfClusters + 1);

    //create a reward function that is linear with respect to those features and has small random
    //parameter values to start
    LinearStateDifferentiableRF rf = new LinearStateDifferentiableRF(features,
        numberOfClusters + 1);
    for (int i = 0; i < rf.numParameters() - 1; i++) {
      rf.setParameter(i,
          Math.abs(DecisionDomainGenerator.getRandomRewardInInterval(configuration)));
    }

    //set dummy state
    rf.setParameter(rf.numParameters() - 1,
        configuration.getMinReward() * configuration.getMultiplierOfRewardForDeadEnd());

    //use either DifferentiableVI or DifferentiableSparseSampling for planning. The latter enables receding horizon IRL,
    //but you will probably want to use a fairly large horizon for this kind of reward function.
    HashableStateFactory hashingFactory = new SimpleHashableStateFactory();
//        DifferentiableVI dplanner = new DifferentiableVI(domain, rf, 0.99, beta, hashingFactory, 0.01, 100);
    DifferentiableSparseSampling dplanner = new DifferentiableSparseSampling(domain, rf,
        configuration.getGamma(), hashingFactory, (int) Math.sqrt(numberOfClusters + 1),
        configuration.getCountOfTrajectoriesPerIRLBatch(), configuration.getBeta());

    dplanner.toggleDebugPrinting(doNotPrintDebug);

    //define the IRL problem
    MLIRLRequest request = new MLIRLRequest(domain, dplanner, episodes, rf);
    request.setBoltzmannBeta(configuration.getBeta());

    //run MLIRL on it
    MLIRL irl = new OurMLIRL(request, configuration,
        new BatchIterator(configuration.getCountOfTrajectoriesPerIRLBatch(), episodes));
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
