package aic.gas.sc.gg_bot.model;

import aic.gas.abstract_bot.model.features.FeatureContainerHeader;
import aic.gas.abstract_bot.service.DecisionLoadingService;
import aic.gas.mas.model.knowledge.DataForDecision;
import aic.gas.mas.model.metadata.AgentTypeID;
import aic.gas.mas.model.metadata.DesireKeyID;
import aic.gas.sc.gg_bot.service.implementation.DecisionLoadingServiceImpl;

/**
 * Class with static method to get decision for passed configuration
 */
public class Decider {

  private static final DecisionLoadingService DECISION_LOADING_SERVICE = DecisionLoadingServiceImpl
      .getInstance();

  /**
   * Get decision
   */
  public static boolean getDecision(AgentTypeID agentTypeID, DesireKeyID desireKeyID,
      DataForDecision dataForDecision, FeatureContainerHeader featureContainerHeader) {
    return DECISION_LOADING_SERVICE.getDecisionPoint(agentTypeID, desireKeyID)
        .nextAction(featureContainerHeader.formVector(dataForDecision));
  }

}
