package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.units;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_IDLE_DRONES;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_MINERALS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_MINERALS_ON_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.HAS_SELECTED_MINERAL_TO_MINE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.IS_CARRYING_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.IS_CARRYING_MINERAL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.IS_MINING_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.IS_MINING_MINERAL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.IS_WAITING_ON_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.IS_WAITING_ON_MINERAL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_MOVE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_SCOUT_BY_WORKER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.HAS_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.HAS_EXTRACTOR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.HAS_SOMETHING_TO_BUILD;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IDLE_SINCE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_GATHERING_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_GATHERING_MINERALS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_ISLAND;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_START_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LAST_TIME_SCOUTED;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.MADE_OBSERVATION_IN_FRAME;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.MINERAL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.MINERAL_TO_MINE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.MINING_IN_EXTRACTOR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.MINING_MINERAL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_BUILDING;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.PLACE_FOR_CREEP_COLONY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.PLACE_FOR_EVOLUTION_CHAMBER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.PLACE_FOR_EXPANSION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.PLACE_FOR_EXTRACTOR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.PLACE_FOR_HYDRALISK_DEN;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.PLACE_FOR_POOL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.PLACE_FOR_SPIRE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.PLACE_TO_GO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.PLACE_TO_REACH;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.REPRESENTS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.WORKER_MINING_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.WORKER_MINING_MINERALS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.WORKER_ON_BASE;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APosition;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ATilePosition;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitOfPlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitWithCommands;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import aic.gas.sc.gg_bot.bot.service.implementation.BuildLockerService;
import aic.gas.sc.gg_bot.bot.utils.Util;
import aic.gas.sc.gg_bot.mas.model.knowledge.ReadOnlyMemory;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentType;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithAbstractPlan;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithCommand;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import aic.gas.sc.gg_bot.mas.model.planing.command.ReasoningCommand;
import bwapi.Order;
import bwapi.UnitCommandType;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DroneAgentType {

  private static final Random RANDOM = new Random();

  //worker
  public static final AgentTypeUnit DRONE = AgentTypeUnit.builder()
      .agentTypeID(AgentTypes.DRONE)
      .usingTypesForFacts(
          new HashSet<>(Arrays.asList(MINING_MINERAL, MINERAL_TO_MINE, IS_MORPHING_TO,
              IS_GATHERING_MINERALS, IS_GATHERING_GAS, BASE_TO_SCOUT_BY_WORKER, PLACE_TO_GO,
              PLACE_FOR_POOL, PLACE_FOR_EXPANSION, BASE_TO_MOVE, PLACE_FOR_EXTRACTOR,
              MINING_IN_EXTRACTOR, PLACE_FOR_SPIRE, PLACE_FOR_HYDRALISK_DEN, PLACE_FOR_CREEP_COLONY,
              PLACE_FOR_EVOLUTION_CHAMBER, IDLE_SINCE, PLACE_TO_REACH, HAS_SOMETHING_TO_BUILD)))
      .initializationStrategy(type -> {

        //mine gas
        ConfigurationWithAbstractPlan mineGas = ConfigurationWithAbstractPlan.builder()
            .reactionOnChangeStrategy(
                (memory, desireParameters) -> memory.updateFact(MINING_IN_EXTRACTOR,
                    desireParameters.returnFactSetValueForGivenKey(HAS_EXTRACTOR).get().findAny()
                        .get()))
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> memory.eraseFactValueForGivenKey(MINING_IN_EXTRACTOR))
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> (!dataForDecision.madeDecisionToAny()
                    //committed agents
                    && dataForDecision.getNumberOfCommittedAgents() < 3
                    //is in base location
                    //TODO or is idle
                    && dataForDecision.returnFactSetValueForGivenKey(HAS_EXTRACTOR).get().findAny()
                    .get().getNearestBaseLocation().get().equals(
                        memory.returnFactValueForGivenKey(IS_UNIT).get().getNearestBaseLocation()
                            .orElse(null))
                ))
                .useFactsInMemory(true)
                .desiresToConsider(new HashSet<>(Arrays
                    .asList(DesiresKeys.WORKER_SCOUT, DesiresKeys.GO_TO_BASE,
                        DesiresKeys.MINE_MINERALS_IN_BASE, DesiresKeys.MINE_GAS_IN_BASE,
                        DesiresKeys.BUILD)))
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.madeDecisionToAny()
                        || dataForDecision.getNumberOfCommittedAgents() > 3
                        //or no body is mining minerals
                        || memory.getReadOnlyMemoriesForAgentType(AgentTypes.BASE_LOCATION).filter(
                        readOnlyMemory -> readOnlyMemory
                            .returnFactValueForGivenKey(IS_BASE_LOCATION).get()
                            .equals(memory.returnFactValueForGivenKey(IS_UNIT).get()
                                .getNearestBaseLocation().orElse(null))).anyMatch(readOnlyMemory ->
                        readOnlyMemory.returnFactSetValueForGivenKey(WORKER_MINING_MINERALS)
                            .orElse(Stream.empty()).count() == 0)
                )
                .desiresToConsider(new HashSet<>(Arrays.asList(
                    DesiresKeys.WORKER_SCOUT, DesiresKeys.GO_TO_BASE,
                    DesiresKeys.MINE_MINERALS_IN_BASE, DesiresKeys.BUILD)))
                .useFactsInMemory(true)
                .build())
            .desiresWithIntentionToAct(new HashSet<>(Collections.singleton(DesiresKeys.MINE_GAS)))
            .build();
        type.addConfiguration(DesiresKeys.MINE_GAS_IN_BASE, mineGas, false);

        //send worker to mine gas
        ConfigurationWithCommand.WithActingCommandDesiredBySelf sendForGas = ConfigurationWithCommand.
            WithActingCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ActCommand.Own(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                return intention.returnFactValueForGivenKey(IS_UNIT).get().gather(
                    intention.returnFactValueForGivenKey(MINING_IN_EXTRACTOR).get());
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueBeliefs(
                        IS_MINING_GAS) == 0
                        && dataForDecision.getFeatureValueBeliefs(IS_CARRYING_GAS) == 0
                        && dataForDecision.getFeatureValueBeliefs(IS_WAITING_ON_GAS) == 0)
                .beliefTypes(
                    new HashSet<>(Arrays.asList(IS_MINING_GAS, IS_CARRYING_GAS, IS_WAITING_ON_GAS)))
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .build();
        type.addConfiguration(DesiresKeys.MINE_GAS, DesiresKeys.MINE_GAS_IN_BASE, sendForGas);

        //go scouting
        ConfigurationWithCommand.WithActingCommandDesiredByOtherAgent goScouting = ConfigurationWithCommand.WithActingCommandDesiredByOtherAgent
            .builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> false)
                .useFactsInMemory(true)
                .build())
            .reactionOnChangeStrategy((memory, desireParameters) -> {
              Optional<ABaseLocationWrapper> baseToScout = memory.getReadOnlyMemoriesForAgentType(
                  AgentTypes.BASE_LOCATION)
                  .filter(readOnlyMemory -> !readOnlyMemory.returnFactValueForGivenKey(
                      LAST_TIME_SCOUTED).isPresent())
                  .filter(readOnlyMemory -> !readOnlyMemory.returnFactValueForGivenKey(
                      IS_ISLAND).get())
                  .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                      IS_START_LOCATION).get())
                  .map(
                      readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_BASE_LOCATION))
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .findAny();
              baseToScout
                  .ifPresent(aBaseLocationWrapper -> memory.updateFact(BASE_TO_SCOUT_BY_WORKER,
                      aBaseLocationWrapper));
            })
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> memory
                    .eraseFactValueForGivenKey(BASE_TO_SCOUT_BY_WORKER))
            .commandCreationStrategy(intention -> new ActCommand.DesiredByAnotherAgent(intention) {
              @Override
              public boolean act(WorkingMemory memory) {

                //todo hack...
                AUnitWithCommands me = intention.returnFactValueForGivenKey(IS_UNIT).get();
                if (intention.returnFactValueForGivenKey(BASE_TO_SCOUT_BY_WORKER).get().distanceTo(
                    me.getPosition()) < 10) {
                  Optional<ABaseLocationWrapper> baseToScout = memory
                      .getReadOnlyMemoriesForAgentType(
                          AgentTypes.BASE_LOCATION)
                      .filter(readOnlyMemory -> !readOnlyMemory.returnFactValueForGivenKey(
                          LAST_TIME_SCOUTED).isPresent())
                      .filter(readOnlyMemory -> !readOnlyMemory.returnFactValueForGivenKey(
                          IS_ISLAND).get())
                      .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                          IS_START_LOCATION).get())
                      .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                          IS_BASE_LOCATION))
                      .filter(Optional::isPresent)
                      .map(Optional::get)
                      .findAny();
                  baseToScout.ifPresent(
                      aBaseLocationWrapper -> memory.updateFact(BASE_TO_SCOUT_BY_WORKER,
                          aBaseLocationWrapper));
                }
                return me.move(intention.returnFactValueForGivenKey(BASE_TO_SCOUT_BY_WORKER).get());
              }
            })
            .build();
        type.addConfiguration(DesiresKeys.WORKER_SCOUT, goScouting);

        //reason about activities related to worker
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf workerConcerns = ConfigurationWithCommand.
            WithReasoningCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                AUnitOfPlayer me = memory.returnFactValueForGivenKey(REPRESENTS_UNIT).get();
                memory.updateFact(IS_GATHERING_MINERALS,
                    me.isCarryingMinerals() || me.isGatheringMinerals()
                        || (me.getOrder().isPresent() && (me.getOrder().get().equals(
                        Order.MiningMinerals)
                        || me.getOrder().get().equals(
                        Order.MoveToMinerals) || me.getOrder().get().equals(
                        Order.WaitForMinerals)
                        || me.getOrder().get().equals(Order.ReturnMinerals))));
                memory.updateFact(IS_GATHERING_GAS, me.isCarryingGas() || me.isGatheringGas()
                    || (me.getOrder().isPresent() && (me.getOrder().get().equals(Order.HarvestGas)
                    || me.getOrder().get().equals(Order.MoveToGas) || me.getOrder().get().equals(
                    Order.WaitForGas)
                    || me.getOrder().get().equals(Order.ReturnGas))));

                //is idle
                if (me.isIdle()) {
                  if (!memory.returnFactValueForGivenKey(IDLE_SINCE).isPresent()) {
                    memory.updateFact(IDLE_SINCE,
                        memory.returnFactValueForGivenKey(MADE_OBSERVATION_IN_FRAME).orElse(0));
                  }
                } else {
                  memory.eraseFactValueForGivenKey(IDLE_SINCE);
                }

                return true;
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, workingMemory) -> true)
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, workingMemory) -> true)
                .build())
            .build();
        type.addConfiguration(DesiresKeys.UPDATE_BELIEFS_ABOUT_WORKER_ACTIVITIES, workerConcerns);

        //reason about morphing
        type.addConfiguration(DesiresKeys.MORPHING_TO, AgentTypeUnit.beliefsAboutMorphing);

        //abstract plan to mine minerals in base
        ConfigurationWithAbstractPlan mineInBase = ConfigurationWithAbstractPlan.builder()
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> !dataForDecision.madeDecisionToAny()
                    //is in same base
                    && (dataForDecision.returnFactValueForGivenKey(IS_BASE_LOCATION).get().equals(
                    memory.returnFactValueForGivenKey(REPRESENTS_UNIT).get()
                        .getNearestBaseLocation().orElse(null))
                    && dataForDecision.getNumberOfCommittedAgents() <= 1.5 * dataForDecision
                    .getFeatureValueDesireBeliefSets(COUNT_OF_MINERALS_ON_BASE)))
                .useFactsInMemory(true)
                .parameterValueSetTypes(
                    new HashSet<>(Collections.singletonList(COUNT_OF_MINERALS_ON_BASE)))
                .desiresToConsider(new HashSet<>(Arrays
                    .asList(DesiresKeys.WORKER_SCOUT, DesiresKeys.MINE_GAS_IN_BASE,
                        DesiresKeys.BUILD, DesiresKeys.GO_TO_BASE)))
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> {
                  if (dataForDecision.madeDecisionToAny() ||
                      dataForDecision.getNumberOfCommittedAgents() >= 2 * dataForDecision
                          .getFeatureValueDesireBeliefSets(
                              COUNT_OF_MINERALS_ON_BASE)
                      || memory.getReadOnlyMemoriesForAgentType(AgentTypes.BASE_LOCATION).filter(
                      readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_BASE_LOCATION)
                          .get().equals(memory.returnFactValueForGivenKey(IS_UNIT).get()
                              .getNearestBaseLocation().orElse(null))).anyMatch(readOnlyMemory ->
                      readOnlyMemory.returnFactSetValueForGivenKey(WORKER_MINING_GAS)
                          .orElse(Stream.empty()).count() == 0)) {
                    return true;
                  }
                  return false;
                })
                .parameterValueSetTypes(
                    new HashSet<>(Collections.singletonList(COUNT_OF_MINERALS_ON_BASE)))
                .desiresToConsider(new HashSet<>(Arrays
                    .asList(DesiresKeys.WORKER_SCOUT, DesiresKeys.GO_TO_BASE,
                        DesiresKeys.MINE_GAS_IN_BASE, DesiresKeys.BUILD)))
                .useFactsInMemory(true)
                .build()
            )
            .desiresWithIntentionToAct(
                new HashSet<>(Collections.singletonList(DesiresKeys.MINE_MINERALS)))
            .desiresWithIntentionToReason(
                new HashSet<>(
                    Arrays.asList(DesiresKeys.SELECT_MINERAL, DesiresKeys.UNSELECT_MINERAL)))
            .build();
        type.addConfiguration(DesiresKeys.MINE_MINERALS_IN_BASE, mineInBase, false);

        //select closest mineral to mine from set of available
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf selectMineral = ConfigurationWithCommand.
            WithReasoningCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                Set<AUnit> mineralsBeingMined = memory.getReadOnlyMemoriesForAgentType(DRONE)
                    .filter(readOnlyMemory -> readOnlyMemory.getAgentId() != memory.getAgentId())
                    .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                        MINING_MINERAL))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
                Set<AUnit> mineralsToMine = intention
                    .returnFactSetValueOfParentIntentionForGivenKey(
                        MINERAL).get()
                    .filter(unit -> !mineralsBeingMined.contains(unit))
                    .collect(Collectors.toSet());
                if (!mineralsToMine.isEmpty()) {

                  //select free nearest mineral to closest hatchery
                  APosition myPosition = intention.returnFactValueForGivenKey(
                      IS_UNIT).get().getPosition();
                  Optional<AUnit> mineralToPick = mineralsToMine.stream()
                      .min(Comparator.comparingDouble(
                          o -> myPosition.distanceTo(o.getPosition())));
                  mineralToPick.ifPresent(aUnit -> {
                    memory.updateFact(MINERAL_TO_MINE, aUnit);

                    //release currently mined mineral if it differs with mineral about to be mined
                    if (intention.returnFactValueForGivenKey(MINING_MINERAL).isPresent()
                        && !intention.returnFactValueForGivenKey(MINING_MINERAL).get().equals(
                        aUnit)) {
                      memory.updateFact(MINING_MINERAL, MINING_MINERAL.getInitValue());
                    }
                  });
                }
                return true;
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> (dataForDecision.getFeatureValueBeliefs(
                        IS_MINING_MINERAL) == 0
                        || dataForDecision.getFeatureValueBeliefs(IS_WAITING_ON_MINERAL) == 1)
                        && dataForDecision.getFeatureValueBeliefs(IS_CARRYING_MINERAL) == 0
                )
                .beliefTypes(new HashSet<>(
                    Arrays.asList(IS_MINING_MINERAL, IS_CARRYING_MINERAL, IS_WAITING_ON_MINERAL)))
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build()
            )
            .build();
        type.addConfiguration(DesiresKeys.SELECT_MINERAL, DesiresKeys.MINE_MINERALS_IN_BASE,
            selectMineral);

        //remove occupancy of mineral when carrying it
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf unselectMineral = ConfigurationWithCommand.
            WithReasoningCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                memory.eraseFactValueForGivenKey(MINING_MINERAL);
                memory.eraseFactValueForGivenKey(MINERAL_TO_MINE);
                return true;
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueBeliefs(
                        IS_MINING_MINERAL) == 1
                        && dataForDecision.getFeatureValueBeliefs(IS_CARRYING_MINERAL) == 1)
                .beliefTypes(new HashSet<>(Arrays.asList(IS_MINING_MINERAL, IS_CARRYING_MINERAL)))
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build()
            )
            .build();
        type.addConfiguration(DesiresKeys.UNSELECT_MINERAL, DesiresKeys.MINE_MINERALS_IN_BASE,
            unselectMineral);

        //go to mine it
        ConfigurationWithCommand.WithActingCommandDesiredBySelf mine = ConfigurationWithCommand.
            WithActingCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ActCommand.Own(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                boolean hasStartedMining = intention.returnFactValueForGivenKey(IS_UNIT).get()
                    .gather(
                        intention.returnFactValueForGivenKeyInDesireParameters(MINERAL_TO_MINE)
                            .get());
                if (hasStartedMining) {
                  memory.updateFact(MINING_MINERAL,
                      intention.returnFactValueForGivenKey(MINERAL_TO_MINE).get());
                }
                return hasStartedMining;
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueBeliefs(
                        HAS_SELECTED_MINERAL_TO_MINE) != 0
                        && !memory.returnFactValueForGivenKey(MINERAL_TO_MINE).get().equals(
                        memory.returnFactValueForGivenKey(MINING_MINERAL).orElse(null))
                        && dataForDecision.getFeatureValueBeliefs(IS_CARRYING_MINERAL) == 0)
                .beliefTypes(new HashSet<>(Arrays.asList(IS_MINING_MINERAL, IS_CARRYING_MINERAL,
                    HAS_SELECTED_MINERAL_TO_MINE)))
                .useFactsInMemory(true)
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .build();
        type.addConfiguration(DesiresKeys.MINE_MINERALS, DesiresKeys.MINE_MINERALS_IN_BASE, mine);

        //morphing
        type.addConfiguration(DesiresKeys.MORPHING_TO, AgentTypeUnit.beliefsAboutMorphing);

        //surrounding units and location belief update
        type.addConfiguration(
            DesiresKeys.SURROUNDING_UNITS_AND_LOCATION,
            AgentTypeUnit.beliefsAboutSurroundingUnitsAndLocation);

        //go to nearest own base with minerals when you have nothing to do
        ConfigurationWithCommand.WithActingCommandDesiredBySelf goToNearestBase = ConfigurationWithCommand.
            WithActingCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ActCommand.Own(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                AUnitWithCommands me = intention.returnFactValueForGivenKey(IS_UNIT).get();
                if (me.isCarryingGas() || me.isCarryingMinerals()) {
                  me.returnCargo();
                } else {
                  if (intention.returnFactValueForGivenKey(PLACE_TO_GO).isPresent()) {
                    me.move(intention.returnFactValueForGivenKey(PLACE_TO_GO).get());
                  }
                }
                return true;
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    {
                      if (dataForDecision.madeDecisionToAny()) {
                        return false;
                      }

                      AUnitOfPlayer me = memory.returnFactValueForGivenKey(REPRESENTS_UNIT).get();
                      //is in enemy base
                      if (me.getNearestBaseLocation().isPresent()) {
                        boolean isInEnemyBase = memory.getReadOnlyMemoriesForAgentType(
                            AgentTypes.BASE_LOCATION)
                            .filter(readOnlyMemory -> readOnlyMemory
                                .returnFactValueForGivenKey(FactKeys.IS_ENEMY_BASE).get())
                            .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                                IS_BASE_LOCATION))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .anyMatch(aBaseLocationWrapper -> aBaseLocationWrapper
                                .equals(me.getNearestBaseLocation().get()));
                        //is in small distance
                        if (isInEnemyBase) {
                          return true;
                        }
                      }

                      //is idle or has nothing to do
                      return !dataForDecision.madeDecisionToAny() && me.isIdle();
                    }
                )
                .desiresToConsider(new HashSet<>(Arrays.asList(
                    DesiresKeys.WORKER_SCOUT, DesiresKeys.EXPAND,
                    DesiresKeys.MORPH_TO_POOL, DesiresKeys.MORPH_TO_SPIRE,
                    DesiresKeys.MORPH_TO_HYDRALISK_DEN, DesiresKeys.MORPH_TO_EXTRACTOR,
                    DesiresKeys.MORPH_TO_CREEP_COLONY, DesiresKeys.MORPH_TO_EVOLUTION_CHAMBER,
                    DesiresKeys.MINE_MINERALS_IN_BASE, DesiresKeys.MINE_GAS_IN_BASE,
                    DesiresKeys.BUILD, DesiresKeys.GO_TO_BASE)))
                .build()
            )
            .reactionOnChangeStrategy((memory, desireParameters) -> {
              AUnitOfPlayer me = memory.returnFactValueForGivenKey(REPRESENTS_UNIT).get();

              //skip when is carrying cargo
              if (!me.isCarryingGas() && !me.isCarryingMinerals()) {

                //find base where can help
                Optional<ABaseLocationWrapper> basesToGo = memory.getReadOnlyMemoriesForAgentType(
                    AgentTypes.BASE_LOCATION).filter(
                    readOnlyMemory -> readOnlyMemory.returnFactSetValueForGivenKey(HAS_BASE)
                        .orElse(Stream.empty()).anyMatch(
                            aUnitOfPlayer -> !aUnitOfPlayer.isMorphing() && !aUnitOfPlayer
                                .isBeingConstructed())).filter(
                    readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_BASE).get())
                    .filter(
                        readOnlyMemory -> readOnlyMemory.collectKeysOfDesiresInTreeCounts().keySet()
                            .stream()
                            .anyMatch(
                                desireKey -> desireKey.equals(DesiresKeys.MINE_MINERALS_IN_BASE)
                                    || desireKey.equals(DesiresKeys.MINE_GAS_IN_BASE)))
                    .filter(readOnlyMemory -> {
                      long countOfMinerals = readOnlyMemory.returnFactSetValueForGivenKey(
                          MINERAL).orElse(Stream.empty()).count();
                      long extractors = readOnlyMemory.returnFactSetValueForGivenKey(
                          HAS_EXTRACTOR).orElse(Stream.empty()).count();
                      long workers = readOnlyMemory.returnFactSetValueForGivenKey(
                          WORKER_ON_BASE).orElse(Stream.empty()).count();
                      return ((countOfMinerals * 2.5) + (extractors * 3)) > workers;
                    })
                    .map(readOnlyMemory -> readOnlyMemory
                        .returnFactValueForGivenKey(IS_BASE_LOCATION))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(aBaseLocationWrapper -> !aBaseLocationWrapper
                        .equals(me.getNearestBaseLocation().orElse(null)))
                    .min(Comparator.comparingDouble(value -> value.distanceTo(me.getPosition())));
                basesToGo.ifPresent(aBaseLocationWrapper -> memory
                    .updateFact(PLACE_TO_GO, aBaseLocationWrapper.getPosition()));
              }
            })
            .reactionOnChangeStrategyInIntention(
                (memory, desireParameters) -> memory.eraseFactValueForGivenKey(PLACE_TO_GO))
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .build();
        type.addConfiguration(DesiresKeys.RETURN_TO_BASE, goToNearestBase);

        //build pool
        initAbstractBuildingPlan(type, AUnitTypeWrapper.SPAWNING_POOL_TYPE, PLACE_FOR_POOL,
            DesiresKeys.MORPH_TO_POOL, DesiresKeys.FIND_PLACE_FOR_POOL, new HashSet<>(Arrays.asList(
                DesiresKeys.WORKER_SCOUT, DesiresKeys.EXPAND,
                DesiresKeys.MORPH_TO_EXTRACTOR, DesiresKeys.MORPH_TO_SPIRE,
                DesiresKeys.MORPH_TO_HYDRALISK_DEN, DesiresKeys.MORPH_TO_CREEP_COLONY,
                DesiresKeys.MORPH_TO_EVOLUTION_CHAMBER)));

        //build spire
        initAbstractBuildingPlan(type, AUnitTypeWrapper.SPIRE_TYPE, PLACE_FOR_SPIRE,
            DesiresKeys.MORPH_TO_SPIRE,
            DesiresKeys.FIND_PLACE_FOR_SPIRE,
            new HashSet<>(Arrays.asList(DesiresKeys.WORKER_SCOUT, DesiresKeys.EXPAND,
                DesiresKeys.MORPH_TO_EXTRACTOR, DesiresKeys.MORPH_TO_POOL,
                DesiresKeys.MORPH_TO_HYDRALISK_DEN, DesiresKeys.MORPH_TO_CREEP_COLONY,
                DesiresKeys.MORPH_TO_EVOLUTION_CHAMBER)));

        //build hydralisk den
        initAbstractBuildingPlan(type, AUnitTypeWrapper.HYDRALISK_DEN_TYPE, PLACE_FOR_HYDRALISK_DEN,
            DesiresKeys.MORPH_TO_HYDRALISK_DEN, DesiresKeys.FIND_PLACE_FOR_HYDRALISK_DEN,
            new HashSet<>(Arrays.asList(DesiresKeys.WORKER_SCOUT, DesiresKeys.EXPAND,
                DesiresKeys.MORPH_TO_EXTRACTOR, DesiresKeys.MORPH_TO_POOL,
                DesiresKeys.MORPH_TO_SPIRE, DesiresKeys.MORPH_TO_CREEP_COLONY,
                DesiresKeys.MORPH_TO_EVOLUTION_CHAMBER)));

        //build expansion
        initAbstractBuildingPlan(type, AUnitTypeWrapper.HATCHERY_TYPE, PLACE_FOR_EXPANSION,
            DesiresKeys.EXPAND,
            DesiresKeys.FIND_PLACE_FOR_HATCHERY,
            new HashSet<>(Arrays.asList(DesiresKeys.WORKER_SCOUT, DesiresKeys.MORPH_TO_POOL,
                DesiresKeys.MORPH_TO_EXTRACTOR, DesiresKeys.MORPH_TO_SPIRE,
                DesiresKeys.MORPH_TO_HYDRALISK_DEN, DesiresKeys.MORPH_TO_CREEP_COLONY,
                DesiresKeys.MORPH_TO_EVOLUTION_CHAMBER)));

        //build extractor
        initAbstractBuildingPlan(type, AUnitTypeWrapper.EXTRACTOR_TYPE, PLACE_FOR_EXTRACTOR,
            DesiresKeys.MORPH_TO_EXTRACTOR,
            DesiresKeys.FIND_PLACE_FOR_EXTRACTOR,
            new HashSet<>(Arrays.asList(DesiresKeys.WORKER_SCOUT, DesiresKeys.EXPAND,
                DesiresKeys.MORPH_TO_POOL, DesiresKeys.MORPH_TO_SPIRE,
                DesiresKeys.MORPH_TO_HYDRALISK_DEN, DesiresKeys.MORPH_TO_CREEP_COLONY,
                DesiresKeys.MORPH_TO_EVOLUTION_CHAMBER)));

        //build creep colony
        initAbstractBuildingPlan(type, AUnitTypeWrapper.CREEP_COLONY_TYPE, PLACE_FOR_CREEP_COLONY,
            DesiresKeys.MORPH_TO_CREEP_COLONY, DesiresKeys.FIND_PLACE_FOR_CREEP_COLONY,
            new HashSet<>(Arrays.asList(DesiresKeys.WORKER_SCOUT, DesiresKeys.EXPAND,
                DesiresKeys.MORPH_TO_POOL, DesiresKeys.MORPH_TO_SPIRE,
                DesiresKeys.MORPH_TO_HYDRALISK_DEN, DesiresKeys.MORPH_TO_EXTRACTOR,
                DesiresKeys.MORPH_TO_EVOLUTION_CHAMBER)));

//                //build evolution chamber
//                initAbstractBuildingPlan(type, AUnitTypeWrapper.EVOLUTION_CHAMBER_TYPE, PLACE_FOR_EVOLUTION_CHAMBER,
//                        MORPH_TO_EVOLUTION_CHAMBER, FIND_PLACE_FOR_EVOLUTION_CHAMBER, new HashSet<>(Arrays.asList(WORKER_SCOUT, EXPAND,
//                                MORPH_TO_POOL, MORPH_TO_SPIRE, MORPH_TO_HYDRALISK_DEN, MORPH_TO_EXTRACTOR, MORPH_TO_CREEP_COLONY)));

        //stop on failed build intention
        ConfigurationWithCommand.WithActingCommandDesiredBySelf stopBuild = ConfigurationWithCommand
            .WithActingCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ActCommand.Own(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                AUnitWithCommands me = intention.returnFactValueForGivenKey(IS_UNIT).get();
                return (me.getLastCommand().isPresent() && me.getLastCommand().get()
                    .getUnitCommandType().equals(UnitCommandType.Gather)) || me.stop();
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    memory.returnFactValueForGivenKey(HAS_SOMETHING_TO_BUILD).isPresent()
                        && memory.returnFactValueForGivenKey(HAS_SOMETHING_TO_BUILD).get()
                        && !dataForDecision.madeDecisionToAny()
                )
                .desiresToConsider(new HashSet<>(Arrays
                    .asList(DesiresKeys.EXPAND, DesiresKeys.MORPH_TO_POOL,
                        DesiresKeys.MORPH_TO_SPIRE, DesiresKeys.MORPH_TO_HYDRALISK_DEN,
                        DesiresKeys.MORPH_TO_EXTRACTOR, DesiresKeys.MORPH_TO_CREEP_COLONY,
                        DesiresKeys.MORPH_TO_EVOLUTION_CHAMBER, DesiresKeys.BUILD)))
                .build()
            )
            .reactionOnChangeStrategy(
                (memory, desireParameters) -> memory
                    .eraseFactValueForGivenKey(HAS_SOMETHING_TO_BUILD))
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .build();
        type.addConfiguration(DesiresKeys.STOP_BUILD, stopBuild);

      })
      .desiresWithIntentionToReason(
          new HashSet<>(Arrays.asList(DesiresKeys.SURROUNDING_UNITS_AND_LOCATION,
              DesiresKeys.UPDATE_BELIEFS_ABOUT_WORKER_ACTIVITIES, DesiresKeys.MORPHING_TO)))
      .desiresWithIntentionToAct(
          new HashSet<>(Arrays.asList(DesiresKeys.RETURN_TO_BASE, DesiresKeys.STOP_BUILD)))
      .build();

  /**
   * Init building plan
   */
  private static void initAbstractBuildingPlan(AgentType type, AUnitTypeWrapper typeOfBuilding,
      FactKey<ATilePosition> placeForBuilding, DesireKey reactOn,
      DesireKey findPlace, Set<DesireKey> desiresToConsider) {

    //abstract plan for building
    ConfigurationWithAbstractPlan buildPlan = ConfigurationWithAbstractPlan
        .builder()
        .reactionOnChangeStrategy((memory, desireParameters) -> {
          memory.updateFact(BASE_TO_MOVE,
              desireParameters.returnFactValueForGivenKey(BASE_TO_MOVE).get());
          memory.updateFact(HAS_SOMETHING_TO_BUILD, true);
//          log.error("Commitmed to " + reactOn.getName());
        })
        .reactionOnChangeStrategyInIntention((memory, desireParameters) -> {
          memory.eraseFactValueForGivenKey(placeForBuilding);
          memory.eraseFactValueForGivenKey(BASE_TO_MOVE);
          memory.eraseFactValueForGivenKey(HAS_SOMETHING_TO_BUILD);
//          log.error("Removed commitment to " + reactOn.getName());
        }).decisionInDesire(CommitmentDeciderInitializer.builder()
            .decisionStrategy((dataForDecision, memory) -> !dataForDecision.madeDecisionToAny()
                && dataForDecision.returnFactValueForGivenKey(BASE_TO_MOVE).isPresent()
                //is idle or no one is idle
                && (memory.returnFactValueForGivenKey(IS_UNIT).get().isIdle()
                || dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_IDLE_DRONES) == 0)
                //is on location or no one is on location
                && (dataForDecision.returnFactValueForGivenKey(BASE_TO_MOVE).get().equals(
                memory.returnFactValueForGivenKey(IS_UNIT).get().getNearestBaseLocation()
                    .orElse(null))
                || memory.getReadOnlyMemoriesForAgentType(DRONE)
                .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(REPRESENTS_UNIT)
                    .get().getNearestBaseLocation())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .noneMatch(aBaseLocationWrapper -> aBaseLocationWrapper
                    .equals(dataForDecision.returnFactValueForGivenKey(BASE_TO_MOVE).get())))
                //resources
                && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_MINERALS) >= (
                typeOfBuilding.getMineralPrice() * 0.9)
                && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_GAS) >= (
                typeOfBuilding.getGasPrice() * 0.9)
                //is this type of building not locked
                && !BuildLockerService.getInstance().isLocked(typeOfBuilding))
            .globalBeliefTypesByAgentType(
                new HashSet<>(Arrays.asList(COUNT_OF_MINERALS, COUNT_OF_GAS, COUNT_OF_IDLE_DRONES)))
            .desiresToConsider(
                Stream.concat(desiresToConsider.stream(), Stream.of(reactOn))
                    .collect(Collectors.toSet())).build())
        .decisionInIntention(CommitmentDeciderInitializer.builder().decisionStrategy(
            (dataForDecision, memory) -> dataForDecision.madeDecisionToAny()
                //cancel where there is not enough minerals
                || dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_MINERALS) <
                (typeOfBuilding.getMineralPrice() * 0.8)
                || dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_GAS) <
                (typeOfBuilding.getGasPrice() * 0.8)
                //is this type of building is locked
                || BuildLockerService.getInstance().isLocked(typeOfBuilding))
            .globalBeliefTypesByAgentType(
                new HashSet<>(Arrays.asList(COUNT_OF_MINERALS, COUNT_OF_GAS)))
            .desiresToConsider(desiresToConsider)
            .build()).desiresWithIntentionToAct(
            new HashSet<>(Arrays.asList(DesiresKeys.BUILD, DesiresKeys.GO_TO_BASE, findPlace)))
        .build();
    type.addConfiguration(reactOn, buildPlan, false);

    //move to location
    ConfigurationWithCommand.WithActingCommandDesiredBySelf moveToBase = ConfigurationWithCommand.
        WithActingCommandDesiredBySelf.builder()
        .commandCreationStrategy(intention -> new ActCommand.Own(intention) {
          @Override
          public boolean act(WorkingMemory memory) {
            AUnitWithCommands me = intention.returnFactValueForGivenKey(IS_UNIT).get();
            APosition destination = intention.returnFactValueForGivenKey(BASE_TO_MOVE).get()
                .getPosition();
            me.move(destination);
            return true;
          }
        })
        .decisionInDesire(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) -> !memory.returnFactValueForGivenKey(BASE_TO_MOVE).get()
                    .equals(memory.returnFactValueForGivenKey(REPRESENTS_UNIT).get()
                        .getNearestBaseLocation().orElse(null)))
            .useFactsInMemory(true)
            .build()
        )
        .decisionInIntention(CommitmentDeciderInitializer.builder()
            .decisionStrategy((dataForDecision, memory) ->
                memory.returnFactValueForGivenKey(BASE_TO_MOVE).get()
                    .equals(memory.returnFactValueForGivenKey(REPRESENTS_UNIT).get()
                        .getNearestBaseLocation().orElse(null)))
            .build())
        .build();
    type.addConfiguration(DesiresKeys.GO_TO_BASE, reactOn, moveToBase);

    //find place near the building
    ConfigurationWithCommand.WithActingCommandDesiredBySelf findPlaceReasoning = ConfigurationWithCommand.WithActingCommandDesiredBySelf
        .builder()
        .reactionOnChangeStrategy((memory, desireParameters) -> {
          AUnitWithCommands me = memory.returnFactValueForGivenKey(IS_UNIT).get();
          if (me.getNearestBaseLocation().isPresent()) {
            BotFacade.ADDITIONAL_OBSERVATIONS_PROCESSOR.requestObservation((mem, environment) -> {
              Optional<ATilePosition> place = Util.getBuildTile(typeOfBuilding,
                  me.getNearestBaseLocation().get().getTilePosition(), me, environment);

              //keep unoccupied place
              if (place.isPresent()) {
                memory.updateFact(placeForBuilding, place.get());
              } else {
                memory.eraseFactValueForGivenKey(placeForBuilding);
              }
              return true;
            }, memory, DRONE);
          }
        })
        .commandCreationStrategy(intention -> new ActCommand.Own(intention) {
          @Override
          public boolean act(WorkingMemory memory) {
            AUnitWithCommands me = intention.returnFactValueForGivenKey(IS_UNIT).get();
            if (me.getNearestBaseLocation().isPresent()) {

              //move somewhere else in location only if place to build is not present
              if (!memory.returnFactValueForGivenKey(placeForBuilding).isPresent()) {

                Optional<APosition> aPlaceToGo = memory.returnFactValueForGivenKey(PLACE_TO_GO);
                if (!aPlaceToGo.isPresent() || me.getPosition().distanceTo(aPlaceToGo.get()) < 2) {
                  Optional<ReadOnlyMemory> memoryOptional = memory.getReadOnlyMemoriesForAgentType(
                      AgentTypes.BASE_LOCATION)
                      .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                          IS_BASE_LOCATION).get().equals(
                          me.getNearestBaseLocation().orElse(null)))
                      .findAny();
                  if (memoryOptional.isPresent()) {
                    List<AUnit> unitsOnLocation = memoryOptional.get()
                        .returnFactSetValueForGivenKey(OWN_BUILDING)
                        .orElse(Stream.empty())
                        .collect(Collectors.toList());
                    if (unitsOnLocation.isEmpty()) {
                      memory.updateFact(PLACE_TO_GO, intention.returnFactValueForGivenKey(
                          BASE_TO_MOVE).get().getPosition());
                    } else {
                      memory.updateFact(PLACE_TO_GO, unitsOnLocation.get(
                          RANDOM.nextInt(unitsOnLocation.size())).getPosition());
                    }
                  } else {
                    memory.updateFact(PLACE_TO_GO,
                        intention.returnFactValueForGivenKey(BASE_TO_MOVE).get().getPosition());
                  }
                }
                me.move(memory.returnFactValueForGivenKey(PLACE_TO_GO).get());
              }
            }
            return true;
          }
        })
        .decisionInDesire(CommitmentDeciderInitializer.builder()
            .decisionStrategy((dataForDecision, memory) -> !dataForDecision.madeDecisionToAny()
                && memory.returnFactValueForGivenKey(BASE_TO_MOVE).get().equals(
                memory.returnFactValueForGivenKey(REPRESENTS_UNIT).get().getNearestBaseLocation()
                    .orElse(null)))
            .desiresToConsider(new HashSet<>(Collections.singleton(DesiresKeys.GO_TO_BASE)))
            .build())
        .decisionInIntention(CommitmentDeciderInitializer.builder()
            .decisionStrategy((dataForDecision, memory) -> true)
            .build())
        .build();
    type.addConfiguration(findPlace, reactOn, findPlaceReasoning);

    //morph to building
    ConfigurationWithCommand.WithActingCommandDesiredBySelf build = ConfigurationWithCommand.
        WithActingCommandDesiredBySelf.builder()
        .commandCreationStrategy(intention -> new ActCommand.Own(intention) {
          @Override
          public boolean act(WorkingMemory memory) {
            if (intention.returnFactValueForGivenKey(placeForBuilding).isPresent()) {
              intention.returnFactValueForGivenKey(IS_UNIT).get().build(typeOfBuilding,
                  intention.returnFactValueForGivenKey(placeForBuilding).get());
            }
            return true;
          }
        })
        .decisionInDesire(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) -> memory.returnFactValueForGivenKey(placeForBuilding)
                    .isPresent())
            .build()
        )
        .reactionOnChangeStrategyInIntention((memory, desireParameters) -> {
          memory.eraseFactValueForGivenKey(placeForBuilding);
        })
        .decisionInIntention(CommitmentDeciderInitializer.builder()
            .decisionStrategy(
                (dataForDecision, memory) -> true)
            .build())
        .build();
    type.addConfiguration(DesiresKeys.BUILD, reactOn, build);
  }

}
