package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.units;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_HATCHERIES;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_HYDRALISK_DENS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_MINERALS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_MORPHING_OVERLORDS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_POOLS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_SPIRES;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.COUNT_OF_SUPPLY_BY_OVERLORDS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.CURRENT_POPULATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactConverters.MAX_POPULATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.HAS_EXTRACTOR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.MINERAL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.MORPH_TO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.REPRESENTS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.WORKER_ON_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.DRONE_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.HYDRALISK_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.MUTALISK_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.OVERLORD_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.ZERGLING_TYPE;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitOfPlayer;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import aic.gas.sc.gg_bot.bot.service.implementation.BuildLockerService;
import aic.gas.sc.gg_bot.mas.model.knowledge.ReadOnlyMemory;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithCommand;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Stream;

public class LarvaAgentType {

  public static final AgentTypeUnit LARVA = AgentTypeUnit.builder()
      .agentTypeID(AgentTypes.LARVA)
      .usingTypesForFacts(new HashSet<>(Collections.singletonList(IS_MORPHING_TO)))
      .initializationStrategy(type -> {

        //morph to overlord
        ConfigurationWithCommand.WithActingCommandDesiredByOtherAgent morphToOverlord = ConfigurationWithCommand.
            WithActingCommandDesiredByOtherAgent.builder()
            .commandCreationStrategy(intention -> new ActCommand.DesiredByAnotherAgent(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                return intention.returnFactValueForGivenKey(IS_UNIT).get().morph(
                    intention.getDesireKey().returnFactValueForGivenKey(
                        MORPH_TO).get().returnType());
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> !dataForDecision.madeDecisionToAny()
                    && dataForDecision.getFeatureValueGlobalBeliefs(
                    COUNT_OF_MORPHING_OVERLORDS) == 0
                    && dataForDecision.getFeatureValueGlobalBeliefs(CURRENT_POPULATION)
                    >= dataForDecision.getFeatureValueGlobalBeliefs(MAX_POPULATION)
                    //is overlord locked
                    && !BuildLockerService.getInstance().isLocked(OVERLORD_TYPE)
                    && (
                    dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_MINERALS) >= OVERLORD_TYPE
                        .getMineralPrice()
                        && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_GAS)
                        >= OVERLORD_TYPE.getGasPrice())
                )
                .globalBeliefTypesByAgentType(new HashSet<>(
                    Arrays
                        .asList(CURRENT_POPULATION, MAX_POPULATION, COUNT_OF_SUPPLY_BY_OVERLORDS,
                            COUNT_OF_HATCHERIES, COUNT_OF_MINERALS, COUNT_OF_GAS)))
                .globalBeliefTypes(
                    new HashSet<>(Collections.singletonList(COUNT_OF_MORPHING_OVERLORDS)))
                .desiresToConsider(new HashSet<>(
                    Arrays.asList(DesiresKeys.MORPH_TO_OVERLORD, DesiresKeys.BOOST_GROUND_MELEE,
                        DesiresKeys.BOOST_GROUND_RANGED, DesiresKeys.BOOST_AIR,
                        DesiresKeys.MORPH_TO_DRONE)))
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> false)
                .build())
            .build();
        type.addConfiguration(DesiresKeys.MORPH_TO_OVERLORD, morphToOverlord);

        //morph to zergling
        ConfigurationWithCommand.WithActingCommandDesiredByOtherAgent morphToZergling = ConfigurationWithCommand.
            WithActingCommandDesiredByOtherAgent.builder()
            .commandCreationStrategy(intention -> new ActCommand.DesiredByAnotherAgent(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                return intention.returnFactValueForGivenKey(IS_UNIT).get().morph(
                    intention.getDesireKey().returnFactValueForGivenKey(
                        MORPH_TO).get().returnType());
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> !dataForDecision.madeDecisionToAny()
                    && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_POOLS) != 0
                    && dataForDecision.getFeatureValueGlobalBeliefs(CURRENT_POPULATION)
                    < dataForDecision.getFeatureValueGlobalBeliefs(MAX_POPULATION)
                    //is zergling locked
                    && !BuildLockerService.getInstance().isLocked(ZERGLING_TYPE) && (
                    dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_MINERALS) >= ZERGLING_TYPE
                        .getMineralPrice()
                        && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_GAS)
                        >= ZERGLING_TYPE.getGasPrice())
                )
                .globalBeliefTypesByAgentType(new HashSet<>(
                    Arrays.asList(CURRENT_POPULATION, MAX_POPULATION, COUNT_OF_POOLS,
                        COUNT_OF_MINERALS, COUNT_OF_GAS)))
                .desiresToConsider(new HashSet<>(
                    Arrays.asList(DesiresKeys.MORPH_TO_OVERLORD, DesiresKeys.BOOST_GROUND_MELEE,
                        DesiresKeys.BOOST_GROUND_RANGED, DesiresKeys.BOOST_AIR,
                        DesiresKeys.MORPH_TO_DRONE)))
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_POOLS) == 0
                        || dataForDecision.getFeatureValueGlobalBeliefs(CURRENT_POPULATION)
                        >= dataForDecision.getFeatureValueGlobalBeliefs(MAX_POPULATION))
                .globalBeliefTypesByAgentType(new HashSet<>(
                    Arrays.asList(CURRENT_POPULATION, MAX_POPULATION, COUNT_OF_POOLS)))
                .build())
            .build();
        type.addConfiguration(DesiresKeys.BOOST_GROUND_MELEE, morphToZergling);

        //morph to hydras
        ConfigurationWithCommand.WithActingCommandDesiredByOtherAgent morphToHydra = ConfigurationWithCommand.
            WithActingCommandDesiredByOtherAgent.builder()
            .commandCreationStrategy(intention -> new ActCommand.DesiredByAnotherAgent(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                return intention.returnFactValueForGivenKey(IS_UNIT).get().morph(
                    intention.getDesireKey().returnFactValueForGivenKey(
                        MORPH_TO).get().returnType());
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> !dataForDecision.madeDecisionToAny()
                    && dataForDecision.getFeatureValueGlobalBeliefs(
                    COUNT_OF_HYDRALISK_DENS) != 0 && dataForDecision.getFeatureValueGlobalBeliefs(
                    CURRENT_POPULATION)
                    < dataForDecision.getFeatureValueGlobalBeliefs(MAX_POPULATION)
                    //is hydralisk locked
                    && !BuildLockerService.getInstance().isLocked(HYDRALISK_TYPE) && (
                    dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_MINERALS)
                        >= HYDRALISK_TYPE
                        .getMineralPrice()
                        && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_GAS)
                        >= HYDRALISK_TYPE.getGasPrice())
                )
                .globalBeliefTypesByAgentType(new HashSet<>(
                    Arrays.asList(CURRENT_POPULATION, MAX_POPULATION, COUNT_OF_HYDRALISK_DENS,
                        COUNT_OF_GAS, COUNT_OF_MINERALS)))
                .desiresToConsider(new HashSet<>(
                    Arrays.asList(DesiresKeys.MORPH_TO_OVERLORD, DesiresKeys.BOOST_GROUND_MELEE,
                        DesiresKeys.BOOST_GROUND_RANGED, DesiresKeys.BOOST_AIR,
                        DesiresKeys.MORPH_TO_DRONE)))
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) ->
                    dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_HYDRALISK_DENS) == 0
                        || dataForDecision.getFeatureValueGlobalBeliefs(CURRENT_POPULATION)
                        >= dataForDecision.getFeatureValueGlobalBeliefs(MAX_POPULATION))
                .globalBeliefTypesByAgentType(new HashSet<>(
                    Arrays.asList(CURRENT_POPULATION, MAX_POPULATION, COUNT_OF_HYDRALISK_DENS)))
                .build())
            .build();
        type.addConfiguration(DesiresKeys.BOOST_GROUND_RANGED, morphToHydra);

        //morph to mutalisk
        ConfigurationWithCommand.WithActingCommandDesiredByOtherAgent morphToMutalisk = ConfigurationWithCommand.
            WithActingCommandDesiredByOtherAgent.builder()
            .commandCreationStrategy(intention -> new ActCommand.DesiredByAnotherAgent(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                return intention.returnFactValueForGivenKey(IS_UNIT).get().morph(
                    intention.getDesireKey().returnFactValueForGivenKey(
                        MORPH_TO).get().returnType());
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> !dataForDecision.madeDecisionToAny()
                    && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_SPIRES) != 0
                    && dataForDecision.getFeatureValueGlobalBeliefs(CURRENT_POPULATION)
                    < dataForDecision.getFeatureValueGlobalBeliefs(MAX_POPULATION)
                    //is mutalisk locked
                    && !BuildLockerService.getInstance().isLocked(MUTALISK_TYPE) && (
                    dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_MINERALS) >= MUTALISK_TYPE
                        .getMineralPrice()
                        && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_GAS)
                        >= MUTALISK_TYPE.getGasPrice())
                )
                .globalBeliefTypesByAgentType(new HashSet<>(
                    Arrays.asList(CURRENT_POPULATION, MAX_POPULATION, COUNT_OF_SPIRES,
                        COUNT_OF_MINERALS, COUNT_OF_GAS)))
                .desiresToConsider(new HashSet<>(
                    Arrays.asList(DesiresKeys.MORPH_TO_OVERLORD, DesiresKeys.BOOST_GROUND_MELEE,
                        DesiresKeys.BOOST_GROUND_RANGED, DesiresKeys.BOOST_AIR,
                        DesiresKeys.MORPH_TO_DRONE)))
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        COUNT_OF_SPIRES) == 0
                        || dataForDecision.getFeatureValueGlobalBeliefs(CURRENT_POPULATION)
                        >= dataForDecision.getFeatureValueGlobalBeliefs(MAX_POPULATION))
                .globalBeliefTypesByAgentType(new HashSet<>(
                    Arrays.asList(CURRENT_POPULATION, MAX_POPULATION, COUNT_OF_SPIRES)))
                .build())
            .build();
        type.addConfiguration(DesiresKeys.BOOST_AIR, morphToMutalisk);

        //morph to drone
        ConfigurationWithCommand.WithActingCommandDesiredByOtherAgent mine = ConfigurationWithCommand.
            WithActingCommandDesiredByOtherAgent.builder()
            .commandCreationStrategy(intention -> new ActCommand.DesiredByAnotherAgent(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                return intention.returnFactValueForGivenKey(IS_UNIT).get().morph(
                    intention.getDesireKey().returnFactValueForGivenKey(MORPH_TO).get()
                        .returnType());
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> {
                      //check if drone can be build
                      if (BuildLockerService.getInstance().isLocked(DRONE_TYPE)) {
                        return false;
                      }
                      if (dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_MINERALS) < DRONE_TYPE
                          .getMineralPrice()
                          && dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_GAS) < DRONE_TYPE
                          .getGasPrice()) {
                        return false;
                      }
                      if (dataForDecision.madeDecisionToAny()
                          || dataForDecision.getFeatureValueGlobalBeliefs(
                          CURRENT_POPULATION)
                          >= dataForDecision.getFeatureValueGlobalBeliefs(MAX_POPULATION)) {
                        return false;
                      }
                      AUnitOfPlayer me = memory.returnFactValueForGivenKey(REPRESENTS_UNIT).get();
                      if (!me.getEnemyUnitsInRadiusOfSight().isEmpty()) {
                        return false;
                      }
                      Optional<ABaseLocationWrapper> locationWrapper = me.getNearestBaseLocation();
                      Optional<ReadOnlyMemory> baseLarvaIsLocatedBeliefs = memory
                          .getReadOnlyMemoriesForAgentType(
                              AgentTypes.BASE_LOCATION)
                          .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                              IS_BASE_LOCATION).isPresent())
                          .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                              IS_BASE_LOCATION).get().equals(locationWrapper.orElse(null)))
                          .findAny();
                      if (!baseLarvaIsLocatedBeliefs.isPresent()) {
                        return false;
                      }
                      ReadOnlyMemory readOnlyMemory = baseLarvaIsLocatedBeliefs.get();
                      long countOfMinerals = readOnlyMemory.returnFactSetValueForGivenKey(MINERAL)
                          .orElse(Stream.empty()).count();
                      long extractors = readOnlyMemory.returnFactSetValueForGivenKey(
                          HAS_EXTRACTOR).map(Stream::count).orElse(0L);
                      long workers = readOnlyMemory.returnFactSetValueForGivenKey(WORKER_ON_BASE)
                          .map(Stream::count).orElse(0L);
                      return ((countOfMinerals * 2.5) + (extractors * 3)) > workers;
                    }
                )
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Arrays.asList(CURRENT_POPULATION, MAX_POPULATION, COUNT_OF_GAS,
                        COUNT_OF_MINERALS)))
                .desiresToConsider(new HashSet<>(
                    Arrays.asList(DesiresKeys.MORPH_TO_OVERLORD, DesiresKeys.BOOST_GROUND_MELEE,
                        DesiresKeys.BOOST_GROUND_RANGED, DesiresKeys.BOOST_AIR,
                        DesiresKeys.MORPH_TO_DRONE)))
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> dataForDecision.getFeatureValueGlobalBeliefs(
                        CURRENT_POPULATION) >= dataForDecision
                        .getFeatureValueGlobalBeliefs(MAX_POPULATION)
                        || dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_MINERALS)
                        < DRONE_TYPE.getMineralPrice()
                        || dataForDecision.getFeatureValueGlobalBeliefs(COUNT_OF_GAS)
                        < DRONE_TYPE.getGasPrice()
                        || BuildLockerService.getInstance().isLocked(DRONE_TYPE))
                .globalBeliefTypesByAgentType(
                    new HashSet<>(Arrays
                        .asList(CURRENT_POPULATION, MAX_POPULATION, COUNT_OF_MINERALS,
                            COUNT_OF_GAS)))
                .build())
            .build();
        type.addConfiguration(DesiresKeys.MORPH_TO_DRONE, mine);

        //reason about morphing
        type.addConfiguration(DesiresKeys.MORPHING_TO, AgentTypeUnit.beliefsAboutMorphing);

      })
      .desiresWithIntentionToReason(
          new HashSet<>(Collections.singletonList(DesiresKeys.MORPHING_TO)))
      .build();
}
