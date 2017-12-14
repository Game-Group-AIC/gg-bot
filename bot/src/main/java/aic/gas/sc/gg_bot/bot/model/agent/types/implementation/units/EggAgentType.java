package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.units;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import java.util.Collections;
import java.util.HashSet;

public class EggAgentType {

  public static final AgentTypeUnit EGG = AgentTypeUnit.builder()
      .agentTypeID(AgentTypes.EGG)
      .usingTypesForFacts(new HashSet<>(Collections.singletonList(IS_MORPHING_TO)))
      .initializationStrategy(type -> {

        //reason about morphing
        type.addConfiguration(DesiresKeys.MORPHING_TO, AgentTypeUnit.beliefsAboutMorphing);

      })
      .desiresWithIntentionToReason(
          new HashSet<>(Collections.singletonList(DesiresKeys.MORPHING_TO)))
      .build();
}
