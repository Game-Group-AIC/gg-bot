package aic.gas.sc.gg_bot.abstract_bot.model.features;

import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.oo.state.exceptions.UnknownClassException;
import burlap.mdp.core.oo.state.exceptions.UnknownObjectException;
import burlap.mdp.core.state.UnknownKeyException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(of = {"featureVector"})
@Getter
public class OurState implements OOState, ObjectInstance, Serializable {

  private final double[] featureVector;
  private final List<Object> keys;
  public final static String NAME = "OUR_STATE";

  public OurState(double[] featureVector) {
    this.featureVector = featureVector;
    this.keys = IntStream.range(0, this.featureVector.length)
        .boxed()
        .collect(Collectors.toList());
  }

  @Override
  public ObjectInstance copyWithName(String objectName) {
    if (!objectName.equals(NAME)) {
      throw new RuntimeException("OurState must be " + NAME);
    } else {
      return this.copy();
    }
  }

  @Override
  public List<Object> variableKeys() {
    return keys;
  }

  @Override
  public Object get(Object o) {
    try {
      int i = (int) o;
      return getValue(i);
    } catch (Exception e) {
      throw new UnknownKeyException(o);
    }
  }

  public double getValue(int index) {
    return featureVector[index];
  }

  public int size() {
    return featureVector.length;
  }

  @Override
  public OurState copy() {
    return new OurState(featureVector.clone());
  }

  @Override
  public int numObjects() {
    return 1;
  }

  @Override
  public ObjectInstance object(String oname) {
    if (NAME.equals(oname)) {
      return this;
    } else {
      throw new UnknownObjectException(oname);
    }
  }

  @Override
  public List<ObjectInstance> objects() {
    return Collections.singletonList(this);
  }

  @Override
  public List<ObjectInstance> objectsOfClass(String oclass) {
    if (oclass.equals(NAME)) {
      return Collections.singletonList(this);
    } else {
      throw new UnknownClassException(oclass);
    }
  }

  @Override
  public String className() {
    return NAME;
  }

  @Override
  public String name() {
    return NAME;
  }
}
