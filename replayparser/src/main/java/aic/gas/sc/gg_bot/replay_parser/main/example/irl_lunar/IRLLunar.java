package aic.gas.sc.gg_bot.replay_parser.main.example.irl_lunar;

import aic.gas.sc.gg_bot.abstract_bot.model.decision.OurState;
import aic.gas.sc.gg_bot.replay_parser.configuration.Configuration;
import aic.gas.sc.gg_bot.replay_parser.model.irl.GPMLIRL;
import aic.gas.sc.gg_bot.replay_parser.model.irl.GPRewardFunction;
import aic.gas.sc.gg_bot.replay_parser.model.irl.IPlanerInitializerStrategy;
import aic.gas.sc.gg_bot.replay_parser.model.irl.KFoldBatchIterator;
import aic.gas.sc.gg_bot.replay_parser.model.irl.OurGradientDescentSARSA;
import burlap.behavior.functionapproximation.DifferentiableStateActionValue;
import burlap.behavior.functionapproximation.dense.ConcatenatedObjectFeatures;
import burlap.behavior.functionapproximation.dense.NumericVariableFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TileCodingFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TilingArrangement;
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
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IRLLunar {

  public static void main(String[] args) {
    mimicExpert(20);
  }

  public static void mimicExpert(int numberOfDemonstrationToUse) {
    LunarLanderDomain lld = new LunarLanderDomain();
    OOSADomain domain = lld.generateDomain();
    ConcatenatedObjectFeatures inputFeatures = new ConcatenatedObjectFeatures()
        .addObjectVectorizion(OurState.NAME, new NumericVariableFeatures());

    Configuration configuration = Configuration.builder().build();

    int nTilings = configuration.getNTilings();
    double resolution = configuration.getResolution();

    double xWidth = (lld.getXmax() - lld.getXmin()) / resolution;
    double yWidth = (lld.getYmax() - lld.getYmin()) / resolution;
    double velocityWidth = 2 * lld.getVmax() / resolution;
    double angleWidth = 2 * lld.getAngmax() / resolution;

    TileCodingFeatures tilecoding = new TileCodingFeatures(inputFeatures);
    tilecoding.addTilingsForAllDimensionsWithWidths(new double[]{xWidth, yWidth, velocityWidth,
        velocityWidth, angleWidth}, nTilings, TilingArrangement.RANDOM_JITTER);
    double defaultQ = configuration.getDefaultQ();

    KFoldBatchIterator batchIterator = new KFoldBatchIterator(configuration.getFolds(),
        ExpertExampleGenerator.getExpertsDemonstrations(numberOfDemonstrationToUse));
    List<Action> actions = batchIterator.getAll()
        .flatMap(episode -> episode.actionSequence.stream())
        .distinct()
        .collect(Collectors.toList());

    //default strategy to create instance of planner
    IPlanerInitializerStrategy initializerStrategy = () -> {
      DifferentiableStateActionValue vfa = tilecoding.generateVFA(defaultQ / nTilings);
      return new OurGradientDescentSARSA(0.99, vfa, 0.02, 0.5, actions, 0.05);
    };

    //solve IRL problem
    ProgramGene<Double> reward = GPMLIRL.learnReward(configuration, batchIterator, 5,
        initializerStrategy);
    log.info(Tree.toString(reward));

    //learn policy from demonstrations
    GPRewardFunction ourRewardFunction = new GPRewardFunction(reward);
    DifferentiableStateActionValue vfa = tilecoding.generateVFA(defaultQ / nTilings);
    OurGradientDescentSARSA agent = new OurGradientDescentSARSA(0.99, vfa, 0.02,
        0.5, actions, 0.1);
    batchIterator.getAll().forEach(episode -> agent.learnFromEpisode(episode, ourRewardFunction));
    for (int i = 0; i < 100; i++) {
      if (agent.getMaxRelativeQValueChange() <= 0.1) {
        break;
      }
      agent.resetMaxRelativeQValueChange();
      batchIterator.getAll().forEach(episode -> agent.learnFromEpisode(episode, ourRewardFunction));
    }

    //run agent in environment
    LLState s = new LLState(new LLAgent(5, 0, 0), new LLBlock.LLPad(75, 95, 0, 10, "pad"));
    SimulatedEnvironment env = new SimulatedEnvironment(domain, s);
    List<Episode> episodes = new ArrayList<>();

    for (int i = 0; i < 100; i++) {
      Episode episode = new Episode();
      env.resetEnvironment();
      int trials = 0;
      while (true) {
        LLState state = (LLState) env.currentObservation();
        OurState ourState = new OurState(new double[]{state.agent.x, state.agent.y, state.agent.vx,
            state.agent.vy, state.agent.angle});
        Action a = agent.getCurrentPolicy().selectActionInState(ourState);
        episode.addState(state);
        episode.addAction(a);
        env.executeAction(a);
        episode.addReward(env.lastReward());
        trials++;
        if (trials > 500 || env.isInTerminalState()) {
          if (env.isInTerminalState()) {
            log.info("Reached terminal state in it.:" + i);
          }
          episode.addState(env.currentObservation());
          if (i >= 90) {
            episodes.add(episode);
          }
          break;
        }
      }

      //learn from episode
//      agent.learnFromEpisode(episode, ourRewardFunction, state ->
//          new OurState(new double[]{((LLState) state).agent.x, ((LLState) state).agent.y,
//              ((LLState) state).agent.vx, ((LLState) state).agent.vy,
//              ((LLState) state).agent.angle}));
    }

    // avg cumulative reward for demonstrations
    double avg = batchIterator.getAll()
        .mapToDouble(episode -> episode.rewardSequence.stream()
            .mapToDouble(value -> value)
            .sum())
        .average().orElse(0);
    log.info("Dem. trajectories avg. cumulative reward: " + avg);

    // avg cumulative reward
    avg = episodes.stream()
        .mapToDouble(episode -> episode.rewardSequence.stream()
            .mapToDouble(value -> value)
            .sum())
        .average().orElse(0);
    log.info("Own trajectories avg. cumulative reward: " + avg + " from " + episodes.size());

    // avg cumulative reward
    avg = episodes.stream()
        .mapToDouble(episode -> episode.rewardSequence.stream()
            .mapToDouble(value -> value)
            .sum())
        .average().orElse(0);
    log.info("Own trajectories - terminated avg. cumulative reward: " + avg + " from "
        + episodes.size());

    Visualizer v = LLVisualizer.getVisualizer(lld.getPhysParams());
    new EpisodeSequenceVisualizer(v, domain, episodes);
  }

}
