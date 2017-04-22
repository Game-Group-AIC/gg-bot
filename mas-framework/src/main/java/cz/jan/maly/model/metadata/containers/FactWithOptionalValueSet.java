package cz.jan.maly.model.metadata.containers;

import cz.jan.maly.model.FeatureRawValueObtainingStrategy;
import cz.jan.maly.model.metadata.FactConverterID;
import lombok.Getter;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Container for fact type and raw value obtaining strategy
 * Created by Jan on 14-Apr-17.
 */
@Getter
public class FactWithOptionalValueSet<V> extends FactConverterID<V> {
    private final FeatureRawValueObtainingStrategy<Optional<Stream<V>>> strategyToObtainValue;

    public FactWithOptionalValueSet(FactConverterID<V> factConverterID, FeatureRawValueObtainingStrategy<Optional<Stream<V>>> strategyToObtainValue) {
        super(factConverterID.getID(), factConverterID.getFactKey());
        this.strategyToObtainValue = strategyToObtainValue;
    }
}
