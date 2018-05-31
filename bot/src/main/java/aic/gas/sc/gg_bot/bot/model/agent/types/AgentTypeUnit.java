package aic.gas.sc.gg_bot.bot.model.agent.types;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_AIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_BUILDING;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_GROUND;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_AIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_BUILDING;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_GROUND;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.PLACE_TO_REACH;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.REPRESENTS_UNIT;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APosition;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ATilePosition;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit.Enemy;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitOfPlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitWithCommands;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
import aic.gas.sc.gg_bot.mas.model.knowledge.Memory;
import aic.gas.sc.gg_bot.mas.model.knowledge.ReadOnlyMemory;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentType;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeMakingObservations;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithAbstractPlan;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithCommand;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import aic.gas.sc.gg_bot.mas.model.planing.command.ObservingCommand;
import aic.gas.sc.gg_bot.mas.model.planing.command.ReasoningCommand;
import bwapi.Game;
import bwapi.Position;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

//(\d)+-(\d)+-(\d)+ (\d)+:(\d)+:(\d)+ \[Thread-(\d)+\] INFO :: (\w)+, constructing: (\w)+, morphing: (\w)+(, in queue: (\w)+)*

/**
 * Type definition - agent type for unit observing game
 */
//TODO refactor
@Slf4j
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

          memory.updateFact(LOCATION, unit.getNearestBaseLocation()
              .orElse(LOCATION.getInitValue()));

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
      .reactionOnChangeStrategyInIntention(
          (memory, desireParameters) -> memory.eraseFactValueForGivenKey(IS_MORPHING_TO))
      .decisionInDesire(CommitmentDeciderInitializer.builder()
          .decisionStrategy((dataForDecision, memory) ->
              dataForDecision.getFeatureValueBeliefs(FactConverters.IS_MORPHING) == 1)
          .beliefTypes(Collections.singleton(FactConverters.IS_MORPHING))
          .build())
      .decisionInIntention(CommitmentDeciderInitializer.builder()
          .decisionStrategy((dataForDecision, memory) ->
              dataForDecision.getFeatureValueBeliefs(FactConverters.IS_MORPHING) == 0)
          .beliefTypes(Collections.singleton(FactConverters.IS_MORPHING))
          .build())
      .build();

  //single definition of command to observe to be used by all agents of this type
  private static final ObservingCommand<Game> OBSERVING_COMMAND = (memory, environment) -> {
    Optional<AUnitWithCommands> unitWithCommands = memory.returnFactValueForGivenKey(IS_UNIT);
    if (!unitWithCommands.isPresent()) {
      log.error("Trying to access commendable unit but it is not present.");
      throw new RuntimeException("Trying to access commendable unit but it is not present.");
    }

    //update fields by creating new instance
    AUnitWithCommands unit = unitWithCommands.get()
        .makeObservationOfEnvironment(environment.getFrameCount());

    //add updated version of itself to knowledge
    memory.updateFact(IS_UNIT, unit);
    memory.updateFact(REPRESENTS_UNIT, unit);
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
        Stream.concat(usingTypesForFacts.stream(), Stream.of(IS_UNIT, REPRESENTS_UNIT, LOCATION,
            PLACE_TO_REACH)).collect(Collectors.toSet()),
        Stream.concat(usingTypesForFactSets.stream(), Stream.of(ENEMY_BUILDING, ENEMY_AIR,
            ENEMY_GROUND, OWN_BUILDING, OWN_AIR, OWN_GROUND))
            .collect(Collectors.toSet()),
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
    private int skipTurnsToMakeObservation = 1;
  }

  public static void initAttackPlan(AgentType type, DesireKey desireKey,
      boolean isScaredOfAntiAir) {

    //attack
    ConfigurationWithAbstractPlan attackPlan = ConfigurationWithAbstractPlan.builder()
        .reactionOnChangeStrategy((memory, desireParameters) -> memory.updateFact(PLACE_TO_REACH,
            desireParameters.returnFactValueForGivenKey(IS_BASE_LOCATION).get()))
        .reactionOnChangeStrategyInIntention((memory, desireParameters) ->
            memory.eraseFactValueForGivenKey(PLACE_TO_REACH))
        .decisionInDesire(CommitmentDeciderInitializer.builder()
            .decisionStrategy((dataForDecision, memory) -> !dataForDecision.madeDecisionToAny())
            .desiresToConsider(Collections.singleton(desireKey))
            .build())
        .decisionInIntention(CommitmentDeciderInitializer.builder()
            .decisionStrategy((dataForDecision, memory) -> false)
            .build())
        .desiresWithIntentionToAct(new HashSet<>(Arrays.asList(DesiresKeys.MOVE_AWAY_FROM_DANGER,
            DesiresKeys.MOVE_TO_POSITION, DesiresKeys.ATTACK)))
        .build();
    type.addConfiguration(desireKey, attackPlan, false);

    //if is in danger - select the closest unit (based on type) and move away from it
    ConfigurationWithCommand.WithActingCommandDesiredBySelf flee = ConfigurationWithCommand.WithActingCommandDesiredBySelf
        .builder()
        .decisionInDesire(CommitmentDeciderInitializer.builder()
            .decisionStrategy((dataForDecision, memory) -> {
              AUnitOfPlayer me = memory.returnFactValueForGivenKey(IS_UNIT).get();
              return me.isUnderAttack() && me.getHPPercent() < 0.2;
            })
            .build())
        .decisionInIntention(CommitmentDeciderInitializer.builder()
            .decisionStrategy((dataForDecision, memory) -> true)
            .build())
        .commandCreationStrategy(intention -> new ActCommand.Own(intention) {
          @Override
          public int getHash(WorkingMemory memory) {
            AUnitWithCommands me = intention.returnFactValueForGivenKey(IS_UNIT).get();
            Optional<Enemy> enemyToFleeFrom = me.getEnemyUnitsInRadiusOfSight().stream()
                .filter(enemy -> isScaredOfAntiAir ? enemy.getType().canAttackAirUnits()
                    : enemy.getType().canAttackGroundUnits())
                .min(Comparator.comparingDouble(
                    value -> value.getPosition().getATilePosition()
                        .distanceTo(me.getPosition().getATilePosition())));
            if (!enemyToFleeFrom.isPresent()) {
              enemyToFleeFrom = me.getEnemyUnitsInRadiusOfSight().stream()
                  .min(Comparator.comparingDouble(value -> value.getPosition().getATilePosition()
                      .distanceTo(me.getPosition().getATilePosition())));
            }
            APosition position = null;
            if (enemyToFleeFrom.isPresent()) {
              position = moveFromPosition(me.getPosition(), enemyToFleeFrom.get().getPosition());
            }
            return Objects.hash("MOVE", position);
          }

          @Override
          public boolean act(WorkingMemory memory) {
            AUnitWithCommands me = intention.returnFactValueForGivenKey(IS_UNIT).get();
            Optional<Enemy> enemyToFleeFrom = me.getEnemyUnitsInRadiusOfSight().stream()
                .filter(enemy -> isScaredOfAntiAir ? enemy.getType().canAttackAirUnits()
                    : enemy.getType().canAttackGroundUnits())
                .min(Comparator.comparingDouble(
                    value -> value.getPosition().getATilePosition().distanceTo(me
                        .getPosition().getATilePosition())));
            if (enemyToFleeFrom.isPresent()) {
              return me.move(moveFromPosition(me.getPosition(),
                  enemyToFleeFrom.get().getPosition()));
            } else {
              Optional<AUnit.Enemy> enemy = me.getEnemyUnitsInRadiusOfSight().stream()
                  .min(Comparator.comparingDouble(value -> value.getPosition().getATilePosition()
                      .distanceTo(me.getPosition().getATilePosition())));
              if (enemy.isPresent()) {
                return me.move(moveFromPosition(me.getPosition(), enemy.get().getPosition()));
              }
            }
            return true;
          }
        })
        .build();
    type.addConfiguration(DesiresKeys.MOVE_AWAY_FROM_DANGER, desireKey, flee);

    //if is not in danger - continue to position
    ConfigurationWithCommand.WithActingCommandDesiredBySelf moveOnPosition = ConfigurationWithCommand.WithActingCommandDesiredBySelf
        .builder()
        .decisionInDesire(CommitmentDeciderInitializer.builder()
            .decisionStrategy((dataForDecision, memory) -> !dataForDecision.madeDecisionToAny()
                && memory.returnFactValueForGivenKey(IS_UNIT).isPresent()
                && memory.returnFactValueForGivenKey(PLACE_TO_REACH).isPresent()
                && !memory.returnFactValueForGivenKey(IS_UNIT).get().isUnderAttack()
                && memory.returnFactValueForGivenKey(PLACE_TO_REACH).get().getTilePosition()
                .distanceTo(memory.returnFactValueForGivenKey(REPRESENTS_UNIT).get()
                    .getPosition().getATilePosition()) > 5 && hasEnoughForceToAttack(memory))
            .desiresToConsider(new HashSet<>(Collections.singleton(DesiresKeys.ATTACK)))
            .build())
        .decisionInIntention(CommitmentDeciderInitializer.builder()
            .decisionStrategy((dataForDecision, memory) -> true)
            .build())
        .commandCreationStrategy(intention -> new ActCommand.Own(intention) {
          @Override
          public int getHash(WorkingMemory memory) {
            return Objects.hash("ATTACK", memory
                .returnFactValueForGivenKey(PLACE_TO_REACH).get());
          }

          @Override
          public boolean act(WorkingMemory memory) {
            return memory.returnFactValueForGivenKey(IS_UNIT).get().attack(positionToGroup(memory));
          }
        })
        .build();
    type.addConfiguration(DesiresKeys.MOVE_TO_POSITION, desireKey, moveOnPosition);

    //attack on position
    ConfigurationWithCommand.WithActingCommandDesiredBySelf attack = ConfigurationWithCommand.WithActingCommandDesiredBySelf
        .builder()
        .decisionInDesire(CommitmentDeciderInitializer.builder()
            .decisionStrategy((dataForDecision, memory) -> memory
                .returnFactValueForGivenKey(IS_UNIT).isPresent() && !memory
                .returnFactValueForGivenKey(IS_UNIT).get().isUnderAttack())
            .build())
        .decisionInIntention(CommitmentDeciderInitializer.builder()
            .decisionStrategy((dataForDecision, memory) -> true)
            .build())
        .commandCreationStrategy(intention -> new ActCommand.Own(intention) {
          @Override
          public int getHash(WorkingMemory memory) {
            AUnitWithCommands unitOfPlayer = memory.returnFactValueForGivenKey(IS_UNIT).get();
            Optional<AUnit.Enemy> enemy = unitOfPlayer.getEnemyUnitsInRadiusOfSight().stream()
                .filter(AUnit::isAttacking)
                .min(Comparator.comparingDouble(value -> value.getPosition().getATilePosition()
                    .distanceTo(unitOfPlayer.getPosition().getATilePosition())));
            if (!enemy.isPresent()) {

              //attack units on position
              Optional<ReadOnlyMemory> base = memory
                  .getReadOnlyMemoriesForAgentType(AgentTypes.BASE_LOCATION)
                  .min(Comparator.comparingDouble(value -> value.returnFactValueForGivenKey(
                      IS_BASE_LOCATION).get().getTilePosition().distanceTo(unitOfPlayer
                      .getPosition().getATilePosition())));
              if (base.isPresent()) {
                enemy = base.get().returnFactSetValueForGivenKey(ENEMY_UNIT).orElse(Stream.empty())
                    .filter(AUnit::isAttacking)
                    .min(Comparator.comparingDouble(value -> value.getPosition().getATilePosition()
                        .distanceTo(unitOfPlayer.getPosition().getATilePosition())));
                if (!enemy.isPresent()) {
                  enemy = base.get().returnFactSetValueForGivenKey(ENEMY_UNIT)
                      .orElse(Stream.empty())
                      .min(Comparator.comparingDouble(value -> value.getPosition()
                          .getATilePosition().distanceTo(unitOfPlayer.getPosition()
                              .getATilePosition())));
                  enemy.ifPresent(unitOfPlayer::attack);
                }
              }
            }
            return Objects.hash("ATTACK", enemy.map(AUnit::getUnitId).orElse(0));
          }

          @Override
          public boolean act(WorkingMemory memory) {
            //select closest enemy
            AUnitWithCommands unitOfPlayer = memory.returnFactValueForGivenKey(IS_UNIT).get();

            Optional<AUnit.Enemy> enemy = unitOfPlayer.getEnemyUnitsInRadiusOfSight().stream()
                .filter(AUnit::isAttacking)
                .min(Comparator.comparingDouble(value -> value.getPosition().getATilePosition()
                    .distanceTo(unitOfPlayer.getPosition().getATilePosition())));
            if (enemy.isPresent()) {
              unitOfPlayer.attack(enemy.get());
            } else {

              //attack units on position
              Optional<ReadOnlyMemory> base = memory.getReadOnlyMemoriesForAgentType(
                  AgentTypes.BASE_LOCATION)
                  .min(Comparator.comparingDouble(value -> value.returnFactValueForGivenKey(
                      IS_BASE_LOCATION).get().getTilePosition()
                      .distanceTo(unitOfPlayer.getPosition().getATilePosition())));
              if (base.isPresent()) {
                enemy = base.get().returnFactSetValueForGivenKey(ENEMY_UNIT).orElse(Stream.empty())
                    .filter(AUnit::isAttacking)
                    .min(Comparator.comparingDouble(value -> value.getPosition().getATilePosition()
                        .distanceTo(unitOfPlayer.getPosition().getATilePosition())));
                if (enemy.isPresent()) {
                  unitOfPlayer.attack(enemy.get());
                } else {
                  enemy = base.get().returnFactSetValueForGivenKey(ENEMY_UNIT)
                      .orElse(Stream.empty())
                      .min(Comparator.comparingDouble(value -> value.getPosition()
                          .getATilePosition().distanceTo(unitOfPlayer.getPosition()
                              .getATilePosition())));
                  enemy.ifPresent(unitOfPlayer::attack);
                }
              }
            }
            return true;
          }
        })
        .build();
    type.addConfiguration(DesiresKeys.ATTACK, desireKey, attack);
  }

  //TODO simple heuristic
  private static APosition positionToGroup(Memory<?> memory) {
    ABaseLocationWrapper toGo = memory.returnFactValueForGivenKey(PLACE_TO_REACH).get();
    AUnitWithCommands me = memory.returnFactValueForGivenKey(IS_UNIT).get();

    List<APosition> positions = memory.getReadOnlyMemories()
        .filter(readOnlyMemory -> readOnlyMemory.isFactKeyForValueInMemory(PLACE_TO_REACH))
        .filter(readOnlyMemory -> toGo.equals(readOnlyMemory
            .returnFactValueForGivenKey(PLACE_TO_REACH).orElse(null)))
        .filter(readOnlyMemory -> readOnlyMemory.isFactKeyForValueInMemory(REPRESENTS_UNIT))
        .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(REPRESENTS_UNIT).get())
        .filter(aUnitOfPlayer -> aUnitOfPlayer.isFlying() == me.isFlying())
        .filter(aUnitOfPlayer -> !aUnitOfPlayer.getType().isWorker()
            && !aUnitOfPlayer.getType().equals(AUnitTypeWrapper.OVERLORD_TYPE))
        .map(AUnit::getPosition)
        .sorted(Comparator.comparingDouble(value -> toGo.getTilePosition().distanceTo(value
            .getATilePosition())))
        .collect(Collectors.toList());

    //median
    APosition position = positions.isEmpty() ? me.getPosition() :
        positions.get(positions.size() / 2);

    //is in center - continue...
    return position.getATilePosition().distanceTo(me.getPosition().getATilePosition()) <= 5 ?
        toGo.getPosition() : position;
  }

  private static boolean hasEnoughForceToAttack(Memory<?> memory) {
    ABaseLocationWrapper toGo = memory.returnFactValueForGivenKey(PLACE_TO_REACH).get();
    AUnitWithCommands me = memory.returnFactValueForGivenKey(IS_UNIT).get();
    return memory.getReadOnlyMemories()
        .filter(readOnlyMemory -> readOnlyMemory.isFactKeyForValueInMemory(PLACE_TO_REACH))
        .filter(readOnlyMemory -> toGo.equals(readOnlyMemory
            .returnFactValueForGivenKey(PLACE_TO_REACH).orElse(null)))
        .filter(readOnlyMemory -> readOnlyMemory.isFactKeyForValueInMemory(REPRESENTS_UNIT))
        .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(REPRESENTS_UNIT).get())
        .filter(aUnitOfPlayer -> !aUnitOfPlayer.getType().isWorker()
            && !aUnitOfPlayer.getType().equals(AUnitTypeWrapper.OVERLORD_TYPE))
        .filter(aUnitOfPlayer -> aUnitOfPlayer.isFlying() == me.isFlying())
        .count() >= (me.isFlying() ? 2 : 4);
  }

  public static APosition moveFromPosition(APosition myPosition, APosition dangerPosition) {
    int difX = (myPosition.getATilePosition().getX() - dangerPosition.getATilePosition().getY()) * 4
        * ATilePosition.SIZE_IN_PIXELS, difY = (myPosition.getATilePosition().getY() -
        dangerPosition.getATilePosition().getY()) * 4 * ATilePosition.SIZE_IN_PIXELS;
    if (difX > 0) {
      if (difY > 0) {
        return APosition.wrap(new Position(myPosition.getX() - difX, myPosition.getY() - difY));
      } else {
        return APosition.wrap(new Position(myPosition.getX() - difX, myPosition.getY() + difY));
      }
    } else {
      if (difY > 0) {
        return APosition.wrap(new Position(myPosition.getX() + difX, myPosition.getY() - difY));
      } else {
        return APosition.wrap(new Position(myPosition.getX() + difX, myPosition.getY() + difY));
      }
    }
  }
}
