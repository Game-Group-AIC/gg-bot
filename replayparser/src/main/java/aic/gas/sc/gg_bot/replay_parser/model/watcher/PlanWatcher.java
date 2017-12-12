package aic.gas.sc.gg_bot.replay_parser.model.watcher;

import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.State;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.Trajectory;
import aic.gas.sc.gg_bot.replay_parser.service.WatcherMediatorService;
import java.util.Set;
import java.util.stream.Stream;
import lombok.Getter;

/**
 * Class PlanWatcher track agent commitment to desire given beliefs
 */
public abstract class PlanWatcher {

  private final FeatureContainer container;
  @Getter
  private final Trajectory trajectory;
  @Getter
  private final DesireKeyID desireKey;
  private double[] currentState;
  @Getter
  private boolean isCommitted = false;

  protected PlanWatcher(
      FeatureContainerInitializationStrategy featureContainerInitializationStrategy,
      DesireKeyID desireKey) {
    this.container = featureContainerInitializationStrategy.returnFeatureContainer();
    this.desireKey = desireKey;
    this.trajectory = new Trajectory(this.container.getNumberOfFeatures());
    this.currentState = this.container.getFeatureVector().clone();
  }

  public void addCommitment() {
    container.addCommitment();
  }

  public void removeCommitment() {
    container.removeCommitment();
  }

  /**
   * Decide if agent is committed based on handcrafted rules
   */
  protected abstract boolean isAgentCommitted(WatcherMediatorService mediatorService,
      Beliefs beliefs);

  /**
   * Get stream of agents to notify when agent has changed commitment to this plan
   */
  protected abstract Stream<AgentWatcher<?>> streamOfAgentsToNotifyAboutCommitment();

  /**
   * If agent transit to new state (either feature vector has changed or commitment) add new state
   * to trajectory
   */
  public void addNewStateIfAgentHasTransitedToOne(Beliefs beliefs,
      WatcherMediatorService mediatorService, Set<Integer> committedToIDs) {
    double[] currentFeatureState = container.getFeatureVector().clone();
    boolean hasStatusChanged = false;
    if (isCommitted != isAgentCommitted(mediatorService, beliefs)) {
      isCommitted = !isCommitted;

      //notify other agents that this one is commited to their desire
      streamOfAgentsToNotifyAboutCommitment().forEach(agentWatcher -> agentWatcher
          .commitmentByOtherAgentToDesireOfThisAgentHasBeenChanged(isCommitted, desireKey));

      hasStatusChanged = true;
    }
    if (container.isStatusUpdated(beliefs, mediatorService, committedToIDs) || hasStatusChanged) {
      trajectory.addNewState(new State(currentState, isCommitted));
    }
    currentState = currentFeatureState;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PlanWatcher that = (PlanWatcher) o;

    return desireKey.equals(that.desireKey);
  }

  @Override
  public int hashCode() {
    return desireKey.hashCode();
  }

  /**
   * Create instance of FeatureContainer
   */
  public interface FeatureContainerInitializationStrategy {

    FeatureContainer returnFeatureContainer();
  }
}
