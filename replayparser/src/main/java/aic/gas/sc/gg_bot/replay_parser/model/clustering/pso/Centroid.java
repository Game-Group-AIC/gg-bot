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
import jsat.classifiers.DataPoint;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class Centroid implements ICentroid {

  private double isEnabled = 0.5;

  @Getter
  private Vec center;

  @Getter
  private final List<DistanceTuple<DataPoint>> pointsInCluster = new ArrayList<>();
  private final List<DistanceTuple<DataPoint>> pointsInClusterToMove = new ArrayList<>();
  private final double[] velocity;

  private Set<DataPoint> pointSet = new HashSet<>();

  public Centroid(Vec center, double[] velocity, List<Bound> bounds) {
    this.center = center;
    this.velocity = velocity;
    updateByVelocity(bounds);
  }

  public void nextIteration() {
    pointsInCluster.addAll(pointsInClusterToMove);
    pointsInClusterToMove.clear();
    pointSet = pointsInCluster.stream()
        .map(dataPointDistanceTuple -> dataPointDistanceTuple.t)
        .collect(Collectors.toSet());
  }

  public boolean hasPointsInCluster() {
    return !pointsInCluster.isEmpty();
  }

  public boolean hasPointInCluster(DataPoint dataPoint) {
    return pointSet.contains(dataPoint);
  }

  public double getDistance(Vec vec) {
    return VectorNormalizer.DISTANCE_FUNCTION.dist(center, vec);
  }

  public double getDistance(DataPoint point) {
    return VectorNormalizer.DISTANCE_FUNCTION.dist(center, point.getNumericalValues());
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
        double maxDistance = getMaximalDistance().getAsDouble() * 2.0;
        List<Centroid> toCheck = enabledCentroids.stream()
            .filter(centroid -> centroid.getDistance(center) < maxDistance)
            .collect(Collectors.toList());

        Iterator<DistanceTuple<DataPoint>> i = pointsInCluster.iterator();
        while (i.hasNext()) {
          DistanceTuple<DataPoint> next = i.next();
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

  public void addToCluster(DataPoint point) {
    pointsInClusterToMove.add(new DistanceTuple<>(point, getDistance(point)));
  }

  @Override
  public double isEnabledValue() {
    return isEnabled;
  }

  public OptionalDouble getAverageDistance() {
    return pointsInCluster.stream()
        .mapToDouble(value -> value.distance)
        .average();
  }

  public OptionalDouble getMaximalDistance() {
    return pointsInCluster.stream()
        .mapToDouble(value -> value.distance)
        .max();
  }

  @AllArgsConstructor
  private static class DistanceTuple<T> {

    private final T t;
    private double distance;
  }

}
