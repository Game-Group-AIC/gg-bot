package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import bwta.BWTA;
import bwta.BaseLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

  //shortest path
  private final ConcurrentHashMap<ABaseLocationWrapper, List<ATilePosition>> path;

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

    //ground shortest path
    this.path = new ConcurrentHashMap<>(getBaseLocationsInCache().collect(Collectors
        .toMap(Function.identity(), o -> BWTA.getShortestPath(location.getTilePosition(),
            o.wrappedPosition.getTilePosition()).stream()
            .map(ATilePosition::wrap)
            .collect(Collectors.toList()))));

    //update other locations distances
    airDistance.forEach((loc, value) -> loc.airDistance.put(this, value));
    groundDistance.forEach((loc, value) -> loc.groundDistance.put(this, value));

    //update paths
    path.forEach((loc, value) -> {
      List<ATilePosition> path = new ArrayList<>(value);
      Collections.reverse(path);
      loc.path.put(this, path);
    });
  }

//  public Optional<ATilePosition> getNextTileOnPathToBase(ATilePosition myPosition) {
//    if (myPosition.distanceTo(tilePosition) <= 10) {
//      //already there
//      return Optional.ofNullable(tilePosition);
//    }
//
//    //heuristic - to compute distance by following path...
//    return path.values().stream()
//        .filter(path -> !path.isEmpty())
//        .map(path -> {
//          int index = IntStream.range(0, path.size())
//              .boxed()
//              .min(Comparator.comparingDouble(o -> path.get(o).distanceTo(myPosition)))
//              .get();
//          return path.subList(Math.min(index + 1, path.size() - 1), path.size());
//        })
//        .map(path -> new Pair<>(path.get(0), path.get(0).distanceTo(myPosition) +
//            IntStream.range(0, path.size() - 1)
//                .mapToDouble(i -> path.get(i).distanceTo(path.get(i + 1)))
//                .sum()))
//        .min(Comparator.comparingDouble(pair -> pair.second))
//        .map(pair -> pair.first);
//  }

//  public ATilePosition getNextTileOnPath(List<ATilePosition> allPositions,
//      ABaseLocationWrapper to) {
//    if (to.equals(this) || path.get(to).isEmpty()) {
//      return tilePosition;
//    }
//    List<ATilePosition> positions = path.get(to);
//    Optional<Integer> index = allPositions.stream()
//        .map(myPosition -> IntStream.range(0, positions.size())
//            .boxed()
//            .min(Comparator.comparingDouble(o -> positions.get(o).distanceTo(myPosition))))
//        .filter(Optional::isPresent)
//        .map(Optional::get)
//        .collect(Collectors.groupingBy(o -> o, Collectors.counting()))
//        .entrySet().stream()
//        .max(Comparator.comparingLong(Entry::getValue))
//        .map(Entry::getKey);
//    if (!index.isPresent()) {
//      return tilePosition;
//    }
//    return positions.get(Math.min(index.get() + 1, positions.size() - 1));
//  }

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
