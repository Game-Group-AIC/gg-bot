package aic.gas.sc.gg_bot.bot.model.agent;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_MOVE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_RACE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.HAS_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_PLAYER;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypePlayer;
import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import aic.gas.sc.gg_bot.mas.model.planing.ReactionOnChangeStrategy;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * Agent to represent "player" - to access field of Player
 */
@Slf4j
public class AgentPlayer extends AgentObservingGame<AgentTypePlayer> {

  public AgentPlayer(AgentTypePlayer agentType, BotFacade botFacade, APlayer aPlayer,
      ARace enemyStartingRace) {
    super(agentType, botFacade);

    //add itself to knowledge
    beliefs.updateFact(IS_PLAYER, aPlayer);
    if (!enemyStartingRace.equals(ARace.UNKNOWN)) {
      beliefs.updateFact(ENEMY_RACE, enemyStartingRace);
    } else {
      beliefs.updateFact(ENEMY_RACE, ARace.getRandomRace());
    }
  }

  public static final ReactionOnChangeStrategy FIND_MAIN_BASE = (memory, desireParameters) -> {
    Optional<ABaseLocationWrapper> ourBase = memory
        .getReadOnlyMemoriesForAgentType(AgentTypes.BASE_LOCATION)
        .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_BASE).get())
        .filter(readOnlyMemory -> readOnlyMemory.returnFactSetValueForGivenKey(HAS_BASE).get()
            .anyMatch(aUnitOfPlayer -> !aUnitOfPlayer.isMorphing() && !aUnitOfPlayer
                .isBeingConstructed()))
        .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(IS_BASE_LOCATION).get())
        .filter(ABaseLocationWrapper::isStartLocation)
        .findAny();
    if (ourBase.isPresent()) {
      memory.updateFact(BASE_TO_MOVE, ourBase.get());
    } else {
      memory.eraseFactValueForGivenKey(BASE_TO_MOVE);
    }
  };
}
