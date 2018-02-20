package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.virtual;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LOCATION;
import static aic.gas.sc.gg_bot.bot.model.agent.types.implementation.AgentTypeUtils.createConfigurationWithSharedDesireToTrainFromTemplate;
import static aic.gas.sc.gg_bot.bot.model.agent.types.implementation.AgentTypeUtils.createOwnConfigurationWithAbstractPlanToTrainFromTemplate;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.bot.model.DesiresKeys;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentType;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithAbstractPlan;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithSharedDesire;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UnitOrderManager {

  public static final AgentType UNIT_ORDER_MANAGER = AgentType.builder()
      .agentTypeID(AgentTypes.UNIT_ORDER_MANAGER)
      .usingTypesForFacts(Collections.singleton(LOCATION))
      .initializationStrategy(type -> {

        //build zerglings
        ConfigurationWithAbstractPlan trainZergling = createOwnConfigurationWithAbstractPlanToTrainFromTemplate(
            DesiresKeys.BOOST_GROUND_MELEE,
            AUnitTypeWrapper.ZERGLING_TYPE,
            AgentTypes.UNIT_ORDER_MANAGER,
            FeatureContainerHeaders.BOOST_GROUND_MELEE);
        type.addConfiguration(DesiresKeys.BOOST_GROUND_MELEE, trainZergling, true);

        ConfigurationWithSharedDesire trainZerglingShared = createConfigurationWithSharedDesireToTrainFromTemplate(
            DesiresKeys.BOOST_GROUND_MELEE,
            AUnitTypeWrapper.ZERGLING_TYPE);
        type.addConfiguration(DesiresKeys.BOOST_GROUND_MELEE, DesiresKeys.BOOST_GROUND_MELEE,
            trainZerglingShared);

        //build hydras
        ConfigurationWithAbstractPlan trainHydra = createOwnConfigurationWithAbstractPlanToTrainFromTemplate(
            DesiresKeys.BOOST_GROUND_RANGED, AUnitTypeWrapper.HYDRALISK_TYPE,
            AgentTypes.UNIT_ORDER_MANAGER,
            FeatureContainerHeaders.BOOST_GROUND_RANGED);
        type.addConfiguration(DesiresKeys.BOOST_GROUND_RANGED, trainHydra, true);
        ConfigurationWithSharedDesire trainHydraShared = createConfigurationWithSharedDesireToTrainFromTemplate(
            DesiresKeys.BOOST_GROUND_RANGED, AUnitTypeWrapper.HYDRALISK_TYPE);
        type.addConfiguration(DesiresKeys.BOOST_GROUND_RANGED, DesiresKeys.BOOST_GROUND_RANGED,
            trainHydraShared);

        //build mutalisks
        ConfigurationWithAbstractPlan trainMuta = createOwnConfigurationWithAbstractPlanToTrainFromTemplate(
            DesiresKeys.BOOST_AIR, AUnitTypeWrapper.MUTALISK_TYPE, AgentTypes.UNIT_ORDER_MANAGER,
            FeatureContainerHeaders.BOOST_AIR);
        type.addConfiguration(DesiresKeys.BOOST_AIR, trainMuta, true);
        ConfigurationWithSharedDesire trainMutaShared = createConfigurationWithSharedDesireToTrainFromTemplate(
            DesiresKeys.BOOST_AIR, AUnitTypeWrapper.MUTALISK_TYPE);
        type.addConfiguration(DesiresKeys.BOOST_AIR, DesiresKeys.BOOST_AIR, trainMutaShared);

        //TODO - abstract plan to build units based on position requests?
      })
      .desiresWithAbstractIntention(Stream
          .of(DesiresKeys.BOOST_GROUND_MELEE,
              DesiresKeys.BOOST_GROUND_RANGED,
              DesiresKeys.BOOST_AIR)
          .collect(Collectors.toSet()))
      .build();
}
