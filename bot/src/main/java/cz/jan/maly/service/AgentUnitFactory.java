package cz.jan.maly.service;

import bwapi.Unit;
import bwapi.UnitType;
import cz.jan.maly.model.AgentsTypes;
import cz.jan.maly.model.agent.BWAgentInGame;
import cz.jan.maly.model.game.wrappers.AUnitWithCommands;
import cz.jan.maly.model.game.wrappers.UnitWrapperFactory;
import cz.jan.maly.service.implementation.BotFacade;
import cz.jan.maly.utils.MyLogger;

import java.util.Optional;

/**
 * Factory to create agent for given unit
 * Created by Jan on 17-Dec-16.
 */
public class AgentUnitFactory implements AgentUnitHandler {

    @Override
    public synchronized Optional<BWAgentInGame> createAgentForUnit(Unit unit, BotFacade botFacade, int frameCount) {
        if (unit.getType().equals(UnitType.Zerg_Drone)) {
            Optional<AUnitWithCommands> wrappedUnit = Optional.ofNullable(UnitWrapperFactory.getCurrentWrappedUnitToCommand(unit, frameCount, true));
            if (!wrappedUnit.isPresent()) {
                MyLogger.getLogger().warning("Could not initiate unit " + UnitType.Zerg_Drone);
                throw new RuntimeException("Could not initiate unit " + UnitType.Zerg_Drone);
            }
            BWAgentInGame agent = new BWAgentInGame(AgentsTypes.WORKER, botFacade, wrappedUnit.get());
            return Optional.of(agent);
        }
        return Optional.empty();
    }

}
