package aic.gas.sc.gg_bot.mas.model.metadata.agents;

import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithCommand;
import aic.gas.sc.gg_bot.mas.model.planing.IntentionCommand.OwnActing;
import aic.gas.sc.gg_bot.mas.model.planing.OwnDesire;
import aic.gas.sc.gg_bot.mas.model.planing.OwnDesire.Acting;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand.Own;
import aic.gas.sc.gg_bot.mas.model.planing.command.ICommandFormulationStrategy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * Concrete implementation of own desire with acting command formulation
 */
@Slf4j
public class OwnDesireWithIntentionWithActingCommandFormulation extends
    DesireFormulation.WithCommand<ICommandFormulationStrategy<Own, OwnActing>> implements
    IOwnInternalDesireFormulation<Acting> {

  @Override
  public Optional<OwnDesire.Acting> formDesire(DesireKey key, WorkingMemory memory) {
    if (supportsDesireType(key)) {
      OwnDesire.Acting acting = new OwnDesire.Acting(key,
          memory, getDecisionInDesire(key), getDecisionInIntention(key), commandsByKey.get(key),
          getReactionInDesire(key), getReactionInIntention(key));
      return Optional.of(acting);
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
  public static class Stacked extends OwnDesireWithIntentionWithActingCommandFormulation implements
      IOwnInternalDesireFormulationStacked<Acting> {

    private final Map<DesireKey, OwnDesireWithIntentionWithActingCommandFormulation> stack = new HashMap<>();

    @Override
    public Optional<OwnDesire.Acting> formDesire(DesireKey parentKey, DesireKey key,
        WorkingMemory memory, DesireParameters parentsDesireParameters) {
      OwnDesireWithIntentionWithActingCommandFormulation formulation = stack.get(parentKey);
      if (formulation != null) {
        if (formulation.supportsDesireType(key)) {
          OwnDesire.Acting acting = new OwnDesire.Acting(key,
              memory, formulation.getDecisionInDesire(key), formulation.getDecisionInIntention(key),
              formulation.commandsByKey.get(key), parentsDesireParameters,
              formulation.getReactionInDesire(key), formulation.getReactionInIntention(key));
          return Optional.of(acting);
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
        ConfigurationWithCommand<ICommandFormulationStrategy<Own, OwnActing>> configuration) {
      OwnDesireWithIntentionWithActingCommandFormulation formulation = stack.computeIfAbsent(parent,
          desireKey -> new OwnDesireWithIntentionWithActingCommandFormulation());
      formulation.addDesireFormulationConfiguration(key, configuration);
    }

  }
}
