package aic.gas.sc.gg_bot.replay_parser.model.tracking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Trajectory - to capture player's decision on commitment given state
 */
public class Trajectory implements Serializable {

  @Getter
  private final int numberOfFeatures;
  @Getter
  private List<State> states = new ArrayList<>();

  public Trajectory(int numberOfFeatures) {
    this.numberOfFeatures = numberOfFeatures;
  }

  /**
   * Add new state on trajectory
   */
  public void addNewState(State state) {
    states.add(state);
  }
}
