package gg.galaxygaming.projectextended.client;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.client.gui.AlchemicalBarrelScreen;
import gg.galaxygaming.projectextended.client.rendering.PETridentRenderer;
import gg.galaxygaming.projectextended.client.rendering.item.ShieldISTER;
import gg.galaxygaming.projectextended.client.rendering.item.TridentISTER;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedContainerTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedEntityTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedItems;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = ProjectExtended.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemPropertyFunction override = (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
            addPropertyOverrides(ProjectExtended.rl("blocking"), override, ProjectExtendedItems.DARK_MATTER_SHIELD, ProjectExtendedItems.RED_MATTER_SHIELD);
            addPropertyOverrides(ProjectExtended.rl("throwing"), override, ProjectExtendedItems.DARK_MATTER_TRIDENT, ProjectExtendedItems.RED_MATTER_TRIDENT);
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ProjectExtendedEntityTypes.PE_TRIDENT.get(), PETridentRenderer::new);
    }

    @SubscribeEvent
    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ShieldISTER.RENDERER);
        event.registerReloadListener(TridentISTER.RENDERER);
    }

    private static void addPropertyOverrides(ResourceLocation override, ItemPropertyFunction propertyGetter, ItemLike... items) {
        for (ItemLike item : items) {
            ItemProperties.register(item.asItem(), override, propertyGetter);
        }
    }

    @SubscribeEvent
    public static void registerContainerTypes(RegisterEvent event) {
        event.register(Registries.MENU, helper -> MenuScreens.register(ProjectExtendedContainerTypes.ALCHEMICAL_BARREL_CONTAINER.get(), AlchemicalBarrelScreen::new));
    }
}