package aic.gas.sc.gg_bot.replay_parser.model.clustering.pso;

import aic.gas.sc.gg_bot.abstract_bot.utils.VectorNormalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(of = {"velocity", "center", "isEnabled", "pointsInCluster"})
public class Centroid implements ICentroid {

  private double isEnabled = 0.5;

  @Getter
  private Vec center;
  @Getter
  private final List<DistanceTuple<DataPointWithDistanceCache>> pointsInCluster = new ArrayList<>();
  private Set<Vec> vecs = new HashSet<>();

  //precomputed
  @Getter
  private OptionalDouble maximalDistanceBetweenPoints = OptionalDouble.empty();
  @Getter
  private OptionalDouble averageDistanceToCenter = OptionalDouble.empty();
  @Getter
  private OptionalDouble maximalDistanceToCenter = OptionalDouble.empty();
  @Getter
  private OptionalDouble averageDistanceBetweenPoints = OptionalDouble.empty();

  //precomputed

  private final List<DistanceTuple<DataPointWithDistanceCache>> pointsInClusterToMove = new ArrayList<>();
  private final double[] velocity;

  public Centroid(Vec center, double[] velocity, List<Bound> bounds) {
    this.center = center;
    this.velocity = velocity;
    updateByVelocity(bounds);
  }

  public void nextIteration() {
    pointsInCluster.addAll(pointsInClusterToMove);
    pointsInClusterToMove.clear();
    vecs = pointsInCluster.stream()
        .map(dataPointDistanceTuple -> dataPointDistanceTuple.t.getDataPoint().getNumericalValues())
        .collect(Collectors.toSet());

    //precompute
    //TODO too slow
//    averageDistanceBetweenPoints = IntStream.range(0, pointsInCluster.size())
//        .boxed()
//        .flatMap(i -> IntStream.range(i + 1, pointsInCluster.size())
//            .boxed()
//            .map(j -> new Tuple(j, pointsInCluster.get(i).t.getDistance(pointsInCluster.get(j).t)))
//            .flatMap(tuple -> IntStream.range(0,
//                pointsInCluster.get(i).t.getCount() * pointsInCluster.get(tuple.index).t.getCount())
//                .boxed()
//                .map(integer -> tuple.distance)))
//        .mapToDouble(value -> value)
//        .average();
//    maximalDistanceBetweenPoints = IntStream.range(0, pointsInCluster.size())
//        .boxed()
//        .flatMap(i -> IntStream.range(i + 1, pointsInCluster.size())
//            .boxed()
//            .map(j -> pointsInCluster.get(i).t.getDistance(pointsInCluster.get(j).t)))
//        .mapToDouble(value -> value)
//        .max();
    averageDistanceToCenter = pointsInCluster.stream()
        .flatMap(tuple -> IntStream.range(0, tuple.t.getCount())
            .boxed()
            .map(value -> tuple.distance))
        .mapToDouble(value -> value)
        .average();
    maximalDistanceToCenter = pointsInCluster.stream()
        .mapToDouble(value -> value.distance)
        .max();
  }

  public boolean hasPointsInCluster() {
    return !pointsInCluster.isEmpty();
  }

  public int countOfPointsInCluster() {
    return pointsInCluster.stream()
        .mapToInt(value -> value.t.getCount())
        .sum();
  }

  public boolean hasPointInCluster(Vec vec) {
    return vecs.contains(vec);
  }

  public double getDistance(Vec vec) {
    return VectorNormalizer.DISTANCE_FUNCTION.dist(center, vec);
  }

  public double getDistance(DataPointWithDistanceCache point) {
    return VectorNormalizer.DISTANCE_FUNCTION
        .dist(center, point.getDataPoint().getNumericalValues());
  }

  public void update(double learningRateLoc, double randomCofLoc, ICentroid localBest,
      double learningRateGlo, double randomCofGlo, ICentroid globalBest, List<Bound> bounds) {

    //update velocity
    for (int i = 0; i < center.length(); i++) {
      velocity[i] = bounds.get(i).getValueInBounds(velocity[i] + (learningRateLoc * randomCofLoc *
          (localBest.getCenter().get(i) - center.get(i))) + (learningRateGlo * randomCofGlo
          * (globalBest.getCenter().get(i) - center.get(i))));
    }
    velocity[center.length()] = Math
        .max(Math.min(velocity[center.length()] + (learningRateLoc * randomCofLoc *
            (localBest.isEnabledValue() - isEnabled)) + (learningRateGlo * randomCofGlo * (
            localBest.isEnabledValue() - isEnabled)), 1), 0);

    //update
    updateByVelocity(bounds);
  }

  private void updateByVelocity(List<Bound> bounds) {
    double[] newVec = new double[center.length()];
    for (int i = 0; i < center.length(); i++) {
      newVec[i] = bounds.get(i).getValueInBounds(center.get(i) + velocity[i]);
    }
    this.center = new DenseVector(newVec);
    isEnabled = Math.max(Math.min(isEnabled + velocity[center.length()], 1), 0);
  }

  public void movePointsToDifferentCluster(List<Centroid> enabledCentroids) {
    if (!pointsInCluster.isEmpty()) {

      //recompute distance first
      pointsInCluster.forEach(tuple -> tuple.distance = getDistance(tuple.t));

      //this centroid is disabled, move everything to new clusters
      if (!isEnabled()) {
        pointsInCluster.forEach(next -> enabledCentroids.stream()
            .map(centroid -> new DistanceTuple<>(centroid, centroid.getDistance(next.t)))
            .min(Comparator.comparingDouble(o -> o.distance))
            .map(tuple -> tuple.t)
            .get().addToCluster(next.t));
        pointsInCluster.clear();
      } else {

        //cluster to take into consideration
        double maxDistance = getMaximalDistanceToCenter().getAsDouble() * 2.0;
        List<Centroid> toCheck = enabledCentroids.stream()
            .filter(centroid -> centroid.getDistance(center) < maxDistance)
            .collect(Collectors.toList());

        Iterator<DistanceTuple<DataPointWithDistanceCache>> i = pointsInCluster.iterator();
        while (i.hasNext()) {
          DistanceTuple<DataPointWithDistanceCache> next = i.next();
          Optional<Centroid> newCentroid = toCheck.stream()
              .map(centroid -> new DistanceTuple<>(centroid, centroid.getDistance(next.t)))
              .filter(tuple -> tuple.distance < next.distance)
              .min(Comparator.comparingDouble(o -> o.distance))
              .map(tuple -> tuple.t);
          if (newCentroid.isPresent()) {
            newCentroid.get().addToCluster(next.t);
            i.remove();
          }
        }
      }
    }
  }

  public void addToCluster(DataPointWithDistanceCache point) {
    pointsInClusterToMove.add(new DistanceTuple<>(point, getDistance(point)));
  }

  @Override
  public double isEnabledValue() {
    return isEnabled;
  }

  @AllArgsConstructor
  private static class DistanceTuple<T> {

    private final T t;
    private double distance;
  }

  @AllArgsConstructor
  private static class Tuple {

    private final int index;
    private double distance;
  }

}
