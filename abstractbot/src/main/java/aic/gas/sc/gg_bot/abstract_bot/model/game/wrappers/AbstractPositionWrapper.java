package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Template for wrapper with static position.
 */
public abstract class AbstractPositionWrapper<T> {

  //cache to store position objects
  static Map<Class<?>, Map<Coordinates, AbstractPositionWrapper<?>>> cache = new ConcurrentHashMap<>();

  @Getter
  final T wrappedPosition;

  @Getter
  private final int x, y;

  AbstractPositionWrapper(T wrappedPosition, int x, int y) {
    this.wrappedPosition = wrappedPosition;
    this.x = x;
    this.y = y;
  }

  @Override
  public String toString() {
    return "Position{" +
        "x=" + x +
        ", y=" + y +
        '}';
  }

  /**
   * Clear cache
   */
  public static void clearCache() {
    cache.clear();
  }

  /**
   * Returns distance from one position to other in build tiles. One build tile equals to 32 pixels.
   * Usage of build tiles instead of pixels is preferable, because it's easier to imagine distances
   * if one knows building dimensions.
   */
  public double distanceTo(AbstractPositionWrapper<?> other) {
    return distanceTo(x, y, other.getX(), other.getY());
  }

  public boolean isOnSameCoordinates(AbstractPositionWrapper<?> other) {
    return x == other.x && y == other.y;
  }

  private double distanceTo(int oneX, int oneY, int otherX, int otherY) {
    int dx = oneX - otherX;
    int dy = oneY - otherY;
    return Math.sqrt(dx * dx + dy * dy);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AbstractPositionWrapper<?> that = (AbstractPositionWrapper<?>) o;

    if (x != that.x) {
      return false;
    }
    return y == that.y;
  }

  @Override
  public int hashCode() {
    int result = x;
    result = 31 * result + y;
    return result;
  }

  @EqualsAndHashCode
  @AllArgsConstructor
  @Getter
  protected static class Coordinates {

    private final int x, y;
  }
}
