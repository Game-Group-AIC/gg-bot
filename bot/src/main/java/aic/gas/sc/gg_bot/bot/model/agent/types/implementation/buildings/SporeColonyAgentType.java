package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.buildings;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import java.util.Collections;

public class SporeColonyAgentType {

  public static final AgentTypeUnit SPORE_COLONY = AgentTypeUnit.builder()
      .agentType(AgentTypes.SPORE_COLONY)
      .initializationStrategy(
          type -> type.addConfiguration(DesiresKeys.UPDATE_BELIEFS_ABOUT_CONSTRUCTION,
              AgentTypeUnit.beliefsAboutMorphing))
      .usingTypesForFacts(Collections.singleton(IS_MORPHING_TO))
      .desiresWithIntentionToReason(
          Collections.singleton(DesiresKeys.UPDATE_BELIEFS_ABOUT_CONSTRUCTION))
      .build();
}
