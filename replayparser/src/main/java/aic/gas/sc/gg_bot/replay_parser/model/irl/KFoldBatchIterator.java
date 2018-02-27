package aic.gas.sc.gg_bot.replay_parser.model.irl;

import burlap.behavior.singleagent.Episode;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KFoldBatchIterator {

  private int foldPointer;

  private final List<List<Episode>> episodes;

  public KFoldBatchIterator(int folds, List<Episode> episodes) {
    this.episodes = Lists.partition(episodes, episodes.size() / folds);
    this.foldPointer = 0;
  }

  public Stream<Episode> getAll() {
    return episodes.stream()
        .flatMap(Collection::stream);
  }

  public Stream<Episode> trainingData() {
    return IntStream.range(0, episodes.size())
        .boxed()
        .filter(integer -> integer != foldPointer)
        .flatMap(integer -> episodes.get(integer).stream());
  }

  public Stream<Episode> testingData() {
    return episodes.get(foldPointer).stream();
  }

  public void next() {
    foldPointer = (foldPointer + 1) % episodes.size();
  }

}
