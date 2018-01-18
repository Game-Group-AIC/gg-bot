package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.units;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_IDLE_DRONES;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_MINERALS_ON_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.IS_CARRYING_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.IS_CARRYING_MINERAL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.IS_MINING_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.IS_MINING_MINERAL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_MOVE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_SCOUT_BY_WORKER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.HAS_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.HAS_EXTRACTOR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_GATHERING_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_GATHERING_MINERALS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_ISLAND;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_START_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LAST_TIME_SCOUTED;
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
  private static final List<DesireKey> BUILDING_DESIRES = Arrays
      .asList(DesiresKeys.EXPAND, DesiresKeys.MORPH_TO_POOL, DesiresKeys.MORPH_TO_SPIRE,
          DesiresKeys.MORPH_TO_HYDRALISK_DEN, DesiresKeys.MORPH_TO_EXTRACTOR,
          DesiresKeys.MORPH_TO_CREEP_COLONY, DesiresKeys.MORPH_TO_EVOLUTION_CHAMBER);

  //worker
  public static final AgentTypeUnit DRONE = AgentTypeUnit.builder()
      .agentTypeID(AgentTypes.DRONE)
      .usingTypesForFacts(
          new HashSet<>(Arrays.asList(MINING_MINERAL, MINERAL_TO_MINE, IS_MORPHING_TO,
              IS_GATHERING_MINERALS, IS_GATHERING_GAS, BASE_TO_SCOUT_BY_WORKER, PLACE_TO_GO,
              PLACE_FOR_POOL, PLACE_FOR_EXPANSION, BASE_TO_MOVE, PLACE_FOR_EXTRACTOR,
              MINING_IN_EXTRACTOR, PLACE_FOR_SPIRE, PLACE_FOR_HYDRALISK_DEN, PLACE_FOR_CREEP_COLONY,
              PLACE_FOR_EVOLUTION_CHAMBER, PLACE_TO_REACH)))
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
                    && dataForDecision.returnFactSetValueForGivenKey(HAS_EXTRACTOR).get().findAny()
                    .get().getNearestBaseLocation().get().equals(
                        memory.returnFactValueForGivenKey(IS_UNIT).get().getNearestBaseLocation()
                            .orElse(null))
                    //is not gathering resources
                    && !memory.returnFactValueForGivenKey(IS_GATHERING_MINERALS).orElse(false)
                    && !memory.returnFactValueForGivenKey(IS_GATHERING_GAS).orElse(false)
                ))
                .useFactsInMemory(true)
                .desiresToConsider(Stream.concat(Stream.of(DesiresKeys.WORKER_SCOUT,
                    DesiresKeys.GO_TO_BASE, DesiresKeys.MINE_MINERALS_IN_BASE,
                    DesiresKeys.MINE_GAS_IN_BASE), BUILDING_DESIRES.stream())
                    .collect(Collectors.toSet()))
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
                .desiresToConsider(Stream.concat(Stream.of(DesiresKeys.WORKER_SCOUT,
                    DesiresKeys.GO_TO_BASE, DesiresKeys.MINE_MINERALS_IN_BASE),
                    BUILDING_DESIRES.stream()).collect(Collectors.toSet()))
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
                        && dataForDecision.getFeatureValueBeliefs(IS_CARRYING_GAS) == 0)
                .beliefTypes(new HashSet<>(Arrays.asList(IS_MINING_GAS, IS_CARRYING_GAS)))
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
                        || (me.getOrder().isPresent() && (
                        me.getOrder().get().equals(Order.MiningMinerals)
                            || me.getOrder().get().equals(Order.MoveToMinerals) || me.getOrder()
                            .get().equals(Order.WaitForMinerals) || me.getOrder().get()
                            .equals(Order.ReturnMinerals))));
                memory.updateFact(IS_GATHERING_GAS, me.isCarryingGas() || me.isGatheringGas()
                    || (me.getOrder().isPresent() && (me.getOrder().get().equals(Order.HarvestGas)
                    || me.getOrder().get().equals(Order.MoveToGas) || me.getOrder().get().equals(
                    Order.WaitForGas)
                    || me.getOrder().get().equals(Order.ReturnGas))));
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
                    //resources are under saturated
                    && dataForDecision.getNumberOfCommittedAgents() <= 1.5 * dataForDecision
                    .getFeatureValueDesireBeliefSets(COUNT_OF_MINERALS_ON_BASE))
                    //is not gathering resources
                    && !memory.returnFactValueForGivenKey(IS_GATHERING_MINERALS).orElse(false)
                    && !memory.returnFactValueForGivenKey(IS_GATHERING_GAS).orElse(false)
                )
                .useFactsInMemory(true)
                .parameterValueSetTypes(
                    new HashSet<>(Collections.singletonList(COUNT_OF_MINERALS_ON_BASE)))
                .desiresToConsider(Stream.concat(Stream
                        .of(DesiresKeys.WORKER_SCOUT, DesiresKeys.MINE_GAS_IN_BASE,
                            DesiresKeys.GO_TO_BASE),
                    BUILDING_DESIRES.stream()).collect(Collectors.toSet()))
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.madeDecisionToAny() ||
                        //minerals are over saturated
                        dataForDecision.getNumberOfCommittedAgents() >= 2 * dataForDecision
                            .getFeatureValueDesireBeliefSets(COUNT_OF_MINERALS_ON_BASE)
                        //nobody is mining gas at same location
                        || memory.getReadOnlyMemoriesForAgentType(AgentTypes.BASE_LOCATION)
                        .filter(readOnlyMemory -> readOnlyMemory
                            .returnFactValueForGivenKey(IS_BASE_LOCATION)
                            .get().equals(memory.returnFactValueForGivenKey(IS_UNIT).get()
                                .getNearestBaseLocation().orElse(null)))
                        .anyMatch(readOnlyMemory ->
                            readOnlyMemory.returnFactSetValueForGivenKey(HAS_EXTRACTOR)
                                .orElse(Stream.empty()).count() > 0
                                && readOnlyMemory.returnFactSetValueForGivenKey(WORKER_MINING_GAS)
                                .orElse(Stream.empty()).count() == 0))
                .parameterValueSetTypes(
                    new HashSet<>(Collections.singletonList(COUNT_OF_MINERALS_ON_BASE)))
                .desiresToConsider(Stream.concat(Stream
                    .of(DesiresKeys.WORKER_SCOUT, DesiresKeys.GO_TO_BASE,
                        DesiresKeys.MINE_GAS_IN_BASE), BUILDING_DESIRES.stream()).collect(
                    Collectors.toSet()))
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

                //occupied minerals
                Set<AUnit> mineralsBeingMined = memory.getReadOnlyMemoriesForAgentType(DRONE)
                    .filter(readOnlyMemory -> readOnlyMemory.getAgentId() != memory.getAgentId())
                    .map(
                        readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(MINING_MINERAL))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());

                //free minerals
                Set<AUnit> mineralsToMine = intention
                    .returnFactSetValueOfParentIntentionForGivenKey(MINERAL).get()
                    .filter(unit -> !mineralsBeingMined.contains(unit))
                    .collect(Collectors.toSet());

                if (!mineralsToMine.isEmpty()) {

                  //select free nearest mineral to worker
                  APosition myPosition = intention.returnFactValueForGivenKey(
                      IS_UNIT).get().getPosition();
                  Optional<AUnit> mineralToPick = mineralsToMine.stream()
                      .min(Comparator.comparingDouble(o -> myPosition.distanceTo(o.getPosition())));
                  mineralToPick.ifPresent(aUnit -> memory.updateFact(MINERAL_TO_MINE, aUnit));
                } else {
                  memory.eraseFactValueForGivenKey(MINERAL_TO_MINE);
                }
                return true;
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    //constantly look for minerals when you do not mine
                    !memory.returnFactValueForGivenKey(IS_GATHERING_MINERALS).orElse(false))
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
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf unselectedMineral = ConfigurationWithCommand.
            WithReasoningCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                memory.eraseFactValueForGivenKey(MINING_MINERAL);
                return true;
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        //is waiting on minerals - try to find better one
                        (memory.returnFactValueForGivenKey(REPRESENTS_UNIT).get().getOrder()
                            .isPresent()
                            && memory.returnFactValueForGivenKey(REPRESENTS_UNIT).get().getOrder()
                            .get().equals(Order.WaitForMinerals))
                            //release mineral when you gathered resources
                            || dataForDecision.getFeatureValueBeliefs(IS_CARRYING_MINERAL) == 1)
                .beliefTypes(new HashSet<>(Collections.singletonList(IS_CARRYING_MINERAL)))
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build()
            )
            .build();
        type.addConfiguration(DesiresKeys.UNSELECT_MINERAL, DesiresKeys.MINE_MINERALS_IN_BASE,
            unselectedMineral);

        //go to mine it
        ConfigurationWithCommand.WithActingCommandDesiredBySelf mine = ConfigurationWithCommand.
            WithActingCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ActCommand.Own(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                boolean hasStartedMining = intention.returnFactValueForGivenKey(IS_UNIT).get()
                    .gather(intention.returnFactValueForGivenKeyInDesireParameters(MINERAL_TO_MINE)
                        .get());
                if (hasStartedMining) {
                  memory.updateFact(MINING_MINERAL,
                      intention.returnFactValueForGivenKeyInDesireParameters(MINERAL_TO_MINE)
                          .get());
                }
                return hasStartedMining;
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        //mineral is selected and we do not cary any
                        memory.returnFactValueForGivenKey(MINERAL_TO_MINE).isPresent()
                            && dataForDecision.getFeatureValueBeliefs(IS_CARRYING_MINERAL) == 0
                            //mineral has changed or it is not set
                            && (dataForDecision.getFeatureValueBeliefs(IS_MINING_MINERAL) == 0
                            || !memory.returnFactValueForGivenKey(MINERAL_TO_MINE).get()
                            .equals(memory.returnFactValueForGivenKey(MINING_MINERAL).get())))
                .beliefTypes(new HashSet<>(Arrays.asList(IS_MINING_MINERAL, IS_CARRYING_MINERAL)))
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
                  return me.returnCargo();
                } else {
                  if (intention.returnFactValueForGivenKey(PLACE_TO_GO).isPresent()) {
                    return me.move(intention.returnFactValueForGivenKey(PLACE_TO_GO).get());
                  }
                }
                return true;
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    //is idle and has nothing to do
                    !dataForDecision.madeDecisionToAny() && memory
                        .returnFactValueForGivenKey(REPRESENTS_UNIT).get().isIdle()
                )
                .desiresToConsider(Stream.concat(Stream
                        .of(DesiresKeys.WORKER_SCOUT, DesiresKeys.MINE_MINERALS_IN_BASE,
                            DesiresKeys.MINE_GAS_IN_BASE, DesiresKeys.GO_TO_BASE),
                    BUILDING_DESIRES.stream())
                    .collect(Collectors.toSet()))
                .build()
            )
            .reactionOnChangeStrategy((memory, desireParameters) -> {
              AUnitOfPlayer me = memory.returnFactValueForGivenKey(REPRESENTS_UNIT).get();

              //skip when carrying cargo
              if (!me.isCarryingGas() && !me.isCarryingMinerals()) {

                //find base where can help
                Optional<ABaseLocationWrapper> basesToGo = memory.getReadOnlyMemoriesForAgentType(
                    AgentTypes.BASE_LOCATION)
                    //is base
                    .filter(
                        readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_BASE).get())
                    //is ready
                    .filter(readOnlyMemory -> readOnlyMemory.returnFactSetValueForGivenKey(HAS_BASE)
                        .orElse(Stream.empty())
                        .anyMatch(aUnitOfPlayer -> !aUnitOfPlayer.isBeingConstructed()))
                    //wants to have resources gathered
                    .filter(
                        readOnlyMemory -> readOnlyMemory.collectKeysOfDesiresInTreeCounts().keySet()
                            .stream().anyMatch(
                                desireKey -> desireKey.equals(DesiresKeys.MINE_MINERALS_IN_BASE)
                                    || desireKey.equals(DesiresKeys.MINE_GAS_IN_BASE)))
                    //is not saturated by workers
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
                    //closet base
                    .min(Comparator.comparingDouble(value -> value.distanceTo(me.getPosition())));

                //go there
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

        //build evolution chamber
        initAbstractBuildingPlan(type, AUnitTypeWrapper.EVOLUTION_CHAMBER_TYPE,
            PLACE_FOR_EVOLUTION_CHAMBER, DesiresKeys.MORPH_TO_EVOLUTION_CHAMBER,
            DesiresKeys.FIND_PLACE_FOR_EVOLUTION_CHAMBER, new HashSet<>(Arrays
                .asList(DesiresKeys.WORKER_SCOUT, DesiresKeys.EXPAND, DesiresKeys.MORPH_TO_POOL,
                    DesiresKeys.MORPH_TO_SPIRE, DesiresKeys.MORPH_TO_HYDRALISK_DEN,
                    DesiresKeys.MORPH_TO_EXTRACTOR, DesiresKeys.MORPH_TO_CREEP_COLONY)));

      })
      .desiresWithIntentionToReason(
          new HashSet<>(Arrays.asList(DesiresKeys.SURROUNDING_UNITS_AND_LOCATION,
              DesiresKeys.UPDATE_BELIEFS_ABOUT_WORKER_ACTIVITIES, DesiresKeys.MORPHING_TO)))
      .desiresWithIntentionToAct(
          new HashSet<>(Collections.singletonList(DesiresKeys.RETURN_TO_BASE)))
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
        .reactionOnChangeStrategy((memory, desireParameters) -> memory.updateFact(BASE_TO_MOVE,
            desireParameters.returnFactValueForGivenKey(BASE_TO_MOVE).get()))
        .reactionOnChangeStrategyInIntention((memory, desireParameters) -> {
          memory.eraseFactValueForGivenKey(placeForBuilding);
          memory.eraseFactValueForGivenKey(BASE_TO_MOVE);
        })
        .decisionInDesire(CommitmentDeciderInitializer.builder()
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
                    .equals(dataForDecision.returnFactValueForGivenKey(BASE_TO_MOVE).get()))))
            .globalBeliefTypesByAgentType(Collections.singleton(COUNT_OF_IDLE_DRONES))
            .desiresToConsider(
                Stream.concat(desiresToConsider.stream(), Stream.of(reactOn))
                    .collect(Collectors.toSet())).build())
        .decisionInIntention(CommitmentDeciderInitializer.builder().decisionStrategy(
            (dataForDecision, memory) -> dataForDecision.madeDecisionToAny())
            .desiresToConsider(desiresToConsider)
            .build())
        .desiresWithIntentionToAct(Stream.of(DesiresKeys.BUILD, DesiresKeys.GO_TO_BASE, findPlace)
            .collect(Collectors.toSet()))
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
              log.info("Morphing to " + typeOfBuilding.getName());
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
                (dataForDecision, memory) -> false)
            .build())
        .build();
    type.addConfiguration(DesiresKeys.BUILD, reactOn, build);
  }

}
