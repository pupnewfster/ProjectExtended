package gg.galaxygaming.projectextended.client.rendering.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.items.PETrident;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class TridentISTER extends ItemStackTileEntityRenderer {

    public static final ResourceLocation DM_TRIDENT = ProjectExtended.rl("textures/entity/dark_matter_trident.png");
    public static final ResourceLocation RM_TRIDENT = ProjectExtended.rl("textures/entity/red_matter_trident.png");

    @Override
    public void func_239207_a_(@Nonnull ItemStack stack, @Nonnull TransformType transformType, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer,
          int light, int overlayLight) {
        matrix.push();
        matrix.scale(1, -1, -1);
        IVertexBuilder builder = ItemRenderer.getEntityGlintVertexBuilder(renderer, trident.getRenderType(getTexture(stack)), false, stack.hasEffect());
        trident.render(matrix, builder, light, overlayLight, 1, 1, 1, 1);
        matrix.pop();
    }

    private ResourceLocation getTexture(ItemStack stack) {
        if (stack.getItem() instanceof PETrident && ((PETrident) stack.getItem()).getMatterTier() > 0) {
            return RM_TRIDENT;
        }
        //Fallback to dark matter trident
        return DM_TRIDENT;
    }
}