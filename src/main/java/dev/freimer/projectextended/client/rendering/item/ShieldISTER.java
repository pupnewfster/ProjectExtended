package dev.freimer.projectextended.client.rendering.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.freimer.projectextended.ProjectExtended;
import dev.freimer.projectextended.common.items.PEShield;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.jetbrains.annotations.NotNull;

public class ShieldISTER extends BlockEntityWithoutLevelRenderer {

    public static final Material DM_SHIELD = new Material(Sheets.SHIELD_SHEET, ProjectExtended.rl("entity/dark_matter_shield"));
    public static final Material RM_SHIELD = new Material(Sheets.SHIELD_SHEET, ProjectExtended.rl("entity/red_matter_shield"));
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
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer,
          int light, int overlayLight) {
        BannerPatternLayers patternLayers = stack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
        DyeColor dyeColor = stack.get(DataComponents.BASE_COLOR);

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
        shieldModel.handle().render(matrix, buffer, light, overlayLight);
        if (!patternLayers.layers().isEmpty() || dyeColor != null) {
            BannerRenderer.renderPatterns(matrix, renderer, light, overlayLight, shieldModel.plate(), material, false,
                  Objects.requireNonNullElse(dyeColor, DyeColor.WHITE), patternLayers, stack.hasFoil());
        } else {
            shieldModel.plate().render(matrix, buffer, light, overlayLight);
        }
        matrix.popPose();
    }
}