package aic.gas.sc.gg_bot.replay_parser.model.irl;

import burlap.behavior.singleagent.Episode;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

//TODO this may be little bit strange
@Slf4j
@AllArgsConstructor
public class BatchIterator {

  @Getter
  private final int sizeOfBatch;
  private static final Random RANDOM = new Random();

  private final List<Episode> episodes;

  public List<Episode> sampleBatchFromEpisodes() {
    if (episodes.size() < sizeOfBatch) {
      return episodes;
    }
    Set<Integer> indexesForBatch = generateRandomIndexes(episodes.size());
    return IntStream.range(0, episodes.size())
        .filter(indexesForBatch::contains)
        .boxed()
        .map(episodes::get)
        .collect(Collectors.toList());
  }

  private Set<Integer> generateRandomIndexes(int range) {
    if (range <= sizeOfBatch) {
      return IntStream.range(0, range).boxed().collect(Collectors.toSet());
    }
    Set<Integer> indexes = new HashSet<>();
    while (indexes.size() < sizeOfBatch) {
      indexes.add(RANDOM.nextInt(range));
    }
    return indexes;
  }

}
