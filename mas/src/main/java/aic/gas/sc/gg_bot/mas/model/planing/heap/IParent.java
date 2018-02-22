package aic.gas.sc.gg_bot.mas.model.planing.heap;

import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import java.util.List;
import java.util.Optional;

/**
 * Contract for parent - it has at least one children
 */
public interface IParent<V extends Node<?> & IDesireNode, K extends Node<?> & IIntentionNode & IVisitorAcceptor> {

  /**
   * Get nodes for desires
   */
  List<V> getNodesWithDesire();

  /**
   * Get nodes for intentions
   */
  List<K> getNodesWithIntention();

  /**
   * Return desire key
   */
  Optional<DesireKey> getDesireKeyAssociatedWithParent();

}
