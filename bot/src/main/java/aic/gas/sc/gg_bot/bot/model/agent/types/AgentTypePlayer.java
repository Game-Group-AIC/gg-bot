package aic.gas.sc.gg_bot.bot.model.agent.types;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_RACE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_PLAYER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.MADE_OBSERVATION_IN_FRAME;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentType;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeMakingObservations;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKey;
import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import aic.gas.sc.gg_bot.mas.model.planing.command.ObservingCommand;
import bwapi.Game;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * Type definition - agent type for player
 */
@Slf4j
public class AgentTypePlayer extends AgentTypeMakingObservations<Game> {

  //single definition of command to observe to be used by all agents of this type
  private static final ObservingCommand<Game> OBSERVING_COMMAND = (memory, environment) -> {
    Optional<APlayer> aPlayer = memory.returnFactValueForGivenKey(IS_PLAYER);
    if (!aPlayer.isPresent()) {
      log.error("Trying to access player but it is not present.");
      throw new RuntimeException("Trying to access player but it is not present.");
    }

    //update fields by creating new instance
    APlayer player = aPlayer.get().makeObservationOfEnvironment(environment.getFrameCount());

    //add updated version of itself to knowledge
    memory.updateFact(IS_PLAYER, player);
    memory.updateFact(MADE_OBSERVATION_IN_FRAME, environment.getFrameCount());
    return true;
  };

  /**
   * Define agent type. Together with initial desires
   */
  @Builder
  private AgentTypePlayer(AgentTypeID agentTypeID, Set<DesireKey> desiresForOthers,
      Set<DesireKey> desiresWithAbstractIntention,
      Set<DesireKey> desiresWithIntentionToAct, Set<DesireKey> desiresWithIntentionToReason,
      Set<FactKey<?>> usingTypesForFacts, Set<FactKey<?>> usingTypesForFactSets,
      AgentType.ConfigurationInitializationStrategy initializationStrategy,
      int skipTurnsToMakeObservation) {
    super(agentTypeID, desiresForOthers, desiresWithAbstractIntention, desiresWithIntentionToAct,
        desiresWithIntentionToReason,

        //add facts related to agent - IS_UNIT, REPRESENTS_UNIT
        Stream.concat(usingTypesForFacts.stream(),
            Arrays.stream(new FactKey<?>[]{IS_PLAYER, ENEMY_RACE,
                MADE_OBSERVATION_IN_FRAME})).collect(Collectors.toSet()),
        usingTypesForFactSets, initializationStrategy, OBSERVING_COMMAND,
        skipTurnsToMakeObservation);
  }

  //builder with default fields
  public static class AgentTypePlayerBuilder extends AgentTypeMakingObservationsBuilder {

    private Set<DesireKey> desiresForOthers = new HashSet<>();
    private Set<DesireKey> desiresWithAbstractIntention = new HashSet<>();
    private Set<DesireKey> desiresWithIntentionToAct = new HashSet<>();
    private Set<DesireKey> desiresWithIntentionToReason = new HashSet<>();
    private Set<FactKey<?>> usingTypesForFacts = new HashSet<>();
    private Set<FactKey<?>> usingTypesForFactSets = new HashSet<>();
    private int skipTurnsToMakeObservation = 1;
  }

}
