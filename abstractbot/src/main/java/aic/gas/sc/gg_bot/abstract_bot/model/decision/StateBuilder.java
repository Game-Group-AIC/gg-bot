package aic.gas.sc.gg_bot.abstract_bot.model.decision;

import aic.gas.sc.gg_bot.abstract_bot.model.features.IFeatureNormalizer;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StateBuilder implements Serializable {

  protected final IFeatureNormalizer[] normalizers;
  protected final List<Integer> keys;

  public StateBuilder(IFeatureNormalizer[] normalizers) {
    this.normalizers = normalizers;
    this.keys = IntStream.range(0, normalizers.length)
        .boxed()
        .collect(Collectors.toList());
  }


  //create state from features
  public OurState buildState(double[] featureVector) {
    return new OurState(IntStream.range(0, keys.size())
        .mapToDouble(i -> normalizers[i].normalize(featureVector[i]))
        .toArray());
  }

}
