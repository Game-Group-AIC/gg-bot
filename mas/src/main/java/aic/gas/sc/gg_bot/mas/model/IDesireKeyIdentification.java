package aic.gas.sc.gg_bot.mas.model;

import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;

/**
 * Contract defining method to be implemented by each class which wants enable user to get desire
 * key associated with class
 */
public interface IDesireKeyIdentification {

  /**
   * Returns DesireKey associated with this instance
   */
  DesireKey getDesireKey();

}
