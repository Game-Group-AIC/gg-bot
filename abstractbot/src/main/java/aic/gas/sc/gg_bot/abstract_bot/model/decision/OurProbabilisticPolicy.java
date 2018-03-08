package aic.gas.sc.gg_bot.abstract_bot.model.decision;

import burlap.behavior.functionapproximation.DifferentiableStateActionValue;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OurProbabilisticPolicy {

  private final DifferentiableStateActionValue vfa;

  //we assume we have in each state same actions
  private final List<Action> actions;

  private double evaluate(State s, Action a) {
    return vfa.evaluate(s, a);
  }

  private Map<Action, Double> getActionProbabilitiesInState(State s) {
    Map<Action, Double> actionsValuesInState = actions.stream()
        .collect(Collectors.toMap(Function.identity(), action -> evaluate(s, action)));
    double toAdd = Math.min(actionsValuesInState.values().stream()
        .mapToDouble(aDouble -> aDouble).min().getAsDouble(), 0.0);
    double sum = actionsValuesInState.values().stream()
        .mapToDouble(aDouble -> aDouble + toAdd)
        .sum();
    return actionsValuesInState.entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey, o -> (o.getValue() + toAdd) / sum));
  }

  /**
   * Compute probability
   */
  public double getProbabilityOfActionInState(State s, Action action) {
    return getActionProbabilitiesInState(s).get(action);
  }

  /**
   * Sample action based on its value
   */
  public Action selectActionInState(State s) {
    double randomThreshold = Math.random(), sumOfProbability = 0;
    for (Entry<Action, Double> entry : getActionProbabilitiesInState(s).entrySet()) {
      sumOfProbability = sumOfProbability + entry.getValue();
      if (randomThreshold <= sumOfProbability) {
        return entry.getKey();
      }
    }
    throw new IllegalStateException("No action selected.");
  }

}
