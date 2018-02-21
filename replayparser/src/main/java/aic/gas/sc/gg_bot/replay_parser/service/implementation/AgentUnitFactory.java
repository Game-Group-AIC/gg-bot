package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.*;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.*;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.*;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.*;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension.UnitWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_type_extension.UnitWatcherType;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.updating_strategies.Reasoning;
import aic.gas.sc.gg_bot.replay_parser.service.IAgentUnitHandler;
import bwapi.Game;
import bwapi.Order;
import bwapi.Unit;
import java.util.*;
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
              .equals(BASE_LOCATION.name()))
          .map(agentWatcher -> agentWatcher.getBeliefs()
              .returnFactValueForGivenKey(IS_BASE_LOCATION))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .min(Comparator.comparingDouble(value -> value.distanceTo(targetPosition)));
      beliefs.updateFact(HOLD_LOCATION, holdInBaseLocation.get());
    } else {
      beliefs.updateFact(HOLD_LOCATION, HOLD_LOCATION.getInitValue());
    }
  };

  private final Map<AUnitTypeWrapper, UnitWatcherType> agentConfigurationForUnitType = new HashMap<>();

  private final void initConfig() {
    agentConfigurationForUnitType.put(DRONE_TYPE, UnitWatcherType.builder()
        .agentType(DRONE)
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
        .agentType(HATCHERY)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    agentConfigurationForUnitType.put(SPAWNING_POOL_TYPE, UnitWatcherType.builder()
        .agentType(SPAWNING_POOL)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    agentConfigurationForUnitType.put(EXTRACTOR_TYPE, UnitWatcherType.builder()
        .agentType(EXTRACTOR)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    agentConfigurationForUnitType.put(LAIR_TYPE, UnitWatcherType.builder()
        .agentType(LAIR)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    agentConfigurationForUnitType.put(SPIRE_TYPE, UnitWatcherType.builder()
        .agentType(SPIRE)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    agentConfigurationForUnitType.put(EVOLUTION_CHAMBER_TYPE, UnitWatcherType.builder()
        .agentType(EVOLUTION_CHAMBER)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    agentConfigurationForUnitType.put(HYDRALISK_DEN_TYPE, UnitWatcherType.builder()
        .agentType(HYDRALISK_DEN)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    //static defense
    agentConfigurationForUnitType.put(SUNKEN_COLONY_TYPE, UnitWatcherType.builder()
        .agentType(SUNKEN_COLONY)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    agentConfigurationForUnitType.put(CREEP_COLONY_TYPE, UnitWatcherType.builder()
        .agentType(CREEP_COLONY)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    agentConfigurationForUnitType.put(SPORE_COLONY_TYPE, UnitWatcherType.builder()
        .agentType(SPORE_COLONY)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());

    //population
    agentConfigurationForUnitType.put(OVERLORD_TYPE, UnitWatcherType.builder()
        .agentType(OVERLORD)
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(
            (beliefs, mediatorService) -> {
            }))
        .build());

    //attack units
    agentConfigurationForUnitType.put(ZERGLING_TYPE, UnitWatcherType.builder()
        .agentType(ZERGLING)
        .factKeys(new HashSet<>(Collections.singleton(HOLD_LOCATION)))
        .reasoning(
            new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(UNIT_TARGET_LOCATION))
        .build());
    agentConfigurationForUnitType.put(MUTALISK_TYPE, UnitWatcherType.builder()
        .agentType(MUTALISK)
        .factKeys(new HashSet<>(Collections.singleton(HOLD_LOCATION)))
        .reasoning(
            new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(UNIT_TARGET_LOCATION))
        .build());
    agentConfigurationForUnitType.put(HYDRALISK_TYPE, UnitWatcherType.builder()
        .agentType(HYDRALISK)
        .factKeys(new HashSet<>(Collections.singleton(HOLD_LOCATION)))
        .reasoning(
            new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(UNIT_TARGET_LOCATION))
        .build());

    //"barracks"
    agentConfigurationForUnitType.put(EGG_TYPE, UnitWatcherType.builder()
        .agentType(EGG)
        .factKeys(new HashSet<>(Collections.singleton(IS_MORPHING_TO)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(MORPHING_REASONING))
        .build());
    agentConfigurationForUnitType.put(LARVA_TYPE, UnitWatcherType.builder()
        .agentType(LARVA)
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
