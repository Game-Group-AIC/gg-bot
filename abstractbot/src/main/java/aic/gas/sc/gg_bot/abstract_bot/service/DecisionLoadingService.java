package aic.gas.sc.gg_bot.abstract_bot.service;

import aic.gas.mas.model.metadata.AgentTypeID;
import aic.gas.mas.model.metadata.DesireKeyID;
import aic.gas.sc.gg_bot.abstract_bot.model.decision.DecisionPoint;

/**
 * Contract for service loading decision points
 */
public interface DecisionLoadingService {

  /**
   * For given keys get decision point instance
   */
  DecisionPoint getDecisionPoint(AgentTypeID agentTypeID, DesireKeyID desireKeyID);

}
