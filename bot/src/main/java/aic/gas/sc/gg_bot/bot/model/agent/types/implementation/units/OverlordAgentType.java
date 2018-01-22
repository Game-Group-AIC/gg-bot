package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.units;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_MOVE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.PLACE_TO_REACH;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.REPRESENTS_UNIT;
import static aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit.positionToMove;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitOfPlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitWithCommands;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithAbstractPlan;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithCommand;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;

//TODO refactor
public class OverlordAgentType {

  public static final AgentTypeUnit OVERLORD = AgentTypeUnit.builder()
      .agentTypeID(AgentTypes.OVERLORD)
      .usingTypesForFacts(new HashSet<>(Arrays.asList(IS_MORPHING_TO, BASE_TO_MOVE)))
      .initializationStrategy(type -> {
        type.addConfiguration(
            DesiresKeys.SURROUNDING_UNITS_AND_LOCATION,
            AgentTypeUnit.beliefsAboutSurroundingUnitsAndLocation);

        //scouting - move to base scouted for last time. at start prefer unvisited base locations
        ConfigurationWithAbstractPlan goScouting = ConfigurationWithAbstractPlan.builder()
            .reactionOnChangeStrategy(
                (memory, desireParameters) -> memory.updateFact(PLACE_TO_REACH,
                    desireParameters.returnFactValueForGivenKey(IS_BASE_LOCATION).get()
                        .getPosition()))
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> memory.eraseFactValueForGivenKey(PLACE_TO_REACH))
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        dataForDecision.getFeatureValueMadeCommitmentToType(
                            DesiresKeys.VISIT) != 1.0
                            && dataForDecision.getNumberOfCommittedAgents() <= 2
                )
                .desiresToConsider(new HashSet<>(Collections.singletonList(DesiresKeys.VISIT)))
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getNumberOfCommittedAgents() > 2)
                .build())
            .desiresWithIntentionToAct(new HashSet<>(
                Arrays.asList(DesiresKeys.MOVE_AWAY_FROM_DANGER, DesiresKeys.MOVE_TO_POSITION)))
            .build();
        type.addConfiguration(DesiresKeys.VISIT, goScouting, false);

        //return to base
        ConfigurationWithCommand.WithActingCommandDesiredBySelf returnToBase = ConfigurationWithCommand.WithActingCommandDesiredBySelf
            .builder()
            .reactionOnChangeStrategy((memory, desireParameters) -> {
              AUnitOfPlayer unitOfPlayer = memory.returnFactValueForGivenKey(REPRESENTS_UNIT).get();
              Optional<ABaseLocationWrapper> baseToReturnTo = memory
                  .getReadOnlyMemoriesForAgentType(
                      AgentTypes.BASE_LOCATION)
                  .filter(
                      readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_BASE).get())
                  .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                      IS_BASE_LOCATION).get())
                  .min(Comparator.comparingDouble(
                      value -> value.distanceTo(unitOfPlayer.getPosition())));
              baseToReturnTo.ifPresent(
                  aBaseLocationWrapper -> memory.updateFact(BASE_TO_MOVE, aBaseLocationWrapper));
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> !dataForDecision.madeDecisionToAny())
                .desiresToConsider(new HashSet<>(Collections.singletonList(DesiresKeys.VISIT)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> dataForDecision.madeDecisionToAny())
                .desiresToConsider(new HashSet<>(Collections.singletonList(DesiresKeys.VISIT)))
                .build())
            .commandCreationStrategy(intention -> new ActCommand.Own(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                return !memory.returnFactValueForGivenKey(
                    BASE_TO_MOVE).isPresent() || memory.returnFactValueForGivenKey(
                    IS_UNIT).get().move(memory.returnFactValueForGivenKey(BASE_TO_MOVE).get());
              }
            })
            .build();
        type.addConfiguration(DesiresKeys.GO_TO_BASE, returnToBase);

        //if is in danger - select the closest anti-air (or other if missing) unit and move away from it
        ConfigurationWithCommand.WithActingCommandDesiredBySelf flee = ConfigurationWithCommand.WithActingCommandDesiredBySelf
            .builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> memory.returnFactValueForGivenKey(
                    IS_UNIT).get().isUnderAttack())
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .commandCreationStrategy(intention -> new ActCommand.Own(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                AUnitWithCommands me = intention.returnFactValueForGivenKey(IS_UNIT).get();
                Optional<AUnit.Enemy> enemyAntiAir = me.getEnemyUnitsInRadiusOfSight().stream()
                    .filter(enemy -> enemy.getType().canAttackAirUnits())
                    .min(Comparator.comparingDouble(
                        value -> value.getPosition().distanceTo(me.getPosition())));
                if (enemyAntiAir.isPresent()) {
                  return me
                      .move(positionToMove(me.getPosition(), enemyAntiAir.get().getPosition()));
                } else {
                  Optional<AUnit.Enemy> enemy = me.getEnemyUnitsInRadiusOfSight().stream()
                      .min(Comparator.comparingDouble(
                          value -> value.getPosition().distanceTo(me.getPosition())));
                  if (enemy.isPresent()) {
                    return me.move(positionToMove(me.getPosition(), enemy.get().getPosition()));
                  }
                }
                return true;
              }
            })
            .build();
        type.addConfiguration(DesiresKeys.MOVE_AWAY_FROM_DANGER, DesiresKeys.VISIT, flee);

        //if is not in danger - continue to position
        ConfigurationWithCommand.WithActingCommandDesiredBySelf moveOnPosition = ConfigurationWithCommand.WithActingCommandDesiredBySelf
            .builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> !memory.returnFactValueForGivenKey(
                    IS_UNIT).get().isUnderAttack()
                    && memory.returnFactValueForGivenKey(IS_UNIT).get().getHPPercent() > 0.5
                )
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .commandCreationStrategy(intention -> new ActCommand.Own(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                return memory.returnFactValueForGivenKey(IS_UNIT).get().move(
                    memory.returnFactValueForGivenKey(PLACE_TO_REACH).get());
              }
            })
            .build();
        type.addConfiguration(DesiresKeys.MOVE_TO_POSITION, DesiresKeys.VISIT, moveOnPosition);

        //reason about morphing
        type.addConfiguration(DesiresKeys.MORPHING_TO, AgentTypeUnit.beliefsAboutMorphing);
      })
      .desiresWithIntentionToReason(
          new HashSet<>(
              Arrays.asList(DesiresKeys.SURROUNDING_UNITS_AND_LOCATION, DesiresKeys.MORPHING_TO)))
      .desiresWithIntentionToAct(new HashSet<>(Collections.singleton(DesiresKeys.GO_TO_BASE)))
      .build();
}
