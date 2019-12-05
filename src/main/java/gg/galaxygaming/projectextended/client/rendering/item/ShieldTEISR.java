package gg.galaxygaming.projectextended.client.rendering.item;

import com.mojang.blaze3d.platform.GlStateManager;
import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.items.PEShield;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.ResourceLocation;

public class ShieldTEISR extends ItemStackTileEntityRenderer {

    public static final BannerTextures.Cache DM_SHIELD_DESIGNS = new BannerTextures.Cache("dark_matter_shield_",
          new ResourceLocation(ProjectExtended.MODID, "textures/entity/dark_matter_shield_base.png"), "textures/entity/shield/");
    public static final BannerTextures.Cache RM_SHIELD_DESIGNS = new BannerTextures.Cache("red_matter_shield_",
          new ResourceLocation(ProjectExtended.MODID, "textures/entity/red_matter_shield_base.png"), "textures/entity/shield/");

    public static final ResourceLocation DM_SHIELD = new ResourceLocation(ProjectExtended.MODID, "textures/entity/dark_matter_shield_base_nopattern.png");
    public static final ResourceLocation RM_SHIELD = new ResourceLocation(ProjectExtended.MODID, "textures/entity/red_matter_shield_base_nopattern.png");

    @Override
    public void renderByItem(@Nonnull ItemStack stack) {
        if (stack.getChildTag("BlockEntityTag") != null) {
            banner.loadFromItemStack(stack, ShieldItem.getColor(stack));
            Minecraft.getInstance().getTextureManager().bindTexture(getTextureCache(stack).getResourceLocation(banner.getPatternResourceLocation(), banner.getPatternList(), banner.getColorList()));
        } else {
            Minecraft.getInstance().getTextureManager().bindTexture(getTexture(stack));
        }
        GlStateManager.pushMatrix();
        GlStateManager.scalef(1.0F, -1.0F, -1.0F);
        modelShield.render();
        if (stack.hasEffect()) {
            renderEffect(modelShield::render);
        }
        GlStateManager.popMatrix();
    }

    private BannerTextures.Cache getTextureCache(ItemStack stack) {
        if (stack.getItem() instanceof PEShield && ((PEShield) stack.getItem()).getMatterTier() > 0) {
            return RM_SHIELD_DESIGNS;
        }
        //Fallback to dark matter shield cache
        return DM_SHIELD_DESIGNS;
    }

    private ResourceLocation getTexture(ItemStack stack) {
        if (stack.getItem() instanceof PEShield && ((PEShield) stack.getItem()).getMatterTier() > 0) {
            return RM_SHIELD;
        }
        //Fallback to dark matter shield
        return DM_SHIELD;
    }
}