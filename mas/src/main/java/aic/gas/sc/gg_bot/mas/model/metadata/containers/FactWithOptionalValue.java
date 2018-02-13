package aic.gas.sc.gg_bot.mas.model.metadata.containers;

import aic.gas.sc.gg_bot.mas.model.FeatureRawValueObtainingStrategy;
import aic.gas.sc.gg_bot.mas.model.metadata.FactConverterID;
import java.util.Optional;
import lombok.Getter;

/**
 * Container for fact type and raw value obtaining strategy
 */
@Getter
public class FactWithOptionalValue<V> extends FactConverterID<V> {

  private final FeatureRawValueObtainingStrategy<Optional<V>> strategyToObtainValue;

  public FactWithOptionalValue(FactConverterID<V> factConverterID,
      FeatureRawValueObtainingStrategy<Optional<V>> strategyToObtainValue) {
    super(factConverterID.getId(), factConverterID.getFactKey(), factConverterID.getName());
    this.strategyToObtainValue = strategyToObtainValue;
  }
}
