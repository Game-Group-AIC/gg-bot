package aic.gas.sc.gg_bot.mas.model.servicies.beliefs;

import aic.gas.sc.gg_bot.mas.model.knowledge.ReadOnlyMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.mas.model.servicies.ReadOnlyRegister;
import java.util.Optional;
import java.util.stream.Stream;

public interface IReadOnlyMemoryRegister extends ReadOnlyRegister {

  /**
   * Get ReadOnlyMemory by agent id
   */
  Optional<ReadOnlyMemory> getReadOnlyMemoryForAgent(int agentId);

  /**
   * Get set of memories by agent type
   */
  Stream<ReadOnlyMemory> getReadOnlyMemoriesForAgentType(AgentTypeID agentType);

  /**
   * Get all beliefs in system
   */
  Stream<ReadOnlyMemory> getReadOnlyMemories();

}
