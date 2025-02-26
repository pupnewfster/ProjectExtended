package dev.freimer.projectextended.common.recipe;

import dev.freimer.projectextended.common.items.PEShield;
import dev.freimer.projectextended.common.registries.ProjectExtendedRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.jetbrains.annotations.NotNull;

public class PEShieldSpecialRecipe extends CustomRecipe {

    public PEShieldSpecialRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput inv, @NotNull Level world) {
        ItemStack shieldStack = ItemStack.EMPTY;
        ItemStack bannerStack = ItemStack.EMPTY;
        for (ItemStack stackInSlot : inv.items()) {
            if (!stackInSlot.isEmpty()) {
                if (stackInSlot.getItem() instanceof BannerItem) {
                    if (!bannerStack.isEmpty()) {
                        return false;
                    }
                    bannerStack = stackInSlot;
                } else {
                    if (!(stackInSlot.getItem() instanceof PEShield) || !shieldStack.isEmpty()) {
                        return false;
                    }
                    BannerPatternLayers bannerpatternlayers = stackInSlot.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
                    if (!bannerpatternlayers.layers().isEmpty()) {
                        return false;
                    }
                    shieldStack = stackInSlot;
                }
            }
        }
        return !shieldStack.isEmpty() && !bannerStack.isEmpty();
    }

    @NotNull
    @Override
    public ItemStack assemble(CraftingInput inv, @NotNull HolderLookup.Provider registryAccess) {
        ItemStack bannerStack = ItemStack.EMPTY;
        ItemStack shieldStack = ItemStack.EMPTY;
        for (ItemStack stackInSlot : inv.items()) {
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
        shieldStack.set(DataComponents.BANNER_PATTERNS, bannerStack.get(DataComponents.BANNER_PATTERNS));
        shieldStack.set(DataComponents.BASE_COLOR, ((BannerItem) bannerStack.getItem()).getColor());
        return shieldStack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ProjectExtendedRecipeSerializers.SHIELD_DECORATION.get();
    }
}