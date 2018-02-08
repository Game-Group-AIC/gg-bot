package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.player;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.AVAILABLE_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.AVAILABLE_MINERALS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_MOVE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.DIFFERENCE_IN_BASES;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_AIR_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_BUILDING_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_RACE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_STATIC_AIR_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_STATIC_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.FORCE_SUPPLY_RATIO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.FREE_SUPPLY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_OUR_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_ENEMY_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_PLAYER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LOCKED_BUILDINGS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LOCKED_UNITS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OUR_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_AIR_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_BUILDING_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_STATIC_AIR_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_STATIC_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.POPULATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.POPULATION_LIMIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.UPGRADE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.WAS_VISITED;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.ESTIMATE_ARMY_SUPPLY_RATIO;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.ESTIMATE_ENEMY_FORCE_IN_BUILDINGS;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.ESTIMATE_ENEMY_FORCE_IN_UNITS;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.ESTIMATE_OUR_FORCE_IN_BUILDINGS;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.ESTIMATE_OUR_FORCE_IN_UNITS;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.READ_PLAYERS_DATA;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.REASON_ABOUT_BASES;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.UPDATE_ENEMY_RACE;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.WORKER_SCOUT;

import aic.gas.sc.gg_bot.abstract_bot.model.UnitTypeStatus;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DecisionConfiguration;
import aic.gas.sc.gg_bot.abstract_bot.model.game.util.Utils;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.UnitWrapperFactory;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypePlayer;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithCommand;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithSharedDesire;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.command.ReasoningCommand;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerAgentType {

  private static final boolean SEND_SIXTH_WORKER = (new Random()).nextBoolean();

  public static final AgentTypePlayer PLAYER = AgentTypePlayer.builder()
      .agentTypeID(AgentTypes.PLAYER)
      .usingTypesForFacts(
          Stream.of(AVAILABLE_MINERALS, ENEMY_RACE, AVAILABLE_GAS, POPULATION_LIMIT, POPULATION,
              IS_PLAYER, BASE_TO_MOVE, IS_BASE_LOCATION, LOCATION, FREE_SUPPLY, FORCE_SUPPLY_RATIO,
              DIFFERENCE_IN_BASES)
              .collect(Collectors.toSet()))
      .usingTypesForFactSets(Stream.of(UPGRADE_STATUS, OUR_BASE, ENEMY_BASE,
          OWN_AIR_FORCE_STATUS, OWN_BUILDING_STATUS, OWN_GROUND_FORCE_STATUS,
          ENEMY_AIR_FORCE_STATUS, ENEMY_BUILDING_STATUS, ENEMY_GROUND_FORCE_STATUS, LOCKED_UNITS,
          LOCKED_BUILDINGS, ENEMY_STATIC_AIR_FORCE_STATUS, ENEMY_STATIC_GROUND_FORCE_STATUS,
          OWN_STATIC_AIR_FORCE_STATUS, OWN_STATIC_GROUND_FORCE_STATUS, ENEMY_FORCE_STATUS,
          OWN_FORCE_STATUS)
          .collect(Collectors.toSet()))
      .initializationStrategy(type -> {

        //read data from player
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf readPlayersData = ConfigurationWithCommand.
            WithReasoningCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                APlayer aPlayer = memory.returnFactValueForGivenKey(IS_PLAYER).get();
                memory.updateFact(AVAILABLE_MINERALS, (double) aPlayer.getMinerals());
                memory.updateFact(AVAILABLE_GAS, (double) aPlayer.getGas());
                memory.updateFact(POPULATION_LIMIT, (double) aPlayer.getSupplyTotal());
                memory.updateFact(POPULATION, (double) aPlayer.getSupplyUsed());
                memory.updateFact(FREE_SUPPLY,
                    (double) (aPlayer.getSupplyTotal() - aPlayer.getSupplyUsed()));
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
        type.addConfiguration(READ_PLAYERS_DATA, readPlayersData);

        //estimate enemy force by buildings
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf estimateEnemyForceByBuildings = createConfigurationTemplateForForceEstimator(
            () -> UnitWrapperFactory.getStreamOfAllAliveEnemyUnits()
                .filter(enemy -> enemy.getType().isBuilding()),
            (unitTypeStatuses, memoryToUpdate) -> {
              memoryToUpdate.updateFactSetByFacts(ENEMY_BUILDING_STATUS, unitTypeStatuses);
              memoryToUpdate
                  .updateFactSetByFacts(ENEMY_STATIC_AIR_FORCE_STATUS, unitTypeStatuses.stream()
                      .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper()
                          .isMilitaryBuildingAntiAir())
                      .collect(Collectors.toSet()));
              memoryToUpdate
                  .updateFactSetByFacts(ENEMY_STATIC_GROUND_FORCE_STATUS, unitTypeStatuses.stream()
                      .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper()
                          .isMilitaryBuildingAntiGround())
                      .collect(Collectors.toSet()));
            });
        type.addConfiguration(ESTIMATE_ENEMY_FORCE_IN_BUILDINGS, estimateEnemyForceByBuildings);

        //estimate supply ratio
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf estimateArmySupplyRatio = ConfigurationWithCommand.
            WithReasoningCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                memory.updateFact(FORCE_SUPPLY_RATIO, Utils
                    .computeOurVsEnemyForceRatio(
                        memory.returnFactSetValueForGivenKey(OWN_FORCE_STATUS),
                        memory.returnFactSetValueForGivenKey(ENEMY_FORCE_STATUS)));
                return true;
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> true)
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .build();
        type.addConfiguration(ESTIMATE_ARMY_SUPPLY_RATIO, estimateArmySupplyRatio);

        //estimate enemy force by units
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf estimateEnemyForceByUnits = createConfigurationTemplateForForceEstimator(
            () -> UnitWrapperFactory.getStreamOfAllAliveEnemyUnits()
                .filter(enemy -> !enemy.getType().isNotActuallyUnit()
                    && !enemy.getType().isBuilding()),
            (unitTypeStatuses, memoryToUpdate) -> {
              memoryToUpdate.updateFactSetByFacts(ENEMY_AIR_FORCE_STATUS, unitTypeStatuses.stream()
                  .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().canAttackAirUnits())
                  .collect(Collectors.toSet()));
              memoryToUpdate
                  .updateFactSetByFacts(ENEMY_GROUND_FORCE_STATUS, unitTypeStatuses.stream()
                      .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper()
                          .canAttackGroundUnits())
                      .collect(Collectors.toSet()));
              memoryToUpdate.updateFactSetByFacts(ENEMY_FORCE_STATUS, unitTypeStatuses);
            });
        type.addConfiguration(ESTIMATE_ENEMY_FORCE_IN_UNITS, estimateEnemyForceByUnits);

        //estimate our force by buildings
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf estimateOurForceByBuildings = createConfigurationTemplateForForceEstimator(
            () -> UnitWrapperFactory.getStreamOfAllAlivePlayersUnits()
                .filter(enemy -> enemy.getType().isBuilding()),
            (unitTypeStatuses, memoryToUpdate) -> {
              memoryToUpdate.updateFactSetByFacts(OWN_BUILDING_STATUS, unitTypeStatuses);
              memoryToUpdate
                  .updateFactSetByFacts(OWN_STATIC_AIR_FORCE_STATUS, unitTypeStatuses.stream()
                      .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper()
                          .isMilitaryBuildingAntiAir())
                      .collect(Collectors.toSet()));
              memoryToUpdate
                  .updateFactSetByFacts(OWN_STATIC_GROUND_FORCE_STATUS, unitTypeStatuses.stream()
                      .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper()
                          .isMilitaryBuildingAntiGround())
                      .collect(Collectors.toSet()));
            });
        type.addConfiguration(ESTIMATE_OUR_FORCE_IN_BUILDINGS, estimateOurForceByBuildings);

        //estimate our force by units
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf estimateOurForceByUnits = createConfigurationTemplateForForceEstimator(
            () -> UnitWrapperFactory.getStreamOfAllAlivePlayersUnits()
                .filter(
                    enemy -> !enemy.getType().isNotActuallyUnit() && !enemy.getType().isBuilding()),
            (unitTypeStatuses, memoryToUpdate) -> {
              memoryToUpdate.updateFactSetByFacts(OWN_AIR_FORCE_STATUS, unitTypeStatuses.stream()
                  .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().canAttackAirUnits())
                  .collect(Collectors.toSet()));
              memoryToUpdate.updateFactSetByFacts(OWN_GROUND_FORCE_STATUS, unitTypeStatuses.stream()
                  .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper()
                      .canAttackGroundUnits())
                  .collect(Collectors.toSet()));
              memoryToUpdate.updateFactSetByFacts(OWN_FORCE_STATUS, unitTypeStatuses);
            });
        type.addConfiguration(ESTIMATE_OUR_FORCE_IN_UNITS, estimateOurForceByUnits);

        //enemy race
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf updateEnemyRace = ConfigurationWithCommand.
            WithReasoningCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                memory.updateFact(ENEMY_RACE, DecisionConfiguration.getRace());
                return true;
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> DecisionConfiguration.isRaceWasDetermined()
                        && !memory.returnFactValueForGivenKey(ENEMY_RACE).get()
                        .equals(DecisionConfiguration.getRace()))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .build();
        type.addConfiguration(UPDATE_ENEMY_RACE, updateEnemyRace);

        //not all main bases are visited - send 6th or 7th drone to scout
        ConfigurationWithSharedDesire tellWorkerToScout = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(WORKER_SCOUT)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    //we haven't found enemy base
                    memory.getReadOnlyMemoriesForAgentType(AgentTypes.BASE_LOCATION)
                        .noneMatch(readOnlyMemory -> readOnlyMemory
                            .returnFactValueForGivenKey(IS_ENEMY_BASE).orElse(false))
                        //there are unvisited main bases
                        && memory.getReadOnlyMemoriesForAgentType(AgentTypes.BASE_LOCATION)
                        .filter(readOnlyMemory -> !readOnlyMemory
                            .returnFactValueForGivenKey(WAS_VISITED)
                            .orElse(false))
                        .map(readOnlyMemory -> readOnlyMemory
                            .returnFactValueForGivenKey(IS_BASE_LOCATION))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .anyMatch(ABaseLocationWrapper::isStartLocation)
                        //there is more workers then 6 or 7
                        && (SEND_SIXTH_WORKER ? 6 : 7) <= memory
                        .getReadOnlyMemoriesForAgentType(AgentTypes.DRONE).count())
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    //we have found enemy base
                    memory.getReadOnlyMemoriesForAgentType(AgentTypes.BASE_LOCATION)
                        .anyMatch(readOnlyMemory -> readOnlyMemory
                            .returnFactValueForGivenKey(IS_ENEMY_BASE).orElse(false))
                        //all main bases were visited
                        || memory.getReadOnlyMemoriesForAgentType(AgentTypes.BASE_LOCATION)
                        .filter(readOnlyMemory -> !readOnlyMemory
                            .returnFactValueForGivenKey(WAS_VISITED)
                            .orElse(false))
                        .map(readOnlyMemory -> readOnlyMemory
                            .returnFactValueForGivenKey(IS_BASE_LOCATION))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .noneMatch(ABaseLocationWrapper::isStartLocation)
                )
                .build())
            .counts(1)
            .build();
        type.addConfiguration(WORKER_SCOUT, tellWorkerToScout);

        //bases
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf reasonAboutBases = ConfigurationWithCommand.
            WithReasoningCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                memory.updateFactSetByFacts(OUR_BASE,
                    memory.getReadOnlyMemoriesForAgentType(BASE_LOCATION)
                        .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                            IS_OUR_BASE)
                            .orElse(false))
                        .map(readOnlyMemory -> readOnlyMemory
                            .returnFactValueForGivenKey(IS_BASE_LOCATION).get())
                        .collect(Collectors.toSet()));
                memory.updateFactSetByFacts(ENEMY_BASE,
                    memory.getReadOnlyMemoriesForAgentType(BASE_LOCATION)
                        .filter(readOnlyMemory -> readOnlyMemory
                            .returnFactValueForGivenKey(IS_ENEMY_BASE).orElse(false))
                        .map(readOnlyMemory -> readOnlyMemory
                            .returnFactValueForGivenKey(IS_BASE_LOCATION).get())
                        .collect(Collectors.toSet()));
                memory.updateFact(DIFFERENCE_IN_BASES, Utils
                    .computeDifferenceInBases(memory.returnFactSetValueForGivenKey(OUR_BASE),
                        memory.returnFactSetValueForGivenKey(ENEMY_BASE)));
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
        type.addConfiguration(REASON_ABOUT_BASES, reasonAboutBases);

      })
      .desiresWithIntentionToReason(Stream.of(READ_PLAYERS_DATA, ESTIMATE_ENEMY_FORCE_IN_BUILDINGS,
          ESTIMATE_ENEMY_FORCE_IN_UNITS, ESTIMATE_OUR_FORCE_IN_BUILDINGS,
          ESTIMATE_ARMY_SUPPLY_RATIO, ESTIMATE_OUR_FORCE_IN_UNITS, UPDATE_ENEMY_RACE,
          REASON_ABOUT_BASES)
          .collect(Collectors.toSet()))
      .desiresForOthers(Collections.singleton(WORKER_SCOUT))
      .build();

  private interface GetUnitsStreamStrategy<V extends AUnit> {

    Stream<V> getUnitsAsStream();
  }

  private interface UpdateStatusesStrategy {

    void updateBy(Set<UnitTypeStatus> unitTypeStatuses, WorkingMemory memoryToUpdate);
  }

  /**
   * Template to estimate force
   */
  private static <V extends AUnit> ConfigurationWithCommand.WithReasoningCommandDesiredBySelf createConfigurationTemplateForForceEstimator(
      GetUnitsStreamStrategy<V> getUnitsStreamStrategy,
      UpdateStatusesStrategy updateStatusesStrategy) {
    return ConfigurationWithCommand.
        WithReasoningCommandDesiredBySelf.builder()
        .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
          @Override
          public boolean act(WorkingMemory memory) {

            //set of unit we are interested in
            Set<UnitTypeStatus> unitTypeStatuses = getUnitsStreamStrategy.getUnitsAsStream()
                .collect(Collectors.groupingBy(AUnit::getType)).entrySet().stream()
                .map(entry -> new UnitTypeStatus(entry.getKey(), entry.getValue().stream()))
                .collect(Collectors.toSet());

            updateStatusesStrategy.updateBy(unitTypeStatuses, memory);
            return true;
          }
        })
        .decisionInDesire(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) -> true)
            .build())
        .decisionInIntention(CommitmentDeciderInitializer.builder()
            .decisionStrategy((dataForDecision, memory) -> true)
            .build())
        .build();
  }

}
