package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * Wrapper for Unit with fields visible by all players
 */
public class AUnit {

  private static final Set<UnitType> resourcesTypes = Arrays
      .stream(new UnitType[]{UnitType.Resource_Mineral_Field,
          UnitType.Resource_Mineral_Field_Type_2, UnitType.Resource_Mineral_Field_Type_3,
          UnitType.Resource_Vespene_Geyser})
      .collect(Collectors.toSet());

  @Getter
  final Unit unit;
  //    final List<Unit> enemyUnitsInWeaponRange = new ArrayList<>();
//    final List<Integer> enemyUnitsInWeaponRangeIds;
  final List<Unit> friendlyUnitsInRadiusOfSight = new ArrayList<>();
  final List<Integer> friendlyUnitsInRadiusOfSightIds;
  final List<Unit> resourceUnitsInRadiusOfSight = new ArrayList<>();
  final List<Integer> resourceUnitsInRadiusOfSightIds;
  final List<Unit> enemyUnitsInRadiusOfSight = new ArrayList<>();
  final List<Integer> enemyUnitsInRadiusOfSightIds;
  @Getter
  private final int unitId;
  private final UnitType type;
  @Getter
  private final boolean exists;
  @Getter
  private final APosition position;
  @Getter
  private final boolean isFlying;
  @Getter
  private final boolean isSieged;
  @Getter
  private final boolean isStuck;
  @Getter
  private final boolean isLifted;
  @Getter
  private final boolean isMoving;
  @Getter
  private final int shields;
  @Getter
  private final double velocityY;
  @Getter
  private final int hitPoints;
  @Getter
  private final boolean isAccelerating;
  @Getter
  private final double velocityX;
  @Getter
  private final boolean isIdle;
  @Getter
  private final boolean isBeingGathered;
  @Getter
  private final boolean isBraking;
  @Getter
  private final boolean isEnsnared;
  @Getter
  private final boolean isLockedDown;
  @Getter
  private final boolean isParasited;
  @Getter
  private final boolean isIrradiated;
  @Getter
  private final boolean isBurrowed;
  @Getter
  private final boolean isAttacking;
  @Getter
  private final boolean isCloaked;
  @Getter
  private final boolean isConstructing;
  @Getter
  private final boolean isCarryingGas;
  @Getter
  private final boolean isInvincible;
  @Getter
  private final boolean isDetected;
  @Getter
  private final boolean isAttackFrame;
  @Getter
  private final boolean isGatheringGas;
  @Getter
  private final boolean isMaelstrommed;
  @Getter
  private final boolean isBeingHealed;
  @Getter
  private final boolean isMorphing;
  @Getter
  private final boolean isUnderDarkSwarm;
  @Getter
  private final boolean isTargetable;
  @Getter
  private final boolean isStasised;
  @Getter
  private final boolean isRepairing;
  @Getter
  private final boolean isPlagued;
  @Getter
  private final boolean isStimmed;
  @Getter
  private final boolean isUnderStorm;
  @Getter
  private final boolean isVisible;
  @Getter
  private final boolean isPowered;
  @Getter
  private final boolean isStartingAttack;
  @Getter
  private final boolean isUnderAttack;
  @Getter
  private final boolean isBeingConstructed;
  @Getter
  private final boolean isGatheringMinerals;
  @Getter
  private final boolean isCarryingMinerals;
  @Getter
  private final Optional<APlayer> player;
  @Getter
  private final Optional<bwta.Region> unitRegion;
  @Getter
  private final Optional<ABaseLocationWrapper> nearestBaseLocation;
  @Getter
  private final int frameCount;

  AUnit(Unit unit, boolean isCreatingUnit, int frameCount) {
    this.unit = unit;
    this.type = unit.getType();

    //identify position
    this.position = APosition.wrap(unit.getPosition());
    this.unitRegion = Optional.ofNullable(BWTA.getRegion(unit.getTilePosition()));
    if (unitRegion.isPresent()) {
      Optional<BaseLocation> nearestBaseLocation = unitRegion.get().getBaseLocations().stream()
          .min(Comparator
              .comparingInt(o -> o.getPosition().getApproxDistance(position.wrappedPosition)));
      this.nearestBaseLocation = nearestBaseLocation
          .flatMap(baseLocation -> Optional.ofNullable(ABaseLocationWrapper.wrap(baseLocation)));
    } else {
      this.nearestBaseLocation = Optional.empty();
    }

    this.frameCount = frameCount;

    //todo strange behaviour in bwmirror
    //units in weapon range
//        if (!isCreatingUnit && !unit.getPlayer().isNeutral()
//                && !resourcesTypes.contains(unit.getType())) {
//            addEnemyUnitsInWeaponRange(unit.getUnitsInWeaponRange(getType().getAirWeapon().type));
//            addEnemyUnitsInWeaponRange(unit.getUnitsInWeaponRange(getType().getGroundWeapon().type));
//        }

    //ids
//        this.enemyUnitsInWeaponRangeIds = this.enemyUnitsInWeaponRange.stream()
//                .map(Unit::getId)
//                .collect(Collectors.toList());

    //units in radius of sight
    if (!isCreatingUnit && !unit.getPlayer().isNeutral()
        && !resourcesTypes.contains(unit.getType())) {
      for (Unit unitInRange : unit.getUnitsInRadius(unit.getType().sightRange())) {
        if (unitInRange.getPlayer().getID() == unit.getPlayer().getID()) {
          this.friendlyUnitsInRadiusOfSight.add(unitInRange);
        } else {
          if (resourcesTypes.contains(unitInRange.getType()) && unitInRange.getPlayer()
              .isNeutral()) {
            this.resourceUnitsInRadiusOfSight.add(unitInRange);
          } else {
            if (unitInRange.getPlayer().getID() != unit.getPlayer().getID() && !unitInRange
                .getPlayer().isNeutral()) {
              this.enemyUnitsInRadiusOfSight.add(unitInRange);
            }
          }
        }
      }
    }

    //ids
    this.friendlyUnitsInRadiusOfSightIds = this.friendlyUnitsInRadiusOfSight.stream()
        .map(Unit::getID)
        .collect(Collectors.toList());
    this.resourceUnitsInRadiusOfSightIds = this.resourceUnitsInRadiusOfSight.stream()
        .map(Unit::getID)
        .collect(Collectors.toList());
    this.enemyUnitsInRadiusOfSightIds = this.enemyUnitsInRadiusOfSight.stream()
        .map(Unit::getID)
        .collect(Collectors.toList());

    this.exists = unit.exists();
    this.velocityX = unit.getVelocityX();
    this.isIdle = unit.isIdle();
    this.isFlying = unit.isFlying();
    this.isSieged = unit.isSieged();
    this.isStuck = unit.isStuck();
    this.isLifted = unit.isLifted();
    this.isMoving = unit.isMoving();
    this.shields = unit.getShields();
    this.velocityY = unit.getVelocityY();
    this.hitPoints = unit.getHitPoints();
    this.isAccelerating = unit.isAccelerating();
    this.isBeingGathered = unit.isBeingGathered();
    this.isBraking = unit.isBraking();
    this.isEnsnared = unit.isEnsnared();
    this.isLockedDown = unit.isLockedDown();
    this.isParasited = unit.isParasited();
    this.isIrradiated = unit.isIrradiated();
    this.isBurrowed = unit.isBurrowed();
    this.isAttacking = unit.isAttacking();
    this.isCloaked = unit.isCloaked();
    this.isConstructing = unit.isConstructing();
    this.isCarryingGas = unit.isCarryingGas();
    this.isInvincible = unit.isInvincible();
    this.isDetected = unit.isDetected();
    this.isAttackFrame = unit.isAttackFrame();
    this.isGatheringGas = unit.isGatheringGas();
    this.isMaelstrommed = unit.isMaelstrommed();
    this.isBeingHealed = unit.isBeingHealed();
    this.isMorphing = unit.isMorphing();
    this.isUnderDarkSwarm = unit.isUnderDarkSwarm();
    this.isTargetable = unit.isTargetable();
    this.isStasised = unit.isStasised();
    this.isRepairing = unit.isRepairing();
    this.isPlagued = unit.isPlagued();
    this.isStimmed = unit.isStimmed();
    this.isUnderStorm = unit.isUnderStorm();
    this.isVisible = unit.isVisible();
    this.isPowered = unit.isPowered();
    this.isStartingAttack = unit.isStartingAttack();
    this.isUnderAttack = unit.isUnderAttack();
    this.isBeingConstructed = unit.isBeingConstructed();
    this.isGatheringMinerals = unit.isGatheringMinerals();
    this.isCarryingMinerals = unit.isCarryingMinerals();
    this.player = APlayer.wrapPlayer(unit.getPlayer(), frameCount);
    unitId = unit.getID();
  }

  public boolean isDoingSomething() {
    return isGatheringGas() || isGatheringMinerals() ||
        isConstructing() || isMoving() || isAccelerating()
        || isMorphing() || isAttacking();
  }

  public AUnitTypeWrapper getType() {
    return WrapperTypeFactory.createFrom(type);
  }

//    private void addEnemyUnitsInWeaponRange(List<Unit> unitsInWeaponRange) {
//        for (Unit unitInWeaponRange : unitsInWeaponRange) {
//            if (unitInWeaponRange.getPlayer().isEnemy(unit.getPlayer())) {
//                this.enemyUnitsInWeaponRange.add(unitInWeaponRange);
//            }
//        }
//    }

  public boolean isFullyHealthy() {
    return getHitPoints() >= getType().getMaxHitPoints();
  }

  public double getHPPercent() {
    return ((double) getHitPoints()) / ((double) getType().getMaxHitPoints());
  }

  public boolean isWounded() {
    return getHitPoints() < getType().getMaxHitPoints();
  }

  public boolean isAlive() {
    return !UnitWrapperFactory.isDead(unitId);
  }

  /**
   * Returns true if this unit is capable of attacking <b>otherUnit</b>. For example Zerglings can't
   * attack flying targets and Corsairs can't attack ground targets.
   */
  public boolean canAttackThisKindOfUnit(AUnitOfPlayer otherUnit) {

    // Enemy is GROUND unit
    if (!otherUnit.getType().isFlyer()) {
      return getType().canAttackGroundUnits();
    } // Enemy is AIR unit
    else {
      return getType().canAttackAirUnits();
    }
  }

//    /**
//     * Returns <b>true</b> if this unit can attack <b>targetUnit</b> in terms of both min and max range
//     * conditions fulfilled.
//     *
//     * @param safetyMargin allowed error (in tiles) applied to the max distance condition
//     */
//    public boolean hasRangeToAttack(AUnit targetUnit, double safetyMargin) {
//        AWeaponTypeWrapper weaponAgainstThisUnit = getWeaponAgainst(targetUnit);
//        double dist = getPosition().distanceTo(targetUnit);
//        return !weaponAgainstThisUnit.isForType(WeaponType.None)
//                && weaponAgainstThisUnit.getMaxRange() <= (dist + safetyMargin)
//                && weaponAgainstThisUnit.getMinRange() >= dist;
//    }

  /**
   * Returns weapon that would be used to attack given target. If no such weapon, then
   * WeaponTypes.None will be returned.
   */
  public AWeaponTypeWrapper getWeaponAgainst(AUnit target) {
    if (!target.getType().isFlyer()) {
      return getType().getGroundWeapon();
    } else {
      return getType().getAirWeapon();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AUnit aUnit = (AUnit) o;

    return unitId == aUnit.unitId;
  }

  @Override
  public int hashCode() {
    return unitId;
  }

  /**
   * Returns true if unit is starting an attack or already in the attack frame animation.
   */
  public boolean isJustShooting() {
    return isAttackFrame() || isStartingAttack();
  }

  /**
   * Enemy
   */
  public static class Enemy extends AUnit {

    Enemy(Unit unit, boolean isCreatingUnit, int frameCount) {
      super(unit, isCreatingUnit, frameCount);
    }

//        public List<AUnitOfPlayer> getEnemyUnitsInWeaponRange() {
//            return enemyUnitsInWeaponRangeIds.stream()
//                    .map(UnitWrapperFactory::getWrappedPlayersUnit)
//                    .filter(Optional::isPresent)
//                    .map(Optional::get)
//                    .collect(Collectors.toList());
//        }

    public List<AUnit.Enemy> getFriendlyUnitsInRadiusOfSight() {
      return friendlyUnitsInRadiusOfSightIds.stream()
          .map(UnitWrapperFactory::getWrappedEnemyUnit)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toList());
    }

    public List<AUnit> getResourceUnitsInRadiusOfSight() {
      return resourceUnitsInRadiusOfSightIds.stream()
          .map(UnitWrapperFactory::getWrappedResourceUnit)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toList());
    }

    public List<AUnitOfPlayer> getEnemyUnitsInRadiusOfSight() {
      return enemyUnitsInRadiusOfSightIds.stream()
          .map(UnitWrapperFactory::getWrappedPlayersUnit)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toList());
    }

  }

  /**
   * Player's unit
   */
  public static class Players extends AUnit {

    Players(Unit unit, boolean isCreatingUnit, int frameCount) {
      super(unit, isCreatingUnit, frameCount);
    }

//        public List<AUnit.Enemy> getEnemyUnitsInWeaponRange() {
//            return enemyUnitsInWeaponRangeIds.stream()
//                    .map(UnitWrapperFactory::getWrappedEnemyUnit)
//                    .filter(Optional::isPresent)
//                    .map(Optional::get)
//                    .collect(Collectors.toList());
//        }

    public List<AUnitOfPlayer> getFriendlyUnitsInRadiusOfSight() {
      return friendlyUnitsInRadiusOfSightIds.stream()
          .map(UnitWrapperFactory::getWrappedPlayersUnit)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toList());
    }

    public List<AUnit> getResourceUnitsInRadiusOfSight() {
      return resourceUnitsInRadiusOfSightIds.stream()
          .map(UnitWrapperFactory::getWrappedResourceUnit)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toList());
    }

    public List<AUnit.Enemy> getEnemyUnitsInRadiusOfSight() {
      return enemyUnitsInRadiusOfSightIds.stream()
          .map(UnitWrapperFactory::getWrappedEnemyUnit)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toList());
    }

  }

}
