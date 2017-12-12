package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.replay_parser.service.ReplayLoaderService;
import aic.gas.sc.gg_bot.replay_parser.service.StorageService;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Concrete implementation of service to load replays files
 */
@Slf4j
public class FileReplayLoaderServiceImpl implements ReplayLoaderService {

  private static final StorageService STORAGE_SERVICE = StorageServiceImp.getInstance();
  private static final Pattern lineWithMatchPattern = Pattern
      .compile("map\\s=\\s[\\w\\\\\\*\\.\\?\\-\\s\\[\\]\\)\\(&,`]+\\.rep");
  //paths
  private static final String replaysPath = "c:\\Program Files (x86)\\StarCraft\\Maps\\replays";
  private static final String playersFolder = "players";
  private static final String bwapiIniPath = "c:\\Program Files (x86)\\StarCraft\\bwapi-data\\bwapi.ini";
  private static final String starcraftPath = "c:\\Program Files (x86)\\StarCraft";
  //files
  private static final File bwapiIni = new File(bwapiIniPath);
  private static final File starcraftFolder = new File(starcraftPath);
  private Iterator<File> replayIterator = (new HashSet<File>()).iterator();

  /**
   * Get all replays in replay folder
   */
  private static Set<File> getAllFilesInFolder(String directoryName) {
    File directory = new File(directoryName);

    //get all the files from a directory
    File[] fList = directory.listFiles();
    Set<File> files = new HashSet<>();
    for (File file : fList) {
      if (file.isFile()) {
        String fileExtension = Files.getFileExtension(file.getPath());
        if (fileExtension.equals("rep")) {
          files.add(file);
        }
      } else {
        if (file.isDirectory()) {
          Set<File> replays = getAllFilesInFolder(directoryName + "\\" + file.getName());
          files.addAll(replays);
        }
      }
    }
    return files;
  }

  @Override
  public void loadReplaysToParse() {
    Set<File> allReplays = getAllFilesInFolder(replaysPath + "\\" + playersFolder);
    Set<File> replaysToParse = STORAGE_SERVICE.filterNotPlayedReplays(allReplays);
    log.info(replaysToParse.size() + " replays will be parsed.");
    replayIterator = replaysToParse.iterator();
  }

  @Override
  public File returnNextReplayToPlay() throws Exception {
    if (!replayIterator.hasNext()) {
      log.info("All replays were parsed.");
      throw new IndexOutOfBoundsException("All replays were parsed.");
    } else {
      File replayToParseNext = replayIterator.next();
      replayIterator.remove();
      setupReplayInConfigurationFile(replayToParseNext);
      log.info(
          "New replay was setup in configuration file. Replay file set to: " + replayToParseNext);
      return replayToParseNext;
    }
  }

  /**
   * Set up configuration on given replay file
   */
  private void setupReplayInConfigurationFile(File replayFile) throws IOException {
    Path pathToReplay = replayFile.toPath();
    Path pathOfSC = starcraftFolder.toPath();
    String pathToReplayRelativeToSCFolder = pathOfSC.relativize(pathToReplay).toString();

    List<String> fileLines = Files.readLines(bwapiIni, StandardCharsets.UTF_8);
    for (int i = 0; i < fileLines.size(); i++) {
      Matcher matcher = lineWithMatchPattern.matcher(fileLines.get(i));
      if (matcher.matches()) {
        fileLines.set(i, "map = " + pathToReplayRelativeToSCFolder);
        break;
      }
    }
    String fileContent = fileLines.stream().map(Object::toString).collect(Collectors.joining("\n"));
    Files.write(fileContent, bwapiIni, StandardCharsets.UTF_8);
  }

}
