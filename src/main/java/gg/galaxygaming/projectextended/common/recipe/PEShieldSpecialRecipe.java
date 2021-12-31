package gg.galaxygaming.projectextended.common.recipe;

import gg.galaxygaming.projectextended.common.items.PEShield;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedRecipeSerializers;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class PEShieldSpecialRecipe extends CustomRecipe {

    public PEShieldSpecialRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingContainer inv, @Nonnull Level world) {
        ItemStack shieldStack = ItemStack.EMPTY;
        ItemStack bannerStack = ItemStack.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stackInSlot = inv.getItem(i);
            if (!stackInSlot.isEmpty()) {
                if (stackInSlot.getItem() instanceof BannerItem) {
                    if (!bannerStack.isEmpty()) {
                        return false;
                    }
                    bannerStack = stackInSlot;
                } else {
                    if (!(stackInSlot.getItem() instanceof PEShield) || !shieldStack.isEmpty() || stackInSlot.getTagElement("BlockEntityTag") != null) {
                        return false;
                    }
                    shieldStack = stackInSlot;
                }
            }
        }
        return !shieldStack.isEmpty() && !bannerStack.isEmpty();
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack bannerStack = ItemStack.EMPTY;
        ItemStack shieldStack = ItemStack.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stackInSlot = inv.getItem(i);
            if (!stackInSlot.isEmpty()) {
                if (stackInSlot.getItem() instanceof BannerItem) {
                    bannerStack = stackInSlot;
                } else if (stackInSlot.getItem() instanceof PEShield) {
                    shieldStack = stackInSlot.copy();
                }
            }
        }
        if (shieldStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        CompoundTag blockEntityTag = bannerStack.getTagElement("BlockEntityTag");
        CompoundTag tag = blockEntityTag == null ? new CompoundTag() : blockEntityTag.copy();
        tag.putInt("Base", ((BannerItem) bannerStack.getItem()).getColor().getId());
        shieldStack.addTagElement("BlockEntityTag", tag);
        return shieldStack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ProjectExtendedRecipeSerializers.SHIELD_DECORATION.get();
    }
}