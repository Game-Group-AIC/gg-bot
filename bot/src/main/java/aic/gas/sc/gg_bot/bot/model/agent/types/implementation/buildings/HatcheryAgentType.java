package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.buildings;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.LAIR_TYPE;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithCommand;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import java.util.Collections;

public class HatcheryAgentType {

  public static final AgentTypeUnit HATCHERY = AgentTypeUnit.builder()
      .agentType(AgentTypes.HATCHERY)
      .initializationStrategy(type -> {
        type.addConfiguration(
            DesiresKeys.UPDATE_BELIEFS_ABOUT_CONSTRUCTION, AgentTypeUnit.beliefsAboutMorphing);

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
                .decisionStrategy((dataForDecision, memory) -> !dataForDecision.madeDecisionToAny())
                .desiresToConsider(Collections.singleton(DesiresKeys.UPGRADE_TO_LAIR))
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> false)
                .build())
            .build();
        type.addConfiguration(DesiresKeys.UPGRADE_TO_LAIR, upgradeToLair);
      })
      .usingTypesForFacts(Collections.singleton(IS_MORPHING_TO))
      .desiresWithIntentionToReason(
          Collections.singleton(DesiresKeys.UPDATE_BELIEFS_ABOUT_CONSTRUCTION))
      .build();
}
