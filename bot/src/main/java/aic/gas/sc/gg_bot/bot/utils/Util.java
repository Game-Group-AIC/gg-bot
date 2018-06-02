package aic.gas.sc.gg_bot.bot.utils;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APosition;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ATilePosition;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import bwapi.Game;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j;

/**
 * Useful utils for bots
 */
@Log4j
public class Util {

  private static final int offsetMin = 2;
  private static final int offsetStatic = 4;
  private static int offset = offsetStatic;
  private static final int offsetResources = 1;
  private static final int offsetDefense = -1;
  private static final int lookupDistance = 30;

  //cache
  private static final Map<ABaseLocationWrapper, Map<AUnitTypeWrapper, ATilePosition>> CACHE = new HashMap<>();

  /**
   * Returns a suitable TilePosition to build a given building type near specified TilePosition
   * aroundTile, or null if not found
   */
  public static Optional<ATilePosition> getBuildTile(AUnitTypeWrapper buildingType,
      ATilePosition currentTile, AUnit worker, Game game) {
    long start = System.currentTimeMillis();

    try {

      // Refinery, Assimilator, Extractor
      if (buildingType.isRefinery()) {
        return game.neutral().getUnits().stream()
            .filter(unit -> unit.getType() == UnitType.Resource_Vespene_Geyser)
            .map(Unit::getTilePosition)
            .min(Comparator.comparing(tilePosition -> tilePosition
                .getDistance(worker.getPosition().getATilePosition().getWrappedPosition())))
            .map(ATilePosition::wrap);
      }

      if (worker.getNearestBaseLocation().isPresent()) {
        if (!CACHE.containsKey(worker.getNearestBaseLocation().get())) {
          CACHE.put(worker.getNearestBaseLocation().get(), new HashMap<>());
        }
        Map<AUnitTypeWrapper, ATilePosition> map = CACHE.get(worker.getNearestBaseLocation().get());
        if (map.containsKey(buildingType)) {
          ATilePosition oldPosition = map.get(buildingType);
          if (canBuildHereCheck(buildingType, oldPosition, worker, game)) {
            return Optional.of(oldPosition);
          } else {
            map.remove(buildingType);
          }
        }
      }

      //find better place for defensive buildings...
      if (buildingType.isMilitaryBuilding()) {
        ATilePosition position = APosition.wrap(BWTA.getNearestChokepoint(currentTile
            .getWrappedPosition()).getCenter()).getATilePosition();
        if (!position.equals(currentTile) && position.distanceTo(currentTile) <= lookupDistance) {
          Optional<ATilePosition> toReturn = getBuildTile(buildingType, position, worker, game);
          if (toReturn.isPresent()) {
            if (worker.getNearestBaseLocation().isPresent()) {
              CACHE.get(worker.getNearestBaseLocation().get()).put(buildingType, toReturn.get());
            }
            return toReturn;
          }
        }
      }

      int maxDist = 3;
      while (maxDist < lookupDistance) {
        for (int i = Math.max(currentTile.getX() - maxDist, 0); i <= currentTile.getX()
            + maxDist; i++) {
          for (int j = Math.max(currentTile.getY() - maxDist, 0); j <= currentTile.getY()
              + maxDist; j++) {
//            if (game.canBuildHere(new TilePosition(i, j), buildingType.getType(), worker.getUnit(),
//                false)) {
            ATilePosition position = ATilePosition.wrap((new TilePosition(i, j)).makeValid());
            if (canBuildHereCheck(buildingType, position, worker, game)) {
              if (worker.getNearestBaseLocation().isPresent()) {
                CACHE.get(worker.getNearestBaseLocation().get()).put(buildingType, position);
              }
              return Optional.of(position);
            }
//            }
          }
        }
        maxDist += 3;
      }
      return Optional.empty();
    } finally {
      if (System.currentTimeMillis() - start >= 60) {
        offset = Math.max(offset--, offsetMin);
      } else {
        offset = offsetStatic;
      }
    }
  }

  public static boolean canBuildHereCheck(AUnitTypeWrapper buildingType, ATilePosition currentTile,
      AUnit worker, Game game) {

    // Refinery, Assimilator, Extractor
    if (buildingType.isRefinery()) {
      return game.neutral().getUnits().stream()
          .filter(unit -> unit.getType() == UnitType.Resource_Vespene_Geyser)
          .map(Unit::getTilePosition)
          .anyMatch(position -> position.getX() == currentTile.getX()
              && position.getY() == currentTile.getY());
    }

    return game.canBuildHere(currentTile.getWrappedPosition(), buildingType.getType(),
        worker.getUnit());
//        && canBuildHere(buildingType, currentTile, worker, game);
  }

//  private static boolean canBuildHere(AUnitTypeWrapper buildingType, ATilePosition currentTile,
//      AUnit worker, Game game) {
//    int defenseOffset = buildingType.isMilitaryBuilding() ? offsetDefense : 0;
//
//    // units that are blocking the tile
//    return Stream.concat(Stream.concat(game.getUnitsInRadius(currentTile.getWrappedPosition()
//        .toPosition(), lookupDistance).stream(), game.getMinerals().stream()), game.getGeysers()
//        .stream())
//        .filter(u -> u.getID() != worker.getUnitId())
//        .filter(u -> !u.isFlying())
//        .allMatch(u -> {
//          int resourceOffset = u.getType().isMineralField() || u.getType().isRefinery()
//              || u.getType() == UnitType.Resource_Vespene_Geyser ? offsetResources : 0;
//          return Math.abs(u.getTilePosition().getX() - currentTile.getX()) >= Math.max(offset +
//              resourceOffset + defenseOffset, 0) && Math.abs(u.getTilePosition().getY() -
//              currentTile.getY()) >= Math.max(offset + resourceOffset + defenseOffset, 0);
//        });
//  }

}
