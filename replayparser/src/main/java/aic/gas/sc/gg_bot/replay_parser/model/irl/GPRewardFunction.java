package aic.gas.sc.gg_bot.replay_parser.model.irl;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;
import io.jenetics.prog.ProgramGene;

public class GPRewardFunction implements RewardFunction {

  private final ProgramGene<Double> program;

  public GPRewardFunction(ProgramGene<Double> program) {
    this.program = program;
  }

  @Override
  public double reward(State state, Action action, State sprime) {
    if (sprime instanceof ObjectInstance && state instanceof ObjectInstance) {
      ObjectInstance agentBefore = (ObjectInstance) state;
      ObjectInstance agentNow = (ObjectInstance) sprime;
      return program.eval(differences(agentBefore, agentNow));
    }
    throw new IllegalArgumentException("The state is not instance of our state type.");
  }

  private static Double[] differences(ObjectInstance agentBefore, ObjectInstance agentNow) {
    return agentBefore.variableKeys().stream()
        .mapToDouble(k -> dist((double) agentBefore.get(k), (double) agentNow.get(k)))
        .boxed()
        .toArray(Double[]::new);
  }

  private static double dist(double a, double b) {
    return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
  }

}
