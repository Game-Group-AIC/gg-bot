package aic.gas.sc.gg_bot.bot.service.implementation;

import aic.gas.sc.gg_bot.mas.model.QueuedItemInterfaceWithResponse;
import aic.gas.sc.gg_bot.mas.model.ResponseReceiverInterface;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentType;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import aic.gas.sc.gg_bot.mas.model.planing.command.ObservingCommand;
import aic.gas.sc.gg_bot.mas.service.CommandManager;
import aic.gas.sc.gg_bot.mas.service.ObservingCommandManager;
import bwapi.Game;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service let queued instances of commands be executed on frame. Only one action is executed at
 * time. Service watch for execution time to manage executions of actions based on priority in queue
 * and remaining time - it make maximum to make sure that limit is not overstep.
 */
public class GameCommandExecutor implements CommandManager<ActCommand<?>>,
    ObservingCommandManager<Game, ObservingCommand<Game>> {

  //FIFO
  private final List<QueuedItemInterfaceWithResponseWithCommandClassGetter> queuedItems = new ArrayList<>();
  private final Game game;
  //structures to keep durations of command execution
  private final Map<AgentType, Map<Class, Long>> lastDurationOfCommandTypeExecutionForAgentType = new HashMap<>();
  //counter of frames
  private int countOfPassedFrames = 0;

  GameCommandExecutor(Game game) {
    this.game = game;
  }

  //Not synchronized. Speed is primal concern not accuracy
  int getCountOfPassedFrames() {
    return countOfPassedFrames;
  }

  /**
   * Add request to internal queue
   */
  private boolean addToQueue(QueuedItemInterfaceWithResponseWithCommandClassGetter request) {
    synchronized (queuedItems) {
      return queuedItems.add(request);
    }
  }

  /**
   * Method to add item to queue with code to execute action in GAME
   */
  public boolean addCommandToObserve(ObservingCommand<Game> command,
      WorkingMemory memory,
      ResponseReceiverInterface<Boolean> responseReceiver,
      AgentType agentType) {
    return addToQueue(new QueuedItemInterfaceWithResponseWithCommandClassGetter() {
      @Override
      Class getClassOfCommand() {
        return command.getClass();
      }

      @Override
      AgentType getAgentType() {
        return agentType;
      }

      @Override
      public Boolean executeCode() {
        return executeCommand(command, memory, game);
      }

      @Override
      public ResponseReceiverInterface<Boolean> getReceiverOfResponse() {
        return responseReceiver;
      }
    });
  }

  /**
   * Method to add item to queue with code to execute action in GAME
   */
  public boolean addCommandToAct(ActCommand<?> command,
      WorkingMemory memory,
      ResponseReceiverInterface<Boolean> responseReceiver,
      AgentType agentType) {
    return addToQueue(new QueuedItemInterfaceWithResponseWithCommandClassGetter() {
      @Override
      Class getClassOfCommand() {
        return command.getClass();
      }

      @Override
      AgentType getAgentType() {
        return agentType;
      }

      @Override
      public Boolean executeCode() {
        return executeCommand(command, memory);
      }

      @Override
      public ResponseReceiverInterface<Boolean> getReceiverOfResponse() {
        return responseReceiver;
      }
    });
  }

  /**
   * Method to be called on each frame. It executes commands in queue based on their priority. First
   * command in queue is executed always - to make sure that commands do not stuck in queue
   */
  void actOnFrame() {
    long currentTime = System.currentTimeMillis(), end =
        currentTime + BotFacade.getMaxFrameExecutionTime(), start = currentTime;
    int startIndex = 0;
    executeCommand();
    currentTime = System.currentTimeMillis();
    while (end > currentTime) {
      startIndex = executeCommand(end - currentTime, startIndex);
      currentTime = System.currentTimeMillis();
    }

    //this is not vital to have it synchronized. primary concern is speed
    this.countOfPassedFrames = game.getFrameCount();

//        long tameItTook = (System.currentTimeMillis() - start);
//        log.info("Frame commands " + countOfPassedFrames + " executed in " + tameItTook + " ms");
//        game.printf("Frame commands " + countOfPassedFrames + " executed in " + tameItTook + " ms");
  }

  /**
   * Execute first command
   */
  private void executeCommand() {
    if (!queuedItems.isEmpty()) {
      QueuedItemInterfaceWithResponseWithCommandClassGetter queuedItem;
      synchronized (queuedItems) {
        queuedItem = queuedItems.remove(0);
      }
      long timeItTook = executeCommand(queuedItem);
      lastDurationOfCommandTypeExecutionForAgentType.computeIfAbsent(queuedItem.getAgentType(),
          at -> new HashMap<>()).put(queuedItem.getClassOfCommand(), timeItTook);
    }
  }

  private long executeCommand(QueuedItemInterfaceWithResponseWithCommandClassGetter queuedItem) {
    long start = System.currentTimeMillis();
    queuedItem.executeItem();
    return System.currentTimeMillis() - start;
  }

  /**
   * Find first queued command which was executed last time in less time than is remaining time
   */
  private int executeCommand(long remainingTime, int startIndex) {
    long end = System.currentTimeMillis() + remainingTime;
    for (int i = startIndex; i < queuedItems.size(); i++) {
      if (System.currentTimeMillis() >= end) {
        break;
      }
      QueuedItemInterfaceWithResponseWithCommandClassGetter queuedItem;
      synchronized (queuedItems) {
        queuedItem = queuedItems.get(i);
      }
      long executionTime = lastDurationOfCommandTypeExecutionForAgentType
          .getOrDefault(queuedItem.getAgentType(),
              new HashMap<>())
          .getOrDefault(queuedItem.getClassOfCommand(), BotFacade.getMaxFrameExecutionTime());
      if (executionTime < end - System.currentTimeMillis()) {
        synchronized (queuedItems) {
          queuedItem = queuedItems.remove(i);
        }
        executionTime = executeCommand(queuedItem);
        lastDurationOfCommandTypeExecutionForAgentType.computeIfAbsent(queuedItem.getAgentType(),
            at -> new HashMap<>()).put(queuedItem.getClassOfCommand(), executionTime);
        return i;
      }
    }
    return 0;
  }

  /**
   * Extension of item in queue
   */
  private static abstract class QueuedItemInterfaceWithResponseWithCommandClassGetter implements
      QueuedItemInterfaceWithResponse<Boolean> {

    /**
     * Get class of command
     */
    abstract Class getClassOfCommand();

    /**
     * Get agent type
     */
    abstract AgentType getAgentType();
  }


}
