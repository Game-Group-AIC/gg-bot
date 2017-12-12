package aic.gas.sc.gg_bot.replay_parser.service;

import java.io.File;

/**
 * Interface to describe service for replays file parser
 */
public interface ReplayLoaderService {

  void loadReplaysToParse();

  File returnNextReplayToPlay() throws Exception;

}
