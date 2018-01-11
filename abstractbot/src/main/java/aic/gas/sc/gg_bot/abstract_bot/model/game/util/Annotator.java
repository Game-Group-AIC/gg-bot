package aic.gas.sc.gg_bot.abstract_bot.model.game.util;

import bwapi.Color;
import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwta.BWTA;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Annotator
 */
public class Annotator {

  private final List<Position> bordersOfRegions;
  private final Player player;
  private final List<Player> playersToAnnotate;
  private final Game bwapi;

  public Annotator(List<Player> playersToAnnotate, Player player, Game bwapi) {
    this.bwapi = bwapi;
    this.playersToAnnotate = playersToAnnotate;
    this.player = player;
    this.bordersOfRegions = BWTA.getRegions().stream()
        .flatMap(region -> region.getPolygon().getPoints().stream())
        .distinct()
        .collect(Collectors.toList());
  }

  /**
   * Returns a <b>new</b> Position that represents the effect of moving this position by [deltaX,
   * deltaY].
   */
  private static Position translate(Position position, int deltaPixelX, int deltaPixelY) {
    return new Position(position.getX() + deltaPixelX, position.getY() + deltaPixelY);
  }

  public static void printMessage(String text, int x, int y, Game bwapi) {
    bwapi.drawTextScreen(x, y, text);
  }

  static void paintRectangle(Position position, int width, int height, Color color, Game bwapi) {
    bwapi.drawBoxMap(position, translate(position, width, height), color, false);
  }

  static void paintRectangleFilled(Position position, int width, int height, Color color,
      Game bwapi) {
    bwapi.drawBoxMap(position, translate(position, width, height), color, true);
  }

  static void paintCircle(Position position, int radius, Color color, Game bwapi) {
    bwapi.drawCircleMap(position, radius, color, false);
  }

  static void paintCircleFilled(Position position, int radius, Color color, Game bwapi) {
    bwapi.drawCircleMap(position, radius, color, true);
  }

  static void paintLine(Position start, Position end, Color color, Game bwapi) {
    bwapi.drawLineMap(start, end, color);
  }

  static void paintText(Position position, String text, Game bwapi) {
    bwapi.drawTextMap(position, text);
  }

  static void paintTextCentered(Position position, String text, Game bwapi) {

    //split text to multiple lines if it is too long
    int lines = 1;
    if (text.length() >= 25) {
      String[] words = text.split(" ");
      text = "";
      for (String s : words) {
        if (text.length() >= 25) {
          text = text + "\n";
          lines++;
        }
        text = text + s;
      }
    }

    bwapi.drawTextMap(translate(position, (int) (-2.7 * Math.min(text.length(), 25)), -2 * lines),
        text);
  }

  /**
   * Annotate map
   */
  public void annotate() {
    PainterForMap.annotateMap(bwapi, bordersOfRegions);
    PainterForUnits.annotateUnitsForPlayers(playersToAnnotate, player, bwapi);
  }

}
