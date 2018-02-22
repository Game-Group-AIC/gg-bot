package aic.gas.sc.gg_bot.replay_parser.main.example.irl_lunar;

import aic.gas.sc.gg_bot.replay_parser.configuration.Configuration;
import aic.gas.sc.gg_bot.replay_parser.model.irl.BatchIterator;
import aic.gas.sc.gg_bot.replay_parser.model.irl_rl.OurGradientDescentSarsaLam;
import aic.gas.sc.gg_bot.replay_parser.model.irl_rl.OurMLIRL;
import aic.gas.sc.gg_bot.replay_parser.model.irl_rl.OurRewardFunction;
import burlap.behavior.functionapproximation.DifferentiableStateActionValue;
import burlap.behavior.functionapproximation.dense.ConcatenatedObjectFeatures;
import burlap.behavior.functionapproximation.dense.NumericVariableFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TileCodingFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TilingArrangement;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.domain.singleagent.lunarlander.LLVisualizer;
import burlap.domain.singleagent.lunarlander.LunarLanderDomain;
import burlap.domain.singleagent.lunarlander.state.LLAgent;
import burlap.domain.singleagent.lunarlander.state.LLBlock;
import burlap.domain.singleagent.lunarlander.state.LLState;
import burlap.mdp.core.action.Action;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.visualizer.Visualizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IRLLunar {

  private static final Random RANDOM = new Random();

  public static void main(String[] args) {
    mimicExpert(300, 3);
  }

  public static void mimicExpert(int numberOfDemonstrationToUse, int indeterminateOfPolynomial) {

    //solve IRL problem
    double[][] reward = OurMLIRL.learnReward(Configuration.builder().build(),
        new BatchIterator(300,
            ExpertExampleGenerator.getExpertsDemonstrations(numberOfDemonstrationToUse)), 5,
        indeterminateOfPolynomial);

    OurRewardFunction rf = new OurRewardFunction(5, indeterminateOfPolynomial);
    rf.updateCoefficients(reward);
    LunarLanderDomain lld = new LunarLanderDomain();
    lld.setRf(rf);
    OOSADomain domain = lld.generateDomain();

    LLState s = new LLState(new LLAgent(5, 0, 0), new LLBlock.LLPad(75, 95, 0, 10, "pad"));

    ConcatenatedObjectFeatures inputFeatures = new ConcatenatedObjectFeatures()
        .addObjectVectorizion(LunarLanderDomain.CLASS_AGENT, new NumericVariableFeatures());

    int nTilings = 5;
    double resolution = 10.;

    SimulatedEnvironment env = new SimulatedEnvironment(domain, s);

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
    OurGradientDescentSarsaLam agent = new OurGradientDescentSarsaLam(domain, 0.99, 10, vfa, 0.02,
        0.5, 500, rf);

    //run agent in environment
    List<Episode> episodes = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Episode episode = new Episode();
      env.resetEnvironment();
      Policy greedyQPolicy = agent.planFromState(env.currentObservation());
      int trials = 0;
      while (!env.isInTerminalState()) {
        Action a = greedyQPolicy.action(env.currentObservation());
        episode.addState(env.currentObservation());
        episode.addAction(a);
        env.executeAction(a);
        trials++;
        if (trials > 500) {
          break;
        }
      }
      episodes.add(episode);
    }

    Visualizer v = LLVisualizer.getVisualizer(lld.getPhysParams());
    new EpisodeSequenceVisualizer(v, domain, episodes);
  }

}
