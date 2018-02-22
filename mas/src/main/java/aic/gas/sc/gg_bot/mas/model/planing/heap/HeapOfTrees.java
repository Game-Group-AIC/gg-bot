package aic.gas.sc.gg_bot.mas.model.planing.heap;

import aic.gas.sc.gg_bot.mas.model.IPlanningTree;
import aic.gas.sc.gg_bot.mas.model.IResponseReceiver;
import aic.gas.sc.gg_bot.mas.model.agents.Agent;
import aic.gas.sc.gg_bot.mas.model.knowledge.PlanningTreeOfAnotherAgent;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireParameters;
import aic.gas.sc.gg_bot.mas.model.planing.Desire;
import aic.gas.sc.gg_bot.mas.model.planing.DesireFromAnotherAgent;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesire;
import aic.gas.sc.gg_bot.mas.model.planing.SharedDesireForAgents;
import aic.gas.sc.gg_bot.mas.model.servicies.desires.IReadOnlyDesireRegister;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Facade for planning heapOfTrees. HeapOfTrees manages nodes at top level.
 */
@Slf4j
public class HeapOfTrees implements IPlanningTree,
    IParent<DesireNodeAtTopLevel<?>, IntentionNodeAtTopLevel<?, ?>>,
    IResponseReceiver<Boolean> {

  private final Map<SharedDesire, SharedDesireForAgents> sharedDesiresForOtherAgents = new HashMap<>();
  private final Map<SharedDesire, SharedDesireForAgents> sharedDesiresByOtherAgents = new HashMap<>();

  @Getter
  private final Agent<?> agent;

  private final SharingDesireRemovalInSubtreeRoutine sharingDesireRemovalInSubtreeRoutine = new SharingDesireRemovalInSubtreeRoutine();

  //registers of nodes with desires from others
  private final NodeManipulationWithAbstractDesiresFromOthers manipulationWithAbstractDesiresFromOthers = new NodeManipulationWithAbstractDesiresFromOthers();
  private final NodeManipulationWithDesiresFromOthers manipulationWithDesiresFromOthers = new NodeManipulationWithDesiresFromOthers();

  //registers of nodes with own desires
  private final ChildNodeManipulation<IntentionNodeAtTopLevel.WithCommand.OwnReasoning, DesireNodeAtTopLevel.Own.WithReasoningCommand> manipulatorWithReasoningCommands = new ChildNodeManipulation<>();
  private final ChildNodeManipulation<IntentionNodeAtTopLevel.WithCommand.OwnActing, DesireNodeAtTopLevel.Own.WithActingCommand> manipulatorWithActingCommands = new ChildNodeManipulation<>();
  private final ChildNodeManipulation<IntentionNodeAtTopLevel.WithDesireForOthers, DesireNodeAtTopLevel.ForOthers> manipulatorWithDesiresForOthers = new ChildNodeManipulation<>();
  private final ChildNodeManipulation<IntentionNodeAtTopLevel.WithAbstractPlan.Own, DesireNodeAtTopLevel.Own.WithAbstractIntention> manipulatorWithOwnAbstractPlan = new ChildNodeManipulation<>();

  //to aggregate data
  private final List<ChildNodeManipulation<? extends IntentionNodeAtTopLevel<?, ?>, ? extends DesireNodeAtTopLevel<?>>> registers = new ArrayList<>();

  public HeapOfTrees(Agent<?> agent) {
    this.agent = agent;
    registers.add(manipulationWithAbstractDesiresFromOthers);
    registers.add(manipulationWithDesiresFromOthers);
    registers.add(manipulatorWithReasoningCommands);
    registers.add(manipulatorWithActingCommands);
    registers.add(manipulatorWithDesiresForOthers);
    registers.add(manipulatorWithOwnAbstractPlan);
  }

  public void removeCommitmentToSharedDesires() {
    agent.getDesireMediator()
        .removeCommitmentToDesires(agent, new HashSet<>(sharedDesiresByOtherAgents.values()), this);
  }

  /**
   * Initialize top level of heapOfTrees with desires' types specified in agent type
   */
  public void initTopLevelDesires(IReadOnlyDesireRegister desireRegister) {
    updateTopLevelDesires(desireRegister, agent.getAgentType().returnPlanAsSetOfDesiresForOthers(),
        agent.getAgentType().returnPlanAsSetOfDesiresWithAbstractIntention(),
        agent.getAgentType().returnPlanAsSetOfDesiresWithIntentionToAct(),
        agent.getAgentType().returnPlanAsSetOfDesiresWithIntentionToReason());
  }

  /**
   * Method update top level nodes
   */
  public void updateDesires(IReadOnlyDesireRegister desireRegister) {
    updateTopLevelDesires(desireRegister,
        manipulatorWithDesiresForOthers.removeDesiresForUncommittedNodesAndReturnTheirKeys(),
        manipulatorWithOwnAbstractPlan.removeDesiresForUncommittedNodesAndReturnTheirKeys(),
        manipulatorWithActingCommands.removeDesiresForUncommittedNodesAndReturnTheirKeys(),
        manipulatorWithReasoningCommands.removeDesiresForUncommittedNodesAndReturnTheirKeys());

    //tell nodes with abstract plans to update its childes
    manipulatorWithOwnAbstractPlan.intentionNodesByKey.values()
        .forEach(IIntentionNodeWithChildes::updateDesires);
    manipulationWithAbstractDesiresFromOthers.intentionNodesByKey.values()
        .forEach(IIntentionNodeWithChildes::updateDesires);
  }

  /**
   * Method update top level nodes
   */
  private void updateTopLevelDesires(IReadOnlyDesireRegister desireRegister,
      Set<DesireKey> desiresForOthers, Set<DesireKey> desiresWithAbstractIntention,
      Set<DesireKey> desiresWithIntentionToAct, Set<DesireKey> desiresWithIntentionToReason) {
    desiresForOthers.stream()
        .map(agent::formDesireForOthers)
        .forEach(desireForOthers -> manipulatorWithDesiresForOthers
            .addDesireNode(new DesireNodeAtTopLevel.ForOthers(this, desireForOthers)));
    desiresWithIntentionToAct.stream()
        .map(agent::formOwnDesireWithActingCommand)
        .forEach(acting -> manipulatorWithActingCommands
            .addDesireNode(new DesireNodeAtTopLevel.Own.WithActingCommand(this, acting)));
    desiresWithIntentionToReason.stream()
        .map(agent::formOwnDesireWithReasoningCommand)
        .forEach(reasoning -> manipulatorWithReasoningCommands
            .addDesireNode(new DesireNodeAtTopLevel.Own.WithReasoningCommand(this, reasoning)));
    desiresWithAbstractIntention.stream()
        .map(agent::formOwnDesireWithAbstractIntention)
        .forEach(withAbstractIntention -> manipulatorWithOwnAbstractPlan.addDesireNode(
            new DesireNodeAtTopLevel.Own.WithAbstractIntention(this, withAbstractIntention)));
    resolveSharedDesires(desireRegister);
  }

  /**
   * Take register and update own structures with shared desires
   */
  private void resolveSharedDesires(IReadOnlyDesireRegister desireRegister) {
    desireRegister.getOwnSharedDesires(agent).forEach(
        sharedDesire -> sharedDesiresForOtherAgents.get(sharedDesire)
            .updateCommittedAgentsSet(sharedDesire));

    //update shared desires by others
    Map<SharedDesire, SharedDesireForAgents> sharedDesiresByOtherAgentsInRegister = desireRegister
        .getSharedDesiresFromOtherAgents(agent);

    //remove missing, also remove own shared desires in removed subtrees
    Set<SharedDesireForAgents> toRemove = new HashSet<>();
    for (Map.Entry<SharedDesire, SharedDesireForAgents> entry : sharedDesiresByOtherAgents
        .entrySet()) {
      SharedDesireForAgents desireInRegister = sharedDesiresByOtherAgentsInRegister
          .get(entry.getKey());
      if (desireInRegister != null) {
        entry.getValue().updateCommittedAgentsSet(desireInRegister);
      } else {
        toRemove.add(entry.getValue());
      }
    }
    toRemove.forEach(sharedDesiresByOtherAgents::remove);
    toRemove = toRemove.stream()
        .filter(sharedDesireForAgents -> !manipulationWithDesiresFromOthers
            .removeSharedDesire(sharedDesireForAgents))
        .collect(Collectors.toSet());
    Set<SharedDesireForAgents> toRemoveOwnSharedDesiresFromRegister = toRemove.stream()
        .map(
            manipulationWithAbstractDesiresFromOthers::removeSharedDesireAndCollectOwnSharedDesiresInSubtreeToRemove)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
    if (!toRemoveOwnSharedDesiresFromRegister.isEmpty()) {
      sharingDesireRemovalInSubtreeRoutine
          .unregisterSharedDesire(toRemoveOwnSharedDesiresFromRegister, this);
    }

    //add new, try to add node with abstract plan first
    sharedDesiresByOtherAgentsInRegister.entrySet().stream()
        .filter(entry -> !sharedDesiresByOtherAgents.containsKey(entry.getValue()))
        .forEach(entry -> {
          Optional<DesireFromAnotherAgent.WithAbstractIntention> withAbstractIntention = agent
              .formDesireFromOtherAgentWithAbstractIntention(entry.getValue());
          if (withAbstractIntention.isPresent()) {
            manipulationWithAbstractDesiresFromOthers.addDesireNode(
                new DesireNodeAtTopLevel.FromAnotherAgent.WithAbstractIntention(this,
                    withAbstractIntention.get()));
            sharedDesiresByOtherAgents.put(entry.getKey(), entry.getValue());
          } else {
            Optional<DesireFromAnotherAgent.WithIntentionWithPlan> intentionWithPlan = agent
                .formDesireFromOtherAgentWithIntentionWithPlan(entry.getValue());
            if (intentionWithPlan.isPresent()) {
              manipulationWithDesiresFromOthers.addDesireNode(
                  new DesireNodeAtTopLevel.FromAnotherAgent.WithIntentionWithPlan(this,
                      intentionWithPlan.get()));
            } else {
              log.error(agent.getAgentType().getName()
                  + " is trying to add desire from other which is not supported.");
              throw new IllegalArgumentException(agent.getAgentType().getName()
                  + " is trying to add desire from other which is not supported.");
            }
          }
          sharedDesiresByOtherAgents.put(entry.getKey(), entry.getValue());
        });
  }

  /**
   * Register desire to share with other agents
   */
  void addSharedDesireForOtherAgents(SharedDesireForAgents sharedDesire) {
    sharedDesiresForOtherAgents.put(sharedDesire, sharedDesire);
  }

  /**
   * Unregister desire to share with other agents
   */
  void removeSharedDesireForOtherAgents(SharedDesireForAgents sharedDesire) {
    sharedDesiresForOtherAgents.remove(sharedDesire);
  }

  /**
   * Unregister desires to share with other agents
   */
  void removeSharedDesiresForOtherAgents(Set<SharedDesireForAgents> sharedDesires) {
    sharedDesires.forEach(sharedDesiresForOtherAgents::remove);
  }

  /**
   * Returns read only copy of this heapOfTrees to be shared with other agents
   */
  public PlanningTreeOfAnotherAgent getReadOnlyCopy() {
    return new PlanningTreeOfAnotherAgent(collectKeysOfCommittedDesiresInTreeCounts(),
        collectKeysOfDesiresInTreeCounts(),
        getParametersOfCommittedDesiresOnTopLevel(),
        getParametersOfDesiresOnTopLevel(), committedSharedDesiresParametersByOtherAgents(),
        sharedDesiresParameters());
  }

  public List<DesireNodeAtTopLevel<?>> getNodesWithDesire() {
    return registers.stream()
        .flatMap(childNodeManipulation -> {

          //todo hack to catch exception
          try {
            return new ArrayList<>(childNodeManipulation.desiresNodesByKey.values()).stream();
          } catch (Exception e) {
            return Stream.empty();
          }
        })
        .collect(Collectors.toList());
  }

  public List<IntentionNodeAtTopLevel<?, ?>> getNodesWithIntention() {
    return registers.stream()
        .flatMap(childNodeManipulation -> {

          //todo hack to catch exception
          try {
            return new ArrayList<>(childNodeManipulation.intentionNodesByKey.values()).stream();
          } catch (Exception e) {
            return Stream.empty();
          }
        })
        .collect(Collectors.toList());
  }

  @Override
  public Optional<DesireKey> getDesireKeyAssociatedWithParent() {
    return Optional.empty();
  }

  @Override
  public Set<DesireParameters> committedSharedDesiresParametersByOtherAgents() {
    Set<DesireParameters> toReturn = new HashSet<>(
        manipulationWithAbstractDesiresFromOthers.intentionNodesByKey.keySet());
    toReturn.addAll(manipulationWithDesiresFromOthers.intentionNodesByKey.keySet());
    return toReturn;
  }

  @Override
  public Set<DesireParameters> sharedDesiresParameters() {
    return sharedDesiresForOtherAgents.keySet().stream()
        .map(Desire::getDesireParameters)
        .collect(Collectors.toSet());
  }

  @Override
  public int countOfCommittedSharedDesiresByOtherAgents() {
    return manipulationWithAbstractDesiresFromOthers.intentionNodesByKey.size()
        + manipulationWithDesiresFromOthers.intentionNodesByKey.size();
  }

  @Override
  public int countOfSharedDesires() {
    return sharedDesiresForOtherAgents.size();
  }

  @Override
  public Map<DesireKey, Long> collectKeysOfCommittedDesiresInTreeCounts() {
    List<DesireKey> list = new ArrayList<>();
    List<IntentionNodeAtTopLevel<?, ?>> intentions = getNodesWithIntention();
    intentions
        .forEach(intentionNode -> intentionNode.collectKeysOfCommittedDesiresInSubtree(list));
    return list.stream()
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
  }

  @Override
  public Map<DesireKey, Long> collectKeysOfDesiresInTreeCounts() {
    List<DesireKey> list = getNodesWithDesire().stream()
        .map(Node::getDesireKey)
        .collect(Collectors.toList());
    getNodesWithIntention()
        .forEach(intentionNode -> intentionNode.collectKeysOfDesiresInSubtree(list));
    return list.stream()
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
  }

  @Override
  public Set<DesireParameters> getParametersOfCommittedDesiresOnTopLevel() {
    return getNodesWithIntention().stream()
        .map(intentionNode -> intentionNode.desireParameters)
        .collect(Collectors.toSet());
  }

  @Override
  public Set<DesireParameters> getParametersOfDesiresOnTopLevel() {
    return getNodesWithDesire().stream()
        .map(desiresNode -> desiresNode.desireParameters)
        .collect(Collectors.toSet());
  }

  void replaceDesireByIntention(
      DesireNodeAtTopLevel.FromAnotherAgent.WithAbstractIntention desireNode,
      IntentionNodeAtTopLevel.WithAbstractPlan.FromAnotherAgent intentionNode) {
    manipulationWithAbstractDesiresFromOthers.replaceDesireByIntention(desireNode, intentionNode);
  }

  void replaceIntentionByDesire(
      IntentionNodeAtTopLevel.WithAbstractPlan.FromAnotherAgent intentionNode,
      DesireNodeAtTopLevel.FromAnotherAgent.WithAbstractIntention desireNode) {
    manipulationWithAbstractDesiresFromOthers.replaceIntentionByDesire(intentionNode, desireNode);
  }

  void replaceDesireByIntention(
      DesireNodeAtTopLevel.FromAnotherAgent.WithIntentionWithPlan desireNode,
      IntentionNodeAtTopLevel.WithCommand.FromAnotherAgent intentionNode) {
    manipulationWithDesiresFromOthers.replaceDesireByIntention(desireNode, intentionNode);
  }

  void replaceIntentionByDesire(IntentionNodeAtTopLevel.WithCommand.FromAnotherAgent
      intentionNode,
      DesireNodeAtTopLevel.FromAnotherAgent.WithIntentionWithPlan desireNode) {
    manipulationWithDesiresFromOthers.replaceIntentionByDesire(intentionNode, desireNode);
  }

  void replaceDesireByIntention(DesireNodeAtTopLevel.Own.WithReasoningCommand desireNode,
      IntentionNodeAtTopLevel.WithCommand.OwnReasoning intentionNode) {
    manipulatorWithReasoningCommands.replaceDesireByIntention(desireNode, intentionNode);
  }

  void replaceIntentionByDesire(IntentionNodeAtTopLevel.WithCommand.OwnReasoning intentionNode,
      DesireNodeAtTopLevel.Own.WithReasoningCommand desireNode) {
    manipulatorWithReasoningCommands.replaceIntentionByDesire(intentionNode, desireNode);
  }

  void replaceDesireByIntention(DesireNodeAtTopLevel.Own.WithActingCommand desireNode,
      IntentionNodeAtTopLevel.WithCommand.OwnActing intentionNode) {
    manipulatorWithActingCommands.replaceDesireByIntention(desireNode, intentionNode);
  }

  void replaceIntentionByDesire(IntentionNodeAtTopLevel.WithCommand.OwnActing intentionNode,
      DesireNodeAtTopLevel.Own.WithActingCommand desireNode) {
    manipulatorWithActingCommands.replaceIntentionByDesire(intentionNode, desireNode);
  }

  void replaceDesireByIntention(DesireNodeAtTopLevel.ForOthers desireNode,
      IntentionNodeAtTopLevel.WithDesireForOthers intentionNode) {
    manipulatorWithDesiresForOthers.replaceDesireByIntention(desireNode, intentionNode);
  }

  void replaceIntentionByDesire(IntentionNodeAtTopLevel.WithDesireForOthers intentionNode,
      DesireNodeAtTopLevel.ForOthers desireNode) {
    manipulatorWithDesiresForOthers.replaceIntentionByDesire(intentionNode, desireNode);
  }

  void replaceDesireByIntention(DesireNodeAtTopLevel.Own.WithAbstractIntention desireNode,
      IntentionNodeAtTopLevel.WithAbstractPlan.Own intentionNode) {
    manipulatorWithOwnAbstractPlan.replaceDesireByIntention(desireNode, intentionNode);
  }

  void replaceIntentionByDesire(IntentionNodeAtTopLevel.WithAbstractPlan.Own intentionNode,
      DesireNodeAtTopLevel.Own.WithAbstractIntention desireNode) {
    manipulatorWithOwnAbstractPlan.replaceIntentionByDesire(intentionNode, desireNode);
  }

  @Override
  public void receiveResponse(Boolean response) {
    //commitment to desires by other agents removed
  }

  /**
   * Class to register nodes with WithAbstractPlan.FromAnotherAgent
   */
  private class NodeManipulationWithAbstractDesiresFromOthers extends
      ChildNodeManipulation<IntentionNodeAtTopLevel.WithAbstractPlan.FromAnotherAgent, DesireNodeAtTopLevel.FromAnotherAgent.WithAbstractIntention> {

    /**
     * Removes shared desire and collect own shared desires in subtree to remove them from register
     * later
     */
    private Set<SharedDesireForAgents> removeSharedDesireAndCollectOwnSharedDesiresInSubtreeToRemove(
        SharedDesireForAgents sharedDesireForAgents) {
      if (desiresNodesByKey.remove(sharedDesireForAgents.getDesireParameters()) != null) {
        removeSharedDesireForOtherAgents(sharedDesireForAgents);
      } else {
        IntentionNodeAtTopLevel.WithAbstractPlan.FromAnotherAgent intentionNode = intentionNodesByKey
            .remove(sharedDesireForAgents.getDesireParameters());
        if (intentionNode != null) {
          Set<SharedDesireForAgents> sharedDesires = new HashSet<>();
          intentionNode.collectSharedDesiresForOtherAgentsInSubtree(sharedDesires);
          intentionNode.actOnRemoval();
          return sharedDesires;
        }
      }
      return new HashSet<>();
    }

  }

  /**
   * Class to register nodes with WithAbstractPlan.FromAnotherAgent
   */
  private class NodeManipulationWithDesiresFromOthers extends
      ChildNodeManipulation<IntentionNodeAtTopLevel.WithCommand.FromAnotherAgent, DesireNodeAtTopLevel.FromAnotherAgent.WithIntentionWithPlan> {

    /**
     * Removes shared desire
     */
    private boolean removeSharedDesire(SharedDesireForAgents sharedDesireForAgents) {
      return desiresNodesByKey.remove(sharedDesireForAgents.getDesireParameters()) != null
          || intentionNodesByKey.remove(sharedDesireForAgents.getDesireParameters()) != null;
    }

  }
}
