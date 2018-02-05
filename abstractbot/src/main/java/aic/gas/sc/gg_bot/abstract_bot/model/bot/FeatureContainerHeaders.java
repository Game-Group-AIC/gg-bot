package aic.gas.sc.gg_bot.abstract_bot.model.bot;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureContainerHeader;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
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
      .build();

  public static final FeatureContainerHeader INCREASING_CAPACITY = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(
          Collections.singleton(FactConverters.IS_POOL_BUILT))
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.FREE_SUPPLY, FactConverters.GAME_PHASE)
          .collect(Collectors.toSet()))
      .build();

  public static final FeatureContainerHeader TRAINING_WORKER = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefs(Collections.singleton(FactConverters.COUNT_OF_WORKERS))
      .convertersForFactsForGlobalBeliefsByAgentType(
          Stream.of(FactConverters.COUNT_OF_BASES, FactConverters.GAME_PHASE)
              .collect(Collectors.toSet()))
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
          FactConverters.COUNT_OF_EXTRACTORS)
          .collect(Collectors.toSet()))
      .build();
//  public static final FeatureContainerHeader TRAINING_WORKER = FeatureContainerHeader.builder()
//      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(FactConverters.GAME_PHASE)
//          .collect(Collectors.toSet()))
//      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
//          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE
  //todo is pool built
//          //todo ratio of our army supply vs enemy army supply - cap 0.5 - 2.0
//      )
//          .collect(Collectors.toSet()))
//      .build();

  public static final FeatureContainerHeader EXPANDING = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefs(Collections.singleton(FactConverters.COUNT_OF_WORKERS))
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.CURRENT_POPULATION,
          FactConverters.MAX_POPULATION,
          FactConverters.GAME_PHASE)
          .collect(Collectors.toSet()))
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
          FactConverters.COUNT_OF_EXTRACTORS)
          .collect(Collectors.toSet()))
      .build();
//  public static final FeatureContainerHeader EXPANDING = FeatureContainerHeader.builder()
//      //TODO minerals
//      //TODO ratio of our army supply vs enemy army supply - cap 0.5 - 2.0
//      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
//          //TODO difference among our bases count vs enemy bases count - cap (-3,3)
//          FactConverters.GAME_PHASE)
//          .collect(Collectors.toSet()))
//      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
//          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE)
//          .collect(Collectors.toSet()))
//      .build();

  //Build order manager
  public static final FeatureContainerHeader BUILDING_POOL = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefs(Collections.singleton(FactConverters.COUNT_OF_WORKERS))
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.CURRENT_POPULATION,
          FactConverters.MAX_POPULATION,
          FactConverters.GAME_PHASE)
          .collect(Collectors.toSet()))
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
          FactConverters.COUNT_OF_EXTRACTORS)
          .collect(Collectors.toSet()))
      .build();
//  public static final FeatureContainerHeader BUILDING_POOL = FeatureContainerHeader.builder()
//      //TODO ratio of our army supply vs enemy army supply - cap 0.5 - 2.0
//      //TODO does enemy have structures to produce military units - 1 or 0
//      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
//          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE)
//          .collect(Collectors.toSet()))
//      .build();

  public static final FeatureContainerHeader UPGRADING_TO_LAIR = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.CURRENT_POPULATION,
          FactConverters.MAX_POPULATION,
          FactConverters.GAME_PHASE)
          .collect(Collectors.toSet()))
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
          FactConverters.COUNT_OF_EXTRACTORS)
          .collect(Collectors.toSet()))
      .build();
//  public static final FeatureContainerHeader UPGRADING_TO_LAIR = FeatureContainerHeader.builder()
//      //TODO ratio of our army supply vs enemy army supply - cap 0.5 - 2.0
//      //TODO has at least 2 bases
//      .build();

  public static final FeatureContainerHeader BUILDING_SPIRE = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.CURRENT_POPULATION,
          FactConverters.MAX_POPULATION,
          FactConverters.GAME_PHASE)
          .collect(Collectors.toSet()))
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
          FactConverters.COUNT_OF_EXTRACTORS)
          .collect(Collectors.toSet()))
      .build();
//  public static final FeatureContainerHeader BUILDING_SPIRE = FeatureContainerHeader.builder()
//      //TODO ratio of our army supply vs enemy army supply - cap 0.5 - 2.0
//      //TODO has at least 2 bases
//      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
//          FactConverters.SUM_OF_ENEMY_AIR_DMG, //TODO cap 200
//          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG) //TODO 300
//          .collect(Collectors.toSet()))
//      .build();

  public static final FeatureContainerHeader BUILDING_HYDRALISK_DEN = FeatureContainerHeader
      .builder()
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.CURRENT_POPULATION,
          FactConverters.MAX_POPULATION,
          FactConverters.GAME_PHASE)
          .collect(Collectors.toSet()))
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
          FactConverters.COUNT_OF_EXTRACTORS)
          .collect(Collectors.toSet()))
      .build();
//  public static final FeatureContainerHeader BUILDING_HYDRALISK_DEN = FeatureContainerHeader
//      .builder()
//      //TODO has at least 10 army supply
//      //TODO ratio of enemy ranged vs melee damage - cap 0.5 - 2.0
//      //TODO ratio of own ranged vs melee damage - cap 0.5 - 2.0
//      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
//          FactConverters.SUM_OF_ENEMY_AIR_HP)
//          .collect(Collectors.toSet()))
//      .build();

  public static final FeatureContainerHeader BUILDING_EVOLUTION_CHAMBER = FeatureContainerHeader
      .builder()
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.CURRENT_POPULATION,
          FactConverters.MAX_POPULATION,
          FactConverters.GAME_PHASE)
          .collect(Collectors.toSet()))
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
          FactConverters.COUNT_OF_EXTRACTORS)
          .collect(Collectors.toSet()))
      .build();

//  public static final FeatureContainerHeader BUILDING_EVOLUTION_CHAMBER = FeatureContainerHeader
//      .builder()
//      //TODO ratio of our army supply vs enemy army supply - cap 0.5 - 2.0
//      //TODO has at least 10 army supply
//      //TODO has at least 2 bases
//      .build();

  //Unit order manager
  public static final FeatureContainerHeader BOOSTING_AIR = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.CURRENT_POPULATION,
          FactConverters.MAX_POPULATION,
          FactConverters.GAME_PHASE)
          .collect(Collectors.toSet()))
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE)
          .collect(Collectors.toSet()))
      .build();

//  public static final FeatureContainerHeader BOOSTING_AIR = FeatureContainerHeader.builder()
//      //TODO has at least 2 bases
//      //TODO has at least 10 army supply
//      //TODO number of enemy bases unprotected against air
//      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
//          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
//          FactConverters.SUM_OF_ENEMY_AIR_DMG,
//          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG,
//          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
//        )
//      .collect(Collectors.toSet()))
//      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
//          //TODO difference among our bases count vs enemy bases count - cap (-3,3)
//          FactConverters.GAME_PHASE)
//          .collect(Collectors.toSet()))
//      .build();


  public static final FeatureContainerHeader BOOSTING_GROUND_MELEE = FeatureContainerHeader
      .builder()
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.CURRENT_POPULATION,
          FactConverters.MAX_POPULATION,
          FactConverters.GAME_PHASE)
          .collect(Collectors.toSet()))
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE)
          .collect(Collectors.toSet()))
      .build();

//  public static final FeatureContainerHeader BOOSTING_GROUND_MELEE = FeatureContainerHeader
//      .builder()
//      //TODO ratio of our army supply vs enemy army supply - cap 0.5 - 2.0
//      //TODO ratio of enemy ranged vs melee damage
//      //TODO ratio of own ranged vs melee damage
//      //TODO number of enemy bases unprotected against ground
//      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
//          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
//          FactConverters.SUM_OF_ENEMY_AIR_UNITS,
//        )
//      .collect(Collectors.toSet()))
//      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
//          //TODO difference among our bases count vs enemy bases count - cap (-3,3)
//          .collect(Collectors.toSet()))
//      .build();

  public static final FeatureContainerHeader BOOSTING_GROUND_RANGED = FeatureContainerHeader
      .builder()
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.CURRENT_POPULATION,
          FactConverters.MAX_POPULATION,
          FactConverters.GAME_PHASE)
          .collect(Collectors.toSet()))
      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_MINERALS_PER_BASE,
          FactConverters.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE)
          .collect(Collectors.toSet()))
      .build();

//  public static final FeatureContainerHeader BOOSTING_GROUND_RANGED = FeatureContainerHeader
//      .builder()
//      //TODO ratio of our army supply vs enemy army supply - cap 0.5 - 2.0
//      //TODO ratio of enemy ranged vs melee damage
//      //TODO ratio of own ranged vs melee damage
//      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
//          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
//          FactConverters.SUM_OF_ENEMY_AIR_UNITS,
//        )
//      .collect(Collectors.toSet()))
//      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
//          //TODO difference among our bases count vs enemy bases count - cap (-3,3)
//          .collect(Collectors.toSet()))
//      .build();

  //BASE
  //TODO HOLDING BY AIR UNITS, BY GROUND UNITS
  public static final FeatureContainerHeader HOLDING = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.GAME_PHASE)
          .collect(Collectors.toSet()))
//      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
//          FactConverters.SUM_OF_ENEMY_AIR_DMG,
//          FactConverters.SUM_OF_ENEMY_GROUND_DMG,
//          FactConverters.SUM_OF_ENEMY_AIR_HP,
//          FactConverters.SUM_OF_ENEMY_GROUND_HP,
//          FactConverters.SUM_OF_ENEMY_AIR_SHIELDS,
//          FactConverters.SUM_OF_ENEMY_GROUND_SHIELDS,
//          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG,
//          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
//          FactConverters.SUM_OF_ENEMY_STATIC_AIR_UNITS,
//          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_UNITS,
//          FactConverters.SUM_OF_ENEMY_AIR_UNITS,
//          FactConverters.SUM_OF_ENEMY_GROUND_UNITS,
//          FactConverters.SUM_OF_OWN_AIR_DMG,
//          FactConverters.SUM_OF_OWN_GROUND_DMG,
//          FactConverters.SUM_OF_OWN_AIR_HP,
//          FactConverters.SUM_OF_OWN_GROUND_HP,
//          FactConverters.SUM_OF_OWN_STATIC_AIR_DMG,
//          FactConverters.SUM_OF_OWN_STATIC_GROUND_DMG,
//          FactConverters.SUM_OF_OWN_STATIC_AIR_UNITS,
//          FactConverters.SUM_OF_OWN_STATIC_GROUND_UNITS,
//          FactConverters.SUM_OF_OWN_AIR_UNITS,
//          FactConverters.SUM_OF_OWN_GROUND_UNITS)
//          .collect(Collectors.toSet()))
//      .convertersForFactSets(Stream.of(
//          FactConverters.SUM_OF_ENEMY_AIR_DMG_BASE,
//          FactConverters.SUM_OF_ENEMY_GROUND_DMG_BASE,
//          FactConverters.SUM_OF_ENEMY_AIR_HP_BASE,
//          FactConverters.SUM_OF_ENEMY_GROUND_HP_BASE,
//          FactConverters.SUM_OF_ENEMY_AIR_SHIELDS_BASE,
//          FactConverters.SUM_OF_ENEMY_GROUND_SHIELDS_BASE,
//          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG_BASE,
//          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG_BASE,
//          FactConverters.SUM_OF_ENEMY_STATIC_AIR_UNITS_BASE,
//          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_UNITS_BASE,
//          FactConverters.SUM_OF_ENEMY_AIR_UNITS_BASE,
//          FactConverters.SUM_OF_ENEMY_GROUND_UNITS_BASE,
//          FactConverters.SUM_OF_OWN_AIR_DMG_BASE,
//          FactConverters.SUM_OF_OWN_GROUND_DMG_BASE,
//          FactConverters.SUM_OF_OWN_AIR_HP_BASE,
//          FactConverters.SUM_OF_OWN_GROUND_HP_BASE,
//          FactConverters.SUM_OF_OWN_STATIC_AIR_DMG_BASE,
//          FactConverters.SUM_OF_OWN_STATIC_GROUND_DMG_BASE,
//          FactConverters.SUM_OF_OWN_STATIC_AIR_UNITS_BASE,
//          FactConverters.SUM_OF_OWN_STATIC_GROUND_UNITS_BASE,
//          FactConverters.SUM_OF_OWN_AIR_UNITS_BASE,
//          FactConverters.SUM_OF_OWN_GROUND_UNITS_BASE)
//          .collect(Collectors.toSet()))
      .convertersForFacts(Stream.of(
          FactConverters.IS_BASE,
          FactConverters.IS_ENEMY_BASE,
          FactConverters.IS_MINERAL_ONLY,
          FactConverters.IS_ISLAND,
          FactConverters.IS_START_LOCATION)
          .collect(Collectors.toSet()))
      .build();

//BASE
//TODO HOLDING BY AIR UNITS, BY GROUND UNITS
//  public static final FeatureContainerHeader HOLDING_BY_AIR_UNITS = FeatureContainerHeader.builder()

//     //TODO ratio of how much damage per minute our air army can inflict vs. how much damage per minute it can suffer in that region
//     //TODO ratio of our global air army supply vs. enemy anti-air army supply in that region
//     //TODO is this region our main base? (True / False)
//     //TODO is this region enemy main base? (True / False)
//     //TODO is there our base in this region? (True / False)
//     //TODO is there an enemy base in this region? (True / False)
//     //TODO air distance to nearest enemy base
//     //TODO air distance to our nearest base
//      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
//          FactConverters.COUNT_OF_BASES,
//          FactConverters.COUNT_OF_ENEMY_BASES,
//          )
//          .collect(Collectors.toSet()))
//      .build();

//  public static final FeatureContainerHeader HOLDING_BY_GROUND_UNITS = FeatureContainerHeader.builder()
//     //TODO ratio of how much damage per minute our ground army can inflict vs. how much damage per minute it can suffer in that region
//     //TODO ratio of our global ground army supply vs. enemy anti-ground army supply in that region
//     //TODO is this region our main base? (True / False)
//     //TODO is this region enemy main base? (True / False)
//     //TODO is there our base in this region? (True / False)
//     //TODO is there an enemy base in this region? (True / False)
//     //TODO ground distance to nearest enemy base
//     //TODO ground distance to our nearest base
//      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
//          FactConverters.COUNT_OF_BASES,
//          FactConverters.COUNT_OF_ENEMY_BASES,
//          )
//          .collect(Collectors.toSet()))

//      .build();

  //TODO 3 cases - build sunken, creep and spore
  public static final FeatureContainerHeader DEFENSE = FeatureContainerHeader.builder()
      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
          FactConverters.COUNT_OF_BASES,
          FactConverters.COUNT_OF_ENEMY_BASES,
          FactConverters.AVAILABLE_BASES,
          FactConverters.GAME_PHASE)
          .collect(Collectors.toSet()))
//      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
//          FactConverters.SUM_OF_ENEMY_AIR_DMG,
//          FactConverters.SUM_OF_ENEMY_GROUND_DMG,
//          FactConverters.SUM_OF_ENEMY_AIR_HP,
//          FactConverters.SUM_OF_ENEMY_GROUND_HP,
//          FactConverters.SUM_OF_ENEMY_AIR_SHIELDS,
//          FactConverters.SUM_OF_ENEMY_GROUND_SHIELDS,
//          FactConverters.SUM_OF_ENEMY_STATIC_AIR_DMG,
//          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_DMG,
//          FactConverters.SUM_OF_ENEMY_STATIC_AIR_UNITS,
//          FactConverters.SUM_OF_ENEMY_STATIC_GROUND_UNITS,
//          FactConverters.SUM_OF_ENEMY_AIR_UNITS,
//          FactConverters.SUM_OF_ENEMY_GROUND_UNITS,
//          FactConverters.SUM_OF_OWN_AIR_DMG,
//          FactConverters.SUM_OF_OWN_GROUND_DMG,
//          FactConverters.SUM_OF_OWN_AIR_HP,
//          FactConverters.SUM_OF_OWN_GROUND_HP,
//          FactConverters.SUM_OF_OWN_STATIC_AIR_DMG,
//          FactConverters.SUM_OF_OWN_STATIC_GROUND_DMG,
//          FactConverters.SUM_OF_OWN_STATIC_AIR_UNITS,
//          FactConverters.SUM_OF_OWN_STATIC_GROUND_UNITS,
//          FactConverters.SUM_OF_OWN_AIR_UNITS,
//          FactConverters.SUM_OF_OWN_GROUND_UNITS)
//          .collect(Collectors.toSet()))
      .convertersForFactSets(Stream.of(FactConverters.COUNT_OF_CREEP_COLONIES_AT_BASE,
          FactConverters.COUNT_OF_SPORE_COLONIES_AT_BASE,
          FactConverters.COUNT_OF_SUNKEN_COLONIES_AT_BASE)
          .collect(Collectors.toSet()))
      .convertersForFacts(Stream.of(
          FactConverters.IS_BASE,
          FactConverters.IS_ENEMY_BASE,
          FactConverters.IS_MINERAL_ONLY,
          FactConverters.IS_ISLAND,
          FactConverters.IS_START_LOCATION)
          .collect(Collectors.toSet()))
      .build();

  //TODO by malo zmysel rozdelit DEFENSE na 2 casti - sunken a spore. creep colony je len prerekvizita - stavia sa len preto, ze chces sunken alebo spore.
  //TODO do not limit creep colonies by one
//  public static final FeatureContainerHeader DEFENSE = FeatureContainerHeader.builder()
//     //TODO ratio of our army supply vs enemy army supply - cap 0.5 - 2.0
//     //TODO is this region our main base? (True / False)
//     //TODO is there our base in this region? (True / False)
//     //TODO ground distance to nearest enemy base
//     //TODO air distance to nearest enemy base
//      .convertersForFactsForGlobalBeliefsByAgentType(Stream.of(
//          FactConverters.COUNT_OF_BASES
//          ).collect(Collectors.toSet()))
//      .convertersForFactSetsForGlobalBeliefsByAgentType(Stream.of(
//          FactConverters.SUM_OF_ENEMY_GROUND_DMG,
//          FactConverters.SUM_OF_ENEMY_AIR_UNITS
//      ).collect(Collectors.toSet()))
//      .convertersForFactSets(Stream.of(
//          FactConverters.COUNT_OF_CREEP_COLONIES_AT_BASE,
//          FactConverters.COUNT_OF_SPORE_COLONIES_AT_BASE,
//          FactConverters.COUNT_OF_SUNKEN_COLONIES_AT_BASE)
//          .collect(Collectors.toSet()))
//      .build();

  //TODO build lurkers
  //TODO research lurkers
  //TODO burrow lurker

  //TODO research

}
