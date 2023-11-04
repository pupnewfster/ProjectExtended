package gg.galaxygaming.projectextended.client;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.items.PEShield;
import gg.galaxygaming.projectextended.common.items.PETrident;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlocks;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedItems;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registration.impl.ItemRegistryObject;
import moze_intel.projecte.utils.RegistryUtils;
import net.minecraft.client.renderer.block.model.BlockModel.GuiLight;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class ProjectExtendedItemModelProvider extends ItemModelProvider {

    public ProjectExtendedItemModelProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, ProjectExtended.MODID, helper);
    }

    @NotNull
    @Override
    public String getName() {
        return ProjectExtended.MOD_NAME + " Item Models";
    }

    private static String getName(ItemLike itemProvider) {
        return RegistryUtils.getPath(itemProvider.asItem());
    }

    protected ItemModelBuilder generated(ItemLike itemProvider, ResourceLocation texture) {
        return generated(getName(itemProvider), texture);
    }

    protected ItemModelBuilder generated(String name, ResourceLocation texture) {
        return withExistingParent(name, "item/generated").texture("layer0", texture);
    }

    @Override
    protected void registerModels() {
        generateShieldModel(ProjectExtendedItems.DARK_MATTER_SHIELD, mcLoc("block/diamond_block"));
        generateShieldModel(ProjectExtendedItems.RED_MATTER_SHIELD, PECore.rl("block/dark_matter_block"));
        generateTridentModel(ProjectExtendedItems.DARK_MATTER_TRIDENT);
        generateTridentModel(ProjectExtendedItems.RED_MATTER_TRIDENT);

        String name = getName(ProjectExtendedBlocks.ALCHEMICAL_BARREL);
        withExistingParent(name, modLoc("block/" + name));

        generated(ProjectExtendedBlocks.INTERDICTION_LANTERN, modLoc("item/interdiction_lantern"));
    }

    private void generateShieldModel(ItemRegistryObject<PEShield> item, ResourceLocation particle) {
        String name = getName(item);
        withExistingParent(name, "shield")
              .texture("particle", particle)
              .override()
              .predicate(modLoc("blocking"), 1)
              .model(withExistingParent(name + "_blocking", "shield_blocking")
                    .texture("particle", particle))
              .end();
    }

    private void generateTridentModel(ItemRegistryObject<PETrident> item) {
        String name = getName(item);
        ResourceLocation itemLoc = modLoc(folder + "/" + name);
        ItemModelBuilder guiModel = nested()
              .parent(withExistingParent(name + "_gui", "item/generated")
                    .texture("layer0", itemLoc));
        ItemModelBuilder throwingModel = getBuilder(name + "_throwing")
              .guiLight(GuiLight.FRONT)
              .texture("particle", itemLoc)
              .customLoader(SeparateTransformsModelBuilder::begin)
              //Throwing model is "base" so that we can have our transforms
              .base(nested()
                    .parent(getExistingFile(mcLoc("trident_throwing")))
                    .texture("particle", itemLoc))
              //Gui, ground, and fixed all use the normal "item model"
              .perspective(ItemDisplayContext.GUI, guiModel)
              .perspective(ItemDisplayContext.GROUND, guiModel)
              .perspective(ItemDisplayContext.FIXED, guiModel)
              .end();
        getBuilder(name)
              .guiLight(GuiLight.FRONT)
              .texture("particle", itemLoc)
              //Override when throwing to the throwing model to ensure we have the correct transforms
              .override()
              .predicate(modLoc("throwing"), 1)
              .model(throwingModel)
              .end()
              .customLoader(SeparateTransformsModelBuilder::begin)
              //In hand model is base
              .base(nested()
                    .parent(getExistingFile(mcLoc("trident_in_hand")))
                    .texture("particle", itemLoc)
                    //Add head transformation
                    .transforms()
                    .transform(ItemDisplayContext.HEAD)
                    .rotation(0, 180, 120)
                    .translation(8, 10, -11)
                    .scale(1.5F)
                    .end()
                    .end())
              //Gui, ground, and fixed all use the normal "item model"
              .perspective(ItemDisplayContext.GUI, guiModel)
              .perspective(ItemDisplayContext.GROUND, guiModel)
              .perspective(ItemDisplayContext.FIXED, guiModel);
    }
}