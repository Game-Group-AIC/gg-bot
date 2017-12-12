package aic.gas.sc.gg_bot.replay_parser.model.irl;

import aic.gas.sc.gg_bot.abstract_bot.model.decision.NextActionEnumerations;
import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.statemodel.FullStateModel;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of model - how transitions are made
 */
public class DecisionModel implements FullStateModel {

  private final Map<DecisionState, Map<NextActionEnumerations, Map<DecisionState, Double>>> transitionsProbabilitiesBasedOnActions = new HashMap<>();
  private DecisionState deadEnd;

  /**
   * Very nasty hack as we do not know what will happen when we decide to take action which nobody
   * took - agent is send to special state with transitions to itself. As no player is tacking this
   * action it should be fine to use it
   */
  public void addExtraTransitionToStateWhenNoActionExists() {
    deadEnd = new DecisionState(transitionsProbabilitiesBasedOnActions.size());
    Map<NextActionEnumerations, Map<DecisionState, Double>> transitionMap = new HashMap<>();

    //dummy map
    Map<DecisionState, Double> dummyMap = new HashMap<>();
    dummyMap.put(deadEnd, 1.0);

    for (NextActionEnumerations action : NextActionEnumerations.values()) {
      transitionMap.put(action, dummyMap);
    }
    transitionsProbabilitiesBasedOnActions.put(deadEnd, transitionMap);

    //now add missing transitions
    transitionsProbabilitiesBasedOnActions.values()
        .forEach(nextActionEnumerationsMapMap -> Arrays.stream(NextActionEnumerations.values())
            .filter(actionEnumerations -> !nextActionEnumerationsMapMap.keySet()
                .contains(actionEnumerations))
            .forEach(actionEnumerations -> nextActionEnumerationsMapMap
                .put(actionEnumerations, dummyMap)));
  }


  /**
   * Add transition based on action and its probability
   */
  public void addTransition(DecisionState from, NextActionEnumerations action,
      DecisionState transitingToState, double probability) {
    transitionsProbabilitiesBasedOnActions.computeIfAbsent(from, decisionState -> new HashMap<>())
        .computeIfAbsent(action, actionEnumerations -> new HashMap<>())
        .put(transitingToState, probability);
  }

  /**
   * Return all possible state transitions from this state based on action taken
   */
  public List<StateTransitionProb> stateTransitions(DecisionState from, NextActionEnumerations a) {
    return transitionsProbabilitiesBasedOnActions.get(from).get(a).entrySet().stream()
        .map(entry -> new StateTransitionProb(entry.getKey().copy(), entry.getValue()))
        .collect(Collectors.toList());
  }

  @Override
  public List<StateTransitionProb> stateTransitions(State state, Action action) {
    return stateTransitions((DecisionState) state,
        NextActionEnumerations.returnNextAction(action.actionName()));
  }

  @Override
  public DecisionState sample(State state, Action action) {
    List<StateTransitionProb> reachableStates;
    try {
      reachableStates = stateTransitions(state, action);
    } catch (NullPointerException e) {
      reachableStates = Collections.singletonList(new StateTransitionProb(deadEnd, 1.0));
    }
    Collections.shuffle(reachableStates);

    //sample random roll
    double randomThreshold = Math.random(), sumOfProbability = 0;
    for (StateTransitionProb reachableState : reachableStates) {
      sumOfProbability = sumOfProbability + reachableState.p;
      if (randomThreshold <= sumOfProbability) {
        return ((DecisionState) reachableState.s).copy();
      }
    }
    throw new IndexOutOfBoundsException("No state found!");
  }
}
