package aic.gas.sc.gg_bot.bot.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.bot.service.IBuildLockerService;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BuildLockerService implements IBuildLockerService {

  private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
  private final Map<AUnitTypeWrapper, Integer> lockRegister = new HashMap<>();
  private static final int lockTypeForDuration = 15;

  private static final BuildLockerService instance = new BuildLockerService();

  //private constructor to avoid client applications to use constructor
  private BuildLockerService() {
  }

  public static BuildLockerService getInstance() {
    return instance;
  }

  @Override
  public void lock(AUnitTypeWrapper unitType) {
    try {
      lock.writeLock().lock();
      lockRegister.put(unitType, 0);
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void releaseLocksOnTypes() {
    try {
      lock.writeLock().lock();
      Iterator<Entry<AUnitTypeWrapper, Integer>> entryIt = lockRegister.entrySet().iterator();

      //handle locks
      while (entryIt.hasNext()) {
        Entry<AUnitTypeWrapper, Integer> entry = entryIt.next();
        if (entry.getValue() + 1 >= lockTypeForDuration) {
          entryIt.remove();
        } else {
          entry.setValue(entry.getValue() + 1);
        }
      }

    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public boolean isLocked(AUnitTypeWrapper unitType) {
    try {
      lock.readLock().lock();
      return lockRegister.containsKey(unitType);
    } finally {
      lock.readLock().unlock();
    }
  }

}