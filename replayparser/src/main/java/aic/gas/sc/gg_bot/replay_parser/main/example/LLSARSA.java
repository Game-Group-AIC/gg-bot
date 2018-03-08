package aic.gas.sc.gg_bot.replay_parser.main.example;

import burlap.behavior.functionapproximation.DifferentiableStateActionValue;
import burlap.behavior.functionapproximation.dense.ConcatenatedObjectFeatures;
import burlap.behavior.functionapproximation.dense.NumericVariableFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TileCodingFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TilingArrangement;
import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.learning.tdmethods.vfa.GradientDescentSarsaLam;
import burlap.domain.singleagent.lunarlander.LLVisualizer;
import burlap.domain.singleagent.lunarlander.LunarLanderDomain;
import burlap.domain.singleagent.lunarlander.state.LLAgent;
import burlap.domain.singleagent.lunarlander.state.LLBlock;
import burlap.domain.singleagent.lunarlander.state.LLState;
import burlap.mdp.core.action.Action;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.visualizer.Visualizer;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LLSARSA {

  public static void LLSARSA() {

    LunarLanderDomain lld = new LunarLanderDomain();
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
    GradientDescentSarsaLam agent = new GradientDescentSarsaLam(domain, 0.99, vfa, 0.02, 0.5);

    SimulatedEnvironment env = new SimulatedEnvironment(domain, s);

    //learn policy
    for (int i = 0; i < 5000; i++) {
      agent.runLearningEpisode(env);
      env.resetEnvironment();
    }

    //run agent in environment
    Episode episode = new Episode();
    env.resetEnvironment();
    GreedyQPolicy greedyQPolicy = agent.planFromState(env.currentObservation());
    while (true) {
      Action a = greedyQPolicy.action(env.currentObservation());
      episode.addState(env.currentObservation());
      episode.addAction(a);
      episode.addReward(env.lastReward());
      env.executeAction(a);
      if (env.isInTerminalState()) {
        episode.addState(env.currentObservation());
        episode.addReward(env.lastReward());
        break;
      }
    }
    List<Episode> episodes = Collections.singletonList(episode);
    Visualizer v = LLVisualizer.getVisualizer(lld.getPhysParams());
    new EpisodeSequenceVisualizer(v, domain, episodes);
  }


  public static void main(String[] args) {
    //MCLSPIFB();
    //MCLSPIRBF();
    //IPSS();
    LLSARSA();
  }

}
