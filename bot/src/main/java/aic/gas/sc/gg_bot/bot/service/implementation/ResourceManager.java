package aic.gas.sc.gg_bot.bot.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ATechTypeWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AbstractWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.TypeToBuy;
import aic.gas.sc.gg_bot.bot.service.IResourceManager;
import bwapi.Player;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

//TODO check dependencies
//TODO consider population
@Slf4j
public class ResourceManager implements IResourceManager {

  private final Object MONITOR = new Object();
  private boolean updatingResources = false;
  private boolean readingResources = false;
  private final List<Tuple<?>> resourcesAvailableFor = new ArrayList<>();
  private final List<Tuple<?>> reservationQueue = new ArrayList<>();
  private final Set<Integer> reservationsFrom = new HashSet<>();

  public void processReservations(int minedMinerals, int minedGas, int supplyAvailable,
      Player player) {
    updatingResources = true;
    synchronized (MONITOR) {
      try {
        if (readingResources) {
          MONITOR.wait();
        }
        resourcesAvailableFor.clear();
        int sumOfMinerals = 0, sumOfGas = 0, sumOfSupply = 0;
        boolean skippedGasRequest = false;
        for (Tuple<?> tuple : reservationQueue) {

          //skip when dependencies are not met
          if (tuple.reservationMadeOn instanceof AUnitTypeWrapper) {
            if (!player.isUnitAvailable(((AUnitTypeWrapper) tuple.reservationMadeOn).getType())) {
              continue;
            }
          } else if (tuple.reservationMadeOn instanceof ATechTypeWrapper) {
            if (!player
                .isResearchAvailable(((ATechTypeWrapper) tuple.reservationMadeOn).getType())) {
              continue;
            }
          }

          if (sumOfMinerals + tuple.reservationMadeOn.mineralCost() <= minedMinerals
              && sumOfGas + tuple.reservationMadeOn.gasCost() <= sumOfGas
              && sumOfSupply + tuple.reservationMadeOn.supplyRequired() <= sumOfSupply) {
            resourcesAvailableFor.add(tuple);
            sumOfMinerals = sumOfMinerals + tuple.reservationMadeOn.mineralCost();
            sumOfGas = sumOfGas + tuple.reservationMadeOn.gasCost();
            sumOfSupply = sumOfSupply + tuple.reservationMadeOn.supplyRequired();
          } else {
            if (sumOfMinerals + tuple.reservationMadeOn.mineralCost() > minedMinerals
                && sumOfGas + tuple.reservationMadeOn.gasCost() > sumOfGas) {
              break;
            } else {
              if (!skippedGasRequest && sumOfGas + tuple.reservationMadeOn.gasCost() > sumOfGas) {
                skippedGasRequest = true;
              } else {
                break;
              }
            }
          }
        }
      } catch (InterruptedException e) {
        log.error(e.getMessage());
      } finally {
        updatingResources = false;
        MONITOR.notify();
      }
    }
  }

  @Override
  public <T extends AbstractWrapper<?> & TypeToBuy> boolean canSpendResourcesOn(T t, int agentId) {
    synchronized (MONITOR) {
      try {
        while (updatingResources) {
          MONITOR.notify();
          MONITOR.wait();
        }
        readingResources = true;
        for (Tuple<?> tuple : resourcesAvailableFor) {
          if (tuple.reservationMadeBy == agentId) {
            if (tuple.reservationMadeOn.equals(t)) {
              return true;
            }
          }
        }
      } catch (InterruptedException e) {
        log.error(e.getMessage());
      } finally {
        readingResources = false;
        MONITOR.notify();
      }
    }
    return false;
  }

  @Override
  public <T extends AbstractWrapper<?> & TypeToBuy> void makeReservation(T t, int agentId) {
    synchronized (MONITOR) {
      try {
        while (updatingResources) {
          MONITOR.notify();
          MONITOR.wait();
        }
        readingResources = true;
        reservationQueue.add(new Tuple<>(agentId, t));
        reservationsFrom.add(agentId);
      } catch (InterruptedException e) {
        log.error(e.getMessage());
      } finally {
        readingResources = false;
        MONITOR.notify();
      }
    }
  }

  @Override
  public <T extends AbstractWrapper<?> & TypeToBuy> void removeReservation(T t, int agentId) {
    synchronized (MONITOR) {
      try {
        while (updatingResources) {
          MONITOR.notify();
          MONITOR.wait();
        }
        readingResources = true;
        if (reservationsFrom.contains(agentId)) {
          int index = -1;
          for (int i = 0; i < reservationQueue.size(); i++) {
            Tuple<?> tuple = reservationQueue.get(i);
            if (tuple.reservationMadeBy == agentId) {
              if (tuple.reservationMadeOn.equals(t)) {
                reservationQueue.remove(i);
                index = i;
                break;
              }
            }
          }
          boolean isPresent = false;
          for (int i = index; i < reservationQueue.size(); i++) {
            if (reservationQueue.get(i).reservationMadeBy == agentId) {
              isPresent = true;
              break;
            }
          }
          if (!isPresent) {
            reservationsFrom.remove(agentId);
          }
        }
      } catch (InterruptedException e) {
        log.error(e.getMessage());
      } finally {
        readingResources = false;
        MONITOR.notify();
      }
    }
  }

  @Override
  public void removeAllReservations(int agentId) {
    synchronized (MONITOR) {
      try {
        while (updatingResources) {
          MONITOR.notify();
          MONITOR.wait();
        }
        readingResources = true;
        if (reservationsFrom.contains(agentId)) {
          reservationQueue.removeIf(tuple -> tuple.reservationMadeBy == agentId);
          reservationsFrom.remove(agentId);
        }
      } catch (InterruptedException e) {
        log.error(e.getMessage());
      } finally {
        readingResources = false;
        MONITOR.notify();
      }
    }
  }

  @EqualsAndHashCode(of = {"reservationMadeBy", "reservationMadeOn"})
  @AllArgsConstructor
  private static class Tuple<T extends AbstractWrapper<?> & TypeToBuy> {

    private final int reservationMadeBy;
    private final T reservationMadeOn;
  }

}
