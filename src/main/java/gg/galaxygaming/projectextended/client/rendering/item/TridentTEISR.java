package gg.galaxygaming.projectextended.client.rendering.item;

import com.mojang.blaze3d.platform.GlStateManager;
import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.items.PETrident;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class TridentTEISR extends ItemStackTileEntityRenderer {

    public static final ResourceLocation DM_TRIDENT = new ResourceLocation(ProjectExtended.MODID, "textures/entity/dark_matter_trident.png");
    public static final ResourceLocation RM_TRIDENT = new ResourceLocation(ProjectExtended.MODID, "textures/entity/red_matter_trident.png");

    @Override
    public void renderByItem(@Nonnull ItemStack stack) {
        //TODO: FIXME, when "throwing" it makes gui stop looking like it should
        Minecraft.getInstance().getTextureManager().bindTexture(getTexture(stack));
        GlStateManager.pushMatrix();
        GlStateManager.scalef(1.0F, -1.0F, -1.0F);
        trident.renderer();
        if (stack.hasEffect()) {
            //Unused
            renderEffect(trident::renderer);
        }
        GlStateManager.popMatrix();
    }

    private ResourceLocation getTexture(ItemStack stack) {
        if (stack.getItem() instanceof PETrident && ((PETrident) stack.getItem()).getMatterTier() > 0) {
            return RM_TRIDENT;
        }
        //Fallback to dark matter trident
        return DM_TRIDENT;
    }
}