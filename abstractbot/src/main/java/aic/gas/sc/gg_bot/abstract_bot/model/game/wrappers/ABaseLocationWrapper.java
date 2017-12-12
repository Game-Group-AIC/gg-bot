package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import bwta.BaseLocation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

/**
 * Wrapper for BaseLocation
 */
public class ABaseLocationWrapper extends AbstractPositionWrapper<BaseLocation> {

  @Getter
  private final APosition position;

  @Getter
  private final ATilePosition tilePosition;

  @Getter
  private final boolean isMineralOnly, isStartLocation, isIsland;

  //todo other fields - regions,... paths to other positions + distances - to access it easily

  private ABaseLocationWrapper(BaseLocation location) {
    super(location, location.getX(), location.getY());
    this.tilePosition = ATilePosition.wrap(location.getTilePosition());
    this.position = APosition.wrap(location.getPosition());
    this.isStartLocation = location.isStartLocation();
    this.isIsland = location.isIsland();
    this.isMineralOnly = location.isMineralOnly();
  }

  /**
   * Wrap location
   */
  public static ABaseLocationWrapper wrap(BaseLocation toWrap) {
    Map<Integer, Map<Integer, AbstractPositionWrapper<?>>> positionsByCoordinates = cache
        .computeIfAbsent(BaseLocation.class, aClass -> new ConcurrentHashMap<>());
    Map<Integer, AbstractPositionWrapper<?>> positionsByYCoordinates = positionsByCoordinates
        .computeIfAbsent(toWrap.getX(), integer -> new ConcurrentHashMap<>());
    return (ABaseLocationWrapper) positionsByYCoordinates
        .computeIfAbsent(toWrap.getY(), integer -> new ABaseLocationWrapper(toWrap));
  }
}
