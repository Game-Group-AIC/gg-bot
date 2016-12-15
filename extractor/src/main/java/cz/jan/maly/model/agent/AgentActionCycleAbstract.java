package cz.jan.maly.model.agent;

import java.util.Optional;
import java.util.Set;

/**
 * Abstract class to be implemented by each action agent takes during his run cycle.
 * Created by Jan on 14-Dec-16.
 */
public abstract class AgentActionCycleAbstract {
    protected final Agent agent;

    protected AgentActionCycleAbstract(Agent agent) {
        this.agent = agent;
        if (!returnWorkflowExecutionBackToAgent()){
            throw new IllegalStateException("Each agent's workflow has to make sure that execution is return back to agent at the end of workflow.");
        }
    }

    /**
     * Method to be called when agent should act. Executed action depend on implementation. It returns optional of next possible action to be taken by agent.
     * Its behaviour may differ based on notification from other agents - this can be primary used to start over execution cycle
     * @param agentsSentNotification
     * @return
     */
    public abstract Optional<AgentActionCycleAbstract> executeAction(Set<Agent> agentsSentNotification);

    public abstract boolean returnWorkflowExecutionBackToAgent();

    /**
     * Return whatever this action nis leaf (end of the path)
     * @return
     */
    public abstract boolean isLeaf();

}