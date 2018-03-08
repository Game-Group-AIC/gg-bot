package aic.gas.sc.gg_bot.replay_parser.model.irl_rl;

import java.io.Serializable;

public interface IOurState extends Serializable {

  double[] getFeatureRepresentation();
}
