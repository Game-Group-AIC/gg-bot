package aic.gas.sc.gg_bot.mas.service;

import aic.gas.sc.gg_bot.mas.model.CycleSynchronizationObtainingStrategy;
import aic.gas.sc.gg_bot.mas.model.InternalClockObtainingStrategy;
import aic.gas.sc.gg_bot.mas.model.agents.Agent;
import aic.gas.sc.gg_bot.mas.model.planing.command.ReasoningCommand;
import aic.gas.sc.gg_bot.mas.service.implementation.AgentsRegister;
import aic.gas.sc.gg_bot.mas.service.implementation.BeliefMediator;
import aic.gas.sc.gg_bot.mas.service.implementation.DesireMediator;
import com.rits.cloning.Cloner;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Facade for framework. It keeps useful references as well as declaration of common data
 * structures
 */
@Slf4j
public class MASFacade implements TerminableService {

  //for cloning data
  public static final Cloner CLONER = new Cloner();
  //instance of reasoning manager, it can be shared by agents as it is stateless
  public static final CommandManager<ReasoningCommand> REASONING_EXECUTOR = new CommandManager<ReasoningCommand>() {
  };
  //framework timing configuration...
  @Setter
  @Getter
  public static int lengthOfIntervalBeforeUpdatingRegisterWithDesires = 100;
  @Setter
  public static int howManyCyclesStayAgentsMemoryInRegisterWithoutUpdate = 100;
  @Setter
  @Getter
  public static int lengthOfIntervalBeforeUpdatingRegisterWithMemory = 100;

  @Getter
  private final InternalClockObtainingStrategy clockObtainingStrategy;

  //register of agents - to assign ids to them
  @Getter
  private final AgentsRegister agentsRegister = new AgentsRegister();

  //shared desire mediator
  @Getter
  private final DesireMediator desireMediator = new DesireMediator();

  //shared knowledge mediator
  @Getter
  private final BeliefMediator beliefMediator = new BeliefMediator();

  private final Set<Agent> agentsInSystem = new HashSet<>();

  //TODO HACK - to prevent executing more agents' cycles per frame
  @Getter
  private final CycleSynchronizationObtainingStrategy cycleSynchronizationObtainingStrategy;

  public MASFacade(InternalClockObtainingStrategy clockObtainingStrategy,
      boolean cycleSynchronization) {
    this.clockObtainingStrategy = clockObtainingStrategy;
    this.cycleSynchronizationObtainingStrategy = () -> cycleSynchronization;
  }

  public void notifyAgentsAboutNextCycle() {
    if (cycleSynchronizationObtainingStrategy.areCyclesSynchronized()) {
      synchronized (agentsInSystem) {
        agentsInSystem.forEach(Agent::notifyOnNextCycle);
      }
    }
  }

  public int getInternalClockCounter() {
    return clockObtainingStrategy.internalClockCounter();
  }

  /**
   * Register agent in system
   */
  public void addAgentToSystem(Agent agent) {
    synchronized (agentsInSystem) {
      agentsInSystem.add(agent);
      agent.run();
    }
  }

  /**
   * Unregister agent from system
   */
  public void removeAgentFromSystem(Agent agent, boolean removeAgentFromGlobalBeliefs) {
    synchronized (agentsInSystem) {
      if (agentsInSystem.remove(agent)) {
        agent.terminateAgent(removeAgentFromGlobalBeliefs);
      } else {
        log.error("Agent is not registered in system.");
        throw new IllegalArgumentException("Agent is not registered in system.");
      }
    }
  }

  public void terminate() {
    synchronized (agentsInSystem) {
      agentsInSystem.forEach(agent -> agent.terminateAgent(true));
    }
    notifyAgentsAboutNextCycle();
    desireMediator.terminate();
    beliefMediator.terminate();
  }

}