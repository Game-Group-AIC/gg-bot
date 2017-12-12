package aic.gas.sc.gg_bot.abstract_bot.model.features;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.DoubleStream;
import lombok.Getter;

/**
 * Transform value to z-score based on computed parameters
 */
@Getter
public class FeatureNormalizer implements Serializable {

  private final double mean;
  private final double std;

  public FeatureNormalizer(Set<Double> possibleValues) {
    this.mean = computeMean(possibleValues.stream().mapToDouble(value -> value));
    this.std = computeStandardDeviation(possibleValues.stream().mapToDouble(value -> value),
        this.mean, possibleValues.size());
  }

  private double computeMean(DoubleStream doubles) {
    return doubles.average().orElseGet(null);
  }

  private double computeStandardDeviation(DoubleStream doubles, double mean, double count) {
    return Math.sqrt(doubles.map(d -> Math.pow(d - mean, 2)).sum() / count);
  }

  /**
   * Transform value to z-score
   */
  public double zScoreNormalization(double value) {
    return (value - mean) / std;
  }

}
