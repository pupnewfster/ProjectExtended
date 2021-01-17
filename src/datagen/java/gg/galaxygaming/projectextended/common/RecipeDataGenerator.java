package gg.galaxygaming.projectextended.common;

import gg.galaxygaming.projectextended.common.registries.ProjectExtendedItems;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedRecipeSerializers;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.registries.PEItems;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;

public class RecipeDataGenerator extends RecipeProvider {

    public RecipeDataGenerator(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
        addCustomRecipeSerializer(consumer, ProjectExtendedRecipeSerializers.SHIELD_DECORATION.get());
        addTridentRecipe(consumer, ProjectExtendedItems.DARK_MATTER_TRIDENT, PEItems.DARK_MATTER, Items.TRIDENT, Ingredient.fromTag(Tags.Items.GEMS_DIAMOND));
        addTridentRecipe(consumer, ProjectExtendedItems.RED_MATTER_TRIDENT, PEItems.RED_MATTER, ProjectExtendedItems.DARK_MATTER_TRIDENT,
              Ingredient.fromItems(PEItems.DARK_MATTER));
        //Dark matter shield
        ShapedRecipeBuilder.shapedRecipe(ProjectExtendedItems.DARK_MATTER_SHIELD)
              .patternLine("PMP")
              .patternLine("PPP")
              .patternLine(" P ")
              .key('M', PEItems.DARK_MATTER)
              .key('P', Tags.Items.GEMS_DIAMOND)
              .addCriterion("has_matter", hasItem(PEItems.DARK_MATTER))
              .build(consumer);
        //Red matter shield
        ShapedRecipeBuilder.shapedRecipe(ProjectExtendedItems.RED_MATTER_SHIELD)
              .patternLine("PMP")
              .patternLine("PSP")
              .patternLine(" P ")
              .key('M', PEItems.RED_MATTER)
              .key('S', ProjectExtendedItems.DARK_MATTER_SHIELD)
              .key('P', PEItems.DARK_MATTER)
              .addCriterion("has_matter", hasItem(PEItems.RED_MATTER))
              .addCriterion("has_shield", hasItem(ProjectExtendedItems.DARK_MATTER_SHIELD))
              .build(consumer);
    }

    private static void addCustomRecipeSerializer(Consumer<IFinishedRecipe> consumer, SpecialRecipeSerializer<?> serializer) {
        CustomRecipeBuilder.customRecipe(serializer).build(consumer, serializer.getRegistryName().toString());
    }

    private void addTridentRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider item, IItemProvider matter, IItemProvider trident, Ingredient previousTier) {
        ShapedRecipeBuilder.shapedRecipe(item)
              .patternLine("MTM")
              .patternLine(" P ")
              .patternLine(" P ")
              .key('M', matter)
              .key('T', trident)
              .key('P', previousTier)
              .addCriterion("has_matter", hasItem(matter))
              .addCriterion("has_trident", hasItem(trident))
              .build(consumer);
    }
}