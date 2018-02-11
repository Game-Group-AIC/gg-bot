package aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.ENABLE_AIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.ENABLE_GROUND_MELEE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.ENABLE_GROUND_RANGED;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.ENABLE_STATIC_ANTI_AIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys.UPGRADE_TO_LAIR;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_EVOLUTION_CHAMBER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_HYDRALISK_DEN;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_POOL;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.BUILDING_SPIRE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders.UPGRADING_TO_LAIR;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcher;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcherType.PlanWatcherInitializationStrategy;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.FeatureContainer;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_type_extension.BuildOrderManagerWatcherType;
import java.util.Arrays;

/**
 * Implementation of watcher for planing build construction
 */
public class BuildOrderManagerWatcher extends AgentWatcher<BuildOrderManagerWatcherType> {

  public BuildOrderManagerWatcher() {
    super(BuildOrderManagerWatcherType.builder()
        .agentTypeID(AgentTypes.BUILDING_ORDER_MANAGER)
        .planWatchers(Arrays.asList(new PlanWatcherInitializationStrategy[]{

            //ENABLE_GROUND_MELEE
            () -> new AbstractAgentWatcherUtils.AbstractPlanWatcher(
                () -> new FeatureContainer(BUILDING_POOL), ENABLE_GROUND_MELEE,
                AUnitTypeWrapper.SPAWNING_POOL_TYPE),

            //UPGRADE_TO_LAIR
            () -> new AbstractAgentWatcherUtils.AbstractPlanWatcher(
                () -> new FeatureContainer(UPGRADING_TO_LAIR), UPGRADE_TO_LAIR,
                AUnitTypeWrapper.LAIR_TYPE),

            //ENABLE_AIR
            () -> new AbstractAgentWatcherUtils.AbstractPlanWatcher(
                () -> new FeatureContainer(BUILDING_SPIRE), ENABLE_AIR,
                AUnitTypeWrapper.SPIRE_TYPE),

            //ENABLE_GROUND_RANGED
            () -> new AbstractAgentWatcherUtils.AbstractPlanWatcher(
                () -> new FeatureContainer(BUILDING_HYDRALISK_DEN), ENABLE_GROUND_RANGED,
                AUnitTypeWrapper.HYDRALISK_DEN_TYPE),

            //ENABLE_STATIC_ANTI_AIR
            () -> new AbstractAgentWatcherUtils.AbstractPlanWatcher(
                () -> new FeatureContainer(BUILDING_EVOLUTION_CHAMBER), ENABLE_STATIC_ANTI_AIR,
                AUnitTypeWrapper.EVOLUTION_CHAMBER_TYPE)
        }))
        .build()
    );
  }
}
