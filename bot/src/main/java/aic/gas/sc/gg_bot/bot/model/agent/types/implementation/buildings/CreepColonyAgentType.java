package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.buildings;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_MINERALS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_MOVE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BEING_CONSTRUCTED;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.REPRESENTS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.SPORE_COLONY_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.SUNKEN_COLONY_TYPE;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitOfPlayer;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithCommand;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class CreepColonyAgentType {

  public static final AgentTypeUnit CREEP_COLONY = AgentTypeUnit.builder()
      .agentTypeID(AgentTypes.CREEP_COLONY)
      .initializationStrategy(type -> {

        type.addConfiguration(
            DesiresKeys.UPDATE_BELIEFS_ABOUT_CONSTRUCTION, AgentTypeUnit.beliefsAboutConstruction);

        //upgrade to sunken
        ConfigurationWithCommand.WithActingCommandDesiredByOtherAgent upgradeToSunken = ConfigurationWithCommand.
            WithActingCommandDesiredByOtherAgent.builder()
            .commandCreationStrategy(intention -> new ActCommand.DesiredByAnotherAgent(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                return intention.returnFactValueForGivenKey(IS_UNIT).get()
                    .morph(SUNKEN_COLONY_TYPE);
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> {
                      if (dataForDecision.madeDecisionToAny()) {
                        return false;
                      }
                      AUnitOfPlayer me = memory.returnFactValueForGivenKey(REPRESENTS_UNIT).get();
                      if (!me.getNearestBaseLocation().isPresent()) {
                        return false;
                      }
                      return dataForDecision.returnFactValueForGivenKey(BASE_TO_MOVE).get().equals(
                          me.getNearestBaseLocation().orElse(null))
                          && dataForDecision.getFeatureValueGlobalBeliefs(
                          COUNT_OF_MINERALS) >= SUNKEN_COLONY_TYPE.getMineralPrice()
                          && dataForDecision.getFeatureValueGlobalBeliefs(
                          COUNT_OF_GAS) >= SUNKEN_COLONY_TYPE.getGasPrice();
                    }
                )
                .desiresToConsider(new HashSet<>(Arrays.asList(DesiresKeys.MORPH_TO_SPORE_COLONY,
                    DesiresKeys.MORPH_TO_SUNKEN_COLONY)))
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Arrays.asList(COUNT_OF_MINERALS, COUNT_OF_GAS)))
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> false)
                .build())
            .build();
        type.addConfiguration(DesiresKeys.MORPH_TO_SUNKEN_COLONY, upgradeToSunken);

        //upgrade to spore colony
        ConfigurationWithCommand.WithActingCommandDesiredByOtherAgent upgradeToSporeColony = ConfigurationWithCommand.
            WithActingCommandDesiredByOtherAgent.builder()
            .commandCreationStrategy(intention -> new ActCommand.DesiredByAnotherAgent(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                return intention.returnFactValueForGivenKey(IS_UNIT).get().morph(SPORE_COLONY_TYPE);
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> {
                      if (dataForDecision.madeDecisionToAny()) {
                        return false;
                      }
                      AUnitOfPlayer me = memory.returnFactValueForGivenKey(REPRESENTS_UNIT).get();
                      if (!me.getNearestBaseLocation().isPresent()) {
                        return false;
                      }
                      return dataForDecision.returnFactValueForGivenKey(BASE_TO_MOVE).get().equals(
                          me.getNearestBaseLocation().orElse(null))
                          && dataForDecision.getFeatureValueGlobalBeliefs(
                          COUNT_OF_MINERALS) >= SPORE_COLONY_TYPE.getMineralPrice()
                          && dataForDecision.getFeatureValueGlobalBeliefs(
                          COUNT_OF_GAS) >= SPORE_COLONY_TYPE.getGasPrice();
                    }
                )
                .desiresToConsider(new HashSet<>(Arrays.asList(DesiresKeys.MORPH_TO_SPORE_COLONY,
                    DesiresKeys.MORPH_TO_SUNKEN_COLONY)))
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Arrays.asList(COUNT_OF_MINERALS, COUNT_OF_GAS)))
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> false)
                .build())
            .build();
        type.addConfiguration(DesiresKeys.MORPH_TO_SPORE_COLONY, upgradeToSporeColony);

      })
      .usingTypesForFacts(new HashSet<>(Arrays.asList(IS_BEING_CONSTRUCTED)))
      .desiresWithIntentionToReason(new HashSet<>(Collections.singletonList(
          DesiresKeys.UPDATE_BELIEFS_ABOUT_CONSTRUCTION)))
      .build();
}
