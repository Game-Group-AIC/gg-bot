package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import bwapi.Position;

/**
 * Class using default methods which are extracted from AUnitWithCommands class to separate this
 * functionality.
 *
 * @author Rafal Poniatowski <ravaelles@gmail.com>
 */
public interface IUnitActions {

  /**
   * Get reference on unit implementing this interface - to command it to execute action
   */
  AUnitWithCommands unit();

  /**
   * Attack unit
   */
  default boolean attack(AUnit target) {

    // Do NOT issue double orders
    if (unit().unit.isAttacking() && unit().unit.getTarget() != null && unit().unit.getTarget()
        .equals(target.unit)) {
      return false;
    } else {
      return unit().unit.attack(target.unit);
    }

  }

  default boolean attack(APosition target) {

    // Do NOT issue double orders
    if (unit().unit.isAttacking() && unit().unit.getTargetPosition() != null && unit().unit
        .getTargetPosition().equals(target)) {
      return false;
    } else {
      return unit().unit.attack(target.wrappedPosition);
    }
  }

  default boolean train(AUnitTypeWrapper unitToTrain) {
    return unit().unit.train(unitToTrain.type);
  }

  default boolean morph(AUnitTypeWrapper into) {
    return unit().unit.morph(into.type);
  }

  default boolean build(AUnitTypeWrapper buildingType, ATilePosition buildTilePosition) {
    return unit().unit.build(buildingType.type, buildTilePosition.wrappedPosition);
  }

  default boolean move(APosition target) {
    return unit().unit.move(target.wrappedPosition);
  }

  default boolean move(AbstractPositionWrapper<?> target) {
    return unit().unit.move(new Position(target.getX(), target.getY()));
  }

  /**
   * Orders the unit to patrol between its current position and the specified position. While
   * patrolling, units will attack and chase enemy units that they encounter, and then return u().to
   * its patrol route. Medics will automatically heal units and then return u().to their patrol
   * route. Parameters target The position to patrol to. shiftQueueCommand (optional) If this value
   * is true, then the order will be queued instead of immediately executed. If this value is
   * omitted, then the order will be executed immediately by default. Returns true if the command
   * was passed to Broodwar, and false if BWAPI determined that the command would fail. Note There
   * is a small chance for a command to fail after it has been passed to Broodwar. See also
   * isPatrolling, canPatrol
   */
  default boolean patrol(APosition target) {
    return unit().unit.patrol(target.wrappedPosition);
  }

  /**
   * Orders the unit to hold its position. Parameters shiftQueueCommand (optional) If this value is
   * true, then the order will be queued instead of immediately executed. If this value is omitted,
   * then the order will be executed immediately by default. Returns true if the command was passed
   * to Broodwar, and false if BWAPI determined that the command would fail. Note There is a small
   * chance for a command to fail after it has been passed to Broodwar. See also canHoldPosition,
   * isHoldingPosition
   */
  default boolean holdPosition() {
    return unit().unit.holdPosition();
  }

  /**
   * Orders the unit to stop. Parameters shiftQueueCommand (optional) If this value is true, then
   * the order will be queued instead of immediately executed. If this value is omitted, then the
   * order will be executed immediately by default. Returns true if the command was passed to
   * Broodwar, and false if BWAPI determined that the command would fail. Note There is a small
   * chance for a command to fail after it has been passed to Broodwar. See also canStop, isIdle
   */
  default boolean stop() {
    return unit().unit.stop();
  }

  /**
   * Orders the unit to follow the specified unit. Units that are following other units will not
   * perform any other actions such as attacking. They will ignore attackers. Parameters target The
   * target unit to start following. shiftQueueCommand (optional) If this value is true, then the
   * order will be queued instead of immediately executed. If this value is omitted, then the order
   * will be executed immediately by default. Returns true if the command was passed to Broodwar,
   * and false if BWAPI determined that the command would fail. Note There is a small chance for a
   * command to fail after it has been passed to Broodwar. See also isFollowing, canFollow,
   * getOrderTarget
   */
  default boolean follow(AUnit target) {
    return unit().unit.follow(target.unit);
  }

  /**
   * Orders the unit to gather the specified unit (must be mineral or refinery type). Parameters
   * target The target unit to gather from. shiftQueueCommand (optional) If this value is true, then
   * the order will be queued instead of immediately executed. If this value is omitted, then the
   * order will be executed immediately by default. Returns true if the command was passed to
   * Broodwar, and false if BWAPI determined that the command would fail. Note There is a small
   * chance for a command to fail after it has been passed to Broodwar. See also isGatheringGas,
   * isGatheringMinerals, canGather
   */
  default boolean gather(AUnit target) {
    return unit().unit.gather(target.unit);
  }

  /**
   * Orders the unit to return u().its cargo to a nearby resource depot such as a
   * CommandForIntention Center. Only workers that are carrying minerals or gas can be ordered to
   * return u().cargo. Parameters shiftQueueCommand (optional) If this value is true, then the order
   * will be queued instead of immediately executed. If this value is omitted, then the order will
   * be executed immediately by default. Returns true if the command was passed to Broodwar, and
   * false if BWAPI determined that the command would fail. Note There is a small chance for a
   * command to fail after it has been passed to Broodwar. See also isCarryingGas,
   * isCarryingMinerals, canReturnCargo
   */
  default boolean returnCargo() {
    return unit().unit.returnCargo();
  }

  /**
   * Orders the unit to repair the specified unit. Only Terran SCVs can be ordered to repair, and
   * the target must be a mechanical Terran unit or building. Parameters target The unit to repair.
   * shiftQueueCommand (optional) If this value is true, then the order will be queued instead of
   * immediately executed. If this value is omitted, then the order will be executed immediately by
   * default. Returns true if the command was passed to Broodwar, and false if BWAPI determined that
   * the command would fail. Note There is a small chance for a command to fail after it has been
   * passed to Broodwar. See also isRepairing, canRepair
   */
  default boolean repair(AUnit target) {
    return unit().unit.repair(target.unit);
  }

  /**
   * Orders the unit to burrow. Either the unit must be a Lurker, or the unit must be a Zerg ground
   * unit that is capable of Burrowing, and Burrow technology must be researched. Returns true if
   * the command was passed to Broodwar, and false if BWAPI determined that the command would fail.
   * Note There is a small chance for a command to fail after it has been passed to Broodwar. See
   * also unburrow, isBurrowed, canBurrow
   */
  default boolean burrow() {
    return unit().unit.burrow();
  }

  /**
   * Orders a burrowed unit to unburrow. Returns true if the command was passed to Broodwar, and
   * false if BWAPI determined that the command would fail. Note There is a small chance for a
   * command to fail after it has been passed to Broodwar. See also burrow, isBurrowed, canUnburrow
   */
  default boolean unburrow() {
    return unit().unit.unburrow();
  }

  /**
   * Orders the unit to cloak. Returns true if the command was passed to Broodwar, and false if
   * BWAPI determined that the command would fail. Note There is a small chance for a command to
   * fail after it has been passed to Broodwar. See also decloak, isCloaked, canCloak
   */
  default boolean cloak() {
    return unit().unit.cloak();
  }

  /**
   * Orders a cloaked unit to decloak. Returns true if the command was passed to Broodwar, and false
   * if BWAPI determined that the command would fail. Note There is a small chance for a command to
   * fail after it has been passed to Broodwar. See also cloak, isCloaked, canDecloak
   */
  default boolean decloak() {
    return unit().unit.decloak();
  }

  /**
   * Orders the unit to siege. Only works for Siege Tanks. Returns true if the command was passed to
   * Broodwar, and false if BWAPI determined that the command would fail. Note There is a small
   * chance for a command to fail after it has been passed to Broodwar. See also unsiege, isSieged,
   * canSiege
   */
  default boolean siege() {
    return unit().unit.siege();
  }

  /**
   * Orders the unit to unsiege. Only works for sieged Siege Tanks. Returns true if the command was
   * passed to Broodwar, and false if BWAPI determined that the command would fail. Note There is a
   * small chance for a command to fail after it has been passed to Broodwar. See also siege,
   * isSieged, canUnsiege
   */
  default boolean unsiege() {
    return unit().unit.unsiege();
  }

  /**
   * Orders the unit to lift. Only works for liftable Terran structures. Returns true if the command
   * was passed to Broodwar, and false if BWAPI determined that the command would fail. Note There
   * is a small chance for a command to fail after it has been passed to Broodwar. See also land,
   * isLifted, canLift
   */
  default boolean lift() {
    return unit().unit.lift();
  }

  /**
   * Orders the unit to land. Only works for Terran structures that are currently lifted. Parameters
   * target The tile position to land this structure at. Returns true if the command was passed to
   * Broodwar, and false if BWAPI determined that the command would fail. Note There is a small
   * chance for a command to fail after it has been passed to Broodwar. See also lift, isLifted,
   * canLand
   */
  default boolean land(ATilePosition target) {
    return unit().unit.land(target.wrappedPosition);
  }

  /**
   * Orders the unit to load the target unit. Only works if this unit is a Transport(Dropship,
   * Shuttle, Overlord ) or Bunker type. Parameters target The target unit to load into this
   * Transport(Dropship, Shuttle, Overlord ) or Bunker. shiftQueueCommand (optional) If this value
   * is true, then the order will be queued instead of immediately executed. If this value is
   * omitted, then the order will be executed immediately by default. Returns true if the command
   * was passed to Broodwar, and false if BWAPI determined that the command would fail. Note There
   * is a small chance for a command to fail after it has been passed to Broodwar. See also unload,
   * unloadAll, getLoadedUnits, isLoaded
   */
  default boolean load(AUnit target) {
    return unit().unit.load(target.unit);
  }

  /**
   * Orders the unit to unload the target unit. Only works for Transports(Dropships, Shuttles,
   * Overlords ) and Bunkers. Parameters target Unloads the target unit from this
   * Transport(Dropship, Shuttle, Overlord ) or Bunker. Returns true if the command was passed to
   * Broodwar, and false if BWAPI determined that the command would fail. Note There is a small
   * chance for a command to fail after it has been passed to Broodwar. See also load, unloadAll,
   * getLoadedUnits, isLoaded, canUnload, canUnloadAtPosition
   */
  default boolean unload(AUnit target) {
    return unit().unit.unload(target.unit);
  }

  /**
   * Orders the unit to unload all loaded units at the unit's current position. Only works for
   * Transports(Dropships, Shuttles, Overlords ) and Bunkers. Parameters shiftQueueCommand
   * (optional) If this value is true, then the order will be queued instead of immediately
   * executed. If this value is omitted, then the order will be executed immediately by default.
   * Returns true if the command was passed to Broodwar, and false if BWAPI determined that the
   * command would fail. Note There is a small chance for a command to fail after it has been passed
   * to Broodwar. See also load, unload, getLoadedUnits, isLoaded, canUnloadAll,
   * canUnloadAtPosition
   */
  default boolean unloadAll() {
    return unit().unit.unloadAll();
  }

  /**
   * Orders the unit to unload all loaded units at the unit's current position. Only works for
   * Transports(Dropships, Shuttles, Overlords ) and Bunkers. Parameters shiftQueueCommand
   * (optional) If this value is true, then the order will be queued instead of immediately
   * executed. If this value is omitted, then the order will be executed immediately by default.
   * Returns true if the command was passed to Broodwar, and false if BWAPI determined that the
   * command would fail. Note There is a small chance for a command to fail after it has been passed
   * to Broodwar. See also load, unload, getLoadedUnits, isLoaded, canUnloadAll,
   * canUnloadAtPosition
   */
  default boolean unloadAll(APosition target) {
    return unit().unit.unloadAll(target.wrappedPosition);
  }

  /**
   * Orders a SCV to stop constructing a structure. This leaves the structure in an incomplete state
   * until it is either cancelled, razed, or completed by another SCV. Returns true if the command
   * was passed to Broodwar, and false if BWAPI determined that the command would fail. Note There
   * is a small chance for a command to fail after it has been passed to Broodwar. See also
   * isConstructing, canHaltConstruction
   */
  default boolean haltConstruction() {
    return unit().unit.haltConstruction();
  }

  /**
   * Orders this unit to cancel and refund itself from begin constructed. Returns true if the
   * command was passed to Broodwar, and false if BWAPI determined that the command would fail. Note
   * There is a small chance for a command to fail after it has been passed to Broodwar. See also
   * isBeingConstructed, build, canCancelConstruction
   */
  default boolean cancelConstruction() {
    return unit().unit.cancelConstruction();
  }

  /**
   * Orders this unit to cancel and refund an add-on that is being constructed. Returns true if the
   * command was passed to Broodwar, and false if BWAPI determined that the command would fail. Note
   * There is a small chance for a command to fail after it has been passed to Broodwar. See also
   * canCancelAddon, buildAddon
   */
  default boolean cancelAddon() {
    return unit().unit.cancelAddon();
  }

  /**
   * Orders the unit to remove the specified unit from its training queue. Parameters slot
   * (optional) Identifies the slot that will be cancelled. If the specified value is at least 0,
   * then the unit in the corresponding slot from the list provided by getTrainingQueue will be
   * cancelled. If the value is either omitted or -2, then the last slot is cancelled. Note The
   * value of slot is passed directly to Broodwar. Other negative values have no effect. See also
   * train, cancelTrain, isTraining, getTrainingQueue, canCancelTrain, canCancelTrainSlot
   */
  default boolean cancelTrain() {
    return unit().unit.cancelTrain();
  }

  default boolean cancelTrain(int slot) {
    return unit().unit.cancelTrain(slot);
  }

  /**
   * Orders this unit to cancel and refund a unit that is morphing. Returns true if the command was
   * passed to Broodwar, and false if BWAPI determined that the command would fail. Note There is a
   * small chance for a command to fail after it has been passed to Broodwar. See also morph,
   * isMorphing, canCancelMorph
   */
  default boolean cancelMorph() {
    return unit().unit.cancelMorph();
  }

  /**
   * Orders this unit to cancel and refund a research that is in progress. Returns true if the
   * command was passed to Broodwar, and false if BWAPI determined that the command would fail. Note
   * There is a small chance for a command to fail after it has been passed to Broodwar. See also
   * research, isResearching, getTech, canCancelResearch
   */
  default boolean cancelResearch() {
    return unit().unit.cancelResearch();
  }

  /**
   * Orders this unit to research tech. Returns true if the command was passed to Broodwar, and
   * false if BWAPI determined that the command would fail. Note There is a small chance for a
   * command to fail after it has been passed to Broodwar. See also research, isResearching,
   * getTech, canCancelResearch
   */
  default boolean research(ATechTypeWrapper techType) {
    return unit().unit.research(techType.type);
  }


  /**
   * Orders this unit to build addon of this type. Returns true if the command was passed to
   * Broodwar, and false if BWAPI determined that the command would fail. Note There is a small
   * chance for a command to fail after it has been passed to Broodwar.
   */
  default boolean buildAddon(AUnitTypeWrapper unitType) {
    return unit().unit.buildAddon(unitType.type);
  }

  /**
   * Orders this unit to cancel and refund an upgrade that is in progress. Returns true if the
   * command was passed to Broodwar, and false if BWAPI determined that the command would fail. Note
   * There is a small chance for a command to fail after it has been passed to Broodwar. See also
   * upgrade, isUpgrading, getUpgrade, canCancelUpgrade
   */
  default boolean cancelUpgrade() {
    return unit().unit.cancelUpgrade();
  }

  /**
   * Orders the unit to use a technology. Parameters tech The technology type to use. target
   * (optional) If specified, indicates the target location or unit to use the tech on. If
   * unspecified, causes the tech to be used without a target (i.e. Stim Packs). Returns true if the
   * command was passed to Broodwar, and false if BWAPI determined that the command would fail. See
   * also canUseTechWithOrWithoutTarget, canUseTech, canUseTechWithoutTarget, canUseTechUnit,
   * canUseTechPosition, TechTypes
   */
  default boolean useTech(ATechTypeWrapper tech) {
    return unit().unit.useTech(tech.type);
  }

  default boolean useTech(ATechTypeWrapper tech, APosition target) {
    return unit().unit.useTech(tech.type, target.wrappedPosition);
  }

  default boolean useTech(ATechTypeWrapper tech, AUnitOfPlayer target) {
    return unit().unit.useTech(tech.type, target.unit);
  }

}
