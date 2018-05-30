package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import bwapi.TilePosition;
import java.util.Optional;
import lombok.Getter;

/**
 * Wrapper for TilePosition
 */
public class ATilePosition extends AbstractPositionWrapper<TilePosition> {

  public static final int SIZE_IN_PIXELS = TilePosition.SIZE_IN_PIXELS;

  @Getter
  private final double length;

  private ATilePosition(TilePosition tilePosition) {
    super(tilePosition, tilePosition.getX(), tilePosition.getY());
    this.length = tilePosition.getLength();
  }

  /**
   * Wrap position
   */
  public static ATilePosition wrap(TilePosition toWrap) {
    return cacheForTilePositions.computeIfAbsent(new Coordinates(toWrap.getX(), toWrap.getY()),
        integer -> new ATilePosition(toWrap));
  }

  static Optional<ATilePosition> creteOrEmpty(TilePosition tilePosition) {
    if (tilePosition == null) {
      return Optional.empty();
    }
    return Optional.of(new ATilePosition(tilePosition));
  }
}
