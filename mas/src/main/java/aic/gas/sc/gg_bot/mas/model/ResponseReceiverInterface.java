package aic.gas.sc.gg_bot.mas.model;

/**
 * Interface for response receiver defining method to send response
 */
public interface ResponseReceiverInterface<V> {

  /**
   * Method is called on receiver to end him response
   */
  void receiveResponse(V response);

}
