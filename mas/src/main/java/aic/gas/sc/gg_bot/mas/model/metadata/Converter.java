package aic.gas.sc.gg_bot.mas.model.metadata;

/**
 * Interface for converter
 */
public interface Converter {

  /**
   * Get order of converted feature
   */
  int getId();

  /**
   * Get name of converter
   */
  String getName();

}
