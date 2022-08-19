package gg.galaxygaming.projectextended.client.rendering;

import gg.galaxygaming.projectextended.client.rendering.item.ShieldISTER;
import gg.galaxygaming.projectextended.client.rendering.item.TridentISTER;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

//This class is used to prevent class loading issues on the server without having to use OnlyIn hacks
public class ISTERProvider {

    public static IClientItemExtensions shield() {
        return new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ShieldISTER.RENDERER;
            }
        };
    }

    public static IClientItemExtensions trident() {
        return new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return TridentISTER.RENDERER;
            }
        };
    }
}