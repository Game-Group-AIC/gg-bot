package aic.gas.sc.gg_bot.mas.model.planing.heap;

import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Generic node manipulator
 */
@Slf4j
class ChildNodeManipulation<V extends Node<?> & IVisitorAcceptor & IIntentionNode, K extends Node<?> & IDesireNode<?>> {

  final Map<DesireParameters, K> desiresNodesByKey = new HashMap<>();
  final Map<DesireParameters, V> intentionNodesByKey = new HashMap<>();

  /**
   * Get set of desires key for uncommitted nodes. Remove all uncommitted nodes
   */
  Set<DesireKey> removeDesiresForUncommittedNodesAndReturnTheirKeys() {
    Set<DesireKey> desireKeys = desiresNodesByKey.keySet().stream()
        .map(DesireParameters::getDesireKey)
        .collect(Collectors.toSet());
    desiresNodesByKey.clear();
    return desireKeys;
  }

  /**
   * Add desire node
   */
  void addDesireNode(K desireNode) {
    desiresNodesByKey.put(desireNode.desireParameters, desireNode);
  }

  /**
   * Replace desire by intention
   */
  void replaceDesireByIntention(K desireNode, V intentionNode) {
    if (desiresNodesByKey.containsKey(desireNode.desireParameters)) {
      desiresNodesByKey.remove(desireNode.desireParameters);
      intentionNodesByKey.put(intentionNode.desireParameters, intentionNode);
    } else {
      log.error("Could not replace desire by intention, desire node is missing.");
      throw new RuntimeException("Could not replace desire by intention, desire node is missing.");
    }
  }

  /**
   * Replace desire by intention
   */
  void replaceIntentionByDesire(V intentionNode, K desireNode) {
    if (intentionNodesByKey.containsKey(intentionNode.desireParameters)) {
      intentionNodesByKey.remove(intentionNode.desireParameters);
      desiresNodesByKey.put(desireNode.desireParameters, desireNode);
    } else {
      log.error("Could not replace intention by desire, intention node is missing.");
      throw new RuntimeException(
          "Could not replace intention by desire, intention node is missing.");
    }
  }

}
