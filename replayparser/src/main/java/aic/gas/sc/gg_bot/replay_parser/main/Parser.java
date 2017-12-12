package aic.gas.sc.gg_bot.replay_parser.main;

import aic.gas.sc.gg_bot.replay_parser.service.implementation.ReplayParserServiceImpl;

/**
 */
public class Parser {

  public static void main(String[] args) throws Exception {
    //to speed things up when executing parallel stream
//    System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "100");

    ReplayParserServiceImpl replayParserService = new ReplayParserServiceImpl();
    replayParserService.parseReplays();
  }
}
