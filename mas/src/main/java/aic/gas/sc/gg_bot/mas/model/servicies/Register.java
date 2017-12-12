package aic.gas.sc.gg_bot.mas.model.servicies;

import aic.gas.sc.gg_bot.mas.model.agents.Agent;
import java.util.Map;

/**
 * Template for register
 */
public abstract class Register<V> {

  protected final Map<Agent, V> dataByOriginator;

  protected Register(Map<Agent, V> dataByOriginator) {
    this.dataByOriginator = dataByOriginator;
  }
}
