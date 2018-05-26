package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.decision.MetaPolicy;
import aic.gas.sc.gg_bot.abstract_bot.model.decision.StateBuilder;
import aic.gas.sc.gg_bot.replay_parser.configuration.Configuration;
import aic.gas.sc.gg_bot.replay_parser.model.irl.GPMLIRL;
import aic.gas.sc.gg_bot.replay_parser.model.irl.GPRewardFunction;
import aic.gas.sc.gg_bot.replay_parser.model.irl.IPlanerInitializerStrategy;
import aic.gas.sc.gg_bot.replay_parser.model.irl.KFoldBatchIterator;
import aic.gas.sc.gg_bot.replay_parser.model.irl.OurGradientDescentSARSA;
import aic.gas.sc.gg_bot.replay_parser.service.IPolicyLearningService;
import burlap.behavior.functionapproximation.DifferentiableStateActionValue;
import burlap.behavior.singleagent.Episode;
import io.jenetics.ext.util.Tree;
import io.jenetics.prog.ProgramGene;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of IPolicyLearningService. To learn policy IRL is used. Policy is learnt using
 * provided "experts'" episodes
 */
@Slf4j
public class PolicyLearningService implements IPolicyLearningService {

  @Override
  public MetaPolicy learnPolicy(List<Episode> episodes, StateBuilder stateBuilder,
      Configuration configuration, int numberOfFeatures) {

    //K-folds
    KFoldBatchIterator batchIterator = new KFoldBatchIterator(configuration.getFolds(), episodes);

    //default strategy to create instance of planner
    IPlanerInitializerStrategy initializerStrategy = () -> {
      DifferentiableStateActionValue vfa = MetaPolicy.initializeVFA(stateBuilder,
          configuration.getNTilings(), configuration.getResolution(), configuration.getDefaultQ());
      return new OurGradientDescentSARSA(configuration.getGamma(), vfa,
          configuration.getLearningRate(), configuration.getLambda(), MetaPolicy.ACTIONS,
          configuration.getExploration());
    };

    //solve IRL problem
    ProgramGene<Double> reward = GPMLIRL.learnReward(configuration, batchIterator, numberOfFeatures,
        initializerStrategy);
    log.info(Tree.toString(reward));

    //learn policy from demonstrations
    GPRewardFunction ourRewardFunction = new GPRewardFunction(reward);
    DifferentiableStateActionValue vfa = MetaPolicy.initializeVFA(stateBuilder,
        configuration.getNTilings(), configuration.getResolution(), configuration.getDefaultQ());
    OurGradientDescentSARSA agent = new OurGradientDescentSARSA(configuration.getGamma(), vfa,
        configuration.getLearningRate(), configuration.getLambda(), MetaPolicy.ACTIONS,
        configuration.getExploration());

    batchIterator.getAll().forEach(episode -> agent.learnFromEpisode(episode, ourRewardFunction));
    for (int i = 0; i < 100; i++) {
      if (agent.getMaxRelativeQValueChange() <= 0.1){
        break;
      }
      agent.resetMaxRelativeQValueChange();
      batchIterator.getAll().forEach(episode -> agent.learnFromEpisode(episode, ourRewardFunction));
    }

    //get weights
    Map<Integer, Double> weights = IntStream.range(0, vfa.numParameters())
        .boxed()
        .collect(Collectors.toMap(Function.identity(), vfa::getParameter));

    return MetaPolicy.builder()
        .defaultQ(configuration.getDefaultQ())
        .exploration(configuration.getExploration())
        .nTilings(configuration.getNTilings())
        .resolution(configuration.getResolution())
        .stateBuilder(stateBuilder)
        .weights(weights)
        .build();
  }

}
