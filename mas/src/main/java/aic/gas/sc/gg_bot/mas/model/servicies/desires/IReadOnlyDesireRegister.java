package aic.gas.sc.gg_bot.mas.model.servicies.desires;

import aic.gas.sc.gg_bot.mas.model.agents.Agent;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesire;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesireForAgents;
import aic.gas.sc.gg_bot.mas.model.servicies.ReadOnlyRegister;
import java.util.Map;
import java.util.Set;

/**
 * Read-only register for memory
 */
public interface IReadOnlyDesireRegister extends ReadOnlyRegister {

  /**
   * Get desires shared by agent
   */
  Set<SharedDesireForAgents> getOwnSharedDesires(Agent agent);

  /**
   * For given agent get are shared desires from others he can accomplish
   */
  Map<SharedDesire, SharedDesireForAgents> getSharedDesiresFromOtherAgents(Agent self);

}
