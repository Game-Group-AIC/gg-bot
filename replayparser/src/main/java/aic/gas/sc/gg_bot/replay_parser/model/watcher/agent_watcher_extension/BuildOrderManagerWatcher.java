package aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_extension;


import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.DesireKeys;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders;
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
                () -> new FeatureContainer(FeatureContainerHeaders.ENABLE_GROUND_MELEE),
                DesireKeys.ENABLE_GROUND_MELEE,
                AUnitTypeWrapper.SPAWNING_POOL_TYPE),

            //UPGRADE_TO_LAIR
            () -> new AbstractAgentWatcherUtils.AbstractPlanWatcher(
                () -> new FeatureContainer(FeatureContainerHeaders.UPGRADE_TO_LAIR),
                DesireKeys.UPGRADE_TO_LAIR,
                AUnitTypeWrapper.LAIR_TYPE),

            //ENABLE_AIR
            () -> new AbstractAgentWatcherUtils.AbstractPlanWatcher(
                () -> new FeatureContainer(FeatureContainerHeaders.ENABLE_AIR),
                DesireKeys.ENABLE_AIR,
                AUnitTypeWrapper.SPIRE_TYPE),

            //ENABLE_GROUND_RANGED
            () -> new AbstractAgentWatcherUtils.AbstractPlanWatcher(
                () -> new FeatureContainer(FeatureContainerHeaders.ENABLE_GROUND_RANGED),
                DesireKeys.ENABLE_GROUND_RANGED,
                AUnitTypeWrapper.HYDRALISK_DEN_TYPE),

            //ENABLE_STATIC_ANTI_AIR
            () -> new AbstractAgentWatcherUtils.AbstractPlanWatcher(
                () -> new FeatureContainer(FeatureContainerHeaders.ENABLE_STATIC_ANTI_AIR),
                DesireKeys.ENABLE_STATIC_ANTI_AIR,
                AUnitTypeWrapper.EVOLUTION_CHAMBER_TYPE)
        }))
        .build()
    );
  }
}
