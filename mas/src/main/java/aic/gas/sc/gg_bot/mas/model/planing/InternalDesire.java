package aic.gas.sc.gg_bot.mas.model.planing;

import aic.gas.sc.gg_bot.mas.model.FactContainerInterface;
import aic.gas.sc.gg_bot.mas.model.agents.Agent;
import aic.gas.sc.gg_bot.mas.model.knowledge.ReadOnlyMemory;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentType;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Class extending Desire describes template for internal desires agents may want to commit to.
 * Concrete implementation of this are used in planning heap.
 */
@Slf4j
public abstract class InternalDesire<T extends Intention<? extends InternalDesire<?>>> extends
    Desire
    implements FactContainerInterface, OnChangeActor, OnDestructionActor {

  final CommitmentDeciderInitializer removeCommitment;
  @Getter
  final boolean isAbstract;
  final WorkingMemory memory;
  private final CommitmentDecider commitmentDecider;
  private final Optional<DesireParameters> parentsDesireParameters;
  @Getter
  private final Optional<ReactionOnChangeStrategy> reactionOnChangeStrategy;

  InternalDesire(DesireKey desireKey, WorkingMemory memory,
      CommitmentDeciderInitializer commitmentDecider,
      CommitmentDeciderInitializer removeCommitment, boolean isAbstract,
      ReactionOnChangeStrategy reactionOnChangeStrategy) {
    super(desireKey, memory);
    this.commitmentDecider = commitmentDecider.initializeCommitmentDecider(desireParameters);
    this.memory = memory;
    this.removeCommitment = removeCommitment;
    this.isAbstract = isAbstract;
    this.parentsDesireParameters = Optional.empty();
    this.reactionOnChangeStrategy = Optional.ofNullable(reactionOnChangeStrategy);
  }

  InternalDesire(DesireKey desireKey, WorkingMemory memory,
      CommitmentDeciderInitializer commitmentDecider,
      CommitmentDeciderInitializer removeCommitment, boolean isAbstract,
      DesireParameters parentsDesireParameters, ReactionOnChangeStrategy reactionOnChangeStrategy) {
    super(desireKey, memory);
    this.commitmentDecider = commitmentDecider.initializeCommitmentDecider(desireParameters);
    this.memory = memory;
    this.removeCommitment = removeCommitment;
    this.isAbstract = isAbstract;
    this.parentsDesireParameters = Optional.of(parentsDesireParameters);
    this.reactionOnChangeStrategy = Optional.ofNullable(reactionOnChangeStrategy);
  }

  InternalDesire(DesireParameters desireParameters, WorkingMemory memory,
      CommitmentDeciderInitializer commitmentDecider,
      CommitmentDeciderInitializer removeCommitment, boolean isAbstract, int originatorId,
      ReactionOnChangeStrategy reactionOnChangeStrategy) {
    super(desireParameters, originatorId);
    this.memory = memory;
    this.commitmentDecider = commitmentDecider.initializeCommitmentDecider(desireParameters);
    this.removeCommitment = removeCommitment;
    this.isAbstract = isAbstract;
    this.parentsDesireParameters = Optional.empty();
    this.reactionOnChangeStrategy = Optional.ofNullable(reactionOnChangeStrategy);
  }

  @Override
  public void actOnRemoval() {
    actOnChange(memory, desireParameters);
  }

  public boolean shouldCommit(List<DesireKey> madeCommitmentToTypes,
      List<DesireKey> didNotMakeCommitmentToTypes,
      List<DesireKey> typesAboutToMakeDecision) {

    //TODO - HACK! does not change return value and enables reaction
    return commitmentDecider
        .shouldCommit(madeCommitmentToTypes, didNotMakeCommitmentToTypes, typesAboutToMakeDecision,
            memory) && actOnChange(memory, desireParameters);
  }

  public boolean shouldCommit(List<DesireKey> madeCommitmentToTypes,
      List<DesireKey> didNotMakeCommitmentToTypes,
      List<DesireKey> typesAboutToMakeDecision, int numberOfCommittedAgents) {

    //TODO - HACK! does not change return value and enables reaction
    return commitmentDecider
        .shouldCommit(madeCommitmentToTypes, didNotMakeCommitmentToTypes, typesAboutToMakeDecision,
            memory, numberOfCommittedAgents) && actOnChange(memory, desireParameters);
  }

  public <V> Optional<V> returnFactValueForGivenKey(FactKey<V> factKey) {
    return memory.returnFactValueForGivenKey(factKey);
  }

  public <V, S extends Stream<V>> Optional<S> returnFactSetValueForGivenKey(FactKey<V> factKey) {
    return memory.returnFactSetValueForGivenKey(factKey);
  }

  public Optional<ReadOnlyMemory> getReadOnlyMemoryForAgent(int agentId) {
    return memory.getReadOnlyMemoryForAgent(agentId);
  }

  public Stream<ReadOnlyMemory> getReadOnlyMemoriesForAgentType(AgentType agentType) {
    return memory.getReadOnlyMemoriesForAgentType(agentType.getAgentTypeID());
  }

  public Stream<ReadOnlyMemory> getReadOnlyMemories() {
    return memory.getReadOnlyMemories();
  }

  public boolean isFactKeyForValueInMemory(FactKey<?> factKey) {
    return memory.isFactKeyForValueInMemory(factKey);
  }

  public boolean isFactKeyForSetInMemory(FactKey<?> factKey) {
    return memory.isFactKeyForSetInMemory(factKey);
  }

  public int getAgentId() {
    return memory.getAgentId();
  }

  /**
   * Returns fact value for desire parameters of parenting intention
   */
  public <V> Optional<V> returnFactValueOfParentIntentionForGivenKey(FactKey<V> factKey) {
    if (parentsDesireParameters.isPresent()) {
      return parentsDesireParameters.get().returnFactValueForGivenKey(factKey);
    }
    log.error("There are no parameters from parent intention present.");
    return Optional.empty();
  }

  /**
   * Returns fact value set for desire parameters of parenting intention
   */
  public <V, S extends Stream<V>> Optional<S> returnFactSetValueOfParentIntentionForGivenKey(
      FactKey<V> factKey) {
    if (parentsDesireParameters.isPresent()) {
      return parentsDesireParameters.get().returnFactSetValueForGivenKey(factKey);
    }
    log.error("There are no parameters from parent intention present.");
    return Optional.empty();
  }

  /**
   * Return intention induced by this desire for given agent
   */
  public abstract T formIntention(Agent agent);

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof InternalDesire)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    InternalDesire that = (InternalDesire) o;

    return isAbstract == that.isAbstract;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (isAbstract ? 1 : 0);
    return result;
  }
}
