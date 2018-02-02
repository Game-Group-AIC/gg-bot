package aic.gas.sc.gg_bot.replay_parser.model.tracking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class TrajectoryWrapper {

  private final Trajectory trajectory;
  private boolean usedToLearnPolicy;
}
