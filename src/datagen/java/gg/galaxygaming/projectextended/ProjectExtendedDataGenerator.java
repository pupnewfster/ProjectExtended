package gg.galaxygaming.projectextended;

import gg.galaxygaming.projectextended.client.ProjectExtendedBlockStateProvider;
import gg.galaxygaming.projectextended.client.ProjectExtendedItemModelProvider;
import gg.galaxygaming.projectextended.client.lang.ProjectExtendedLangProvider;
import gg.galaxygaming.projectextended.common.ProjectExtendedAdvancementsProvider;
import gg.galaxygaming.projectextended.common.ProjectExtendedRecipeProvider;
import gg.galaxygaming.projectextended.common.loot.ProjectExtendedLootProvider;
import gg.galaxygaming.projectextended.common.tag.ProjectExtendedBlockTagProvider;
import gg.galaxygaming.projectextended.common.tag.ProjectExtendedEntityTypesTagProvider;
import gg.galaxygaming.projectextended.common.tag.ProjectExtendedItemTagProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = ProjectExtended.MODID, bus = Bus.MOD)
public class ProjectExtendedDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        //Client side datagen
        gen.addProvider(event.includeClient(), new ProjectExtendedLangProvider(gen));
        gen.addProvider(event.includeClient(), new ProjectExtendedBlockStateProvider(gen, existingFileHelper));
        gen.addProvider(event.includeClient(), new ProjectExtendedItemModelProvider(gen, existingFileHelper));
        //Server side datagen
        ProjectExtendedBlockTagProvider blockTagsProvider = new ProjectExtendedBlockTagProvider(gen, existingFileHelper);
        gen.addProvider(event.includeServer(), blockTagsProvider);
        gen.addProvider(event.includeServer(), new ProjectExtendedItemTagProvider(gen, blockTagsProvider, existingFileHelper));
        gen.addProvider(event.includeServer(), new ProjectExtendedEntityTypesTagProvider(gen, existingFileHelper));
        gen.addProvider(event.includeServer(), new ProjectExtendedRecipeProvider(gen));
        gen.addProvider(event.includeServer(), new ProjectExtendedAdvancementsProvider(gen, existingFileHelper));
        gen.addProvider(event.includeServer(), new ProjectExtendedLootProvider(gen));
    }
}