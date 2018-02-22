package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.units;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_MOVE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.PLACE_TO_REACH;
import static aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit.moveFromPosition;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitWithCommands;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithAbstractPlan;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithCommand;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OverlordAgentType {

  public static final AgentTypeUnit OVERLORD = AgentTypeUnit.builder()
      .agentType(AgentTypes.OVERLORD)
      .usingTypesForFacts(Stream.of(IS_MORPHING_TO, BASE_TO_MOVE)
          .collect(Collectors.toSet()))
      .initializationStrategy(type -> {
        type.addConfiguration(DesiresKeys.SURROUNDING_UNITS_AND_LOCATION,
            AgentTypeUnit.beliefsAboutSurroundingUnitsAndLocation);

        //scouting - move to base scouted for last time. at start prefer unvisited base locations
        ConfigurationWithAbstractPlan goScouting = ConfigurationWithAbstractPlan.builder()
            .reactionOnChangeStrategy(
                (memory, desireParameters) -> memory.updateFactSetByFact(PLACE_TO_REACH,
                    desireParameters.returnFactValueForGivenKey(IS_BASE_LOCATION).get()
                        .getPosition()))
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> memory.eraseFactSetForGivenKey(PLACE_TO_REACH))
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> !dataForDecision.madeDecisionToAny())
                .desiresToConsider(Collections.singleton(DesiresKeys.VISIT))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> false)
                .build())
            .desiresWithIntentionToAct(
                Stream.of(DesiresKeys.MOVE_AWAY_FROM_DANGER, DesiresKeys.MOVE_TO_POSITION)
                    .collect(Collectors.toSet()))
            .build();
        type.addConfiguration(DesiresKeys.VISIT, goScouting, false);

        //if is in danger - select the closest anti-air (or other if missing) unit and move away from it
        ConfigurationWithCommand.WithActingCommandDesiredBySelf flee = ConfigurationWithCommand.WithActingCommandDesiredBySelf
            .builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    //there ant-air in range of sight
                    memory.returnFactValueForGivenKey(IS_UNIT).get().getEnemyUnitsInRadiusOfSight()
                        .stream()
                        .anyMatch(enemy -> enemy.getType().canAttackAirUnits()))
                .build())
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

                enemyAntiAir.ifPresent(
                    enemy -> me.move(moveFromPosition(me.getPosition(), enemy.getPosition())));
                return true;
              }
            }).build();
        type.addConfiguration(DesiresKeys.MOVE_AWAY_FROM_DANGER, DesiresKeys.VISIT, flee);

        //if it is not in danger - continue to position
        ConfigurationWithCommand.WithActingCommandDesiredBySelf moveOnPosition = ConfigurationWithCommand.WithActingCommandDesiredBySelf
            .builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    //there is no ant-air in range of sight
                    memory.returnFactValueForGivenKey(IS_UNIT).get().getEnemyUnitsInRadiusOfSight()
                        .stream()
                        .noneMatch(enemy -> enemy.getType().canAttackAirUnits()))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .commandCreationStrategy(intention -> new ActCommand.Own(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                return memory.returnFactValueForGivenKey(IS_UNIT).get()
                    .move(memory.returnFactValueForGivenKey(PLACE_TO_REACH).get());
              }
            })
            .build();
        type.addConfiguration(DesiresKeys.MOVE_TO_POSITION, DesiresKeys.VISIT, moveOnPosition);

        //reason about morphing
        type.addConfiguration(DesiresKeys.MORPHING_TO, AgentTypeUnit.beliefsAboutMorphing);
      })
      .desiresWithIntentionToReason(
          Stream.of(DesiresKeys.SURROUNDING_UNITS_AND_LOCATION, DesiresKeys.MORPHING_TO)
              .collect(Collectors.toSet()))
      .build();
}
