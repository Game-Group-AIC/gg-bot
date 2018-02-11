package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import aic.gas.sc.gg_bot.replay_parser.model.clustering.Pair;
import aic.gas.sc.gg_bot.replay_parser.model.clustering.PairWithOccurrenceCount;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.Trajectory;
import aic.gas.sc.gg_bot.replay_parser.service.IPairFindingService;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DifferentConsecutivePairFindingService implements IPairFindingService {

  @Override
  public Set<PairWithOccurrenceCount> findPairs(Stream<Trajectory> trajectories,
      List<FeatureNormalizer> normalizers) {
    return trajectories.parallel()
        .map(Trajectory::getStates)
        .filter(states -> states.size() >= 2)
        .flatMap(states -> IntStream.range(0, states.size() - 1)
            //only different states
            .filter(i -> !Arrays
                .equals(states.get(i).getFeatureVector(), states.get(i + 1).getFeatureVector()))
            .boxed()
            .map(i -> new Pair(createDataPoint(states.get(i), normalizers),
                createDataPoint(states.get(i + 1), normalizers))))
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet()
        .stream()
        .map(pairLongEntry -> new PairWithOccurrenceCount(pairLongEntry.getKey().getFirst(),
            pairLongEntry.getKey().getSecond(), pairLongEntry.getValue().intValue()))
        .collect(Collectors.toSet());
  }
}
