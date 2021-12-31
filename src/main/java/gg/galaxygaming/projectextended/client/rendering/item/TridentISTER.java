package gg.galaxygaming.projectextended.client.rendering.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.items.PETrident;
import javax.annotation.Nonnull;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class TridentISTER extends BlockEntityWithoutLevelRenderer {

    public static final ResourceLocation DM_TRIDENT = ProjectExtended.rl("textures/entity/dark_matter_trident.png");
    public static final ResourceLocation RM_TRIDENT = ProjectExtended.rl("textures/entity/red_matter_trident.png");

    public TridentISTER(BlockEntityRenderDispatcher renderDispatcher, EntityModelSet modelSet) {
        super(renderDispatcher, modelSet);
    }

    @Override
    public void renderByItem(@Nonnull ItemStack stack, @Nonnull TransformType transformType, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource renderer,
          int light, int overlayLight) {
        matrix.pushPose();
        matrix.scale(1, -1, -1);
        VertexConsumer builder = ItemRenderer.getFoilBufferDirect(renderer, tridentModel.renderType(getTexture(stack)), false, stack.hasFoil());
        tridentModel.renderToBuffer(matrix, builder, light, overlayLight, 1, 1, 1, 1);
        matrix.popPose();
    }

    private ResourceLocation getTexture(ItemStack stack) {
        if (stack.getItem() instanceof PETrident trident && trident.getMatterTier() > 0) {
            return RM_TRIDENT;
        }
        //Fallback to dark matter trident
        return DM_TRIDENT;
    }
}