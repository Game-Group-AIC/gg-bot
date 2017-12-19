package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.DecisionConfiguration;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.MapSizeEnums;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AbstractPositionWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.UnitWrapperFactory;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.WrapperTypeFactory;
import aic.gas.sc.gg_bot.replay_parser.model.AgentMakingObservations;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.Replay;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension.*;
import aic.gas.sc.gg_bot.replay_parser.service.AgentUnitHandler;
import aic.gas.sc.gg_bot.replay_parser.service.ReplayLoaderService;
import aic.gas.sc.gg_bot.replay_parser.service.ReplayParserService;
import aic.gas.sc.gg_bot.replay_parser.service.WatcherMediatorService;
import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

/**
 * Concrete implementation of service to parse replays
 */
@Slf4j
public class ReplayParserServiceImpl extends DefaultBWListener implements ReplayParserService {

  private static final Pattern lineWithMatchPattern = Pattern.compile("^map\\s*=\\s*.+$");
  // paths
  private static final String bwapiIniPath = "c:\\sc\\bwapi-data\\bwapi.ini";
  private static final String starcraftPath = "c:\\sc";
  private static final String chaosluncherPath = starcraftPath + "\\Chaoslauncher.lnk";
  // files
  private static final File bwapiIni = new File(bwapiIniPath);
  private static final File starcraftFolder = new File(starcraftPath);
  private final WatcherMediatorService watcherMediatorService = WatcherMediatorServiceImpl
          .getInstance();
  private ReplayLoaderService replayLoaderService;
  //  Alternatively use this loader:
  //   private ReplayLoaderService replayLoaderService = new FileReplayLoaderServiceImpl();
  private Optional<Replay> replay;
  private Set<Player> players;
  private AgentUnitHandler agentUnitHandler;
  private final String externalDrive = "e:\\";
  private boolean shouldExit = false;

  public ReplayParserServiceImpl(ReplayLoaderService replayLoader) {
    replayLoaderService = replayLoader;
  }

  /**
   * Method to start Chaosluncher with predefined configuration. Sadly process can be only started,
   * not closed. See comment in method body to setup it appropriately
   */
  private void startGame() throws IOException {
    //  You might need to run this to be able to run linux cmd from wine:
    //  https:// stackoverflow.com/a/45545068/1233675
    //
    //  k='HKLM\System\CurrentControlSet\Control\Session Manager\Environment'
    //  pathext_orig=$( wine reg query "$k" /v PATHEXT | tr -d '\r' | awk '/^  /{ print $3 }' )
    //  echo "$pathext_orig" | grep -qE '(^|;)\.(;|$)' || wine reg add "$k" /v PATHEXT /f /d "${pathext_orig};."
    log.info("Starting game...");
    Runtime rt = Runtime.getRuntime();
    rt.exec("cmd /c start \"\" \"" + chaosluncherPath + "\"");
  }

  /**
   * Return next replay to parse
   */
  private Replay getNextReplay() throws Exception {
    File nextReplay = replayLoaderService.returnNextReplayToPlay();

    File externalFile = new File(externalDrive + nextReplay.getPath()
            .replace("c:\\sc\\maps\\replays\\data", "")
    );

    // copy from external storage
    FileUtils.copyFile(externalFile, nextReplay);
    log.info("Copied "+externalFile+" to "+nextReplay);

    return new Replay(nextReplay);
  }

  /**
   * Set next replay or terminate listener
   */
  private void setNextReplay() {
    try {
      log.info("setNextReplay");
      replay = Optional.of(getNextReplay());

      log.info("setupReplayInConfigurationFile");
      setupReplayInConfigurationFile(replay.get().getRawFile());

    } catch (Exception e) {
      e.printStackTrace();

      // terminate process
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
    log.info("setupReplayInConfigurationFile.pathToReplayRelativeToSCFolder "
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

    log.info("setupReplayInConfigurationFile.writing to ini file");
    try (FileOutputStream out = new FileOutputStream(bwapiIni)) {
      java.nio.channels.FileLock lock = out.getChannel().lock();
      log.info("setupReplayInConfigurationFile.lock received");
      Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
      try {
        writer.write(fileContent);
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        lock.release();
      }
      log.info("setupReplayInConfigurationFile.writing finished");
      writer.close();
    }
  }


  @Override
  public void parseReplays() {
    Thread gameListener = new Thread(new GameListener(), "GameListener");

    //  load all not parsed replays
    log.info("load replays to parse");
    replayLoaderService.loadReplaysToParse();

    setNextReplay();

    try {
      gameListener.start();
      startGame();
    } catch (IOException e) {
      // terminate process
      log.info("Could not start game. " + e.getLocalizedMessage());
      System.exit(1);
    }
  }

  private void log_meta(String key, Object value) {
    System.err.println("aic.gas: " + key + " " + value);
  }

  private class GameListener extends DefaultBWListener implements Runnable {

    private final List<AgentMakingObservations> agentsWithObservations = new ArrayList<>();
    // keep track of units watchers
    private final Map<Integer, UnitWatcher> watchersOfUnits = new HashMap<>();
    private Mirror mirror = new Mirror();
    private Game currentGame;
    private Player parsingPlayer;

    @Override
    public void onStart() {
      try {
        if(shouldExit) {
          log.info("Going to shutdown!");
          Thread.sleep(1000);
          System.exit(0);
        }

        log.info("New game from replay " + replay.get().getFile().get());

        UnitWrapperFactory.clearCache();
        WrapperTypeFactory.clearCache();
        AbstractPositionWrapper.clearCache();

        currentGame = mirror.getGame();
        currentGame.setLocalSpeed(0);

        log_meta("mapFileName", currentGame.mapFileName());
        log_meta("mapName", currentGame.mapName());
        log_meta("mapHash", currentGame.mapHash());
        log_meta("mapWidth", currentGame.mapWidth());
        log_meta("mapHeight", currentGame.mapHeight());
        log_meta("getReplayFrameCount", currentGame.getReplayFrameCount());
        log_meta("getStartLocations", currentGame.getStartLocations().size());

        players = currentGame.getPlayers().stream()
                .filter(player -> !player.isNeutral())
                .peek(player -> log_meta("race[" + player.getID() + "]", player.getRace()))
                .collect(Collectors.toSet());
        int numPlayers = players.size();
        log_meta("numPlayers", numPlayers);

        watchersOfUnits.clear();
        agentUnitHandler = new AgentUnitFactory();

        // Use BWTA to analyze map.
        // This may take a few minutes if the map is processed first time!
        log.info("Analyzing map...");
        BWTA.readMap();
        BWTA.analyze();
        log.info("Map data ready");

        // set map size
        int mapSize = (int) BWTA.getBaseLocations().stream()
                .filter(BaseLocation::isStartLocation)
                .count();
        DecisionConfiguration.setMapSize(MapSizeEnums.getByStartBases(mapSize));

        // set player to parse
        Set<Integer> playersToParse = currentGame.getPlayers().stream()
            .filter(p -> p.getRace().equals(Race.Zerg))
            .filter(p -> p.allUnitCount() == 9)
            .map(Player::getID)
            .collect(Collectors.toSet());
        parsingPlayer = currentGame.getPlayers().stream()
            .filter(p -> playersToParse.contains(p.getID()))
            .findFirst()
            .get();

        // try to setup race
        DecisionConfiguration.setupRace(parsingPlayer, currentGame.getPlayers());

        WatcherPlayer watcherPlayer = new WatcherPlayer(parsingPlayer, currentGame);
        agentsWithObservations.add(watcherPlayer);
        watcherMediatorService.addWatcher(watcherPlayer);

        // init base agents
        BWTA.getBaseLocations().forEach(baseLocation -> {
          BaseWatcher baseWatcher = new BaseWatcher(ABaseLocationWrapper.wrap(baseLocation),
                  currentGame, new BaseWatcher.UpdateChecksStrategy());
          agentsWithObservations.add(baseWatcher);
          watcherMediatorService.addWatcher(baseWatcher);
        });

        // abstract managers
        watcherMediatorService.addWatcher(new EcoManagerWatcher());
        watcherMediatorService.addWatcher(new BuildOrderManagerWatcher());
        watcherMediatorService.addWatcher(new UnitOrderManagerWatcher());

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onUnitShow(Unit unit) {
      try {
        DecisionConfiguration.setupEnemyRace(parsingPlayer, unit);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onUnitCreate(Unit unit) {
      try {
        if (parsingPlayer.getID() == unit.getPlayer().getID()) {
          Optional<UnitWatcher> unitWatcher = agentUnitHandler
                  .createAgentForUnit(unit, currentGame);
          unitWatcher.ifPresent(watcher -> {
            agentsWithObservations.add(watcher);
            watcherMediatorService.addWatcher(watcher);
            watchersOfUnits.put(unit.getID(), watcher);
          });
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onEnd(boolean b) {
      try {
        log.info("Game has finished. Processing data...");

        log_meta("numberOfStayedPlayers", players.size());
        //  todo: add test for bots - winner is one that still has buildings
        for (Player player : players) {
          log_meta("stayedPlayer", player.getRace());
        }

        if (players.size() == 1) {
          log_meta("winningRace", players.iterator().next().getRace());
        } else {
          PlayerWithCounts bestPlayer = players.stream().map(
                  player -> {
                    PlayerWithCounts playerWithCounts = new PlayerWithCounts(
                            player,
                            player.getUnits().stream()
                                    .filter(unit -> unit.getType().isBuilding())
                                    .count());
                    log_meta("playerNumBuildings[" + playerWithCounts.getPlayer().getID() + "] ",
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

          log_meta("bestPlayerNumBuildings", bestPlayer.counts);
          log_meta("winningRace", bestPlayer.getPlayer().getRace());
        }

        // save trajectories and reset register
        watcherMediatorService.clearAllAgentsAndSaveTheirTrajectories();

        replayLoaderService.finishedProcessing(replay.get().getRawFile());
        //setNextReplay();

        System.out.println("Data processed.");
        shouldExit = true;

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onFrame() {
      try {
        super.onFrame();
        if (currentGame.getFrameCount() % 200 == 0) {
          log.info(currentGame.getFrameCount() + " / " + currentGame.getReplayFrameCount());
        }

        try {
          //make observations
          agentsWithObservations.forEach(AgentMakingObservations::makeObservation);

          //watch agents, update their additional beliefs and track theirs commitment
          watcherMediatorService.tellAgentsToObserveSystemAndHandlePlans();

        } catch (Exception e) {
          e.printStackTrace();
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onUnitDestroy(Unit unit) {
      try {
        if (parsingPlayer.getID() == unit.getPlayer().getID()) {
          Optional<UnitWatcher> watcher = Optional
                  .ofNullable(watchersOfUnits.remove(unit.getID()));
          watcher.ifPresent(unitWatcher -> watcherMediatorService.removeWatcher(unitWatcher));
        }
        UnitWrapperFactory.unitDied(unit);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onUnitMorph(Unit unit) {
      try {
        if (parsingPlayer.getID() == unit.getPlayer().getID()) {
          Optional<UnitWatcher> watcher = Optional
                  .ofNullable(watchersOfUnits.remove(unit.getID()));
          watcher.ifPresent(unitWatcher -> watcherMediatorService.removeWatcher(unitWatcher));
          onUnitCreate(unit);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onPlayerLeft(Player player) {
      try {

        super.onPlayerLeft(player);

        log.info(player.getRace() + " id: " + player.getID() + " left the game.");
        players.remove(player);
      } catch (Exception e) {
        e.printStackTrace();
      }
      if (currentGame.getFrameCount() % 200 == 0) {
        log.info(currentGame.getFrameCount() + " / " + currentGame.getReplayFrameCount());
      }
    }

    @Override
    public void onPlayerDropped(Player player) {
      try {
        super.onPlayerDropped(player);

        log.info(player.getRace() + " id: " + player.getID() + " dropped the game.");
        players.remove(player);
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
