package gg.galaxygaming.projectextended.common;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.client.LanguageDataGenerator;
import gg.galaxygaming.projectextended.client.ModelDataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(modid = ProjectExtended.MODID, bus = Bus.MOD)
public class DataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        net.minecraft.data.DataGenerator gen = event.getGenerator();
        if (event.includeClient()) {
            gen.addProvider(new LanguageDataGenerator(gen));
            gen.addProvider(new ModelDataGenerator(gen, event.getExistingFileHelper()));
        }
        if (event.includeServer()) {
            gen.addProvider(new RecipeDataGenerator(gen));
        }
    }
}