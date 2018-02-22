package aic.gas.sc.gg_bot.replay_parser.model.watcher;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.State;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.Trajectory;
import aic.gas.sc.gg_bot.replay_parser.service.IWatcherMediatorService;
import java.util.Set;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Class PlanWatcher track agent commitment to desire given beliefs
 */
@Slf4j
public abstract class PlanWatcher {

  private final FeatureContainer container;
  @Getter
  private final Trajectory trajectory;
  @Getter
  private final DesireKeys desireKey;
  private double[] currentState;
  @Getter
  private boolean isCommitted = false;

  protected PlanWatcher(
      IFeatureContainerInitializationStrategy featureContainerInitializationStrategy,
      DesireKeys desireKey) {
    this.container = featureContainerInitializationStrategy.returnFeatureContainer();
    this.desireKey = desireKey;
    this.trajectory = new Trajectory(this.container.getNumberOfFeatures());
    this.currentState = this.container.getFeatureVector();
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
  protected abstract boolean isAgentCommitted(IWatcherMediatorService mediatorService,
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
      IWatcherMediatorService mediatorService, Set<Integer> committedToIDs) {
    boolean hasStatusChanged = false;
    if (isCommitted != isAgentCommitted(mediatorService, beliefs)) {
      isCommitted = !isCommitted;

      //notify other agents that this one is committed to their desire
      streamOfAgentsToNotifyAboutCommitment().forEach(agentWatcher -> agentWatcher
          .commitmentByOtherAgentToDesireOfThisAgentHasBeenChanged(isCommitted, desireKey));

      hasStatusChanged = true;
    }
    if (container.isStatusUpdated(beliefs, mediatorService, committedToIDs) || hasStatusChanged) {
      trajectory.addNewState(new State(currentState, isCommitted));
    }
    currentState = container.getFeatureVector();
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
  public interface IFeatureContainerInitializationStrategy {

    FeatureContainer returnFeatureContainer();
  }
}
