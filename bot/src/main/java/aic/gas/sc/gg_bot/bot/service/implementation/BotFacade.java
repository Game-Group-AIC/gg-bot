package aic.gas.sc.gg_bot.bot.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.DecisionConfiguration;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.MapSizeEnums;
import aic.gas.sc.gg_bot.abstract_bot.model.game.util.Annotator;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.UnitWrapperFactory;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.WrapperTypeFactory;
import aic.gas.sc.gg_bot.bot.model.agent.AbstractAgent;
import aic.gas.sc.gg_bot.bot.model.agent.AgentPlayer;
import aic.gas.sc.gg_bot.bot.model.agent.AgentUnit;
import aic.gas.sc.gg_bot.bot.service.IAgentUnitHandler;
import aic.gas.sc.gg_bot.bot.service.ILocationInitializer;
import aic.gas.sc.gg_bot.bot.service.IPlayerInitializer;
import aic.gas.sc.gg_bot.bot.service.IResourceManager;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import aic.gas.sc.gg_bot.mas.service.MASFacade;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Facade for bot.
 */
@Getter
@Slf4j
public class BotFacade extends DefaultBWListener {

  //TODO !!!THIS IS HACK DO NOT USE INSIDE OTHER COMMAND INTERACTING WITH GAME!!!
  //class to handle additional commands with observations requests
  public static AdditionalCommandToObserveGameProcessor ADDITIONAL_OBSERVATIONS_PROCESSOR;

  public static final IResourceManager RESOURCE_MANAGER = new ResourceManager();

  //TODO increase + block frame for a while
  private final long maxFrameExecutionTime;
  private final boolean annotateMap;
  private final boolean drawDebug;

  private long time, execution;

  //TODO hack to prevent building same types
  private final BuildLockerService buildLockerService = BuildLockerService.getInstance();
  private static final int frameCountToRestart = 150;

  //TODO hack to restart idle workers
  private final Map<WorkerTuple, Integer> idleWorkers = new HashMap<>();

  //keep track of agent units
  private final Map<Integer, AgentUnit> agentsWithGameRepresentation = new HashMap<>();
  //executor of game commands
  private GameCommandExecutor gameCommandExecutor;
  //facade for MAS
  private MASFacade masFacade = new MASFacade(() -> gameCommandExecutor.getCountOfPassedFrames(),
      true);
  //this is created with new game
  private IAgentUnitHandler agentUnitFactory;
  private IPlayerInitializer playerInitializer;
  private ILocationInitializer locationInitializer;
  private final List<AbstractAgent> abstractAgents;

  //game related fields
  private Mirror mirror = new Mirror();

  @Getter
  private Game game;

  @Getter
  private Player self;

  private Annotator annotator;

  public BotFacade(long maxFrameExecutionTime, boolean annotateMap, boolean drawDebug) {
    long start = System.currentTimeMillis();

    this.maxFrameExecutionTime = maxFrameExecutionTime;
    this.annotateMap = annotateMap;
    this.drawDebug = drawDebug;

    //load decision points
    DecisionLoadingServiceImpl.getInstance();

    //init factories
    playerInitializer = new PlayerInitializer();
    locationInitializer = new LocationInitializer();
    agentUnitFactory = new AgentUnitHandler();

    //create abstract agents
    abstractAgents = (new AbstractAgentsInitializer()).initializeAbstractAgents(this);

    log.info("Facade ready. It took " + (System.currentTimeMillis() - start));
  }

  /**
   * Initialize agents out of start method - concurrently
   */
  @AllArgsConstructor
  private class InitializationThread implements Runnable {

    private final APlayer playerToInitAsAgent;
    private final List<ABaseLocationWrapper> baseLocations;
    private final BotFacade botFacade;
    private final ARace enemyRace;

    @Override
    public void run() {
      long start = System.currentTimeMillis();
      log.info("Agent initialization has started.");

      //init player as another agent
      AgentPlayer agentPlayer = playerInitializer
          .createAgentForPlayer(playerToInitAsAgent, botFacade, enemyRace);
      masFacade.addAgentToSystem(agentPlayer);

      //init base locations as agents
      baseLocations.stream()
          .map(location -> locationInitializer.createAgent(location, botFacade))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .forEach(agentBaseLocation -> masFacade.addAgentToSystem(agentBaseLocation));

      //run abstract agents
      abstractAgents.forEach(abstractAgent -> masFacade.addAgentToSystem(abstractAgent));

      log.info("Agent initialization has ended. It took " + (System.currentTimeMillis() - start));
    }
  }

  @Override
  public void onStart() {
    try {
      long start = System.currentTimeMillis();
      log.info("Game initialization.");

      //initialize game related data
      game = mirror.getGame();
      self = game.self();

      //initialize command executor
      gameCommandExecutor = new GameCommandExecutor(game);
      ADDITIONAL_OBSERVATIONS_PROCESSOR = new AdditionalCommandToObserveGameProcessor(
          gameCommandExecutor);

      //Use BWTA to analyze map
      //This may take a few minutes if the map is processed first time!
      log.info("Analyzing map");
      BWTA.readMap();
      BWTA.analyze();
      log.info("Map data ready");

      //get player
      Optional<APlayer> me = APlayer
          .wrapPlayer(self, gameCommandExecutor.getCountOfPassedFrames());
      if (!me.isPresent()) {
        log.error("Could not initiate player.");
        throw new RuntimeException("Could not initiate player.");
      }

      //get base locations
      List<ABaseLocationWrapper> baseLocations = BWTA.getBaseLocations().stream()
          .map(ABaseLocationWrapper::wrap).collect(Collectors.toList());

      log.info(
          "Data for agent initialization ready. It took " + (System.currentTimeMillis() - start));

      //start parallel thread
      Thread thread = new Thread(new InitializationThread(me.get(), baseLocations, this,
          ARace.getRace(game.enemy().getRace())));
      thread.start();

      log.info("Setting up data for decisions.");

      //set map size
      int mapSize = (int) baseLocations.stream()
          .filter(ABaseLocationWrapper::isStartLocation)
          .count();
      DecisionConfiguration.setMapSize(MapSizeEnums.getByStartBases(mapSize));

      //try to setup race
      DecisionConfiguration.setupRace(self, game.getPlayers());

      //init annotation
      if (annotateMap) {
        annotator = new Annotator(game.getPlayers().stream()
            .filter(player -> player.isEnemy(self) || player.getID() == self.getID())
            .collect(Collectors.toList()), self, game);
      }

      log.info("System ready. It took " + (System.currentTimeMillis() - start));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onUnitCreate(Unit unit) {
    try {
      if (self.getID() == unit.getPlayer().getID()) {
        Optional<AgentUnit> agent = agentUnitFactory
            .createAgentForUnit(unit, this, game.getFrameCount());
        log.info("Creating " + agent.map(agentUnit -> agentUnit.getAgentType().getName())
            .orElse("null"));
        agent.ifPresent(agentObservingGame -> {
          agentsWithGameRepresentation.put(unit.getID(), agentObservingGame);
          masFacade.addAgentToSystem(agentObservingGame);
        });
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onUnitDestroy(Unit unit) {
    try {
      if (self.getID() == unit.getPlayer().getID()) {
        Optional<AgentUnit> agent = Optional
            .ofNullable(agentsWithGameRepresentation.remove(unit.getID()));
        log.info("Destroying " + agent.map(agentUnit -> agentUnit.getAgentType().getName())
            .orElse("null"));
        agent.ifPresent(
            agentUnit -> masFacade.removeAgentFromSystem(agentUnit, unit.getType().isBuilding()));
      }
      UnitWrapperFactory.unitDied(unit);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onUnitMorph(Unit unit) {
    try {
      if (self.getID() == unit.getPlayer().getID()) {
        Optional<AgentUnit> agent = Optional
            .ofNullable(agentsWithGameRepresentation.remove(unit.getID()));
        agent.ifPresent(agentUnit -> masFacade.removeAgentFromSystem(agentUnit, true));

        log.info("Morphing from " + agent.map(agentUnit -> agentUnit.getAgentType().getName())
            .orElse("null")
            + " to " + unit.getType().toString());

        //put it under lock
        buildLockerService.lock(WrapperTypeFactory.createFrom(unit.getType()));

        onUnitCreate(unit);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void run() throws IOException, InterruptedException {
    mirror.getModule().setEventListener(this);
    DecisionLoadingServiceImpl.getInstance();
    mirror.startGame();
  }

  @Override
  public void onUnitShow(Unit unit) {
    DecisionConfiguration.setupEnemyRace(self, unit);
  }

  @Override
  public void onEnd(boolean b) {
    try {
      game.printf("http://gas.fel.cvut.cz");
      agentsWithGameRepresentation.clear();
      masFacade.terminate();
    } catch (Exception e) {
      e.printStackTrace();
    }
    idleWorkers.clear();
  }

  @Override
  public void onFrame() {
    time = System.currentTimeMillis();

    try {
      gameCommandExecutor.actOnFrame(maxFrameExecutionTime);
      buildLockerService.releaseLocksOnTypes();
    }
    // === Catch any exception that occur not to "kill" the bot with one trivial error ===================
    catch (Exception e) {
      e.printStackTrace();
    }

    //annotate map
    if (annotateMap) {
      annotator.annotate();
    }

    //draw debug
    if (drawDebug) {
      drawDebug();
    }

    //manage resources
    RESOURCE_MANAGER.processReservations(self.minerals(), self.gas());

//    //TODO hack to kill idle workers and start as new agents
//    checkWorkers();

    //TODO hack to ensure frame sync
    masFacade.notifyAgentsAboutNextCycle();

    //hold frame for a small amount of time to give MAS time to handle new data
    {
      if ((execution = System.currentTimeMillis() - time) < maxFrameExecutionTime) {
        try {
          wait(maxFrameExecutionTime - execution);
        } catch (InterruptedException e) {
          log.error(e.getMessage());
        }
      }
    }

    if ((execution = System.currentTimeMillis() - time) >= 75) {
      game.printf("On frame " + game.getFrameCount() + " execution took " + execution + " ms.");
    }
  }

  private void drawDebug() {

    //learnt desires
    Map<DesireKeyID, Boolean> commitmentToLearntDesires = masFacade
        .returnCommitmentToDesires(DesireKeys.LEARNT_DESIRE_KEYS);
    String message = DesireKeys.LEARNT_DESIRE_KEYS.stream()
        .map(desireKeyID -> desireKeyID.getName() + ": " + Optional
            .ofNullable(commitmentToLearntDesires.get(desireKeyID)).map(aBoolean ->
                aBoolean ? "1" : "0").orElse("N/A"))
        .collect(Collectors.joining("\n"));
    Annotator.printMessage(message, 10, 10, game);

    //buildings
    Map<DesireKeyID, Boolean> commitmentToBuildDesires = masFacade
        .returnCommitmentToDesires(DesireKeys.BUILDING_DESIRE_KEYS);
    message = DesireKeys.BUILDING_DESIRE_KEYS.stream()
        .map(desireKeyID -> desireKeyID.getName() + ": " + Optional
            .ofNullable(commitmentToBuildDesires.get(desireKeyID)).map(aBoolean ->
                aBoolean ? "1" : "0").orElse("N/A"))
        .collect(Collectors.joining("\n"));
    Annotator.printMessage(message, 200, 10, game);
  }

  //TODO hack to restart idle workers
  private void checkWorkers() {
    Set<WorkerTuple> idleWorkersSet = self.getUnits().stream()
        .filter(unit -> unit.getType().isWorker())
        .filter(Unit::isIdle)
        .map(WorkerTuple::new)
        .collect(Collectors.toSet());

    //remove missing
    idleWorkers.keySet().removeIf(v -> !idleWorkersSet.contains(v));

    //add new
    idleWorkersSet.stream()
        .filter(workerTuple -> !idleWorkers.containsKey(workerTuple))
        .forEach(workerTuple -> idleWorkers.put(workerTuple, 0));

    //restart stucked and update the map
    Iterator<Entry<WorkerTuple, Integer>> it = idleWorkers.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<WorkerTuple, Integer> pair = it.next();
      if (pair.getValue() + 1 >= frameCountToRestart) {
        it.remove();

        //restart
        Optional<AgentUnit> agent = Optional
            .ofNullable(agentsWithGameRepresentation.remove(pair.getKey().id));
        log.info("Destroying stucked worker: " + agent
            .map(agentUnit -> agentUnit.getAgentType().getName())
            .orElse("null"));
        agent.ifPresent(agentObservingGame -> masFacade
            .removeAgentFromSystem(agentObservingGame, pair.getKey().unit.getType().isBuilding()));
        onUnitCreate(pair.getKey().unit);
      } else {

        //update clock
        pair.setValue(pair.getValue() + 1);
      }
    }
  }

  //TODO handle more events - unit renegade, visibility

  @Getter
  @EqualsAndHashCode(of = "id")
  private static class WorkerTuple {

    private final int id;
    private final Unit unit;

    private WorkerTuple(Unit unit) {
      this.unit = unit;
      this.id = unit.getID();
    }
  }

  //tODO remove
  public boolean buildPool(Unit builder) {
    int maxDist = 3;
    int stopDist = 40;

    while (maxDist < stopDist) {
      for (int i = builder.getTilePosition().getX() - maxDist;
          i <= builder.getTilePosition().getX() + maxDist; i++) {
        for (int j = builder.getTilePosition().getY() - maxDist;
            j <= builder.getTilePosition().getY() + maxDist; j++) {
          if (game
              .canBuildHere(new TilePosition(i, j), UnitType.Zerg_Spawning_Pool, builder, false)) {
            // units that are blocking the tile
            boolean unitsInWay = false;
            for (Unit u : game.getAllUnits()) {
              if (u.getID() == builder.getID()) {
                continue;
              }
              if ((Math.abs(u.getTilePosition().getX() - i) < 4) && (
                  Math.abs(u.getTilePosition().getY() - j) < 4)) {
                unitsInWay = true;
              }
            }
            if (!unitsInWay) {
              builder.build(UnitType.Zerg_Spawning_Pool, new TilePosition(i, j));
              log.info("Building POOL.");
              return true;
            }
          }
        }
      }
      maxDist += 2;
    }
    return false;
  }
}
