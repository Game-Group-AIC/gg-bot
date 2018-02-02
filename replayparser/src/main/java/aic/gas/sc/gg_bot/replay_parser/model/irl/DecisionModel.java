package aic.gas.sc.gg_bot.replay_parser.model.irl;

import aic.gas.sc.gg_bot.abstract_bot.model.decision.NextActionEnumerations;
import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.statemodel.FullStateModel;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of model - how transitions are made
 */
public class DecisionModel implements FullStateModel {

  /**
   * Very nasty hack as we do not know what will happen when we decide to take action which nobody
   * took - agent is send to special state with transitions to itself. As no player is tacking this
   * action it should be fine to use it
   */
  private final DecisionState deadEnd;

  public DecisionModel(int numberOfStates) {
    this.deadEnd = new DecisionState(numberOfStates);
  }

  /**
   * Return all possible state transitions from this state based on action taken
   */
  private List<StateTransitionProb> stateTransitions(DecisionState from, NextActionEnumerations a) {
    return from.getStateTransitions(a).collect(Collectors.toList());
  }

  @Override
  public List<StateTransitionProb> stateTransitions(State state, Action action) {
    return stateTransitions((DecisionState) state,
        NextActionEnumerations.returnNextAction(action.actionName()));
  }

  @Override
  public DecisionState sample(State state, Action action) {
    List<StateTransitionProb> reachableStates = stateTransitions(state, action);

    //dead end with highest negative reward. To prevent agent going there
    if (reachableStates.isEmpty()) {

      //copy state
      return deadEnd.copy();
    }

    Collections.shuffle(reachableStates);

    //sample random roll
    double randomThreshold = Math.random(), sumOfProbability = 0;
    for (StateTransitionProb reachableState : reachableStates) {
      sumOfProbability = sumOfProbability + reachableState.p;
      if (randomThreshold <= sumOfProbability) {

        //copy state
        return ((DecisionState) reachableState.s).copy();
      }
    }
    throw new IndexOutOfBoundsException("No state found!");
  }
}
