package aic.gas.sc.gg_bot.mas.model.servicies;

/**
 * Common contract for working registers
 */
public interface WorkingRegister<V extends ReadOnlyRegister> extends ReadOnlyRegister {

  /**
   * Returns itself as read only register
   */
  V getAsReadonlyRegister();

  /**
   * Execute maintenance (eg. forget)
   */
  void executeMaintenance();
}
