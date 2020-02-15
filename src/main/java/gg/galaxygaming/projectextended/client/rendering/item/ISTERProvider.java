package gg.galaxygaming.projectextended.client.rendering.item;

import java.util.concurrent.Callable;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;

//This class is used to prevent class loading issues on the server without having to use OnlyIn hacks
public class ISTERProvider {

    public static Callable<ItemStackTileEntityRenderer> shield() {
        return ShieldTEISR::new;
    }

    public static Callable<ItemStackTileEntityRenderer> trident() {
        return TridentTEISR::new;
    }
}