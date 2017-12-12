package aic.gas.sc.gg_bot.replay_parser.main.example;

import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.StateUtilities;
import burlap.mdp.core.state.UnknownKeyException;
import burlap.mdp.core.state.annotations.DeepCopyState;
import java.util.Arrays;
import java.util.List;

/**
 */
@DeepCopyState
public class EXGridState implements MutableState {

  private final static List<Object> keys = Arrays.<Object>asList(ExampleGridWorld.VAR_X,
      ExampleGridWorld.VAR_Y);
  public int x;
  public int y;

  public EXGridState() {
  }

  public EXGridState(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getIndex() {
    return (11 * y) + x;
  }

  @Override
  public MutableState set(Object variableKey, Object value) {
    if (variableKey.equals(ExampleGridWorld.VAR_X)) {
      this.x = StateUtilities.stringOrNumber(value).intValue();
    } else if (variableKey.equals(ExampleGridWorld.VAR_Y)) {
      this.y = StateUtilities.stringOrNumber(value).intValue();
    } else {
      throw new UnknownKeyException(variableKey);
    }
    return this;
  }

  public List<Object> variableKeys() {
    return keys;
  }

  @Override
  public Object get(Object variableKey) {
    if (variableKey.equals(ExampleGridWorld.VAR_X)) {
      return x;
    } else if (variableKey.equals(ExampleGridWorld.VAR_Y)) {
      return y;
    }
    throw new UnknownKeyException(variableKey);
  }

  @Override
  public EXGridState copy() {
    return new EXGridState(x, y);
  }

  @Override
  public String toString() {
    return StateUtilities.stateToString(this);
  }
}
