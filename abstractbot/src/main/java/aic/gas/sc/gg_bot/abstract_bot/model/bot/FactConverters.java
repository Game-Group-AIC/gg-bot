package aic.gas.sc.gg_bot.abstract_bot.model.bot;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_AIR_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_STATIC_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper.MAX_DISTANCE;

import aic.gas.sc.gg_bot.abstract_bot.model.UnitTypeStatus;
import aic.gas.sc.gg_bot.abstract_bot.model.game.util.Utils;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitOfPlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitWithCommands;
import aic.gas.sc.gg_bot.mas.model.metadata.FactConverterID;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactValueSet;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactValueSets;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactValueSetsForAgentType;
import bwapi.Order;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * Enumeration of all IDs for facts' types as static classes
 */
@Slf4j
public class FactConverters {

  public static final FactValueSetsForAgentType<AUnitOfPlayer> COUNT_OF_EXTRACTORS = new FactValueSetsForAgentType<>(
      new FactConverterID<>(5, FactKeys.HAS_EXTRACTOR, "COUNT_OF_EXTRACTORS"),
      AgentTypes.BASE_LOCATION.getId(),
      optionalStream -> (double) optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .mapToLong(Stream::count)
          .sum()
  );

  public static final FactValueSet<AUnit> COUNT_OF_MINERALS_ON_BASE = new FactValueSet<>(
      new FactConverterID<>(6, FactKeys.MINERAL, "COUNT_OF_MINERALS_ON_BASE"),
      aUnitStream -> aUnitStream.map(aUnitStream1 -> (double) aUnitStream1.count()).orElse(0.0));


  public static final FactValueSet<AUnitOfPlayer> COUNT_OF_EXTRACTORS_ON_BASE = new FactValueSet<>(
      new FactConverterID<>(7, FactKeys.HAS_EXTRACTOR, "COUNT_OF_EXTRACTORS_ON_BASE"),
      aUnitStream -> aUnitStream.map(aUnitStream1 -> (double) aUnitStream1
          .filter(aUnitOfPlayer -> !aUnitOfPlayer.isMorphing()
              && !aUnitOfPlayer.isBeingConstructed())
          .count()).orElse(0.0));

  public static final FactValueSetsForAgentType<Double> CURRENT_POPULATION = new FactValueSetsForAgentType<>(
      new FactConverterID<>(38, FactKeys.POPULATION, "CURRENT_POPULATION"),
      AgentTypes.PLAYER.getId(),
      optionalStream -> optionalStream.filter(Optional::isPresent)
          .map(Optional::get)
          .map(Stream::findFirst)
          .filter(Optional::isPresent)
          .mapToDouble(Optional::get)
          .sum());

  public static final FactValueSetsForAgentType<Double> MAX_POPULATION = new FactValueSetsForAgentType<>(
      new FactConverterID<>(39, FactKeys.POPULATION_LIMIT, "MAX_POPULATION"),
      AgentTypes.PLAYER.getId(),
      optionalStream -> optionalStream.filter(Optional::isPresent)
          .map(Optional::get)
          .map(Stream::findFirst)
          .filter(Optional::isPresent)
          .mapToDouble(Optional::get)
          .sum());

  public static final FactValueSet<Boolean> IS_BASE = new FactValueSet<>(
      new FactConverterID<>(323, FactKeys.IS_OUR_BASE, "IS_BASE"),
      aBoolean -> aBoolean.orElse(Stream.of(false)).findFirst().orElse(false) ? 1.0 : 0.0
  );

  public static final FactValueSet<AUnitOfPlayer> COUNT_OF_CREEP_COLONIES_AT_BASE = new FactValueSet<>(
      new FactConverterID<>(328, FactKeys.STATIC_DEFENSE, "COUNT_OF_CREEP_COLONIES_AT_BASE"),
      vStream -> (double) vStream.orElse(Stream.empty())
          .map(AUnit::getType)
          .filter(typeWrapper -> typeWrapper.equals(AUnitTypeWrapper.CREEP_COLONY_TYPE))
          .count());

  public static final FactValueSet<AUnitOfPlayer> COUNT_OF_SPORE_COLONIES_AT_BASE = new FactValueSet<>(
      new FactConverterID<>(329, FactKeys.STATIC_DEFENSE, "COUNT_OF_SPORE_COLONIES_AT_BASE"),
      vStream -> (double) vStream.orElse(Stream.empty())
          .map(AUnit::getType)
          .filter(typeWrapper -> typeWrapper.equals(AUnitTypeWrapper.SPORE_COLONY_TYPE))
          .count());

  public static final FactValueSet<AUnitOfPlayer> COUNT_OF_SUNKEN_COLONIES_AT_BASE = new FactValueSet<>(
      new FactConverterID<>(330, FactKeys.STATIC_DEFENSE, "COUNT_OF_SUNKEN_COLONIES_AT_BASE"),
      vStream -> (double) vStream.orElse(Stream.empty())
          .map(AUnit::getType)
          .filter(typeWrapper -> typeWrapper.equals(AUnitTypeWrapper.SUNKEN_COLONY_TYPE))
          .count());

  public static final FactValueSet<AUnitOfPlayer> BASE_IS_COMPLETED = new FactValueSet<>(
      new FactConverterID<>(334, FactKeys.HAS_BASE, "BASE_IS_COMPLETED"),
      vStream -> vStream.orElse(Stream.empty())
          .anyMatch(aUnitOfPlayer -> !aUnitOfPlayer.isBeingConstructed()
              && !aUnitOfPlayer.isMorphing()) ? 1.0 : 0.0);

  public static final FactValueSetsForAgentType<AUnitOfPlayer> COUNT_OF_POOLS = new FactValueSetsForAgentType<>(
      new FactConverterID<>(402, FactKeys.REPRESENTS_UNIT, "COUNT_OF_POOLS"),
      AgentTypes.SPAWNING_POOL.getId(),
      optionalStream -> (double) optionalStream
          .filter(Optional::isPresent)
          .count());

  public static final FactValueSetsForAgentType<AUnitOfPlayer> COUNT_OF_LAIRS = new FactValueSetsForAgentType<>(
      new FactConverterID<>(410, FactKeys.REPRESENTS_UNIT, "COUNT_OF_LAIRS"),
      AgentTypes.LAIR.getId(),
      optionalStream -> (double) optionalStream.filter(Optional::isPresent)
          .count());

  public static final FactValueSetsForAgentType<AUnitOfPlayer> COUNT_OF_SPIRES = new FactValueSetsForAgentType<>(
      new FactConverterID<>(412, FactKeys.REPRESENTS_UNIT, "COUNT_OF_SPIRES"),
      AgentTypes.SPIRE.getId(),
      optionalStream -> (double) optionalStream.filter(Optional::isPresent)
          .count());

  public static final FactValueSetsForAgentType<AUnitOfPlayer> COUNT_OF_HYDRALISK_DENS = new FactValueSetsForAgentType<>(
      new FactConverterID<>(414, FactKeys.REPRESENTS_UNIT, "COUNT_OF_HYDRALISK_DENS"),
      AgentTypes.HYDRALISK_DEN.getId(),
      optionalStream -> (double) optionalStream.filter(Optional::isPresent)
          .count());

  public static final FactValueSetsForAgentType<AUnitOfPlayer> COUNT_OF_EVOLUTION_CHAMBERS = new FactValueSetsForAgentType<>(
      new FactConverterID<>(416, FactKeys.REPRESENTS_UNIT, "COUNT_OF_EVOLUTION_CHAMBERS"),
      AgentTypes.EVOLUTION_CHAMBER.getId(),
      optionalStream -> (double) optionalStream.filter(Optional::isPresent)
          .count());

  public static final FactValueSet<AUnitOfPlayer> IS_MORPHING = new FactValueSet<>(
      new FactConverterID<>(502, FactKeys.REPRESENTS_UNIT, "IS_MORPHING"),
      aUnit -> aUnit.get().findFirst().get().getOrder().isPresent()
          && Stream.of(
          Order.ZergBuildingMorph, Order.IncompleteBuilding, Order.ZergUnitMorph
      ).anyMatch(order -> order == aUnit.get().findFirst().get().getOrder().get()) ? 1.0 : 0.0);

  public static final FactValueSet<AUnit> IS_MINING_MINERAL = new FactValueSet<>(
      new FactConverterID<>(701, FactKeys.MINING_MINERAL, "IS_MINING_MINERAL"),
      aUnit -> {
        if (aUnit.isPresent()) {
          return 1;
        }
        return 0;
      });

  public static final FactValueSet<AUnitWithCommands> IS_CARRYING_MINERAL = new FactValueSet<>(
      new FactConverterID<>(702, FactKeys.IS_UNIT, "IS_CARRYING_MINERAL"),
      aUnit -> {
        if (aUnit.isPresent()) {
          if (aUnit.get().findFirst().get().isCarryingMinerals()) {
            return 1;
          }
        }
        return 0;
      });

  public static final FactValueSet<AUnitWithCommands> IS_MINING_GAS = new FactValueSet<>(
      new FactConverterID<>(707, FactKeys.IS_UNIT, "IS_MINING_GAS"),
      aUnit -> aUnit.get().findFirst().get().isGatheringGas() ? 1.0 : 0.0
  );

  public static final FactValueSet<AUnitWithCommands> IS_CARRYING_GAS = new FactValueSet<>(
      new FactConverterID<>(708, FactKeys.IS_UNIT, "IS_CARRYING_GAS"),
      aUnit -> aUnit.get().findFirst().get().isCarryingGas() ? 1.0 : 0.0
  );

  public static final FactValueSetsForAgentType<AUnitOfPlayer> COUNT_OF_IDLE_DRONES = new FactValueSetsForAgentType<>(
      new FactConverterID<>(710, FactKeys.REPRESENTS_UNIT, "COUNT_OF_IDLE_DRONES"),
      AgentTypes.DRONE.getId(),
      optionalStream -> optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .map(Stream::findFirst)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(AUnit::isIdle)
          .count());

  static final FactValueSetsForAgentType<APlayer> GAME_PHASE = new FactValueSetsForAgentType<>(
      new FactConverterID<>(0, FactKeys.IS_PLAYER, "GAME_PHASE"),
      AgentTypes.PLAYER.getId(),
      optionalStream -> (double) optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .map(Stream::findFirst)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .mapToDouble(APlayer::getFrameCount)
          .map(frameCount -> {
            // assuming 20 FPS
            // intervals at 5, 10, 15, 20 mins
            if (frameCount < 6000) {
              return 0;
            } else if (frameCount < 12000) {
              return 1;
            } else if (frameCount < 18000) {
              return 2;
            } else if (frameCount < 24000) {
              return 3;
            } else {
              return 4;
            }
          })
          .findFirst()
          .orElse(0.0));

  //converters for base
  static final FactValueSetsForAgentType<Double> AVERAGE_COUNT_OF_WORKERS_PER_BASE = new FactValueSetsForAgentType<>(
      new FactConverterID<>(2, FactKeys.AVERAGE_COUNT_OF_WORKERS_PER_BASE,
          "AVERAGE_COUNT_OF_WORKERS_PER_BASE"), AgentTypes.PLAYER.getId(), optionalStream ->
      optionalStream.filter(Optional::isPresent)
          .map(Optional::get)
          .map(Stream::findFirst)
          .filter(Optional::isPresent)
          .mapToDouble(Optional::get)
          .sum());

  static final FactValueSetsForAgentType<Double> AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE = new FactValueSetsForAgentType<>(
      new FactConverterID<>(3, FactKeys.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
          "AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE"), AgentTypes.PLAYER.getId(),
      optionalStream -> optionalStream.filter(Optional::isPresent)
          .map(Optional::get)
          .map(Stream::findFirst)
          .filter(Optional::isPresent)
          .mapToDouble(Optional::get)
          .sum());

  static final FactValueSetsForAgentType<Integer> COUNT_OF_BASES_WITHOUT_EXTRACTORS = new FactValueSetsForAgentType<>(
      new FactConverterID<>(8, FactKeys.COUNT_OF_BASES_WITHOUT_EXTRACTORS,
          "COUNT_OF_BASES_WITHOUT_EXTRACTORS"), AgentTypes.PLAYER.getId(),
      optionalStream -> Math.min(optionalStream.filter(Optional::isPresent)
          .map(Optional::get)
          .map(Stream::findFirst)
          .filter(Optional::isPresent)
          .mapToInt(Optional::get)
          .sum(), 3));

  //converters for player's - aggregated data
  static final FactValueSetsForAgentType<Boolean> COUNT_OF_BASES = new FactValueSetsForAgentType<>(
      new FactConverterID<>(31, FactKeys.IS_OUR_BASE, "COUNT_OF_BASES"),
      AgentTypes.BASE_LOCATION.getId(),
      optionalStream -> (double) optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .map(Stream::findFirst)
          .filter(Optional::isPresent)
          .filter(Optional::get)
          .count()
  );

  static final FactValueSetsForAgentType<Boolean> HAS_AT_LEAST_TWO_BASES = new FactValueSetsForAgentType<>(
      new FactConverterID<>(32, FactKeys.IS_OUR_BASE, "HAS_AT_LEAST_TWO_BASES"),
      AgentTypes.BASE_LOCATION.getId(),
      optionalStream -> optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .map(Stream::findFirst)
          .filter(Optional::isPresent)
          .filter(Optional::get)
          .count() >= 2 ? 1.0 : 0.0);

  static final FactValueSetsForAgentType<Boolean> COUNT_OF_ENEMY_BASES = new FactValueSetsForAgentType<>(
      new FactConverterID<>(33, FactKeys.IS_ENEMY_BASE, "COUNT_OF_ENEMY_BASES"),
      AgentTypes.BASE_LOCATION.getId(),
      optionalStream -> (double) optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .map(Stream::findFirst)
          .filter(Optional::isPresent)
          .filter(Optional::get)
          .count());

  //base army stats
  static final FactValueSetsForAgentType<UnitTypeStatus> SUM_OF_ENEMY_AIR_DMG = new FactValueSetsForAgentType<>(
      new FactConverterID<>(34, ENEMY_AIR_FORCE_STATUS, "SUM_OF_ENEMY_AIR_DMG"),
      AgentTypes.PLAYER.getId(),
      optionalStream -> optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .mapToDouble(value -> value
              .mapToDouble(
                  v -> v.getCount() * v.getUnitTypeWrapper().getAirWeapon()
                      .getDamagePerSecondNormalized())
              .sum())
          .sum());

  static final FactValueSetsForAgentType<UnitTypeStatus> SUM_OF_ENEMY_STATIC_AIR_DMG = new FactValueSetsForAgentType<>(
      new FactConverterID<>(35, FactKeys.ENEMY_STATIC_AIR_FORCE_STATUS,
          "SUM_OF_ENEMY_STATIC_AIR_DMG"), AgentTypes.PLAYER.getId(),
      optionalStream -> optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .mapToDouble(value -> value
              .mapToDouble(
                  v -> v.getCount() * v.getUnitTypeWrapper().getAirWeapon()
                      .getDamagePerSecondNormalized())
              .sum())
          .sum());

  static final FactValueSetsForAgentType<UnitTypeStatus> CAN_ENEMY_PRODUCE_MILITARY_UNITS = new FactValueSetsForAgentType<>(
      new FactConverterID<>(36, FactKeys.ENEMY_BUILDING_STATUS, "CAN_ENEMY_PRODUCE_MILITARY_UNITS"),
      AgentTypes.PLAYER.getId(),
      optionalStream -> {
        boolean canProduceMilitaryUnits = optionalStream
            .filter(Optional::isPresent)
            .map(Optional::get)
            .flatMap(unitTypeStatusStream -> unitTypeStatusStream)
            .map(UnitTypeStatus::getUnitTypeWrapper)
            .anyMatch(AUnitTypeWrapper::isEnablesMilitaryUnits);

        return canProduceMilitaryUnits ? 1.0 : 0.0;
      }
  );

  //defense
  static final FactValueSetsForAgentType<Double> COUNT_OF_MINERALS = new FactValueSetsForAgentType<>(
      new FactConverterID<>(40, FactKeys.AVAILABLE_MINERALS, "COUNT_OF_MINERALS"),
      AgentTypes.PLAYER.getId(),
      optionalStream -> (double) optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .map(Stream::findFirst)
          .filter(Optional::isPresent)
          .mapToDouble(Optional::get)
          .sum());

  static final FactValueSetsForAgentType<Double> FREE_SUPPLY = new FactValueSetsForAgentType<>(
      new FactConverterID<>(41, FactKeys.FREE_SUPPLY, "FREE_SUPPLY"), AgentTypes.PLAYER.getId(),
      optionalStream -> {
        double count = optionalStream
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(Stream::findFirst)
            .filter(Optional::isPresent)
            .mapToDouble(Optional::get)
            .sum();

        return count > 7 ? 7 : count;
      });

  static final FactValueSetsForAgentType<Double> FORCE_SUPPLY_RATIO = new FactValueSetsForAgentType<>(
      new FactConverterID<>(42, FactKeys.FORCE_SUPPLY_RATIO, "FORCE_SUPPLY_RATIO"),
      AgentTypes.PLAYER.getId(),
      optionalStream -> {
        double sum = optionalStream
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(Stream::findFirst)
            .filter(Optional::isPresent)
            .mapToDouble(Optional::get)
            .sum();

        //cap to interval 0.5 - 2.0
        return Math.max(Math.min(sum, 2.0), 0.5);

      });

  static final FactValueSetsForAgentType<Double> DIFFERENCE_IN_BASES = new FactValueSetsForAgentType<>(
      new FactConverterID<>(43, FactKeys.DIFFERENCE_IN_BASES, "DIFFERENCE_IN_BASES"),
      AgentTypes.PLAYER.getId(),
      optionalStream -> {
        double sum = optionalStream
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(Stream::findFirst)
            .filter(Optional::isPresent)
            .mapToDouble(Optional::get)
            .sum();

        //cap to interval 0.5 - 2.0
        return Math.max(Math.min(sum, 3.0), -3.0);

      });

  static final FactValueSetsForAgentType<UnitTypeStatus> ENEMY_RANGED_VS_MELEE_DAMAGE = new FactValueSetsForAgentType<>(
      new FactConverterID<>(44, ENEMY_GROUND_FORCE_STATUS, "ENEMY_RANGED_VS_MELEE_DAMAGE"),
      AgentTypes.PLAYER.getId(),
      optionalStream -> {
        double sum = optionalStream
            .filter(Optional::isPresent)
            .mapToDouble(Utils::computeRangedVsMeleeDamageRatio)
            .sum();

        //cap to interval 0.5 - 2.0
        return Math.max(Math.min(sum, 2.0), 0.5);

      }
  );

  static final FactValueSetsForAgentType<UnitTypeStatus> OUR_RANGED_VS_MELEE_DAMAGE = new FactValueSetsForAgentType<>(
      new FactConverterID<>(45, FactKeys.OWN_GROUND_FORCE_STATUS, "OUR_RANGED_VS_MELEE_DAMAGE"),
      AgentTypes.PLAYER.getId(),
      optionalStream -> {
        double sum = optionalStream
            .filter(Optional::isPresent)
            .mapToDouble(Utils::computeRangedVsMeleeDamageRatio)
            .sum();

        //cap to interval 0.5 - 2.0
        return Math.max(Math.min(sum, 2.0), 0.5);

      }
  );

  static final FactValueSetsForAgentType<UnitTypeStatus> SUM_OF_ENEMY_AIR_HP = new FactValueSetsForAgentType<>(
      new FactConverterID<>(46, ENEMY_AIR_FORCE_STATUS, "SUM_OF_ENEMY_AIR_HP"),
      AgentTypes.PLAYER.getId(),
      optionalStream -> optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .mapToDouble(value -> value
              .mapToDouble(v -> v.getCount() * (v.getUnitTypeWrapper().getMaxHitPoints() + v
                  .getUnitTypeWrapper().getMaxShields())).sum())
          .sum());

  static final FactValueSetsForAgentType<UnitTypeStatus> HAS_AT_LEAST_10_ARMY_SUPPLY = new FactValueSetsForAgentType<>(
      new FactConverterID<>(47, FactKeys.OWN_FORCE_STATUS, "HAS_AT_LEAST_10_ARMY_SUPPLY"),
      AgentTypes.PLAYER.getId(),
      optionalStream -> optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .mapToDouble(value -> value
              .mapToDouble(v -> v.getCount() * v.getUnitTypeWrapper().supplyRequired()).sum())
          .sum() >= 10 ? 1.0 : 0.0);

  static final FactValueSetsForAgentType<UnitTypeStatus> SUM_OF_ENEMY_STATIC_GROUND_DMG = new FactValueSetsForAgentType<>(
      new FactConverterID<>(48, ENEMY_STATIC_GROUND_FORCE_STATUS, "SUM_OF_ENEMY_STATIC_GROUND_DMG"),
      AgentTypes.PLAYER.getId(),
      optionalStream -> (double) optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .mapToDouble(value -> value
              .mapToDouble(v -> v.getCount() * v.getUnitTypeWrapper().getGroundWeapon()
                  .getDamagePerSecondNormalized()).sum())
          .sum());

  static final FactValueSetsForAgentType<UnitTypeStatus> SUM_OF_ENEMY_AIR_UNITS = new FactValueSetsForAgentType<>(
      new FactConverterID<>(49, ENEMY_AIR_FORCE_STATUS, "SUM_OF_ENEMY_AIR_UNITS"),
      AgentTypes.PLAYER.getId(),
      optionalStream -> (double) optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .mapToDouble(value -> value.mapToDouble(UnitTypeStatus::getCount).sum())
          .sum());

  static final FactValueSetsForAgentType<Double> ENEMY_BASES_UNPROTECTED_AGAINST_AIR = new FactValueSetsForAgentType<>(
      new FactConverterID<>(50, FactKeys.DPS_OF_ANTI_AIR_UNITS_ON_ENEMY_BASE,
          "ENEMY_BASES_UNPROTECTED_AGAINST_AIR"), AgentTypes.BASE_LOCATION.getId(),
      optionalStream -> optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .map(Stream::findFirst)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(aDouble -> aDouble < 60)
          .count());

  static final FactValueSetsForAgentType<Double> ENEMY_BASES_UNPROTECTED_AGAINST_GROUND = new FactValueSetsForAgentType<>(
      new FactConverterID<>(51, FactKeys.DPS_OF_ANTI_GROUND_UNITS_ON_ENEMY_BASE,
          "ENEMY_BASES_UNPROTECTED_AGAINST_GROUND"), AgentTypes.BASE_LOCATION.getId(),
      optionalStream -> optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .map(Stream::findFirst)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(aDouble -> aDouble < 60)
          .count());

  static final FactValueSetsForAgentType<UnitTypeStatus> SUM_OF_ENEMY_GROUND_DMG = new FactValueSetsForAgentType<>(
      new FactConverterID<>(52, ENEMY_GROUND_FORCE_STATUS, "SUM_OF_ENEMY_GROUND_DMG"),
      AgentTypes.PLAYER.getId(),
      optionalStream -> (double) optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .mapToDouble(value -> value.mapToDouble(v -> v.getCount()
              * v.getUnitTypeWrapper().getGroundWeapon().getDamagePerSecondNormalized()).sum())
          .sum());

  static final FactValueSet<Boolean> IS_ENEMY_BASE = new FactValueSet<>(
      new FactConverterID<>(324, FactKeys.IS_ENEMY_BASE, "IS_ENEMY_BASE"),
      aBoolean -> aBoolean.orElse(Stream.of(false)).findFirst().orElse(false) ? 1.0 : 0.0);

  //building
  static final FactValueSet<Boolean> IS_START_LOCATION = new FactValueSet<>(
      new FactConverterID<>(327, FactKeys.IS_START_LOCATION, "IS_START_LOCATION"),
      aBoolean -> aBoolean.orElse(Stream.of(false)).findFirst().orElse(false) ? 1.0 : 0.0);

  static final FactValueSet<Double> DMG_AIR_CAN_INFLICT_TO_GROUND_VS_SUFFER = new FactValueSet<>(
      new FactConverterID<>(335, FactKeys.DAMAGE_AIR_CAN_INFLICT_TO_GROUND_VS_SUFFER,
          "DMG_AIR_CAN_INFLICT_TO_GROUND_VS_SUFFER"),
      aDouble -> aDouble.orElse(Stream.of(0.0)).findFirst().orElse(0.0));

  static final FactValueSet<Double> DMG_GROUND_CAN_INFLICT_TO_GROUND_VS_SUFFER = new FactValueSet<>(
      new FactConverterID<>(336, FactKeys.DAMAGE_GROUND_CAN_INFLICT_TO_GROUND_VS_SUFFER,
          "DMG_GROUND_CAN_INFLICT_TO_GROUND_VS_SUFFER"),
      aDouble -> aDouble.orElse(Stream.of(0.0)).findFirst().orElse(0.0));

  static final FactValueSet<Double> DMG_AIR_CAN_INFLICT_TO_AIR_VS_SUFFER = new FactValueSet<>(
      new FactConverterID<>(337, FactKeys.DAMAGE_AIR_CAN_INFLICT_TO_AIR_VS_SUFFER,
          "DMG_AIR_CAN_INFLICT_TO_AIR_VS_SUFFER"),
      aDouble -> aDouble.orElse(Stream.of(0.0)).findFirst().orElse(0.0));

  static final FactValueSet<Double> DMG_GROUND_CAN_INFLICT_TO_AIR_VS_SUFFER = new FactValueSet<>(
      new FactConverterID<>(338, FactKeys.DAMAGE_GROUND_CAN_INFLICT_TO_AIR_VS_SUFFER,
          "DMG_GROUND_CAN_INFLICT_TO_AIR_VS_SUFFER"),
      aDouble -> aDouble.orElse(Stream.of(0.0)).findFirst().orElse(0.0));

  static final FactValueSet<Double> RATIO_GLOBAL_AIR_VS_ANTI_AIR_ON_BASE = new FactValueSet<>(
      new FactConverterID<>(339, FactKeys.RATIO_GLOBAL_AIR_VS_ANTI_AIR_ON_BASE,
          "RATIO_GLOBAL_AIR_VS_ANTI_AIR_ON_BASE"),
      aDouble -> aDouble.orElse(Stream.of(0.0)).findFirst().orElse(0.0));

  //"is unit morphing - command was issued"
  static final FactValueSet<Double> AIR_DISTANCE_TO_OUR_CLOSEST_BASE = new FactValueSet<>(
      new FactConverterID<>(340, FactKeys.AIR_DISTANCE_TO_OUR_CLOSEST_BASE,
          "AIR_DISTANCE_TO_OUR_CLOSEST_BASE"),
      aDouble -> aDouble.orElse(Stream.of(MAX_DISTANCE)).findFirst().orElse(MAX_DISTANCE));

  //worker
  static final FactValueSet<Double> AIR_DISTANCE_TO_ENEMY_CLOSEST_BASE = new FactValueSet<>(
      new FactConverterID<>(341, FactKeys.AIR_DISTANCE_TO_ENEMY_CLOSEST_BASE,
          "AIR_DISTANCE_TO_ENEMY_CLOSEST_BASE"),
      aDouble -> aDouble.orElse(Stream.of(MAX_DISTANCE)).findFirst().orElse(MAX_DISTANCE));

  static final FactValueSet<Double> GROUND_DISTANCE_TO_OUR_CLOSEST_BASE = new FactValueSet<>(
      new FactConverterID<>(342, FactKeys.GROUND_DISTANCE_TO_OUR_CLOSEST_BASE,
          "GROUND_DISTANCE_TO_OUR_CLOSEST_BASE"),
      aDouble -> aDouble.orElse(Stream.of(MAX_DISTANCE)).findFirst().orElse(MAX_DISTANCE));

  static final FactValueSet<Double> GROUND_DISTANCE_TO_ENEMY_CLOSEST_BASE = new FactValueSet<>(
      new FactConverterID<>(343, FactKeys.GROUND_DISTANCE_TO_ENEMY_CLOSEST_BASE,
          "GROUND_DISTANCE_TO_ENEMY_CLOSEST_BASE"),
      aDouble -> aDouble.orElse(Stream.of(MAX_DISTANCE)).findFirst().orElse(MAX_DISTANCE));

  static final FactValueSet<Double> RATIO_GLOBAL_GROUND_VS_ANTI_GROUND_ON_BASE = new FactValueSet<>(
      new FactConverterID<>(344, FactKeys.RATIO_GLOBAL_GROUND_VS_ANTI_GROUND_ON_BASE,
          "RATIO_GLOBAL_GROUND_VS_ANTI_GROUND_ON_BASE"),
      aDouble -> aDouble.orElse(Stream.of(0.0)).findFirst().orElse(0.0));

  static final FactValueSetsForAgentType<AUnitOfPlayer> IS_POOL_BUILT = new FactValueSetsForAgentType<>(
      new FactConverterID<>(407, FactKeys.REPRESENTS_UNIT, "IS_POOL_BUILT"),
      AgentTypes.SPAWNING_POOL.getId(),
      optionalStream -> optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .count() > 0 ? 1.0 : 0.0);

  //morphing to - count has cap 2
  static final FactValueSets<AUnitTypeWrapper> COUNT_OF_INCOMPLETE_EXTRACTORS = new FactValueSets<>(
      new FactConverterID<>(801, FactKeys.IS_MORPHING_TO, "COUNT_OF_INCOMPLETE_EXTRACTORS"),
      optionalStream -> Math.min(
          optionalStream
              .filter(Optional::isPresent)
              .map(Optional::get)
              .map(Stream::findFirst)
              .filter(Optional::isPresent)
              .map(Optional::get)
              .filter(AUnitTypeWrapper::isGasBuilding)
              .count(),
          2.0)
  );

  static final FactValueSets<AUnitTypeWrapper> COUNT_OF_INCOMPLETE_OVERLORDS = new FactValueSets<>(
      new FactConverterID<>(802, FactKeys.IS_MORPHING_TO, "COUNT_OF_INCOMPLETE_OVERLORDS"),
      optionalStream -> Math.min(optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(unitTypeWrapper -> unitTypeWrapper.equals(AUnitTypeWrapper.OVERLORD_TYPE))
          .count(), 2.0));

  static final FactValueSets<AUnitTypeWrapper> COUNT_OF_INCOMPLETE_DRONES = new FactValueSets<>(
      new FactConverterID<>(803, FactKeys.IS_MORPHING_TO, "COUNT_OF_INCOMPLETE_DRONES"),
      optionalStream -> Math.min(optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .map(Stream::findFirst)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(AUnitTypeWrapper::isWorker)
          .count(), 2.0));

  static final FactValueSets<AUnitTypeWrapper> COUNT_OF_INCOMPLETE_HATCHERIES = new FactValueSets<>(
      new FactConverterID<>(804, FactKeys.IS_MORPHING_TO, "COUNT_OF_INCOMPLETE_HATCHERIES"),
      optionalStream -> Math.min(optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(unitTypeWrapper -> unitTypeWrapper.equals(AUnitTypeWrapper.HATCHERY_TYPE))
          .count(), 2.0));

  static final FactValueSets<AUnitTypeWrapper> COUNT_OF_INCOMPLETE_AIRS = new FactValueSets<>(
      new FactConverterID<>(805, FactKeys.IS_MORPHING_TO, "COUNT_OF_INCOMPLETE_AIRS"),
      optionalStream -> Math.min(optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .map(Stream::findFirst)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(
              unitTypeWrapper -> unitTypeWrapper.isFlyer() && !unitTypeWrapper.isNotActuallyUnit()
                  && !unitTypeWrapper.equals(AUnitTypeWrapper.OVERLORD_TYPE))
          .count(), 2.0));

  static final FactValueSets<AUnitTypeWrapper> COUNT_OF_INCOMPLETE_RANGED = new FactValueSets<>(
      new FactConverterID<>(806, FactKeys.IS_MORPHING_TO, "COUNT_OF_INCOMPLETE_RANGED"),
      optionalStream -> Math.min(optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .map(Stream::findFirst)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(
              unitTypeWrapper -> !unitTypeWrapper.isWorker() && !unitTypeWrapper.isFlyer()
                  && !unitTypeWrapper.isNotActuallyUnit()
                  && !unitTypeWrapper.equals(AUnitTypeWrapper.ZERGLING_TYPE))
          .count(), 2.0));

  static final FactValueSets<AUnitTypeWrapper> COUNT_OF_INCOMPLETE_MELEE = new FactValueSets<>(
      new FactConverterID<>(807, FactKeys.IS_MORPHING_TO, "COUNT_OF_INCOMPLETE_MELEE"),
      optionalStream -> Math.min(optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(
              unitTypeWrapper -> unitTypeWrapper.equals(AUnitTypeWrapper.ZERGLING_TYPE))
          .count(), 2.0));


}
