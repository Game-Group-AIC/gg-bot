package aic.gas.sc.gg_bot.abstract_bot.model.features;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Transform value to z-score based on computed parameters
 */
@Getter
@AllArgsConstructor
public class FeatureNormalizer implements Serializable {

  private final double mean;
  private final double std;
  private final String header;

  /**
   * Transform value to z-score
   */
  public double standardize(double value) {
    return (value - mean) / std;
  }

  @Override
  public String toString() {
    return "FeatureNormalizer for " + header + " with " + "mean = " + mean + ", std = " + std;
  }
}
