package aic.gas.sc.gg_bot.mas.model;

/**
 * Contract to convert object to feature value
 */
public interface IFeatureRawValueObtainingStrategy<V> {

  double returnRawValue(V v);
}
