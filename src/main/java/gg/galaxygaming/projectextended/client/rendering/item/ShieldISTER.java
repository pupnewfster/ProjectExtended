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
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.BannerTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;

public class ShieldISTER extends ItemStackTileEntityRenderer {

    public static final RenderMaterial DM_SHIELD = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, ProjectExtended.rl("entity/dark_matter_shield"));
    public static final RenderMaterial RM_SHIELD = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, ProjectExtended.rl("entity/red_matter_shield"));

    @Override
    public void func_239207_a_(@Nonnull ItemStack stack, @Nonnull TransformType transformType, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer,
          int light, int overlayLight) {
        matrix.push();
        matrix.scale(1, -1, -1);
        RenderMaterial material;
        if (stack.getItem() instanceof PEShield && ((PEShield) stack.getItem()).getMatterTier() > 0) {
            material = RM_SHIELD;
        } else {
            //Fallback to dark matter shield
            material = DM_SHIELD;
        }
        IVertexBuilder buffer = material.getSprite().wrapBuffer(ItemRenderer.getEntityGlintVertexBuilder(renderer, modelShield.getRenderType(material.getAtlasLocation()),
              true, stack.hasEffect()));
        if (stack.getChildTag("BlockEntityTag") != null) {
            modelShield.func_228294_b_().render(matrix, buffer, light, overlayLight, 1, 1, 1, 1);
            List<Pair<BannerPattern, DyeColor>> list = BannerTileEntity.getPatternColorData(ShieldItem.getColor(stack), BannerTileEntity.getPatternData(stack));
            BannerTileEntityRenderer.func_230180_a_(matrix, renderer, light, overlayLight, modelShield.func_228293_a_(), material, false, list);
        } else {
            modelShield.render(matrix, buffer, light, overlayLight, 1, 1, 1, 1);
        }
        matrix.pop();
    }
}