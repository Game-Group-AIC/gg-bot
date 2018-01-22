package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.units;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import java.util.Collections;

public class EggAgentType {

  public static final AgentTypeUnit EGG = AgentTypeUnit.builder()
      .agentTypeID(AgentTypes.EGG)
      .usingTypesForFacts(Collections.singleton(IS_MORPHING_TO))
      .initializationStrategy(type -> {
        //reason about morphing
        type.addConfiguration(DesiresKeys.MORPHING_TO, AgentTypeUnit.beliefsAboutMorphing);
      })
      .desiresWithIntentionToReason(Collections.singleton(DesiresKeys.MORPHING_TO))
      .build();
}
