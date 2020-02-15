package gg.galaxygaming.projectextended.client.rendering.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.items.PETrident;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class TridentTEISR extends ItemStackTileEntityRenderer {

    public static final ResourceLocation DM_TRIDENT = new ResourceLocation(ProjectExtended.MODID, "textures/entity/dark_matter_trident.png");
    public static final ResourceLocation RM_TRIDENT = new ResourceLocation(ProjectExtended.MODID, "textures/entity/red_matter_trident.png");

    @Override
    public void render(@Nonnull ItemStack stack, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light, int overlayLight) {
        //TODO: FIXME, when "throwing" it makes gui stop looking like it should
        matrix.push();
        matrix.scale(1.0F, -1.0F, -1.0F);
        IVertexBuilder ivertexbuilder1 = ItemRenderer.getBuffer(renderer, trident.getRenderType(getTexture(stack)), false, stack.hasEffect());
        trident.render(matrix, ivertexbuilder1, light, overlayLight, 1.0F, 1.0F, 1.0F, 1.0F);
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