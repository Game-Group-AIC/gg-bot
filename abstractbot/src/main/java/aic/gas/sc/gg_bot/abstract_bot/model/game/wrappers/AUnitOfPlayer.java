package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import bwapi.Order;
import bwapi.Unit;
import bwapi.UnitType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * Extension of unit wrapper with fields visible to player only
 */
public class AUnitOfPlayer extends AUnit.Players {

  final Optional<Unit> transport;
  final List<Unit> loadedUnits;
  @Getter
  private final boolean isLoaded;
  @Getter
  private final boolean hasNuke;
  @Getter
  private final boolean isBlind;
  private final List<UnitType> trainingQueue;
  @Getter
  private final int removeTimer;
  @Getter
  private final int spellCooldown;
  @Getter
  private final int energy;
  @Getter
  private final int scarabCount;
  @Getter
  private final int plagueTimer;
  @Getter
  private final int orderTimer;
  @Getter
  private final int stasisTimer;
  @Getter
  private final int ensnareTimer;
  @Getter
  private final int lockdownTimer;
  @Getter
  private final int stimTimer;
  private final Optional<Integer> transportId;
  private final List<Integer> loadedUnitsIds;
  @Getter
  private final boolean isUpgrading;
  @Getter
  private final boolean isTraining;
  @Getter
  private final boolean isResearching;
  @Getter
  private final boolean canAttackUnit;
  @Getter
  private final int remainingBuildTime;
  @Getter
  private final int remainingTrainTime;
  @Getter
  private final int remainingUpgradeTime;
  @Getter
  private final int airWeaponCooldown;
  @Getter
  private final int defenseMatrixTimer;
  @Getter
  private final int remainingResearchTime;
  @Getter
  private final int spaceRemaining;
  @Getter
  private final int defenseMatrixPoints;
  @Getter
  private final int irradiateTimer;
  @Getter
  private final int lastCommandFrame;
  @Getter
  private final int acidSporeCount;
  @Getter
  private final int spiderMineCount;
  @Getter
  private final int maelstromTimer;
  @Getter
  private final int groundWeaponCooldown;
  @Getter
  private final boolean isHoldingPosition;
  @Getter
  private final boolean isDefenseMatrixed;
  @Getter
  private final boolean isUnderDisruptionWeb;

  @Getter
  private final Optional<Unit> target;

  @Getter
  private final Optional<Order> order;

  @Getter
  private final Optional<Unit> orderTarget;

  @Getter
  private final Optional<APosition> targetPosition;

  @Getter
  private final Optional<APosition> orderTargetPosition;

  @Getter
  private final Optional<Order> secondaryOrder;

  @Getter
  private final Optional<AUnitCommand> lastCommand;

  AUnitOfPlayer(Unit unit, boolean isCreatingUnit, int frameCount) {
    super(unit, isCreatingUnit, frameCount);

    this.isLoaded = unit.isLoaded();
    this.hasNuke = unit.hasNuke();
    this.isBlind = unit.isBlind();
    this.trainingQueue = new ArrayList<>(unit.getTrainingQueue());
    this.removeTimer = unit.getRemoveTimer();
    this.spellCooldown = unit.getSpellCooldown();
    this.energy = unit.getEnergy();
    this.scarabCount = unit.getScarabCount();
    this.plagueTimer = unit.getPlagueTimer();
    this.orderTimer = unit.getOrderTimer();
    this.stasisTimer = unit.getStasisTimer();
    this.ensnareTimer = unit.getEnsnareTimer();
    this.lockdownTimer = unit.getLockdownTimer();
    this.stimTimer = unit.getStimTimer();

    if (!isCreatingUnit) {
      this.transport = Optional.ofNullable(unit.getTransport());
      if (unit.getTransport() != null) {
        this.transportId = Optional.of(unit.getTransport().getID());
      } else {
        this.transportId = Optional.empty();
      }
      this.loadedUnits = new ArrayList<>(unit.getLoadedUnits());
    } else {
      this.transport = Optional.empty();
      this.transportId = Optional.empty();
      this.loadedUnits = new ArrayList<>();
    }

    this.loadedUnitsIds = this.loadedUnits.stream()
        .map(Unit::getID)
        .collect(Collectors.toList());
    this.isUpgrading = unit.isUpgrading();
    this.isTraining = unit.isTraining();
    this.isResearching = unit.isResearching();
    this.canAttackUnit = unit.canAttackUnit();
    this.remainingBuildTime = unit.getRemainingBuildTime();
    this.remainingTrainTime = unit.getRemainingTrainTime();
    this.remainingUpgradeTime = unit.getRemainingUpgradeTime();
    this.airWeaponCooldown = unit.getAirWeaponCooldown();
    this.defenseMatrixTimer = unit.getDefenseMatrixTimer();
    this.remainingResearchTime = unit.getRemainingResearchTime();
    this.spaceRemaining = unit.getSpaceRemaining();
    this.defenseMatrixPoints = unit.getDefenseMatrixPoints();
    this.irradiateTimer = unit.getIrradiateTimer();
    this.lastCommandFrame = unit.getLastCommandFrame();
    this.acidSporeCount = unit.getAcidSporeCount();
    this.spiderMineCount = unit.getSpiderMineCount();
    this.maelstromTimer = unit.getMaelstromTimer();
    this.groundWeaponCooldown = unit.getGroundWeaponCooldown();
    this.isHoldingPosition = unit.isHoldingPosition();
    this.isDefenseMatrixed = unit.isDefenseMatrixed();
    this.isUnderDisruptionWeb = unit.isUnderDisruptionWeb();

    if (!isCreatingUnit) {
      this.target = Optional.ofNullable(unit.getTarget());
      this.orderTarget = Optional.ofNullable(unit.getOrderTarget());
    } else {
      this.target = Optional.empty();
      this.orderTarget = Optional.empty();
    }

    this.order = Optional.ofNullable(unit.getOrder());
    this.lastCommand = AUnitCommand.creteOrEmpty(unit.getLastCommand());
    if (unit.getTargetPosition() != null) {
      this.targetPosition = Optional.ofNullable(APosition.wrap(unit.getTargetPosition()));
    } else {
      this.targetPosition = Optional.empty();
    }
    this.secondaryOrder = Optional.ofNullable(unit.getSecondaryOrder());
    if (unit.getOrderTargetPosition() != null) {
      this.orderTargetPosition = Optional.ofNullable(APosition.wrap(unit.getOrderTargetPosition()));
    } else {
      this.orderTargetPosition = Optional.empty();
    }
  }

  public List<AUnitTypeWrapper> getTrainingQueue() {
    return trainingQueue.stream()
        .map(WrapperTypeFactory::createFrom)
        .collect(Collectors.toList());
  }

  public Optional<AUnitOfPlayer> getTransport() {
    return transportId.flatMap(UnitWrapperFactory::getWrappedPlayersUnit);
  }

  public List<AUnitOfPlayer> getLoadedUnits() {
    return loadedUnitsIds.stream()
        .map(UnitWrapperFactory::getWrappedPlayersUnit)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  /**
   * Returns true if this unit is capable of attacking <b>otherUnit</b>. For example Zerglings can't
   * attack flying targets and Corsairs can't attack ground targets.
   */
  public boolean canAttackThisKindOfUnit(AUnitOfPlayer otherUnit) {

    // Enemy is GROUND unit
    if (!otherUnit.getType().isFlyer()) {
      return getType().canAttackGroundUnits() && getGroundWeaponCooldown() == 0;
    } // Enemy is AIR unit
    else {
      return getType().canAttackAirUnits() && getAirWeaponCooldown() == 0;
    }
  }

}
