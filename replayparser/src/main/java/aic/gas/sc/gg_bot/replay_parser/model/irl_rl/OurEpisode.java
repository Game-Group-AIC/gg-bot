package aic.gas.sc.gg_bot.replay_parser.model.irl_rl;

import burlap.behavior.singleagent.Episode;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//TODO HACK
public class OurEpisode extends Episode implements Serializable {

  private final List<State> sStateSequence = new ArrayList<>();
  private final List<Action> sActionSequence = new ArrayList<>();
  private final List<Double> sRewardSequence = new ArrayList<>();

  public void addState(State s) {
    this.stateSequence.add(s);
    this.sStateSequence.add(s);
  }

  public void addAction(Action ga) {
    this.actionSequence.add(ga);
    this.sActionSequence.add(ga);
  }

  public void addReward(double r) {
    this.rewardSequence.add(r);
    this.sRewardSequence.add(r);
  }

}
