package dev.freimer.projectextended.client.rendering;

import dev.freimer.projectextended.client.rendering.item.TridentISTER;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.jetbrains.annotations.NotNull;

public class PETridentRenderer extends ThrownTridentRenderer {

    public PETridentRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull ThrownTrident entity) {
        return TridentISTER.getTexture(entity.getWeaponItem());
    }
}