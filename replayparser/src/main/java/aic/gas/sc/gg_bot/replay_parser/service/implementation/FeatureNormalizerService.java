package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.State;
import aic.gas.sc.gg_bot.replay_parser.service.IFeatureNormalizerService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FeatureNormalizerService implements IFeatureNormalizerService {

  @Override
  public List<FeatureNormalizer> computeFeatureNormalizersBasedOnStates(List<State> states,
      int cardinality) {
    return IntStream.range(0, cardinality).boxed()
        .map(integer -> states.stream()
            .map(State::getFeatureVector)
            .mapToDouble(doubles -> doubles[integer])
            .boxed())
        .map(doubleStream -> doubleStream.mapToDouble(Double::doubleValue))
        .map(doubleStream -> doubleStream.boxed().collect(Collectors.toList()))
        .map(doubles -> {
          double mean = computeAverage(doubles);
          double std = computeStandardDeviation(doubles, mean);
          return new FeatureNormalizer(mean, std);
        })
        .collect(Collectors.toList());
  }

  private double computeStandardDeviation(List<Double> doubles, double mean) {
    return Math.sqrt(doubles.stream()
        .map(aDouble -> aDouble - mean)
        .mapToDouble(aDouble -> aDouble * aDouble)
        .sum() / doubles.size());
  }

  private double computeAverage(List<Double> doubles) {
    return doubles.stream()
        .mapToDouble(Double::doubleValue)
        .average().orElse(0);
  }


  private Stream<Double> removeOutliers(Stream<Double> doubleStream, int count) {
    return doubleStream.sorted()
        //skip first tenth
        .skip(count / 10)
        //exclude last tenth
        .limit(count - count / 5);
  }

}
