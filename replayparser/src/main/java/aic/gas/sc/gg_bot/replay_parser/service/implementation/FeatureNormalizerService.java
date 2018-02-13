package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.State;
import aic.gas.sc.gg_bot.replay_parser.service.IFeatureNormalizerService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;

public class FeatureNormalizerService implements IFeatureNormalizerService {

  @Override
  public List<FeatureNormalizer> computeFeatureNormalizersBasedOnStates(List<State> states,
      String[] headers) {
    return IntStream.range(0, headers.length).boxed()
        .map(integer -> new Tuple<>(integer, states.stream()
            .map(State::getFeatureVector)
            .mapToDouble(doubles -> doubles[integer])
            .boxed()
            .collect(Collectors.toList())))
        .map(tuple -> {
          double mean = computeAverage(tuple.content);
          double std = computeStandardDeviation(tuple.content, mean);
          return new FeatureNormalizer(mean, std, headers[tuple.index]);
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

  @AllArgsConstructor
  private static class Tuple<V> {

    private final int index;
    private final V content;
  }

}
