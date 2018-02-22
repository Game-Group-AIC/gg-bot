package aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.*;

import aic.gas.sc.gg_bot.abstract_bot.model.UnitTypeStatus;
import aic.gas.sc.gg_bot.abstract_bot.model.UpgradeTypeStatus;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.game.util.Utils;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.UnitWrapperFactory;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.WrapperTypeFactory;
import aic.gas.sc.gg_bot.replay_parser.model.IAgentMakingObservations;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.Beliefs;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_type_extension.WatcherPlayerType;
import bwapi.Game;
import bwapi.Player;
import bwapi.Race;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of watcher for player Created by Jan on 18-Apr-17.
 */
public class WatcherPlayer extends AgentWatcher<WatcherPlayerType> implements
    IAgentMakingObservations {

  private APlayer player;

  public WatcherPlayer(Player player, Game game) {
    super(WatcherPlayerType.builder()
        .factSetsKeys(Stream.of(AVAILABLE_MINERALS, ENEMY_RACE, AVAILABLE_GAS, POPULATION_LIMIT,
            POPULATION, IS_PLAYER, FREE_SUPPLY, FORCE_SUPPLY_RATIO, DIFFERENCE_IN_BASES,
            AVERAGE_COUNT_OF_WORKERS_PER_BASE, AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE,
            COUNT_OF_BASES_WITHOUT_EXTRACTORS,
            UPGRADE_STATUS, OUR_BASE, ENEMY_BASE,
            OWN_AIR_FORCE_STATUS, OWN_BUILDING_STATUS, OWN_GROUND_FORCE_STATUS,
            ENEMY_AIR_FORCE_STATUS, ENEMY_BUILDING_STATUS,
            ENEMY_GROUND_FORCE_STATUS, LOCKED_UNITS, LOCKED_BUILDINGS,
            ENEMY_STATIC_AIR_FORCE_STATUS, ENEMY_STATIC_GROUND_FORCE_STATUS,
            OWN_STATIC_AIR_FORCE_STATUS, OWN_STATIC_GROUND_FORCE_STATUS, ENEMY_FORCE_STATUS,
            OWN_FORCE_STATUS)
            .collect(Collectors.toSet()))
        .playerEnvironmentObservation((aPlayer, beliefs) -> {
          APlayer p = aPlayer.makeObservationOfEnvironment(game.getFrameCount());
          beliefs.updateFactSetByFacts(UPGRADE_STATUS, WrapperTypeFactory.upgrades().stream()
              .map(aUpgradeTypeWrapper -> new UpgradeTypeStatus(
                  p.getPlayer().getUpgradeLevel(aUpgradeTypeWrapper.getType()),
                  aUpgradeTypeWrapper))
              .collect(Collectors.toSet()));
          beliefs.updateFactSetByFacts(LOCKED_UNITS, WrapperTypeFactory.units().stream()
              .filter(aUnitTypeWrapper -> !p.getPlayer()
                  .hasUnitTypeRequirement(aUnitTypeWrapper.getType()))
              .collect(Collectors.toSet()));
          beliefs.updateFactSetByFacts(LOCKED_BUILDINGS, WrapperTypeFactory.buildings().stream()
              .filter(aUnitTypeWrapper -> !p.getPlayer()
                  .hasUnitTypeRequirement(aUnitTypeWrapper.getType()))
              .collect(Collectors.toSet()));
          return p;
        })
        .agentType(AgentTypes.PLAYER)
        .reasoning((bl, ms) -> {

          //read data from player
          APlayer aPlayer = bl.returnFactValueForGivenKey(IS_PLAYER).get();
          bl.updateFactSetByFact(AVAILABLE_MINERALS, (double) aPlayer.getMinerals());
          bl.updateFactSetByFact(AVAILABLE_GAS, (double) aPlayer.getGas());
          bl.updateFactSetByFact(POPULATION_LIMIT, (double) aPlayer.getSupplyTotal());
          bl.updateFactSetByFact(POPULATION, (double) aPlayer.getSupplyUsed());
          bl.updateFactSetByFact(FREE_SUPPLY,
              (double) (aPlayer.getSupplyTotal() - aPlayer.getSupplyUsed()));

          //estimate enemy force
          Set<UnitTypeStatus> enemyBuildingsTypes = UnitWrapperFactory
              .getStreamOfAllAliveEnemyUnits()
              .filter(enemy -> enemy.getType().isBuilding())
              .collect(Collectors.groupingBy(AUnit::getType)).entrySet().stream()
              .map(entry -> new UnitTypeStatus(entry.getKey(), entry.getValue().stream()))
              .collect(Collectors.toSet());
          bl.updateFactSetByFacts(ENEMY_BUILDING_STATUS, enemyBuildingsTypes);
          bl.updateFactSetByFacts(ENEMY_STATIC_AIR_FORCE_STATUS, enemyBuildingsTypes.stream()
              .filter(
                  unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().isMilitaryBuildingAntiAir())
              .collect(Collectors.toSet()));
          bl.updateFactSetByFacts(ENEMY_STATIC_GROUND_FORCE_STATUS, enemyBuildingsTypes.stream()
              .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper()
                  .isMilitaryBuildingAntiGround())
              .collect(Collectors.toSet()));
          Set<UnitTypeStatus> enemyUnitsTypes = UnitWrapperFactory.getStreamOfAllAliveEnemyUnits()
              .filter(
                  enemy -> !enemy.getType().isNotActuallyUnit() && !enemy.getType().isBuilding())
              .collect(Collectors.groupingBy(AUnit::getType)).entrySet().stream()
              .map(entry -> new UnitTypeStatus(entry.getKey(), entry.getValue().stream()))
              .collect(Collectors.toSet());
          bl.updateFactSetByFacts(ENEMY_AIR_FORCE_STATUS, enemyUnitsTypes.stream()
              .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().canAttackAirUnits())
              .collect(Collectors.toSet()));
          bl.updateFactSetByFacts(ENEMY_GROUND_FORCE_STATUS, enemyUnitsTypes.stream()
              .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().canAttackGroundUnits())
              .collect(Collectors.toSet()));

          //estimate our force
          Set<UnitTypeStatus> ownBuildingsTypes = UnitWrapperFactory
              .getStreamOfAllAlivePlayersUnits()
              .filter(enemy -> enemy.getType().isBuilding())
              .collect(Collectors.groupingBy(AUnit::getType)).entrySet().stream()
              .map(entry -> new UnitTypeStatus(entry.getKey(), entry.getValue().stream()))
              .collect(Collectors.toSet());
          bl.updateFactSetByFacts(OWN_BUILDING_STATUS, ownBuildingsTypes);
          bl.updateFactSetByFacts(OWN_STATIC_AIR_FORCE_STATUS, ownBuildingsTypes.stream()
              .filter(
                  unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().isMilitaryBuildingAntiAir())
              .collect(Collectors.toSet()));
          bl.updateFactSetByFacts(OWN_STATIC_GROUND_FORCE_STATUS, ownBuildingsTypes.stream()
              .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper()
                  .isMilitaryBuildingAntiGround())
              .collect(Collectors.toSet()));
          Set<UnitTypeStatus> ownUnitsTypes = UnitWrapperFactory.getStreamOfAllAlivePlayersUnits()
              .filter(
                  enemy -> !enemy.getType().isNotActuallyUnit() && !enemy.getType().isBuilding())
              .collect(Collectors.groupingBy(AUnit::getType)).entrySet().stream()
              .map(entry -> new UnitTypeStatus(entry.getKey(), entry.getValue().stream()))
              .collect(Collectors.toSet());
          bl.updateFactSetByFacts(OWN_AIR_FORCE_STATUS, ownUnitsTypes.stream()
              .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().canAttackAirUnits())
              .collect(Collectors.toSet()));
          bl.updateFactSetByFacts(OWN_GROUND_FORCE_STATUS, ownUnitsTypes.stream()
              .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().canAttackGroundUnits())
              .collect(Collectors.toSet()));

          //enemy + our force
          bl.updateFactSetByFacts(OWN_FORCE_STATUS, ownUnitsTypes);
          bl.updateFactSetByFacts(ENEMY_FORCE_STATUS, enemyUnitsTypes);
          bl.updateFactSetByFact(FORCE_SUPPLY_RATIO, Utils
              .computeOurVsEnemyForceRatio(bl.returnFactSetValueForGivenKey(OWN_FORCE_STATUS),
                  bl.returnFactSetValueForGivenKey(ENEMY_FORCE_STATUS)));

          //enemy race
          Optional<Race> enemyRace = UnitWrapperFactory.getStreamOfAllAliveEnemyUnits()
              .map(enemy -> enemy.getType().getRace()).findAny();
          enemyRace.ifPresent(race -> bl.updateFactSetByFact(ENEMY_RACE, ARace.getRace(race)));

          //bases
          bl.updateFactSetByFacts(OUR_BASE, ms.getStreamOfWatchers()
              .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                  .equals(BASE_LOCATION.name()))
              .filter(agentWatcher -> agentWatcher.getBeliefs().returnFactValueForGivenKey(
                  IS_OUR_BASE)
                  .orElse(false))
              .map(agentWatcher -> agentWatcher.getBeliefs()
                  .returnFactValueForGivenKey(IS_BASE_LOCATION).get())
              .collect(Collectors.toSet()));
          bl.updateFactSetByFacts(ENEMY_BASE, ms.getStreamOfWatchers()
              .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                  .equals(BASE_LOCATION.name()))
              .filter(agentWatcher -> agentWatcher.getBeliefs()
                  .returnFactValueForGivenKey(IS_ENEMY_BASE).orElse(false))
              .map(agentWatcher -> agentWatcher.getBeliefs()
                  .returnFactValueForGivenKey(IS_BASE_LOCATION).get())
              .collect(Collectors.toSet()));
          bl.updateFactSetByFact(DIFFERENCE_IN_BASES, Utils
              .computeDifferenceInBases(bl.returnFactSetValueForGivenKey(OUR_BASE),
                  bl.returnFactSetValueForGivenKey(ENEMY_BASE)));

          //our bases aggregated info
          Set<Beliefs> ourBases = ms.getStreamOfWatchers()
              .filter(agentWatcher -> agentWatcher.getAgentWatcherType().getName()
                  .equals(BASE_LOCATION.name()))
              .map(AgentWatcher::getBeliefs)
              .filter(blf -> blf.returnFactValueForGivenKey(IS_OUR_BASE).orElse(false))
              .collect(Collectors.toSet());
          bl.updateFactSetByFact(AVERAGE_COUNT_OF_WORKERS_PER_BASE, ourBases.stream()
              .map(readOnlyMemory -> readOnlyMemory
                  .returnFactSetValueForGivenKey(WORKER_ON_BASE))
              .map(str -> str.orElse(Stream.empty()))
              .mapToDouble(Stream::count)
              .average().orElse(0.0));
          bl.updateFactSetByFact(AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE, ourBases.stream()
              .map(readOnlyMemory -> readOnlyMemory
                  .returnFactSetValueForGivenKey(WORKER_MINING_GAS))
              .map(str -> str.orElse(Stream.empty()))
              .mapToDouble(Stream::count)
              .average().orElse(0.0));
          bl.updateFactSetByFact(COUNT_OF_BASES_WITHOUT_EXTRACTORS, (int) ourBases.stream()
              .filter(readOnlyMemory -> readOnlyMemory
                  .returnFactSetValueForGivenKey(HAS_EXTRACTOR)
                  .orElse(Stream.empty()).count() == 0)
              .map(readOnlyMemory -> readOnlyMemory
                  .returnFactValueForGivenKey(IS_BASE_LOCATION).get())
              .filter(locationWrapper -> !locationWrapper.isMineralOnly())
              .count());
        })
        .planWatchers(new ArrayList<>())
        .build()
    );
    this.player = APlayer.wrapPlayer(player, game.getFrameCount()).get();
    beliefs.updateFactSetByFact(IS_PLAYER, this.player);
  }

  public void makeObservation() {
    beliefs.updateFactSetByFact(IS_PLAYER,
        agentWatcherType.getPlayerEnvironmentObservation().updateBeliefs(player, beliefs));
  }

}
