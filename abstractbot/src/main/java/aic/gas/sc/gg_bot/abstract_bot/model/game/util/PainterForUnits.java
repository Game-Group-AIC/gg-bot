package aic.gas.sc.gg_bot.abstract_bot.model.game.util;

import static aic.gas.sc.gg_bot.abstract_bot.model.game.util.Annotator.paintCircle;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.util.Annotator.paintCircleFilled;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.util.Annotator.paintRectangleFilled;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.util.Annotator.paintText;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.util.Annotator.paintTextCentered;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitCommand;
import bwapi.Color;
import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwapi.Unit;
import bwapi.WeaponType;
import java.util.List;
import java.util.Optional;

/**
 * Class to annotate game map with unit information
 */
class PainterForUnits {

  private static final Tooltip.UnitTooltip unitTooltip = new Tooltip.UnitTooltip();
  private static final Tooltip.BuildingTooltip buildingTooltip = new Tooltip.BuildingTooltip();

  static void annotateUnitsForPlayers(List<Player> playersToPrint, Player me, Game bwapi) {
    playersToPrint.forEach(player -> player.getUnits().stream()
        .filter(Unit::isCompleted)
        .forEach(unit -> annotateUnit(unit, bwapi, me.getID() != unit.getPlayer().getID())));
  }

  private static void annotateUnit(Unit unit, Game bwapi, boolean isEnemy) {
    if (unit.getType().isBuilding()) {
      buildingTooltip.annotateUnit(unit, bwapi, isEnemy);
    } else {
      if (unit.getType().canMove()) {
        unitTooltip.annotateUnit(unit, bwapi, isEnemy);
      }
    }
  }

  /**
   * Class to make annotation for unit
   */
  private static abstract class Tooltip {

    void annotateUnit(Unit unit, Game bwapi, boolean isEnemy) {

      //print color if unit is enemy
      Color color, colorForCommand;
      if (isEnemy) {
        color = Color.Red;
        colorForCommand = Color.Brown;
      } else {
        color = Color.Green;
        colorForCommand = Color.Orange;
      }
      paintCircleFilled(unit.getPosition(), 1, color, bwapi);

      //paint sight circle
      paintCircle(unit.getPosition(), unit.getType().sightRange(), Color.Brown, bwapi);

      printAttackRanges(unit, bwapi);
      makeAdditionalAnnotation(unit, bwapi, colorForCommand);

      String annotation =
          unit.getType().toString() + ", hitPoints: " + unit.getHitPoints() + "/" + unit.getType()
              .maxHitPoints() + ", energyPoints: " + unit.getEnergy() + "/" + unit.getType()
              .maxEnergy() + ", " + makeTextAnnotation(unit) + ", idle: " + unit.isIdle();
      paintTextCentered(unit.getPosition(), annotation, bwapi);
    }

    private void printAttackRanges(Unit unit, Game bwapi) {
      WeaponType weaponType = unit.getType().airWeapon();
      if (!weaponType.equals(WeaponType.None)) {
        paintCircle(unit.getPosition(), weaponType.maxRange(), Color.Blue, bwapi);
      }
      weaponType = unit.getType().groundWeapon();
      if (!weaponType.equals(WeaponType.None)) {
        paintCircle(unit.getPosition(), weaponType.maxRange(), Color.Red, bwapi);
      }
    }

    abstract void makeAdditionalAnnotation(Unit unit, Game bwapi, Color color);

    abstract String makeTextAnnotation(Unit unit);

    static class UnitTooltip extends Tooltip {

      @Override
      void makeAdditionalAnnotation(Unit unit, Game bwapi, Color color) {
        Optional<AUnitCommand> command = AUnitCommand.creteOrEmpty(unit.getLastCommand());
        if (command.isPresent()) {
          Optional<Position> aPosition = Optional.empty();
          if (command.get().getTarget().isPresent()) {
            aPosition = Optional.ofNullable(command.get().getTarget().get().getPosition());
          } else {
            if (command.get().getTargetPosition().isPresent()) {
              aPosition = Optional
                  .ofNullable(command.get().getTargetPosition().get().getWrappedPosition());
            }
            if (!aPosition.isPresent()) {
              if (command.get().getTargetTilePosition().isPresent()) {
                aPosition = Optional.ofNullable(
                    command.get().getTargetTilePosition().get().getWrappedPosition().toPosition());
              }
            }
          }
          if (aPosition.isPresent()) {
            paintRectangleFilled(aPosition.get(), 10, 10, color, bwapi);
            paintText(aPosition.get(), command.get().getUnitCommandType().toString(), bwapi);
          }
        }
      }

      @Override
      String makeTextAnnotation(Unit unit) {
        Optional<AUnitCommand> command = AUnitCommand.creteOrEmpty(unit.getLastCommand());
        return command.map(aUnitCommand -> aUnitCommand.getUnitCommandType().toString()).orElse("");
      }
    }

    static class BuildingTooltip extends Tooltip {

      @Override
      void makeAdditionalAnnotation(Unit unit, Game bwapi, Color color) {

      }

      @Override
      String makeTextAnnotation(Unit unit) {
        Optional<AUnitCommand> command = AUnitCommand.creteOrEmpty(unit.getLastCommand());
        return command.map(aUnitCommand -> aUnitCommand.getUnitCommandType().toString()).orElse("");
      }
    }

  }

}
