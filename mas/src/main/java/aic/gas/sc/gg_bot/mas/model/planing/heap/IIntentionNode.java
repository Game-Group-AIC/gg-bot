package aic.gas.sc.gg_bot.mas.model.planing.heap;

import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesireForAgents;
import java.util.List;
import java.util.Set;

/**
 * Contract for each node representing intention
 */
public interface IIntentionNode {

  /**
   * Adds shared desires to given set if node contains shared desire. This method is introduce to
   * collect desires which will be removed from register as agent is no longer committed to subtree
   */
  void collectSharedDesiresForOtherAgentsInSubtree(
      Set<SharedDesireForAgents> sharedDesiresInSubtree);

  /**
   * Remove commitment to this intention and replace itself by desire
   */
  boolean removeCommitment(List<DesireKey> madeCommitmentToTypes,
      List<DesireKey> didNotMakeCommitmentToTypes,
      List<DesireKey> typesAboutToMakeDecision);

  /**
   * Tell node to act on removal - tell contained intention to update knowledge
   */
  void actOnRemoval();

  /**
   * Add own desire key to list + when intermediate node - ask childes
   */
  void collectKeysOfCommittedDesiresInSubtree(List<DesireKey> list);

  /**
   * Add desire key to list - ask childes (if they are desires)
   */
  void collectKeysOfDesiresInSubtree(List<DesireKey> list);

}
