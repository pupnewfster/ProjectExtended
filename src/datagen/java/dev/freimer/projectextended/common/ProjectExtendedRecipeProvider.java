package dev.freimer.projectextended.common;

import dev.freimer.projectextended.common.recipe.PEShieldSpecialRecipe;
import dev.freimer.projectextended.common.registries.ProjectExtendedBlocks;
import dev.freimer.projectextended.common.registries.ProjectExtendedItems;
import dev.freimer.projectextended.common.registries.ProjectExtendedRecipeSerializers;
import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.gameObjs.registries.PEItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class ProjectExtendedRecipeProvider extends RecipeProvider {

    public ProjectExtendedRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        SpecialRecipeBuilder.special(PEShieldSpecialRecipe::new).save(recipeOutput, ProjectExtendedRecipeSerializers.SHIELD_DECORATION.getId());
        addTridentRecipe(recipeOutput, ProjectExtendedItems.DARK_MATTER_TRIDENT, PEItems.DARK_MATTER, Items.TRIDENT.builtInRegistryHolder(), Ingredient.of(Tags.Items.GEMS_DIAMOND));
        addTridentRecipe(recipeOutput, ProjectExtendedItems.RED_MATTER_TRIDENT, PEItems.RED_MATTER, ProjectExtendedItems.DARK_MATTER_TRIDENT,
              Ingredient.of(PEItems.DARK_MATTER));
        //Dark matter shield
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ProjectExtendedItems.DARK_MATTER_SHIELD)
              .pattern("PMP")
              .pattern("PPP")
              .pattern(" P ")
              .define('M', PEItems.DARK_MATTER)
              .define('P', Tags.Items.GEMS_DIAMOND)
              .unlockedBy("has_matter", has(PEItems.DARK_MATTER))
              .save(recipeOutput);
        //Red matter shield
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ProjectExtendedItems.RED_MATTER_SHIELD)
              .pattern("PMP")
              .pattern("PSP")
              .pattern(" P ")
              .define('M', PEItems.RED_MATTER)
              .define('S', ProjectExtendedItems.DARK_MATTER_SHIELD)
              .define('P', PEItems.DARK_MATTER)
              .unlockedBy("has_matter", has(PEItems.RED_MATTER))
              .unlockedBy("has_shield", has(ProjectExtendedItems.DARK_MATTER_SHIELD))
              .save(recipeOutput);
        //Alchemical Barrel
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ProjectExtendedBlocks.ALCHEMICAL_BARREL)
              .pattern("LMH")
              .pattern("SDS")
              .pattern("IBI")
              .define('L', PEItems.LOW_COVALENCE_DUST)
              .define('M', PEItems.MEDIUM_COVALENCE_DUST)
              .define('H', PEItems.HIGH_COVALENCE_DUST)
              .define('S', Tags.Items.STONES)
              .define('I', Tags.Items.INGOTS_IRON)
              .define('B', Tags.Items.BARRELS_WOODEN)
              .define('D', Tags.Items.GEMS_DIAMOND)
              .unlockedBy("has_covalence_dust", InventoryChangeTrigger.TriggerInstance.hasItems(PEItems.LOW_COVALENCE_DUST, PEItems.MEDIUM_COVALENCE_DUST, PEItems.HIGH_COVALENCE_DUST))
              .save(recipeOutput);
        //Interdiction Lantern
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ProjectExtendedBlocks.INTERDICTION_LANTERN)
              .pattern("RDR")
              .pattern("DPD")
              .pattern("GGG")
              .define('R', Items.SOUL_LANTERN)
              .define('G', Tags.Items.DUSTS_GLOWSTONE)
              .define('D', Tags.Items.GEMS_DIAMOND)
              .define('P', PEItems.PHILOSOPHERS_STONE)
              .unlockedBy("has_philo_stone", has(PEItems.PHILOSOPHERS_STONE))
              .save(recipeOutput);
    }

    private void addTridentRecipe(RecipeOutput recipeOutput, Holder<Item> item, Holder<Item> matter, Holder<Item> trident, Ingredient previousTier) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, item.value())
              .pattern("MTM")
              .pattern(" P ")
              .pattern(" P ")
              .define('M', matter.value())
              .define('T', trident.value())
              .define('P', previousTier)
              .unlockedBy("has_matter", has(matter.value()))
              .unlockedBy("has_trident", has(trident.value()))
              .save(recipeOutput);
    }
}