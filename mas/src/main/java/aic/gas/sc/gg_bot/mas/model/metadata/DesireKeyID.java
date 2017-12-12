package aic.gas.sc.gg_bot.mas.model.metadata;

import java.io.Serializable;
import lombok.Getter;

/**
 * Desire key identification
 */
public class DesireKeyID extends Key implements Serializable {

  @Getter
  private final int ID;

  public DesireKeyID(String name, int id) {
    super(name, DesireKeyID.class);
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

    DesireKeyID desireKeyID = (DesireKeyID) o;

    return ID == desireKeyID.ID;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + ID;
    return result;
  }

}
