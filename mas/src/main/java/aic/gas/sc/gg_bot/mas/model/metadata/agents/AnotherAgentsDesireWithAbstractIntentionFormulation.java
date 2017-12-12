package aic.gas.sc.gg_bot.mas.model.metadata.agents;

import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.planing.DesireFromAnotherAgent;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesireForAgents;
import java.util.Optional;

/**
 * Concrete implementation of another agent's desire with abstract plan formulation
 */
public class AnotherAgentsDesireWithAbstractIntentionFormulation extends
    DesireFormulation.WithAbstractPlan implements
    AnotherAgentsInternalDesireFormulation<DesireFromAnotherAgent.WithAbstractIntention> {

  @Override
  public Optional<DesireFromAnotherAgent.WithAbstractIntention> formDesire(
      SharedDesireForAgents desireForAgents, WorkingMemory memory) {
    if (supportsDesireType(desireForAgents.getDesireKey())) {
      DesireFromAnotherAgent.WithAbstractIntention withAbstractIntention = new DesireFromAnotherAgent.WithAbstractIntention(
          desireForAgents,
          memory, getDecisionInDesire(desireForAgents.getDesireKey()),
          getDecisionInIntention(desireForAgents.getDesireKey()),
          desiresForOthersByKey.get(desireForAgents.getDesireKey()),
          desiresWithAbstractIntentionByKey.get(desireForAgents.getDesireKey()),
          desiresWithIntentionToActByKey.get(desireForAgents.getDesireKey()),
          desiresWithIntentionToReasonByKey.get(desireForAgents.getDesireKey()),
          getReactionInDesire(desireForAgents.getDesireKey()),
          getReactionInIntention(desireForAgents.getDesireKey()));
      return Optional.of(withAbstractIntention);
    }
    return Optional.empty();
  }

  @Override
  public boolean supportsDesireType(DesireKey desireKey) {
    return supportsType(desireKey);
  }
}
