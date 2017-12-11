package aic.gas.sc.gg_bot.service.implementation;

import aic.gas.abstract_bot.model.game.util.Annotator;
import aic.gas.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.abstract_bot.model.game.wrappers.AbstractPositionWrapper;
import aic.gas.abstract_bot.model.game.wrappers.UnitWrapperFactory;
import aic.gas.abstract_bot.model.game.wrappers.WrapperTypeFactory;
import aic.gas.mas.service.MASFacade;
import aic.gas.mas.utils.MyLogger;
import aic.gas.sc.gg_bot.model.agent.AgentPlayer;
import aic.gas.sc.gg_bot.model.agent.AgentUnit;
import aic.gas.sc.gg_bot.service.AbstractAgentsInitializer;
import aic.gas.sc.gg_bot.service.AgentUnitHandler;
import aic.gas.sc.gg_bot.service.LocationInitializer;
import aic.gas.sc.gg_bot.service.PlayerInitializer;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.Unit;
import bwta.BWTA;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

/**
 * Facade for bot.
 */
@Getter
public class BotFacade extends DefaultBWListener {

  //TODO !!!THIS IS HACK DO NOT USE INSIDE OTHER COMMAND INTERACTING WITH GAME!!!
  //class to handle additional commands with observations requests
  public static AdditionalCommandToObserveGameProcessor ADDITIONAL_OBSERVATIONS_PROCESSOR;

  @Setter
  @Getter
  private static int gameDefaultSpeed = 0;

  @Setter
  @Getter
  private static long maxFrameExecutionTime = 20;

  @Setter
  @Getter
  private static boolean annotateMap = false;

  //keep track of agent units
  private final Map<Integer, AgentUnit> agentsWithGameRepresentation = new HashMap<>();
  //fields provided by user
  private final AgentUnitFactoryCreationStrategy agentUnitFactoryCreationStrategy;
  private final PlayerInitializerCreationStrategy playerInitializerCreationStrategy;
  private final LocationInitializerCreationStrategy locationInitializerCreationStrategy;
  //to init abstract agents at the beginning of each game
  private final AbstractAgentsInitializer abstractAgentsInitializer = new AbstractAgentsInitializerImpl();
  //facade for MAS
  private MASFacade masFacade;
  //executor of game commands
  private GameCommandExecutor gameCommandExecutor;
  //this is created with new game
  private AgentUnitHandler agentUnitFactory;
  private PlayerInitializer playerInitializer;
  private LocationInitializer locationInitializer;

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
    MyLogger.setLoggingLevel(Level.WARNING);
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
      masFacade = new MASFacade(() -> gameCommandExecutor.getCountOfPassedFrames());
      ADDITIONAL_OBSERVATIONS_PROCESSOR = new AdditionalCommandToObserveGameProcessor(
          gameCommandExecutor);
      playerInitializer = playerInitializerCreationStrategy.createFactory();
      agentUnitFactory = agentUnitFactoryCreationStrategy.createFactory();
      locationInitializer = locationInitializerCreationStrategy.createFactory();

      //Use BWTA to analyze map
      //This may take a few minutes if the map is processed first time!
      MyLogger.getLogger().info("Analyzing map");
      BWTA.readMap();
      BWTA.analyze();

      MyLogger.getLogger().info("Map data ready");

      //init annotation
      annotator = new Annotator(game.getPlayers().stream()
          .filter(player -> player.isEnemy(self) || player.getID() == self.getID())
          .collect(Collectors.toList()), self, game);

      //init player as another agent
      Optional<APlayer> player = APlayer.wrapPlayer(self);
      if (!player.isPresent()) {
        MyLogger.getLogger().warning("Could not initiate player.");
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
      game.setLocalSpeed(getGameDefaultSpeed());

      MyLogger.getLogger().info("Local game speed set to " + getGameDefaultSpeed());

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
        onUnitCreate(unit);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void run() throws IOException, InterruptedException {
    mirror.getModule().setEventListener(this);
    mirror.startGame();
  }

  @Override
  public void onEnd(boolean b) {
    try {
      agentsWithGameRepresentation.clear();
      masFacade.terminate();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onFrame() {
    try {
      gameCommandExecutor.actOnFrame();
    }
    // === Catch any exception that occur not to "kill" the bot with one trivial error ===================
    catch (Exception e) {
      e.printStackTrace();
    }

    //annotate map
    if (annotateMap) {
      annotator.annotate();
    }
  }

  //TODO handle more events - unit renegade, visibility

  /**
   * Contract for strategy to create new AgentUnitHandlerImpl for new game
   */
  public interface AgentUnitFactoryCreationStrategy {

    /**
     * Creates new factory
     */
    AgentUnitHandler createFactory();
  }

  /**
   * Contract for strategy to create new LocationInitializer for new game
   */
  public interface LocationInitializerCreationStrategy {

    /**
     * Creates new factory
     */
    LocationInitializer createFactory();
  }

  /**
   * Contract for strategy to create new PlayerInitializer for new game
   */
  public interface PlayerInitializerCreationStrategy {

    /**
     * Creates new factory
     */
    PlayerInitializer createFactory();
  }
}
