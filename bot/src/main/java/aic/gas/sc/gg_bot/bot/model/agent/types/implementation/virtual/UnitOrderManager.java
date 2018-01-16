package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.virtual;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BOOSTING_AIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BOOSTING_GROUND_MELEE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BOOSTING_GROUND_RANGED;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.BOOST_AIR;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.BOOST_GROUND_MELEE;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.BOOST_GROUND_RANGED;
import static aic.gas.sc.gg_bot.bot.model.agent.types.implementation.AgentTypeUtils.createConfigurationWithSharedDesireToTrainFromTemplate;
import static aic.gas.sc.gg_bot.bot.model.agent.types.implementation.AgentTypeUtils.createOwnConfigurationWithAbstractPlanToTrainFromTemplate;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentType;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithAbstractPlan;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithSharedDesire;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UnitOrderManager {

  public static final AgentType UNIT_ORDER_MANAGER = AgentType.builder()
      .agentTypeID(AgentTypes.UNIT_ORDER_MANAGER)
      .initializationStrategy(type -> {

        //build zerglings
        ConfigurationWithAbstractPlan trainZergling = createOwnConfigurationWithAbstractPlanToTrainFromTemplate(
            BOOST_GROUND_MELEE, AUnitTypeWrapper.ZERGLING_TYPE, AgentTypes.UNIT_ORDER_MANAGER,
            BOOSTING_GROUND_MELEE);
        type.addConfiguration(BOOST_GROUND_MELEE, trainZergling, true);
        ConfigurationWithSharedDesire trainZerglingShared = createConfigurationWithSharedDesireToTrainFromTemplate(
            BOOST_GROUND_MELEE, AUnitTypeWrapper.ZERGLING_TYPE);
        type.addConfiguration(BOOST_GROUND_MELEE, BOOST_GROUND_MELEE, trainZerglingShared);

        //build hydras
        ConfigurationWithAbstractPlan trainHydra = createOwnConfigurationWithAbstractPlanToTrainFromTemplate(
            BOOST_GROUND_RANGED, AUnitTypeWrapper.HYDRALISK_TYPE, AgentTypes.UNIT_ORDER_MANAGER,
            BOOSTING_GROUND_RANGED);
        type.addConfiguration(BOOST_GROUND_RANGED, trainHydra, true);
        ConfigurationWithSharedDesire trainHydraShared = createConfigurationWithSharedDesireToTrainFromTemplate(
            BOOST_GROUND_RANGED, AUnitTypeWrapper.HYDRALISK_TYPE);
        type.addConfiguration(BOOST_GROUND_RANGED, BOOST_GROUND_RANGED, trainHydraShared);

        //build mutalisks
        ConfigurationWithAbstractPlan trainMuta = createOwnConfigurationWithAbstractPlanToTrainFromTemplate(
            BOOST_AIR, AUnitTypeWrapper.HYDRALISK_TYPE, AgentTypes.UNIT_ORDER_MANAGER,
            BOOSTING_AIR);
        type.addConfiguration(BOOST_AIR, trainMuta, true);
        ConfigurationWithSharedDesire trainMutaShared = createConfigurationWithSharedDesireToTrainFromTemplate(
            BOOST_AIR, AUnitTypeWrapper.HYDRALISK_TYPE);
        type.addConfiguration(BOOST_AIR, BOOST_AIR, trainMutaShared);

        //TODO - abstract plan to build units based on position requests?
      })
      .desiresWithAbstractIntention(Stream.of(BOOST_GROUND_MELEE, BOOST_GROUND_RANGED, BOOST_AIR)
          .collect(Collectors.toSet()))
      .build();
}
