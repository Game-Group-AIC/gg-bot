package aic.gas.sc.gg_bot.replay_parser.model.clustering.pso;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Bound {
  private final double upperBound;
  private final double lowerBound;

  public double getValueInBounds(double value){
    return Math.max(Math.min(value, upperBound), lowerBound);
  }

}
