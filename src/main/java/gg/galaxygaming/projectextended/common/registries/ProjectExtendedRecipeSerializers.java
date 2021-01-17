package gg.galaxygaming.projectextended.common.registries;

import gg.galaxygaming.projectextended.common.recipe.PEShieldSpecialRecipe;
import gg.galaxygaming.projectextended.common.registration.IRecipeSerializerDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.IRecipeSerializerRegistryObject;
import net.minecraft.item.crafting.SpecialRecipeSerializer;

public class ProjectExtendedRecipeSerializers {

    public static final IRecipeSerializerDeferredRegister RECIPE_SERIALIZERS = new IRecipeSerializerDeferredRegister();

    public static final IRecipeSerializerRegistryObject<PEShieldSpecialRecipe, SpecialRecipeSerializer<PEShieldSpecialRecipe>> SHIELD_DECORATION = RECIPE_SERIALIZERS.register("shield_decoration", () -> new SpecialRecipeSerializer<>(PEShieldSpecialRecipe::new));
}