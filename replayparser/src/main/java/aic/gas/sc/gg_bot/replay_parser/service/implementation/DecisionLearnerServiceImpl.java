package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.MapSizeEnums;
import aic.gas.sc.gg_bot.abstract_bot.model.decision.DecisionPointDataStructure;
import aic.gas.sc.gg_bot.abstract_bot.model.decision.NextActionEnumerations;
import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import aic.gas.sc.gg_bot.abstract_bot.utils.Configuration;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import aic.gas.sc.gg_bot.replay_parser.model.irl.DecisionDomainGenerator;
import aic.gas.sc.gg_bot.replay_parser.model.irl.DecisionModel;
import aic.gas.sc.gg_bot.replay_parser.model.irl.DecisionState;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.State;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.Trajectory;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.TrajectoryWrapper;
import aic.gas.sc.gg_bot.replay_parser.service.DecisionLearnerService;
import aic.gas.sc.gg_bot.replay_parser.service.PolicyLearningService;
import aic.gas.sc.gg_bot.replay_parser.service.StorageService;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.Episode;
import burlap.mdp.core.action.SimpleAction;
import burlap.mdp.singleagent.SADomain;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of DecisionLearnerService
 */
@Slf4j
public class DecisionLearnerServiceImpl implements DecisionLearnerService {

  private static final List<MapSizeEnums> MAP_SIZES = Stream.of(MapSizeEnums.values())
      .collect(Collectors.toList());
  private static final List<ARace> RACES = Stream.of(ARace.values())
      .filter(race -> !race.equals(ARace.UNKNOWN))
      .collect(Collectors.toList());
  private final StorageService storageService = StorageServiceImp.getInstance();
  private final StateClusteringServiceImpl stateClusteringService = new StateClusteringServiceImpl();
  private final PolicyLearningService policyLearningService = new PolicyLearningServiceImpl();

//    public void test() {
//        List<DecisionState> states = Arrays.asList(new DecisionState(0), new DecisionState(1), new DecisionState(2));
//        DecisionModel decisionModel = new DecisionModel();
//
//        //transitions
//        decisionModel.addTransition(states.get(0), NextActionEnumerations.YES, states.get(1), 0.5);
//        decisionModel.addTransition(states.get(0), NextActionEnumerations.YES, states.get(2), 0.5);
//        decisionModel.addTransition(states.get(0), NextActionEnumerations.NO, states.get(1), 1.0);
//        decisionModel.addTransition(states.get(1), NextActionEnumerations.NO, states.get(2), 0.5);
//        decisionModel.addTransition(states.get(1), NextActionEnumerations.NO, states.get(0), 0.5);
//        decisionModel.addTransition(states.get(1), NextActionEnumerations.YES, states.get(2), 1.0);
//        decisionModel.addTransition(states.get(2), NextActionEnumerations.NO, states.get(2), 0.5);
//        decisionModel.addTransition(states.get(2), NextActionEnumerations.NO, states.get(0), 0.5);
//        decisionModel.addTransition(states.get(2), NextActionEnumerations.YES, states.get(1), 1.0);
//
//        Episode episode1 = new Episode();
//        episode1.addState(states.get(0));
//        episode1.transition(new SimpleAction(NextActionEnumerations.YES.name()), states.get(0), DecisionDomainGenerator.defaultReward);
//        episode1.transition(new SimpleAction(NextActionEnumerations.YES.name()), states.get(2), DecisionDomainGenerator.defaultReward);
//        episode1.transition(new SimpleAction(NextActionEnumerations.YES.name()), states.get(1), DecisionDomainGenerator.defaultReward);
//
//        Episode episode2 = new Episode();
//        episode2.addState(states.get(1));
//        episode2.transition(new SimpleAction(NextActionEnumerations.NO.name()), states.get(2), DecisionDomainGenerator.defaultReward);
//        episode2.transition(new SimpleAction(NextActionEnumerations.NO.name()), states.get(1), DecisionDomainGenerator.defaultReward);
//
//        List<Episode> episodes = new ArrayList<>();
//        for (int i = 0; i < 1000; i++) {
//            episodes.add(episode1);
//            episodes.add(episode2);
//        }
//
//        //learn policy
//        DecisionDomainGenerator decisionDomainGenerator = new DecisionDomainGenerator(decisionModel);
//        SADomain domain = decisionDomainGenerator.generateDomain();
//        Policy policy = policyLearningService.learnPolicy(domain, episodes, states.size(), episodes.size() / 2);
//        System.out.println();
//    }

  /**
   * Find nearest representative
   */
  private static DecisionState closestStateRepresentative(Vec featureVector,
      Map<DecisionState, Vec> statesAndTheirMeans) {
    Optional<DecisionState> closestState = statesAndTheirMeans.entrySet().stream()
        .min(Comparator.comparingDouble(
            o -> Configuration.DISTANCE_FUNCTION.dist(o.getValue(), featureVector)))
        .map(Map.Entry::getKey);
    if (!closestState.isPresent()) {
      log.error("No state is present.");
      throw new IllegalArgumentException("No state is present.");
    }
    return closestState.get();
  }

  @Override
  public void learnDecisionMakers(int parallelismLevel) {
    List<Tuple> toLearn = MAP_SIZES.stream().flatMap(mapSize -> RACES.stream().flatMap(
        race -> storageService.getParsedAgentTypesWithDesiresTypesContainedInStorage(mapSize, race)
            .entrySet().stream().flatMap(entry -> entry.getValue().stream()
                .map(desireKeyID -> new Tuple(mapSize, race, entry.getKey(), desireKeyID)))))
        .filter(tuple -> {
          String path = storageService
              .getLearntDecisionPath(tuple.agentTypeID, tuple.desireKeyID, tuple.mapSize,
                  tuple.race);
          return !new File(path).exists();
        })
        .collect(Collectors.toList());

    //execute
    List<MyTask> tasks = toLearn.parallelStream()
        .map(MyTask::new)
        .collect(Collectors.toList());
    log.info("Clustering finished...");

    ExecutorService executor = Executors.newFixedThreadPool(parallelismLevel);
    tasks.forEach(executor::execute);

    executor.shutdown();
    log.info("Finished...");
  }

  /**
   * Create decision point data structure
   */
  private DecisionPointDataStructure createDecisionPoint(List<FeatureNormalizer> normalizers,
      Map<DecisionState, Vec> statesAndTheirMeans, Policy policy) {
    Set<DecisionPointDataStructure.StateWithTransition> states = statesAndTheirMeans.entrySet()
        .stream()
        .map(entry -> new DecisionPointDataStructure.StateWithTransition(
            entry.getValue().arrayCopy(),
            NextActionEnumerations.getActionMap(policy.action(entry.getKey()).actionName(),
                policy.actionProb(entry.getKey(), policy.action(entry.getKey())))))
        .collect(Collectors.toSet());

    //check if all actions are available
    return new DecisionPointDataStructure(states, normalizers);
  }

  @AllArgsConstructor
  @Getter
  private static class Tuple {

    private final MapSizeEnums mapSize;
    private final ARace race;
    private final AgentTypeID agentTypeID;
    private final DesireKeyID desireKeyID;
  }

  /**
   * Do clustering and prepare data set for policy learning. Learn policy
   */
  @AllArgsConstructor
  public class MyTask implements Runnable {

    private final Tuple tuple;

    @Override
    public void run() {
      String path = storageService
          .getLearntDecisionPath(tuple.agentTypeID, tuple.desireKeyID, tuple.mapSize,
              tuple.race);

      if(new File(path).exists()) {
        log.info("File "+path+" exists, skipping.");
        return;
      }

      // touch file right away
      try {
        File file = new File(path);
        file.getParentFile().mkdirs();
        Files.createFile(file.toPath());
        log.info("Created placeholder "+path);
      } catch (IOException e) {
        log.warn("Failed to create "+path);
      }

      log.info("Starting computation for " + path);

      //TODO load for bots as well. Use human example to learn policy only
      List<TrajectoryWrapper> trajectoriesWrapped = storageService
          .getRandomListOfTrajectories(tuple.agentTypeID, tuple.desireKeyID, tuple.getMapSize(),
              tuple.getRace(), -1);
      List<Trajectory> trajectories = trajectoriesWrapped.stream()
          .map(TrajectoryWrapper::getTrajectory)
          .collect(Collectors.toList());

      //get number of features for state
      int numberOfFeatures = trajectoriesWrapped.get(0).getTrajectory().getNumberOfFeatures();

      log.info("Number of trajectories: " + trajectoriesWrapped.size()
          + " with cardinality of features: "
          + numberOfFeatures + " for " + tuple.desireKeyID.getName() + " of " + tuple.agentTypeID
          .getName() + " on " + tuple.mapSize + " with " + tuple.race);

      //find representatives of states
      List<State> states = trajectoriesWrapped.stream()
          .map(TrajectoryWrapper::getTrajectory)
          .map(Trajectory::getStates)
          .flatMap(Collection::stream)
          .collect(Collectors.toList());
      log.info("Number of states: " + states.size() + " for " + tuple.desireKeyID.getName() + " of "
          + tuple.agentTypeID.getName() + " on " + tuple.mapSize + " with " + tuple.race);

      List<FeatureNormalizer> normalizers = stateClusteringService
          .computeFeatureNormalizersBasedOnStates(states, numberOfFeatures);
      List<Vec> classes = stateClusteringService
          .computeStateRepresentatives(trajectories, states, normalizers);

      //create states with corresponding mean
      Map<DecisionState, Vec> statesAndTheirMeans = new HashMap<>();
      for (int i = 0; i < classes.size(); i++) {
        statesAndTheirMeans.put(new DecisionState(i), classes.get(i));
      }

      log.info("Creating MDP... for " + tuple.desireKeyID.getName() + " of " + tuple.agentTypeID
          .getName() + " on " + tuple.mapSize + " with " + tuple.race);

      //create transitions
      Map<DecisionState, Map<NextActionEnumerations, Map<DecisionState, Double>>> transitions = new HashMap<>();
      List<Episode> episodes = new ArrayList<>();
      for (TrajectoryWrapper trajectoryWrapper : trajectoriesWrapped) {
        List<State> stateList = trajectoryWrapper.getTrajectory().getStates();
        if (stateList.size() <= 2) {
          continue;
        }

        Episode episode = new Episode();
        //convert vectors to states (more memory intensive but faster)
        List<DecisionState> convertedOnStates = stateList.parallelStream()
            .map(state -> closestStateRepresentative(new DenseVector(
                    Configuration
                        .normalizeFeatureVector(state.getFeatureVector(), normalizers)),
                statesAndTheirMeans))
            .collect(Collectors.toList());

        DecisionState currentState = convertedOnStates.get(0);
        NextActionEnumerations nextAction = NextActionEnumerations
            .returnNextAction(stateList.get(0).isCommittedWhenTransiting());

        //add initial position in expert trajectory
        if (trajectoryWrapper.isUsedToLearnPolicy()) {
          episode.addState(convertedOnStates.get(0));
        }

        //there is no transition in last state
        for (int i = 1; i < stateList.size(); i++) {
          Map<DecisionState, Double> transitedTo = transitions
              .computeIfAbsent(currentState, decisionState -> new HashMap<>())
              .computeIfAbsent(nextAction, actionEnumerations -> new HashMap<>());
          currentState = convertedOnStates.get(i);

          //add transition to episode. Do not add last transition if agent is committed
          if (trajectoryWrapper.isUsedToLearnPolicy()) {
            episode.transition(new SimpleAction(nextAction.name()), currentState,
                DecisionDomainGenerator.defaultReward);
          }

          nextAction = NextActionEnumerations
              .returnNextAction(stateList.get(i).isCommittedWhenTransiting());

          //increment count of this type transitions
          Double currentValue = transitedTo.get(currentState);
          if (currentValue == null) {
            currentValue = 0.0;
          }
          transitedTo.put(currentState, currentValue + 1.0);
        }

        if (trajectoryWrapper.isUsedToLearnPolicy()) {
          episodes.add(episode);
        }
      }

      log.info("Preparing data for policy learning... for " + tuple.desireKeyID.getName()
          + " of " + tuple.agentTypeID.getName() + " on " + tuple.mapSize + " with "
          + tuple.race);

      Map<DecisionState, Map<NextActionEnumerations, Double>> sums = transitions
          .keySet()
          .stream()
          .collect(Collectors.toMap(Function.identity(),
              o -> transitions.get(o).entrySet().stream()
                  .collect(
                      Collectors.groupingBy(Map.Entry::getKey, Collectors.summingDouble(
                          value -> value.getValue().values().stream().mapToDouble(v -> v)
                              .sum())))
          ));

      //fill DecisionModel
      DecisionModel decisionModel = new DecisionModel();
      transitions.forEach((decisionState, nextActionEnumerationsMapMap) ->
          nextActionEnumerationsMapMap
              .forEach((actionEnumerations, decisionStateDoubleMap) ->
                  decisionStateDoubleMap.forEach((ds, aDouble) -> decisionModel
                      .addTransition(decisionState, actionEnumerations, ds,
                          aDouble / sums.get(decisionState).get(actionEnumerations))))
      );
      decisionModel.addExtraTransitionToStateWhenNoActionExists();

      //learn policy
      DecisionDomainGenerator decisionDomainGenerator = new DecisionDomainGenerator(
          decisionModel);
      SADomain domain = decisionDomainGenerator.generateDomain();

      log.info("Learning policy... for " + tuple.desireKeyID.getName() + " of "
          + tuple.agentTypeID
          .getName() + " on " + tuple.mapSize + " with " + tuple.race);

      List<Episode> episodesToUse;
      if (episodes.size() > 35) {
        int middle = ((episodes.size() - 25) / 2) + 5;
        episodes.sort(Comparator.comparingInt(o -> o.stateSequence.size()));
        episodesToUse = Stream.concat(
            Stream.concat(episodes
                    .subList(episodes.size() - 20, episodes.size())
                    .stream(),
                episodes.subList(0, 10).stream()),
            episodes.subList(middle - 3, middle + 2).stream())
            .collect(Collectors.toList());
      } else {
        episodesToUse = new ArrayList<>(episodes);
      }
      episodesToUse.sort(Comparator.comparingInt(o -> o.stateSequence.size()));
      Policy policy = policyLearningService
          .learnPolicy(domain, episodesToUse, classes.size() + 1,
              episodesToUse.size());
      //form decision point data structure and store it
      DecisionPointDataStructure decisionPoint = createDecisionPoint(normalizers,
          statesAndTheirMeans, policy);

      try {
        storageService
            .storeLearntDecision(decisionPoint, tuple.getAgentTypeID(), tuple.getDesireKeyID(),
                tuple.getMapSize(), tuple.getRace());
        log.info(
            "Successfully learn decisions for " + tuple.getDesireKeyID().getName() + " of "
                + tuple.getAgentTypeID().getName() + " on " + tuple.mapSize + " with "
                + tuple.race);
      } catch (Exception e) {
        e.printStackTrace();
        log.error(e.getLocalizedMessage());
      }
    }
  }


}
