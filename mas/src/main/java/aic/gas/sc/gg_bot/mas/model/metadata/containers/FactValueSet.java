package aic.gas.sc.gg_bot.mas.model.metadata.containers;

import aic.gas.sc.gg_bot.mas.model.IFeatureRawValueObtainingStrategy;
import aic.gas.sc.gg_bot.mas.model.metadata.FactConverterID;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;

/**
 * Container for fact type and raw value obtaining strategy
 */
@Getter
public class FactValueSet<V> extends FactConverterID<V> {

  private final IFeatureRawValueObtainingStrategy<Optional<Stream<V>>> strategyToObtainValue;

  public FactValueSet(
      FactConverterID<V> factConverterID,
      IFeatureRawValueObtainingStrategy<Optional<Stream<V>>> strategyToObtainValue) {
    super(factConverterID.getId(), factConverterID.getFactKey(), factConverterID.getName());
    this.strategyToObtainValue = strategyToObtainValue;
  }
}
