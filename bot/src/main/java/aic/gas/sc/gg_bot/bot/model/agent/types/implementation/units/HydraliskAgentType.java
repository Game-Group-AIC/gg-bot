package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.units;


import static aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit.initAttackPlan;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import java.util.Collections;
import java.util.HashSet;

public class HydraliskAgentType {

  public static final AgentTypeUnit HYDRALISK = AgentTypeUnit.builder()
      .agentTypeID(AgentTypes.HYDRALISK)
      .initializationStrategy(type -> {
        initAttackPlan(type, DesiresKeys.HOLD_GROUND, false);
        initAttackPlan(type, DesiresKeys.DEFEND, false);
        type.addConfiguration(
            DesiresKeys.SURROUNDING_UNITS_AND_LOCATION,
            AgentTypeUnit.beliefsAboutSurroundingUnitsAndLocation);
      })
      .desiresWithIntentionToReason(new HashSet<>(Collections.singletonList(
          DesiresKeys.SURROUNDING_UNITS_AND_LOCATION)))
      .build();
}
