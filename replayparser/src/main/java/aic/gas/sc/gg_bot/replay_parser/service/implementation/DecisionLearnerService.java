package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.MapSizeEnums;
import aic.gas.sc.gg_bot.abstract_bot.model.decision.MetaPolicy;
import aic.gas.sc.gg_bot.abstract_bot.model.decision.NextActionEnumerations;
import aic.gas.sc.gg_bot.abstract_bot.model.decision.StateBuilder;
import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureContainerHeader;
import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import aic.gas.sc.gg_bot.replay_parser.configuration.Configuration;
import aic.gas.sc.gg_bot.replay_parser.configuration.DecisionLearningConfiguration;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.State;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.Trajectory;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.TrajectoryWrapper;
import aic.gas.sc.gg_bot.replay_parser.service.IDecisionLearnerService;
import aic.gas.sc.gg_bot.replay_parser.service.IPolicyLearningService;
import aic.gas.sc.gg_bot.replay_parser.service.IStorageService;
import burlap.behavior.singleagent.Episode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of IDecisionLearnerService
 */
@Slf4j
public class DecisionLearnerService implements IDecisionLearnerService {

  private static final List<MapSizeEnums> MAP_SIZES = Stream.of(MapSizeEnums.values())
      .collect(Collectors.toList());
  private static final List<ARace> RACES = Stream.of(ARace.values())
      .filter(race -> !race.equals(ARace.UNKNOWN))
      .collect(Collectors.toList());
  private final IStorageService storageService = StorageService.getInstance();
  private final IPolicyLearningService policyLearningService = new PolicyLearningService();

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

    ExecutorService executor = Executors.newFixedThreadPool(parallelismLevel);
    tasks.forEach(executor::execute);

    executor.shutdown();

    log.info("Finished...");
  }

  //build state builder...
  private static StateBuilder getStateBuilder(List<TrajectoryWrapper> trajectoriesWrapped,
      int numberOfFeatures) {

    //encountered states
    List<State> states = trajectoriesWrapped.stream()
        .map(TrajectoryWrapper::getTrajectory)
        .map(Trajectory::getStates)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());

    FeatureNormalizer[] normalizers = IntStream.range(0, numberOfFeatures)
        .boxed()
        .map(i -> states.stream()
            .map(state -> state.getFeatureVector()[i])
            .collect(Collectors.toList()))
        .map(FeatureNormalizer::new)
        .toArray(FeatureNormalizer[]::new);

    return new StateBuilder(normalizers);
  }

  /**
   * Do clustering and prepare data set for policy learning. Learn policy
   */
  @AllArgsConstructor
  public class MyTask implements Runnable {

    private final Tuple tuple;

    @Override
    public void run() {
      String path = storageService.getLearntDecisionPath(tuple.agentTypeID, tuple.desireKeyID,
          tuple.mapSize, tuple.race);

      if (new File(path).exists()) {
        log.info("File " + path + " exists, skipping.");
        return;
      }

      // touch file right away
      try {
        File file = new File(path);
        file.getParentFile().mkdirs();
        Files.createFile(file.toPath());
        log.info("Created placeholder " + path);
      } catch (IOException e) {
        log.warn("Failed to create " + path);
      }

      log.info("Starting computation for " + path);

      Configuration configuration = DecisionLearningConfiguration
          .getConfiguration(tuple.agentTypeID, tuple.desireKeyID);

      List<TrajectoryWrapper> trajectoriesWrapped = storageService.getRandomListOfTrajectories(
          tuple.agentTypeID, tuple.desireKeyID, tuple.getMapSize(), tuple.getRace(), -1);

      //load headers
      Optional<FeatureContainerHeader> header = FeatureContainerHeaders.getHeader(tuple.agentTypeID,
          tuple.desireKeyID);
      String[] headers;
      if (header.isPresent()) {
        headers = header.get().getHeaders();
      } else {
        log.error("Could not find header for " + tuple.agentTypeID.getName() + ", "
            + tuple.desireKeyID.getName());

        //get number of features for state
        int numberOfFeatures = trajectoriesWrapped.get(0).getTrajectory().getNumberOfFeatures();
        headers = new String[numberOfFeatures];
        IntStream.range(0, numberOfFeatures).boxed()
            .forEach(integer -> headers[integer] = "N/A");
      }

      log.info("Number of trajectories: " + trajectoriesWrapped.size() + " with cardinality of "
          + "features: " + headers.length + " for " + tuple.desireKeyID.getName() + " of " +
          tuple.agentTypeID.getName() + " on " + tuple.mapSize + " with " + tuple.race + ".");

      StateBuilder stateBuilder = getStateBuilder(trajectoriesWrapped, headers.length);

      //create episodes to learn from
      List<Episode> episodesToLearnFrom = trajectoriesWrapped.stream()
          .filter(TrajectoryWrapper::isUsedToLearnPolicy)
          .map(TrajectoryWrapper::getTrajectory)
          .map(trajectory -> {
            Episode episode = new Episode();
            trajectory.getStates().forEach(state -> {
              episode.addState(stateBuilder.buildState(state.getFeatureVector()));
              episode.addAction(NextActionEnumerations
                  .returnNextAction(state.isCommittedWhenTransiting()));
            });
            return episode;
          })
          .filter(episode -> episode.maxTimeStep() > 0)
          .collect(Collectors.toList());

      log.info("IRL routine...");
      MetaPolicy metaPolicy = policyLearningService.learnPolicy(episodesToLearnFrom, stateBuilder,
          configuration, headers.length);
      try {
        storageService.storeLearntDecision(metaPolicy, tuple.getAgentTypeID(),
            tuple.getDesireKeyID(), tuple.getMapSize(), tuple.getRace());
        log.info("Successfully learn decisions for " + tuple.getDesireKeyID().getName() + " of "
            + tuple.getAgentTypeID().getName() + " on " + tuple.mapSize + " with " + tuple.race);
      } catch (Exception e) {
        e.printStackTrace();
        log.error(e.getLocalizedMessage());
      }
    }
  }

  @AllArgsConstructor
  @Getter
  private static class Tuple {

    private final MapSizeEnums mapSize;
    private final ARace race;
    private final AgentTypeID agentTypeID;
    private final DesireKeyID desireKeyID;
  }

}
