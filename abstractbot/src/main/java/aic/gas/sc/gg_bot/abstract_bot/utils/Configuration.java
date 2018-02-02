package aic.gas.sc.gg_bot.abstract_bot.utils;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.MapSizeEnums;
import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import jsat.linear.distancemetrics.DistanceMetric;
import jsat.linear.distancemetrics.EuclideanDistance;
import lombok.extern.slf4j.Slf4j;

/**
 * Contains configuration...
 */
@Slf4j
public class Configuration {

  /**
   * Map static fields of agentTypeId from AgentTypes to folders in storage
   */
  public static Set<AgentTypeID> getParsedAgentTypesContainedInStorage(String folder,
      MapSizeEnums mapSize, ARace race) {
    File directory = new File(folder + "/" + mapSize.name() + "/" + race.name());
    if (!directory.exists()) {
      return new HashSet<>();
    }

    Set<String> foldersInParsingDirectory = Arrays.stream(directory.listFiles())
        .filter(File::isDirectory)
        .map(File::getName)
        .collect(Collectors.toSet());
    return Arrays.stream(AgentTypes.class.getDeclaredFields())
        .filter(field -> Modifier.isStatic(field.getModifiers()))
        .filter(field -> field.getType().equals(AgentTypeID.class))
        .map(field -> {
              try {
                return (AgentTypeID) field.get(null);
              } catch (IllegalAccessException e) {
                log.error(e.getLocalizedMessage());
              }
              return null;
            }
        )
        .filter(Objects::nonNull)
        .filter(agentTypeID -> foldersInParsingDirectory.contains(agentTypeID.getName()))
        .collect(Collectors.toSet());
  }

  /**
   * Map static fields of desireKeyID from DesireKeys to folders in storage
   */
  public static Set<DesireKeyID> getParsedDesireTypesForAgentTypeContainedInStorage(
      AgentTypeID agentTypeID, String folder, MapSizeEnums mapSize, ARace race) {
    File directory = new File(
        folder + "/" + mapSize.name() + "/" + race.name() + "/" + agentTypeID.getName());
    Set<String> filesInParsingDirectory = Arrays.stream(directory.listFiles())
        .filter(File::isFile)
        .map(File::getName)
        .map(s -> s.replace(".db", ""))
        .collect(Collectors.toSet());
    return Arrays.stream(DesireKeys.class.getDeclaredFields())
        .filter(field -> Modifier.isStatic(field.getModifiers()))
        .filter(field -> field.getType().equals(DesireKeyID.class))
        .map(field -> {
              try {
                return (DesireKeyID) field.get(null);
              } catch (IllegalAccessException e) {
                log.error(e.getLocalizedMessage());
              }
              return null;
            }
        )
        .filter(Objects::nonNull)
        .filter(desireKeyID -> filesInParsingDirectory.stream()
            .anyMatch(s -> s.contains(desireKeyID.getName())))
        .collect(Collectors.toSet());
  }

}
