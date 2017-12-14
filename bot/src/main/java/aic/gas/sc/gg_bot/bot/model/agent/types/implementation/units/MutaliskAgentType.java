package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.units;

import static aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit.initAttackPlan;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import java.util.Collections;
import java.util.HashSet;

public class MutaliskAgentType {

  public static final AgentTypeUnit MUTALISK = AgentTypeUnit.builder()
      .agentTypeID(AgentTypes.MUTALISK)
      .initializationStrategy(type -> {
        initAttackPlan(type, DesiresKeys.HOLD_AIR, true);
        initAttackPlan(type, DesiresKeys.DEFEND, true);
        type.addConfiguration(
            DesiresKeys.SURROUNDING_UNITS_AND_LOCATION,
            AgentTypeUnit.beliefsAboutSurroundingUnitsAndLocation);
      })
      .desiresWithIntentionToReason(new HashSet<>(Collections.singletonList(
          DesiresKeys.SURROUNDING_UNITS_AND_LOCATION)))
      .build();
}
