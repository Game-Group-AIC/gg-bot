package aic.gas.sc.gg_bot.mas.model.planing;

import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import java.util.Set;

/**
 * Class for intention with abstract plan - it defines sets of other desires to commit to
 */
public class AbstractIntention<T extends InternalDesire<?>> extends Intention<T> {

  private final Set<DesireKey> desiresForOthers;
  private final Set<DesireKey> desiresWithAbstractIntention;
  private final Set<DesireKey> desiresWithIntentionToAct;
  private final Set<DesireKey> desiresWithIntentionToReason;

  AbstractIntention(T originalDesire, CommitmentDeciderInitializer removeCommitment,
      Set<DesireKey> desiresForOthers, Set<DesireKey> desiresWithAbstractIntention,
      Set<DesireKey> desiresWithIntentionToAct,
      Set<DesireKey> desiresWithIntentionToReason,
      IReactionOnChangeStrategy reactionOnChangeStrategy) {
    super(originalDesire, removeCommitment, reactionOnChangeStrategy);
    this.desiresForOthers = desiresForOthers;
    this.desiresWithAbstractIntention = desiresWithAbstractIntention;
    this.desiresWithIntentionToAct = desiresWithIntentionToAct;
    this.desiresWithIntentionToReason = desiresWithIntentionToReason;
  }

  /**
   * Returns plan as set of desires for others to commit to
   */
  public Set<DesireKey> returnPlanAsSetOfDesiresForOthers() {
    return desiresForOthers;
  }

  /**
   * Returns plan as set of own desires with abstract intention
   */
  public Set<DesireKey> returnPlanAsSetOfDesiresWithAbstractIntention() {
    return desiresWithAbstractIntention;
  }

  /**
   * Returns plan as set of own desires with intention with act command
   */
  public Set<DesireKey> returnPlanAsSetOfDesiresWithIntentionToAct() {
    return desiresWithIntentionToAct;
  }

  /**
   * Returns plan as set of own desires with intention with reason command
   */
  public Set<DesireKey> returnPlanAsSetOfDesiresWithIntentionToReason() {
    return desiresWithIntentionToReason;
  }
}
