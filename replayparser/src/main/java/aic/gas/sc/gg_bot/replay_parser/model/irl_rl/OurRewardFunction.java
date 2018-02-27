package aic.gas.sc.gg_bot.replay_parser.model.irl_rl;

import burlap.domain.singleagent.lunarlander.state.LLAgent;
import burlap.domain.singleagent.lunarlander.state.LLState;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;
import java.util.stream.IntStream;

public class OurRewardFunction implements RewardFunction {

  private final double[][] coefficients;

  public OurRewardFunction(double[][] coefficients) {
    this.coefficients = coefficients;
  }

  public OurRewardFunction(int dimension, int indeterminateOfPolynomial) {
    this.coefficients = new double[dimension][indeterminateOfPolynomial];
  }

  //TODO change
  @Override
  public double reward(State state, Action action, State sprime) {
    if (sprime instanceof LLState) {
      LLAgent agent = ((LLState) sprime).agent;
      LLAgent agentS = ((LLState) state).agent;
      double[] stateF = new double[]{agent.x, agent.y, agent.vx, agent.vy, agent.angle};
      double[] stateS = new double[]{agentS.x, agentS.y, agentS.vx, agentS.vy, agentS.angle};
      double value = IntStream.range(0, coefficients.length)
          .boxed()
          .flatMap(i -> IntStream.range(0, coefficients[i].length)
              .boxed()
              .mapToDouble(j -> coefficients[i][j] * Math.pow(dist(stateF[i], stateS[i]), j + 1))
              .boxed())
          .reduce(0.0, (a, b) -> a + b);
      return value;
    }
    throw new IllegalArgumentException("The state is not instance of our state type.");
  }

  public void updateCoefficients(double[][] newCoefficients) {
    System.arraycopy(newCoefficients, 0, coefficients, 0, newCoefficients.length);
  }

  private static double dist(double a, double b) {
    return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
  }
}
