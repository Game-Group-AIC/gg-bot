package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.CREEP_COLONY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.DRONE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.EGG;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.EVOLUTION_CHAMBER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.EXTRACTOR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.HATCHERY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.HYDRALISK;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.HYDRALISK_DEN;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.LAIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.LARVA;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.MUTALISK;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.OVERLORD;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.SPAWNING_POOL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.SPIRE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.SPORE_COLONY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.SUNKEN_COLONY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.ZERGLING;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.HOLD_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_GATHERING_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_GATHERING_MINERALS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.REPRESENTS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.CREEP_COLONY_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.DRONE_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.EGG_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.EVOLUTION_CHAMBER_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.EXTRACTOR_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.HATCHERY_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.HYDRALISK_DEN_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.HYDRALISK_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.LAIR_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.LARVA_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.MUTALISK_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.OVERLORD_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.SPAWNING_POOL_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.SPIRE_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.SPORE_COLONY_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.SUNKEN_COLONY_TYPE;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.ZERGLING_TYPE;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APosition;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitOfPlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitWithCommands;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.UnitWrapperFactory;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.WrapperTypeFactory;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension.UnitWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_type_extension.UnitWatcherType;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.updating_strategies.Reasoning;
import aic.gas.sc.gg_bot.replay_parser.service.IAgentUnitHandler;
import bwapi.Game;
import bwapi.Order;
import bwapi.Unit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * Concrete implementation of IAgentUnitHandler
 */
@Slf4j
public class AgentUnitFactory implements IAgentUnitHandler {

  private static final Reasoning MORPHING_REASONING = (beliefs, mediatorService) -> {
    AUnitOfPlayer me = beliefs.returnFactValueForGivenKey(REPRESENTS_UNIT).get();
    if (me.getOrder().isPresent() && Stream
        .of(Order.ZergBuildingMorph, Order.IncompleteBuilding, Order.ZergUnitMorph)
        .anyMatch(order -> order == me.getOrder().get())) {
      if (!me.getTrainingQueue().isEmpty()) {
        beliefs.updateFact(IS_MORPHING_TO, me.getTrainingQueue().get(0));
      } else {
        beliefs.updateFact(IS_MORPHING_TO, me.getType());
      }
    } else {
      beliefs.eraseFactValueForGivenKey(IS_MORPHING_TO);
    }
  };

  private static final List<Order> ordersCheckForAttack = Arrays.asList(Order.AttackMove,
      Order.AttackTile, Order.AttackUnit, Order.HarassMove, Order.Move);

  private static final Reasoning UNIT_TARGET_LOCATION = (beliefs, mediatorService) -> {
    AUnitOfPlayer me = beliefs.returnFactValueForGivenKey(REPRESENTS_UNIT).get();
    Optional<Order> order = me.getOrder();
    if (order.isPresent() && ordersCheckForAttack.contains(order.get())) {

      //what is the target position
      APosition targetPosition = me.getTargetPosition()
          .orElse(me.getOrderTargetPosition().orElse(me.getPosition()));

      //select closest location to target - iteration over all base location agents
      Optional<ABaseLocationWrapper> holdInBaseLocation = mediatorService.getStreamOfWatchers()
          .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
              .equals(BASE_LOCATION.getName()))
          .map(agentWatcher -> agentWatcher.getBeliefs()
              .returnFactValueForGivenKey(IS_BASE_LOCATION))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .min(Comparator.comparingDouble(value -> value.getTilePosition()
              .distanceTo(targetPosition.getATilePosition())));
      beliefs.updateFact(HOLD_LOCATION, holdInBaseLocation.get());
    } else {
      beliefs.updateFact(HOLD_LOCATION, HOLD_LOCATION.getInitValue());
    }
  };

  private final Map<AUnitTypeWrapper, UnitWatcherType> agentConfigurationForUnitType = new HashMap<>();

  private final void initConfig() {
    agentConfigurationForUnitType.put(DRONE_TYPE, UnitWatcherType.builder()
        .agentTypeID(DRONE)
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(
            (beliefs, mediatorService) -> {
              AUnitOfPlayer me = beliefs.returnFactValueForGivenKey(REPRESENTS_UNIT).get();
              beliefs.updateFact(IS_GATHERING_MINERALS,
                  me.isCarryingMinerals() || me.isGatheringMinerals()
                      || (me.getOrder().isPresent() && (
                      me.getOrder().get().equals(Order.MiningMinerals)
                          || me.getOrder().get().equals(Order.MoveToMinerals) || me.getOrder().get()
                          .equals(Order.WaitForMinerals)
                          || me.getOrder().get().equals(Order.ReturnMinerals))));
              beliefs.updateFact(IS_GATHERING_GAS, me.isCarryingGas() || me.isGatheringGas()
                  || (me.getOrder().isPresent() && (me.getOrder().get().equals(Order.HarvestGas)
                  || me.getOrder().get().equals(Order.MoveToGas) || me.getOrder().get()
                  .equals(Order.WaitForGas)
                  || me.getOrder().get().equals(Order.ReturnGas))));
              MORPHING_REASONING.updateBeliefs(beliefs, mediatorService);
            }))
        .factKeys(Stream.of(IS_GATHERING_MINERALS, IS_GATHERING_GAS, IS_MORPHING_TO)
            .collect(Collectors.toSet()))
        .build());

    //buildings
    agentConfigurationForUnitType.put(HATCHERY_TYPE, UnitWatcherType.builder()
        .agentTypeID(HATCHERY)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    agentConfigurationForUnitType.put(SPAWNING_POOL_TYPE, UnitWatcherType.builder()
        .agentTypeID(SPAWNING_POOL)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    agentConfigurationForUnitType.put(EXTRACTOR_TYPE, UnitWatcherType.builder()
        .agentTypeID(EXTRACTOR)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    agentConfigurationForUnitType.put(LAIR_TYPE, UnitWatcherType.builder()
        .agentTypeID(LAIR)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    agentConfigurationForUnitType.put(SPIRE_TYPE, UnitWatcherType.builder()
        .agentTypeID(SPIRE)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    agentConfigurationForUnitType.put(EVOLUTION_CHAMBER_TYPE, UnitWatcherType.builder()
        .agentTypeID(EVOLUTION_CHAMBER)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    agentConfigurationForUnitType.put(HYDRALISK_DEN_TYPE, UnitWatcherType.builder()
        .agentTypeID(HYDRALISK_DEN)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    //static defense
    agentConfigurationForUnitType.put(SUNKEN_COLONY_TYPE, UnitWatcherType.builder()
        .agentTypeID(SUNKEN_COLONY)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    agentConfigurationForUnitType.put(CREEP_COLONY_TYPE, UnitWatcherType.builder()
        .agentTypeID(CREEP_COLONY)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    agentConfigurationForUnitType.put(SPORE_COLONY_TYPE, UnitWatcherType.builder()
        .agentTypeID(SPORE_COLONY)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());

    //population
    agentConfigurationForUnitType.put(OVERLORD_TYPE, UnitWatcherType.builder()
        .agentTypeID(OVERLORD)
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(
            (beliefs, mediatorService) -> {
            }))
        .build());

    //attack units
    agentConfigurationForUnitType.put(ZERGLING_TYPE, UnitWatcherType.builder()
        .agentTypeID(ZERGLING)
        .factKeys(new HashSet<>(Collections.singleton(HOLD_LOCATION)))
        .reasoning(
            new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(UNIT_TARGET_LOCATION))
        .build());
    agentConfigurationForUnitType.put(MUTALISK_TYPE, UnitWatcherType.builder()
        .agentTypeID(MUTALISK)
        .factKeys(new HashSet<>(Collections.singleton(HOLD_LOCATION)))
        .reasoning(
            new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(UNIT_TARGET_LOCATION))
        .build());
    agentConfigurationForUnitType.put(HYDRALISK_TYPE, UnitWatcherType.builder()
        .agentTypeID(HYDRALISK)
        .factKeys(new HashSet<>(Collections.singleton(HOLD_LOCATION)))
        .reasoning(
            new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(UNIT_TARGET_LOCATION))
        .build());

    AgentTypeID dummy = new AgentTypeID("DUMMY", 10000);
    AUnitTypeWrapper.OTHER_UNIT_TYPES.forEach(
        typeWrapper -> agentConfigurationForUnitType.put(typeWrapper, UnitWatcherType.builder()
            .agentTypeID(dummy)
            .factKeys(new HashSet<>(Collections.singleton(HOLD_LOCATION)))
            .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(
                UNIT_TARGET_LOCATION))
            .build()));

    //"barracks"
    agentConfigurationForUnitType.put(EGG_TYPE, UnitWatcherType.builder()
        .agentTypeID(EGG)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    agentConfigurationForUnitType.put(LARVA_TYPE, UnitWatcherType.builder()
        .agentTypeID(LARVA)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
  }

  @Override
  public Optional<UnitWatcher> createAgentForUnit(Unit unit, Game game) {
    if (agentConfigurationForUnitType.size() == 0) {
      initConfig();
    }

    Optional<UnitWatcherType> agentTypeUnit = Optional.ofNullable(
        agentConfigurationForUnitType.get(WrapperTypeFactory.createFrom(unit.getType())));
    if (agentTypeUnit.isPresent()) {
      Optional<AUnitWithCommands> wrappedUnit = Optional.ofNullable(
          UnitWrapperFactory.getCurrentWrappedUnitToCommand(unit, game.getFrameCount(), false));
      if (!wrappedUnit.isPresent()) {
        log.error("Could not initiate unit " + unit.getType());
        throw new RuntimeException("Could not initiate unit " + unit.getType());
      }
      UnitWatcher unitWatcher = new UnitWatcher(agentTypeUnit.get(), game, wrappedUnit.get(), unit);
      return Optional.of(unitWatcher);
    }
    return Optional.empty();
  }
}
