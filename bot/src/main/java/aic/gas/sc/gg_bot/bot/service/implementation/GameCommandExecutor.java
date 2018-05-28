package aic.gas.sc.gg_bot.bot.service.implementation;

import aic.gas.sc.gg_bot.mas.model.QueuedItemInterfaceWithResponse;
import aic.gas.sc.gg_bot.mas.model.ResponseReceiverInterface;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import aic.gas.sc.gg_bot.mas.model.planing.command.ObservingCommand;
import aic.gas.sc.gg_bot.mas.service.CommandManager;
import aic.gas.sc.gg_bot.mas.service.ObservingCommandManager;
import bwapi.Game;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Service let queued instances of commands be executed on frame. Only one action is executed at
 * time. Service watch for execution time to manage executions of actions based on priority in queue
 * and remaining time - it make maximum to make sure that limit is not overstep.
 */
@Slf4j
public class GameCommandExecutor implements CommandManager<ActCommand<?>>,
    ObservingCommandManager<Game, ObservingCommand<Game>> {

  //FIFO
  private final Map<Integer, CommandQueueForAgent> queues = new HashMap<>();
  private final List<CommandQueueForAgent> orderOfQueues = new ArrayList<>();
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
  private List<CommandQueueForAgent> toExecute = new ArrayList<>();
  private final Game game;
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
   * Method to be called on each frame. It executes commands in queue based on their priority. First
   * command in queue is executed always - to make sure that commands do not stuck in queue
   */
  public void actOnFrame(long timeResources) {
    long start = System.currentTimeMillis();
    boolean executedAnyCommand, hasTimeRemaining = true;
    int frame = game.getFrameCount();

    //try to execute previous requests
    List<CommandQueueForAgent> toExecuteNext = new ArrayList<>();

    //execute at least one from previous request
    if (!toExecute.isEmpty()) {
      toExecute.remove(0).executedCommand(frame);
    }

    for (int i = 0; i < toExecute.size(); i++) {
      if (toExecute.get(i).canBeExpectedToBeExecuteInInterval(
          timeResources - (System.currentTimeMillis() - start))) {
        toExecute.get(i).executedCommand(frame);
      } else {

        //higher priority is assigned
        toExecuteNext.add(toExecute.get(i));
      }
      if (timeResources - (System.currentTimeMillis() - start) <= 0) {

        //reschedule
        if (i + 1 < toExecute.size()) {
          toExecuteNext.addAll(toExecute.subList(i + 1, toExecute.size()));
        }

        //exit execution and resume next time where it ended
        hasTimeRemaining = false;
        break;
      }
    }

    toExecute = toExecuteNext;
    Set<CommandQueueForAgent> queuesToSkip = new HashSet<>(toExecuteNext);

    //execute new requests if there is still time
    if (hasTimeRemaining && timeResources - (System.currentTimeMillis() - start) > 0) {
      int queues;
      try {
        lock.readLock().lock();
        queues = orderOfQueues.size();
      } finally {
        lock.readLock().unlock();
      }

      //execute new request while there is a time
      while (true) {
        executedAnyCommand = false;
        for (int i = 0; i < queues; i++) {
          if (timeResources - (System.currentTimeMillis() - start) <= 0) {

            //exit execution and resume next time where it ended
            hasTimeRemaining = false;
            break;
          }
          CommandQueueForAgent myQueue;
          try {
            lock.readLock().lock();
            myQueue = orderOfQueues.get(i);
          } finally {
            lock.readLock().unlock();
          }
          if (myQueue.canBeExpectedToBeExecuteInInterval(
              timeResources - (System.currentTimeMillis() - start))) {
            if (myQueue.isDeltaOfExecutionGreatEnough(frame) && myQueue.executedCommand(frame)) {
              executedAnyCommand = true;
            }
          } else {

            //higher priority is assigned
            if (!queuesToSkip.contains(myQueue) && myQueue.isDeltaOfExecutionGreatEnough(frame)) {
              toExecute.add(myQueue);
              queuesToSkip.add(myQueue);
            }
          }
        }
        if (!executedAnyCommand || !hasTimeRemaining) {
          break;
        }
      }
    }

    countOfPassedFrames = frame;
  }

  /**
   * Add request to internal queue
   */
  private boolean addToQueue(QueuedItemInterfaceWithResponseWithCommandClassGetter command) {
    CommandQueueForAgent myQueue;

    //is myQueue to handle this type present
    try {
      lock.readLock().lock();
      myQueue = queues.get(command.getAgentId());
    } finally {
      lock.readLock().unlock();
    }

    //create new myQueue to handle new types of commands
    if (myQueue == null) {
      try {
        lock.writeLock().lock();
        myQueue = new CommandQueueForAgent();
        queues.put(command.getAgentId(), myQueue);
        orderOfQueues.add(myQueue);
      } finally {
        lock.writeLock().unlock();
      }
    }
    myQueue.addToQueue(command);
    return true;
  }

  /**
   * Method to add item to queue with code to execute action in GAME
   */
  public boolean addCommandToObserve(ObservingCommand<Game> command, WorkingMemory memory,
      ResponseReceiverInterface<Boolean> responseReceiver) {
    return addToQueue(new QueuedItemInterfaceWithResponseWithCommandClassGetter() {

      @Override
      int getAgentId() {
        return memory.getAgentId();
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
  public boolean addCommandToAct(ActCommand<?> command, WorkingMemory memory,
      ResponseReceiverInterface<Boolean> responseReceiver) {

    return addToQueue(new QueuedItemInterfaceWithResponseWithCommandClassGetter() {

      @Override
      int getAgentId() {
        return memory.getAgentId();
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
   * Extension of item in queue
   */
  private static abstract class QueuedItemInterfaceWithResponseWithCommandClassGetter implements
      QueuedItemInterfaceWithResponse<Boolean> {

    /**
     * Get agent type
     */
    abstract int getAgentId();
  }

  private static class CommandQueueForAgent {

    private final Queue<QueuedItemInterfaceWithResponseWithCommandClassGetter> queue = new LinkedList<>();

    @Getter
    private long averageExecution = 0;

    private int countOfExecutions = 0;

    private int lastExecuted = 0;

    private static final int executionSpan = 10;

    boolean isDeltaOfExecutionGreatEnough(int currentFrame) {
      return currentFrame - lastExecuted > executionSpan;
    }

    boolean canBeExpectedToBeExecuteInInterval(long interval) {
      return interval > averageExecution;
    }

    boolean executedCommand(int frameCount) {
      Optional<QueuedItemInterfaceWithResponseWithCommandClassGetter> command;
      synchronized (queue) {
        command = Optional.ofNullable(queue.poll());
      }
      if (command.isPresent()) {

        //execute command and update average execution time
        long start = System.currentTimeMillis(), duration;
        command.get().executeItem();
        duration = System.currentTimeMillis() - start;
        averageExecution =
            ((averageExecution * countOfExecutions) + duration) / (countOfExecutions + 1);
        countOfExecutions++;
        lastExecuted = frameCount;
        return true;
      }
      return false;
    }

    /**
     * Add request to internal queue
     */
    void addToQueue(QueuedItemInterfaceWithResponseWithCommandClassGetter request) {
      synchronized (queue) {
        queue.add(request);
      }
    }

  }

}
