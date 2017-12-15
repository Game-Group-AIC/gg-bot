package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.DecisionConfiguration;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.MapSizeEnums;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AbstractPositionWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.UnitWrapperFactory;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.WrapperTypeFactory;
import aic.gas.sc.gg_bot.replay_parser.model.AgentMakingObservations;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.Replay;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension.BaseWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension.BuildOrderManagerWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension.EcoManagerWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension.UnitOrderManagerWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension.UnitWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension.WatcherPlayer;
import aic.gas.sc.gg_bot.replay_parser.service.AgentUnitHandler;
import aic.gas.sc.gg_bot.replay_parser.service.ReplayLoaderService;
import aic.gas.sc.gg_bot.replay_parser.service.ReplayParserService;
import aic.gas.sc.gg_bot.replay_parser.service.StorageService;
import aic.gas.sc.gg_bot.replay_parser.service.WatcherMediatorService;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.Race;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Concrete implementation of service to parse replays
 */
@Slf4j
public class ReplayParserServiceImpl extends DefaultBWListener implements ReplayParserService {

  private static final String chaosluncherPath = "c:\\Users\\Jan\\Desktop\\Chaosluncher Run With Full Privileges.lnk";
  private static final StorageService storageService = StorageServiceImp.getInstance();
  //  private ReplayLoaderService replayLoaderService = new RabbitMQReplayLoaderServiceImpl(
//      System.getenv("RABBITMQ_BROKER_HOST"),
//      System.getenv("RABBITMQ_DEFAULT_USER"),
//      System.getenv("RABBITMQ_DEFAULT_PASS"),
//      Integer.parseInt(System.getenv("RABBITMQ_BROKER_PORT"))
//  );
  // Alternatively use this loader:
  private ReplayLoaderService replayLoaderService = new FileReplayLoaderServiceImpl();
  private WatcherMediatorService watcherMediatorService = WatcherMediatorServiceImpl.getInstance();
  private Optional<Replay> replay;

  /**
   * Method to start Chaosluncher with predefined configuration. Sadly process can be only started,
   * not closed. See comment in method body to setup it appropriately
   */
  private void startChaosluncher() throws IOException {
    /*
     * To fully automate process of lunching Starcraft:
     * Lunch Chaosluncher with full privileges, guide to setup such a shortucut is on
     * http://lifehacker.com/how-to-eliminate-uac-prompts-for-specific-applications-493128966.
     *
     * Also do not forget to set: Setting > Run Starcraft on Startup to initialize game based on configuration file.
     */
    log.info("Starting Chaosluncher...");
    Runtime rt = Runtime.getRuntime();
    rt.exec("cmd /c start \"\" \"" + chaosluncherPath + "\"");
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
      replay = Optional.of(getNextReplay());
    } catch (Exception e) {
      log.error(e.getLocalizedMessage());

      //terminate process
      System.exit(1);
    }
  }

  @Override
  public void parseReplays() {

    //start game listener
    Thread gameListener = new Thread(new GameListener(), "GameListener");
    gameListener.start();

    //load all not parsed replays
    replayLoaderService.loadReplaysToParse();
    setNextReplay();

//        //try to lunch chaosluncher
//        try {
//            startChaosluncher();
//        } catch (IOException e) {
//
//            //terminate process
//            log.error("Could not start Chaosluncher. " + e.getLocalizedMessage());
//            System.exit(1);
//        }
  }

  private class GameListener extends DefaultBWListener implements Runnable {

    private final List<AgentMakingObservations> agentsWithObservations = new ArrayList<>();
    //keep track of units watchers
    private final Map<Integer, UnitWatcher> watchersOfUnits = new HashMap<>();
    private Mirror mirror = new Mirror();
    private Game currentGame;
    private Player parsingPlayer;
    private AgentUnitHandler agentUnitHandler = null;
    private boolean track = false, shouldExit = false;

    @Override
    public void onStart() {
      UnitWrapperFactory.clearCache();
      WrapperTypeFactory.clearCache();
      AbstractPositionWrapper.clearCache();

      track = false;
      log.info("New game from replay " + replay.get().getFile());
      currentGame = mirror.getGame();

      //speed up game to maximal possible, disable gui
      currentGame.setLocalSpeed(0);
//            currentGame.setGUI(false);

      //kill no matter what to prevent crash (and rerun by outside script :))
      if (shouldExit) {
        System.exit(0);
      }

      if (!replay.get().isParsedMoreTimes()) {
        track = true;
        watchersOfUnits.clear();

        //init types
        if (agentUnitHandler == null) {
          agentUnitHandler = new AgentUnitFactory();
        }

        //mark replay as loaded to skip it next time
        storageService.markReplayAsParsed(replay.get());

        try {

          //Use BWTA to analyze map
          //This may take a few minutes if the map is processed first time!
          log.info("Analyzing map...");
          BWTA.readMap();
          BWTA.analyze();
          log.info("Map data ready");

          //set map size
          int mapSize = (int) BWTA.getBaseLocations().stream()
              .filter(BaseLocation::isStartLocation)
              .count();
          DecisionConfiguration.setMapSize(MapSizeEnums.getByStartBases(mapSize));

          //set player to parse
          Set<Integer> playersToParse = currentGame.getPlayers().stream()
              .filter(p -> p.getRace().equals(Race.Zerg))
              .filter(p -> p.allUnitCount() == 9)
              .peek(player -> log.info(
                  player.getRace() + " id: " + player.getID() + " units: " + player.allUnitCount()))
              .map(Player::getID)
              .collect(Collectors.toSet());
          parsingPlayer = currentGame.getPlayers().stream()
              .filter(p -> playersToParse.contains(p.getID()))
              .findFirst()
              .get();

          //try to setup race
          DecisionConfiguration.setupRace(parsingPlayer, currentGame.getPlayers());

          WatcherPlayer watcherPlayer = new WatcherPlayer(parsingPlayer);
          agentsWithObservations.add(watcherPlayer);
          watcherMediatorService.addWatcher(watcherPlayer);

          //init base agents
          BWTA.getBaseLocations().forEach(baseLocation -> {
            BaseWatcher baseWatcher = new BaseWatcher(ABaseLocationWrapper.wrap(baseLocation),
                currentGame, new BaseWatcher.UpdateChecksStrategy());
            agentsWithObservations.add(baseWatcher);
            watcherMediatorService.addWatcher(baseWatcher);
          });

          //abstract managers
          watcherMediatorService.addWatcher(new EcoManagerWatcher());
          watcherMediatorService.addWatcher(new BuildOrderManagerWatcher());
          watcherMediatorService.addWatcher(new UnitOrderManagerWatcher());

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    @Override
    public void onUnitShow(Unit unit) {
      DecisionConfiguration.setupEnemyRace(parsingPlayer, unit);
    }

    @Override
    public void onUnitCreate(Unit unit) {
      if (track && parsingPlayer != null) {
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
    }


    @Override
    public void onEnd(boolean b) {
      try {
        log.info("Game has finished. Processing data...");

        //save trajectories and reset register
        watcherMediatorService.clearAllAgentsAndSaveTheirTrajectories();

        //if all players in queue were parsed, move to next replay
        storageService.markReplayAsParsed(replay.get());
//                setNextReplay();

        //tell app to exit
        shouldExit = true;

        log.info("Data processed.");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onFrame() {
      if (track && parsingPlayer != null) {
        try {

          //make observations
          agentsWithObservations.forEach(AgentMakingObservations::makeObservation);

          //watch agents, update their additional beliefs and track theirs commitment
          watcherMediatorService.tellAgentsToObserveSystemAndHandlePlans();

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      if (currentGame.getFrameCount() % 200 == 0) {
        log.info(currentGame.getFrameCount() + " / " + currentGame.getReplayFrameCount());
      }
    }

    @Override
    public void onUnitDestroy(Unit unit) {
      if (track && parsingPlayer != null) {
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
    }

    @Override
    public void onUnitMorph(Unit unit) {
      if (track && parsingPlayer != null) {
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
    }

    @Override
    public void run() {
      mirror.getModule().setEventListener(this);
      mirror.startGame();
    }
  }

}
