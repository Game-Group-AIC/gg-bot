package aic.gas.sc.gg_bot.replay_parser.model.clustering.pso;

import aic.gas.sc.gg_bot.abstract_bot.utils.VectorNormalizer;
import java.util.HashMap;
import java.util.Map;
import jsat.classifiers.DataPoint;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(of = "dataPoint")
public class DataPointWithDistanceCache {

  private final Map<DataPoint, Double> distances = new HashMap<>();

  @Getter
  private final DataPoint dataPoint;

  @Getter
  private final int count;

  public DataPointWithDistanceCache(DataPoint point, int count) {
    this.dataPoint = point;
    this.count = count;
  }

  public double getDistance(DataPointWithDistanceCache other) {
    Double distance = distances.get(dataPoint);
    if (distance == null) {
      distance = VectorNormalizer.DISTANCE_FUNCTION
          .dist(dataPoint.getNumericalValues(), other.dataPoint.getNumericalValues());
      distances.put(other.dataPoint, distance);
      other.distances.put(dataPoint, distance);
    }
    return distance;
  }


}
