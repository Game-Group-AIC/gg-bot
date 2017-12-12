package aic.gas.sc.gg_bot.replay_parser.service;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.Episode;
import burlap.mdp.singleagent.SADomain;
import java.util.List;

/**
 * Contract for policy learning service
 */
public interface PolicyLearningService {

  /**
   * Learn policy for given domain using episodes
   */
  Policy learnPolicy(SADomain domain, List<Episode> episodes, int numberOfStates,
      int numberOfSamplesToUse);

}
