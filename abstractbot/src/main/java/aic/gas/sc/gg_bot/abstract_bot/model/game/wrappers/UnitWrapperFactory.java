package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import bwapi.Unit;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;

/**
 * UnitWrapperFactory wraps units and handles refreshment of fields in wrappers to keep them current
 */
public class UnitWrapperFactory {

  private static final Map<Integer, AUnit.Enemy> enemyUnits = new ConcurrentHashMap<>();
  private static final Map<Integer, AUnit> resourceUnits = new ConcurrentHashMap<>();
  private static final Map<Integer, AUnitWithCommands> playersUnits = new ConcurrentHashMap<>();
  private static final Set<Integer> idsOfDeadUnits = Collections
      .newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());

  @Setter
  @Getter
  private static long refreshInfoAboutOwnUnitAfterFrames = 2;
  @Setter
  @Getter
  private static long refreshInfoAboutEnemyUnitAfterFrames = 4;
  @Setter
  @Getter
  private static long refreshInfoAboutResourceUnitAfterFrames = 20;

  //todo move units from maps if they become hostile/friendly?

  /**
   * Return all registered enemy units alive
   */
  public static Stream<AUnit.Enemy> getStreamOfAllAliveEnemyUnits() {
    return enemyUnits.values().stream()
        .filter(AUnit::isAlive);
  }

  /**
   * Return all registered player's units alive
   */
  public static Stream<AUnitOfPlayer> getStreamOfAllAlivePlayersUnits() {
    return playersUnits.values().stream()
        .filter(AUnit::isAlive)
        .map(aUnitWithCommands -> aUnitWithCommands);
  }

  /**
   * Clear cache
   */
  public static void clearCache() {
    enemyUnits.clear();
    resourceUnits.clear();
    playersUnits.clear();
    idsOfDeadUnits.clear();
  }

  /**
   * Returns true if unit is dead
   */
  static boolean isDead(Integer unitId) {
    return idsOfDeadUnits.contains(unitId);
  }

  /**
   * Register unit as dead
   */
  public static void unitDied(Unit unit) {
    idsOfDeadUnits.add(unit.getID());
  }

  /**
   * Wrap player's units to AUnitWithCommands
   */
  public static AUnitWithCommands getCurrentWrappedUnitToCommand(Unit unit, int frameCount,
      boolean isCreatingUnit) {
    AUnitWithCommands unitValue = playersUnits.get(unit.getID());

    //unit is present
    if (unitValue != null) {

      //update unit only if it is not current
      if (unitValue.getFrameCount() + getRefreshInfoAboutOwnUnitAfterFrames() < frameCount) {
        unitValue = new AUnitWithCommands(unit, isCreatingUnit, frameCount);
        playersUnits.put(unit.getID(), unitValue);
        wrapReferencedUnitsForUnit(unitValue, frameCount, isCreatingUnit);
      }
    } else {

      //register new unit
      unitValue = new AUnitWithCommands(unit, isCreatingUnit, frameCount);
      playersUnits.put(unit.getID(), unitValue);
      wrapReferencedUnitsForUnit(unitValue, frameCount, isCreatingUnit);
    }

    return unitValue;
  }

  /**
   * Wraps enemy unit
   */
  private static void wrapEnemyUnits(Unit unit, int frameCount, boolean isCreatingUnit) {
    AUnit.Enemy unitValue = enemyUnits.get(unit.getID());

    //unit is present
    if (unitValue != null) {

      //update unit only if it is not current
      if (unitValue.getFrameCount() + getRefreshInfoAboutEnemyUnitAfterFrames() < frameCount) {
        unitValue = new AUnit.Enemy(unit, isCreatingUnit, frameCount);
        enemyUnits.put(unit.getID(), unitValue);
        wrapReferencedUnitsForUnit(unitValue, frameCount, isCreatingUnit);
      }
    } else {

      //register new unit
      unitValue = new AUnit.Enemy(unit, isCreatingUnit, frameCount);
      enemyUnits.put(unit.getID(), unitValue);
      wrapReferencedUnitsForUnit(unitValue, frameCount, isCreatingUnit);
    }
  }

  /**
   * Wraps enemy unit
   */
  public static AUnit wrapResourceUnits(Unit unit, int frameCount, boolean isCreatingUnit) {
    AUnit unitValue = resourceUnits.get(unit.getID());

    //unit is present
    if (unitValue != null) {

      //update unit only if it is not current
      if (unitValue.getFrameCount() + getRefreshInfoAboutResourceUnitAfterFrames() < frameCount) {
        unitValue = new AUnit(unit, isCreatingUnit, frameCount);
        resourceUnits.put(unit.getID(), unitValue);
      }
    } else {

      //register new unit
      unitValue = new AUnit(unit, isCreatingUnit, frameCount);
      resourceUnits.put(unit.getID(), unitValue);
    }

    return unitValue;
  }

  /**
   * Wraps referenced units by enemy
   */
  private static void wrapReferencedUnitsForUnit(AUnit.Enemy unit, int frameCount,
      boolean isCreatingUnit) {

    //each enemy unit of enemy is player's in 1v1 game
    Set<Unit> playersUnits = new HashSet<>();
    playersUnits.addAll(unit.enemyUnitsInRadiusOfSight);
//        playersUnits.addAll(unit.enemyUnitsInWeaponRange);
    playersUnits.forEach(u -> getCurrentWrappedUnitToCommand(u, frameCount, isCreatingUnit));

    //wrap enemies units
    unit.friendlyUnitsInRadiusOfSight.forEach(u -> wrapEnemyUnits(u, frameCount, isCreatingUnit));

    //wrap resources
    unit.resourceUnitsInRadiusOfSight
        .forEach(u -> wrapResourceUnits(u, frameCount, isCreatingUnit));
  }

  /**
   * Wraps referenced units by player's unit
   */
  private static void wrapReferencedUnitsForUnit(AUnitWithCommands unit, int frameCount,
      boolean isCreatingUnit) {

    //wrap enemy units
    Set<Unit> enemyUnits = new HashSet<>();
    enemyUnits.addAll(unit.enemyUnitsInRadiusOfSight);
//        enemyUnits.addAll(unit.enemyUnitsInWeaponRange);
    enemyUnits.forEach(u -> wrapEnemyUnits(u, frameCount, isCreatingUnit));

    //wrap friendly units
    Set<Unit> playerUnits = new HashSet<>();
    playerUnits.addAll(unit.friendlyUnitsInRadiusOfSight);
    playerUnits.addAll(unit.loadedUnits);
    unit.transport.ifPresent(playerUnits::add);
    playerUnits.forEach(u -> getCurrentWrappedUnitToCommand(u, frameCount, isCreatingUnit));

    //wrap resources
    unit.resourceUnitsInRadiusOfSight
        .forEach(u -> wrapResourceUnits(u, frameCount, isCreatingUnit));
  }

  /**
   * Get wrapped enemy unit for passed unit
   */
  static Optional<AUnit.Enemy> getWrappedEnemyUnit(Integer unitId) {
    if (enemyUnits.containsKey(unitId)) {
      return Optional.ofNullable(enemyUnits.get(unitId));
    }
    return Optional.empty();
  }

  /**
   * Get wrapped resource unit for passed unit
   */
  static Optional<AUnit> getWrappedResourceUnit(Integer unitId) {
    if (resourceUnits.containsKey(unitId)) {
      return Optional.ofNullable(resourceUnits.get(unitId));
    }
    return Optional.empty();
  }

  /**
   * Get wrapped player's unit for passed unit
   */
  static Optional<AUnitOfPlayer> getWrappedPlayersUnit(Integer unitId) {
    if (playersUnits.containsKey(unitId)) {
      return Optional.ofNullable(playersUnits.get(unitId));
    }
    return Optional.empty();
  }

  /**
   * Get wrapped unit for passed unit
   */
  static Optional<AUnit> getWrappedUnit(Integer unitId) {
    if (enemyUnits.containsKey(unitId)) {
      return Optional.ofNullable(enemyUnits.get(unitId));
    }
    if (resourceUnits.containsKey(unitId)) {
      return Optional.ofNullable(resourceUnits.get(unitId));
    }
    if (playersUnits.containsKey(unitId)) {
      return Optional.ofNullable(playersUnits.get(unitId));
    }
    return Optional.empty();
  }
}
