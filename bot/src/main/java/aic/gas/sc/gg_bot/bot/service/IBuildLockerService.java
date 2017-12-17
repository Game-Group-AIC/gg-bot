package aic.gas.sc.gg_bot.bot.service;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;

//TODO this is hack. Instance locks unit types for period of time to prevent multiple builds from single order
public interface IBuildLockerService {

  void lock(AUnitTypeWrapper unitType);

  void releaseLocksOnTypes();

  boolean isLocked(AUnitTypeWrapper unitType);

}
