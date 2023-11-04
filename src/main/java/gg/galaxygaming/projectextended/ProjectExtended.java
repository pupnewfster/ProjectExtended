package gg.galaxygaming.projectextended;

import gg.galaxygaming.projectextended.common.BlacklistType;
import gg.galaxygaming.projectextended.common.ProjectExtendedTags;
import gg.galaxygaming.projectextended.common.config.ProjectExtendedConfig;
import gg.galaxygaming.projectextended.common.integration.ProjectExtendedHooks;
import gg.galaxygaming.projectextended.common.integration.gamestages.BlacklistReader;
import gg.galaxygaming.projectextended.common.integration.gamestages.EMCGameStageHelper;
import gg.galaxygaming.projectextended.common.network.PacketHandler;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlockEntityTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlocks;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedContainerTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedEntityTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedItems;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedRecipeSerializers;
import moze_intel.projecte.api.event.PlayerAttemptCondenserSetEvent;
import moze_intel.projecte.api.event.PlayerAttemptLearnEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Mod(ProjectExtended.MODID)
public class ProjectExtended {

    public static final String MODID = "projectextended";
    public static final String MOD_NAME = "ProjectExtended";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static ProjectExtended INSTANCE;
    public static ModContainer MOD_CONTAINER;

    @Nullable
    private ResourceManager resourceManager;

    public ProjectExtended() {
        INSTANCE = this;
        MOD_CONTAINER = ModLoadingContext.get().getActiveContainer();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ProjectExtendedItems.ITEMS.register(modEventBus);
        ProjectExtendedBlocks.BLOCKS.register(modEventBus);
        ProjectExtendedBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        ProjectExtendedContainerTypes.CONTAINER_TYPES.register(modEventBus);
        ProjectExtendedEntityTypes.ENTITY_TYPES.register(modEventBus);
        ProjectExtendedRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(this::addReloadListeners);
        //Note: High priority so that ProjectE gets the event after us and clears out any NSSTags we make as we don't need
        // conversions defined for them
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::tagsUpdated);
        MinecraftForge.EVENT_BUS.addListener(this::playerConnect);
        MinecraftForge.EVENT_BUS.addListener(this::serverQuit);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onAttemptCondenserSet);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onAttemptLearnEvent);

        ProjectExtendedConfig.register();
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MODID, path);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        //Ensure our tags are all initialized
        event.enqueueWork(() -> {
            ProjectExtendedTags.init();
            PacketHandler.register();
            ProjectExtendedHooks.hookCommon();
        });
    }

    private void tagsUpdated(TagsUpdatedEvent event) {
        if (resourceManager != null && ProjectExtendedHooks.gameStagesLoaded) {
            BlacklistReader.readDataPacks(resourceManager);
            PacketHandler.sendToAllNonLocal(EMCGameStageHelper.syncPacket());
            resourceManager = null;
        }
    }

    private void addReloadListeners(AddReloadListenerEvent event) {
        if (ProjectExtendedHooks.gameStagesLoaded) {
            event.addListener((ResourceManagerReloadListener) rm -> resourceManager = rm);
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

    private void playerConnect(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && ProjectExtendedHooks.gameStagesLoaded) {
            PacketHandler.sendNonLocal(EMCGameStageHelper.syncPacket(), player);
        }
    }

    private void serverQuit(ServerStoppedEvent event) {
        if (ProjectExtendedHooks.gameStagesLoaded) {
            EMCGameStageHelper.clearBlacklist();
        }
    }
}