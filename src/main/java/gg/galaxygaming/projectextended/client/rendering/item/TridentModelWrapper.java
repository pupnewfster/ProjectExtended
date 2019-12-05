package gg.galaxygaming.projectextended.client.rendering.item;

import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.IModelData;
import org.apache.commons.lang3.tuple.Pair;

public class TridentModelWrapper implements IBakedModel {

    private final IBakedModel guiModel;
    private final IBakedModel fullModel;

    private TransformType transform = TransformType.NONE;

    public TridentModelWrapper(IBakedModel guiModel, IBakedModel fullModel) {
        this.guiModel = guiModel;
        this.fullModel = fullModel;
    }

    @Override
    public IBakedModel getBakedModel() {
        return transform == TransformType.GUI ? guiModel : fullModel;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        return getBakedModel().getQuads(state, side, rand, extraData);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
        return getBakedModel().getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion(BlockState state) {
        return getBakedModel().isAmbientOcclusion(state);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return getBakedModel().isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        //This has to be false or there is a weird minor GL leak when rendering the gui's model
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return getBakedModel().isBuiltInRenderer();
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
        return getBakedModel().getParticleTexture(data);
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getParticleTexture() {
        return getBakedModel().getParticleTexture();
    }

    @Nonnull
    @Deprecated
    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return getBakedModel().getItemCameraTransforms();
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IEnviromentBlockReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        return getBakedModel().getModelData(world, pos, state, tileData);
    }

    @Nonnull
    @Override
    public ItemOverrideList getOverrides() {
        return getBakedModel().getOverrides();
    }

    @Nonnull
    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(@Nonnull TransformType type) {
        transform = type;
        return ForgeHooksClient.handlePerspective(getBakedModel(), type);
    }
}