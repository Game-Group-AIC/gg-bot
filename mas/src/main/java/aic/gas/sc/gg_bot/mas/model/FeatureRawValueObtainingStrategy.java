package aic.gas.sc.gg_bot.mas.model;

/**
 * Contract to convert object to feature value
 */
public interface FeatureRawValueObtainingStrategy<V> {

  double returnRawValue(V v);
}
