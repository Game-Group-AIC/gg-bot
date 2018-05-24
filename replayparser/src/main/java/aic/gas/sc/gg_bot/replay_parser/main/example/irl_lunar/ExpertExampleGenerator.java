package aic.gas.sc.gg_bot.replay_parser.main.example.irl_lunar;

import aic.gas.sc.gg_bot.abstract_bot.model.features.OurState;
import aic.gas.sc.gg_bot.abstract_bot.utils.SerializationUtil;
import aic.gas.sc.gg_bot.replay_parser.model.irl_rl.OurEpisode;
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
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExpertExampleGenerator {

  private static final String EXTENSION = "demo";
  private static final String FOLDER = "lunar_demonstrations";

  public static List<Episode> getExpertsDemonstrations(int countOfDemonstrations) {
    List<Episode> demonstrations = new ArrayList<>();
//    List<Episode> demonstrations = SerializationUtil.getAllFilesInFolder(FOLDER, EXTENSION).stream()
//        .map(file -> {
//          try {
//            return (Episode) SerializationUtil.deserialize(file.getAbsolutePath());
//          } catch (Exception e) {
//            return null;
//          }
//        })
//        .filter(Objects::nonNull)
//        .collect(Collectors.toList());
    if (demonstrations.size() < countOfDemonstrations) {
      int missingEpisodes = countOfDemonstrations - demonstrations.size();
      List<OurEpisode> newEpisodes = generateEpisodes(missingEpisodes, 5000);

//      //TODO hack, just increase count...
//      try {
//        int bound = newEpisodes.size();
//        for (int i = 0; i < bound; i++) {
//          SerializationUtil.serialize(newEpisodes.get(i), FOLDER + "/" +
//              +(demonstrations.size() + i) + "." + EXTENSION);
//        }
//      } catch (Exception e) {
//        log.info("Failed to serialize episode: " + e.getLocalizedMessage());
//      }

      demonstrations.addAll(newEpisodes);
    }
    return demonstrations.subList(0, countOfDemonstrations);
  }

  public static List<OurEpisode> generateEpisodes(int countOfEpisodesToGenerate, int trials) {

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
    List<OurEpisode> episodes = new ArrayList<>();

    for (int i = 0; i < countOfEpisodesToGenerate; i++) {
      OurEpisode episode = new OurEpisode();
      env.resetEnvironment();
      GreedyQPolicy greedyQPolicy = agent.planFromState(env.currentObservation());
      env.resetEnvironment();
      boolean reachedTerminalState = false;
      while (true) {
        LLState state = (LLState) env.currentObservation();
        Action a = greedyQPolicy.action(state);
        state = (LLState) env.currentObservation();
        OurState ourState = new OurState(new double[]{state.agent.x, state.agent.y, state.agent.vx,
            state.agent.vy, state.agent.angle});
        episode.addState(ourState);
        episode.addAction(a);
        env.executeAction(a);
        episode.addReward(env.lastReward());
        if (env.isInTerminalState() || episode.numTimeSteps() > 500) {
          if (env.isInTerminalState()) {
            log.info("Terminal state reached in it. " + i);
            reachedTerminalState = true;
          }
          ourState = new OurState(new double[]{state.agent.x, state.agent.y, state.agent.vx,
              state.agent.vy, state.agent.angle});
          episode.addState(ourState);
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
