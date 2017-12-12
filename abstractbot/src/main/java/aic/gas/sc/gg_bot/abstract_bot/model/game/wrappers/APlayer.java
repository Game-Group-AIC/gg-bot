package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import bwapi.Player;
import bwapi.PlayerType;
import bwapi.Race;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

/**
 * Wrapper for Player in GAME
 */
public class APlayer {

  private static final Map<Integer, APlayer> instances = new ConcurrentHashMap<>();

  @Getter
  private final Player player;

  @Getter
  private final String name;

  @Getter
  private final PlayerType type;

  @Getter
  private final int gas;

  @Getter
  private final int spentGas;

  @Getter
  private final int minerals;

  @Getter
  private final boolean leftGame;

  @Getter
  private final Race race;

  @Getter
  private final boolean isNeutral;

  @Getter
  private final Optional<ATilePosition> startLocation;

  @Getter
  private final boolean isVictorious;

  @Getter
  private final boolean isDefeated;

  @Getter
  private final int gatheredMinerals;

  @Getter
  private final int gatheredGas;

  @Getter
  private final int repairedMinerals;

  @Getter
  private final int repairedGas;

  @Getter
  private final int refundedMinerals;

  @Getter
  private final int refundedGas;

  @Getter
  private final int spentMinerals;

  @Getter
  private final int supplyTotal;

  @Getter
  private final int supplyUsed;

  @Getter
  private final int deadUnitCount;

  @Getter
  private final int killedUnitCount;

  @Getter
  private final int unitScore;

  @Getter
  private final int killScore;

  @Getter
  private final int buildingScore;

  @Getter
  private final int razingScore;

  @Getter
  private final int customScore;

  @Getter
  private final boolean isObserver;

  @Getter
  private final int playerId;

  private APlayer(Player player) {
    this.player = player;
    this.playerId = player.getID();
    this.name = player.getName();
    this.type = player.getType();
    this.gas = player.gas();
    this.spentGas = player.spentGas();
    this.minerals = player.minerals();
    this.leftGame = player.leftGame();
    this.race = player.getRace();
    this.isNeutral = player.isNeutral();
    this.startLocation = ATilePosition.creteOrEmpty(player.getStartLocation());
    this.isVictorious = player.isVictorious();
    this.isDefeated = player.isDefeated();
    this.gatheredMinerals = player.gatheredMinerals();
    this.gatheredGas = player.gatheredGas();
    this.repairedMinerals = player.repairedMinerals();
    this.repairedGas = player.repairedGas();
    this.refundedMinerals = player.refundedMinerals();
    this.refundedGas = player.refundedGas();
    this.spentMinerals = player.spentMinerals();
    this.supplyTotal = player.supplyTotal();
    this.supplyUsed = player.supplyUsed();
    this.deadUnitCount = player.deadUnitCount();
    this.killedUnitCount = player.killedUnitCount();
    this.unitScore = player.getUnitScore();
    this.killScore = player.getKillScore();
    this.buildingScore = player.getBuildingScore();
    this.razingScore = player.getRazingScore();
    this.customScore = player.getCustomScore();
    this.isObserver = player.isObserver();
  }

  /**
   * Wrapped player is returned
   */
  public static Optional<APlayer> wrapPlayer(Player player) {
    if (player == null) {
      return Optional.empty();
    }
    if (instances.containsKey(player.getID())) {
      return Optional.of(instances.get(player.getID()));
    } else {
      APlayer aPlayer = new APlayer(player);
      instances.put(player.getID(), aPlayer);
      return Optional.of(aPlayer);
    }
  }

  /**
   * Method to refresh fields in wrapper for unit
   */
  public APlayer makeObservationOfEnvironment() {
    APlayer aPlayer = new APlayer(player);
    instances.put(player.getID(), aPlayer);
    return aPlayer;
  }

}
