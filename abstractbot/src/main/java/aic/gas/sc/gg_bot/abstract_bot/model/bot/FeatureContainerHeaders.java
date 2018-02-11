package aic.gas.sc.gg_bot.abstract_bot.model.bot;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_INCOMPLETE_AIRS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_INCOMPLETE_DRONES;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_INCOMPLETE_EXTRACTORS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_INCOMPLETE_HATCHERIES;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_INCOMPLETE_MELEE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_INCOMPLETE_OVERLORDS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_INCOMPLETE_RANGED;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureContainerHeader;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * Enumeration of all feature container headers as static classes
 */
public class FeatureContainerHeaders {

  //ECO manager
  public static final FeatureContainerHeader BUILDING_EXTRACTOR = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(
          Stream.of(FactConverters.COUNT_OF_BASES, FactConverters.IS_POOL_BUILT)
              .collect(Collectors.toSet()))
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.COUNT_OF_BASES_WITHOUT_EXTRACTORS,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE)
          .collect(Collectors.toSet()))
//      .convertersForFactsForGlobalBeliefs(Collections.singleton(COUNT_OF_INCOMPLETE_EXTRACTORS))
      .build();

  public static final FeatureContainerHeader INCREASING_CAPACITY = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(
          Collections.singleton(FactConverters.IS_POOL_BUILT))
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.FREE_SUPPLY, FactConverters.GAME_PHASE)
          .collect(Collectors.toSet()))
//      .convertersForFactsForGlobalBeliefs(Collections.singleton(COUNT_OF_INCOMPLETE_OVERLORDS))
      .build();

  public static final FeatureContainerHeader TRAINING_WORKER = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(FactConverters.GAME_PHASE,
          FactConverters.IS_POOL_BUILT, FactConverters.FORCE_SUPPLY_RATIO)
          .collect(Collectors.toSet()))
      .convertersForFactSetsForGlobalBeliefsByAgentType(Collections.singleton(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE))
//      .convertersForFactsForGlobalBeliefs(Collections.singleton(COUNT_OF_INCOMPLETE_DRONES))
      .build();

  public static final FeatureContainerHeader EXPANDING = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.FORCE_SUPPLY_RATIO, FactConverters.DIFFERENCE_IN_BASES,
          FactConverters.COUNT_OF_MINERALS, FactConverters.GAME_PHASE)
          .collect(Collectors.toSet()))
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE)
          .collect(Collectors.toSet()))
//      .convertersForFactsForGlobalBeliefs(Collections.singleton(COUNT_OF_INCOMPLETE_HATCHERIES))
      .build();

  //Build order manager
  public static final FeatureContainerHeader BUILDING_POOL = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(
          Collections.singleton(FactConverters.FORCE_SUPPLY_RATIO))
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream
          .of(FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
              FactConverters.CAN_ENEMY_PRODUCE_MILITARY_UNITS)
          .collect(Collectors.toSet()))
      .build();

  public static final FeatureContainerHeader UPGRADING_TO_LAIR = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(
          Stream.of(FactConverters.HAS_AT_LEAST_TWO_BASES, FactConverters.FORCE_SUPPLY_RATIO)
              .collect(Collectors.toSet()))
      .build();

  public static final FeatureContainerHeader BUILDING_SPIRE = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(
          Stream.of(FactConverters.HAS_AT_LEAST_TWO_BASES, FactConverters.FORCE_SUPPLY_RATIO)
              .collect(Collectors.toSet()))
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.SUM_OF_ENEMY_AIR_DMG, FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG)
          .collect(Collectors.toSet()))
      .build();

  public static final FeatureContainerHeader BUILDING_HYDRALISK_DEN = FeatureContainerHeader
      .builder()
      .convertersForFactSetsForGlobalBeliefsByAgentType(
          Stream.of(FactConverters.ENEMY_RANGED_VS_MELEE_DAMAGE,
              FactConverters.OUR_RANGED_VS_MELEE_DAMAGE, FactConverters.SUM_OF_ENEMY_AIR_HP,
              FactConverters.HAS_AT_LEAST_10_ARMY_SUPPLY)
              .collect(Collectors.toSet()))
      .build();

  public static final FeatureContainerHeader BUILDING_EVOLUTION_CHAMBER = FeatureContainerHeader
      .builder()
      .convertersForFactSetsForGlobalBeliefsByAgentType(
          Collections.singleton(FactConverters.HAS_AT_LEAST_10_ARMY_SUPPLY))
      .convertersForFactsForGlobalBeliefsByAgentType(
          Stream.of(FactConverters.HAS_AT_LEAST_TWO_BASES, FactConverters.FORCE_SUPPLY_RATIO)
              .collect(Collectors.toSet()))
      .build();

  //Unit order manager
  public static final FeatureContainerHeader BOOSTING_AIR = FeatureContainerHeader.builder()
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream
          .of(FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
              FactConverters.SUM_OF_ENEMY_AIR_DMG, FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG,
              FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
              FactConverters.HAS_AT_LEAST_10_ARMY_SUPPLY)
          .collect(Collectors.toSet()))
      .convertersForFactsForGlobalBeliefsByAgentType(Stream
          .of(FactConverters.DIFFERENCE_IN_BASES, FactConverters.HAS_AT_LEAST_TWO_BASES,
              FactConverters.GAME_PHASE, FactConverters.ENEMY_BASES_UNPROTECTED_AGAINST_AIR)
          .collect(Collectors.toSet()))
//      .convertersForFactsForGlobalBeliefs(Collections.singleton(COUNT_OF_INCOMPLETE_AIRS))
      .build();

  public static final FeatureContainerHeader BOOSTING_GROUND_MELEE = FeatureContainerHeader
      .builder()
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
          FactConverters.ENEMY_RANGED_VS_MELEE_DAMAGE,
          FactConverters.OUR_RANGED_VS_MELEE_DAMAGE, FactConverters.SUM_OF_ENEMY_AIR_UNITS)
          .collect(Collectors.toSet()))
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(FactConverters.DIFFERENCE_IN_BASES,
          FactConverters.ENEMY_BASES_UNPROTECTED_AGAINST_GROUND, FactConverters.FORCE_SUPPLY_RATIO)
          .collect(Collectors.toSet()))
//      .convertersForFactsForGlobalBeliefs(Collections.singleton(COUNT_OF_INCOMPLETE_MELEE))
      .build();

  public static final FeatureContainerHeader BOOSTING_GROUND_RANGED = FeatureContainerHeader
      .builder()
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream
          .of(FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG, FactConverters.SUM_OF_ENEMY_AIR_UNITS,
              FactConverters.ENEMY_RANGED_VS_MELEE_DAMAGE,
              FactConverters.OUR_RANGED_VS_MELEE_DAMAGE)
          .collect(Collectors.toSet()))
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(FactConverters.DIFFERENCE_IN_BASES,
          FactConverters.FORCE_SUPPLY_RATIO)
          .collect(Collectors.toSet()))
//      .convertersForFactsForGlobalBeliefs(Collections.singleton(COUNT_OF_INCOMPLETE_RANGED))
      .build();

  //BASE
  public static final FeatureContainerHeader HOLDING_BY_AIR_UNITS = FeatureContainerHeader.builder()
      .convertersForFacts(Stream.of(FactConverters.DMG_AIR_CAN_INFLICT_TO_GROUND_VS_SUFFER,
          FactConverters.DMG_AIR_CAN_INFLICT_TO_AIR_VS_SUFFER,
          FactConverters.RATIO_GLOBAL_AIR_VS_ANTI_AIR_ON_BASE, FactConverters.IS_BASE,
          FactConverters.IS_ENEMY_BASE, FactConverters.IS_START_LOCATION,
          FactConverters.AIR_DISTANCE_TO_ENEMY_CLOSEST_BASE,
          FactConverters.AIR_DISTANCE_TO_OUR_CLOSEST_BASE)
          .collect(Collectors.toSet()))
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES)
          .collect(Collectors.toSet()))
      .build();

  public static final FeatureContainerHeader HOLDING_BY_GROUND_UNITS = FeatureContainerHeader
      .builder()
      .convertersForFacts(Stream.of(FactConverters.DMG_GROUND_CAN_INFLICT_TO_GROUND_VS_SUFFER,
          FactConverters.DMG_GROUND_CAN_INFLICT_TO_AIR_VS_SUFFER, FactConverters.IS_BASE,
          FactConverters.IS_ENEMY_BASE, FactConverters.IS_START_LOCATION,
          FactConverters.GROUND_DISTANCE_TO_OUR_CLOSEST_BASE,
          FactConverters.GROUND_DISTANCE_TO_ENEMY_CLOSEST_BASE,
          FactConverters.RATIO_GLOBAL_GROUND_VS_ANTI_GROUND_ON_BASE)
          .collect(Collectors.toSet()))
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES)
          .collect(Collectors.toSet()))
      .build();

  public static final FeatureContainerHeader DEFENSE = FeatureContainerHeader.builder()
      .convertersForFacts(Stream.of(FactConverters.IS_BASE, FactConverters.IS_START_LOCATION,
          FactConverters.AIR_DISTANCE_TO_ENEMY_CLOSEST_BASE,
          FactConverters.GROUND_DISTANCE_TO_ENEMY_CLOSEST_BASE)
          .collect(Collectors.toSet()))
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(FactConverters.COUNT_OF_BASES,
          FactConverters.FORCE_SUPPLY_RATIO)
          .collect(Collectors.toSet()))
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.SUM_OF_ENEMY_GROUND_DMG, FactConverters.SUM_OF_ENEMY_AIR_UNITS)
          .collect(Collectors.toSet()))
      .convertersForFactSets(Stream.of(FactConverters.COUNT_OF_CREEP_COLONIES_AT_BASE,
          FactConverters.COUNT_OF_SPORE_COLONIES_AT_BASE,
          FactConverters.COUNT_OF_SUNKEN_COLONIES_AT_BASE)
          .collect(Collectors.toSet()))
      .build();

  //TODO research lurkers
  //TODO (un)burrow lurker
  //TODO mutate lurkers
  //TODO research

}
