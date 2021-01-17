package gg.galaxygaming.projectextended;

import gg.galaxygaming.projectextended.client.ModelDataGenerator;
import gg.galaxygaming.projectextended.client.lang.ProjectExtendedLangProvider;
import gg.galaxygaming.projectextended.common.RecipeDataGenerator;
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
            gen.addProvider(new ModelDataGenerator(gen, existingFileHelper));
        }
        if (event.includeServer()) {
            gen.addProvider(new RecipeDataGenerator(gen));
        }
    }
}