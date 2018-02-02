package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FileReplayLoaderService extends FolderReplayLoaderService {

  public FileReplayLoaderService(String replaySource) throws IOException {
    super(replaySource);
  }

  @Override
  public void loadReplaysToParse() {
    Set<File> replaysToParse = new HashSet<>();
    replaysToParse.add(new File(replaySource));
//    Set<File> replaysToParse = STORAGE_SERVICE.filterNotPlayedReplays(allReplays);
    replayIterator = replaysToParse.iterator();
  }
}
