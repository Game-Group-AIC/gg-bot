package aic.gas.sc.gg_bot.replay_parser.main.example.irl_lunar;

import burlap.behavior.functionapproximation.DifferentiableStateActionValue;
import burlap.behavior.functionapproximation.dense.ConcatenatedObjectFeatures;
import burlap.behavior.functionapproximation.dense.NumericVariableFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TileCodingFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TilingArrangement;
import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.learning.tdmethods.vfa.GradientDescentSarsaLam;
import burlap.domain.singleagent.lunarlander.LunarLanderDomain;
import burlap.domain.singleagent.lunarlander.state.LLAgent;
import burlap.domain.singleagent.lunarlander.state.LLBlock;
import burlap.domain.singleagent.lunarlander.state.LLState;
import burlap.mdp.core.action.Action;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.oo.OOSADomain;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExpertExampleGenerator {

  private static final String LUNAR_DEMONSTRATIONS = ".lunar_demonstrations";
  private static final String LUNAR_DEMONSTRATIONS_FOLDER = "lunar_demonstrations";

  public static List<Episode> getExpertsDemonstrations(int countOfDemonstrations) {
    List<Episode> demonstrations = Episode.readEpisodes(LUNAR_DEMONSTRATIONS_FOLDER);
    if (demonstrations.size() < countOfDemonstrations) {
      int missingEpisodes = countOfDemonstrations - demonstrations.size();
      demonstrations.addAll(generateEpisodes(missingEpisodes, 5000));
      Episode.writeEpisodes(demonstrations, LUNAR_DEMONSTRATIONS_FOLDER, LUNAR_DEMONSTRATIONS);
    }
    return demonstrations.subList(0, countOfDemonstrations);
  }

  public static List<Episode> generateEpisodes(int countOfEpisodesToGenerate, int trials) {

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
    for (int i = 0; i < trials; i++) {
      agent.runLearningEpisode(env, 1000);
      env.resetEnvironment();
    }

    //run agent in environment
    List<Episode> episodes = new ArrayList<>();

    for (int i = 0; i < countOfEpisodesToGenerate; i++) {
      Episode episode = new Episode();
      env.resetEnvironment();
      GreedyQPolicy greedyQPolicy = agent.planFromState(env.currentObservation());
      env.resetEnvironment();
      boolean reachedTerminalState = false;
      while (true) {
        Action a = greedyQPolicy.action(env.currentObservation());
        episode.addState(env.currentObservation());
        episode.addAction(a);
        env.executeAction(a);
        if (env.isInTerminalState() || episode.numTimeSteps() > 500) {
          episode.addState(env.currentObservation());
          episode.addReward(env.lastReward());
          if (env.isInTerminalState()) {
            log.info("Terminal state reached in it. " + i);
            reachedTerminalState = true;
          }
          break;
        }
      }
      if (reachedTerminalState) {
        log.info("Adding new episode in iteration " + i);
        episodes.add(episode);
      }
    }

    return episodes;
  }

}
