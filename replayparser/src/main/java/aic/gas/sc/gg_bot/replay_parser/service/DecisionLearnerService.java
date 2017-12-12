package aic.gas.sc.gg_bot.replay_parser.service;

/**
 * Contract for decision learning service
 */
public interface DecisionLearnerService {

  /**
   * Method to learn decision makers and store them to storage
   */
  void learnDecisionMakers();

}
