package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import bwapi.TilePosition;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
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
    Map<Integer, Map<Integer, AbstractPositionWrapper<?>>> positionsByCoordinates = cache
        .computeIfAbsent(TilePosition.class, aClass -> new ConcurrentHashMap<>());
    Map<Integer, AbstractPositionWrapper<?>> positionsByYCoordinates = positionsByCoordinates
        .computeIfAbsent(toWrap.getX(), integer -> new ConcurrentHashMap<>());
    return (ATilePosition) positionsByYCoordinates
        .computeIfAbsent(toWrap.getY(), integer -> new ATilePosition(toWrap));
  }

  static Optional<ATilePosition> creteOrEmpty(TilePosition tilePosition) {
    if (tilePosition == null) {
      return Optional.empty();
    }
    return Optional.of(new ATilePosition(tilePosition));
  }
}
