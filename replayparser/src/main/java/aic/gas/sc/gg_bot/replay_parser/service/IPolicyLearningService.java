package aic.gas.sc.gg_bot.replay_parser.service;

import aic.gas.sc.gg_bot.abstract_bot.model.decision.MetaPolicy;
import aic.gas.sc.gg_bot.abstract_bot.model.decision.StateBuilder;
import aic.gas.sc.gg_bot.replay_parser.configuration.Configuration;
import burlap.behavior.singleagent.Episode;
import java.util.List;

/**
 * Contract for policy learning service
 */
public interface IPolicyLearningService {

  /**
   * Learn policy using episodes
   */
  MetaPolicy learnPolicy(List<Episode> episodes, StateBuilder stateBuilder,
      Configuration configuration, int numberOfFeatures);

}
