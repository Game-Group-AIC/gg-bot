package aic.gas.sc.gg_bot.abstract_bot.model.decision;

import burlap.behavior.functionapproximation.DifferentiableStateActionValue;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

@AllArgsConstructor
@Log4j
public class OurProbabilisticPolicy {

  private static final Random RANDOM = new Random();
  private final DifferentiableStateActionValue vfa;
  private final double exploration;

  //we assume we have in each state same actions
  private final List<Action> actions;

  private double evaluate(State s, Action a) {
    return vfa.evaluate(s, a);
  }

  private Map<Action, Double> getActionProbabilitiesInState(State s) {
    Map<Action, Double> actionsValuesInState;
    try {
      actionsValuesInState = actions.stream()
          .collect(Collectors.toMap(action -> action, action -> evaluate(s, action)));
    } catch (Exception e){
      actionsValuesInState = new HashMap<>();
      log.info(e.getMessage());
    }

    //uniform ppt
    double maxValue = actionsValuesInState.values().stream()
        .max(Double::compareTo)
        .get();
    int countWithSameValue = (int) actionsValuesInState.values().stream()
        .filter(aDouble -> aDouble == maxValue)
        .count();
    if (countWithSameValue == actionsValuesInState.size()) {
      int count = actionsValuesInState.size();
      return actionsValuesInState.entrySet().stream()
          .collect(Collectors.toMap(Entry::getKey, o -> (1.0 / count)));
    }

    int size = actionsValuesInState.size();

    //set ppt to 1-e to action with highest value, all others have e/|count of actions - 1|
    return actionsValuesInState.entrySet().stream()
        .map(entry -> new Pair<>(entry.getKey(),
            entry.getValue() != maxValue ? exploration / (size - 1)
                : (1.0 - exploration) / countWithSameValue))
        .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
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
    LinkedHashMap<Action, Double> map = new LinkedHashMap<>();
    getActionProbabilitiesInState(s).entrySet().stream()
        .sorted(Comparator.comparingDouble(actionDoubleEntry -> -actionDoubleEntry.getValue()))
        .forEach(entry -> map.put(entry.getKey(), entry.getValue()));
    double ppt = RANDOM.nextDouble(), sum = 0.0;
    int i = 0;
    for (Entry<Action, Double> entry : map.entrySet()) {
      i++;
      sum = sum + entry.getValue();
      if (ppt <= sum || i == map.size()) {
        return entry.getKey();
      }
    }
    throw new UnsupportedOperationException("Error");
  }

  @AllArgsConstructor
  @Getter
  private static class Pair<K, V> {

    private final K first;
    private final V second;
  }

}
