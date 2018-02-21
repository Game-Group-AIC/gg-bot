package aic.gas.sc.gg_bot.replay_parser.configuration;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import java.util.HashMap;
import java.util.Map;

/**
 * Parameters to tweak learning decision process...
 */
public class DecisionLearningConfiguration {

  private static final Configuration DEFAULT_CONFIGURATION = Configuration.builder().build();

  private static final Map<AgentTypes, Map<DesireKeys, Configuration>> CONFIGURATIONS = new HashMap<>();

  static {
    //TODO init map by configurations
  }

  public static Configuration getConfiguration(AgentTypes agentType, DesireKeys desireKey) {
    if (CONFIGURATIONS.containsKey(agentType)) {
      return CONFIGURATIONS.get(agentType).getOrDefault(desireKey, DEFAULT_CONFIGURATION);
    }
    return DEFAULT_CONFIGURATION;
  }

}
