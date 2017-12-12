package aic.gas.sc.gg_bot.abstract_bot.model.bot;

import aic.gas.mas.model.metadata.AgentTypeID;
import aic.gas.mas.model.metadata.DesireKeyID;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;

/**
 * Metadata - combinations of agent and its learnt decisions
 */
public class DecisionCombinations {

  public static final Map<AgentTypeID, Set<DesireKeyID>> decisionsToLoad = ImmutableMap.of(
      AgentTypes.BASE_LOCATION, ImmutableSet
          .of(DesireKeys.BUILD_CREEP_COLONY, DesireKeys.BUILD_SPORE_COLONY,
              DesireKeys.BUILD_SUNKEN_COLONY, DesireKeys.HOLD_AIR, DesireKeys.HOLD_GROUND),
      AgentTypes.BUILDING_ORDER_MANAGER, ImmutableSet
          .of(DesireKeys.ENABLE_AIR, DesireKeys.ENABLE_GROUND_MELEE,
              DesireKeys.ENABLE_GROUND_RANGED, DesireKeys.ENABLE_STATIC_ANTI_AIR,
              DesireKeys.UPGRADE_TO_LAIR),
      AgentTypes.ECO_MANAGER, ImmutableSet
          .of(DesireKeys.BUILD_EXTRACTOR, DesireKeys.BUILD_WORKER, DesireKeys.EXPAND,
              DesireKeys.INCREASE_CAPACITY),
      AgentTypes.UNIT_ORDER_MANAGER, ImmutableSet
          .of(DesireKeys.BOOST_AIR, DesireKeys.BOOST_GROUND_MELEE, DesireKeys.BOOST_GROUND_RANGED)
  );
}
