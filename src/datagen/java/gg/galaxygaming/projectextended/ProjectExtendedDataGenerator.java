package gg.galaxygaming.projectextended;

import gg.galaxygaming.projectextended.client.ProjectExtendedItemModelProvider;
import gg.galaxygaming.projectextended.client.lang.ProjectExtendedLangProvider;
import gg.galaxygaming.projectextended.common.ProjectExtendedRecipeProvider;
import gg.galaxygaming.projectextended.common.tag.ProjectExtendedBlockTagProvider;
import gg.galaxygaming.projectextended.common.tag.ProjectExtendedEntityTypesTagProvider;
import gg.galaxygaming.projectextended.common.tag.ProjectExtendedItemTagProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(modid = ProjectExtended.MODID, bus = Bus.MOD)
public class ProjectExtendedDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        if (event.includeClient()) {
            gen.addProvider(new ProjectExtendedLangProvider(gen));
            gen.addProvider(new ProjectExtendedItemModelProvider(gen, existingFileHelper));
        }
        if (event.includeServer()) {
            ProjectExtendedBlockTagProvider blockTagsProvider = new ProjectExtendedBlockTagProvider(gen, existingFileHelper);
            gen.addProvider(blockTagsProvider);
            gen.addProvider(new ProjectExtendedItemTagProvider(gen, blockTagsProvider, existingFileHelper));
            gen.addProvider(new ProjectExtendedEntityTypesTagProvider(gen, existingFileHelper));
            gen.addProvider(new ProjectExtendedRecipeProvider(gen));
        }
    }
}