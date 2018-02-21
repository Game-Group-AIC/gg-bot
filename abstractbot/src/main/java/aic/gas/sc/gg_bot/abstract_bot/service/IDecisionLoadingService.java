package aic.gas.sc.gg_bot.abstract_bot.service;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.decision.MDPForDecisionWithPolicy;

/**
 * Contract for service loading decision points
 */
public interface IDecisionLoadingService {

  /**
   * For given keys get decision point instance
   */
  MDPForDecisionWithPolicy getDecisionPoint(AgentTypes agentType, DesireKeys desireKey);

}
