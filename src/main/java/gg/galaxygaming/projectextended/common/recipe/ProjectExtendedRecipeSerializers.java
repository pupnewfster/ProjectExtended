package gg.galaxygaming.projectextended.common.recipe;

import gg.galaxygaming.projectextended.ProjectExtended;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ProjectExtendedRecipeSerializers {

    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, ProjectExtended.MODID);

    public static final RegistryObject<SpecialRecipeSerializer<?>> SHIELD_DECORATION = RECIPE_SERIALIZERS.register("pe_shielddecoration", () -> new SpecialRecipeSerializer<>(PEShieldSpecialRecipe::new));
}