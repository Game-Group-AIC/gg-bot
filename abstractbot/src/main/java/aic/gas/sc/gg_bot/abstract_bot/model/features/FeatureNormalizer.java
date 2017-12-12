package aic.gas.sc.gg_bot.abstract_bot.model.features;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * Transform value to z-score based on computed parameters
 */
@Getter
public class FeatureNormalizer implements Serializable {

  private final double max;
  private final double min;

  public FeatureNormalizer(List<Double> values) {
    int tenth = values.size() / 10;
    List<Double> valuesSorted = values.stream()
        .sorted().collect(Collectors.toList());
    valuesSorted = valuesSorted.subList(tenth, values.size() - tenth);
    this.max = valuesSorted.get(valuesSorted.size() - 1);
    this.min = valuesSorted.get(0);
  }

  /**
   * Transform value to z-score
   */
  public Double rescaling(double value) {
    double valueToScale;
    if (value > max) {
      valueToScale = max;
    } else {
      if (value < min) {
        valueToScale = min;
      } else {
        valueToScale = value;
      }
    }
    return (valueToScale - min) / (max - min);
  }

}
