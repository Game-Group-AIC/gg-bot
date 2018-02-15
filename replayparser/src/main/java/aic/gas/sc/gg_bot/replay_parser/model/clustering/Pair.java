package aic.gas.sc.gg_bot.replay_parser.model.clustering;

import java.util.Objects;
import jsat.classifiers.DataPoint;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents pairs which should be preferably put to different cluster
 */
@Getter
@AllArgsConstructor
public class Pair {

  private final DataPoint first;
  private final DataPoint second;
  private final boolean isCommittedWhenTransiting;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Pair pair = (Pair) o;
    if (isCommittedWhenTransiting != ((Pair) o).isCommittedWhenTransiting) {
      return false;
    }
    return (Objects.equals(first, pair.first) && Objects.equals(second, pair.second))
        || (Objects.equals(first, pair.second) && Objects.equals(second, pair.first));
  }

  @Override
  public int hashCode() {
    return first.hashCode() * second.hashCode() * Objects.hashCode(isCommittedWhenTransiting);
  }
}
