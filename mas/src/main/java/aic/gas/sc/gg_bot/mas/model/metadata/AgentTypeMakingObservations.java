package aic.gas.sc.gg_bot.mas.model.metadata;

import aic.gas.sc.gg_bot.mas.model.planing.command.ObservingCommand;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

/**
 * Extension of type. It also defines command to make observation
 */
public class AgentTypeMakingObservations<E> extends AgentType {

  @Getter
  private final int skipTurnsToMakeObservation;
  @Getter
  private ObservingCommand<E> observingCommand;

  /**
   * Define agent type. Together with initial desires
   */
  protected AgentTypeMakingObservations(AgentTypeID agentTypeID, Set<DesireKey> desiresForOthers,
      Set<DesireKey> desiresWithAbstractIntention,
      Set<DesireKey> desiresWithIntentionToAct, Set<DesireKey> desiresWithIntentionToReason,
      Set<FactKey<?>> usingTypesForFacts, Set<FactKey<?>> usingTypesForFactSets,
      ConfigurationInitializationStrategy initializationStrategy,
      ObservingCommand<E> observingCommand, int skipTurnsToMakeObservation) {
    super(agentTypeID, desiresForOthers, desiresWithAbstractIntention, desiresWithIntentionToAct,
        desiresWithIntentionToReason,
        usingTypesForFacts, usingTypesForFactSets, initializationStrategy);
    this.observingCommand = observingCommand;
    this.skipTurnsToMakeObservation = skipTurnsToMakeObservation;
  }

  //builder with default fields
  public static class AgentTypeMakingObservationsBuilder extends AgentTypeBuilder {

    private Set<DesireKey> desiresForOthers = new HashSet<>();
    private Set<DesireKey> desiresWithAbstractIntention = new HashSet<>();
    private Set<DesireKey> desiresWithIntentionToAct = new HashSet<>();
    private Set<DesireKey> desiresWithIntentionToReason = new HashSet<>();
    private Set<FactKey<?>> usingTypesForFacts = new HashSet<>();
    private Set<FactKey<?>> usingTypesForFactSets = new HashSet<>();
    private int skipTurnsToMakeObservation = 5;
  }
}
