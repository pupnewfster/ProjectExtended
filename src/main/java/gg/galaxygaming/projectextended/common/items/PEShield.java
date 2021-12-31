package gg.galaxygaming.projectextended.common.items;

import gg.galaxygaming.projectextended.client.rendering.ISTERProvider;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.EnumMatterType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.client.IItemRenderProperties;

public class PEShield extends ShieldItem {

    private final EnumMatterType matterType;

    public PEShield(EnumMatterType matterType, Properties props) {
        super(props);
        this.matterType = matterType;
    }

    public int getMatterTier() {
        return matterType.getMatterTier();
    }

    @Override
    public boolean isEnchantable(@Nonnull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return 0;
    }

    @Override
    public boolean isValidRepairItem(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
        //Override the shield allowing planks to repair it as we can't lose durability anyway
        return false;
    }

    @Override
    public void initializeClient(@Nonnull Consumer<IItemRenderProperties> consumer) {
        consumer.accept(ISTERProvider.shield());
    }
}