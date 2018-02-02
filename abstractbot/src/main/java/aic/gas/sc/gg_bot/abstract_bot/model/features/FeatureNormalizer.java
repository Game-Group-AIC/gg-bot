package aic.gas.sc.gg_bot.abstract_bot.model.features;

import java.io.Serializable;
import lombok.Getter;

/**
 * Transform value to z-score based on computed parameters
 */
@Getter
public class FeatureNormalizer implements Serializable {

  private final double mean;
  private final double std;

  public FeatureNormalizer(double mean, double std) {
    this.mean = mean;
    this.std = std;
  }

  /**
   * Transform value to z-score
   */
  public double standardize(double value) {
    return (value - mean) / std;
  }

}
