package gg.galaxygaming.projectextended;

import gg.galaxygaming.projectextended.common.BlacklistType;
import gg.galaxygaming.projectextended.common.config.ProjectExtendedConfig;
import gg.galaxygaming.projectextended.common.integration.ProjectExtendedHooks;
import gg.galaxygaming.projectextended.common.integration.gamestages.BlacklistManager;
import gg.galaxygaming.projectextended.common.network.PacketHandler;
import gg.galaxygaming.projectextended.common.network.to_client.PacketSyncBlacklist;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlockEntityTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlocks;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedContainerTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedCreativeTabs;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedDataComponentTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedEntityTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedItems;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedRecipeSerializers;
import java.util.List;
import moze_intel.projecte.api.event.PlayerAttemptCondenserSetEvent;
import moze_intel.projecte.api.event.PlayerAttemptLearnEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Mod(ProjectExtended.MODID)
public class ProjectExtended {

    public static final String MODID = "projectextended";
    public static final String MOD_NAME = "ProjectExtended";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    private static ProjectExtended INSTANCE;

    private final PacketHandler packetHandler;

    @Nullable
    private ResourceManager resourceManager;

    public ProjectExtended(ModContainer modContainer, IEventBus modEventBus) {
        INSTANCE = this;
        ProjectExtendedItems.ITEMS.register(modEventBus);
        ProjectExtendedBlocks.BLOCKS.register(modEventBus);
        ProjectExtendedBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        ProjectExtendedContainerTypes.CONTAINER_TYPES.register(modEventBus);
        ProjectExtendedCreativeTabs.CREATIVE_TABS.register(modEventBus);
        ProjectExtendedDataComponentTypes.DATA_COMPONENT_TYPES.register(modEventBus);
        ProjectExtendedEntityTypes.ENTITY_TYPES.register(modEventBus);
        ProjectExtendedRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        //Note: High priority so that ProjectE gets the event after us and clears out any NSSTags we make as we don't need
        // conversions defined for them. Technically this doesn't fully matter as projecte acts on datapack sync instead of
        // the reload listener level, but it is still worth doing
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, this::addReloadListeners);
        NeoForge.EVENT_BUS.addListener(this::dataPackSync);
        NeoForge.EVENT_BUS.addListener(this::serverQuit);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onAttemptCondenserSet);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onAttemptLearnEvent);

        ProjectExtendedConfig.register(modContainer);
        modEventBus.addListener(ProjectExtendedConfig::onConfigLoad);

        this.packetHandler = new PacketHandler(modEventBus, modContainer.getModInfo().getVersion());
    }

    public static PacketHandler packetHandler() {
        return INSTANCE.packetHandler;
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        //Ensure our tags are all initialized
        event.enqueueWork(ProjectExtendedHooks::hookCommon);
    }

    private void addReloadListeners(AddReloadListenerEvent event) {
        if (ProjectExtendedHooks.gameStagesLoaded) {
            event.addListener(BlacklistManager.INSTANCE);
        }
    }

    private void onAttemptCondenserSet(PlayerAttemptCondenserSetEvent event) {
        if (BlacklistType.CONDENSER.isBlacklisted(event.getPlayer(), event.getSourceInfo(), event.getReducedInfo())) {
            event.setCanceled(true);
        }
    }

    private void onAttemptLearnEvent(PlayerAttemptLearnEvent event) {
        if (BlacklistType.LEARNING.isBlacklisted(event.getPlayer(), event.getSourceInfo(), event.getReducedInfo())) {
            event.setCanceled(true);
        }
    }

    private void dataPackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            List<ServerPlayer> players = event.getPlayerList().getPlayers();
            if (players.isEmpty() || !ProjectExtendedHooks.gameStagesLoaded) {
                return;
            }
            PacketSyncBlacklist blacklistPacket = BlacklistManager.syncPacket();
            for (ServerPlayer player : players) {
                if (!player.connection.getConnection().isMemoryConnection()) {
                    PacketDistributor.sendToPlayer(player, blacklistPacket);
                }
            }
        } else {
            ServerPlayer player = event.getPlayer();
            if (!player.connection.getConnection().isMemoryConnection()) {
                if (ProjectExtendedHooks.gameStagesLoaded) {
                    PacketDistributor.sendToPlayer(player, BlacklistManager.syncPacket());
                }
            }
        }
    }

    private void serverQuit(ServerStoppedEvent event) {
        if (ProjectExtendedHooks.gameStagesLoaded) {
            BlacklistManager.clearBlacklist();
        }
    }
}