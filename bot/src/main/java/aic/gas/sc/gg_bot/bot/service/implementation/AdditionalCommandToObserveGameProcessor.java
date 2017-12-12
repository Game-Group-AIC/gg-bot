package aic.gas.sc.gg_bot.bot.service.implementation;

import aic.gas.sc.gg_bot.mas.model.ResponseReceiverInterface;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentType;
import aic.gas.sc.gg_bot.mas.model.planing.command.ObservingCommand;
import bwapi.Game;
import lombok.extern.slf4j.Slf4j;

/**
 * Extra class to handle additional requests to observe game
 */
@Slf4j
public class AdditionalCommandToObserveGameProcessor {

  private final GameCommandExecutor commandExecutor;

  public AdditionalCommandToObserveGameProcessor(GameCommandExecutor commandExecutor) {
    this.commandExecutor = commandExecutor;
  }

  /**
   * Method to request extra game observation
   */
  public void requestObservation(ObservingCommand<Game> command, WorkingMemory memory,
      AgentType agentType) {
    GuardWaitingForResponse guardWaitingForResponse = new GuardWaitingForResponse();
    guardWaitingForResponse.requestObservation(command, memory, agentType);
  }

  /**
   * Class to represent object which waits on command execution
   */
  private class GuardWaitingForResponse implements ResponseReceiverInterface<Boolean> {

    final Object lockMonitor = new Object();

    /**
     * Send command to observe game
     */
    void requestObservation(ObservingCommand<Game> command, WorkingMemory memory,
        AgentType agentType) {
      synchronized (lockMonitor) {
        if (commandExecutor.addCommandToObserve(command, memory, this, agentType)) {
          try {
            lockMonitor.wait();
          } catch (InterruptedException e) {
            log.error(this.getClass().getSimpleName() + ": " + e.getLocalizedMessage());
          }
        }
      }
    }

    @Override
    public void receiveResponse(Boolean response) {

      //notify waiting method
      synchronized (lockMonitor) {
        if (!response) {
          log.error(this.getClass().getSimpleName() + " could not execute command");
        }
        lockMonitor.notify();
      }
    }
  }
}
