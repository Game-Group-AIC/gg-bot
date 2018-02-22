package aic.gas.sc.gg_bot.abstract_bot.model;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;

/**
 * Template for strategy to get define strategy to obtain type after game is loaded
 */
public interface ITypeWrapperStrategy {

  AUnitTypeWrapper returnType();
}
