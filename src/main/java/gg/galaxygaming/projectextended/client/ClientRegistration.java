package gg.galaxygaming.projectextended.client;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.client.rendering.PETridentRenderer;
import gg.galaxygaming.projectextended.client.rendering.item.TridentModelWrapper;
import gg.galaxygaming.projectextended.common.entity.ProjectExtendedEntityTypes;
import java.util.Map;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientRegistration {

    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ProjectExtendedEntityTypes.PE_TRIDENT.get(), PETridentRenderer::new);
    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        ModelLoader.addSpecialModel(getInventoryMRL("dark_matter_trident_throwing"));
        ModelLoader.addSpecialModel(getInventoryMRL("red_matter_trident_throwing"));
        ModelLoader.addSpecialModel(getInventoryMRL("dark_matter_trident_in_hand"));
        ModelLoader.addSpecialModel(getInventoryMRL("red_matter_trident_in_hand"));

        ModelLoader.addSpecialModel(getInventoryMRL("dark_matter_shield_blocking"));
        ModelLoader.addSpecialModel(getInventoryMRL("red_matter_shield_blocking"));
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
        wrapTridentModel(modelRegistry, "dark_matter_trident");
        wrapTridentModel(modelRegistry, "red_matter_trident");
    }

    private static void wrapTridentModel(Map<ResourceLocation, IBakedModel> modelRegistry, String trident) {
        ModelResourceLocation tridentGui = getInventoryMRL(trident);
        ModelResourceLocation tridentThrowing = getInventoryMRL(trident + "_throwing");
        ModelResourceLocation tridentHand = getInventoryMRL(trident + "_in_hand");
        IBakedModel guiModel = modelRegistry.get(tridentGui);
        IBakedModel throwingModel = modelRegistry.get(tridentThrowing);
        IBakedModel handModel = modelRegistry.get(tridentHand);

        //TODO: We need to overwrite the overrides thing for handModel so that it points to the correct throwing model
        modelRegistry.put(tridentThrowing, new TridentModelWrapper(guiModel, throwingModel));
        modelRegistry.put(tridentGui, new TridentModelWrapper(guiModel, handModel));
    }

    private static ModelResourceLocation getInventoryMRL(String type) {
        return new ModelResourceLocation(new ResourceLocation(ProjectExtended.MODID, type), "inventory");
    }
}