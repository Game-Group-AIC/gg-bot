package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import bwapi.DamageType;
import bwapi.ExplosionType;
import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import bwapi.WeaponType;
import lombok.Getter;

/**
 * Wrapper for WeaponType
 */
public class AWeaponTypeWrapper extends AbstractWrapper<WeaponType> {

  private final UnitType whatUses;
  @Getter
  private final int maxRange;
  private final TechType tech;
  @Getter
  private final int minRange;
  @Getter
  private final int outerSplashRadius;
  @Getter
  private final boolean targetsNonRobotic;
  @Getter
  private final int innerSplashRadius;
  @Getter
  private final int medianSplashRadius;
  @Getter
  private final boolean targetsMechanical;
  @Getter
  private final boolean targetsNonBuilding;
  @Getter
  private final int damageFactor;
  @Getter
  private final ExplosionType explosionType;
  @Getter
  private final boolean targetsAir;
  @Getter
  private final DamageType damageType;
  @Getter
  private final boolean targetsTerrain;
  @Getter
  private final boolean targetsOwn;
  @Getter
  private final int damageCooldown;
  @Getter
  private final int damageAmount;
  @Getter
  private final boolean targetsGround;
  @Getter
  private final boolean targetsOrgOrMech;
  private final UpgradeType upgradeType;
  @Getter
  private final boolean targetsOrganic;
  @Getter
  private final int damageBonus;

  AWeaponTypeWrapper(WeaponType type) {
    super(type, type.toString());
    WrapperTypeFactory.add(this);

    //fields
    this.whatUses = type.whatUses();
    this.maxRange = type.maxRange();
    this.tech = type.getTech();
    this.minRange = type.minRange();
    this.outerSplashRadius = type.outerSplashRadius();
    this.targetsNonRobotic = type.targetsNonRobotic();
    this.innerSplashRadius = type.innerSplashRadius();
    this.medianSplashRadius = type.medianSplashRadius();
    this.targetsMechanical = type.targetsMechanical();
    this.targetsNonBuilding = type.targetsNonBuilding();
    this.damageFactor = type.damageFactor();
    this.explosionType = type.explosionType();
    this.targetsAir = type.targetsAir();
    this.damageType = type.damageType();
    this.targetsTerrain = type.targetsTerrain();
    this.targetsOwn = type.targetsOwn();
    this.damageCooldown = type.damageCooldown();
    this.damageAmount = type.damageAmount();
    this.targetsGround = type.targetsGround();
    this.targetsOrgOrMech = type.targetsOrgOrMech();
    this.upgradeType = type.upgradeType();
    this.targetsOrganic = type.targetsOrganic();
    this.damageBonus = type.damageBonus();
  }

  public AUnitTypeWrapper getWhatUses() {
    return WrapperTypeFactory.createFrom(whatUses);
  }

  public ATechTypeWrapper getTech() {
    return WrapperTypeFactory.createFrom(tech);
  }

  public AUpgradeTypeWrapper getUpgradeType() {
    return WrapperTypeFactory.createFrom(upgradeType);
  }

  public double getDamageNormalized() {
    if (isForType(WeaponType.Psi_Blades)) {
      return 16;
    } else {
      return getDamageAmount() * getDamageFactor();
    }
  }
}
