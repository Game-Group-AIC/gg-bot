package cz.jan.maly.model;

import bwapi.Game;
import cz.jan.maly.model.game.wrappers.AUnitWrapper;
import cz.jan.maly.model.metadata.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

import static cz.jan.maly.model.DesiresKeys.MINE_MINERALS;
import static cz.jan.maly.model.FactsKeys.*;

/**
 * Created by Jan on 15-Mar-17.
 */
public class AgentsTypes {

    public static final AgentType<Game> WORKER = new AgentType<Game>("WORKER",
            (memory, environment) -> {
                AUnitWrapper me = (AUnitWrapper) memory.returnFactValueForGivenKey(IS_UNIT).get();
//                System.out.println("Cam I attack air units: " + me.canAttackAirUnits());
                return true;
            }, new HashSet<>(), new HashSet<>(), Arrays.stream(new DesireKey[]{MINE_MINERALS}).collect(Collectors.toSet()), new HashSet<>()) {
        @Override
        protected void initializeConfiguration() {
            addDesireFormulationConfigurationForOwnDesireWithActingCommand(MINE_MINERALS,
                    new DecisionParameters(Arrays.stream(new FactKey<?>[]{IS_MINING_MINERAL}).collect(Collectors.toSet()),
                            Arrays.stream(new FactKey<?>[]{MINERAL}).collect(Collectors.toSet()),
                            new HashSet<>()),
                    (dataForDecision, desire) -> dataForDecision.returnFactValueForGivenKey(IS_MINING_MINERAL).isPresent() && dataForDecision.returnFactValueForGivenKey(IS_MINING_MINERAL).get() != null && !dataForDecision.returnFactSetValueForGivenKey(MINERAL).get().isEmpty(),
                    new DecisionParameters(new HashSet<>(), new HashSet<>(), new HashSet<>()),
                    (dataForDecision, intention) -> false,
                    new IntentionParameters(Arrays.stream(new FactKey<?>[]{IS_UNIT}).collect(Collectors.toSet()), new HashSet<>()),
                    null);
        }
    };

}