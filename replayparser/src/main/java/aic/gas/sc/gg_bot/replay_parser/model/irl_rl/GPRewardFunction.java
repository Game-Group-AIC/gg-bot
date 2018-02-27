package aic.gas.sc.gg_bot.replay_parser.model.irl_rl;

import burlap.domain.singleagent.lunarlander.state.LLAgent;
import burlap.domain.singleagent.lunarlander.state.LLState;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;
import io.jenetics.prog.ProgramGene;

public class GPRewardFunction implements RewardFunction {

  private final ProgramGene<Double> program;

  public GPRewardFunction(ProgramGene<Double> program) {
    this.program = program;
  }

  //TODO change
  @Override
  public double reward(State state, Action action, State sprime) {
    if (sprime instanceof LLState) {
      LLAgent agentBefore = ((LLState) state).agent;
      LLAgent agentNow = ((LLState) sprime).agent;
      double value = program.eval(dist(agentNow.x, agentBefore.x), dist(agentNow.y, agentBefore.y),
          dist(agentNow.vx, agentBefore.vx), dist(agentNow.vy, agentBefore.vy),
          dist(agentNow.angle, agentBefore.angle));
      return value;
    }
    throw new IllegalArgumentException("The state is not instance of our state type.");
  }

  private static double dist(double a, double b) {
    return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
  }

}
