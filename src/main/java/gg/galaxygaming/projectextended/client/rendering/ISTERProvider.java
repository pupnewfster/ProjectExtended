package gg.galaxygaming.projectextended.client.rendering;

import gg.galaxygaming.projectextended.client.rendering.item.ShieldISTER;
import gg.galaxygaming.projectextended.client.rendering.item.TridentISTER;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.IItemRenderProperties;

//This class is used to prevent class loading issues on the server without having to use OnlyIn hacks
public class ISTERProvider {

    public static IItemRenderProperties shield() {
        return new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return ShieldISTER.RENDERER;
            }
        };
    }

    public static IItemRenderProperties trident() {
        return new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return TridentISTER.RENDERER;
            }
        };
    }
}