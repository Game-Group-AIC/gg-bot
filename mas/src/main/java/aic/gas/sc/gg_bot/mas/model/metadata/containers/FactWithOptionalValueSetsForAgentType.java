package aic.gas.sc.gg_bot.mas.model.metadata.containers;

import aic.gas.sc.gg_bot.mas.model.FeatureRawValueObtainingStrategy;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.mas.model.metadata.FactConverterID;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;

/**
 * Container for fact type and raw value obtaining strategy
 */
@Getter
public class FactWithOptionalValueSetsForAgentType<V> extends FactWithOptionalValueSets<V> {

  private final AgentTypeID agentType;

  public FactWithOptionalValueSetsForAgentType(FactConverterID<V> factConverterID,
      AgentTypeID agentType,
      FeatureRawValueObtainingStrategy<Stream<Optional<Stream<V>>>> strategyToObtainValue) {
    super(factConverterID, strategyToObtainValue);
    this.agentType = agentType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    FactWithOptionalValueSetsForAgentType<?> that = (FactWithOptionalValueSetsForAgentType<?>) o;

    return agentType.equals(that.agentType);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + agentType.hashCode();
    return result;
  }
}
