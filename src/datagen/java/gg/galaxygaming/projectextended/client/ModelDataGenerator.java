package gg.galaxygaming.projectextended.client;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.items.PEShield;
import gg.galaxygaming.projectextended.common.items.PETrident;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedItems;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registration.impl.ItemRegistryObject;
import net.minecraft.client.renderer.model.BlockModel.GuiLight;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder.Perspective;
import net.minecraftforge.client.model.generators.loaders.SeparatePerspectiveModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModelDataGenerator extends ItemModelProvider {

    public ModelDataGenerator(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, ProjectExtended.MODID, helper);
    }

    @Nonnull
    @Override
    public String getName() {
        return ProjectExtended.MOD_NAME + " Item Models";
    }

    @Override
    protected void registerModels() {
        generateShieldModel(ProjectExtendedItems.DARK_MATTER_SHIELD, mcLoc("block/diamond_block"));
        generateShieldModel(ProjectExtendedItems.RED_MATTER_SHIELD, PECore.rl("block/dark_matter_block"));
        generateTridentModel(ProjectExtendedItems.DARK_MATTER_TRIDENT);
        generateTridentModel(ProjectExtendedItems.RED_MATTER_TRIDENT);
    }

    private void generateShieldModel(ItemRegistryObject<PEShield> item, ResourceLocation particle) {
        String name = name(item);
        withExistingParent(name, "shield")
              .texture("particle", particle)
              .override()
              .predicate(modLoc("blocking"), 1)
              .model(withExistingParent(name + "_blocking", "shield_blocking")
                    .texture("particle", particle))
              .end();
    }

    private void generateTridentModel(ItemRegistryObject<PETrident> item) {
        String name = name(item);
        ResourceLocation itemLoc = modLoc(folder + "/" + name);
        ItemModelBuilder guiModel = nested()
              .parent(withExistingParent(name + "_gui", "item/generated")
                    .texture("layer0", itemLoc));
        ItemModelBuilder throwingModel = getBuilder(name + "_throwing")
              .guiLight(GuiLight.FRONT)
              .texture("particle", itemLoc)
              .customLoader(SeparatePerspectiveModelBuilder::begin)
              //Throwing model is "base" so that we can have our transforms
              .base(nested()
                    .parent(getExistingFile(mcLoc("trident_throwing")))
                    .texture("particle", itemLoc))
              //Gui, ground, and fixed all use the normal "item model"
              .perspective(TransformType.GUI, guiModel)
              .perspective(TransformType.GROUND, guiModel)
              .perspective(TransformType.FIXED, guiModel)
              .end();
        getBuilder(name)
              .guiLight(GuiLight.FRONT)
              .texture("particle", itemLoc)
              //Override when throwing to the throwing model to ensure we have the correct transforms
              .override()
              .predicate(modLoc("throwing"), 1)
              .model(throwingModel)
              .end()
              .customLoader(SeparatePerspectiveModelBuilder::begin)
              //In hand model is base
              .base(nested()
                    .parent(getExistingFile(mcLoc("trident_in_hand")))
                    .texture("particle", itemLoc)
                    //Add head transformation
                    .transforms()
                    .transform(Perspective.HEAD)
                    .rotation(0, 180, 120)
                    .translation(8, 10, -11)
                    .scale(1.5F)
                    .end()
                    .end())
              //Gui, ground, and fixed all use the normal "item model"
              .perspective(TransformType.GUI, guiModel)
              .perspective(TransformType.GROUND, guiModel)
              .perspective(TransformType.FIXED, guiModel);
    }

    private static String name(IItemProvider item) {
        return item.asItem().getRegistryName().getPath();
    }
}