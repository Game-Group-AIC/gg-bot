package aic.gas.sc.gg_bot.abstract_bot.model;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUpgradeTypeWrapper;
import java.util.Optional;
import lombok.Getter;

/**
 * Contains information about upgrade
 */
public class UpgradeTypeStatus {

  @Getter
  private final int currentLevel;

  private final AUpgradeTypeWrapper typeWrapper;

  @Getter
  private final boolean canBeUpgraded;

  public UpgradeTypeStatus(int currentLevel, AUpgradeTypeWrapper typeWrapper) {
    this.currentLevel = currentLevel;
    this.typeWrapper = typeWrapper;
    this.canBeUpgraded = currentLevel < typeWrapper.getMaxRepeats();
  }

  public Optional<Integer> mineralRequirements() {
    if (canBeUpgraded) {
      return Optional.of(typeWrapper.getMineralPriceList().get(currentLevel + 1));
    }
    return Optional.empty();
  }

  public Optional<Integer> gasRequirements() {
    if (canBeUpgraded) {
      return Optional.of(typeWrapper.getGasPriceList().get(currentLevel));
    }
    return Optional.empty();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    UpgradeTypeStatus that = (UpgradeTypeStatus) o;

    return typeWrapper.equals(that.typeWrapper);
  }

  @Override
  public int hashCode() {
    return typeWrapper.hashCode();
  }
}
