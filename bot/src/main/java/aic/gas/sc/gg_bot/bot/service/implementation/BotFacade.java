package aic.gas.sc.gg_bot.bot.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.DecisionConfiguration;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.MapSizeEnums;
import aic.gas.sc.gg_bot.abstract_bot.model.game.util.Annotator;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AbstractPositionWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.UnitWrapperFactory;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.WrapperTypeFactory;
import aic.gas.sc.gg_bot.bot.model.agent.AgentPlayer;
import aic.gas.sc.gg_bot.bot.model.agent.AgentUnit;
import aic.gas.sc.gg_bot.bot.service.IAbstractAgentsInitializer;
import aic.gas.sc.gg_bot.bot.service.IAgentUnitHandler;
import aic.gas.sc.gg_bot.bot.service.ILocationInitializer;
import aic.gas.sc.gg_bot.bot.service.IPlayerInitializer;
import aic.gas.sc.gg_bot.mas.service.MASFacade;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
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

  @Setter
  @Getter
  private static int gameDefaultSpeed = 15;

  //TODO increase + block frame for a while
  @Setter
  @Getter
  private static long maxFrameExecutionTime = 30;

  @Setter
  @Getter
  private static boolean annotateMap = false;

  private long time, execution;

  //TODO hack to prevent building same types
  private final BuildLockerService buildLockerService = BuildLockerService.getInstance();
  private static final int frameCountToRestart = 600;

  //TODO hack to restart idle workers
  private final Map<WorkerTuple, Integer> idleWorkers = new HashMap<>();

  //keep track of agent units
  private final Map<Integer, AgentUnit> agentsWithGameRepresentation = new HashMap<>();
  //fields provided by user
  private final AgentUnitFactoryCreationStrategy agentUnitFactoryCreationStrategy;
  private final PlayerInitializerCreationStrategy playerInitializerCreationStrategy;
  private final LocationInitializerCreationStrategy locationInitializerCreationStrategy;
  //to init abstract agents at the beginning of each game
  private final IAbstractAgentsInitializer abstractAgentsInitializer = new AbstractAgentsInitializer();
  //executor of game commands
  private GameCommandExecutor gameCommandExecutor;
  //facade for MAS
  private MASFacade masFacade;
  //this is created with new game
  private IAgentUnitHandler agentUnitFactory;
  private IPlayerInitializer playerInitializer;
  private ILocationInitializer locationInitializer;

  //game related fields
  private Mirror mirror = new Mirror();

  @Getter
  private Game game;

  @Getter
  private Player self;

  private Annotator annotator;

  public BotFacade(AgentUnitFactoryCreationStrategy agentUnitFactoryCreationStrategy,
      PlayerInitializerCreationStrategy playerInitializerCreationStrategy,
      LocationInitializerCreationStrategy locationInitializerCreationStrategy) {
    this.agentUnitFactoryCreationStrategy = agentUnitFactoryCreationStrategy;
    this.playerInitializerCreationStrategy = playerInitializerCreationStrategy;
    this.locationInitializerCreationStrategy = locationInitializerCreationStrategy;
    this.masFacade = new MASFacade(() -> gameCommandExecutor.getCountOfPassedFrames(), true);
  }

  @Override
  public void onStart() {
    try {
      UnitWrapperFactory.clearCache();
      WrapperTypeFactory.clearCache();
      AbstractPositionWrapper.clearCache();

      //initialize game related data
      game = mirror.getGame();
      self = game.self();

      //initialize command executor
      gameCommandExecutor = new GameCommandExecutor(game);
      ADDITIONAL_OBSERVATIONS_PROCESSOR = new AdditionalCommandToObserveGameProcessor(
          gameCommandExecutor);
      playerInitializer = playerInitializerCreationStrategy.createFactory();
      agentUnitFactory = agentUnitFactoryCreationStrategy.createFactory();
      locationInitializer = locationInitializerCreationStrategy.createFactory();

      //Use BWTA to analyze map
      //This may take a few minutes if the map is processed first time!
      log.info("Analyzing map");
      BWTA.readMap();
      BWTA.analyze();
      log.info("Map data ready");

      //set map size
      int mapSize = (int) BWTA.getBaseLocations().stream()
          .filter(BaseLocation::isStartLocation)
          .count();
      DecisionConfiguration.setMapSize(MapSizeEnums.getByStartBases(mapSize));

      //try to setup race
      DecisionConfiguration.setupRace(self, game.getPlayers());

      //init annotation
      annotator = new Annotator(game.getPlayers().stream()
          .filter(player -> player.isEnemy(self) || player.getID() == self.getID())
          .collect(Collectors.toList()), self, game);

      //init player as another agent
      Optional<APlayer> player = APlayer
          .wrapPlayer(self, gameCommandExecutor.getCountOfPassedFrames());
      if (!player.isPresent()) {
        log.error("Could not initiate player.");
        throw new RuntimeException("Could not initiate player.");
      }
      AgentPlayer agentPlayer = playerInitializer
          .createAgentForPlayer(player.get(), this, game.enemy().getRace());
      masFacade.addAgentToSystem(agentPlayer);

      //init base location as agents
      BWTA.getBaseLocations().stream()
          .map(location -> locationInitializer.createAgent(location, this))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .forEach(agentBaseLocation -> masFacade.addAgentToSystem(agentBaseLocation));

      //init abstract agents
      abstractAgentsInitializer.initializeAbstractAgents(this)
          .forEach(agentBaseLocation -> masFacade.addAgentToSystem(agentBaseLocation));

      //speed up game to setup value
//      game.setLocalSpeed(getGameDefaultSpeed());

      log.info("Local game speed set to " + getGameDefaultSpeed());

      //load decision points
      DecisionLoadingServiceImpl.getInstance();

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
        agent.ifPresent(agentObservingGame -> masFacade.removeAgentFromSystem(agentObservingGame,
            unit.getType().isBuilding()));
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
        agent.ifPresent(
            agentObservingGame -> masFacade.removeAgentFromSystem(agentObservingGame, true));

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

    //TODO hack to kill idle workers and start as new agents
    checkWorkers();

    //TODO hack to ensure frame sync
    masFacade.notifyAgentsAboutNextCycle();

    if ((execution = System.currentTimeMillis() - time) >= 75) {
      game.printf("On frame " + game.getFrameCount() + " execution took " + execution + " ms.");
    }
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

  /**
   * Contract for strategy to create new AgentUnitHandler for new game
   */
  public interface AgentUnitFactoryCreationStrategy {

    /**
     * Creates new factory
     */
    IAgentUnitHandler createFactory();
  }

  /**
   * Contract for strategy to create new ILocationInitializer for new game
   */
  public interface LocationInitializerCreationStrategy {

    /**
     * Creates new factory
     */
    ILocationInitializer createFactory();
  }

  /**
   * Contract for strategy to create new IPlayerInitializer for new game
   */
  public interface PlayerInitializerCreationStrategy {

    /**
     * Creates new factory
     */
    IPlayerInitializer createFactory();
  }

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
}
