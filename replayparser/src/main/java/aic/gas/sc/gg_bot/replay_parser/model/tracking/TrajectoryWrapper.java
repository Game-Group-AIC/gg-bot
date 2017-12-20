package aic.gas.sc.gg_bot.replay_parser.model.tracking;

import lombok.Getter;
import lombok.Setter;

public class TrajectoryWrapper {
  @Getter
  private final Trajectory trajectory;

  @Getter
  @Setter
  private boolean usedToLearnPolicy = false;

  public TrajectoryWrapper(Trajectory trajectory) {
    this.trajectory = trajectory;
  }
}
