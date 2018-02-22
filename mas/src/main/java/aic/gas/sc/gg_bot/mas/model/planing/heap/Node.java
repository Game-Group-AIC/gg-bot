package aic.gas.sc.gg_bot.mas.model.planing.heap;

import aic.gas.sc.gg_bot.mas.model.IDesireKeyIdentification;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;

/**
 * Template for node. It defines common data structure (methods) for various nodes which extend it.
 */
public abstract class Node<K extends IParent<?, ?>> implements IDesireKeyIdentification {

  final DesireParameters desireParameters;
  final int level;
  final K parent;
  final HeapOfTrees heapOfTrees;

  //to only extend classes defined in this scope
  private Node(DesireParameters desireParameters, int level, K parent, HeapOfTrees heapOfTrees) {
    this.desireParameters = desireParameters;
    this.level = level;
    this.parent = parent;
    this.heapOfTrees = heapOfTrees;
  }

  @Override
  public DesireKey getDesireKey() {
    return desireParameters.getDesireKey();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Node)) {
      return false;
    }

    Node node = (Node) o;

    return level == node.level && desireParameters.equals(node.desireParameters);
  }

  @Override
  public int hashCode() {
    int result = desireParameters.hashCode();
    result = 31 * result + level;
    return result;
  }

  /**
   * Template for nodes in top level
   */
  static abstract class TopLevel extends Node<HeapOfTrees> {

    TopLevel(HeapOfTrees parent, DesireParameters desireParameters) {
      super(desireParameters, 0, parent, parent);
    }
  }

  /**
   * Template for nodes not in top level
   */
  static abstract class NotTopLevel<K extends Node & IIntentionNodeWithChildes & IParent<?, ?>> extends
      Node<K> {

    NotTopLevel(K parent, DesireParameters desireParameters) {
      super(desireParameters, parent.level + 1, parent, parent.heapOfTrees);
    }
  }

}
