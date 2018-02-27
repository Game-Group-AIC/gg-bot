package aic.gas.sc.gg_bot.replay_parser.main.example.irl_lunar;

import aic.gas.sc.gg_bot.replay_parser.configuration.Configuration;
import aic.gas.sc.gg_bot.replay_parser.model.irl.KFoldBatchIterator;
import aic.gas.sc.gg_bot.replay_parser.model.irl_rl.GPMLIRL;
import aic.gas.sc.gg_bot.replay_parser.model.irl_rl.GPRewardFunction;
import aic.gas.sc.gg_bot.replay_parser.model.irl_rl.OurGradientDescentSarsaLam;
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
import io.jenetics.ext.util.Tree;
import io.jenetics.prog.ProgramGene;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IRLLunar {

  public static void main(String[] args) {
    mimicExpert(1000, 4);
  }

  public static void mimicExpert(int numberOfDemonstrationToUse, int height) {

    //solve IRL problem
    KFoldBatchIterator batchIterator = new KFoldBatchIterator(5,
        ExpertExampleGenerator.getExpertsDemonstrations(numberOfDemonstrationToUse));
    ProgramGene<Double> reward = GPMLIRL
        .learnReward(Configuration.builder().build(), batchIterator, 5, height);
    log.info(Tree.toString(reward));

    GPRewardFunction rf = new GPRewardFunction(reward);
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
    OurGradientDescentSarsaLam agent = new OurGradientDescentSarsaLam<>(domain, 0.99, 10, vfa, 0.02,
        0.5, 500, rf);

    //learn policy
    batchIterator.getAll().forEach(episode -> agent.learnFromEpisode(episode, rf));

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
