package gg.galaxygaming.projectextended.common;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.items.ProjectExtendedItems;
import gg.galaxygaming.projectextended.common.recipe.ProjectExtendedRecipeSerializers;
import java.util.function.Consumer;
import java.util.function.Supplier;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

public class RecipeDataGenerator extends RecipeProvider {

    public RecipeDataGenerator(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        addTridentRecipe(ProjectExtendedItems.DARK_MATTER_TRIDENT, ObjHandler.darkMatter, Items.TRIDENT, Ingredient.fromTag(Tags.Items.GEMS_DIAMOND)).build(consumer);
        addTridentRecipe(ProjectExtendedItems.RED_MATTER_TRIDENT, ObjHandler.redMatter, ProjectExtendedItems.DARK_MATTER_TRIDENT.get(), Ingredient.fromItems(ObjHandler.darkMatter)).build(consumer);
        addDMShieldRecipe().build(consumer);
        addShieldRecipe(ProjectExtendedItems.RED_MATTER_SHIELD, ObjHandler.redMatter, ProjectExtendedItems.DARK_MATTER_SHIELD.get(), ObjHandler.darkMatter).build(consumer);
        new CustomRecipeBuilder(ProjectExtendedRecipeSerializers.SHIELD_DECORATION.get()).build(consumer, ProjectExtended.MODID + ":matter_shield_decoration");
    }

    private ShapedRecipeBuilder addTridentRecipe(Supplier<? extends Item> item, Item matter, Item trident, Ingredient previousTier) {
        return ShapedRecipeBuilder.shapedRecipe(item.get())
              .patternLine("MTM")
              .patternLine(" P ")
              .patternLine(" P ")
              .key('M', matter).key('T', trident).key('P', previousTier)
              .addCriterion("has_matter", hasItem(matter)).addCriterion("has_trident", hasItem(trident));
    }

    private ShapedRecipeBuilder addDMShieldRecipe() {
        return ShapedRecipeBuilder.shapedRecipe(ProjectExtendedItems.DARK_MATTER_SHIELD.get())
              .patternLine("PMP")
              .patternLine("PPP")
              .patternLine(" P ")
              .key('M', ObjHandler.darkMatter).key('P', Items.DIAMOND)
              .addCriterion("has_matter", hasItem(ObjHandler.darkMatter));
    }

    private ShapedRecipeBuilder addShieldRecipe(Supplier<? extends Item> item, Item matter, Item shield, Item previousTier) {
        return ShapedRecipeBuilder.shapedRecipe(item.get())
              .patternLine("PMP")
              .patternLine("PSP")
              .patternLine(" P ")
              .key('M', matter).key('S', shield).key('P', previousTier)
              .addCriterion("has_matter", hasItem(matter)).addCriterion("has_shield", hasItem(shield));
    }
}