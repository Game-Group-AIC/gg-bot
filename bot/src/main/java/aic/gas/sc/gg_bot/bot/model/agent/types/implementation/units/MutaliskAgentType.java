package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.units;

import static aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit.initAttackPlan;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import java.util.Collections;

public class MutaliskAgentType {

  public static final AgentTypeUnit MUTALISK = AgentTypeUnit.builder()
      .agentType(AgentTypes.MUTALISK)
      .initializationStrategy(type -> {
        initAttackPlan(type, DesiresKeys.HOLD_AIR, true);
        type.addConfiguration(DesiresKeys.SURROUNDING_UNITS_AND_LOCATION,
            AgentTypeUnit.beliefsAboutSurroundingUnitsAndLocation);
      })
      .desiresWithIntentionToReason(
          Collections.singleton(DesiresKeys.SURROUNDING_UNITS_AND_LOCATION))
      .build();
}
