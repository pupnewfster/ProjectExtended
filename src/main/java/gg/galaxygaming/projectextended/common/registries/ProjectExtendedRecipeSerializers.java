package gg.galaxygaming.projectextended.common.registries;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.recipe.PEShieldSpecialRecipe;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class ProjectExtendedRecipeSerializers {

    public static final PEDeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = new PEDeferredRegister<>(Registries.RECIPE_SERIALIZER, ProjectExtended.MODID);

    public static final PEDeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<PEShieldSpecialRecipe>> SHIELD_DECORATION = RECIPE_SERIALIZERS.register("shield_decoration", () -> new SimpleCraftingRecipeSerializer<>(PEShieldSpecialRecipe::new));
}