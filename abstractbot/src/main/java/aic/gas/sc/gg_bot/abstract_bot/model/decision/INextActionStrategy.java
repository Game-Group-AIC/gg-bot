package aic.gas.sc.gg_bot.abstract_bot.model.decision;

/**
 * Contract to return commitment associated with strategy implementing this interface.
 */
interface INextActionStrategy {

  /**
   * Return next commitment
   */
  boolean commit();

}
