package aic.gas.sc.gg_bot.abstract_bot.model.bot;

import aic.gas.mas.model.metadata.AgentTypeID;
import aic.gas.mas.model.metadata.DesireKeyID;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import bwapi.Player;
import bwapi.Unit;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Metadata - combinations of agent and its learnt decisions
 */
public class DecisionConfiguration {

  public static final Map<AgentTypeID, Set<DesireKeyID>> decisionsToLoad = ImmutableMap.of(
      AgentTypes.BASE_LOCATION, ImmutableSet
          .of(DesireKeys.BUILD_CREEP_COLONY, DesireKeys.BUILD_SPORE_COLONY,
              DesireKeys.BUILD_SUNKEN_COLONY, DesireKeys.HOLD_AIR, DesireKeys.HOLD_GROUND),
      AgentTypes.BUILDING_ORDER_MANAGER, ImmutableSet
          .of(DesireKeys.ENABLE_AIR, DesireKeys.ENABLE_GROUND_MELEE,
              DesireKeys.ENABLE_GROUND_RANGED, DesireKeys.ENABLE_STATIC_ANTI_AIR,
              DesireKeys.UPGRADE_TO_LAIR),
      AgentTypes.ECO_MANAGER, ImmutableSet
          .of(DesireKeys.BUILD_EXTRACTOR, DesireKeys.BUILD_WORKER, DesireKeys.EXPAND,
              DesireKeys.INCREASE_CAPACITY),
      AgentTypes.UNIT_ORDER_MANAGER, ImmutableSet
          .of(DesireKeys.BOOST_AIR, DesireKeys.BOOST_GROUND_MELEE, DesireKeys.BOOST_GROUND_RANGED)
  );

  @Getter
  public static ARace race = ARace.getRandomRace();

  @Getter
  public static boolean raceWasDetermined = false;

  public static void setupEnemyRace(Player self, Unit unit) {
    if (!raceWasDetermined && self.getID() != unit.getPlayer().getID() && unit.getPlayer()
        .isEnemy(self)) {
      ARace gameRace = ARace.getRace(unit.getPlayer().getRace());
      if (!gameRace.equals(ARace.UNKNOWN)) {
        race = gameRace;
        raceWasDetermined = true;
      } else {
        if (!(gameRace = ARace.getRace(unit.getType().getRace())).equals(ARace.UNKNOWN)) {
          race = gameRace;
          raceWasDetermined = true;
        }
      }
    }
  }

  public static void setupRace(Player self, List<Player> players) {
    Optional<ARace> gameRace = players.stream()
        .filter(player -> player.isEnemy(self))
        .map(player -> ARace.getRace(player.getRace()))
        .filter(r -> !r.equals(ARace.UNKNOWN))
        .findAny();
    gameRace.ifPresent(r -> {
      race = r;
      raceWasDetermined = true;
    });
  }

  @Getter
  @Setter
  public static MapSizeEnums mapSize = MapSizeEnums.MAP_FOR_3_AND_MORE;

}
