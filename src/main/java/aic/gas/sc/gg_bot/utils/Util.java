package aic.gas.sc.gg_bot.utils;

import aic.gas.abstract_bot.model.game.wrappers.ATilePosition;
import aic.gas.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import bwapi.Game;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import java.util.Optional;

/**
 * Useful utils for bots
 */
public class Util {

  /**
   * Returns a suitable TilePosition to build a given building type near specified TilePosition
   * aroundTile, or null if not found
   */
  public static Optional<ATilePosition> getBuildTile(AUnitTypeWrapper buildingType,
      ATilePosition currentTile, AUnit worker, Game game) {
    int maxDist = 3;
    int stopDist = 40;

    // Refinery, Assimilator, Extractor
    if (buildingType.isRefinery()) {
      for (Unit n : game.neutral().getUnits()) {
        if ((n.getType() == UnitType.Resource_Vespene_Geyser) &&
            (Math.abs(n.getTilePosition().getX() - currentTile.getX()) < stopDist) &&
            (Math.abs(n.getTilePosition().getY() - currentTile.getY()) < stopDist)) {
          return Optional.ofNullable(ATilePosition.wrap(n.getTilePosition()));
        }
      }
      return Optional.empty();
    }

    //check current location first
    if (game
        .canBuildHere(currentTile.getWrappedPosition(), buildingType.getType(), worker.getUnit())
        && canBuildHere(buildingType, currentTile, worker, game)) {
      return Optional.of(currentTile);
    }

    while (maxDist < stopDist) {
      for (int i = currentTile.getX() - maxDist; i <= currentTile.getX() + maxDist; i++) {
        for (int j = currentTile.getY() - maxDist; j <= currentTile.getY() + maxDist; j++) {
          if (game.canBuildHere(new TilePosition(i, j), buildingType.getType(), worker.getUnit())) {
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
  }

  private static boolean canBuildHere(AUnitTypeWrapper buildingType, ATilePosition currentTile,
      AUnit worker, Game game) {

    // units that are blocking the tile
    for (Unit u : game.getAllUnits()) {
      if (u.getID() == worker.getUnitId()) {
        continue;
      }
      if ((Math.abs(u.getTilePosition().getX() - currentTile.getX()) < 4) && (
          Math.abs(u.getTilePosition().getY() - currentTile.getY()) < 4)) {
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
