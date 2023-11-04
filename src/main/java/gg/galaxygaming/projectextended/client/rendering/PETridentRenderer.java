package gg.galaxygaming.projectextended.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import gg.galaxygaming.projectextended.client.rendering.item.TridentISTER;
import gg.galaxygaming.projectextended.common.entity.PETridentEntity;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

//Most of this is copied from Vanilla's TridentRenderer, but for our trident as it does not extent the vanilla trident entity
public class PETridentRenderer extends EntityRenderer<PETridentEntity> {

    private final TridentModel tridentModel;

    public PETridentRenderer(EntityRendererProvider.Context context) {
        super(context);
        tridentModel = new TridentModel(context.bakeLayer(ModelLayers.TRIDENT));
    }

    @Override
    public void render(PETridentEntity entity, float entityYaw, float partialTicks, PoseStack matrix, @NotNull MultiBufferSource renderer, int light) {
        matrix.pushPose();
        matrix.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        matrix.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 90.0F));
        VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(renderer, tridentModel.renderType(getTextureLocation(entity)), false, entity.isFoil());
        tridentModel.renderToBuffer(matrix, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrix.popPose();
        super.render(entity, entityYaw, partialTicks, matrix, renderer, light);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull PETridentEntity entity) {
        return entity.getMatterTier() == 0 ? TridentISTER.DM_TRIDENT : TridentISTER.RM_TRIDENT;
    }
}