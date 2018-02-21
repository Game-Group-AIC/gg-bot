package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import static aic.gas.sc.gg_bot.abstract_bot.utils.Utils.getParsedAgentTypesContainedInStorage;
import static aic.gas.sc.gg_bot.abstract_bot.utils.Utils.getParsedDesireTypesForAgentTypeContainedInStorage;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DecisionConfiguration;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.MapSizeEnums;
import aic.gas.sc.gg_bot.abstract_bot.model.decision.MDPForDecisionWithPolicy;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import aic.gas.sc.gg_bot.abstract_bot.utils.SerializationUtil;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.Trajectory;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.TrajectoryWrapper;
import aic.gas.sc.gg_bot.replay_parser.service.IStorageService;
import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * IStorageService implementation... as singleton
 */
@Slf4j
public class StorageService implements IStorageService {

  //databases

  //subdirectories
  private static final List<String> subdirectories = Stream.of(MapSizeEnums.values())
      .flatMap(size -> Stream.of(ARace.values())
          .map(race -> size.name() + "/" + race.name()))
      .collect(Collectors.toList());
  // we need two folders: human
  private static final String storageFolder = "storage";
  private static final String parsingFolder = storageFolder + "/parsing";
  private static final String humanFolder = storageFolder + "/human/parsing";
  private static final String otherFolder = storageFolder + "/other/parsing";
  private static final String outputFolder = storageFolder + "/output";
  private static StorageService instance = null;

  private StorageService() {
    //singleton
    createDirectoryIfItDoesNotExist(storageFolder);
    createDirectoryIfItDoesNotExist(parsingFolder);
    createDirectoryIfItDoesNotExist(outputFolder);

    //create map folders
    Stream.of(MapSizeEnums.values()).map(Enum::name)
        .forEach(s -> {
          createDirectoryIfItDoesNotExist(parsingFolder + "/" + s);
          createDirectoryIfItDoesNotExist(outputFolder + "/" + s);
        });

    //create subdirectories
    subdirectories.forEach(s -> {
      createDirectoryIfItDoesNotExist(parsingFolder + "/" + s);
      createDirectoryIfItDoesNotExist(outputFolder + "/" + s);
    });
  }

  static IStorageService getInstance() {
    if (instance == null) {
      instance = new StorageService();
    }
    return instance;
  }


  @Override
  public void saveTrajectory(AgentTypes agentType, DesireKeys desireKey,
      List<Trajectory> trajectories) {
    createDirectoryIfItDoesNotExist(agentType.name(),
        parsingFolder + "/" + DecisionConfiguration.getMapSize().name() + "/"
            + DecisionConfiguration.getRace().name());
    String uuid = UUID.randomUUID().toString();
    String path =
        parsingFolder + "/" + DecisionConfiguration.getMapSize().name() + "/"
            + DecisionConfiguration.getRace().name() + "/" + agentType + "/"
            + desireKey + "_" + uuid
            + ".db";
    ArrayList<Trajectory> savedTrajectories = new ArrayList<>(trajectories);
    try {
      log.info("Saving " + savedTrajectories.size() + " trajectories to " + path);
      SerializationUtil.serialize(savedTrajectories, path);
    } catch (Exception e) {
      log.error("Could not save list. Due to " + e.getLocalizedMessage());
    }
  }

  @Override
  public Map<AgentTypes, Set<DesireKeys>> getParsedAgentTypesWithDesiresTypesContainedInStorage(
      MapSizeEnums mapSize, ARace race) {
    Map<AgentTypes, Set<DesireKeys>> humans = getParsedAgentTypesContainedInStorage(humanFolder,
        mapSize, race).stream()
        .filter(DecisionConfiguration.decisionsToLoad::containsKey)
        .collect(Collectors.toMap(Function.identity(),
            t -> getParsedDesireTypesForAgentTypeContainedInStorage(t, humanFolder, mapSize, race)
                .stream()
                .filter(desireKey -> DecisionConfiguration.decisionsToLoad
                    .getOrDefault(t, new HashSet<>()).contains(desireKey))
                .collect(Collectors.toSet())
        ));

    Map<AgentTypes, Set<DesireKeys>> others = getParsedAgentTypesContainedInStorage(otherFolder,
        mapSize, race).stream()
        .filter(DecisionConfiguration.decisionsToLoad::containsKey)
        .collect(Collectors.toMap(Function.identity(),
            t -> getParsedDesireTypesForAgentTypeContainedInStorage(t, otherFolder, mapSize, race)
                .stream()
                .filter(desireKey -> DecisionConfiguration.decisionsToLoad
                    .getOrDefault(t, new HashSet<>()).contains(desireKey))
                .collect(Collectors.toSet())
        ));

    // merge the two maps
    for (AgentTypes otherKey : others.keySet()) {
      if (humans.containsKey(otherKey)) {
        humans.get(otherKey).addAll(others.get(otherKey));
      } else {
        humans.put(otherKey, others.get(otherKey));
      }
    }

    return humans;
  }

  @Override
  public List<TrajectoryWrapper> getRandomListOfTrajectories(AgentTypes agentType,
      DesireKeys desireKey, MapSizeEnums mapSize, ARace race, int limit) {
    List<File> files = new ArrayList<>(
        getFilesForAgentTypeOfGivenDesire(agentType, desireKey, mapSize, race));
    Collections.shuffle(files);

    //unlimited
    if (limit == -1) {
      limit = files.size();
    }

    return files.subList(0, Math.min(limit, files.size())).stream()
        .map(File::getPath)
        .flatMap(s -> {
          try {
            boolean isHumanReplay = s.contains("human");

            // noinspection unchecked
            return ((List<Trajectory>) SerializationUtil.deserialize(s)).stream()
                .map(trajectory -> TrajectoryWrapper.builder()
                    .trajectory(trajectory)
                    .usedToLearnPolicy(isHumanReplay)
                    .build());
          } catch (Exception e) {
            log.error(e.getLocalizedMessage());
          }
          return Stream.empty();
        })
        .collect(Collectors.toList());
  }

  /**
   * Get files for AgentType Of given desire
   */
  private Set<File> getFilesForAgentTypeOfGivenDesire(AgentTypes agentType,
      DesireKeys desireKey, MapSizeEnums mapSize, ARace race) {

    Set<File> humanFiles = SerializationUtil
        .getAllFilesInFolder(humanFolder + "/" + mapSize.name() + "/"
            + "/" + race.name() + "/" + agentType, "db");
    Set<File> otherFiles = SerializationUtil
        .getAllFilesInFolder(otherFolder + "/" + mapSize.name() + "/"
            + "/" + race.name() + "/" + agentType, "db");
    Set<File> allFiles = new HashSet<>();
    allFiles.addAll(humanFiles);
    allFiles.addAll(otherFiles);

    return allFiles
        .stream()
        .filter(file -> file.getName().contains(desireKey.name()))
        .collect(Collectors.toSet());
  }


  @Override
  public void storeLearntDecision(MDPForDecisionWithPolicy structure, AgentTypes agentType,
      DesireKeys desireKey, MapSizeEnums mapSize, ARace race) throws Exception {
    createDirectoryIfItDoesNotExist(agentType.name(), outputFolder + "/" + mapSize.name() + "/"
        + "/" + race.name());
    String path = getLearntDecisionPath(agentType, desireKey, mapSize, race);
    SerializationUtil.serialize(structure, path);
  }

  @Override
  public String getLearntDecisionPath(AgentTypes agentType, DesireKeys desireKey,
      MapSizeEnums mapSize, ARace race) {
    return outputFolder + "/" + mapSize.name() + "/"
        + "/" + race.name() + "/" + agentType.name() + "/" + desireKey.name() + ".db";
  }

  /**
   * Create story directory for agent
   */
  private void createDirectoryIfItDoesNotExist(String name, String directory) {
    File file = new File(directory + "/" + name);
    if (!file.exists()) {
      if (file.mkdir()) {
        log.info("Creating storage directory for " + name);
      } else {
        log.error("Could not create storage directory for " + name);
      }
    }
  }

  /**
   * Create story directory for agent
   */
  private void createDirectoryIfItDoesNotExist(String name) {
    File file = new File(name);
    if (!file.exists()) {
      if (file.mkdir()) {
        log.info("Creating storage directory for " + name + ". Path is " + file.getPath());
      } else {
        log.error("Could not create storage directory for " + name);
      }
    }
  }
}
