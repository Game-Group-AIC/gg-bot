package aic.gas.sc.gg_bot.mas.model.metadata.agents;

import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithAbstractPlan;
import aic.gas.sc.gg_bot.mas.model.planing.OwnDesire;
import aic.gas.sc.gg_bot.mas.model.planing.OwnDesire.WithAbstractIntention;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * Concrete implementation of own desire with abstract plan formulation
 */
@Slf4j
public class OwnDesireWithAbstractIntentionFormulation extends
    DesireFormulation.WithAbstractPlan implements
    IOwnInternalDesireFormulation<WithAbstractIntention> {

  @Override
  public Optional<OwnDesire.WithAbstractIntention> formDesire(DesireKey key, WorkingMemory memory) {
    if (supportsDesireType(key)) {
      OwnDesire.WithAbstractIntention withAbstractIntention = new OwnDesire.WithAbstractIntention(
          key,
          memory, getDecisionInDesire(key), getDecisionInIntention(key),
          desiresForOthersByKey.get(key), desiresWithAbstractIntentionByKey.get(key),
          desiresWithIntentionToActByKey.get(key),
          desiresWithIntentionToReasonByKey.get(key),
          getReactionInDesire(key), getReactionInIntention(key));
      return Optional.of(withAbstractIntention);
    }
    return Optional.empty();
  }

  @Override
  public boolean supportsDesireType(DesireKey desireKey) {
    return supportsType(desireKey);
  }

  /**
   * Concrete implementation of own desire with abstract plan formulation and possibility to create
   * instance based on parent
   */
  public static class Stacked extends OwnDesireWithAbstractIntentionFormulation implements
      IOwnInternalDesireFormulationStacked<WithAbstractIntention> {

    private final Map<DesireKey, OwnDesireWithAbstractIntentionFormulation> stack = new HashMap<>();

    @Override
    public Optional<OwnDesire.WithAbstractIntention> formDesire(DesireKey parentKey, DesireKey key,
        WorkingMemory memory, DesireParameters parentsDesireParameters) {
      OwnDesireWithAbstractIntentionFormulation formulation = stack.get(parentKey);
      if (formulation != null) {
        if (formulation.supportsDesireType(key)) {
          OwnDesire.WithAbstractIntention withAbstractIntention = new OwnDesire.WithAbstractIntention(
              key,
              memory, formulation.getDecisionInDesire(key), formulation.getDecisionInIntention(key),
              formulation.desiresForOthersByKey.get(key),
              formulation.desiresWithAbstractIntentionByKey.get(key),
              formulation.desiresWithIntentionToActByKey.get(key),
              formulation.desiresWithIntentionToReasonByKey.get(key), parentsDesireParameters,
              formulation.getReactionInDesire(key), formulation.getReactionInIntention(key));
          return Optional.of(withAbstractIntention);
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
        ConfigurationWithAbstractPlan configuration) {
      OwnDesireWithAbstractIntentionFormulation formulation = stack
          .computeIfAbsent(parent, desireKey -> new OwnDesireWithAbstractIntentionFormulation());
      formulation.addDesireFormulationConfiguration(key, configuration);
    }
  }

}
