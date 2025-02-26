package gg.galaxygaming.projectextended;

import gg.galaxygaming.projectextended.client.ProjectExtendedBlockStateProvider;
import gg.galaxygaming.projectextended.client.ProjectExtendedItemModelProvider;
import gg.galaxygaming.projectextended.client.ProjectExtendedSpriteSourceProvider;
import gg.galaxygaming.projectextended.client.lang.ProjectExtendedLangProvider;
import gg.galaxygaming.projectextended.common.ProjectExtendedAdvancementsGenerator;
import gg.galaxygaming.projectextended.common.ProjectExtendedLang;
import gg.galaxygaming.projectextended.common.ProjectExtendedPackMetadataGenerator;
import gg.galaxygaming.projectextended.common.ProjectExtendedRecipeProvider;
import gg.galaxygaming.projectextended.common.loot.ProjectExtendedBlockLootTable;
import gg.galaxygaming.projectextended.common.tag.ProjectExtendedBlockTagProvider;
import gg.galaxygaming.projectextended.common.tag.ProjectExtendedEntityTypesTagProvider;
import gg.galaxygaming.projectextended.common.tag.ProjectExtendedItemTagProvider;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

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
        gen.addProvider(event.includeClient(), new ProjectExtendedSpriteSourceProvider(output, lookupProvider, existingFileHelper));
        gen.addProvider(event.includeClient(), new ProjectExtendedLangProvider(output));
        gen.addProvider(event.includeClient(), new ProjectExtendedBlockStateProvider(output, existingFileHelper));
        gen.addProvider(event.includeClient(), new ProjectExtendedItemModelProvider(output, existingFileHelper));
        //Server side datagen
        ProjectExtendedBlockTagProvider blockTagsProvider = gen.addProvider(event.includeServer(), new ProjectExtendedBlockTagProvider(output, lookupProvider, existingFileHelper));
        gen.addProvider(event.includeServer(), new ProjectExtendedItemTagProvider(output, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
        gen.addProvider(event.includeServer(), new ProjectExtendedEntityTypesTagProvider(output, lookupProvider, existingFileHelper));
        gen.addProvider(event.includeServer(), new ProjectExtendedRecipeProvider(output, lookupProvider));
        gen.addProvider(event.includeServer(), new AdvancementProvider(output, lookupProvider, existingFileHelper, List.of(new ProjectExtendedAdvancementsGenerator())));
        gen.addProvider(event.includeServer(), new LootTableProvider(output, Collections.emptySet(), List.of(
              new SubProviderEntry(ProjectExtendedBlockLootTable::new, LootContextParamSets.BLOCK)
        ), lookupProvider));
    }
}