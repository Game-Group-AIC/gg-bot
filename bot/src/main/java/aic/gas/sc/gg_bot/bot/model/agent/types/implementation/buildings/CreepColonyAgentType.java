package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.buildings;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_MOVE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.SPORE_COLONY_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.SUNKEN_COLONY_TYPE;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithCommand;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreepColonyAgentType {

  public static final AgentTypeUnit CREEP_COLONY = AgentTypeUnit.builder()
      .agentTypeID(AgentTypes.CREEP_COLONY)
      .initializationStrategy(type -> {

        type.addConfiguration(DesiresKeys.UPDATE_BELIEFS_ABOUT_CONSTRUCTION,
            AgentTypeUnit.beliefsAboutMorphing);

        //upgrade to sunken
        ConfigurationWithCommand.WithActingCommandDesiredByOtherAgent upgradeToSunken = ConfigurationWithCommand.
            WithActingCommandDesiredByOtherAgent.builder()
            .commandCreationStrategy(intention -> new ActCommand.DesiredByAnotherAgent(intention) {
              @Override
              public int getHash(WorkingMemory memory) {
                return Objects.hash("MORPH", SUNKEN_COLONY_TYPE);
              }

              @Override
              public boolean act(WorkingMemory memory) {
                return intention.returnFactValueForGivenKey(IS_UNIT).get()
                    .morph(SUNKEN_COLONY_TYPE);
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> BotFacade.RESOURCE_MANAGER
                    .canSpendResourcesOn(SUNKEN_COLONY_TYPE, dataForDecision.getOriginatorID())
                    && !dataForDecision.madeDecisionToAny()
                    && dataForDecision.returnFactValueForGivenKey(BASE_TO_MOVE).get()
                    .equals(memory.returnFactValueForGivenKey(IS_UNIT).get()
                        .getNearestBaseLocation().orElse(null)))
                .desiresToConsider(Stream.of(DesiresKeys.MORPH_TO_SPORE_COLONY,
                    DesiresKeys.MORPH_TO_SUNKEN_COLONY).collect(Collectors.toSet()))
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
              public int getHash(WorkingMemory memory) {
                return Objects.hash("MORPH", SPORE_COLONY_TYPE);
              }

              @Override
              public boolean act(WorkingMemory memory) {
                return intention.returnFactValueForGivenKey(IS_UNIT).get().morph(SPORE_COLONY_TYPE);
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> BotFacade.RESOURCE_MANAGER
                    .canSpendResourcesOn(SPORE_COLONY_TYPE, dataForDecision.getOriginatorID())
                    && !dataForDecision.madeDecisionToAny() && dataForDecision
                    .returnFactValueForGivenKey(BASE_TO_MOVE).get()
                    .equals(memory.returnFactValueForGivenKey(IS_UNIT).get()
                        .getNearestBaseLocation().orElse(null)))
                .desiresToConsider(Stream.of(DesiresKeys.MORPH_TO_SPORE_COLONY,
                    DesiresKeys.MORPH_TO_SUNKEN_COLONY).collect(Collectors.toSet()))
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> false)
                .build())
            .build();
        type.addConfiguration(DesiresKeys.MORPH_TO_SPORE_COLONY, upgradeToSporeColony);

      })
      .usingTypesForFacts(Collections.singleton(IS_MORPHING_TO))
      .desiresWithIntentionToReason(
          Collections.singleton(DesiresKeys.UPDATE_BELIEFS_ABOUT_CONSTRUCTION))
      .build();
}
