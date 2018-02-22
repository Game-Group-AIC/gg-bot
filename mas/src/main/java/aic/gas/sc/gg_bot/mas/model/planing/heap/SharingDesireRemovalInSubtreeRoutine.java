package aic.gas.sc.gg_bot.mas.model.planing.heap;

import aic.gas.sc.gg_bot.mas.model.IResponseReceiver;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesireForAgents;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * Routine for shared desires removal from mediator
 */
@Slf4j
public class SharingDesireRemovalInSubtreeRoutine implements IResponseReceiver<Boolean> {

  private final Object lockMonitor = new Object();
  private Boolean unregistered = false;

  boolean unregisterSharedDesire(Set<SharedDesireForAgents> sharedDesires,
      HeapOfTrees heapOfTrees) {

    //share desire and wait for response of registration
    synchronized (lockMonitor) {
      if (heapOfTrees.getAgent().getDesireMediator()
          .unregisterDesires(new HashSet<>(sharedDesires), this)) {
        try {
          lockMonitor.wait();
        } catch (InterruptedException e) {
          log.error(this.getClass().getSimpleName() + ": " + e.getLocalizedMessage());
        }

        //is desire register, if so, make intention out of it
        if (unregistered) {
          heapOfTrees.removeSharedDesiresForOtherAgents(sharedDesires);
          return true;
        } else {
          log.error(this.getClass().getSimpleName() + ": desire for others was not registered.");
        }
      }
    }
    return false;
  }

  @Override
  public void receiveResponse(Boolean response) {

    //notify waiting method to decide commitment
    synchronized (lockMonitor) {
      this.unregistered = response;
      lockMonitor.notify();
    }
  }

}
