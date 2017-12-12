package aic.gas.sc.gg_bot.mas.model.metadata.agents;

import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithCommand;
import aic.gas.sc.gg_bot.mas.model.planing.IntentionCommand;
import aic.gas.sc.gg_bot.mas.model.planing.OwnDesire;
import aic.gas.sc.gg_bot.mas.model.planing.command.CommandFormulationStrategy;
import aic.gas.sc.gg_bot.mas.model.planing.command.ReasoningCommand;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * Concrete implementation of own desire with reasoning command formulation
 */
@Slf4j
public class OwnDesireWithIntentionWithReasoningCommandFormulation extends
    DesireFormulation.WithCommand<CommandFormulationStrategy<ReasoningCommand, IntentionCommand.OwnReasoning>> implements
    OwnInternalDesireFormulation<OwnDesire.Reasoning> {

  @Override
  public Optional<OwnDesire.Reasoning> formDesire(DesireKey key, WorkingMemory memory) {
    if (supportsDesireType(key)) {
      OwnDesire.Reasoning reasoning = new OwnDesire.Reasoning(key,
          memory, getDecisionInDesire(key), getDecisionInIntention(key),
          commandsByKey.get(key),
          getReactionInDesire(key), getReactionInIntention(key));
      return Optional.of(reasoning);
    }
    return Optional.empty();
  }

  @Override
  public boolean supportsDesireType(DesireKey desireKey) {
    return supportsType(desireKey);
  }

  /**
   * Concrete implementation of own desire with intention with command formulation and possibility
   * to create instance based on parent
   */
  public static class Stacked extends
      OwnDesireWithIntentionWithReasoningCommandFormulation implements
      OwnInternalDesireFormulationStacked<OwnDesire.Reasoning> {

    private final Map<DesireKey, OwnDesireWithIntentionWithReasoningCommandFormulation> stack = new HashMap<>();

    @Override
    public Optional<OwnDesire.Reasoning> formDesire(DesireKey parentKey, DesireKey key,
        WorkingMemory memory, DesireParameters parentsDesireParameters) {
      OwnDesireWithIntentionWithReasoningCommandFormulation formulation = stack.get(parentKey);
      if (formulation != null) {
        if (formulation.supportsDesireType(key)) {
          OwnDesire.Reasoning reasoning = new OwnDesire.Reasoning(key,
              memory, formulation.getDecisionInDesire(key), formulation.getDecisionInIntention(key),
              formulation.commandsByKey.get(key), parentsDesireParameters,
              formulation.getReactionInDesire(key), formulation.getReactionInIntention(key));
          return Optional.of(reasoning);
        }
      }
      return formDesire(key, memory);
    }

    @Override
    public boolean supportsDesireType(DesireKey parent, DesireKey key) {
      if (stack.get(parent) == null || !stack.get(parent).supportsDesireType(key)) {
        log.error(parent.getName() + " is not associated with " + key.getName());
        return supportsType(key);
      }
      return true;
    }

    /**
     * Add configuration for desire
     */
    public void addDesireFormulationConfiguration(DesireKey parent, DesireKey key,
        ConfigurationWithCommand<CommandFormulationStrategy<ReasoningCommand, IntentionCommand.OwnReasoning>> configuration) {
      OwnDesireWithIntentionWithReasoningCommandFormulation formulation = stack
          .computeIfAbsent(parent,
              desireKey -> new OwnDesireWithIntentionWithReasoningCommandFormulation());
      formulation.addDesireFormulationConfiguration(key, configuration);
    }

  }

}
