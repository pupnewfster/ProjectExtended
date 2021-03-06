package gg.galaxygaming.projectextended;

import gg.galaxygaming.projectextended.common.ProjectExtendedTags;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedEntityTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedItems;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedRecipeSerializers;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.event.PlayerAttemptCondenserSetEvent;
import moze_intel.projecte.api.event.PlayerAttemptLearnEvent;
import net.minecraft.util.ResourceLocation;
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
        ProjectExtendedEntityTypes.ENTITY_TYPES.register(modEventBus);
        ProjectExtendedRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onAttemptCondenserSet);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onAttemptLearnEvent);
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MODID, path);
    }

    public void onAttemptCondenserSet(PlayerAttemptCondenserSetEvent event) {
        if (event.getReducedInfo().getItem().isIn(ProjectExtendedTags.Items.BLACKLIST_CONDENSER)) {
            event.setCanceled(true);
        }
    }

    public void onAttemptLearnEvent(PlayerAttemptLearnEvent event) {
        if (event.getReducedInfo().getItem().isIn(ProjectExtendedTags.Items.BLACKLIST_LEARNING)) {
            event.setCanceled(true);
        }
    }
}