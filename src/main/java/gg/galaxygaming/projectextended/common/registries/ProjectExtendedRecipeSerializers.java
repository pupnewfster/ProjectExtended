package gg.galaxygaming.projectextended.common.registries;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.recipe.PEShieldSpecialRecipe;
import moze_intel.projecte.gameObjs.registration.impl.IRecipeSerializerDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.IRecipeSerializerRegistryObject;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class ProjectExtendedRecipeSerializers {

    public static final IRecipeSerializerDeferredRegister RECIPE_SERIALIZERS = new IRecipeSerializerDeferredRegister(ProjectExtended.MODID);

    public static final IRecipeSerializerRegistryObject<PEShieldSpecialRecipe, SimpleCraftingRecipeSerializer<PEShieldSpecialRecipe>> SHIELD_DECORATION = RECIPE_SERIALIZERS.register("shield_decoration", () -> new SimpleCraftingRecipeSerializer<>(PEShieldSpecialRecipe::new));
}