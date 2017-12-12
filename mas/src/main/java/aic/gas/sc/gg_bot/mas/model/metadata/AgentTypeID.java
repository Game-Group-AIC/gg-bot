package aic.gas.sc.gg_bot.mas.model.metadata;

import java.io.Serializable;
import lombok.Getter;

/**
 * AgentType identification
 */
public class AgentTypeID extends Key implements Serializable {

  @Getter
  private final int ID;

  public AgentTypeID(String name, int id) {
    super(name, AgentTypeID.class);
    ID = id;
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

    AgentTypeID that = (AgentTypeID) o;

    return ID == that.ID;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + ID;
    return result;
  }
}
