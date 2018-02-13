package aic.gas.sc.gg_bot.mas.model.metadata.containers;

import aic.gas.sc.gg_bot.mas.model.FeatureRawValueObtainingStrategy;
import aic.gas.sc.gg_bot.mas.model.metadata.FactConverterID;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;

/**
 * Container for fact type and raw value obtaining strategy
 */
@Getter
public class FactWithSetOfOptionalValues<V> extends FactConverterID<V> {

  private final FeatureRawValueObtainingStrategy<Stream<Optional<V>>> strategyToObtainValue;

  public FactWithSetOfOptionalValues(FactConverterID<V> factConverterID,
      FeatureRawValueObtainingStrategy<Stream<Optional<V>>> strategyToObtainValue) {
    super(factConverterID.getId(), factConverterID.getFactKey(), factConverterID.getName());
    this.strategyToObtainValue = strategyToObtainValue;
  }
}
