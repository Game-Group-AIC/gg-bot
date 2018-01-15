package aic.gas.sc.gg_bot.abstract_bot.model;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitOfPlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import java.util.stream.Stream;
import lombok.Getter;

/**
 * Contains statistics for given AUnitTypeWrapper
 */
@Getter
public class UnitTypeStatus {

  private final long count;
  private final AUnitTypeWrapper unitTypeWrapper;

  public UnitTypeStatus(AUnitTypeWrapper unitTypeWrapper, Stream<? extends AUnit> unitSet) {
    this.unitTypeWrapper = unitTypeWrapper;
    this.count = unitSet.filter(unit -> unit.getType().equals(unitTypeWrapper))
        .count();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    UnitTypeStatus that = (UnitTypeStatus) o;

    return unitTypeWrapper.equals(that.unitTypeWrapper);
  }

  @Override
  public int hashCode() {
    return unitTypeWrapper.hashCode();
  }

  /**
   * Extension to track additional status - can be build?
   */
  @Getter
  public static class Own extends UnitTypeStatus {

    private final boolean areRequirementsMet;
    private final boolean isThereEnoughResources;
    private final boolean isThereMinerals;
    private final boolean isThereGas;
    private final boolean isThereCapacity;

    public Own(AUnitTypeWrapper unitTypeWrapper, Stream<AUnitOfPlayer> unitSet,
        Stream<AUnitTypeWrapper> lockedTypes, APlayer aPlayer) {
      super(unitTypeWrapper, unitSet);
      this.isThereMinerals = unitTypeWrapper.getMineralPrice() >= aPlayer.getMinerals();
      this.isThereGas = unitTypeWrapper.getGasPrice() >= aPlayer.getGas();
      this.isThereCapacity =
          unitTypeWrapper.supplyRequired() + aPlayer.getSupplyUsed() <= aPlayer.getSupplyTotal();
      this.isThereEnoughResources = isThereMinerals && isThereCapacity && isThereGas;
      this.areRequirementsMet = lockedTypes
          .noneMatch(typeWrapper -> typeWrapper.equals(unitTypeWrapper));
    }

  }

}
