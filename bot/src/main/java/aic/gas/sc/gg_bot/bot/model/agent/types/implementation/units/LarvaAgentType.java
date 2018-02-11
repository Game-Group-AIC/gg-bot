package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.units;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MORPHING_TO;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_UNIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.MORPH_TO;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeUnit;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithCommand;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LarvaAgentType {

  public static final AgentTypeUnit LARVA = AgentTypeUnit.builder()
      .agentTypeID(AgentTypes.LARVA)
      .usingTypesForFacts(Collections.singleton(IS_MORPHING_TO))
      .initializationStrategy(type -> {

        //Template for morphing command
        ConfigurationWithCommand.WithActingCommandDesiredByOtherAgent morphTo = ConfigurationWithCommand.
            WithActingCommandDesiredByOtherAgent.builder()
            .commandCreationStrategy(intention -> new ActCommand.DesiredByAnotherAgent(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                return intention.returnFactValueForGivenKey(IS_UNIT).get().morph(
                    intention.getDesireKey().returnFactValueForGivenKey(MORPH_TO).get()
                        .returnType());
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> !dataForDecision.madeDecisionToAny())
                .desiresToConsider(
                    Stream.of(DesiresKeys.MORPH_TO_OVERLORD, DesiresKeys.BOOST_GROUND_MELEE,
                        DesiresKeys.BOOST_GROUND_RANGED, DesiresKeys.BOOST_AIR,
                        DesiresKeys.MORPH_TO_DRONE).collect(Collectors.toSet()))
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> false)
                .build())
            .build();

        //morph to overlord
        type.addConfiguration(DesiresKeys.MORPH_TO_OVERLORD, morphTo);

        //morph to zergling
        type.addConfiguration(DesiresKeys.BOOST_GROUND_MELEE, morphTo);

        //morph to hydras
        type.addConfiguration(DesiresKeys.BOOST_GROUND_RANGED, morphTo);

        //morph to mutalisk
        type.addConfiguration(DesiresKeys.BOOST_AIR, morphTo);

        //morph to drone
        type.addConfiguration(DesiresKeys.MORPH_TO_DRONE, morphTo);

        //reason about morphing
        type.addConfiguration(DesiresKeys.MORPHING_TO, AgentTypeUnit.beliefsAboutMorphing);
      })
      .desiresWithIntentionToReason(Collections.singleton(DesiresKeys.MORPHING_TO))
      .build();
}
