package aic.gas.sc.gg_bot.bot.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ATechTypeWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AbstractWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.TypeToBuy;
import aic.gas.sc.gg_bot.bot.service.IRequirementsChecker;
import bwapi.Player;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//TODO this is just heuristic...
public class RequirementsChecker implements IRequirementsChecker {

  private Set<AUnitTypeWrapper> builtUnitTypes = new HashSet<>();
  private Set<ATechTypeWrapper> researchedTechs = new HashSet<>();

  @Override
  public <T extends AbstractWrapper<?> & TypeToBuy> boolean areDependenciesMeet(T t) {
    return t.unitTypeDependencies()
        .allMatch(unitTypeWrapper -> builtUnitTypes.contains(unitTypeWrapper))
        && (!t.techTypeDependency().isPresent() || researchedTechs
        .contains(t.techTypeDependency().get()));
  }

  @Override
  public void updateBuildTreeByPlayersData(Player player) {
    researchedTechs = Stream.concat(ATechTypeWrapper.TECH_TYPES.stream()
            .filter(aTechTypeWrapper -> !researchedTechs.contains(aTechTypeWrapper))
            .filter(aTechTypeWrapper -> player.hasResearched(aTechTypeWrapper.getType())),
        researchedTechs.stream())
        .collect(Collectors.toSet());
    builtUnitTypes = Stream
        .concat(AUnitTypeWrapper.BUILDING_TYPES.stream(), AUnitTypeWrapper.UNITS_TYPES.stream())
        .filter(unitTypeWrapper -> player.allUnitCount(unitTypeWrapper.getType()) > 0)
        .collect(Collectors.toSet());
  }
}
