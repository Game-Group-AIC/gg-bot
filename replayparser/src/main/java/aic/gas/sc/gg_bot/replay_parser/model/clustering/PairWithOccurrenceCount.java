package aic.gas.sc.gg_bot.replay_parser.model.clustering;

import jsat.classifiers.DataPoint;
import lombok.Getter;

@Getter
public class PairWithOccurrenceCount extends Pair {
  private final int occurrence;

  public PairWithOccurrenceCount(DataPoint first, DataPoint second, int occurrence) {
    super(first, second);
    this.occurrence = occurrence;
  }
}
