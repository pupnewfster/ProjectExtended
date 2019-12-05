package gg.galaxygaming.projectextended.common.items;

import gg.galaxygaming.projectextended.client.rendering.item.ShieldTEISR;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.EnumMatterType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PEShield extends ShieldItem {

    private final EnumMatterType matterType;

    public PEShield(EnumMatterType matterType, Properties props) {
        super(props.setTEISR(PEShield::getTEISR));
        this.matterType = matterType;
    }

    @OnlyIn(Dist.CLIENT)
    private static Callable<ItemStackTileEntityRenderer> getTEISR() {
        return ShieldTEISR::new;
    }

    public int getMatterTier() {
        return matterType.getMatterTier();
    }

    @Override
    public boolean isBookEnchantable(@Nonnull ItemStack stack, @Nonnull ItemStack book) {
        return false;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return 0;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        //Override the shield allowing planks to repair it as we can't lose durability anyways
        return false;
    }

    @Override
    public boolean isShield(ItemStack stack, @Nullable LivingEntity entity) {
        //Has to override this because default impl in IForgeItem checks for exact equality with the shield item instead of instanceof
        return true;
    }
}