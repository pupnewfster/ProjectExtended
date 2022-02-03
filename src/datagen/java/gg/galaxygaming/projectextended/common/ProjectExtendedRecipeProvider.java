package gg.galaxygaming.projectextended.common;

import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlocks;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedItems;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedRecipeSerializers;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.registries.PEItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;

public class ProjectExtendedRecipeProvider extends RecipeProvider {

    public ProjectExtendedRecipeProvider(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> consumer) {
        addCustomRecipeSerializer(consumer, ProjectExtendedRecipeSerializers.SHIELD_DECORATION.get());
        addTridentRecipe(consumer, ProjectExtendedItems.DARK_MATTER_TRIDENT, PEItems.DARK_MATTER, Items.TRIDENT, Ingredient.of(Tags.Items.GEMS_DIAMOND));
        addTridentRecipe(consumer, ProjectExtendedItems.RED_MATTER_TRIDENT, PEItems.RED_MATTER, ProjectExtendedItems.DARK_MATTER_TRIDENT,
              Ingredient.of(PEItems.DARK_MATTER));
        //Dark matter shield
        ShapedRecipeBuilder.shaped(ProjectExtendedItems.DARK_MATTER_SHIELD)
              .pattern("PMP")
              .pattern("PPP")
              .pattern(" P ")
              .define('M', PEItems.DARK_MATTER)
              .define('P', Tags.Items.GEMS_DIAMOND)
              .unlockedBy("has_matter", has(PEItems.DARK_MATTER))
              .save(consumer);
        //Red matter shield
        ShapedRecipeBuilder.shaped(ProjectExtendedItems.RED_MATTER_SHIELD)
              .pattern("PMP")
              .pattern("PSP")
              .pattern(" P ")
              .define('M', PEItems.RED_MATTER)
              .define('S', ProjectExtendedItems.DARK_MATTER_SHIELD)
              .define('P', PEItems.DARK_MATTER)
              .unlockedBy("has_matter", has(PEItems.RED_MATTER))
              .unlockedBy("has_shield", has(ProjectExtendedItems.DARK_MATTER_SHIELD))
              .save(consumer);
        //Alchemical Barrel
        ShapedRecipeBuilder.shaped(ProjectExtendedBlocks.ALCHEMICAL_BARREL)
              .pattern("LMH")
              .pattern("SDS")
              .pattern("IBI")
              .define('L', PEItems.LOW_COVALENCE_DUST)
              .define('M', PEItems.MEDIUM_COVALENCE_DUST)
              .define('H', PEItems.HIGH_COVALENCE_DUST)
              .define('S', Tags.Items.STONE)
              .define('I', Tags.Items.INGOTS_IRON)
              .define('B', Tags.Items.BARRELS_WOODEN)
              .define('D', Tags.Items.GEMS_DIAMOND)
              .unlockedBy("has_covalence_dust", InventoryChangeTrigger.TriggerInstance.hasItems(PEItems.LOW_COVALENCE_DUST, PEItems.MEDIUM_COVALENCE_DUST, PEItems.HIGH_COVALENCE_DUST))
              .save(consumer);
    }

    private static void addCustomRecipeSerializer(Consumer<FinishedRecipe> consumer, SimpleRecipeSerializer<?> serializer) {
        SpecialRecipeBuilder.special(serializer).save(consumer, serializer.getRegistryName().toString());
    }

    private void addTridentRecipe(Consumer<FinishedRecipe> consumer, ItemLike item, ItemLike matter, ItemLike trident, Ingredient previousTier) {
        ShapedRecipeBuilder.shaped(item)
              .pattern("MTM")
              .pattern(" P ")
              .pattern(" P ")
              .define('M', matter)
              .define('T', trident)
              .define('P', previousTier)
              .unlockedBy("has_matter", has(matter))
              .unlockedBy("has_trident", has(trident))
              .save(consumer);
    }
}