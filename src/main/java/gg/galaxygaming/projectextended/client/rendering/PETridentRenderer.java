package gg.galaxygaming.projectextended.client.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import gg.galaxygaming.projectextended.client.rendering.item.TridentISTER;
import gg.galaxygaming.projectextended.common.entity.PETridentEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.TridentModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

//Most of this is copied from Vanilla's TridentRenderer, but for our trident as it does not extent the vanilla trident entity
public class PETridentRenderer extends EntityRenderer<PETridentEntity> {

    private final TridentModel tridentModel = new TridentModel();

    public PETridentRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(PETridentEntity entity, float entityYaw, float partialTicks, MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light) {
        matrix.push();
        matrix.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entity.prevRotationYaw, entity.rotationYaw) - 90.0F));
        matrix.rotate(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entity.prevRotationPitch, entity.rotationPitch) + 90.0F));
        IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(renderer, tridentModel.getRenderType(getEntityTexture(entity)), false, entity.hasEffect());
        tridentModel.render(matrix, ivertexbuilder, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrix.pop();
        super.render(entity, entityYaw, partialTicks, matrix, renderer, light);
    }

    @Nonnull
    @Override
    public ResourceLocation getEntityTexture(@Nonnull PETridentEntity entity) {
        return entity.getMatterTier() == 0 ? TridentISTER.DM_TRIDENT : TridentISTER.RM_TRIDENT;
    }
}