package gg.galaxygaming.projectextended;

import gg.galaxygaming.projectextended.client.ProjectExtendedItemModelProvider;
import gg.galaxygaming.projectextended.client.lang.ProjectExtendedLangProvider;
import gg.galaxygaming.projectextended.common.ProjectExtendedEntityTypesTagProvider;
import gg.galaxygaming.projectextended.common.ProjectExtendedRecipeProvider;
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
            gen.addProvider(new ProjectExtendedEntityTypesTagProvider(gen, existingFileHelper));
            gen.addProvider(new ProjectExtendedRecipeProvider(gen));
        }
    }
}