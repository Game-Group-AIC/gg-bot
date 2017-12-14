package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.buildings;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_MINERALS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BEING_CONSTRUCT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.REPRESENTS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.LAIR_TYPE;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithCommand;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class HatcheryAgentType {
  public static final AgentTypeUnit HATCHERY = AgentTypeUnit.builder()
      .agentTypeID(AgentTypes.HATCHERY)
      .initializationStrategy(type -> {
        type.addConfiguration(
            DesiresKeys.UPDATE_BELIEFS_ABOUT_CONSTRUCTION, AgentTypeUnit.beliefsAboutConstruction);

        //upgrade to lair
        ConfigurationWithCommand.WithActingCommandDesiredByOtherAgent upgradeToLair = ConfigurationWithCommand.
            WithActingCommandDesiredByOtherAgent.builder()
            .commandCreationStrategy(intention -> new ActCommand.DesiredByAnotherAgent(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                return intention.returnFactValueForGivenKey(IS_UNIT).get().morph(LAIR_TYPE);
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_MINERALS) >= LAIR_TYPE.getMineralPrice()
                        && dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_GAS) >= LAIR_TYPE.getGasPrice()
                        //is on start position or there is no base on start position
                        && (memory.returnFactValueForGivenKey(
                        REPRESENTS_UNIT).get().getNearestBaseLocation().get().isStartLocation()
                        || memory.getReadOnlyMemoriesForAgentType(AgentTypes.HATCHERY)
                        .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                            REPRESENTS_UNIT).get().getNearestBaseLocation().get())
                        .noneMatch(ABaseLocationWrapper::isStartLocation))
                )
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Arrays.asList(COUNT_OF_MINERALS, COUNT_OF_GAS)))
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> false)
                .build())
            .build();
        type.addConfiguration(DesiresKeys.UPGRADE_TO_LAIR, upgradeToLair);
      })
      .usingTypesForFacts(new HashSet<>(Arrays.asList(IS_BEING_CONSTRUCT)))
      .desiresWithIntentionToReason(new HashSet<>(Collections.singletonList(
          DesiresKeys.UPDATE_BELIEFS_ABOUT_CONSTRUCTION)))
      .build();
}
