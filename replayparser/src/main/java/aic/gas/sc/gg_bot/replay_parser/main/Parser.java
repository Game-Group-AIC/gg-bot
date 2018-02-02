package aic.gas.sc.gg_bot.replay_parser.main;

import aic.gas.sc.gg_bot.replay_parser.service.IReplayLoaderService;
import aic.gas.sc.gg_bot.replay_parser.service.implementation.FileReplayLoaderService;
import aic.gas.sc.gg_bot.replay_parser.service.implementation.FolderReplayLoaderService;
import aic.gas.sc.gg_bot.replay_parser.service.implementation.RabbitMQReplayLoaderService;
import aic.gas.sc.gg_bot.replay_parser.service.implementation.ReplayParserService;
import java.io.IOException;

/**
 */
public class Parser {

  public static void main(String[] args) throws Exception {
    //to speed things up when executing parallel stream
    System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "100");

    IReplayLoaderService replayLoader = createReplayLoader(args);

    ReplayParserService replayParserService = new ReplayParserService(replayLoader,
        args.length == 3 && args[2].equals("-w"));
    replayParserService.parseReplays();
  }

  private static IReplayLoaderService createReplayLoader(String[] args) throws IOException {
    IReplayLoaderService replayLoader;

    if (args.length < 1 || args.length > 3) {
      showHelp(args);
    }

    switch (args[0]) {
      case "--rabbit":
        String host = System.getenv("RABBITMQ_BROKER_HOST");
        String user = System.getenv("RABBITMQ_DEFAULT_USER");
        String pass = System.getenv("RABBITMQ_DEFAULT_PASS");
        String port = System.getenv("RABBITMQ_BROKER_PORT");

        if (host == null || user == null || pass == null || port == null) {
          System.out.println("Could not load env config!");
          System.exit(1);
        }
        replayLoader = new RabbitMQReplayLoaderService(host, user, pass,
            Integer.parseInt(port));
        break;
      case "--folder":
        System.out.println(args[1]);
        replayLoader = new FolderReplayLoaderService(args[1]);
        break;
      case "--file":
        System.out.println(args[1]);
        replayLoader = new FileReplayLoaderService(args[1]);
        break;
      default:
        replayLoader = null;
        showHelp(args);
    }

    return replayLoader;
  }

  private static void showHelp(String args[]) {
    System.out.println("Invalid usage!");
    for (int i = 0; i < args.length; i++) {
      System.out.println(args[i]);
    }

    System.out.println("Use either");
    System.out.println("--rabbit");
    System.out.println("--folder <folder>");
    System.out.println("--file <file>");
    System.out.println("-w ... to run with default WIN configuration at the end of command");

    System.exit(1);
  }
}
