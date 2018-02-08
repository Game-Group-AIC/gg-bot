package aic.gas.sc.gg_bot.abstract_bot.model.game.util;

import aic.gas.sc.gg_bot.abstract_bot.model.UnitTypeStatus;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//TODO code review
public class Utils {

  public static double computeOurVsEnemyForceRatio(Optional<Stream<UnitTypeStatus>> ourForce,
      Optional<Stream<UnitTypeStatus>> enemyForce) {
    double enemyForceSupply = enemyForce.orElse(Stream.empty())
        .filter(unitTypeStatus -> !unitTypeStatus.getUnitTypeWrapper().isWorker())
        .mapToDouble(
            unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().supplyRequired()
                * unitTypeStatus.getCount())
        .sum();
    double ourForceSupply = ourForce.orElse(Stream.empty())
        .filter(unitTypeStatus -> !unitTypeStatus.getUnitTypeWrapper().isWorker())
        .mapToDouble(
            unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().supplyRequired()
                * unitTypeStatus.getCount())
        .sum();
    return computeRatio(ourForceSupply, enemyForceSupply);
  }

  public static double computeRangedVsMeleeDamageRatio(Optional<Stream<UnitTypeStatus>> units) {
    Set<UnitTypeStatus> unitTypeStatusSet = units.orElse(Stream.empty())
        .collect(Collectors.toSet());
    double rangedDamage = unitTypeStatusSet.stream()
        .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().getShootRangeGround() > 0)
        .mapToDouble(
            unitTypeStatus ->
                unitTypeStatus.getUnitTypeWrapper().getGroundWeapon().getDamagePerSecondNormalized()
                    * unitTypeStatus.getCount())
        .sum();
    double meleeDamage = unitTypeStatusSet.stream()
        .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().isMelee())
        .mapToDouble(
            unitTypeStatus ->
                unitTypeStatus.getUnitTypeWrapper().getGroundWeapon().getDamagePerSecondNormalized()
                    * unitTypeStatus.getCount())
        .sum();
    return computeRatio(rangedDamage, meleeDamage);
  }

  public static double computeRatio(double numerator, double denominator) {
    double ratio;
    if (numerator == 0 && denominator == 0) {
      //armies are on pair
      ratio = 1.0;
    } else if (numerator == 0) {
      //enemy has big edge
      ratio = 0.5;
    } else if (denominator == 0) {
      //we have edge (better to set lower then 2 - the game is not fully observable)
      ratio = 1.5;
    } else {
      //cap to interval 0.5 - 2.0
      ratio = Math.max(Math.min(numerator / denominator, 2.0), 0.5);
    }
    return ratio;
  }

  public static double computeDifferenceInBases(Optional<Stream<ABaseLocationWrapper>> ourBases,
      Optional<Stream<ABaseLocationWrapper>> enemyBases) {
    long enemyBasesCount = enemyBases.map(Stream::count).orElse(1L);
    double ourBasesCount = ourBases.map(Stream::count).orElse(0L);
    return ourBasesCount - enemyBasesCount;
  }

}
