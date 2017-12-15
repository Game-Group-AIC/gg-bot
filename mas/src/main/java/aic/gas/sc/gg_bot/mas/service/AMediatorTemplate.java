package aic.gas.sc.gg_bot.mas.service;

import aic.gas.sc.gg_bot.mas.model.QueuedItemInterfaceWithResponse;
import aic.gas.sc.gg_bot.mas.model.servicies.ReadOnlyRegister;
import aic.gas.sc.gg_bot.mas.model.servicies.WorkingRegister;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import lombok.extern.slf4j.Slf4j;

/**
 * Template for each mediator to implement. Mediator instance enables agents to share information
 * through it. Instead of direct communication agent to agent mediator is used to reduce
 * communication required.
 */
@Slf4j
public abstract class AMediatorTemplate<V extends ReadOnlyRegister, T extends WorkingRegister<V>> implements
    TerminableService {

  protected final T workingRegister;
  private final BlockingQueue<QueuedItemInterfaceWithResponse<?>> queue = new LinkedBlockingDeque<>();
  private final Consumer consumer = new Consumer();
  private static final long maintenance = 5000;

  protected AMediatorTemplate(T workingRegister) {
    this.workingRegister = workingRegister;

    //main code for mediator to handle queue and update shared register
    consumer.start();
  }

  /**
   * Add request to internal queue
   */
  protected boolean addToQueue(QueuedItemInterfaceWithResponse<?> request) {
    try {
      queue.put(request);
      return true;
    } catch (InterruptedException e) {
      log.error(e.getLocalizedMessage());
    }
    return false;
  }

  @Override
  public void terminate() {
    consumer.interrupt();
    log.info("Terminating mediator...");
  }

  /**
   * Returns snapshot of register
   */
  public V getReadOnlyRegister() {
    return workingRegister.getAsReadonlyRegister();
  }

  /**
   * Class describing consumer of queued item. Consumer executes items and every once in a while
   * update read only register
   */
  private class Consumer extends Thread {

    @Override
    public void run() {
      try {
        long start = System.currentTimeMillis(), currentTime;

        //consuming items
        while (true) {
          queue.take().executeItem();

          //execute maintenance
          if ((currentTime = System.currentTimeMillis()) - start >= maintenance) {
            workingRegister.executeMaintenance();
            start = currentTime;
          }
        }
      } catch (InterruptedException e) {
        log.error(e.getLocalizedMessage());
      }
    }
  }

}
