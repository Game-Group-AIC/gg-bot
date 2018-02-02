package aic.gas.sc.gg_bot.replay_parser.service;

import java.io.File;

/**
 * Interface to describe service for replays file parser
 */
public interface IReplayLoaderService {

  void loadReplaysToParse();

  File returnNextReplayToPlay() throws Exception;

  void finishedProcessing(File replay);

}
