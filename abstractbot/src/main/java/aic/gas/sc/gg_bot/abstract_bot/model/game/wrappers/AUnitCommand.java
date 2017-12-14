package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import bwapi.Unit;
import bwapi.UnitCommand;
import bwapi.UnitCommandType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;

/**
 * Wrapper for BWMirror UnitCommand
 */
@Getter
public class AUnitCommand {

  //to keep track of types with target
  private static Set<UnitCommandType> typesWithTarget = new HashSet<>(
      Arrays.asList(UnitCommandType.Gather, UnitCommandType.Attack_Unit, UnitCommandType.Follow,
          UnitCommandType.Repair, UnitCommandType.Load, UnitCommandType.Unload));
  private final int slot;
  private final Optional<Unit> target;
  private final Optional<Unit> unit;
  private final UnitCommandType unitCommandType;
  private final Optional<APosition> targetPosition;
  private final Optional<ATilePosition> targetTilePosition;
  private final boolean isQueued;

  private AUnitCommand(UnitCommand command) {
    this.slot = command.getSlot();
    if (typesWithTarget.contains(command.getUnitCommandType())) {
      target = Optional.ofNullable(command.getTarget());
    } else {
      target = Optional.empty();
    }
    unit = Optional.ofNullable(command.getUnit());
    this.unitCommandType = command.getUnitCommandType();
    if (command.getTargetPosition() == null) {
      this.targetPosition = Optional.empty();
    } else {
      this.targetPosition = Optional.of(APosition.wrap(command.getTargetPosition()));
    }
    this.targetTilePosition = ATilePosition.creteOrEmpty(command.getTargetTilePosition());
    this.isQueued = command.isQueued();
  }

  public static Optional<AUnitCommand> creteOrEmpty(UnitCommand command) {
    if (command == null) {
      return Optional.empty();
    }
    return Optional.of(new AUnitCommand(command));
  }

}
