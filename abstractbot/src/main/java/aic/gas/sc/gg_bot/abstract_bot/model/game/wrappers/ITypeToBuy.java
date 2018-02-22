package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import java.util.Optional;
import java.util.stream.Stream;

public interface ITypeToBuy {

  int mineralCost();

  int gasCost();

  default int supplyRequired() {
    return 0;
  }

  Stream<AUnitTypeWrapper> unitTypeDependencies();

  Optional<ATechTypeWrapper> techTypeDependency();

}
