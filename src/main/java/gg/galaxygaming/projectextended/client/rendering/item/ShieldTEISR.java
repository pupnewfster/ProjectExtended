package gg.galaxygaming.projectextended.client.rendering.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.items.PEShield;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.BannerTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.ResourceLocation;

public class ShieldTEISR extends ItemStackTileEntityRenderer {

    public static final Material DM_SHIELD_BASE = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(ProjectExtended.MODID, "entity/dark_matter_shield_base"));
    public static final Material DM_SHIELD_NO_PATTERN = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(ProjectExtended.MODID, "entity/dark_matter_shield_base_nopattern"));
    public static final Material RM_SHIELD_BASE = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(ProjectExtended.MODID, "entity/red_matter_shield_base"));
    public static final Material RM_SHIELD_NO_PATTERN = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(ProjectExtended.MODID, "entity/red_matter_shield_base_nopattern"));

    @Override
    public void render(@Nonnull ItemStack stack, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light, int overlayLight) {
        boolean hasBanner = stack.getChildTag("BlockEntityTag") != null;
        matrix.push();
        matrix.scale(1.0F, -1.0F, -1.0F);
        Material material = getMaterial(stack, hasBanner);
        IVertexBuilder ivertexbuilder = material.getSprite().wrapBuffer(ItemRenderer.getBuffer(renderer, modelShield.getRenderType(material.getAtlasLocation()), false, stack.hasEffect()));
        modelShield.func_228294_b_().render(matrix, ivertexbuilder, light, overlayLight, 1.0F, 1.0F, 1.0F, 1.0F);
        if (hasBanner) {
            List<Pair<BannerPattern, DyeColor>> list = BannerTileEntity.func_230138_a_(ShieldItem.getColor(stack), BannerTileEntity.func_230139_a_(stack));
            BannerTileEntityRenderer.func_230180_a_(matrix, renderer, light, overlayLight, modelShield.func_228293_a_(), material, false, list);
        } else {
            modelShield.func_228293_a_().render(matrix, ivertexbuilder, light, overlayLight, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        matrix.pop();
    }

    private Material getMaterial(ItemStack stack, boolean hasBanner) {
        if (stack.getItem() instanceof PEShield && ((PEShield) stack.getItem()).getMatterTier() > 0) {
            return hasBanner ? RM_SHIELD_BASE : DM_SHIELD_NO_PATTERN;
        }
        //Fallback to dark matter shield
        return hasBanner ? DM_SHIELD_BASE : RM_SHIELD_NO_PATTERN;
    }
}