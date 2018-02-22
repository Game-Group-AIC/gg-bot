package aic.gas.sc.gg_bot.mas.model.planing.heap;

import aic.gas.sc.gg_bot.mas.model.IResponseReceiver;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesireInRegister;
import lombok.extern.slf4j.Slf4j;

/**
 * Routine for sharing desire with mediator
 */
@Slf4j
class SharingDesireRoutine implements IResponseReceiver<Boolean> {

  private final Object lockMonitor = new Object();
  private Boolean registered = false;

  boolean sharedDesire(SharedDesireInRegister sharedDesire, HeapOfTrees heapOfTrees) {
    synchronized (lockMonitor) {
      if (heapOfTrees.getAgent().getDesireMediator().registerDesire(sharedDesire, this)) {
        try {
          lockMonitor.wait();
        } catch (InterruptedException e) {
          log.error(this.getClass().getSimpleName() + ": " + e.getLocalizedMessage());
        }

        //is desire register, if so, make intention out of it
        if (registered) {
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
      this.registered = response;
      lockMonitor.notify();
    }
  }
}
