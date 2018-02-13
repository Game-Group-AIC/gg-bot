package aic.gas.sc.gg_bot.abstract_bot.utils;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import java.util.List;
import jsat.linear.distancemetrics.DistanceMetric;
import jsat.linear.distancemetrics.EuclideanDistance;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VectorNormalizer {

  public static final DistanceMetric DISTANCE_FUNCTION = new EuclideanDistance();

  /**
   * Standardize each part of feature vector according to normalizers
   */
  public static double[] normalizeFeatureVector(double[] featureVector,
      List<FeatureNormalizer> normalizers) {
    double[] normalizeFeatureVector = new double[featureVector.length];
    for (int i = 0; i < featureVector.length; i++) {
      Double normalizedValue = normalizers.get(i).standardize(featureVector[i]);

      //TODO features with mean and std equals to zero should be disabled
      if (normalizedValue.isNaN()) {
        normalizeFeatureVector[i] = 0;
//        log.error("NaN value when normilizing vector for feature "
//            + normalizers.get(i).getHeader());
      } else {
        normalizeFeatureVector[i] = normalizedValue;
      }
    }
    return normalizeFeatureVector;
  }

}
