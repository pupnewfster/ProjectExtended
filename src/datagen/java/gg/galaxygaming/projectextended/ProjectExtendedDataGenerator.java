package gg.galaxygaming.projectextended;

import gg.galaxygaming.projectextended.client.ProjectExtendedBlockStateProvider;
import gg.galaxygaming.projectextended.client.ProjectExtendedItemModelProvider;
import gg.galaxygaming.projectextended.client.ProjectExtendedSpriteSourceProvider;
import gg.galaxygaming.projectextended.client.lang.ProjectExtendedLangProvider;
import gg.galaxygaming.projectextended.common.ProjectExtendedAdvancementsGenerator;
import gg.galaxygaming.projectextended.common.ProjectExtendedLang;
import gg.galaxygaming.projectextended.common.ProjectExtendedPackMetadataGenerator;
import gg.galaxygaming.projectextended.common.ProjectExtendedRecipeProvider;
import gg.galaxygaming.projectextended.common.loot.ProjectExtendedLootProvider;
import gg.galaxygaming.projectextended.common.tag.ProjectExtendedBlockTagProvider;
import gg.galaxygaming.projectextended.common.tag.ProjectExtendedEntityTypesTagProvider;
import gg.galaxygaming.projectextended.common.tag.ProjectExtendedItemTagProvider;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = ProjectExtended.MODID, bus = Bus.MOD)
public class ProjectExtendedDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<Provider> lookupProvider = event.getLookupProvider();

        gen.addProvider(true, new ProjectExtendedPackMetadataGenerator(output, ProjectExtendedLang.PACK_DESCRIPTION));
        //Client side datagen
        gen.addProvider(event.includeClient(), new ProjectExtendedSpriteSourceProvider(output, existingFileHelper));
        gen.addProvider(event.includeClient(), new ProjectExtendedLangProvider(output));
        gen.addProvider(event.includeClient(), new ProjectExtendedBlockStateProvider(output, existingFileHelper));
        gen.addProvider(event.includeClient(), new ProjectExtendedItemModelProvider(output, existingFileHelper));
        //Server side datagen
        ProjectExtendedBlockTagProvider blockTagsProvider = gen.addProvider(event.includeServer(), new ProjectExtendedBlockTagProvider(output, lookupProvider, existingFileHelper));
        gen.addProvider(event.includeServer(), new ProjectExtendedItemTagProvider(output, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
        gen.addProvider(event.includeServer(), new ProjectExtendedEntityTypesTagProvider(output, lookupProvider, existingFileHelper));
        gen.addProvider(event.includeServer(), new ProjectExtendedRecipeProvider(output));
        gen.addProvider(event.includeServer(), new ForgeAdvancementProvider(output, lookupProvider, existingFileHelper, List.of(new ProjectExtendedAdvancementsGenerator())));
        gen.addProvider(event.includeServer(), new ProjectExtendedLootProvider(output));
    }
}