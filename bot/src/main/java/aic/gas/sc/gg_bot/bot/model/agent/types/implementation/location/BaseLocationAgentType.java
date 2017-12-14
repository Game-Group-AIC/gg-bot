package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.location;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.HATCHERY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.LAIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.PLAYER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.BASE_IS_COMPLETED;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_CREEP_COLONIES_AT_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_CREEP_COLONIES_AT_BASE_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_EXTRACTORS_ON_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_MINERALS_ON_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_POOLS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_SPORE_COLONIES_AT_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_SPORE_COLONIES_AT_BASE_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_SUNKEN_COLONIES_AT_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_SUNKEN_COLONIES_AT_BASE_IN_CONSTRUCTION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_MOVE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.CREEP_COLONY_COUNT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_AIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_AIR_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_BUILDING;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_BUILDING_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_GROUND;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_STATIC_AIR_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_STATIC_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.HAS_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.HAS_EXTRACTOR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_ENEMY_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_GATHERING_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_GATHERING_MINERALS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LAST_CREEP_COLONY_BUILDING_TIME;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LAST_SPORE_COLONY_BUILDING_TIME;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LAST_SUNKEN_COLONY_BUILDING_TIME;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LAST_TIME_SCOUTED;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LOCKED_BUILDINGS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LOCKED_UNITS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.MADE_OBSERVATION_IN_FRAME;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OUR_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_AIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_AIR_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_BUILDING;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_BUILDING_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_GROUND;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_STATIC_AIR_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_STATIC_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.REPRESENTS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.SPORE_COLONY_COUNT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.STATIC_DEFENSE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.SUNKEN_COLONY_COUNT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.TIME_OF_HOLD_COMMAND;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.WORKER_MINING_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.WORKER_MINING_MINERALS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.WORKER_ON_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.DEFENSE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.HOLDING;

import aic.gas.sc.gg_bot.abstract_bot.model.UnitTypeStatus;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APosition;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit.Enemy;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitOfPlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.UnitWrapperFactory;
import aic.gas.sc.gg_bot.bot.model.Decider;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeBaseLocation;
import aic.gas.sc.gg_bot.mas.model.knowledge.ReadOnlyMemory;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithAbstractPlan;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithCommand;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithSharedDesire;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.command.ReasoningCommand;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaseLocationAgentType {

  public static final AgentTypeBaseLocation BASE_LOCATION = AgentTypeBaseLocation.builder()
      .initializationStrategy(type -> {

        //reason about last visit
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf reasonAboutVisit = ConfigurationWithCommand.
            WithReasoningCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                ABaseLocationWrapper base = memory.returnFactValueForGivenKey(IS_BASE_LOCATION)
                    .get();
                OptionalInt frameWhenLastVisited = UnitWrapperFactory
                    .getStreamOfAllAlivePlayersUnits()
                    .filter(aUnitOfPlayer -> {
                      APosition aPosition = aUnitOfPlayer.getPosition();
                      return aPosition.distanceTo(base) < 5;
                    })
                    .mapToInt(AUnit::getFrameCount)
                    .max();
                frameWhenLastVisited.ifPresent(
                    integer -> memory.updateFact(LAST_TIME_SCOUTED, integer));

                //base status
                boolean isBase = memory.returnFactSetValueForGivenKey(HAS_BASE).map(
                    Stream::count).orElse(0L) > 0;
                memory.updateFact(IS_BASE, isBase);

                //todo it is not our base and (it has enemy buildings on it || we haven't visited all base locations and this one is unvisited base location)
                memory.updateFact(IS_ENEMY_BASE, !isBase
                    && memory.returnFactSetValueForGivenKey(ENEMY_BUILDING).map(
                    Stream::count).orElse(0L) > 0);
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
        type.addConfiguration(DesiresKeys.VISIT, reasonAboutVisit);

        //tell system to visit me
        ConfigurationWithSharedDesire visitMe = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(DesiresKeys.VISIT)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> {

                  //do not visit our base location
                  if (dataForDecision.getFeatureValueBeliefs(FactConverters.IS_BASE) == 1.0) {
                    return false;
                  }

                  //if everything is visited desire visit
                  if (dataForDecision.getFeatureValueGlobalBeliefs(
                      FactConverters.COUNT_OF_VISITED_BASES)
                      == dataForDecision.getFeatureValueGlobalBeliefs(
                      FactConverters.AVAILABLE_BASES)) {
                    return true;
                  }

                  //visit bases first
                  long countOfUnvisitedStartingPositions = memory.getReadOnlyMemoriesForAgentType(
                      AgentTypes.BASE_LOCATION)
                      .filter(readOnlyMemory -> readOnlyMemory.isFactKeyForValueInMemory(IS_BASE))
                      .filter(readOnlyMemory -> !readOnlyMemory.returnFactValueForGivenKey(
                          IS_BASE).get())
                      .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                          IS_BASE_LOCATION).get().isStartLocation())
                      .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                          LAST_TIME_SCOUTED))
                      .filter(integer -> !integer.isPresent())
                      .count();

                  //visit bases first
                  if (countOfUnvisitedStartingPositions > 0) {
                    return memory.returnFactValueForGivenKey(
                        IS_BASE_LOCATION).get().isStartLocation();
                  }

                  //all starting positions have been visit so desire to visit everything from now on
                  return true;
                })
                .globalBeliefTypesByAgentType(new HashSet<>(
                    Arrays.asList(FactConverters.COUNT_OF_VISITED_BASES,
                        FactConverters.AVAILABLE_BASES)))
                .beliefTypes(new HashSet<>(Collections.singleton(FactConverters.IS_BASE)))
                .useFactsInMemory(true)
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> {

                  //do not visit our base location
                  if (dataForDecision.getFeatureValueBeliefs(FactConverters.IS_BASE) == 1.0) {
                    return true;
                  }

                  //stay if it is enemy base
                  if (dataForDecision.getFeatureValueBeliefs(FactConverters.IS_ENEMY_BASE) == 1.0) {
                    return false;
                  }

                  //not visit anything
                  if (dataForDecision.getFeatureValueDesireBeliefs(
                      FactConverters.LAST_TIME_SCOUTED) == -1
                      && dataForDecision.getFeatureValueBeliefs(
                      FactConverters.LAST_TIME_SCOUTED) == -1) {
                    return false;
                  }

                  ///we made first visit
                  if (dataForDecision.getFeatureValueDesireBeliefs(
                      FactConverters.LAST_TIME_SCOUTED) == -1
                      && dataForDecision.getFeatureValueBeliefs(
                      FactConverters.LAST_TIME_SCOUTED) > 0) {
                    return true;
                  }

                  //new visit
                  return (dataForDecision.getFeatureValueDesireBeliefs(
                      FactConverters.LAST_TIME_SCOUTED) < dataForDecision.getFeatureValueBeliefs(
                      FactConverters.LAST_TIME_SCOUTED));
                })
                .beliefTypes(new HashSet<>(
                    Arrays.asList(FactConverters.IS_BASE, FactConverters.LAST_TIME_SCOUTED,
                        FactConverters.IS_ENEMY_BASE)))
                .parameterValueTypes(
                    new HashSet<>(Collections.singleton(FactConverters.LAST_TIME_SCOUTED)))
                .build())
            .counts(1)
            .build();
        type.addConfiguration(DesiresKeys.VISIT, visitMe);

        //enemy's units
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf enemyUnits = ConfigurationWithCommand.
            WithReasoningCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                ABaseLocationWrapper base = memory.returnFactValueForGivenKey(IS_BASE_LOCATION)
                    .get();

                Set<Enemy> enemies = UnitWrapperFactory.getStreamOfAllAliveEnemyUnits()
                    .filter(
                        enemy -> {
                          Optional<ABaseLocationWrapper> bL = enemy.getNearestBaseLocation();
                          return bL.isPresent() && bL.get().equals(base);
                        }).collect(Collectors.toSet());

                memory.updateFactSetByFacts(ENEMY_UNIT, enemies);
                memory.updateFactSetByFacts(ENEMY_BUILDING,
                    enemies.stream().filter(enemy -> enemy.getType().isBuilding()).collect(
                        Collectors.toSet()));
                memory.updateFactSetByFacts(ENEMY_GROUND, enemies.stream().filter(
                    enemy -> !enemy.getType().isBuilding() && !enemy.getType().isFlyer()).collect(
                    Collectors.toSet()));
                memory.updateFactSetByFacts(ENEMY_AIR, enemies.stream().filter(
                    enemy -> !enemy.getType().isBuilding() && enemy.getType().isFlyer()).collect(
                    Collectors.toSet()));

                return true;
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> {
                  ABaseLocationWrapper base = memory.returnFactValueForGivenKey(
                      IS_BASE_LOCATION).get();
                  return UnitWrapperFactory.getStreamOfAllAliveEnemyUnits().filter(enemy -> {
                    Optional<ABaseLocationWrapper> bL = enemy.getNearestBaseLocation();
                    return bL.isPresent() && bL.get().equals(base);
                  }).count() > 0;
                })
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .build();
        type.addConfiguration(DesiresKeys.ENEMIES_IN_LOCATION, enemyUnits);

        //player's units
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf ourUnits = ConfigurationWithCommand.
            WithReasoningCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                ABaseLocationWrapper base = memory.returnFactValueForGivenKey(IS_BASE_LOCATION)
                    .get();

                Set<AUnitOfPlayer> playersUnits = UnitWrapperFactory
                    .getStreamOfAllAlivePlayersUnits().filter(
                        enemy -> {
                          Optional<ABaseLocationWrapper> bL = enemy.getNearestBaseLocation();
                          return bL.isPresent() && bL.get().equals(base);
                        }).collect(Collectors.toSet());

                memory.updateFactSetByFacts(OUR_UNIT, playersUnits);
                memory.updateFactSetByFacts(OWN_BUILDING,
                    playersUnits.stream().filter(own -> own.getType().isBuilding()).collect(
                        Collectors.toSet()));
                memory.updateFactSetByFacts(OWN_GROUND, playersUnits.stream().filter(
                    own -> !own.getType().isBuilding() && !own.getType().isFlyer()).collect(
                    Collectors.toSet()));
                memory.updateFactSetByFacts(OWN_AIR, playersUnits.stream().filter(
                    own -> !own.getType().isBuilding() && own.getType().isFlyer()).collect(
                    Collectors.toSet()));

                //find new static defense buildings
                if (memory.returnFactSetValueForGivenKey(OWN_BUILDING)
                    .orElse(Stream.empty())
                    .filter(aUnitOfPlayer -> aUnitOfPlayer.getType().isMilitaryBuilding())
                    .count() > 0) {
                  memory.updateFactSetByFacts(STATIC_DEFENSE,
                      memory.returnFactSetValueForGivenKey(OWN_BUILDING).orElse(Stream.empty())
                          .filter(aUnitOfPlayer -> aUnitOfPlayer.getType().isMilitaryBuilding())
                          .collect(Collectors.toSet()));
                }

                //eco buildings
                memory.updateFactSetByFacts(HAS_BASE,
                    Stream.concat(memory.getReadOnlyMemoriesForAgentType(HATCHERY)
                            .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                                REPRESENTS_UNIT).get())
                            .filter(aUnitOfPlayer -> aUnitOfPlayer.getNearestBaseLocation().isPresent())
                            .filter(aUnitOfPlayer -> base.equals(
                                aUnitOfPlayer.getNearestBaseLocation().orElse(null))),
                        memory.getReadOnlyMemoriesForAgentType(LAIR)
                            .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                                REPRESENTS_UNIT).get())
                            .filter(
                                aUnitOfPlayer -> aUnitOfPlayer.getNearestBaseLocation().isPresent())
                            .filter(aUnitOfPlayer -> base.equals(
                                aUnitOfPlayer.getNearestBaseLocation().orElse(null)))
                    ).collect(Collectors.toSet()));
                memory.updateFactSetByFacts(HAS_EXTRACTOR,
                    memory.returnFactSetValueForGivenKey(OWN_BUILDING).orElse(Stream.empty())
                        .filter(aUnitOfPlayer -> aUnitOfPlayer.getType().isGasBuilding())
                        .collect(Collectors.toSet()));

                return true;
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> {
                  ABaseLocationWrapper base = memory.returnFactValueForGivenKey(
                      IS_BASE_LOCATION).get();
                  return UnitWrapperFactory.getStreamOfAllAlivePlayersUnits().filter(enemy -> {
                    Optional<ABaseLocationWrapper> bL = enemy.getNearestBaseLocation();
                    return bL.isPresent() && bL.get().equals(base);
                  }).count() > 0;
                })
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .build();
        type.addConfiguration(DesiresKeys.FRIENDLIES_IN_LOCATION, ourUnits);

        //estimate enemy force
        type.addConfiguration(DesiresKeys.ESTIMATE_ENEMY_FORCE_IN_LOCATION,
            formForceEstimator(ENEMY_UNIT, ENEMY_BUILDING_STATUS,
                ENEMY_STATIC_AIR_FORCE_STATUS, ENEMY_STATIC_GROUND_FORCE_STATUS,
                ENEMY_AIR_FORCE_STATUS,
                ENEMY_GROUND_FORCE_STATUS));

        //estimate our force
        type.addConfiguration(
            DesiresKeys.ESTIMATE_OUR_FORCE_IN_LOCATION,
            formForceEstimator(OUR_UNIT, OWN_BUILDING_STATUS,
                OWN_STATIC_AIR_FORCE_STATUS, OWN_STATIC_GROUND_FORCE_STATUS, OWN_AIR_FORCE_STATUS,
                OWN_GROUND_FORCE_STATUS));

        //eco concerns
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf ecoConcerns = ConfigurationWithCommand.
            WithReasoningCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                ABaseLocationWrapper base = memory.returnFactValueForGivenKey(IS_BASE_LOCATION)
                    .get();

                //workers
                Set<ReadOnlyMemory> workersAroundBase = memory.getReadOnlyMemories()
                    .filter(readOnlyMemory -> readOnlyMemory.isFactKeyForValueInMemory(LOCATION) &&
                        readOnlyMemory.returnFactValueForGivenKey(LOCATION).isPresent()
                        && readOnlyMemory.returnFactValueForGivenKey(LOCATION).get().equals(
                        base))
                    .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                        REPRESENTS_UNIT).get().getType().isWorker()
                        || (!readOnlyMemory.returnFactValueForGivenKey(
                        REPRESENTS_UNIT).get().getTrainingQueue().isEmpty()
                        && readOnlyMemory.returnFactValueForGivenKey(
                        REPRESENTS_UNIT).get().getTrainingQueue().get(0).isWorker()))
                    .collect(Collectors.toSet());

                memory.updateFactSetByFacts(WORKER_ON_BASE, workersAroundBase.stream()
                    .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                        REPRESENTS_UNIT).get())
                    .collect(Collectors.toSet()));
                workersAroundBase = workersAroundBase.stream()
                    .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                        REPRESENTS_UNIT).get().getType().isWorker())
                    .collect(Collectors.toSet());
                memory.updateFactSetByFacts(WORKER_MINING_MINERALS, workersAroundBase.stream()
                    .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                        IS_GATHERING_MINERALS).get())
                    .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                        REPRESENTS_UNIT).get())
                    .collect(Collectors.toSet()));
                memory.updateFactSetByFacts(WORKER_MINING_GAS, workersAroundBase.stream()
                    .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                        IS_GATHERING_GAS).get())
                    .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                        REPRESENTS_UNIT).get())
                    .collect(Collectors.toSet()));

                return true;
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueBeliefs(
                        FactConverters.IS_BASE) == 1.0)
                .beliefTypes(new HashSet<>(Collections.singleton(FactConverters.IS_BASE)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .build();
        type.addConfiguration(DesiresKeys.ECO_STATUS_IN_LOCATION, ecoConcerns);

        //Make request to start mining. Remove request when there are no more minerals to mine or there is no hatchery to bring mineral in
        ConfigurationWithSharedDesire mineMinerals = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(DesiresKeys.MINE_MINERALS_IN_BASE)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueBeliefs(
                        FactConverters.IS_BASE) == 1
                        && dataForDecision.getFeatureValueDesireBeliefSets(
                        COUNT_OF_MINERALS_ON_BASE) > 0)
                .beliefTypes(new HashSet<>(Collections.singletonList(FactConverters.IS_BASE)))
                .parameterValueSetTypes(
                    new HashSet<>(Collections.singletonList(COUNT_OF_MINERALS_ON_BASE)))
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueBeliefs(
                        FactConverters.IS_BASE) == 0
                        || dataForDecision.getFeatureValueBeliefSets(COUNT_OF_MINERALS_ON_BASE) == 0
                        || dataForDecision.getFeatureValueBeliefSets(
                        COUNT_OF_MINERALS_ON_BASE) != dataForDecision
                        .getFeatureValueDesireBeliefSets(
                            COUNT_OF_MINERALS_ON_BASE)
                        || (dataForDecision.madeDecisionToAny()
                        && memory.returnFactSetValueForGivenKey(
                        WORKER_MINING_GAS).orElse(Stream.empty()).count() == 0)
                )
                .beliefTypes(new HashSet<>(Collections.singletonList(FactConverters.IS_BASE)))
                .beliefSetTypes(new HashSet<>(Collections.singletonList(COUNT_OF_MINERALS_ON_BASE)))
                .parameterValueSetTypes(
                    new HashSet<>(Collections.singletonList(COUNT_OF_MINERALS_ON_BASE)))
                .desiresToConsider(
                    new HashSet<>(Collections.singleton(DesiresKeys.MINE_GAS_IN_BASE)))
                .build()
            )
            .build();
        type.addConfiguration(DesiresKeys.MINE_MINERALS_IN_BASE, mineMinerals);

        //Make request to start mining gas. Remove request when there are no extractors or there is no hatchery to bring mineral in
        ConfigurationWithSharedDesire mineGas = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(DesiresKeys.MINE_GAS_IN_BASE)
            .counts(2)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        dataForDecision.getFeatureValueBeliefs(FactConverters.IS_BASE) == 1
                            && dataForDecision.getFeatureValueDesireBeliefSets(
                            COUNT_OF_EXTRACTORS_ON_BASE) > 0
                            && memory.returnFactSetValueForGivenKey(OWN_BUILDING).orElse(
                            Stream.empty())
                            .filter(aUnitOfPlayer -> aUnitOfPlayer.getType().isGasBuilding())
                            .anyMatch(
                                aUnitOfPlayer -> !aUnitOfPlayer.isBeingConstructed()
                                    && !aUnitOfPlayer
                                    .isMorphing()))
                .beliefTypes(new HashSet<>(Collections.singletonList(FactConverters.IS_BASE)))
                .parameterValueSetTypes(
                    new HashSet<>(Collections.singletonList(COUNT_OF_EXTRACTORS_ON_BASE)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueBeliefs(
                        FactConverters.IS_BASE) == 0
                        || dataForDecision.getFeatureValueBeliefSets(COUNT_OF_EXTRACTORS_ON_BASE)
                        == 0
                )
                .beliefTypes(new HashSet<>(Collections.singletonList(FactConverters.IS_BASE)))
                .beliefSetTypes(
                    new HashSet<>(Collections.singletonList(COUNT_OF_EXTRACTORS_ON_BASE)))
                .build()
            )
            .build();
        type.addConfiguration(DesiresKeys.MINE_GAS_IN_BASE, mineGas);

        //build creep colony
        ConfigurationWithSharedDesire buildCreepColony = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(DesiresKeys.MORPH_TO_CREEP_COLONY)
            .counts(1)
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> memory.updateFact(LAST_CREEP_COLONY_BUILDING_TIME,
                    memory.returnFactValueForGivenKey(MADE_OBSERVATION_IN_FRAME).orElse(null)))
            .reactionOnChangeStrategy((memory, desireParameters) -> {
              long countOfCreepColonies =
                  memory.returnFactSetValueForGivenKey(STATIC_DEFENSE).orElse(
                      Stream.empty())
                      .filter(aUnitOfPlayer -> aUnitOfPlayer.getType().equals(
                          AUnitTypeWrapper.CREEP_COLONY_TYPE))
                      .count()
                      //workers building creep colony
                      + memory.returnFactSetValueForGivenKey(WORKER_ON_BASE).orElse(Stream.empty())
                      .filter(AUnit::isMorphing)
                      .filter(aUnitOfPlayer -> !aUnitOfPlayer.getTrainingQueue().isEmpty())
                      .map(aUnitOfPlayer -> aUnitOfPlayer.getTrainingQueue().get(0))
                      .filter(typeWrapper -> typeWrapper.equals(AUnitTypeWrapper.CREEP_COLONY_TYPE))
                      .count();
              memory.updateFact(CREEP_COLONY_COUNT, (int) countOfCreepColonies);
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        dataForDecision.getFeatureValueBeliefSets(BASE_IS_COMPLETED) == 1.0
                            && (memory.returnFactValueForGivenKey(
                            LAST_CREEP_COLONY_BUILDING_TIME).orElse(0) + 100
                            < memory.returnFactValueForGivenKey(MADE_OBSERVATION_IN_FRAME).orElse(
                            0))
                            && (dataForDecision.getFeatureValueBeliefSets(
                            COUNT_OF_CREEP_COLONIES_AT_BASE_IN_CONSTRUCTION)
                            + dataForDecision.getFeatureValueBeliefSets(
                            COUNT_OF_CREEP_COLONIES_AT_BASE)) == 0
                            && !dataForDecision.madeDecisionToAny()
                            && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_POOLS) > 0
                            && Decider.getDecision(AgentTypes.BASE_LOCATION,
                            DesireKeys.BUILD_CREEP_COLONY, dataForDecision, DEFENSE))
                .globalBeliefTypes(DEFENSE.getConvertersForFactsForGlobalBeliefs())
                .globalBeliefSetTypes(DEFENSE.getConvertersForFactSetsForGlobalBeliefs())
                .globalBeliefTypesByAgentType(Stream.concat(
                    DEFENSE.getConvertersForFactsForGlobalBeliefsByAgentType().stream(),
                    Stream.of(COUNT_OF_POOLS)).collect(Collectors.toSet()))
                .globalBeliefSetTypesByAgentType(
                    DEFENSE.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .beliefTypes(DEFENSE.getConvertersForFacts())
                .beliefSetTypes(Stream.concat(DEFENSE.getConvertersForFactSets().stream(),
                    Stream.of(BASE_IS_COMPLETED, COUNT_OF_CREEP_COLONIES_AT_BASE_IN_CONSTRUCTION,
                        COUNT_OF_CREEP_COLONIES_AT_BASE)).collect(Collectors.toSet()))
                .desiresToConsider(
                    new HashSet<>(Collections.singleton(DesiresKeys.BUILD_CREEP_COLONY)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !memory.returnFactValueForGivenKey(IS_BASE).get()
                        || memory.returnFactValueForGivenKey(CREEP_COLONY_COUNT).orElse(0) !=
                        (dataForDecision.getFeatureValueBeliefSets(
                            COUNT_OF_CREEP_COLONIES_AT_BASE_IN_CONSTRUCTION)
                            + dataForDecision.getFeatureValueBeliefSets(
                            COUNT_OF_CREEP_COLONIES_AT_BASE))
                )
                .beliefSetTypes(
                    new HashSet<>(Arrays.asList(COUNT_OF_CREEP_COLONIES_AT_BASE_IN_CONSTRUCTION,
                        COUNT_OF_CREEP_COLONIES_AT_BASE)))
                .useFactsInMemory(true)
                .build())
            .build();
        type.addConfiguration(DesiresKeys.BUILD_CREEP_COLONY, buildCreepColony);

        //common plan to build creep colony for sunken/spore colony
        ConfigurationWithSharedDesire buildCreepColonyCommon = ConfigurationWithSharedDesire
            .builder()
            .sharedDesireKey(DesiresKeys.MORPH_TO_CREEP_COLONY)
            .counts(1)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> (dataForDecision.getFeatureValueBeliefSets(
                        COUNT_OF_CREEP_COLONIES_AT_BASE_IN_CONSTRUCTION)
                        + dataForDecision.getFeatureValueBeliefSets(
                        COUNT_OF_CREEP_COLONIES_AT_BASE)) == 0
                        && !dataForDecision.madeDecisionToAny())
                .beliefSetTypes(
                    Stream.of(BASE_IS_COMPLETED, COUNT_OF_CREEP_COLONIES_AT_BASE_IN_CONSTRUCTION,
                        COUNT_OF_CREEP_COLONIES_AT_BASE).collect(Collectors.toSet()))
                .desiresToConsider(
                    new HashSet<>(Collections.singleton(DesiresKeys.BUILD_CREEP_COLONY)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> (dataForDecision.getFeatureValueBeliefSets(
                        COUNT_OF_CREEP_COLONIES_AT_BASE_IN_CONSTRUCTION)
                        + dataForDecision.getFeatureValueBeliefSets(
                        COUNT_OF_CREEP_COLONIES_AT_BASE)) > 0
                )
                .beliefSetTypes(
                    new HashSet<>(Arrays.asList(COUNT_OF_CREEP_COLONIES_AT_BASE_IN_CONSTRUCTION,
                        COUNT_OF_CREEP_COLONIES_AT_BASE)))
                .useFactsInMemory(true)
                .build())
            .build();

        //build sunken as abstract plan
        ConfigurationWithAbstractPlan buildSunkenAbstract = ConfigurationWithAbstractPlan.builder()
            .reactionOnChangeStrategy((memory, desireParameters) -> {
              long countOfSunkenColonies =
                  memory.returnFactSetValueForGivenKey(STATIC_DEFENSE).orElse(
                      Stream.empty())
                      .filter(aUnitOfPlayer -> aUnitOfPlayer.getType().equals(
                          AUnitTypeWrapper.SUNKEN_COLONY_TYPE))
                      .count()
                      //creep colonies morphing to sunken
                      + memory.returnFactSetValueForGivenKey(STATIC_DEFENSE).orElse(Stream.empty())
                      .filter(aUnitOfPlayer -> aUnitOfPlayer.getType().equals(
                          AUnitTypeWrapper.CREEP_COLONY_TYPE))
                      .filter(aUnitOfPlayer -> !aUnitOfPlayer.getTrainingQueue().isEmpty())
                      .map(aUnitOfPlayer -> aUnitOfPlayer.getTrainingQueue().get(0))
                      .filter(
                          typeWrapper -> typeWrapper.equals(AUnitTypeWrapper.SUNKEN_COLONY_TYPE))
                      .count();
              memory.updateFact(SUNKEN_COLONY_COUNT, (int) countOfSunkenColonies);
            })
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> memory.updateFact(LAST_SUNKEN_COLONY_BUILDING_TIME,
                    memory.returnFactValueForGivenKey(MADE_OBSERVATION_IN_FRAME).orElse(null)))
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        dataForDecision.getFeatureValueBeliefSets(BASE_IS_COMPLETED) == 1.0
                            && (memory.returnFactValueForGivenKey(
                            LAST_SUNKEN_COLONY_BUILDING_TIME).orElse(0) + 100
                            < memory.returnFactValueForGivenKey(MADE_OBSERVATION_IN_FRAME).orElse(
                            0))
                            && (dataForDecision.getFeatureValueBeliefSets(
                            COUNT_OF_SUNKEN_COLONIES_AT_BASE_IN_CONSTRUCTION)
                            + dataForDecision.getFeatureValueBeliefSets(
                            COUNT_OF_SUNKEN_COLONIES_AT_BASE)) <= 4
                            && Decider.getDecision(AgentTypes.BASE_LOCATION,
                            DesireKeys.BUILD_SUNKEN_COLONY, dataForDecision, DEFENSE))
                .globalBeliefTypes(DEFENSE.getConvertersForFactsForGlobalBeliefs())
                .globalBeliefSetTypes(DEFENSE.getConvertersForFactSetsForGlobalBeliefs())
                .globalBeliefTypesByAgentType(
                    DEFENSE.getConvertersForFactsForGlobalBeliefsByAgentType())
                .globalBeliefSetTypesByAgentType(
                    DEFENSE.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .beliefTypes(DEFENSE.getConvertersForFacts())
                .beliefSetTypes(Stream.concat(DEFENSE.getConvertersForFactSets().stream(),
                    Stream.of(BASE_IS_COMPLETED, COUNT_OF_SUNKEN_COLONIES_AT_BASE_IN_CONSTRUCTION,
                        COUNT_OF_SUNKEN_COLONIES_AT_BASE)).collect(Collectors.toSet()))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !memory.returnFactValueForGivenKey(IS_BASE).get()
                        || memory.returnFactValueForGivenKey(SUNKEN_COLONY_COUNT).orElse(0) !=
                        (dataForDecision.getFeatureValueBeliefSets(
                            COUNT_OF_SUNKEN_COLONIES_AT_BASE_IN_CONSTRUCTION)
                            + dataForDecision.getFeatureValueBeliefSets(
                            COUNT_OF_SUNKEN_COLONIES_AT_BASE))
                )
                .beliefSetTypes(
                    new HashSet<>(Arrays.asList(COUNT_OF_SUNKEN_COLONIES_AT_BASE_IN_CONSTRUCTION,
                        COUNT_OF_SUNKEN_COLONIES_AT_BASE)))
                .useFactsInMemory(true)
                .build())
            .desiresForOthers(new HashSet<>(Arrays.asList(
                DesiresKeys.BUILD_CREEP_COLONY, DesiresKeys.BUILD_SUNKEN_COLONY)))
            .build();
        type.addConfiguration(DesiresKeys.BUILD_SUNKEN_COLONY, buildSunkenAbstract, true);
        type.addConfiguration(DesiresKeys.BUILD_CREEP_COLONY, DesiresKeys.BUILD_SUNKEN_COLONY,
            buildCreepColonyCommon);
        ConfigurationWithSharedDesire buildSunken = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(DesiresKeys.MORPH_TO_SUNKEN_COLONY)
            .counts(1)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> false)
                .build())
            .build();
        type.addConfiguration(DesiresKeys.BUILD_SUNKEN_COLONY, DesiresKeys.BUILD_SUNKEN_COLONY,
            buildSunken);

        //spore colony as abstract plan
        ConfigurationWithAbstractPlan buildSporeColonyAbstract = ConfigurationWithAbstractPlan
            .builder()
            .reactionOnChangeStrategy((memory, desireParameters) -> {
              long countOfSporeColonies =
                  memory.returnFactSetValueForGivenKey(STATIC_DEFENSE).orElse(
                      Stream.empty())
                      .filter(aUnitOfPlayer -> aUnitOfPlayer.getType().equals(
                          AUnitTypeWrapper.SPORE_COLONY_TYPE))
                      .count()
                      //creep colonies morphing to sunken
                      + memory.returnFactSetValueForGivenKey(STATIC_DEFENSE).orElse(Stream.empty())
                      .filter(aUnitOfPlayer -> aUnitOfPlayer.getType().equals(
                          AUnitTypeWrapper.CREEP_COLONY_TYPE))
                      .filter(aUnitOfPlayer -> !aUnitOfPlayer.getTrainingQueue().isEmpty())
                      .map(aUnitOfPlayer -> aUnitOfPlayer.getTrainingQueue().get(0))
                      .filter(typeWrapper -> typeWrapper.equals(AUnitTypeWrapper.SPORE_COLONY_TYPE))
                      .count();
              memory.updateFact(SPORE_COLONY_COUNT, (int) countOfSporeColonies);
            })
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> memory.updateFact(LAST_SPORE_COLONY_BUILDING_TIME,
                    memory.returnFactValueForGivenKey(MADE_OBSERVATION_IN_FRAME).orElse(null)))
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        dataForDecision.getFeatureValueBeliefSets(BASE_IS_COMPLETED) == 1.0
                            && (memory.returnFactValueForGivenKey(
                            LAST_SPORE_COLONY_BUILDING_TIME).orElse(0) + 100
                            < memory.returnFactValueForGivenKey(MADE_OBSERVATION_IN_FRAME).orElse(
                            0))
                            && (dataForDecision.getFeatureValueBeliefSets(
                            COUNT_OF_SPORE_COLONIES_AT_BASE_IN_CONSTRUCTION)
                            + dataForDecision.getFeatureValueBeliefSets(
                            COUNT_OF_SPORE_COLONIES_AT_BASE)) <= 4
                            && Decider.getDecision(AgentTypes.BASE_LOCATION,
                            DesireKeys.BUILD_SPORE_COLONY, dataForDecision, DEFENSE))
                .globalBeliefTypes(DEFENSE.getConvertersForFactsForGlobalBeliefs())
                .globalBeliefSetTypes(DEFENSE.getConvertersForFactSetsForGlobalBeliefs())
                .globalBeliefTypesByAgentType(
                    DEFENSE.getConvertersForFactsForGlobalBeliefsByAgentType())
                .globalBeliefSetTypesByAgentType(
                    DEFENSE.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .beliefTypes(DEFENSE.getConvertersForFacts())
                .beliefSetTypes(Stream.concat(DEFENSE.getConvertersForFactSets().stream(),
                    Stream.of(BASE_IS_COMPLETED, COUNT_OF_SPORE_COLONIES_AT_BASE,
                        COUNT_OF_SPORE_COLONIES_AT_BASE_IN_CONSTRUCTION)).collect(
                    Collectors.toSet()))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !memory.returnFactValueForGivenKey(IS_BASE).get()
                        || memory.returnFactValueForGivenKey(SPORE_COLONY_COUNT).orElse(0) !=
                        (dataForDecision.getFeatureValueBeliefSets(
                            COUNT_OF_SPORE_COLONIES_AT_BASE_IN_CONSTRUCTION)
                            + dataForDecision.getFeatureValueBeliefSets(
                            COUNT_OF_SPORE_COLONIES_AT_BASE))
                )
                .beliefSetTypes(
                    new HashSet<>(Arrays.asList(COUNT_OF_SPORE_COLONIES_AT_BASE_IN_CONSTRUCTION,
                        COUNT_OF_SPORE_COLONIES_AT_BASE)))
                .useFactsInMemory(true)
                .build())
            .desiresForOthers(new HashSet<>(Arrays.asList(
                DesiresKeys.BUILD_CREEP_COLONY, DesiresKeys.BUILD_SPORE_COLONY)))
            .build();
        type.addConfiguration(DesiresKeys.BUILD_SPORE_COLONY, buildSporeColonyAbstract, true);
        type.addConfiguration(DesiresKeys.BUILD_CREEP_COLONY, DesiresKeys.BUILD_SPORE_COLONY,
            buildCreepColonyCommon);
        ConfigurationWithSharedDesire buildSporeColony = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(DesiresKeys.MORPH_TO_SPORE_COLONY)
            .counts(1)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> false)
                .build())
            .build();
        type.addConfiguration(DesiresKeys.BUILD_SPORE_COLONY, DesiresKeys.BUILD_SPORE_COLONY,
            buildSporeColony);

        //hold ground
        ConfigurationWithSharedDesire holdGround = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(DesiresKeys.HOLD_GROUND)
            .reactionOnChangeStrategy(
                (memory, desireParameters) -> memory.updateFact(TIME_OF_HOLD_COMMAND,
                    memory.getReadOnlyMemoriesForAgentType(PLAYER)
                        .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                            MADE_OBSERVATION_IN_FRAME))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findAny().orElse(null)))
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    memory.returnFactValueForGivenKey(IS_ENEMY_BASE).get()
                        && Decider.getDecision(AgentTypes.BASE_LOCATION, DesireKeys.HOLD_GROUND,
                        dataForDecision, HOLDING))
                .globalBeliefTypes(HOLDING.getConvertersForFactsForGlobalBeliefs())
                .globalBeliefSetTypes(HOLDING.getConvertersForFactSetsForGlobalBeliefs())
                .globalBeliefTypesByAgentType(
                    HOLDING.getConvertersForFactsForGlobalBeliefsByAgentType())
                .globalBeliefSetTypesByAgentType(
                    HOLDING.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .beliefTypes(HOLDING.getConvertersForFacts())
                .beliefSetTypes(HOLDING.getConvertersForFactSets())
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> !memory.returnFactValueForGivenKey(
                    IS_ENEMY_BASE).get()
                    || !Decider.getDecision(AgentTypes.BASE_LOCATION, DesireKeys.HOLD_GROUND,
                    dataForDecision, HOLDING)
                )
                .globalBeliefTypes(HOLDING.getConvertersForFactsForGlobalBeliefs())
                .globalBeliefSetTypes(HOLDING.getConvertersForFactSetsForGlobalBeliefs())
                .globalBeliefTypesByAgentType(
                    HOLDING.getConvertersForFactsForGlobalBeliefsByAgentType())
                .globalBeliefSetTypesByAgentType(
                    HOLDING.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .beliefTypes(HOLDING.getConvertersForFacts())
                .beliefSetTypes(HOLDING.getConvertersForFactSets())
                .build())
            .build();
        type.addConfiguration(DesiresKeys.HOLD_GROUND, holdGround);

        //hold air
        ConfigurationWithSharedDesire holdAir = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(DesiresKeys.HOLD_AIR)
            .reactionOnChangeStrategy(
                (memory, desireParameters) -> memory.updateFact(TIME_OF_HOLD_COMMAND,
                    memory.getReadOnlyMemoriesForAgentType(PLAYER)
                        .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                            MADE_OBSERVATION_IN_FRAME))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findAny().orElse(null)))
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> memory.returnFactValueForGivenKey(
                    IS_ENEMY_BASE).get()
                    && Decider.getDecision(AgentTypes.BASE_LOCATION, DesireKeys.HOLD_AIR,
                    dataForDecision, HOLDING))
                .globalBeliefTypes(HOLDING.getConvertersForFactsForGlobalBeliefs())
                .globalBeliefSetTypes(HOLDING.getConvertersForFactSetsForGlobalBeliefs())
                .globalBeliefTypesByAgentType(
                    HOLDING.getConvertersForFactsForGlobalBeliefsByAgentType())
                .globalBeliefSetTypesByAgentType(
                    HOLDING.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .beliefTypes(HOLDING.getConvertersForFacts())
                .beliefSetTypes(HOLDING.getConvertersForFactSets())
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> !memory.returnFactValueForGivenKey(
                    IS_ENEMY_BASE).get()
                    || !Decider.getDecision(AgentTypes.BASE_LOCATION, DesireKeys.HOLD_AIR,
                    dataForDecision, HOLDING))
                .globalBeliefTypes(HOLDING.getConvertersForFactsForGlobalBeliefs())
                .globalBeliefSetTypes(HOLDING.getConvertersForFactSetsForGlobalBeliefs())
                .globalBeliefTypesByAgentType(
                    HOLDING.getConvertersForFactsForGlobalBeliefsByAgentType())
                .globalBeliefSetTypesByAgentType(
                    HOLDING.getConvertersForFactSetsForGlobalBeliefsByAgentType())
                .beliefTypes(HOLDING.getConvertersForFacts())
                .beliefSetTypes(HOLDING.getConvertersForFactSets())
                .build())
            .build();
        type.addConfiguration(DesiresKeys.HOLD_AIR, holdAir);

        //defend base
        ConfigurationWithSharedDesire defend = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(DesiresKeys.DEFEND)
            .reactionOnChangeStrategy(
                (memory, desireParameters) -> memory.updateFact(TIME_OF_HOLD_COMMAND,
                    memory.getReadOnlyMemoriesForAgentType(PLAYER)
                        .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                            MADE_OBSERVATION_IN_FRAME))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findAny().orElse(null)))
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> memory.returnFactValueForGivenKey(IS_BASE).get()
                        && memory.returnFactSetValueForGivenKey(ENEMY_UNIT).orElse(
                        Stream.empty()).count() > 0)
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> !memory.returnFactValueForGivenKey(IS_BASE).get()
                        || memory.returnFactSetValueForGivenKey(ENEMY_UNIT).orElse(
                        Stream.empty()).count() == 0)
                .build())
            .build();
        type.addConfiguration(DesiresKeys.DEFEND, defend);
      })
      .usingTypesForFacts(
          new HashSet<>(Arrays.asList(IS_BASE, IS_ENEMY_BASE, BASE_TO_MOVE, SUNKEN_COLONY_COUNT,
              SPORE_COLONY_COUNT, CREEP_COLONY_COUNT, LAST_SUNKEN_COLONY_BUILDING_TIME,
              LAST_CREEP_COLONY_BUILDING_TIME,
              LAST_SPORE_COLONY_BUILDING_TIME, TIME_OF_HOLD_COMMAND)))
      .usingTypesForFactSets(new HashSet<>(Arrays.asList(WORKER_ON_BASE, ENEMY_BUILDING, ENEMY_AIR,
          ENEMY_GROUND, HAS_BASE, HAS_EXTRACTOR, OWN_BUILDING, OWN_AIR, OWN_GROUND,
          WORKER_MINING_MINERALS, WORKER_MINING_GAS, OWN_AIR_FORCE_STATUS, OWN_BUILDING_STATUS,
          OWN_GROUND_FORCE_STATUS, ENEMY_AIR_FORCE_STATUS, ENEMY_BUILDING_STATUS,
          ENEMY_GROUND_FORCE_STATUS, LOCKED_UNITS, LOCKED_BUILDINGS, ENEMY_STATIC_AIR_FORCE_STATUS,
          ENEMY_STATIC_GROUND_FORCE_STATUS, OWN_STATIC_AIR_FORCE_STATUS,
          OWN_STATIC_GROUND_FORCE_STATUS,
          STATIC_DEFENSE, ENEMY_UNIT, OUR_UNIT)))
      .desiresWithIntentionToReason(new HashSet<>(Arrays.asList(DesiresKeys.ECO_STATUS_IN_LOCATION,
          DesiresKeys.FRIENDLIES_IN_LOCATION, DesiresKeys.ENEMIES_IN_LOCATION,
          DesiresKeys.ESTIMATE_ENEMY_FORCE_IN_LOCATION, DesiresKeys.ESTIMATE_OUR_FORCE_IN_LOCATION,
          DesiresKeys.VISIT)))
      .desiresForOthers(new HashSet<>(Arrays.asList(
          DesiresKeys.VISIT, DesiresKeys.MINE_GAS_IN_BASE, DesiresKeys.MINE_MINERALS_IN_BASE,
          DesiresKeys.BUILD_CREEP_COLONY, DesiresKeys.HOLD_GROUND, DesiresKeys.HOLD_AIR,
          DesiresKeys.DEFEND)))
      .desiresWithAbstractIntention(new HashSet<>(Arrays.asList(
          DesiresKeys.BUILD_SUNKEN_COLONY, DesiresKeys.BUILD_SPORE_COLONY)))
      .build();

  /**
   * Template to create configuration of force estimation reasoning
   */
  private static ConfigurationWithCommand.WithReasoningCommandDesiredBySelf formForceEstimator(
      FactKey<? extends AUnit> factToSelectUnits,
      FactKey<UnitTypeStatus> buildings,
      FactKey<UnitTypeStatus> staticAir,
      FactKey<UnitTypeStatus> staticGround,
      FactKey<UnitTypeStatus> air,
      FactKey<UnitTypeStatus> ground) {
    return ConfigurationWithCommand.
        WithReasoningCommandDesiredBySelf.builder()
        .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
          @Override
          public boolean act(WorkingMemory memory) {
            Set<UnitTypeStatus> unitTypes = memory.returnFactSetValueForGivenKey(factToSelectUnits)
                .orElse(Stream.empty())
                .filter(enemy -> enemy.getType().isBuilding())
                .collect(Collectors.groupingBy(AUnit::getType)).entrySet().stream()
                .map(entry -> new UnitTypeStatus(entry.getKey(), entry.getValue().stream()))
                .collect(Collectors.toSet());

            memory.updateFactSetByFacts(buildings, unitTypes);
            memory.updateFactSetByFacts(staticAir, unitTypes.stream()
                .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper()
                    .isMilitaryBuildingAntiAir())
                .collect(Collectors.toSet()));
            memory.updateFactSetByFacts(staticGround, unitTypes.stream()
                .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper()
                    .isMilitaryBuildingAntiGround())
                .collect(Collectors.toSet()));

            Set<UnitTypeStatus> ownUnitsTypes = memory
                .returnFactSetValueForGivenKey(factToSelectUnits)
                .orElse(Stream.empty())
                .filter(
                    enemy -> !enemy.getType().isNotActuallyUnit() && !enemy.getType().isBuilding())
                .collect(Collectors.groupingBy(AUnit::getType)).entrySet().stream()
                .map(entry -> new UnitTypeStatus(entry.getKey(), entry.getValue().stream()))
                .collect(Collectors.toSet());
            memory.updateFactSetByFacts(air, ownUnitsTypes.stream()
                .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().canAttackAirUnits())
                .collect(Collectors.toSet()));
            memory.updateFactSetByFacts(ground, ownUnitsTypes.stream()
                .filter(
                    unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().canAttackGroundUnits())
                .collect(Collectors.toSet()));

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
  }

}
