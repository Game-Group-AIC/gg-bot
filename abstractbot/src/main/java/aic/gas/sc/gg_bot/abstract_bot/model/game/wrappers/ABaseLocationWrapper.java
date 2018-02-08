package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import bwta.BaseLocation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

/**
 * Wrapper for BaseLocation
 */
public class ABaseLocationWrapper extends AbstractPositionWrapper<BaseLocation> {

  public static double MAX_DISTANCE = 10000;

  @Getter
  private final APosition position;

  @Getter
  private final ATilePosition tilePosition;

  @Getter
  private final boolean isMineralOnly, isStartLocation, isIsland;

  //distances
  private final ConcurrentHashMap<ABaseLocationWrapper, Double> airDistance;
  private final ConcurrentHashMap<ABaseLocationWrapper, Double> groundDistance;

  //todo other fields - regions,... paths to other positions + distances - to access it easily

  private ABaseLocationWrapper(BaseLocation location) {
    super(location, location.getX(), location.getY());
    this.tilePosition = ATilePosition.wrap(location.getTilePosition());
    this.position = APosition.wrap(location.getPosition());
    this.isStartLocation = location.isStartLocation();
    this.isIsland = location.isIsland();
    this.isMineralOnly = location.isMineralOnly();

    //compute distances
    this.airDistance = new ConcurrentHashMap<>(getBaseLocationsInCache()
        .collect(Collectors.toMap(Function.identity(),
            o -> o.getWrappedPosition().getAirDistance(location))));
    this.groundDistance = new ConcurrentHashMap<>(getBaseLocationsInCache()
        .collect(Collectors.toMap(Function.identity(),
            o -> o.getWrappedPosition().getGroundDistance(location))));

    //update other locations distances
    airDistance.forEach((loc, value) -> loc.airDistance.put(this, value));
    groundDistance.forEach((loc, value) -> loc.groundDistance.put(this, value));
  }

  public double getAirDistanceToBase(ABaseLocationWrapper otherLocation) {
    return airDistance.getOrDefault(otherLocation, MAX_DISTANCE);
  }

  public double getGroundDistanceToBase(ABaseLocationWrapper otherLocation) {
    return groundDistance.getOrDefault(otherLocation, MAX_DISTANCE);
  }

  private static Stream<ABaseLocationWrapper> getBaseLocationsInCache() {
    return cache.computeIfAbsent(BaseLocation.class, aClass -> new ConcurrentHashMap<>())
        .values().stream()
        .map(base -> (ABaseLocationWrapper) base);
  }

  /**
   * Wrap location
   */
  public synchronized static ABaseLocationWrapper wrap(BaseLocation toWrap) {
    Map<Coordinates, AbstractPositionWrapper<?>> positionsByCoordinates = cache
        .computeIfAbsent(BaseLocation.class, aClass -> new ConcurrentHashMap<>());
    return (ABaseLocationWrapper) positionsByCoordinates
        .computeIfAbsent(new Coordinates(toWrap.getX(), toWrap.getY()),
            integer -> new ABaseLocationWrapper(toWrap));
  }

}
