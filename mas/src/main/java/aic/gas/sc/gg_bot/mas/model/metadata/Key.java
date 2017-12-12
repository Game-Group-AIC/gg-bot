package aic.gas.sc.gg_bot.mas.model.metadata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract class key to be extended by each class describing some kind of metadata used in
 * framework
 */
@Slf4j
public abstract class Key {

  //structure to keep track of names to prevent duplicities
  private static final Map<Class<? extends Key>, Set<String>> keysNameByClass = new HashMap<>();

  @Getter
  final String name;

  @Getter
  final Class<? extends Key> classOfKey;

  protected Key(String name, Class<? extends Key> classOfKey) {
    synchronized (keysNameByClass) {
      Set<String> identificationsForClass = keysNameByClass
          .computeIfAbsent(classOfKey, aClass -> new HashSet<>());
      if (identificationsForClass.contains(name)) {
        log.error(
            "Key with name " + name + " was already defined for " + classOfKey.getSimpleName());
        throw new IllegalArgumentException(
            "Key with name " + name + " was already defined for " + classOfKey.getSimpleName());
      }
      this.name = name;
      this.classOfKey = classOfKey;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Key key = (Key) o;

    return name.equals(key.name) && classOfKey.equals(key.classOfKey);
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + classOfKey.hashCode();
    return result;
  }
}
