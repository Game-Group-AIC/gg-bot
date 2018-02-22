package aic.gas.sc.gg_bot.replay_parser.model.irl_rl;

import burlap.mdp.core.state.MutableState;
import jsat.linear.Vec;

public interface IOurState extends MutableState {

  Vec getFeatureRepresentation();
}
