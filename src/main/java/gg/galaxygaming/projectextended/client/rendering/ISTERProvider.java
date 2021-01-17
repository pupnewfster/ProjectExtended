package gg.galaxygaming.projectextended.client.rendering;

import gg.galaxygaming.projectextended.client.rendering.item.ShieldISTER;
import gg.galaxygaming.projectextended.client.rendering.item.TridentISTER;
import java.util.concurrent.Callable;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;

//This class is used to prevent class loading issues on the server without having to use OnlyIn hacks
public class ISTERProvider {

    public static Callable<ItemStackTileEntityRenderer> shield() {
        return ShieldISTER::new;
    }

    public static Callable<ItemStackTileEntityRenderer> trident() {
        return TridentISTER::new;
    }
}