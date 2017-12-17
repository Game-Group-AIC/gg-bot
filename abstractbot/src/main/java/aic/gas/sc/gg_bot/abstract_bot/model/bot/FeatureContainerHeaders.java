package aic.gas.sc.gg_bot.abstract_bot.model.bot;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureContainerHeader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * Enumeration of all feature container headers as static classes
 */
public class FeatureContainerHeaders {

  //ECO manager
  public static final FeatureContainerHeader BUILDING_EXTRACTOR = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefs(new HashSet<>(Collections.singleton(
          FactConverters.COUNT_OF_WORKERS
      )))
      .convertersForFactsForGlobalBeliefsByAgentType(new HashSet<>(Collections.singletonList(
          FactConverters.COUNT_OF_BASES
      )))
      .convertersForFactSetsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
          FactConverters.COUNT_OF_EXTRACTORS,
          FactConverters.SUM_OF_ENEMY_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_AIR_HP,
          FactConverters.SUM_OF_ENEMY_GROUND_HP,
          FactConverters.SUM_OF_ENEMY_AIR_SHIELDS,
          FactConverters.SUM_OF_ENEMY_GROUND_SHIELDS,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_ENEMY_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_DMG,
          FactConverters.SUM_OF_OWN_GROUND_DMG,
          FactConverters.SUM_OF_OWN_AIR_HP,
          FactConverters.SUM_OF_OWN_GROUND_HP,
          FactConverters.SUM_OF_OWN_STATIC_AIR_DMG,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_OWN_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_UNITS,
          FactConverters.SUM_OF_OWN_GROUND_UNITS
      )))
      .build();
  public static final FeatureContainerHeader INCREASING_CAPACITY = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.CURRENT_POPULATION,
          FactConverters.MAX_POPULATION
      )))
      .convertersForFactSetsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.SUM_OF_ENEMY_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_AIR_HP,
          FactConverters.SUM_OF_ENEMY_GROUND_HP,
          FactConverters.SUM_OF_ENEMY_AIR_SHIELDS,
          FactConverters.SUM_OF_ENEMY_GROUND_SHIELDS,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_ENEMY_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_DMG,
          FactConverters.SUM_OF_OWN_GROUND_DMG,
          FactConverters.SUM_OF_OWN_AIR_HP,
          FactConverters.SUM_OF_OWN_GROUND_HP,
          FactConverters.SUM_OF_OWN_STATIC_AIR_DMG,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_OWN_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_UNITS,
          FactConverters.SUM_OF_OWN_GROUND_UNITS
      )))
      .build();
  public static final FeatureContainerHeader TRAINING_WORKER = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefs(new HashSet<>(Collections.singleton(
          FactConverters.COUNT_OF_WORKERS
      )))
      .convertersForFactsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.COUNT_OF_BASES
      )))
      .convertersForFactSetsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
          FactConverters.COUNT_OF_EXTRACTORS,
          FactConverters.SUM_OF_ENEMY_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_AIR_HP,
          FactConverters.SUM_OF_ENEMY_GROUND_HP,
          FactConverters.SUM_OF_ENEMY_AIR_SHIELDS,
          FactConverters.SUM_OF_ENEMY_GROUND_SHIELDS,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_ENEMY_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_DMG,
          FactConverters.SUM_OF_OWN_GROUND_DMG,
          FactConverters.SUM_OF_OWN_AIR_HP,
          FactConverters.SUM_OF_OWN_GROUND_HP,
          FactConverters.SUM_OF_OWN_STATIC_AIR_DMG,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_OWN_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_UNITS,
          FactConverters.SUM_OF_OWN_GROUND_UNITS
      )))
      .build();
  public static final FeatureContainerHeader EXPANDING = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefs(new HashSet<>(Collections.singleton(
          FactConverters.COUNT_OF_WORKERS
      )))
      .convertersForFactsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.CURRENT_POPULATION,
          FactConverters.MAX_POPULATION
      )))
      .convertersForFactSetsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
          FactConverters.COUNT_OF_EXTRACTORS,
          FactConverters.SUM_OF_ENEMY_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_AIR_HP,
          FactConverters.SUM_OF_ENEMY_GROUND_HP,
          FactConverters.SUM_OF_ENEMY_AIR_SHIELDS,
          FactConverters.SUM_OF_ENEMY_GROUND_SHIELDS,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_ENEMY_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_DMG,
          FactConverters.SUM_OF_OWN_GROUND_DMG,
          FactConverters.SUM_OF_OWN_AIR_HP,
          FactConverters.SUM_OF_OWN_GROUND_HP,
          FactConverters.SUM_OF_OWN_STATIC_AIR_DMG,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_OWN_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_UNITS,
          FactConverters.SUM_OF_OWN_GROUND_UNITS
      )))
      .build();

  //Build order manager
  public static final FeatureContainerHeader BUILDING_POOL = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefs(new HashSet<>(Collections.singleton(
          FactConverters.COUNT_OF_WORKERS
      )))
      .convertersForFactsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.CURRENT_POPULATION,
          FactConverters.MAX_POPULATION
      )))
      .convertersForFactSetsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
          FactConverters.COUNT_OF_EXTRACTORS,
          FactConverters.SUM_OF_ENEMY_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_AIR_HP,
          FactConverters.SUM_OF_ENEMY_GROUND_HP,
          FactConverters.SUM_OF_ENEMY_AIR_SHIELDS,
          FactConverters.SUM_OF_ENEMY_GROUND_SHIELDS,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_ENEMY_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_DMG,
          FactConverters.SUM_OF_OWN_GROUND_DMG,
          FactConverters.SUM_OF_OWN_AIR_HP,
          FactConverters.SUM_OF_OWN_GROUND_HP,
          FactConverters.SUM_OF_OWN_STATIC_AIR_DMG,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_OWN_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_UNITS,
          FactConverters.SUM_OF_OWN_GROUND_UNITS
      )))
      .build();
  public static final FeatureContainerHeader UPGRADING_TO_LAIR = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.CURRENT_POPULATION,
          FactConverters.MAX_POPULATION
      )))
      .convertersForFactSetsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
          FactConverters.COUNT_OF_EXTRACTORS,
          FactConverters.SUM_OF_ENEMY_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_AIR_HP,
          FactConverters.SUM_OF_ENEMY_GROUND_HP,
          FactConverters.SUM_OF_ENEMY_AIR_SHIELDS,
          FactConverters.SUM_OF_ENEMY_GROUND_SHIELDS,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_ENEMY_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_DMG,
          FactConverters.SUM_OF_OWN_GROUND_DMG,
          FactConverters.SUM_OF_OWN_AIR_HP,
          FactConverters.SUM_OF_OWN_GROUND_HP,
          FactConverters.SUM_OF_OWN_STATIC_AIR_DMG,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_OWN_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_UNITS,
          FactConverters.SUM_OF_OWN_GROUND_UNITS
      )))
      .build();
  public static final FeatureContainerHeader BUILDING_SPIRE = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.CURRENT_POPULATION,
          FactConverters.MAX_POPULATION
      )))
      .convertersForFactSetsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
          FactConverters.COUNT_OF_EXTRACTORS,
          FactConverters.SUM_OF_ENEMY_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_AIR_HP,
          FactConverters.SUM_OF_ENEMY_GROUND_HP,
          FactConverters.SUM_OF_ENEMY_AIR_SHIELDS,
          FactConverters.SUM_OF_ENEMY_GROUND_SHIELDS,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_ENEMY_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_DMG,
          FactConverters.SUM_OF_OWN_GROUND_DMG,
          FactConverters.SUM_OF_OWN_AIR_HP,
          FactConverters.SUM_OF_OWN_GROUND_HP,
          FactConverters.SUM_OF_OWN_STATIC_AIR_DMG,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_OWN_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_UNITS,
          FactConverters.SUM_OF_OWN_GROUND_UNITS
      )))
      .build();
  public static final FeatureContainerHeader BUILDING_HYDRALISK_DEN = FeatureContainerHeader
      .builder()
      .convertersForFactsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.CURRENT_POPULATION,
          FactConverters.MAX_POPULATION
      )))
      .convertersForFactSetsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
          FactConverters.COUNT_OF_EXTRACTORS,
          FactConverters.SUM_OF_ENEMY_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_AIR_HP,
          FactConverters.SUM_OF_ENEMY_GROUND_HP,
          FactConverters.SUM_OF_ENEMY_AIR_SHIELDS,
          FactConverters.SUM_OF_ENEMY_GROUND_SHIELDS,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_ENEMY_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_DMG,
          FactConverters.SUM_OF_OWN_GROUND_DMG,
          FactConverters.SUM_OF_OWN_AIR_HP,
          FactConverters.SUM_OF_OWN_GROUND_HP,
          FactConverters.SUM_OF_OWN_STATIC_AIR_DMG,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_OWN_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_UNITS,
          FactConverters.SUM_OF_OWN_GROUND_UNITS
      )))
      .build();
  public static final FeatureContainerHeader BUILDING_EVOLUTION_CHAMBER = FeatureContainerHeader
      .builder()
      .convertersForFactsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.CURRENT_POPULATION,
          FactConverters.MAX_POPULATION
      )))
      .convertersForFactSetsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
          FactConverters.COUNT_OF_EXTRACTORS,
          FactConverters.SUM_OF_ENEMY_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_AIR_HP,
          FactConverters.SUM_OF_ENEMY_GROUND_HP,
          FactConverters.SUM_OF_ENEMY_AIR_SHIELDS,
          FactConverters.SUM_OF_ENEMY_GROUND_SHIELDS,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_ENEMY_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_DMG,
          FactConverters.SUM_OF_OWN_GROUND_DMG,
          FactConverters.SUM_OF_OWN_AIR_HP,
          FactConverters.SUM_OF_OWN_GROUND_HP,
          FactConverters.SUM_OF_OWN_STATIC_AIR_DMG,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_OWN_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_UNITS,
          FactConverters.SUM_OF_OWN_GROUND_UNITS
      )))
      .build();

  //Unit order manager
  public static final FeatureContainerHeader BOOSTING_AIR = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.CURRENT_POPULATION,
          FactConverters.MAX_POPULATION
      )))
      .convertersForFactSetsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
          FactConverters.SUM_OF_ENEMY_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_AIR_HP,
          FactConverters.SUM_OF_ENEMY_GROUND_HP,
          FactConverters.SUM_OF_ENEMY_AIR_SHIELDS,
          FactConverters.SUM_OF_ENEMY_GROUND_SHIELDS,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_ENEMY_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_DMG,
          FactConverters.SUM_OF_OWN_GROUND_DMG,
          FactConverters.SUM_OF_OWN_AIR_HP,
          FactConverters.SUM_OF_OWN_GROUND_HP,
          FactConverters.SUM_OF_OWN_STATIC_AIR_DMG,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_OWN_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_UNITS,
          FactConverters.SUM_OF_OWN_GROUND_UNITS
      )))
      .build();
  public static final FeatureContainerHeader BOOSTING_GROUND_MELEE = FeatureContainerHeader
      .builder()
      .convertersForFactsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.CURRENT_POPULATION,
          FactConverters.MAX_POPULATION
      )))
      .convertersForFactSetsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
          FactConverters.SUM_OF_ENEMY_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_AIR_HP,
          FactConverters.SUM_OF_ENEMY_GROUND_HP,
          FactConverters.SUM_OF_ENEMY_AIR_SHIELDS,
          FactConverters.SUM_OF_ENEMY_GROUND_SHIELDS,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_ENEMY_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_DMG,
          FactConverters.SUM_OF_OWN_GROUND_DMG,
          FactConverters.SUM_OF_OWN_AIR_HP,
          FactConverters.SUM_OF_OWN_GROUND_HP,
          FactConverters.SUM_OF_OWN_STATIC_AIR_DMG,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_OWN_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_UNITS,
          FactConverters.SUM_OF_OWN_GROUND_UNITS
      )))
      .build();
  public static final FeatureContainerHeader BOOSTING_GROUND_RANGED = FeatureContainerHeader
      .builder()
      .convertersForFactsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.CURRENT_POPULATION,
          FactConverters.MAX_POPULATION
      )))
      .convertersForFactSetsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
          FactConverters.SUM_OF_ENEMY_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_AIR_HP,
          FactConverters.SUM_OF_ENEMY_GROUND_HP,
          FactConverters.SUM_OF_ENEMY_AIR_SHIELDS,
          FactConverters.SUM_OF_ENEMY_GROUND_SHIELDS,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_ENEMY_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_DMG,
          FactConverters.SUM_OF_OWN_GROUND_DMG,
          FactConverters.SUM_OF_OWN_AIR_HP,
          FactConverters.SUM_OF_OWN_GROUND_HP,
          FactConverters.SUM_OF_OWN_STATIC_AIR_DMG,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_OWN_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_UNITS,
          FactConverters.SUM_OF_OWN_GROUND_UNITS
      )))
      .build();

  //BASE
  public static final FeatureContainerHeader HOLDING = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES
      )))
      .convertersForFactSetsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.SUM_OF_ENEMY_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_AIR_HP,
          FactConverters.SUM_OF_ENEMY_GROUND_HP,
          FactConverters.SUM_OF_ENEMY_AIR_SHIELDS,
          FactConverters.SUM_OF_ENEMY_GROUND_SHIELDS,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_ENEMY_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_DMG,
          FactConverters.SUM_OF_OWN_GROUND_DMG,
          FactConverters.SUM_OF_OWN_AIR_HP,
          FactConverters.SUM_OF_OWN_GROUND_HP,
          FactConverters.SUM_OF_OWN_STATIC_AIR_DMG,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_OWN_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_UNITS,
          FactConverters.SUM_OF_OWN_GROUND_UNITS
      )))
      .convertersForFactSets(new HashSet<>(Arrays.asList(
          FactConverters.SUM_OF_ENEMY_AIR_DMG_BASE,
          FactConverters.SUM_OF_ENEMY_GROUND_DMG_BASE,
          FactConverters.SUM_OF_ENEMY_AIR_HP_BASE,
          FactConverters.SUM_OF_ENEMY_GROUND_HP_BASE,
          FactConverters.SUM_OF_ENEMY_AIR_SHIELDS_BASE,
          FactConverters.SUM_OF_ENEMY_GROUND_SHIELDS_BASE,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG_BASE,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG_BASE,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_UNITS_BASE,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_UNITS_BASE,
          FactConverters.SUM_OF_ENEMY_AIR_UNITS_BASE,
          FactConverters.SUM_OF_ENEMY_GROUND_UNITS_BASE,
          FactConverters.SUM_OF_OWN_AIR_DMG_BASE,
          FactConverters.SUM_OF_OWN_GROUND_DMG_BASE,
          FactConverters.SUM_OF_OWN_AIR_HP_BASE,
          FactConverters.SUM_OF_OWN_GROUND_HP_BASE,
          FactConverters.SUM_OF_OWN_STATIC_AIR_DMG_BASE,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_DMG_BASE,
          FactConverters.SUM_OF_OWN_STATIC_AIR_UNITS_BASE,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_UNITS_BASE,
          FactConverters.SUM_OF_OWN_AIR_UNITS_BASE,
          FactConverters.SUM_OF_OWN_GROUND_UNITS_BASE
      )))
      .convertersForFacts(new HashSet<>(Arrays.asList(
          FactConverters.IS_BASE,
          FactConverters.IS_ENEMY_BASE,
          FactConverters.IS_MINERAL_ONLY,
          FactConverters.IS_ISLAND,
          FactConverters.IS_START_LOCATION
      )))
      .build();
  public static final FeatureContainerHeader DEFENSE = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES
      )))
      .convertersForFactSetsForGlobalBeliefsByAgentType(new HashSet<>(Arrays.asList(
          FactConverters.SUM_OF_ENEMY_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_AIR_HP,
          FactConverters.SUM_OF_ENEMY_GROUND_HP,
          FactConverters.SUM_OF_ENEMY_AIR_SHIELDS,
          FactConverters.SUM_OF_ENEMY_GROUND_SHIELDS,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_ENEMY_AIR_UNITS,
          FactConverters.SUM_OF_ENEMY_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_DMG,
          FactConverters.SUM_OF_OWN_GROUND_DMG,
          FactConverters.SUM_OF_OWN_AIR_HP,
          FactConverters.SUM_OF_OWN_GROUND_HP,
          FactConverters.SUM_OF_OWN_STATIC_AIR_DMG,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_DMG,
          FactConverters.SUM_OF_OWN_STATIC_AIR_UNITS,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_UNITS,
          FactConverters.SUM_OF_OWN_AIR_UNITS,
          FactConverters.SUM_OF_OWN_GROUND_UNITS
      )))
      .convertersForFactSets(new HashSet<>(Arrays.asList(
          FactConverters.SUM_OF_ENEMY_AIR_DMG_BASE,
          FactConverters.SUM_OF_ENEMY_GROUND_DMG_BASE,
          FactConverters.SUM_OF_ENEMY_AIR_HP_BASE,
          FactConverters.SUM_OF_ENEMY_GROUND_HP_BASE,
          FactConverters.SUM_OF_ENEMY_AIR_SHIELDS_BASE,
          FactConverters.SUM_OF_ENEMY_GROUND_SHIELDS_BASE,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG_BASE,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG_BASE,
          FactConverters.SUM_OF_ENEMY_STATIC_AIR_UNITS_BASE,
          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_UNITS_BASE,
          FactConverters.SUM_OF_ENEMY_AIR_UNITS_BASE,
          FactConverters.SUM_OF_ENEMY_GROUND_UNITS_BASE,
          FactConverters.SUM_OF_OWN_AIR_DMG_BASE,
          FactConverters.SUM_OF_OWN_GROUND_DMG_BASE,
          FactConverters.SUM_OF_OWN_AIR_HP_BASE,
          FactConverters.SUM_OF_OWN_GROUND_HP_BASE,
          FactConverters.SUM_OF_OWN_STATIC_AIR_DMG_BASE,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_DMG_BASE,
          FactConverters.SUM_OF_OWN_STATIC_AIR_UNITS_BASE,
          FactConverters.SUM_OF_OWN_STATIC_GROUND_UNITS_BASE,
          FactConverters.SUM_OF_OWN_AIR_UNITS_BASE,
          FactConverters.SUM_OF_OWN_GROUND_UNITS_BASE,
          FactConverters.COUNT_OF_CREEP_COLONIES_AT_BASE,
          FactConverters.COUNT_OF_SPORE_COLONIES_AT_BASE,
          FactConverters.COUNT_OF_SUNKEN_COLONIES_AT_BASE
      )))
      .convertersForFacts(new HashSet<>(Arrays.asList(
          FactConverters.IS_BASE,
          FactConverters.IS_ENEMY_BASE,
          FactConverters.IS_MINERAL_ONLY,
          FactConverters.IS_ISLAND,
          FactConverters.IS_START_LOCATION
      )))
      .build();

}
