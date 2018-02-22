package aic.gas.sc.gg_bot.mas.service;

/**
 * Contract to be implement by each terminable service
 */
public interface ITerminableService {

  /**
   * Tell service to terminate
   */
  void terminate();
}
