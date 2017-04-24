package cz.jan.maly.model.irl;

import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.StateUtilities;
import burlap.mdp.core.state.UnknownKeyException;
import burlap.mdp.core.state.annotations.DeepCopyState;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

import static cz.jan.maly.model.irl.DecisionDomain.VAR_STATE;

/**
 * Decision state
 * Created by Jan on 24-Apr-17.
 */
@DeepCopyState
public class DecisionState implements MutableState {

    @Getter
    private int state = 0;
    private final static List<Object> keys = Collections.singletonList(VAR_STATE);

    public DecisionState() {
    }

    public DecisionState(int state) {
        this.state = state;
    }


    @Override
    public MutableState set(Object variableKey, Object value) {
        if (variableKey.equals(VAR_STATE)) {
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
        if (variableKey.equals(VAR_STATE)) {
            return state;
        }
        throw new UnknownKeyException(variableKey);
    }

    @Override
    public DecisionState copy() {
        return new DecisionState(state);
    }

    @Override
    public String toString() {
        return StateUtilities.stateToString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DecisionState that = (DecisionState) o;

        return state == that.state;
    }

    @Override
    public int hashCode() {
        return state;
    }
}
