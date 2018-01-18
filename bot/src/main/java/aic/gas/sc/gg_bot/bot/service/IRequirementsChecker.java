package aic.gas.sc.gg_bot.bot.service;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AbstractWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.TypeToBuy;
import bwapi.Player;

public interface IRequirementsChecker {

  <T extends AbstractWrapper<?> & TypeToBuy> boolean areDependenciesMeet(T t);

  void updateBuildTreeByPlayersData(Player player);

}
