package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import bwapi.Race;
import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import bwapi.WeaponType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

/**
 * Wrapper for BWMirror UnitType It also make it possible to use it by different threads
 */
public class AUnitTypeWrapper extends AbstractWrapper<UnitType> implements TypeToBuy {

  //types, only for zerg
  public static final AUnitTypeWrapper ZERGLING_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Zergling);
  public static final AUnitTypeWrapper DRONE_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Drone);
  public static final AUnitTypeWrapper HATCHERY_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Hatchery);
  public static final AUnitTypeWrapper LARVA_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Larva);
  public static final AUnitTypeWrapper EGG_TYPE = WrapperTypeFactory.createFrom(UnitType.Zerg_Egg);
  public static final AUnitTypeWrapper SPAWNING_POOL_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Spawning_Pool);
  public static final AUnitTypeWrapper OVERLORD_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Overlord);
  public static final AUnitTypeWrapper HYDRALISK_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Hydralisk);
  public static final AUnitTypeWrapper MUTALISK_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Mutalisk);
  public static final AUnitTypeWrapper CREEP_COLONY_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Creep_Colony);
  public static final AUnitTypeWrapper SUNKEN_COLONY_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Sunken_Colony);
  public static final AUnitTypeWrapper SPORE_COLONY_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Spore_Colony);
  public static final AUnitTypeWrapper EXTRACTOR_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Extractor);
  public static final AUnitTypeWrapper EVOLUTION_CHAMBER_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Evolution_Chamber);
  public static final AUnitTypeWrapper HYDRALISK_DEN_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Hydralisk_Den);
  public static final AUnitTypeWrapper LAIR_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Lair);
  public static final AUnitTypeWrapper SPIRE_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Spire);
  public static final AUnitTypeWrapper HIVE_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Hive);
  //not used in bot
  public static final AUnitTypeWrapper SCOURGE_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Scourge);
  public static final AUnitTypeWrapper LURKER_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Lurker);
  public static final AUnitTypeWrapper ULTRALISK_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Ultralisk);
  public static final AUnitTypeWrapper DEFILER_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Defiler);
  public static final AUnitTypeWrapper GUARDIAN_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Guardian);
  public static final AUnitTypeWrapper DEVOURER_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Devourer);
  public static final AUnitTypeWrapper QUEEN_TYPE = WrapperTypeFactory
      .createFrom(UnitType.Zerg_Queen);
  public static final Set<AUnitTypeWrapper> OTHER_UNIT_TYPES = Collections
      .unmodifiableSet(new HashSet<>(Arrays.asList(
          SCOURGE_TYPE, LURKER_TYPE, ULTRALISK_TYPE, DEFILER_TYPE, GUARDIAN_TYPE, DEVOURER_TYPE,
          QUEEN_TYPE)));
  public static final Set<AUnitTypeWrapper> BUILDING_TYPES = new HashSet<>(
      Arrays.asList(HATCHERY_TYPE, SPAWNING_POOL_TYPE, CREEP_COLONY_TYPE,
          SUNKEN_COLONY_TYPE, SPORE_COLONY_TYPE, EXTRACTOR_TYPE, EVOLUTION_CHAMBER_TYPE,
          HYDRALISK_DEN_TYPE, LAIR_TYPE, SPIRE_TYPE, HIVE_TYPE));
  public static final Set<AUnitTypeWrapper> UNITS_TYPES = new HashSet<>(
      Arrays.asList(ZERGLING_TYPE, DRONE_TYPE, LARVA_TYPE, EGG_TYPE, OVERLORD_TYPE,
          HYDRALISK_TYPE, MUTALISK_TYPE));
  @Getter
  private final boolean isMechanical;
  @Getter
  private final boolean isRobotic;
  @Getter
  private final boolean canMove;
  @Getter
  private final boolean isSpell;
  @Getter
  private final int armor;
  @Getter
  private final ATilePosition tileSize;
  @Getter
  private final boolean isAddon;
  @Getter
  private final boolean isHero;
  @Getter
  private final int width;
  private final List<UpgradeType> upgrades;
  @Getter
  private final int gasPrice;
  @Getter
  private final int height;
  @Getter
  private final boolean isWorker;
  @Getter
  private final double topSpeed;
  @Getter
  private final boolean isFlyer;
  @Getter
  private final Race race;
  @Getter
  private final boolean isBeacon;
  private final Map<UnitType, Integer> requiredUnits;
  private final TechType cloakingTech;
  private final List<TechType> abilities;
  private final TechType requiredTech;
  private final UpgradeType armorUpgrade;
  @Getter
  private final int seekRange;
  private final int supplyRequired;
  @Getter
  private final int spaceRequired;
  @Getter
  private final int dimensionDown;
  @Getter
  private final int destroyScore;
  @Getter
  private final int tileHeight;
  private final WeaponType groundWeapon;
  @Getter
  private final int maxShields;
  @Getter
  private final int dimensionUp;
  @Getter
  private final int sightRange;
  private final WeaponType airWeapon;
  @Getter
  private final int maxAirHits;
  @Getter
  private final int acceleration;
  @Getter
  private final int turnRadius;
  @Getter
  private final boolean canProduce;
  @Getter
  private final int haltDistance;
  @Getter
  private final boolean regeneratesHP;
  @Getter
  private final int dimensionRight;
  @Getter
  private final int tileWidth;
  @Getter
  private final int spaceProvided;
  @Getter
  private final int maxGroundHits;
  @Getter
  private final boolean isSpellcaster;
  @Getter
  private final int dimensionLeft;
  @Getter
  private final boolean isInvincible;
  @Getter
  private final int buildTime;
  @Getter
  private final boolean canAttack;
  @Getter
  private final boolean isOrganic;
  @Getter
  private final int maxEnergy;
  @Getter
  private final int supplyProvided;
  @Getter
  private final int maxHitPoints;
  @Getter
  private final int buildScore;
  @Getter
  private final boolean hasPermanentCloak;
  @Getter
  private final boolean isSpecialBuilding;
  @Getter
  private final boolean isResourceContainer;
  @Getter
  private final boolean isTwoUnitsInOneEgg;
  @Getter
  private final boolean requiresPsi;
  @Getter
  private final boolean isMineralField;
  @Getter
  private final boolean canBuildAddon;
  @Getter
  private final boolean requiresCreep;
  @Getter
  private final boolean isRefinery;
  @Getter
  private final boolean isBurrowable;
  @Getter
  private final boolean producesCreep;
  @Getter
  private final boolean isFlyingBuilding;
  @Getter
  private final boolean isPowerup;
  @Getter
  private final boolean isFlagBeacon;
  @Getter
  private final boolean isResourceDepot;
  @Getter
  private final boolean isCloakable;
  @Getter
  private final boolean isBuilding;
  @Getter
  private final boolean isNeutral;
  @Getter
  private final boolean isDetector;
  @Getter
  private final boolean producesLarva;
  private final List<TechType> researchesWhat;
  @Getter
  private final boolean isCritter;
  private final List<UpgradeType> upgradesWhat;
  @Getter
  private final boolean isMelee;
  @Getter
  private final int mineralPrice;
  @Getter
  private final boolean isMilitaryBuilding;
  @Getter
  private final boolean isMilitaryBuildingAntiAir;
  @Getter
  private final boolean isMilitaryBuildingAntiGround;
  @Getter
  private final boolean isSupplyUnit;
  @Getter
  private final boolean isGasBuilding;
  @Getter
  private final boolean isBase;
  @Getter
  private final boolean repairableMechanically;
  @Getter
  private final boolean healable;

  AUnitTypeWrapper(UnitType type) {
    super(type, type.toString());
    WrapperTypeFactory.add(this);

    //original fields
    this.isMechanical = type.isMechanical();
    this.isRobotic = type.isRobotic();
    this.canMove = type.canMove();
    this.isSpell = type.isSpell();
    this.armor = type.armor();
    this.tileSize = ATilePosition.wrap(type.tileSize());
    this.isAddon = type.isAddon();
    this.isHero = type.isHero();
    this.width = type.width();
    this.upgrades = new ArrayList<>(type.upgrades());
    this.gasPrice = type.gasPrice();
    this.height = type.height();
    this.isWorker = type.isWorker();
    this.topSpeed = type.topSpeed();
    this.isFlyer = type.isFlyer();
    this.race = type.getRace();
    this.isBeacon = type.isBeacon();
    this.requiredUnits = type.requiredUnits().entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    this.cloakingTech = type.cloakingTech();
    this.abilities = new ArrayList<>(type.abilities());
    this.requiredTech = type.requiredTech();
    this.armorUpgrade = type.armorUpgrade();
    this.seekRange = type.seekRange();
    this.supplyRequired = type.supplyRequired();
    this.spaceRequired = type.spaceRequired();
    this.dimensionDown = type.dimensionDown();
    this.destroyScore = type.destroyScore();
    this.tileHeight = type.tileHeight();
    this.groundWeapon = type.groundWeapon();
    this.maxShields = type.maxShields();
    this.dimensionUp = type.dimensionUp();
    this.sightRange = type.sightRange();
    this.airWeapon = type.airWeapon();
    this.maxAirHits = type.maxAirHits();
    this.acceleration = type.acceleration();
    this.turnRadius = type.turnRadius();
    this.canProduce = type.canProduce();
    this.haltDistance = type.haltDistance();
    this.regeneratesHP = type.regeneratesHP();
    this.dimensionRight = type.dimensionRight();
    this.tileWidth = type.tileWidth();
    this.spaceProvided = type.spaceProvided();
    this.maxGroundHits = type.maxGroundHits();
    this.isSpellcaster = type.isSpellcaster();
    this.dimensionLeft = type.dimensionLeft();
    this.isInvincible = type.isInvincible();
    this.buildTime = type.buildTime();
    this.canAttack = type.canAttack();
    this.isOrganic = type.isOrganic();
    this.maxEnergy = type.maxEnergy();
    this.supplyProvided = type.supplyProvided();
    this.maxHitPoints = type.maxHitPoints();
    this.buildScore = type.buildScore();
    this.hasPermanentCloak = type.hasPermanentCloak();
    this.isSpecialBuilding = type.isSpecialBuilding();
    this.isResourceContainer = type.isResourceContainer();
    this.isTwoUnitsInOneEgg = type.isTwoUnitsInOneEgg();
    this.requiresPsi = type.requiresPsi();
    this.isMineralField = type.isMineralField();
    this.canBuildAddon = type.canBuildAddon();
    this.requiresCreep = type.requiresCreep();
    this.isRefinery = type.isRefinery();
    this.isBurrowable = type.isBurrowable();
    this.producesCreep = type.producesCreep();
    this.isFlyingBuilding = type.isFlyingBuilding();
    this.isPowerup = type.isPowerup();
    this.isFlagBeacon = type.isFlagBeacon();
    this.isResourceDepot = type.isResourceDepot();
    this.isCloakable = type.isCloakable();
    this.isBuilding = type.isBuilding();
    this.isNeutral = type.isNeutral();
    this.isDetector = type.isDetector();
    this.producesLarva = type.producesLarva();
    this.researchesWhat = new ArrayList<>(type.researchesWhat());
    this.isCritter = type.isCritter();
    this.upgradesWhat = new ArrayList<>(type.upgradesWhat());
    this.mineralPrice = type.mineralPrice();

    //additional fields
    this.isBase = type.isBuilding() && isType(type,
        new UnitType[]{UnitType.Terran_Command_Center, UnitType.Protoss_Nexus,
            UnitType.Zerg_Hatchery,
            UnitType.Zerg_Lair, UnitType.Zerg_Hive});
    this.isGasBuilding = type.isBuilding() && isType(type,
        new UnitType[]{UnitType.Terran_Refinery, UnitType.Protoss_Assimilator,
            UnitType.Zerg_Extractor});
    this.isSupplyUnit = isType(type,
        new UnitType[]{UnitType.Protoss_Pylon, UnitType.Terran_Supply_Depot,
            UnitType.Zerg_Overlord});
    this.isMilitaryBuilding = type.isBuilding() && isType(type,
        new UnitType[]{UnitType.Terran_Bunker, UnitType.Terran_Missile_Turret,
            UnitType.Protoss_Photon_Cannon,
            UnitType.Zerg_Sunken_Colony, UnitType.Zerg_Spore_Colony, UnitType.Zerg_Creep_Colony}
    );
    this.isMilitaryBuildingAntiAir =
        isMilitaryBuilding && isType(type, new UnitType[]{UnitType.Terran_Bunker,
            UnitType.Protoss_Photon_Cannon, UnitType.Zerg_Spore_Colony}
        );
    this.isMilitaryBuildingAntiGround = isMilitaryBuilding && isType(type,
        new UnitType[]{UnitType.Terran_Bunker, UnitType.Protoss_Photon_Cannon,
            UnitType.Zerg_Sunken_Colony}
    );

    this.isMelee = isType(type,
        new UnitType[]{UnitType.Terran_SCV, UnitType.Terran_Firebat, UnitType.Protoss_Probe,
            UnitType.Protoss_Zealot, UnitType.Protoss_Dark_Templar, UnitType.Zerg_Drone,
            UnitType.Zerg_Zergling,
            UnitType.Zerg_Broodling}
    );

    // Repair & Heal
    this.repairableMechanically = isBuilding() || isMechanical();
    this.healable = isOrganic() || isWorker();
  }

  public List<AUpgradeTypeWrapper> getUpgrades() {
    return upgrades.stream()
        .map(WrapperTypeFactory::createFrom)
        .collect(Collectors.toList());
  }

  public Map<AUnitTypeWrapper, Integer> getRequiredUnits() {
    return requiredUnits.entrySet().stream()
        .collect(Collectors
            .toMap(entry -> WrapperTypeFactory.createFrom(entry.getKey()), Map.Entry::getValue));
  }

  public ATechTypeWrapper getCloakingTech() {
    return WrapperTypeFactory.createFrom(cloakingTech);
  }

  public List<ATechTypeWrapper> getAbilities() {
    return abilities.stream()
        .map(WrapperTypeFactory::createFrom)
        .collect(Collectors.toList());
  }

  public ATechTypeWrapper getRequiredTech() {
    return WrapperTypeFactory.createFrom(requiredTech);
  }

  public AUpgradeTypeWrapper getArmorUpgrade() {
    return WrapperTypeFactory.createFrom(armorUpgrade);
  }

  public AWeaponTypeWrapper getGroundWeapon() {
    return WrapperTypeFactory.createFrom(groundWeapon);
  }

  public AWeaponTypeWrapper getAirWeapon() {
    return WrapperTypeFactory.createFrom(airWeapon);
  }

  public List<ATechTypeWrapper> getResearchesWhat() {
    return researchesWhat.stream()
        .map(WrapperTypeFactory::createFrom)
        .collect(Collectors.toList());
  }

  public List<AUpgradeTypeWrapper> getUpgradesWhat() {
    return upgradesWhat.stream()
        .map(WrapperTypeFactory::createFrom)
        .collect(Collectors.toList());
  }

  /**
   * Returns max shoot range (in build tiles) of this unit against land targets.
   */
  public double getShootRangeGround() {
    return getGroundWeapon().getMaxRange() / 32;
  }

  /**
   * Returns max shoot range (in build tiles) of this unit against air targets.
   */
  public double getShootRangeAir() {
    return getAirWeapon().getMaxRange() / 32;
  }

  /**
   * Returns max shoot range (in build tiles) of this unit against given <b>opponentUnit</b>.
   */
  public double getShootRangeAgainst(AUnitTypeWrapper type) {
    if (type.isFlyer()) {
      return getShootRangeAir();
    } else {
      return getShootRangeGround();
    }
  }

  public boolean isBunker() {
    return this.isForType(UnitType.Terran_Bunker);
  }

  public boolean isSpiderMine() {
    return this.isForType(UnitType.Terran_Vulture_Spider_Mine);
  }

  public boolean isLarvaOrEgg() {
    return isEgg() || isLarva();
  }

  public boolean isLarva() {
    return this.isForType(UnitType.Zerg_Larva);
  }

  public boolean isEgg() {
    return this.isForType(UnitType.Zerg_Egg);
  }

  /**
   * Not that we're racists, but spider mines and larvas aren't really units...
   */
  public boolean isNotActuallyUnit() {
    return isSpiderMine() || isLarvaOrEgg();
  }

  /**
   * Returns true if unit has anti-ground weapon.
   */
  public boolean canAttackGroundUnits() {
    return !getGroundWeapon().isForType(WeaponType.None);
  }

  /**
   * Returns true if unit has anti-air weapon.
   */
  public boolean canAttackAirUnits() {
    return !getAirWeapon().isForType(WeaponType.None);
  }

  /**
   * Returns true if unit has anti-air weapon.
   */
  public boolean isMedic() {
    return this.isForType(UnitType.Terran_Medic);
  }

  public boolean canBeHealed() {
    return repairableMechanically || healable;
  }

  @Override
  public int mineralCost() {
    return this.mineralPrice;
  }

  @Override
  public int gasCost() {
    return this.gasPrice;
  }

  @Override
  public int supplyRequired() {
    return this.supplyRequired;
  }

  @Override
  public Stream<AUnitTypeWrapper> unitTypeDependencies() {
    return getRequiredUnits().keySet().stream()
        .filter(unitTypeWrapper -> unitTypeWrapper.type != UnitType.None);
  }

  @Override
  public Optional<ATechTypeWrapper> techTypeDependency() {
    return Optional.ofNullable(getRequiredTech())
        .map(aTechTypeWrapper -> aTechTypeWrapper.type == TechType.None ? null : aTechTypeWrapper);
  }
}
