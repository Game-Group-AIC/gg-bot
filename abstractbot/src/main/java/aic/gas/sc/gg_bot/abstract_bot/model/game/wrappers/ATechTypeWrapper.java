package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import bwapi.Order;
import bwapi.Race;
import bwapi.TechType;
import bwapi.UnitType;
import bwapi.WeaponType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

/**
 * Wrapper for TechType
 */
public class ATechTypeWrapper extends AbstractWrapper<TechType> {

  //types, only for zerg
  public static final ATechTypeWrapper RESEARCH_BURROW_TYPE = WrapperTypeFactory
      .createFrom(TechType.Burrowing);
  public static final ATechTypeWrapper RESEARCH_LURKER_ASPECT_TYPE = WrapperTypeFactory
      .createFrom(TechType.Lurker_Aspect);
  static final Set<ATechTypeWrapper> TECH_TYPES = new HashSet<>(
      Arrays.asList(RESEARCH_BURROW_TYPE, RESEARCH_LURKER_ASPECT_TYPE));
  @Getter
  private final Race race;
  @Getter
  private final int gasPrice;
  @Getter
  private final Order order;
  @Getter
  private final int energyCost;
  @Getter
  private final int researchTime;
  private final WeaponType weapon;
  @Getter
  private final boolean targetsUnit;
  private final UnitType whatResearches;
  @Getter
  private final int mineralPrice;
  @Getter
  private final UnitType requiredUnit;
  @Getter
  private final boolean targetsPosition;

  ATechTypeWrapper(TechType type) {
    super(type, type.toString());
    WrapperTypeFactory.add(this);

    //original fields
    this.race = type.getRace();
    this.gasPrice = type.gasPrice();
    this.order = type.getOrder();
    this.energyCost = type.energyCost();
    this.researchTime = type.researchTime();
    this.weapon = type.getWeapon();
    this.targetsUnit = type.targetsUnit();
    this.whatResearches = type.whatResearches();
    this.mineralPrice = type.mineralPrice();
    this.requiredUnit = type.requiredUnit();
    this.targetsPosition = type.targetsPosition();
  }

  public AWeaponTypeWrapper getAWeaponTypeWrapper() {
    return WrapperTypeFactory.createFrom(weapon);
  }

  public AUnitTypeWrapper getWhatResearches() {
    return WrapperTypeFactory.createFrom(whatResearches);
  }

  public AUnitTypeWrapper getRequiredUnit() {
    return WrapperTypeFactory.createFrom(requiredUnit);
  }

}
