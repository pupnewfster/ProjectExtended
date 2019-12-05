package gg.galaxygaming.projectextended;

import gg.galaxygaming.projectextended.client.ClientRegistration;
import gg.galaxygaming.projectextended.common.entity.ProjectExtendedEntityTypes;
import gg.galaxygaming.projectextended.common.items.ProjectExtendedItems;
import gg.galaxygaming.projectextended.common.recipe.ProjectExtendedRecipeSerializers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
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

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.register(ClientRegistration.class);
        });
    }
}