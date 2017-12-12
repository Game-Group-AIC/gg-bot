package aic.gas.sc.gg_bot.abstract_bot.model.game.util;

import bwapi.Color;
import bwapi.Game;
import bwapi.Position;
import bwta.BWTA;
import java.util.List;

/**
 * Class to annotate game map with map information
 */
class PainterForMap {

  private static final int dotRadius = 5;

  static void annotateMap(Game bwapi, List<Position> bordersOfRegions) {
    paintChokePoints(bwapi);
    paintBasePoints(bwapi);
    paintBoardersOfRegions(bwapi, bordersOfRegions);
    paintRegionsPoints(bwapi);
  }

  private static void paintBoardersOfRegions(Game bwapi, List<Position> bordersOfRegions) {
    bordersOfRegions
        .forEach(position -> Annotator.paintCircleFilled(position, 1, Color.Orange, bwapi));
  }

  private static void paintRegionsPoints(Game bwapi) {
    BWTA.getRegions().forEach(
        region -> Annotator.paintCircleFilled(region.getCenter(), dotRadius, Color.Blue, bwapi));
  }

  private static void paintChokePoints(Game bwapi) {
    BWTA.getChokepoints().forEach(chokingPoint -> Annotator
        .paintCircleFilled(chokingPoint.getCenter(), dotRadius, Color.Red, bwapi));
  }

  private static void paintBasePoints(Game bwapi) {
    BWTA.getBaseLocations().forEach(chokingPoint -> Annotator
        .paintCircleFilled(chokingPoint.getPosition(), dotRadius, Color.Green, bwapi));
  }

}
