package aic.gas.sc.gg_bot.replay_parser.model.irl;

import aic.gas.sc.gg_bot.abstract_bot.model.decision.NextActionEnumerations;
import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.StateUtilities;
import burlap.mdp.core.state.UnknownKeyException;
import burlap.mdp.core.state.annotations.DeepCopyState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jsat.linear.Vec;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Decision state
 */
@EqualsAndHashCode(of = {"stateRepresentationByFeatures", "state"})
@DeepCopyState
public class DecisionState implements MutableState {

  private final static List<Object> keys = Collections
      .singletonList(DecisionDomainGenerator.VAR_STATE);

  @Getter
  private final Vec stateRepresentationByFeatures;

  private final Map<NextActionEnumerations, List<DecisionState>> transitions;

  private final Map<NextActionEnumerations, List<StateTransitionProb>> transitionProbabilities;

  @Getter
  private int state;

  public DecisionState(Vec stateRepresentationByFeatures, int state) {
    this.stateRepresentationByFeatures = stateRepresentationByFeatures;
    this.state = state;
    this.transitions = new HashMap<>();
    this.transitionProbabilities = new HashMap<>();
  }

  /**
   * Dead end
   */
  DecisionState(int state) {
    this.stateRepresentationByFeatures = null;
    this.state = state;
    this.transitions = new HashMap<>();
    this.transitionProbabilities = new HashMap<>();
  }

  public void addTransition(NextActionEnumerations nextActionEnumerations,
      DecisionState decisionState) {
    List<DecisionState> transitedStates = transitions
        .computeIfAbsent(nextActionEnumerations, act -> new ArrayList<>());
    transitedStates.add(decisionState);
  }

  private DecisionState(Vec stateRepresentationByFeatures, int state,
      Map<NextActionEnumerations, List<DecisionState>> transitions,
      Map<NextActionEnumerations, List<StateTransitionProb>> transitionProbabilities) {
    this.stateRepresentationByFeatures = stateRepresentationByFeatures;
    this.state = state;
    this.transitions = transitions;
    this.transitionProbabilities = transitionProbabilities;
  }

  public Stream<StateTransitionProb> getStateTransitions(
      NextActionEnumerations nextActionEnumeration) {
    if (!transitionProbabilities.containsKey(nextActionEnumeration)) {
      return Stream.empty();
    }
    return transitionProbabilities.get(nextActionEnumeration).stream();
  }

  public void initTransitionProbabilities() {
    transitions.forEach((nextActionEnumerations, decisionStates) -> {
      Map<DecisionState, Long> map = decisionStates.stream()
          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
      List<StateTransitionProb> stateTransitionProbs = map.entrySet().stream()
          .map(entry -> new StateTransitionProb(entry.getKey(),
              ((double) entry.getValue()) / ((double) decisionStates.size())))
          .collect(Collectors.toList());
      transitionProbabilities.put(nextActionEnumerations, stateTransitionProbs);
    });
  }

  @Override
  public MutableState set(Object variableKey, Object value) {
    if (variableKey.equals(DecisionDomainGenerator.VAR_STATE)) {
      this.state = StateUtilities.stringOrNumber(value).intValue();
      return this;
    }
    throw new UnknownKeyException(variableKey);
  }

  @Override
  public List<Object> variableKeys() {
    return keys;
  }

  @Override
  public Object get(Object variableKey) {
    if (variableKey.equals(DecisionDomainGenerator.VAR_STATE)) {
      return state;
    }
    throw new UnknownKeyException(variableKey);
  }

  @Override
  public DecisionState copy() {
    return new DecisionState(stateRepresentationByFeatures, state, transitions,
        transitionProbabilities);
  }

  @Override
  public String toString() {
    return StateUtilities.stateToString(this);
  }
}
