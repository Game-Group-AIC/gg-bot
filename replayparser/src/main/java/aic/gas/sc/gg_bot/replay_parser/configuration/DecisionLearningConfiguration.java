package aic.gas.sc.gg_bot.replay_parser.configuration;

import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import java.util.HashMap;
import java.util.Map;

/**
 * Parameters to tweak learning decision process...
 */
public class DecisionLearningConfiguration {

  private static final Configuration DEFAULT_CONFIGURATION = Configuration.builder().build();

  private static final Map<AgentTypeID, Map<DesireKeyID, Configuration>> CONFIGURATIONS = new HashMap<>();

  static {
    //TODO init map by configurations
  }

  public static Configuration getConfiguration(AgentTypeID agentTypeID, DesireKeyID desireKeyID) {
    if (CONFIGURATIONS.containsKey(agentTypeID)) {
      return CONFIGURATIONS.get(agentTypeID).getOrDefault(desireKeyID, DEFAULT_CONFIGURATION);
    }
    return DEFAULT_CONFIGURATION;
  }

}
