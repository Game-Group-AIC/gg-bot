package aic.gas.sc.gg_bot.bot.model.agent.types;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.*;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.UnitWrapperFactory;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeMakingObservations;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import aic.gas.sc.gg_bot.mas.model.planing.command.IObservingCommand;
import bwapi.Game;
import bwta.BaseLocation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * Type definition - agent type for base location <p>
 */
@Slf4j
public class AgentTypeBaseLocation extends AgentTypeMakingObservations<Game> {

  //single definition of command to observe to be used by all agents of this type
  private static final IObservingCommand<Game> OBSERVING_COMMAND = (memory, environment) -> {
    Optional<ABaseLocationWrapper> baseLocation = memory
        .returnFactValueForGivenKey(IS_BASE_LOCATION);
    if (!baseLocation.isPresent()) {
      log.error("Trying to access commendable unit but it is not present.");
      throw new RuntimeException("Trying to access commendable unit but it is not present.");
    }
    updateKnowledgeAboutResources(baseLocation.get().getWrappedPosition(), memory,
        environment.getFrameCount());
    return true;
  };

  /**
   * Define agent type. Together with initial desires
   */
  @Builder
  private AgentTypeBaseLocation(Set<DesireKey> desiresForOthers,
      Set<DesireKey> desiresWithAbstractIntention,
      Set<DesireKey> desiresWithIntentionToAct, Set<DesireKey> desiresWithIntentionToReason,
      Set<FactKey<?>> usingTypesForFacts, Set<FactKey<?>> usingTypesForFactSets,
      IConfigurationInitializationStrategy initializationStrategy,
      int skipTurnsToMakeObservation) {
    super(AgentTypes.BASE_LOCATION.getId(), desiresForOthers, desiresWithAbstractIntention,
        desiresWithIntentionToAct, desiresWithIntentionToReason,

        //add facts related to agent
        Stream.concat(usingTypesForFacts.stream(), Stream.of(IS_BASE_LOCATION,
            IS_MINERAL_ONLY, IS_ISLAND, IS_START_LOCATION,
            IS_BASE_LOCATION, WAS_VISITED, BASE_TO_MOVE))
            .collect(Collectors.toSet()),

        //add fact set related to resources
        Stream.concat(usingTypesForFactSets.stream(),
            Arrays.stream(new FactKey<?>[]{MINERAL, GEYSER}))
            .collect(Collectors.toSet()),
        initializationStrategy, OBSERVING_COMMAND, skipTurnsToMakeObservation);
  }

  /**
   * Method to update base info about resources. DO NOT CALL OUTSIDE MAIN GAME THREAD!
   */
  private static void updateKnowledgeAboutResources(BaseLocation location, WorkingMemory memory,
      int frameCount) {
    Set<AUnit> minerals = location.getMinerals().stream()
        .map(unit -> UnitWrapperFactory.wrapResourceUnits(unit, frameCount, false))
        .filter(AUnit::isExists)
        .collect(Collectors.toSet());
    memory.updateFactSetByFacts(MINERAL, minerals);

    Set<AUnit> geysers = location.getGeysers().stream()
        .map(unit -> UnitWrapperFactory.wrapResourceUnits(unit, frameCount, false))
        .filter(AUnit::isExists)
        .collect(Collectors.toSet());
    memory.updateFactSetByFacts(GEYSER, geysers);
  }

  //builder with default fields
  public static class AgentTypeBaseLocationBuilder extends AgentTypeMakingObservationsBuilder {

    private Set<DesireKey> desiresForOthers = new HashSet<>();
    private Set<DesireKey> desiresWithAbstractIntention = new HashSet<>();
    private Set<DesireKey> desiresWithIntentionToAct = new HashSet<>();
    private Set<DesireKey> desiresWithIntentionToReason = new HashSet<>();
    private Set<FactKey<?>> usingTypesForFacts = new HashSet<>();
    private Set<FactKey<?>> usingTypesForFactSets = new HashSet<>();
    private int skipTurnsToMakeObservation = 1;
  }

}
