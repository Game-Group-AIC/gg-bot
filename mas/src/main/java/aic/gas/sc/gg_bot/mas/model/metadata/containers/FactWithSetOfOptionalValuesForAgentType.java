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
public class FactWithSetOfOptionalValuesForAgentType<V> extends FactWithSetOfOptionalValues<V> {

  private final AgentTypeID agentTypeID;

  public FactWithSetOfOptionalValuesForAgentType(FactConverterID<V> factConverterID,
      FeatureRawValueObtainingStrategy<Stream<Optional<V>>> strategyToObtainValue,
      AgentTypeID agentTypeID) {
    super(factConverterID, strategyToObtainValue);
    this.agentTypeID = agentTypeID;
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

    FactWithSetOfOptionalValuesForAgentType<?> that = (FactWithSetOfOptionalValuesForAgentType<?>) o;

    return agentTypeID.equals(that.agentTypeID);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + agentTypeID.hashCode();
    return result;
  }
}
