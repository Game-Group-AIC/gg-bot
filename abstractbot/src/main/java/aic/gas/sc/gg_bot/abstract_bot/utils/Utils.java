package aic.gas.sc.gg_bot.abstract_bot.utils;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.MapSizeEnums;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Contains configuration...
 */
@Slf4j
public class Utils {

  public static Set<AgentTypes> getParsedAgentTypesContainedInStorage(String folder,
      MapSizeEnums mapSize, ARace race) {
    File directory = new File(folder + "/" + mapSize + "/" + race);
    if (!directory.exists()) {
      return new HashSet<>();
    }

    Set<String> foldersInParsingDirectory = Arrays.stream(directory.listFiles())
        .filter(File::isDirectory)
        .map(File::getName)
        .collect(Collectors.toSet());

    return Arrays.stream(AgentTypes.values())
        .filter(agentType -> foldersInParsingDirectory.contains(agentType.name()))
        .collect(Collectors.toSet());
  }

  public static Set<DesireKeys> getParsedDesireTypesForAgentTypeContainedInStorage(
      AgentTypes agentType, String folder, MapSizeEnums mapSize, ARace race) {
    File directory = new File(folder + "/" + mapSize + "/" + race + "/" + agentType);
    Set<String> filesInParsingDirectory = Arrays.stream(directory.listFiles())
        .filter(File::isFile)
        .map(File::getName)
        // remove uuid, lazy to calc its length
        .map(s -> s.substring(0, s.length() - "_db469ebd-3014-416d-8ce9-8e0c98e7851d.db".length()))
        .collect(Collectors.toSet());

    return Arrays.stream(DesireKeys.values())
        .filter(desireKey -> filesInParsingDirectory.stream()
            .anyMatch(s -> s.contains(desireKey.name())))
        .collect(Collectors.toSet());
  }

}
