package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import bwapi.WeaponType;
import java.util.Set;

/**
 * Class with static registers to wrap types
 */
public class WrapperTypeFactory {

  private static final WrapperTypeRegister<TechType, ATechTypeWrapper> A_TECH_TYPE_REGISTER;
  private static final WrapperTypeRegister<UpgradeType, AUpgradeTypeWrapper> UPGRADE_TYPE_REGISTER;
  private static final WrapperTypeRegister<WeaponType, AWeaponTypeWrapper> WEAPON_TYPE_REGISTER;
  private static final WrapperTypeRegister<UnitType, AUnitTypeWrapper> UNIT_TYPE_REGISTER;

  static {
    A_TECH_TYPE_REGISTER = new WrapperTypeRegister<>(ATechTypeWrapper::new);
    UPGRADE_TYPE_REGISTER = new WrapperTypeRegister<>(AUpgradeTypeWrapper::new);
    WEAPON_TYPE_REGISTER = new WrapperTypeRegister<>(AWeaponTypeWrapper::new);
    UNIT_TYPE_REGISTER = new WrapperTypeRegister<>(AUnitTypeWrapper::new);
  }

  /**
   * Clear cache
   */
  public static void clearCache() {
    A_TECH_TYPE_REGISTER.clear();
    UPGRADE_TYPE_REGISTER.clear();
    UPGRADE_TYPE_REGISTER.clear();
    UPGRADE_TYPE_REGISTER.clear();
  }

  /**
   * Returns corresponding wrapper for type instance
   */
  public static AUnitTypeWrapper createFrom(UnitType type) {
    if (type == null) {
      return createFrom(UnitType.None);
    }
    return UNIT_TYPE_REGISTER.createFrom(type);
  }

  static void add(AUnitTypeWrapper type) {
    UNIT_TYPE_REGISTER.addWrappedType(type.type, type);
  }

  /**
   * Returns types for race
   */
  public static Set<AUnitTypeWrapper> buildings() {
    return AUnitTypeWrapper.BUILDING_TYPES;
  }

  /**
   * Returns types for race
   */
  public static Set<AUnitTypeWrapper> units() {
    return AUnitTypeWrapper.UNITS_TYPES;
  }

  /**
   * Returns types for race
   */
  public static Set<ATechTypeWrapper> techs() {
    return ATechTypeWrapper.TECH_TYPES;
  }

  /**
   * Returns types for race
   */
  public static Set<AUpgradeTypeWrapper> upgrades() {
    return AUpgradeTypeWrapper.UPGRADE_TYPES;
  }

  /**
   * Returns corresponding wrapper for type instance
   */
  static AWeaponTypeWrapper createFrom(WeaponType type) {
    if (type == null) {
      return createFrom(WeaponType.None);
    }
    return WEAPON_TYPE_REGISTER.createFrom(type);
  }

  static void add(AWeaponTypeWrapper type) {
    WEAPON_TYPE_REGISTER.addWrappedType(type.type, type);
  }

  /**
   * Returns corresponding wrapper for type instance
   */
  static AUpgradeTypeWrapper createFrom(UpgradeType type) {
    if (type == null) {
      return createFrom(UpgradeType.None);
    }
    return UPGRADE_TYPE_REGISTER.createFrom(type);
  }

  static void add(AUpgradeTypeWrapper type) {
    UPGRADE_TYPE_REGISTER.addWrappedType(type.type, type);
  }

  /**
   * Returns corresponding wrapper for type instance
   */
  static ATechTypeWrapper createFrom(TechType type) {
    if (type == null) {
      return createFrom(TechType.None);
    }
    return A_TECH_TYPE_REGISTER.createFrom(type);
  }

  static void add(ATechTypeWrapper type) {
    A_TECH_TYPE_REGISTER.addWrappedType(type.type, type);
  }
}
