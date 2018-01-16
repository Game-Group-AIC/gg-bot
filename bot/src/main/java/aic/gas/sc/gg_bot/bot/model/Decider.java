package aic.gas.sc.gg_bot.bot.model;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureContainerHeader;
import aic.gas.sc.gg_bot.abstract_bot.service.DecisionLoadingService;
import aic.gas.sc.gg_bot.bot.service.implementation.DecisionLoadingServiceImpl;
import aic.gas.sc.gg_bot.mas.model.knowledge.DataForDecision;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import lombok.extern.slf4j.Slf4j;

/**
 * Class with static method to get decision for passed configuration
 */
@Slf4j
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
