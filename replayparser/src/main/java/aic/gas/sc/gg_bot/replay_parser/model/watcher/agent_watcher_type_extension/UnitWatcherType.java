package aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_type_extension;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.*;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitOfPlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitWithCommands;
import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcherType;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.Beliefs;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.updating_strategies.AgentEnvironmentObservation;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.updating_strategies.Reasoning;
import aic.gas.sc.gg_bot.replay_parser.service.IWatcherMediatorService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

/**
 * Extension of AgentWatcherType to UnitWatcherType
 */
public class UnitWatcherType extends AgentWatcherType {

  @Getter
  private static final AgentEnvironmentObservation agentEnvironmentObservation = (aUnit, beliefs, frame) -> {
    AUnitWithCommands unitWithCommands = aUnit.makeObservationOfEnvironment(frame);
    beliefs.updateFactSetByFact(REPRESENTS_UNIT, unitWithCommands);
    return unitWithCommands;
  };

  @Builder
  private UnitWatcherType(AgentTypes agentType, Set<FactKey<?>> factKeys,
      Set<FactKey<?>> factSetsKeys,
      List<PlanWatcherInitializationStrategy> planWatchers,
      ReasoningForAgentWithUnitRepresentation reasoning) {
    super(agentType, factKeys, factSetsKeys, planWatchers, reasoning);
    this.getFactSetsKeys().addAll(Arrays.asList(ENEMY_BUILDING, ENEMY_AIR,
        ENEMY_GROUND, OWN_BUILDING, OWN_AIR, OWN_GROUND));
    this.getFactKeys().addAll(Arrays.asList(REPRESENTS_UNIT, LOCATION));
  }

  /**
   * Builder with default values
   */
  public static class UnitWatcherTypeBuilder extends AgentWatcherTypeBuilder {

    private Set<FactKey<?>> factKeys = new HashSet<>();
    private Set<FactKey<?>> factSetsKeys = new HashSet<>();
    private List<PlanWatcherInitializationStrategy> planWatchers = new ArrayList<>();
  }

  /**
   * Extension of reasoning to provide common beliefs updates
   */
  public static class ReasoningForAgentWithUnitRepresentation implements Reasoning {

    private final Reasoning reasoning;

    public ReasoningForAgentWithUnitRepresentation(Reasoning reasoning) {
      this.reasoning = reasoning;
    }

    /**
     * Updates beliefs
     */
    void updateBeliefsAboutUnitsInSurroundingArea(Beliefs beliefs) {
      AUnitOfPlayer unit = beliefs.returnFactValueForGivenKey(REPRESENTS_UNIT).get();

      //enemies
      beliefs.updateFactSetByFacts(ENEMY_BUILDING,
          unit.getEnemyUnitsInRadiusOfSight().stream().filter(enemy -> enemy.getType().isBuilding())
              .collect(Collectors.toSet()));
      beliefs.updateFactSetByFacts(ENEMY_GROUND, unit.getEnemyUnitsInRadiusOfSight().stream()
          .filter(enemy -> !enemy.getType().isBuilding() && !enemy.getType().isFlyer())
          .collect(Collectors.toSet()));
      beliefs.updateFactSetByFacts(ENEMY_AIR, unit.getEnemyUnitsInRadiusOfSight().stream()
          .filter(enemy -> !enemy.getType().isBuilding() && enemy.getType().isFlyer())
          .collect(Collectors.toSet()));

      //friendlies
      beliefs.updateFactSetByFacts(OWN_BUILDING,
          unit.getFriendlyUnitsInRadiusOfSight().stream().filter(own -> own.getType().isBuilding())
              .collect(Collectors.toSet()));
      beliefs.updateFactSetByFacts(OWN_GROUND, unit.getFriendlyUnitsInRadiusOfSight().stream()
          .filter(own -> !own.getType().isBuilding() && !own.getType().isFlyer())
          .collect(Collectors.toSet()));
      beliefs.updateFactSetByFacts(OWN_AIR, unit.getFriendlyUnitsInRadiusOfSight().stream()
          .filter(own -> !own.getType().isBuilding() && own.getType().isFlyer())
          .collect(Collectors.toSet()));

      beliefs.updateFactSetByFact(LOCATION,
          unit.getNearestBaseLocation().orElse(LOCATION.getInitValue()));
    }

    @Override
    public void updateBeliefs(Beliefs beliefs, IWatcherMediatorService mediatorService) {
      updateBeliefsAboutUnitsInSurroundingArea(beliefs);
      reasoning.updateBeliefs(beliefs, mediatorService);
    }
  }
}
