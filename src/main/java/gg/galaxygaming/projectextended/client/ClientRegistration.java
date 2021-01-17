package gg.galaxygaming.projectextended.client;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.client.rendering.PETridentRenderer;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedEntityTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedItems;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ProjectExtended.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ProjectExtendedEntityTypes.PE_TRIDENT.get(), PETridentRenderer::new);
        event.enqueueWork(() -> {
            IItemPropertyGetter override = (stack, world, entity) -> entity != null && entity.isHandActive() && entity.getActiveItemStack() == stack ? 1.0F : 0.0F;
            addPropertyOverrides(ProjectExtended.rl("blocking"), override, ProjectExtendedItems.DARK_MATTER_SHIELD, ProjectExtendedItems.RED_MATTER_SHIELD);
            addPropertyOverrides(ProjectExtended.rl("throwing"), override, ProjectExtendedItems.DARK_MATTER_TRIDENT, ProjectExtendedItems.RED_MATTER_TRIDENT);
        });
    }

    private static void addPropertyOverrides(ResourceLocation override, IItemPropertyGetter propertyGetter, IItemProvider... items) {
        for (IItemProvider item : items) {
            ItemModelsProperties.registerProperty(item.asItem(), override, propertyGetter);
        }
    }

    @SubscribeEvent
    public static void onStitch(TextureStitchEvent.Pre event) {
        if (event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)) {
            event.addSprite(ProjectExtended.rl("entity/dark_matter_shield"));
            event.addSprite(ProjectExtended.rl("entity/red_matter_shield"));
        }
    }
}