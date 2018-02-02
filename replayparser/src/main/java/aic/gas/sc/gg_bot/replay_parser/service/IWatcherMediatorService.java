package aic.gas.sc.gg_bot.replay_parser.service;

import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactWithOptionalValueSets;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactWithOptionalValueSetsForAgentType;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactWithSetOfOptionalValues;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactWithSetOfOptionalValuesForAgentType;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcher;
import java.util.stream.Stream;

/**
 * Contract for service to track watchers
 */
public interface IWatcherMediatorService {

  /**
   * Add new watcher (for example new unit was created)
   */
  void addWatcher(AgentWatcher<?> watcher);

  /**
   * Remove watcher (for example unit was killed)
   */
  void removeWatcher(AgentWatcher<?> watcher);

  /**
   * Get stream of watchers
   */
  Stream<AgentWatcher<?>> getStreamOfWatchers();

  /**
   * Remove all agents from register and save theirs trajectories
   */
  void clearAllAgentsAndSaveTheirTrajectories();


  /**
   * Method to tell all agents to observe environment and system (this is called each frame)
   */
  void tellAgentsToObserveSystemAndHandlePlans();

  /**
   * Convert fact to feature value
   */
  <V> double getFeatureValueOfFact(FactWithSetOfOptionalValues<V> convertingStrategy);

  /**
   * Convert fact set to feature value
   */
  <V> double getFeatureValueOfFactSet(FactWithOptionalValueSets<V> convertingStrategy);

  /**
   * Convert fact to feature value
   */
  <V> double getFeatureValueOfFact(FactWithSetOfOptionalValuesForAgentType<V> convertingStrategy);

  /**
   * Convert fact set to feature value
   */
  <V> double getFeatureValueOfFactSet(FactWithOptionalValueSetsForAgentType<V> convertingStrategy);

}
