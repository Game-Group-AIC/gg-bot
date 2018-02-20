package aic.gas.sc.gg_bot.abstract_bot.model.bot;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.*;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureContainerHeader;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * Enumeration of all feature container headers as static classes
 */
@Slf4j
public class FeatureContainerHeaders {

  //ECO manager

  public static final FeatureContainerHeader BUILD_EXTRACTOR = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(
          Stream.of(
              FactConverters.COUNT_OF_BASES,
              FactConverters.IS_POOL_BUILT,
              FactConverters.COUNT_OF_BASES_WITHOUT_EXTRACTORS,
              FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
              FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE
          )
              .collect(Collectors.toSet()))
      .convertersForFactsForGlobalBeliefs(Collections.singleton(COUNT_OF_INCOMPLETE_EXTRACTORS))
      .build();

  public static final FeatureContainerHeader INCREASE_CAPACITY = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(
          Collections.singleton(
              FactConverters.IS_POOL_BUILT)
      )
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.FREE_SUPPLY,
          FactConverters.GAME_PHASE
      )
          .collect(Collectors.toSet()))
      .convertersForFactsForGlobalBeliefs(Collections.singleton(COUNT_OF_INCOMPLETE_OVERLORDS))
      .build();

  public static final FeatureContainerHeader BUILD_WORKER = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.GAME_PHASE,
          FactConverters.IS_POOL_BUILT,
          FactConverters.FORCE_SUPPLY_RATIO,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE
      )
          .collect(Collectors.toSet()))
      .convertersForFactsForGlobalBeliefs(Collections.singleton(COUNT_OF_INCOMPLETE_DRONES))
      .build();

  public static final FeatureContainerHeader EXPAND = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.FORCE_SUPPLY_RATIO,
          FactConverters.DIFFERENCE_IN_BASES,
          FactConverters.COUNT_OF_MINERALS,
          FactConverters.GAME_PHASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE
      )
          .collect(Collectors.toSet()))
      .convertersForFactsForGlobalBeliefs(Collections.singleton(COUNT_OF_INCOMPLETE_HATCHERIES))
      .build();
  //Build order manager

  public static final FeatureContainerHeader ENABLE_GROUND_MELEE = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.FORCE_SUPPLY_RATIO,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE
      )
          .collect(Collectors.toSet()))
      .convertersForFactSetsForGlobalBeliefsByAgentType(Collections
          .singleton(
              FactConverters.CAN_ENEMY_PRODUCE_MILITARY_UNITS)
      )
      .build();

  public static final FeatureContainerHeader UPGRADE_TO_LAIR = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(
          Stream.of(
              FactConverters.HAS_AT_LEAST_TWO_BASES,
              FactConverters.FORCE_SUPPLY_RATIO
          )
              .collect(Collectors.toSet()))
      .build();

  public static final FeatureContainerHeader ENABLE_AIR = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(
          Stream.of(
              FactConverters.HAS_AT_LEAST_TWO_BASES,
              FactConverters.FORCE_SUPPLY_RATIO
          )
              .collect(Collectors.toSet()))
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.SUM_OF_ENEMY_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG
      )
          .collect(Collectors.toSet()))
      .build();

  public static final FeatureContainerHeader ENABLE_GROUND_RANGED = FeatureContainerHeader
      .builder()
      .convertersForFactSetsForGlobalBeliefsByAgentType(
          Stream.of(
              FactConverters.ENEMY_RANGED_VS_MELEE_DAMAGE,
              FactConverters.OUR_RANGED_VS_MELEE_DAMAGE,
              FactConverters.SUM_OF_ENEMY_AIR_HP,
              FactConverters.HAS_AT_LEAST_10_ARMY_SUPPLY
          )
              .collect(Collectors.toSet()))
      .build();

  public static final FeatureContainerHeader ENABLE_STATIC_ANTI_AIR = FeatureContainerHeader
      .builder()
      .convertersForFactSetsForGlobalBeliefsByAgentType(
          Collections.singleton(
              FactConverters.HAS_AT_LEAST_10_ARMY_SUPPLY)
      )
      .convertersForFactsForGlobalBeliefsByAgentType(
          Stream.of(
              FactConverters.HAS_AT_LEAST_TWO_BASES,
              FactConverters.FORCE_SUPPLY_RATIO
          )
              .collect(Collectors.toSet()))
      .build();
  //Unit order manager

  public static final FeatureContainerHeader BOOST_AIR = FeatureContainerHeader.builder()
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream
          .of(
              FactConverters.SUM_OF_ENEMY_AIR_DMG,
              FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG,
              FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
              FactConverters.HAS_AT_LEAST_10_ARMY_SUPPLY
          )
          .collect(Collectors.toSet()))
      .convertersForFactsForGlobalBeliefsByAgentType(Stream
          .of(
              FactConverters.DIFFERENCE_IN_BASES,
              FactConverters.HAS_AT_LEAST_TWO_BASES,
              FactConverters.GAME_PHASE,
              FactConverters.ENEMY_BASES_UNPROTECTED_AGAINST_AIR,
              FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE
          )
          .collect(Collectors.toSet()))
      .convertersForFactsForGlobalBeliefs(Collections.singleton(COUNT_OF_INCOMPLETE_AIRS))
      .build();

  public static final FeatureContainerHeader BOOST_GROUND_MELEE = FeatureContainerHeader
      .builder()
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
          FactConverters.ENEMY_RANGED_VS_MELEE_DAMAGE,
          FactConverters.OUR_RANGED_VS_MELEE_DAMAGE,
          FactConverters.SUM_OF_ENEMY_AIR_UNITS
      )
          .collect(Collectors.toSet()))
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.DIFFERENCE_IN_BASES,
          FactConverters.ENEMY_BASES_UNPROTECTED_AGAINST_GROUND,
          FactConverters.FORCE_SUPPLY_RATIO
      )
          .collect(Collectors.toSet()))
      .convertersForFactsForGlobalBeliefs(Collections.singleton(COUNT_OF_INCOMPLETE_MELEE))
      .build();

  public static final FeatureContainerHeader BOOST_GROUND_RANGED = FeatureContainerHeader
      .builder()
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream
          .of(
              FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
              FactConverters.SUM_OF_ENEMY_AIR_UNITS,
              FactConverters.ENEMY_RANGED_VS_MELEE_DAMAGE,
              FactConverters.OUR_RANGED_VS_MELEE_DAMAGE
          )
          .collect(Collectors.toSet()))
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.DIFFERENCE_IN_BASES,
          FactConverters.FORCE_SUPPLY_RATIO
      )
          .collect(Collectors.toSet()))
      .convertersForFactsForGlobalBeliefs(Collections.singleton(COUNT_OF_INCOMPLETE_RANGED))
      .build();
  //BASE

  public static final FeatureContainerHeader HOLD_AIR = FeatureContainerHeader.builder()
      .convertersForFacts(Stream.of(
          FactConverters.DMG_AIR_CAN_INFLICT_TO_GROUND_VS_SUFFER,
          FactConverters.DMG_AIR_CAN_INFLICT_TO_AIR_VS_SUFFER,
          FactConverters.RATIO_GLOBAL_AIR_VS_ANTI_AIR_ON_BASE,
          FactConverters.IS_BASE,
          FactConverters.IS_ENEMY_BASE,
          FactConverters.IS_START_LOCATION,
          FactConverters.AIR_DISTANCE_TO_ENEMY_CLOSEST_BASE,
          FactConverters.AIR_DISTANCE_TO_OUR_CLOSEST_BASE
      )
          .collect(Collectors.toSet()))
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES
      )
          .collect(Collectors.toSet()))
      .build();

  public static final FeatureContainerHeader HOLD_GROUND = FeatureContainerHeader
      .builder()
      .convertersForFacts(Stream.of(
          FactConverters.DMG_GROUND_CAN_INFLICT_TO_GROUND_VS_SUFFER,
          FactConverters.DMG_GROUND_CAN_INFLICT_TO_AIR_VS_SUFFER,
          FactConverters.IS_BASE,
          FactConverters.IS_ENEMY_BASE,
          FactConverters.IS_START_LOCATION,
          FactConverters.GROUND_DISTANCE_TO_OUR_CLOSEST_BASE,
          FactConverters.GROUND_DISTANCE_TO_ENEMY_CLOSEST_BASE,
          FactConverters.RATIO_GLOBAL_GROUND_VS_ANTI_GROUND_ON_BASE
      )
          .collect(Collectors.toSet()))
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES
      )
          .collect(Collectors.toSet()))
      .build();

  public static final FeatureContainerHeader DEFENSE = FeatureContainerHeader.builder()
      .convertersForFacts(Stream.of(
          FactConverters.IS_BASE,
          FactConverters.IS_START_LOCATION,
          FactConverters.AIR_DISTANCE_TO_ENEMY_CLOSEST_BASE,
          FactConverters.GROUND_DISTANCE_TO_ENEMY_CLOSEST_BASE
      )
          .collect(Collectors.toSet()))
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.COUNT_OF_BASES,
          FactConverters.FORCE_SUPPLY_RATIO
      )
          .collect(Collectors.toSet()))
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.SUM_OF_ENEMY_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_AIR_UNITS
      )
          .collect(Collectors.toSet()))
      .convertersForFactSets(Stream.of(
          FactConverters.COUNT_OF_CREEP_COLONIES_AT_BASE,
          FactConverters.COUNT_OF_SPORE_COLONIES_AT_BASE,
          FactConverters.COUNT_OF_SUNKEN_COLONIES_AT_BASE
      )
          .collect(Collectors.toSet()))
      .build();
  //TODO research lurkers
  //TODO (un)burrow lurker
  //TODO mutate lurkers
  //TODO research
  private static final Map<AgentTypeID, Map<DesireKeyID, FeatureContainerHeader>> ASSIGNMENT = new HashMap<>();

  static {
    //ECO manager
    Map<DesireKeyID, FeatureContainerHeader> ecomDesires = new HashMap<>();
    ecomDesires.put(DesireKeys.BUILD_EXTRACTOR, FeatureContainerHeaders.BUILD_EXTRACTOR);
    ecomDesires.put(DesireKeys.INCREASE_CAPACITY, FeatureContainerHeaders.INCREASE_CAPACITY);
    ecomDesires.put(DesireKeys.BUILD_WORKER, FeatureContainerHeaders.BUILD_WORKER);
    ecomDesires.put(DesireKeys.EXPAND, FeatureContainerHeaders.EXPAND);
    ASSIGNMENT.put(AgentTypes.ECO_MANAGER.getId(), ecomDesires);

    //Build order manager
    Map<DesireKeyID, FeatureContainerHeader> bmoDesires = new HashMap<>();
    bmoDesires.put(DesireKeys.ENABLE_GROUND_MELEE, FeatureContainerHeaders.ENABLE_GROUND_MELEE);
    bmoDesires.put(DesireKeys.UPGRADE_TO_LAIR, FeatureContainerHeaders.UPGRADE_TO_LAIR);
    bmoDesires.put(DesireKeys.ENABLE_AIR, FeatureContainerHeaders.ENABLE_AIR);
    bmoDesires.put(DesireKeys.ENABLE_GROUND_RANGED, FeatureContainerHeaders.ENABLE_GROUND_RANGED);
    bmoDesires.put(DesireKeys.ENABLE_STATIC_ANTI_AIR, FeatureContainerHeaders.ENABLE_STATIC_ANTI_AIR);
    ASSIGNMENT.put(AgentTypes.BUILDING_ORDER_MANAGER.getId(), bmoDesires);

    //Unit order manager
    Map<DesireKeyID, FeatureContainerHeader> umoDesires = new HashMap<>();
    umoDesires.put(DesireKeys.BOOST_AIR, FeatureContainerHeaders.BOOST_AIR);
    umoDesires.put(DesireKeys.BOOST_GROUND_MELEE, FeatureContainerHeaders.BOOST_GROUND_MELEE);
    umoDesires.put(DesireKeys.BOOST_GROUND_RANGED, FeatureContainerHeaders.BOOST_GROUND_RANGED);
    ASSIGNMENT.put(AgentTypes.UNIT_ORDER_MANAGER.getId(), umoDesires);

    //BASE
    Map<DesireKeyID, FeatureContainerHeader> baseDesires = new HashMap<>();
    baseDesires.put(DesireKeys.HOLD_AIR, FeatureContainerHeaders.HOLD_AIR);
    baseDesires.put(DesireKeys.HOLD_GROUND, FeatureContainerHeaders.HOLD_GROUND);
    baseDesires.put(DesireKeys.BUILD_CREEP_COLONY, FeatureContainerHeaders.DEFENSE);
    baseDesires.put(DesireKeys.BUILD_SUNKEN_COLONY, FeatureContainerHeaders.DEFENSE);
    baseDesires.put(DesireKeys.BUILD_SPORE_COLONY, FeatureContainerHeaders.DEFENSE);
    ASSIGNMENT.put(AgentTypes.BASE_LOCATION.getId(), baseDesires);
  }

  public static Optional<FeatureContainerHeader> getHeader(AgentTypeID agentType,
      DesireKeyID desireKey) {
    if (!ASSIGNMENT.containsKey(agentType)) {
      log.info("Agent: " + agentType.getName() + " is not contained.");
      return Optional.empty();
    }
    Map<DesireKeyID, FeatureContainerHeader> map = ASSIGNMENT.get(agentType);
    if (!map.containsKey(desireKey)) {
      log.info("Desire: " + desireKey.getName() + " is not contained.");
      return Optional.empty();
    }
    return Optional.ofNullable(map.get(desireKey));
  }
}
