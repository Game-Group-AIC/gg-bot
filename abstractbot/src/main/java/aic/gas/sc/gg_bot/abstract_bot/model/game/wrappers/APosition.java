package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import bwapi.Position;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

/**
 * Wrapper for BWMirror Position
 */
public class APosition extends AbstractPositionWrapper<Position> {

  @Getter
  private final ATilePosition aTilePosition;

  @Getter
  private final double length;

  private APosition(Position p) {
    super(p, p.getX(), p.getY());
    this.length = p.getLength();
    this.aTilePosition = ATilePosition.wrap(p.toTilePosition());
  }

  /**
   * Wrap position
   */
  public static APosition wrap(Position toWrap) {
    Map<Coordinates, AbstractPositionWrapper<?>> positionsByCoordinates = cache
        .computeIfAbsent(Position.class, aClass -> new ConcurrentHashMap<>());
    return (APosition) positionsByCoordinates
        .computeIfAbsent(new Coordinates(toWrap.getX(), toWrap.getY()),
            integer -> new APosition(toWrap));
  }

  /**
   * Returns X coordinate in tiles
   */
  public int getTileX() {
    return getX() / ATilePosition.SIZE_IN_PIXELS;
  }

  /**
   * Returns Y coordinate in tiles
   */
  public int getTileY() {
    return getY() / ATilePosition.SIZE_IN_PIXELS;
  }

}
