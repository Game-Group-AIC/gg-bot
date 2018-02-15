package aic.gas.sc.gg_bot.replay_parser.model.clustering;

import lombok.Getter;

@Getter
public class PairWithOccurrenceCount extends Pair {

  private final int occurrence;

  public PairWithOccurrenceCount(Pair pair, int occurrence) {
    super(pair.getFirst(), pair.getSecond(), pair.isCommittedWhenTransiting());
    this.occurrence = occurrence;
  }
}
