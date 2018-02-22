package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactValueSets;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactValueSetsForAgentType;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.Trajectory;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcher;
import aic.gas.sc.gg_bot.replay_parser.service.IStorageService;
import aic.gas.sc.gg_bot.replay_parser.service.IWatcherMediatorService;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of IWatcherMediatorService
 */
public class WatcherMediatorService implements IWatcherMediatorService {

  private static IWatcherMediatorService instance = null;
  private final Set<AgentWatcher<?>> watchers = new HashSet<>(), allWatchers = new HashSet<>();
  private final IStorageService storageService = StorageService.getInstance();

  private WatcherMediatorService() {
    //singleton
  }

  public static IWatcherMediatorService getInstance() {
    if (instance == null) {
      instance = new WatcherMediatorService();
    }
    return instance;
  }

  @Override
  public void addWatcher(AgentWatcher watcher) {
    watchers.add(watcher);
    allWatchers.add(watcher);
  }

  @Override
  public void removeWatcher(AgentWatcher watcher) {
    watchers.remove(watcher);
  }

  @Override
  public Stream<AgentWatcher<?>> getStreamOfWatchers() {
    return watchers.stream();
  }

  @Override
  public void clearAllAgentsAndSaveTheirTrajectories() {
    //save trajectories
    Map<AgentTypes, Map<DesireKeys, List<Trajectory>>> toPersist = new HashMap<>();

//    Map<AgentType, List<AgentWatcher<?>>> collect =
    allWatchers.stream()
        //collect watchers by type
        .collect(Collectors.groupingBy(
            AgentWatcher::getAgentWatcherType,
            Collectors.mapping(Function.identity(), Collectors.toList())
        ))
        //for each of them get map of trajectories with desires
        .forEach((agentWatcherType, agentWatchers) -> agentWatchers.stream()
            .map(agentWatcher -> (AgentWatcher<?>) agentWatcher)
            .flatMap(AgentWatcher::getTrajectories)
            .forEach(desireKeyListEntry -> toPersist
                .computeIfAbsent(agentWatcherType.getAgentType(), agentType -> new HashMap<>())
                .computeIfAbsent(desireKeyListEntry.getKey(), desireKey -> new ArrayList<>())
                .addAll(desireKeyListEntry.getValue())
            )
        );
    //save merged entries
    toPersist.forEach((agentType, desireKeys) ->
        desireKeys.forEach((desireKey, trajectories) ->
            storageService.saveTrajectory(agentType, desireKey, trajectories)));

    //remove agents from register
    watchers.clear();
    allWatchers.clear();
  }

  @Override
  public void tellAgentsToObserveSystemAndHandlePlans() {

    //handle trajectories first to keep causality for actions
    watchers.forEach(agentWatcher -> agentWatcher.handleTrajectoriesOfPlans(this));
    watchers.forEach(agentWatcher -> agentWatcher.reason(this));
  }


  @Override
  public <V> double getFeatureValueOfFactSet(FactValueSets<V> convertingStrategy) {
    Stream<Optional<Stream<V>>> stream = watchers.stream()
        .map(AgentWatcher::getBeliefs)
        .filter(beliefs -> beliefs.isFactKeyForSetInMemory(convertingStrategy.getFactKey()))
        .map(beliefs -> beliefs.returnFactSetValueForGivenKey(convertingStrategy.getFactKey()));
    return convertingStrategy.getStrategyToObtainValue().returnRawValue(stream);
  }


  @Override
  public <V> double getFeatureValueOfFactSet(
      FactValueSetsForAgentType<V> convertingStrategy) {
    Stream<Optional<Stream<V>>> stream = watchers.stream()
        .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getID() == convertingStrategy
            .getAgentType().getID())
        .map(AgentWatcher::getBeliefs)
        .map(beliefs -> beliefs.returnFactSetValueForGivenKey(convertingStrategy.getFactKey()));
    return convertingStrategy.getStrategyToObtainValue().returnRawValue(stream);
  }

}
