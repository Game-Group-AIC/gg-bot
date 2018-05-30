package aic.gas.sc.gg_bot.bot.utils;

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
import java.util.Optional;
import lombok.extern.log4j.Log4j;

/**
 * Useful utils for bots
 */
@Log4j
public class Util {

  private static int offset = 3;
  private static int offsetStatic = 3;

  /**
   * Returns a suitable TilePosition to build a given building type near specified TilePosition
   * aroundTile, or null if not found
   */
  public static Optional<ATilePosition> getBuildTile(AUnitTypeWrapper buildingType,
      ATilePosition currentTile, AUnit worker, Game game) {
    long start = System.currentTimeMillis();

    try {
      int maxDist = 3;
      int stopDist = 40;

      // Refinery, Assimilator, Extractor
      if (buildingType.isRefinery()) {
        return game.neutral().getUnits().stream()
            .filter(unit -> unit.getType() == UnitType.Resource_Vespene_Geyser)
            .map(Unit::getTilePosition)
            .min(Comparator.comparing(tilePosition -> tilePosition
                .getDistance(worker.getPosition().getATilePosition().getWrappedPosition())))
            .map(ATilePosition::wrap);
      }

      //find better place for defensive buildings...
      if (buildingType.isMilitaryBuilding()) {
        ATilePosition position = APosition
            .wrap(BWTA.getNearestChokepoint(currentTile.getWrappedPosition()).getCenter())
            .getATilePosition();
        if (!position.equals(currentTile) && position.distanceTo(currentTile) <= 40) {
          Optional<ATilePosition> toReturn = getBuildTile(buildingType, position, worker, game);
          if (toReturn.isPresent()) {
            return toReturn;
          }
        }
      }

      while (maxDist < stopDist) {
        for (int i = currentTile.getX() - maxDist; i <= currentTile.getX() + maxDist; i++) {
          for (int j = currentTile.getY() - maxDist; j <= currentTile.getY() + maxDist; j++) {
            if (game.canBuildHere(new TilePosition(i, j), buildingType.getType(), worker.getUnit(),
                false)) {
              ATilePosition position = ATilePosition.wrap(new TilePosition(i, j));
              if (canBuildHere(buildingType, position, worker, game)) {
                return Optional.ofNullable(position);
              }
            }
          }
        }
        maxDist += 2;
      }
      return Optional.empty();
    } finally {
      if (System.currentTimeMillis() - start >= 60) {
        offset = Math.max(offset--, 0);
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

    return canBuildHere(buildingType, currentTile, worker, game);
  }

  private static boolean canBuildHere(AUnitTypeWrapper buildingType, ATilePosition currentTile,
      AUnit worker, Game game) {

    // units that are blocking the tile
    for (Unit u : game.getAllUnits()) {
      if (u.getID() == worker.getUnitId()) {
        continue;
      }
      if ((Math.abs(u.getTilePosition().getX() - currentTile.getX()) < offset)
          && (Math.abs(u.getTilePosition().getY() - currentTile.getY()) < offset)) {
        return false;
      }
    }

    // creep for Zerg
    if (buildingType.getType().requiresCreep()) {
      for (int k = currentTile.getX(); k <= currentTile.getX() + buildingType.getType().tileWidth();
          k++) {
        for (int l = currentTile.getY();
            l <= currentTile.getY() + buildingType.getType().tileHeight(); l++) {
          if (!game.hasCreep(k, l)) {
            return false;
          }
        }
      }
    }
    return true;
  }

}
