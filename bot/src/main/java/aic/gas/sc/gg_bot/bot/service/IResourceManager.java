package aic.gas.sc.gg_bot.bot.service;

//TODO this is hack. only units can spend resources...

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AbstractWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.TypeToBuy;
import bwapi.Player;

/**
 * Contract for service managing reservation of resources
 */
public interface IResourceManager {

  void processReservations(int minedMinerals, int minedGas, int supplyAvailable, Player player,
      int frame, int workersCount);

  <T extends AbstractWrapper<?> & TypeToBuy> boolean canSpendResourcesOn(T t, int agentId);

  <T extends AbstractWrapper<?> & TypeToBuy> void makeReservation(T t, int agentId);

  <T extends AbstractWrapper<?> & TypeToBuy> void removeReservation(T t, int agentId);

  void removeAllReservations(int agentId);

  <T extends AbstractWrapper<?> & TypeToBuy> boolean hasMadeReservationOn(T t, int agentId);

}
