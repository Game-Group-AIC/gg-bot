package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.*;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.*;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper.*;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.*;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension.UnitWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_type_extension.UnitWatcherType;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.updating_strategies.Reasoning;
import aic.gas.sc.gg_bot.replay_parser.service.AgentUnitHandler;
import bwapi.Game;
import bwapi.Order;
import bwapi.Unit;
import java.util.*;
import lombok.extern.slf4j.Slf4j;

/**
 * Concrete implementation of AgentUnitHandler
 */
@Slf4j
public class AgentUnitFactory implements AgentUnitHandler {

  private static final Reasoning BUILDING_REASONING = (beliefs, mediatorService) -> {
    AUnitOfPlayer me = beliefs.returnFactValueForGivenKey(REPRESENTS_UNIT).get();
    beliefs.updateFact(IS_BEING_CONSTRUCTED, me.isBeingConstructed());
  };
  private static final List<Order> ordersCheckForAttack = Arrays
      .asList(Order.AttackMove, Order.AttackTile,
          Order.AttackUnit, Order.HarassMove);

  private static final Reasoning UNIT_REASONING = (beliefs, mediatorService) -> {
    AUnitOfPlayer me = beliefs.returnFactValueForGivenKey(REPRESENTS_UNIT).get();
    Optional<Order> order = me.getOrder();
    if (order.isPresent() && ordersCheckForAttack.contains(order.get())) {
      APosition targetPosition = me.getOrderTargetPosition()
          .orElse(me.getTargetPosition().orElse(me.getPosition()));
      Optional<ABaseLocationWrapper> holdInBaseLocation = mediatorService.getStreamOfWatchers()
          .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
              .equals(BASE_LOCATION.getName()))
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
              Optional<Order> order = me.getOrder();
              if (order.isPresent() && (order.get().equals(Order.PlaceBuilding) || order.get()
                  .equals(Order.PlaceMine))) {
                List<AUnitTypeWrapper> morphing = me.getTrainingQueue();
                if (morphing.isEmpty()) {
                  beliefs.updateFact(IS_MORPHING_TO, IS_MORPHING_TO.getInitValue());
                } else {
                  beliefs.updateFact(IS_MORPHING_TO, morphing.get(0));
                }
              }
            }))
        .factKeys(
            new HashSet<>(Arrays.asList(IS_GATHERING_MINERALS, IS_GATHERING_GAS, IS_MORPHING_TO)))
        .build());

    //buildings
    agentConfigurationForUnitType.put(HATCHERY_TYPE, UnitWatcherType.builder()
        .agentTypeID(HATCHERY)
        .factKeys(new HashSet<>(Collections.singletonList(IS_BEING_CONSTRUCTED)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(BUILDING_REASONING))
        .build());
    agentConfigurationForUnitType.put(SPAWNING_POOL_TYPE, UnitWatcherType.builder()
        .agentTypeID(SPAWNING_POOL)
        .factKeys(new HashSet<>(Collections.singletonList(IS_BEING_CONSTRUCTED)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(BUILDING_REASONING))
        .build());
    agentConfigurationForUnitType.put(EXTRACTOR_TYPE, UnitWatcherType.builder()
        .agentTypeID(EXTRACTOR)
        .factKeys(new HashSet<>(Collections.singletonList(IS_BEING_CONSTRUCTED)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(BUILDING_REASONING))
        .build());
    agentConfigurationForUnitType.put(LAIR_TYPE, UnitWatcherType.builder()
        .agentTypeID(LAIR)
        .factKeys(new HashSet<>(Collections.singletonList(IS_BEING_CONSTRUCTED)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(BUILDING_REASONING))
        .build());
    agentConfigurationForUnitType.put(SPIRE_TYPE, UnitWatcherType.builder()
        .agentTypeID(SPIRE)
        .factKeys(new HashSet<>(Collections.singletonList(IS_BEING_CONSTRUCTED)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(BUILDING_REASONING))
        .build());
    agentConfigurationForUnitType.put(EVOLUTION_CHAMBER_TYPE, UnitWatcherType.builder()
        .agentTypeID(EVOLUTION_CHAMBER)
        .factKeys(new HashSet<>(Collections.singletonList(IS_BEING_CONSTRUCTED)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(BUILDING_REASONING))
        .build());
    agentConfigurationForUnitType.put(HYDRALISK_DEN_TYPE, UnitWatcherType.builder()
        .agentTypeID(HYDRALISK_DEN)
        .factKeys(new HashSet<>(Collections.singletonList(IS_BEING_CONSTRUCTED)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(BUILDING_REASONING))
        .build());
    //static defense
    agentConfigurationForUnitType.put(SUNKEN_COLONY_TYPE, UnitWatcherType.builder()
        .agentTypeID(SUNKEN_COLONY)
        .factKeys(new HashSet<>(Collections.singletonList(IS_BEING_CONSTRUCTED)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(BUILDING_REASONING))
        .build());
    agentConfigurationForUnitType.put(CREEP_COLONY_TYPE, UnitWatcherType.builder()
        .agentTypeID(CREEP_COLONY)
        .factKeys(new HashSet<>(Collections.singletonList(IS_BEING_CONSTRUCTED)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(BUILDING_REASONING))
        .build());
    agentConfigurationForUnitType.put(SPORE_COLONY_TYPE, UnitWatcherType.builder()
        .agentTypeID(SPORE_COLONY)
        .factKeys(new HashSet<>(Collections.singletonList(IS_BEING_CONSTRUCTED)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(BUILDING_REASONING))
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
        .factKeys(new HashSet<>(Collections.singletonList(HOLD_LOCATION)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(UNIT_REASONING))
        .build());
    agentConfigurationForUnitType.put(MUTALISK_TYPE, UnitWatcherType.builder()
        .agentTypeID(MUTALISK)
        .factKeys(new HashSet<>(Collections.singletonList(HOLD_LOCATION)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(UNIT_REASONING))
        .build());
    agentConfigurationForUnitType.put(HYDRALISK_TYPE, UnitWatcherType.builder()
        .agentTypeID(HYDRALISK)
        .factKeys(new HashSet<>(Collections.singletonList(HOLD_LOCATION)))
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(UNIT_REASONING))
        .build());

    AgentTypeID dummy = new AgentTypeID("DUMMY", 10000);
    AUnitTypeWrapper.OTHER_UNIT_TYPES.forEach(
        typeWrapper -> agentConfigurationForUnitType.put(typeWrapper, UnitWatcherType.builder()
            .agentTypeID(dummy)
            .factKeys(new HashSet<>(Collections.singletonList(HOLD_LOCATION)))
            .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(UNIT_REASONING))
            .build()));

    //"barracks"
    agentConfigurationForUnitType.put(EGG_TYPE, UnitWatcherType.builder()
        .agentTypeID(EGG)
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(
            (beliefs, mediatorService) -> {
              List<AUnitTypeWrapper> morphing = beliefs.returnFactValueForGivenKey(REPRESENTS_UNIT)
                  .get().getTrainingQueue();
              if (morphing.isEmpty()) {
                beliefs.updateFact(IS_MORPHING_TO, IS_MORPHING_TO.getInitValue());
              } else {
                beliefs.updateFact(IS_MORPHING_TO, morphing.get(0));
              }
            }))
        .factKeys(new HashSet<>(Collections.singletonList(IS_MORPHING_TO)))
        .build());
    agentConfigurationForUnitType.put(LARVA_TYPE, UnitWatcherType.builder()
        .agentTypeID(LARVA)
        .reasoning(new UnitWatcherType.ReasoningForAgentWithUnitRepresentation(
            (beliefs, mediatorService) -> {
              Optional<Order> order = beliefs.returnFactValueForGivenKey(REPRESENTS_UNIT).get()
                  .getOrder();
              if (order.isPresent() && !order.get().equals(Order.Larva)) {
                List<AUnitTypeWrapper> morphing = beliefs
                    .returnFactValueForGivenKey(REPRESENTS_UNIT).get().getTrainingQueue();
                if (morphing.isEmpty()) {
                  beliefs.updateFact(IS_MORPHING_TO, IS_MORPHING_TO.getInitValue());
                } else {
                  beliefs.updateFact(IS_MORPHING_TO, morphing.get(0));
                }
              }
            }))
        .factKeys(new HashSet<>(Collections.singletonList(IS_MORPHING_TO)))
        .build());
  }

  @Override
  public Optional<UnitWatcher> createAgentForUnit(Unit unit, Game game) {
    if(agentConfigurationForUnitType.size() == 0) {
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
