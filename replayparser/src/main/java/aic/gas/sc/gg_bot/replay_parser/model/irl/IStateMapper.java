package aic.gas.sc.gg_bot.replay_parser.model.irl;

import aic.gas.sc.gg_bot.abstract_bot.model.decision.OurState;
import burlap.mdp.core.state.State;

public interface IStateMapper {

  OurState map(State state);

}
