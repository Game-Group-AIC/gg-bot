package aic.gas.sc.gg_bot.bot.model;

import aic.gas.sc.gg_bot.abstract_bot.model.decision.MDPForDecisionWithPolicy;
import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureContainerHeader;
import aic.gas.sc.gg_bot.abstract_bot.service.IDecisionLoadingService;
import aic.gas.sc.gg_bot.bot.service.implementation.DecisionLoadingService;
import aic.gas.sc.gg_bot.mas.model.knowledge.DataForDecision;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import lombok.extern.slf4j.Slf4j;

/**
 * Class with static method to get decision for passed configuration
 */
@Slf4j
public class Decider {

  private static final IDecisionLoadingService DECISION_LOADING_SERVICE = DecisionLoadingService
      .getInstance();

  /**
   * Get decision
   */
  public static boolean getDecision(AgentTypeID agentTypeID, DesireKeyID desireKeyID,
      DataForDecision dataForDecision, FeatureContainerHeader featureContainerHeader, int frame,
      int agentId) {
    MDPForDecisionWithPolicy policy = DECISION_LOADING_SERVICE
        .getDecisionPoint(agentTypeID, desireKeyID);
    if (policy == null) {
      log.error(
          "No policy loaded: " + desireKeyID.getName() + ", for agent " + agentTypeID.getName()
              + " with ID: " + agentId);
      return false;
    }
    return policy.nextAction(featureContainerHeader.formVector(dataForDecision), frame, agentId,
        featureContainerHeader.getForHowLongToCacheDecision());
  }

}
