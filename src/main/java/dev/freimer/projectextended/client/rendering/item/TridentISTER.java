package dev.freimer.projectextended.client.rendering.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.freimer.projectextended.ProjectExtended;
import dev.freimer.projectextended.common.items.PETrident;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import moze_intel.projecte.gameObjs.EnumMatterType;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TridentISTER extends BlockEntityWithoutLevelRenderer {

    private static final Int2ObjectMap<ResourceLocation> TRIDENT_TEXTURES = Util.make(new Int2ObjectArrayMap<>(2), map -> {
        map.put(EnumMatterType.DARK_MATTER.getMatterTier(), ProjectExtended.rl("textures/entity/dark_matter_trident.png"));
        map.put(EnumMatterType.RED_MATTER.getMatterTier(), ProjectExtended.rl("textures/entity/red_matter_trident.png"));
    });
    public static final TridentISTER RENDERER = new TridentISTER(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());

    private final EntityModelSet modelSet;
    private TridentModel tridentModel;

    private TridentISTER(BlockEntityRenderDispatcher renderDispatcher, EntityModelSet modelSet) {
        super(renderDispatcher, modelSet);
        this.modelSet = modelSet;
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        this.tridentModel = new TridentModel(modelSet.bakeLayer(ModelLayers.TRIDENT));
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer,
          int light, int overlayLight) {
        matrix.pushPose();
        matrix.scale(1, -1, -1);
        VertexConsumer builder = ItemRenderer.getFoilBufferDirect(renderer, tridentModel.renderType(getTexture(stack)), false, stack.hasFoil());
        tridentModel.renderToBuffer(matrix, builder, light, overlayLight);
        matrix.popPose();
    }

    private static ResourceLocation getTexture(ItemStack stack) {
        //Fall back to vanilla's trident texture
        return stack.getItem() instanceof PETrident trident ? getTexture(trident.getMatterTier()) : TridentModel.TEXTURE;
    }

    public static ResourceLocation getTexture(int matterTier) {
        //Fall back to vanilla's trident texture
        return TRIDENT_TEXTURES.getOrDefault(matterTier, TridentModel.TEXTURE);
    }
}