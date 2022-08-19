package gg.galaxygaming.projectextended.client.rendering.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.items.PEShield;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Holder;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.jetbrains.annotations.NotNull;

public class ShieldISTER extends BlockEntityWithoutLevelRenderer {

    public static final Material DM_SHIELD = new Material(TextureAtlas.LOCATION_BLOCKS, ProjectExtended.rl("entity/dark_matter_shield"));
    public static final Material RM_SHIELD = new Material(TextureAtlas.LOCATION_BLOCKS, ProjectExtended.rl("entity/red_matter_shield"));
    public static final ShieldISTER RENDERER = new ShieldISTER(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());

    private final EntityModelSet modelSet;
    private ShieldModel shieldModel;

    public ShieldISTER(BlockEntityRenderDispatcher renderDispatcher, EntityModelSet modelSet) {
        super(renderDispatcher, modelSet);
        this.modelSet = modelSet;
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        this.shieldModel = new ShieldModel(modelSet.bakeLayer(ModelLayers.SHIELD));
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull TransformType transformType, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer,
          int light, int overlayLight) {
        matrix.pushPose();
        matrix.scale(1, -1, -1);
        Material material;
        if (stack.getItem() instanceof PEShield shield && shield.getMatterTier() > 0) {
            material = RM_SHIELD;
        } else {
            //Fallback to dark matter shield
            material = DM_SHIELD;
        }
        VertexConsumer buffer = material.sprite().wrap(ItemRenderer.getFoilBufferDirect(renderer, shieldModel.renderType(material.atlasLocation()),
              true, stack.hasFoil()));
        if (stack.getTagElement("BlockEntityTag") != null) {
            shieldModel.handle().render(matrix, buffer, light, overlayLight, 1, 1, 1, 1);
            List<Pair<Holder<BannerPattern>, DyeColor>> list = BannerBlockEntity.createPatterns(ShieldItem.getColor(stack), BannerBlockEntity.getItemPatterns(stack));
            BannerRenderer.renderPatterns(matrix, renderer, light, overlayLight, shieldModel.plate(), material, false, list);
        } else {
            shieldModel.renderToBuffer(matrix, buffer, light, overlayLight, 1, 1, 1, 1);
        }
        matrix.popPose();
    }
}