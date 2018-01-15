package aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers;

import bwapi.Race;
import bwapi.UnitType;
import bwapi.UpgradeType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Getter;

/**
 * Wrapper for UpgradeType
 */
public class AUpgradeTypeWrapper extends AbstractWrapper<UpgradeType> implements TypeToBuy {

  //types, only for zerg
  public static final AUpgradeTypeWrapper METABOLIC_BOOST_TYPE = WrapperTypeFactory
      .createFrom(UpgradeType.Metabolic_Boost);
  public static final AUpgradeTypeWrapper ADRENAL_GLANDS_TYPE = WrapperTypeFactory
      .createFrom(UpgradeType.Adrenal_Glands);
  public static final AUpgradeTypeWrapper UPGRADE_MISSILE_ATTACK_TYPE = WrapperTypeFactory
      .createFrom(UpgradeType.Zerg_Missile_Attacks);
  public static final AUpgradeTypeWrapper UPGRADE_MELEE_ATTACK_TYPE = WrapperTypeFactory
      .createFrom(UpgradeType.Zerg_Melee_Attacks);
  public static final AUpgradeTypeWrapper UPGRADE_CARAPACE_TYPE = WrapperTypeFactory
      .createFrom(UpgradeType.Zerg_Carapace);
  public static final AUpgradeTypeWrapper MUSCULAR_AUGMENTS_TYPE = WrapperTypeFactory
      .createFrom(UpgradeType.Muscular_Augments);
  public static final AUpgradeTypeWrapper GROOVED_SPINES_TYPE = WrapperTypeFactory
      .createFrom(UpgradeType.Grooved_Spines);
  public static final AUpgradeTypeWrapper ANTENNAE_TYPE = WrapperTypeFactory
      .createFrom(UpgradeType.Antennae);
  public static final AUpgradeTypeWrapper PNEUMATIZED_CARAPACE_TYPE = WrapperTypeFactory
      .createFrom(UpgradeType.Pneumatized_Carapace);
  public static final AUpgradeTypeWrapper UPGRADE_FLYER_ATTACK_TYPE = WrapperTypeFactory
      .createFrom(UpgradeType.Zerg_Flyer_Attacks);
  public static final AUpgradeTypeWrapper UPGRADE_FLAYER_CARAPACE_TYPE = WrapperTypeFactory
      .createFrom(UpgradeType.Zerg_Flyer_Carapace);
  static final Set<AUpgradeTypeWrapper> UPGRADE_TYPES = new HashSet<>(
      Arrays.asList(METABOLIC_BOOST_TYPE, ADRENAL_GLANDS_TYPE, UPGRADE_MISSILE_ATTACK_TYPE,
          UPGRADE_MELEE_ATTACK_TYPE, UPGRADE_CARAPACE_TYPE, MUSCULAR_AUGMENTS_TYPE,
          GROOVED_SPINES_TYPE, ANTENNAE_TYPE,
          PNEUMATIZED_CARAPACE_TYPE, UPGRADE_FLYER_ATTACK_TYPE, UPGRADE_FLAYER_CARAPACE_TYPE));
  @Getter
  private final Race race;
  @Getter
  private final List<Integer> gasPriceList;
  @Getter
  private final int gasPrice;
  @Getter
  private final int maxRepeats;
  @Getter
  private final List<Integer> mineralPriceList;
  @Getter
  private final int mineralPrice;
  @Getter
  private final int gasPriceFactor;
  @Getter
  private final List<Integer> upgradeTimeList;
  @Getter
  private final int upgradeTime;
  private final UnitType whatUpgrades;
  private final List<UnitType> whatsRequiredList;
  private final UnitType whatsRequired;
  @Getter
  private final int upgradeTimeFactor;
  @Getter
  private final int mineralPriceFactor;

  AUpgradeTypeWrapper(UpgradeType type) {
    super(type, type.toString());
    WrapperTypeFactory.add(this);

    //fields
    List<Integer> repeats = IntStream.rangeClosed(1, type.maxRepeats()).boxed()
        .collect(Collectors.toList());
    this.race = type.getRace();
    this.gasPrice = type.gasPrice();
    this.gasPriceList = repeats.stream().map(type::gasPrice).collect(Collectors.toList());
    this.maxRepeats = type.maxRepeats();
    this.mineralPrice = type.mineralPrice();
    this.mineralPriceList = repeats.stream().map(type::mineralPrice).collect(Collectors.toList());
    this.gasPriceFactor = type.gasPriceFactor();
    this.upgradeTime = type.upgradeTime();
    this.upgradeTimeList = repeats.stream().map(type::upgradeTime).collect(Collectors.toList());
    this.whatUpgrades = type.whatUpgrades();
    this.whatsRequired = type.whatsRequired();
    this.whatsRequiredList = repeats.stream().map(type::whatsRequired).collect(Collectors.toList());
    this.upgradeTimeFactor = type.upgradeTimeFactor();
    this.mineralPriceFactor = type.mineralPriceFactor();
  }

  public AUnitTypeWrapper getWhatUpgrades() {
    return WrapperTypeFactory.createFrom(whatUpgrades);
  }

  public List<AUnitTypeWrapper> getWhatsRequiredList() {
    return whatsRequiredList.stream()
        .map(WrapperTypeFactory::createFrom)
        .collect(Collectors.toList());
  }

  public AUnitTypeWrapper getWhatsRequired() {
    return WrapperTypeFactory.createFrom(whatsRequired);
  }

  //TODO this not correct (price is dependant on level)
  @Override
  public int mineralCost() {
    return mineralPrice;
  }

  //TODO this not correct (price is dependant on level)
  @Override
  public int gasCost() {
    return gasPrice;
  }
}
