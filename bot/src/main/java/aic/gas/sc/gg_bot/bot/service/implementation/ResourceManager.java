package aic.gas.sc.gg_bot.bot.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AbstractWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.TypeToBuy;
import aic.gas.sc.gg_bot.bot.service.IRequirementsChecker;
import aic.gas.sc.gg_bot.bot.service.IResourceManager;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

//TODO there is still probably bug - skip building without dependencies - check if it is possible to mine gas
//TODO save 50 minerals for worker if we do not have any worker
@Slf4j
public class ResourceManager implements IResourceManager {

  //TODO own dependency tree + check why there are duplicities

  private final IRequirementsChecker requirementsChecker;
  private final Object MONITOR = new Object();
  private boolean updatingResources = false;
  private boolean readingResources = false;
  private final List<Tuple<?>> resourcesAvailableFor = new ArrayList<>();
  private final List<Tuple<?>> reservationQueue = new ArrayList<>();
  private final Set<Integer> reservationsFrom = new HashSet<>();

  @Getter
  private List<String> reservationStatuses = new ArrayList<>();

  //todo hack to check for extractor
  private Optional<Unit> extractor = Optional.empty();

  public ResourceManager(IRequirementsChecker requirementsChecker) {
    this.requirementsChecker = requirementsChecker;
  }

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

        //check if we still have extractor
        if (!extractor.isPresent() || !extractor.get().exists()) {
          extractor = player.getUnits().stream()
              .filter(unit -> unit.getType().equals(UnitType.Zerg_Extractor))
              .filter(Unit::isCompleted)
              .findAny();
        }

        reservationStatuses.clear();
        int lastIndex = 0;
        for (int i = 0; i < reservationQueue.size(); i++) {
          lastIndex = i;
          Tuple<?> tuple = reservationQueue.get(i);

          //skip when dependencies are not met
          if (!requirementsChecker.areDependenciesMeet(tuple.reservationMadeOn)) {
            reservationStatuses.add(formMessage(tuple, "UD"));
            continue;
          }

          if (sumOfMinerals + tuple.reservationMadeOn.mineralCost() <= minedMinerals
              && sumOfGas + tuple.reservationMadeOn.gasCost() <= sumOfGas
              && sumOfSupply + tuple.reservationMadeOn.supplyRequired() <= supplyAvailable) {
            resourcesAvailableFor.add(tuple);
            sumOfMinerals = sumOfMinerals + tuple.reservationMadeOn.mineralCost();
            sumOfGas = sumOfGas + tuple.reservationMadeOn.gasCost();
            sumOfSupply = sumOfSupply + tuple.reservationMadeOn.supplyRequired();
            reservationStatuses.add(formMessage(tuple, "R"));
          } else {
            if (sumOfMinerals + tuple.reservationMadeOn.mineralCost() > minedMinerals
                && sumOfGas + tuple.reservationMadeOn.gasCost() > sumOfGas) {
              reservationStatuses.add(formMessage(tuple, "W"));
              break;
            } else {
              if ((!skippedGasRequest || !extractor.isPresent())
                  && sumOfGas + tuple.reservationMadeOn.gasCost() > sumOfGas) {
                reservationStatuses.add(formMessage(tuple, "W"));
                skippedGasRequest = true;
              } else {
                reservationStatuses.add(formMessage(tuple, "W"));
                break;
              }
            }
          }
        }

        //check rest
        for (int i = lastIndex + 1; i < reservationQueue.size(); i++) {
          reservationStatuses.add(formMessage(reservationQueue.get(i), "W"));
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
        Tuple<T> tuple = new Tuple<>(agentId, t);
        reservationQueue.add(tuple);
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
          int index = 0;
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

  private static final String formMessage(Tuple<?> tuple, String status) {
    return tuple.reservationMadeBy + ": " + tuple.reservationMadeOn.getName() + " - " + status;
  }

  @EqualsAndHashCode(of = {"reservationMadeBy", "reservationMadeOn"})
  @AllArgsConstructor
  private static class Tuple<T extends AbstractWrapper<?> & TypeToBuy> {

    private final int reservationMadeBy;
    private final T reservationMadeOn;
  }

}
