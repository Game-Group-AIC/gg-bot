package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.buildings;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BEING_CONSTRUCTED;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class EvolutionChamberAgentType {

  public static final AgentTypeUnit EVOLUTION_CHAMBER = AgentTypeUnit.builder()
      .agentTypeID(AgentTypes.EVOLUTION_CHAMBER)
      .initializationStrategy(type -> {
        type.addConfiguration(
            DesiresKeys.UPDATE_BELIEFS_ABOUT_CONSTRUCTION, AgentTypeUnit.beliefsAboutConstruction);
      })
      .usingTypesForFacts(new HashSet<>(Collections.singletonList(IS_BEING_CONSTRUCTED)))
      .desiresWithIntentionToReason(new HashSet<>(Collections.singletonList(
          DesiresKeys.UPDATE_BELIEFS_ABOUT_CONSTRUCTION)))
      .build();
}
