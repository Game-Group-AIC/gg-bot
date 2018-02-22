package aic.gas.sc.gg_bot.mas.model.metadata.agents;

import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.planing.DesireFromAnotherAgent;
import aic.gas.sc.gg_bot.mas.model.planing.DesireFromAnotherAgent.WithIntentionWithPlan;
import aic.gas.sc.gg_bot.mas.model.planing.IntentionCommand.FromAnotherAgent;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesireForAgents;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand.DesiredByAnotherAgent;
import aic.gas.sc.gg_bot.mas.model.planing.command.ICommandFormulationStrategy;
import java.util.Optional;

/**
 * Concrete implementation of another agent's desire with intention with plan formulation
 */
public class AnotherAgentsDesireWithIntentionWithActingCommandFormulation extends
    DesireFormulation.WithCommand<ICommandFormulationStrategy<DesiredByAnotherAgent, FromAnotherAgent>> implements
    IAnotherAgentsInternalDesireFormulation<WithIntentionWithPlan> {

  @Override
  public Optional<DesireFromAnotherAgent.WithIntentionWithPlan> formDesire(
      SharedDesireForAgents desireForAgents, WorkingMemory memory) {
    if (supportsDesireType(desireForAgents.getDesireKey())) {
      DesireFromAnotherAgent.WithIntentionWithPlan withPlan = new DesireFromAnotherAgent.WithIntentionWithPlan(
          desireForAgents,
          memory, getDecisionInDesire(desireForAgents.getDesireKey()),
          getDecisionInIntention(desireForAgents.getDesireKey()),
          commandsByKey.get(desireForAgents.getDesireKey()),
          getReactionInDesire(desireForAgents.getDesireKey()),
          getReactionInIntention(desireForAgents.getDesireKey()));
      return Optional.of(withPlan);
    }
    return Optional.empty();
  }

  @Override
  public boolean supportsDesireType(DesireKey desireKey) {
    return supportsType(desireKey);
  }
}
