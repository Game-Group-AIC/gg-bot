package aic.gas.sc.gg_bot.abstract_bot.service;

import aic.gas.sc.gg_bot.abstract_bot.model.decision.DecisionPoint;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;

/**
 * Contract for service loading decision points
 */
public interface DecisionLoadingService {

  /**
   * For given keys get decision point instance
   */
  DecisionPoint getDecisionPoint(AgentTypeID agentTypeID, DesireKeyID desireKeyID);

}
