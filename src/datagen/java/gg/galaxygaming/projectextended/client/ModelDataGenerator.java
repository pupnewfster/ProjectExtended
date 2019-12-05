package gg.galaxygaming.projectextended.client;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.items.ProjectExtendedItems;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;

public class ModelDataGenerator extends ItemModelProvider {

    public ModelDataGenerator(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, ProjectExtended.MODID, helper);
    }

    @Override
    protected void registerModels() {
        generateShieldModels(ProjectExtendedItems.DARK_MATTER_SHIELD, mcLoc("block/diamond_block"));
        //Note: We include an empty file to fake that the texture exists
        generateShieldModels(ProjectExtendedItems.RED_MATTER_SHIELD, new ResourceLocation(PECore.MODID, "blocks/dm"));
        generateTridentModels(ProjectExtendedItems.DARK_MATTER_TRIDENT);
        generateTridentModels(ProjectExtendedItems.RED_MATTER_TRIDENT);
    }

    private void generateShieldModels(Supplier<? extends Item> item, ResourceLocation particle) {
        String name = name(item);
        ItemModelBuilder blockingModel = getBuilder(name + "_blocking")
              .parent(new UncheckedModelFile(folder + "/shield_blocking"))
              .texture("particle", particle);
        getBuilder(name)
              .parent(new UncheckedModelFile(folder + "/shield"))
              .texture("particle", particle)
              .override()
              .predicate(mcLoc("blocking"), 1)
              .model(new UncheckedModelFile(blockingModel.getUncheckedLocation()))
              .end();
    }

    private String name(Supplier<? extends Item> item) {
        return item.get().getRegistryName().getPath();
    }

    private void generateTridentModels(Supplier<? extends Item> item) {
        String name = name(item);
        ResourceLocation itemLoc = modLoc(folder + "/" + name);
        //Base Model
        getBuilder(name).parent(new UncheckedModelFile(folder + "/generated")).texture("layer0", itemLoc);
        //Throwing model
        ItemModelBuilder throwingModel = getBuilder(name + "_throwing")
              .parent(new UncheckedModelFile(folder + "/trident_throwing"))
              .texture("particle", itemLoc);
        //In hand model
        getBuilder(name + "_in_hand")
              .parent(new UncheckedModelFile(folder + "/trident_in_hand"))
              .texture("particle", itemLoc)
              .override()
              .predicate(mcLoc("throwing"), 1)
              .model(new UncheckedModelFile(throwingModel.getUncheckedLocation()))
              .end();
    }

    @Nonnull
    @Override
    public String getName() {
        return ProjectExtended.MOD_NAME + " Item Models";
    }
}