package aic.gas.sc.gg_bot.abstract_bot.model.features;

import java.io.Serializable;
import java.util.List;

public class FeatureNormalizer implements IFeatureNormalizer, Serializable {

  private final double max;
  private final double min;

  public FeatureNormalizer(List<Double> values) {
    this.max = values.stream().max(Double::compareTo).get();
    this.min = values.stream().min(Double::compareTo).get();
  }

  @Override
  public double normalize(double value) {
    return ((value > max ? max : value < min ? min : value) - min) / (max - min);
  }

  @Override
  public double range() {
    return 1;
  }
}
