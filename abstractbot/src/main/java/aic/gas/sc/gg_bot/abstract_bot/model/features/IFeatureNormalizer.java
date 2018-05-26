package aic.gas.sc.gg_bot.abstract_bot.model.features;

import java.io.Serializable;

public interface IFeatureNormalizer extends Serializable {

  double normalize(double value);

  double range();

}
