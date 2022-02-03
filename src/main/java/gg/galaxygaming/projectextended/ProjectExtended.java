package gg.galaxygaming.projectextended;

import gg.galaxygaming.projectextended.common.ProjectExtendedTags;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlockEntityTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlocks;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedContainerTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedEntityTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedItems;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedRecipeSerializers;
import moze_intel.projecte.api.event.PlayerAttemptCondenserSetEvent;
import moze_intel.projecte.api.event.PlayerAttemptLearnEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ProjectExtended.MODID)
public class ProjectExtended {

    public static final String MODID = "projectextended";
    public static final String MOD_NAME = "ProjectExtended";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static ProjectExtended INSTANCE;

    public ProjectExtended() {
        INSTANCE = this;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ProjectExtendedItems.ITEMS.register(modEventBus);
        ProjectExtendedBlocks.BLOCKS.register(modEventBus);
        ProjectExtendedBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        ProjectExtendedContainerTypes.CONTAINER_TYPES.register(modEventBus);
        ProjectExtendedEntityTypes.ENTITY_TYPES.register(modEventBus);
        ProjectExtendedRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onAttemptCondenserSet);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onAttemptLearnEvent);
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MODID, path);
    }

    public void onAttemptCondenserSet(PlayerAttemptCondenserSetEvent event) {
        if (ProjectExtendedTags.Items.BLACKLIST_CONDENSER.contains(event.getReducedInfo().getItem())) {
            event.setCanceled(true);
        }
    }

    public void onAttemptLearnEvent(PlayerAttemptLearnEvent event) {
        if (ProjectExtendedTags.Items.BLACKLIST_LEARNING.contains(event.getReducedInfo().getItem())) {
            event.setCanceled(true);
        }
    }
}