package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.replay_parser.model.tracking.Replay;
import aic.gas.sc.gg_bot.replay_parser.service.ReplayLoaderService;
import aic.gas.sc.gg_bot.replay_parser.service.ReplayParserService;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Concrete implementation of service to parse replays
 */
public class ReplayParserServiceImpl extends DefaultBWListener implements ReplayParserService {

  private static final Pattern lineWithMatchPattern = Pattern.compile("^map\\s*=\\s*.+$");
  //paths
  private static final String bwapiIniPath = "c:\\sc\\bwapi-data\\bwapi.ini";
  private static final String starcraftPath = "c:\\sc";
  //files
  private static final File bwapiIni = new File(bwapiIniPath);
  private static final File starcraftFolder = new File(starcraftPath);
  private ReplayLoaderService replayLoaderService;
  // Alternatively use this loader:
  //  private ReplayLoaderService replayLoaderService = new FileReplayLoaderServiceImpl();
  private Optional<Replay> replay;
  private Set<Player> players;
  private boolean shouldSkip = false;

  public ReplayParserServiceImpl(ReplayLoaderService replayLoader) {
    replayLoaderService = replayLoader;
  }

  /**
   * Method to start Chaosluncher with predefined configuration. Sadly process can be only started,
   * not closed. See comment in method body to setup it appropriately
   */
  private void startGame() throws IOException {
    // You might need to run this to be able to run linux cmd from wine:
    // https://stackoverflow.com/a/45545068/1233675
    //
    // k='HKLM\System\CurrentControlSet\Control\Session Manager\Environment'
    // pathext_orig=$( wine reg query "$k" /v PATHEXT | tr -d '\r' | awk '/^  /{ print $3 }' )
    // echo "$pathext_orig" | grep -qE '(^|;)\.(;|$)' || wine reg add "$k" /v PATHEXT /f /d "${pathext_orig};."
    System.err.println("Starting game...");
    Runtime rt = Runtime.getRuntime();
    rt.exec("/usr/bin/launch_game --headful");
  }

  /**
   * Return next replay to parse
   */
  private Replay getNextReplay() throws Exception {
    File nextReplay = replayLoaderService.returnNextReplayToPlay();
    return new Replay(nextReplay);
  }

  /**
   * Set next replay or terminate listener
   */
  private void setNextReplay() {
    try {
      System.err.println("setNextReplay");
      replay = Optional.of(getNextReplay());

      System.err.println("setupReplayInConfigurationFile");
      setupReplayInConfigurationFile(replay.get().getRawFile());

    } catch (Exception e) {
      e.printStackTrace();

      //terminate process
      System.exit(1);
    }
  }

  /**
   * Set up configuration bwapi.ini on given replay file
   */
  private void setupReplayInConfigurationFile(File replayFile) throws IOException {
    Path pathToReplay = replayFile.toPath();
    Path pathOfSC = starcraftFolder.toPath();
    String pathToReplayRelativeToSCFolder = pathOfSC.relativize(pathToReplay)
        .toString().replace("\\", "/");
    System.err.println("setupReplayInConfigurationFile.pathToReplayRelativeToSCFolder "
        + pathToReplayRelativeToSCFolder);

    List<String> fileLines = Files.readLines(bwapiIni, StandardCharsets.UTF_8);
    for (int i = 0; i < fileLines.size(); i++) {
      Matcher matcher = lineWithMatchPattern.matcher(fileLines.get(i));
      if (matcher.matches()) {
        fileLines.set(i, "map = " + pathToReplayRelativeToSCFolder);
        break;
      }
    }
    String fileContent = fileLines.stream().map(Object::toString).collect(Collectors.joining("\n"));

    System.err.println("setupReplayInConfigurationFile.writing to ini file");
    try (FileOutputStream out = new FileOutputStream(bwapiIni)) {
      java.nio.channels.FileLock lock = out.getChannel().lock();
      System.err.println("setupReplayInConfigurationFile.lock received");
      Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
      try {
        writer.write(fileContent);
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        lock.release();
      }
      System.err.println("setupReplayInConfigurationFile.writing finished");
      writer.close();
    }
//    Files.write(fileContent, bwapiIni, StandardCharsets.UTF_8);
  }


  @Override
  public void parseReplays() {

    // start game listener
    Thread gameListener = new Thread(new GameListener(), "GameListener");

    // load all not parsed replays
    System.err.println("load replays to parse");
    replayLoaderService.loadReplaysToParse();

    setNextReplay();

    try {
      gameListener.start();
      startGame();
    } catch (IOException e) {
      //terminate process
      System.err.println("Could not start game. " + e.getLocalizedMessage());
      System.exit(1);
    }
  }

  private class GameListener extends DefaultBWListener implements Runnable {

    private Mirror mirror = new Mirror();
    private Game currentGame;

    @Override
    public void onStart() {
      System.err.println("New game from replay " + replay.get().getFile().get());
      currentGame = mirror.getGame();

      currentGame.setLocalSpeed(0);
      currentGame.setGUI(false);
      shouldSkip = false;

      int numStartLocations = currentGame.getStartLocations().size();
      if (numStartLocations > 4) {
        System.err.println("Skipping this game - too many start location " + numStartLocations);
        currentGame.leaveGame();
        shouldSkip = true;
        return;
      }

      players = currentGame.getPlayers().stream()
          .filter(player -> !player.isNeutral())
          .peek(player -> System.err.println(
              player.getRace() + " id: " + player.getID() + " units: " + player.allUnitCount()))
          .collect(Collectors.toSet());

      int numPlayers = players.size();
      if (numPlayers != 2) {
        System.err.println("Skipping this game - there aren't 2 players but " + numPlayers);
        currentGame.leaveGame();
        shouldSkip = true;
        return;
      }

      System.out.println("aic.gas: -------------------------------");
      System.out.println("aic.gas: mapFileName " + currentGame.mapFileName());
      System.out.println("aic.gas: mapName " + currentGame.mapName());
      System.out.println("aic.gas: mapHash " + currentGame.mapHash());
      System.out.println("aic.gas: mapWidth " + currentGame.mapWidth());
      System.out.println("aic.gas: mapHeight " + currentGame.mapHeight());
      System.out.println("aic.gas: getReplayFrameCount " + currentGame.getReplayFrameCount());
      System.out.println("aic.gas: getStartLocations " + currentGame.getStartLocations().size());
      System.out.println("aic.gas: numPlayers " + numPlayers);

      int i = 0;
      for (Player player : players) {
        System.out.println("aic.gas: race[" + player.getID() + "] " + player.getRace());
        i++;
      }
    }

    @Override
    public void onFrame() {
      super.onFrame();
      if (shouldSkip) {
        return;
      }
      if (currentGame.getFrameCount() % 200 == 0) {
        System.err.println(currentGame.getFrameCount() + " / " + currentGame.getReplayFrameCount());
      }
    }

    @Override
    public void onPlayerLeft(Player player) {
      super.onPlayerLeft(player);
      if (shouldSkip) {
        return;
      }

      System.err.println(player.getRace() + " id: " + player.getID() + " left the game.");
      players.remove(player);
    }

    @Override
    public void onPlayerDropped(Player player) {
      super.onPlayerDropped(player);
      if (shouldSkip) {
        return;
      }

      System.err.println(player.getRace() + " id: " + player.getID() + " dropped the game.");
      players.remove(player);
    }

    @Override
    public void onEnd(boolean b) {
      try {
        if (!shouldSkip) {

          System.err.println("Game has finished. Processing data...");

          System.out.println("aic.gas: numberOfStayedPlayers " + players.size());
          // todo: add test for bots - winner is one that still has buildings
          for (Player player : players) {
            System.out.println("aic.gas: stayedPlayer " + player.getRace());
          }

          if (players.size() == 1) {
            System.out.println("aic.gas: winningRace " + players.iterator().next().getRace());
          } else {
            PlayerWithCounts bestPlayer = players.stream().map(
                player -> {
                  PlayerWithCounts playerWithCounts = new PlayerWithCounts(
                      player,
                      player.getUnits().stream()
                          .filter(unit -> unit.getType().isBuilding())
                          .count());
                  System.out.println("aic.gas: playerNumBuildings["
                      + playerWithCounts.getPlayer().getID() + "] " +
                      playerWithCounts.counts);

                  return playerWithCounts;
                }
            ).max(new Comparator<PlayerWithCounts>() {
                    @Override
                    public int compare(PlayerWithCounts o1, PlayerWithCounts o2) {
                      return Long.compare(o1.counts, o2.counts);
                    }
                  }
            ).get();

            System.out.println("aic.gas: bestPlayerNumBuildings " + bestPlayer.counts);
            System.out.println("aic.gas: winningRace " + bestPlayer.getPlayer().getRace());
          }
        }

        replayLoaderService.finishedProcessing(replay.get().getRawFile());
        setNextReplay();

        System.out.println("Data processed.");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public void run() {
      mirror.getModule().setEventListener(this);
      mirror.startGame();
    }
  }

  class PlayerWithCounts {

    Player player;
    long counts;

    public PlayerWithCounts(Player player, long counts) {
      this.player = player;
      this.counts = counts;
    }

    public Player getPlayer() {
      return player;
    }

    public long getCounts() {
      return counts;
    }
  }

}
