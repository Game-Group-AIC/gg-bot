package aic.gas.sc.gg_bot.abstract_bot.utils;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import java.util.List;
import java.util.Random;
import jsat.linear.distancemetrics.DistanceMetric;
import jsat.linear.distancemetrics.EuclideanDistance;

public class VectorNormalizer {

  public static final DistanceMetric DISTANCE_FUNCTION = new EuclideanDistance();
  private static final Random RANDOM = new Random();

  /**
   * Standardize each part of feature vector according to normalizers
   */
  public static double[] normalizeFeatureVector(double[] featureVector,
      List<FeatureNormalizer> normalizers) {
    double[] normalizeFeatureVector = new double[featureVector.length];
    for (int i = 0; i < featureVector.length; i++) {
      Double normalizedValue = normalizers.get(i).standardize(featureVector[i]);
      normalizeFeatureVector[i] = normalizedValue;
    }
    return normalizeFeatureVector;
  }

}
