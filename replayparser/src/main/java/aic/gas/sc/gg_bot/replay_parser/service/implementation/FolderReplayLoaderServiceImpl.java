package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.replay_parser.service.ReplayLoaderService;
import aic.gas.sc.gg_bot.replay_parser.service.StorageService;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * Concrete implementation of service to load replays files
 */
@Slf4j
public class FolderReplayLoaderServiceImpl implements ReplayLoaderService {

  static final StorageService STORAGE_SERVICE = StorageServiceImp.getInstance();
  String replaySource = "c:\\sc\\Maps\\replays";

  Iterator<File> replayIterator = (new HashSet<File>()).iterator();

  public FolderReplayLoaderServiceImpl(String replaySource) throws IOException {
    this.replaySource = replaySource;
    System.out.println(new File(replaySource));
//    if(!new File(replaySource).exists()) {
//      throw new IOException("Replay source does not exist! "+replaySource);
//    }
  }

  /**
   * Get all replays in replay folder
   */
  private Set<File> getAllFilesInFolder(String directoryName) {
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
    Set<File> replaysToParse = getAllFilesInFolder(replaySource);
//    Set<File> replaysToParse = STORAGE_SERVICE.filterNotPlayedReplays(allReplays);
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
      log.info("New replay was setup in configuration file. Replay file set to: " + replayToParseNext);
      return replayToParseNext;
    }
  }

  @Override
  public void finishedProcessing(File replay) {

  }

}
