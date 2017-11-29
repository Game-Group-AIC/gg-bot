package aic.gas.sc.gg_bot.model.agent.types;

import static aic.gas.abstract_bot.model.bot.FactKeys.ENEMY_AIR;
import static aic.gas.abstract_bot.model.bot.FactKeys.ENEMY_BUILDING;
import static aic.gas.abstract_bot.model.bot.FactKeys.ENEMY_GROUND;
import static aic.gas.abstract_bot.model.bot.FactKeys.IS_BEING_CONSTRUCT;
import static aic.gas.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;
import static aic.gas.abstract_bot.model.bot.FactKeys.IS_UNDER_ATTACK;
import static aic.gas.abstract_bot.model.bot.FactKeys.IS_UNIT;
import static aic.gas.abstract_bot.model.bot.FactKeys.LOCATION;
import static aic.gas.abstract_bot.model.bot.FactKeys.MADE_OBSERVATION_IN_FRAME;
import static aic.gas.abstract_bot.model.bot.FactKeys.OWN_AIR;
import static aic.gas.abstract_bot.model.bot.FactKeys.OWN_BUILDING;
import static aic.gas.abstract_bot.model.bot.FactKeys.OWN_GROUND;
import static aic.gas.abstract_bot.model.bot.FactKeys.PLACE_TO_REACH;
import static aic.gas.abstract_bot.model.bot.FactKeys.REPRESENTS_UNIT;
import static aic.gas.abstract_bot.model.bot.FactKeys.TIME_OF_HOLD_COMMAND;

import aic.gas.abstract_bot.model.bot.FactConverters;
import aic.gas.abstract_bot.model.game.wrappers.AUnitOfPlayer;
import aic.gas.abstract_bot.model.game.wrappers.AUnitWithCommands;
import aic.gas.mas.model.knowledge.WorkingMemory;
import aic.gas.mas.model.metadata.AgentType;
import aic.gas.mas.model.metadata.AgentTypeID;
import aic.gas.mas.model.metadata.AgentTypeMakingObservations;
import aic.gas.mas.model.metadata.DesireKey;
import aic.gas.mas.model.metadata.FactKey;
import aic.gas.mas.model.metadata.agents.configuration.ConfigurationWithCommand;
import aic.gas.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.mas.model.planing.command.ObservingCommand;
import aic.gas.mas.model.planing.command.ReasoningCommand;
import aic.gas.mas.utils.MyLogger;
import bwapi.Game;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;

/**
 * Type definition - agent type for unit observing game
 */
public class AgentTypeUnit extends AgentTypeMakingObservations<Game> {

  //single definition of reasoning command to update beliefs about surrounding units and location
  public static final ConfigurationWithCommand.WithReasoningCommandDesiredBySelf beliefsAboutSurroundingUnitsAndLocation = ConfigurationWithCommand.
      WithReasoningCommandDesiredBySelf.builder()
      .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
        @Override
        public boolean act(WorkingMemory memory) {
          AUnitOfPlayer unit = memory.returnFactValueForGivenKey(REPRESENTS_UNIT).get();

          //enemies
          memory.updateFactSetByFacts(ENEMY_BUILDING, unit.getEnemyUnitsInRadiusOfSight().stream()
              .filter(enemy -> enemy.getType().isBuilding()).collect(Collectors.toSet()));
          memory.updateFactSetByFacts(ENEMY_GROUND, unit.getEnemyUnitsInRadiusOfSight().stream()
              .filter(enemy -> !enemy.getType().isBuilding() && !enemy.getType().isFlyer())
              .collect(Collectors.toSet()));
          memory.updateFactSetByFacts(ENEMY_AIR, unit.getEnemyUnitsInRadiusOfSight().stream()
              .filter(enemy -> !enemy.getType().isBuilding() && enemy.getType().isFlyer())
              .collect(Collectors.toSet()));

          //friendlies
          memory.updateFactSetByFacts(OWN_BUILDING, unit.getFriendlyUnitsInRadiusOfSight().stream()
              .filter(own -> own.getType().isBuilding()).collect(Collectors.toSet()));
          memory.updateFactSetByFacts(OWN_GROUND, unit.getFriendlyUnitsInRadiusOfSight().stream()
              .filter(own -> !own.getType().isBuilding() && !own.getType().isFlyer())
              .collect(Collectors.toSet()));
          memory.updateFactSetByFacts(OWN_AIR, unit.getFriendlyUnitsInRadiusOfSight().stream()
              .filter(own -> !own.getType().isBuilding() && own.getType().isFlyer())
              .collect(Collectors.toSet()));

          memory
              .updateFact(LOCATION, unit.getNearestBaseLocation().orElse(LOCATION.getInitValue()));

          return true;
        }
      })
      .decisionInDesire(CommitmentDeciderInitializer.builder()
          .decisionStrategy((dataForDecision, memory) -> true)
          .build())
      .decisionInIntention(CommitmentDeciderInitializer.builder()
          .decisionStrategy((dataForDecision, memory) -> true)
          .build())
      .build();

  //single definition of reasoning command to update beliefs about being under construction
  public static final ConfigurationWithCommand.WithReasoningCommandDesiredBySelf beliefsAboutConstruction = ConfigurationWithCommand.
      WithReasoningCommandDesiredBySelf.builder()
      .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
        @Override
        public boolean act(WorkingMemory memory) {
          return true;
        }
      })
      .reactionOnChangeStrategy((memory, desireParameters) -> {
        memory.updateFact(IS_BEING_CONSTRUCT, true);
      })
      .reactionOnChangeStrategyInIntention((memory, desireParameters) -> {
        memory.updateFact(IS_BEING_CONSTRUCT, false);
      })
      .decisionInDesire(CommitmentDeciderInitializer.builder()
          .decisionStrategy((dataForDecision, memory) ->
              dataForDecision.getFeatureValueBeliefs(FactConverters.IS_BEING_CONSTRUCT) == 1)
          .beliefTypes(new HashSet<>(Collections.singleton(FactConverters.IS_BEING_CONSTRUCT)))
          .build())
      .decisionInIntention(CommitmentDeciderInitializer.builder()
          .decisionStrategy((dataForDecision, memory) ->
              dataForDecision.getFeatureValueBeliefs(FactConverters.IS_BEING_CONSTRUCT) == 0)
          .beliefTypes(new HashSet<>(Collections.singleton(FactConverters.IS_BEING_CONSTRUCT)))
          .build())
      .build();

  //single definition of reasoning command to update beliefs about morphing to
  public static final ConfigurationWithCommand.WithReasoningCommandDesiredBySelf beliefsAboutMorphing = ConfigurationWithCommand.
      WithReasoningCommandDesiredBySelf.builder()
      .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
        @Override
        public boolean act(WorkingMemory memory) {
          return true;
        }
      })
      .reactionOnChangeStrategy((memory, desireParameters) -> {
        AUnitOfPlayer me = memory.returnFactValueForGivenKey(REPRESENTS_UNIT).get();
        if (!me.getTrainingQueue().isEmpty()) {
          memory.updateFact(IS_MORPHING_TO, me.getTrainingQueue().get(0));
        } else {
          memory.updateFact(IS_MORPHING_TO, me.getType());
        }
      })
      .reactionOnChangeStrategyInIntention((memory, desireParameters) -> {
        memory.eraseFactValueForGivenKey(IS_MORPHING_TO);
      })
      .decisionInDesire(CommitmentDeciderInitializer.builder()
          .decisionStrategy((dataForDecision, memory) ->
              dataForDecision.getFeatureValueBeliefs(FactConverters.IS_TRAINING_QUEUE_EMPTY) == 0
                  || dataForDecision.getFeatureValueBeliefs(FactConverters.IS_MORPHING) == 1)
          .beliefTypes(new HashSet<>(
              Arrays.asList(FactConverters.IS_TRAINING_QUEUE_EMPTY, FactConverters.IS_MORPHING)))
          .build())
      .decisionInIntention(CommitmentDeciderInitializer.builder()
          .decisionStrategy((dataForDecision, memory) ->
              dataForDecision.getFeatureValueBeliefs(FactConverters.IS_TRAINING_QUEUE_EMPTY) == 1
                  || dataForDecision.getFeatureValueBeliefs(FactConverters.IS_MORPHING) == 0)
          .beliefTypes(new HashSet<>(
              Arrays.asList(FactConverters.IS_TRAINING_QUEUE_EMPTY, FactConverters.IS_MORPHING)))
          .build())
      .build();

  //single definition of command to observe to be used by all agents of this type
  private static final ObservingCommand<Game> OBSERVING_COMMAND = (memory, environment) -> {
    Optional<AUnitWithCommands> unitWithCommands = memory.returnFactValueForGivenKey(IS_UNIT);
    if (!unitWithCommands.isPresent()) {
      MyLogger.getLogger().warning("Trying to access commendable unit but it is not present.");
      throw new RuntimeException("Trying to access commendable unit but it is not present.");
    }

    //update fields by creating new instance
    AUnitWithCommands unit = unitWithCommands.get()
        .makeObservationOfEnvironment(environment.getFrameCount());

    //add updated version of itself to knowledge
    memory.updateFact(IS_UNIT, unit);
    memory.updateFact(REPRESENTS_UNIT, unit);
    memory.updateFact(MADE_OBSERVATION_IN_FRAME, environment.getFrameCount());
    return true;
  };

  /**
   * Define agent type. Together with initial desires
   */
  @Builder
  private AgentTypeUnit(AgentTypeID agentTypeID, Set<DesireKey> desiresForOthers,
      Set<DesireKey> desiresWithAbstractIntention,
      Set<DesireKey> desiresWithIntentionToAct, Set<DesireKey> desiresWithIntentionToReason,
      Set<FactKey<?>> usingTypesForFacts, Set<FactKey<?>> usingTypesForFactSets,
      AgentType.ConfigurationInitializationStrategy initializationStrategy,
      int skipTurnsToMakeObservation) {
    super(agentTypeID, desiresForOthers, desiresWithAbstractIntention, desiresWithIntentionToAct,
        desiresWithIntentionToReason,

        //add facts related to agent - IS_UNIT, REPRESENTS_UNIT
        Stream.concat(usingTypesForFacts.stream(),
            Arrays.stream(new FactKey<?>[]{IS_UNIT, REPRESENTS_UNIT,
                MADE_OBSERVATION_IN_FRAME, LOCATION, IS_UNDER_ATTACK, PLACE_TO_REACH,
                TIME_OF_HOLD_COMMAND})).collect(Collectors.toSet()),
        Stream.concat(usingTypesForFactSets.stream(),
            Arrays.stream(new FactKey<?>[]{ENEMY_BUILDING, ENEMY_AIR,
                ENEMY_GROUND, OWN_BUILDING, OWN_AIR, OWN_GROUND})).collect(Collectors.toSet()),
        initializationStrategy, OBSERVING_COMMAND, skipTurnsToMakeObservation);
  }

  //builder with default fields
  public static class AgentTypeUnitBuilder extends AgentTypeMakingObservationsBuilder {

    private Set<DesireKey> desiresForOthers = new HashSet<>();
    private Set<DesireKey> desiresWithAbstractIntention = new HashSet<>();
    private Set<DesireKey> desiresWithIntentionToAct = new HashSet<>();
    private Set<DesireKey> desiresWithIntentionToReason = new HashSet<>();
    private Set<FactKey<?>> usingTypesForFacts = new HashSet<>();
    private Set<FactKey<?>> usingTypesForFactSets = new HashSet<>();
    private int skipTurnsToMakeObservation = 5;
  }
}
