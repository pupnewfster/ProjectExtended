package gg.galaxygaming.projectextended.client.lang;

import gg.galaxygaming.projectextended.client.lang.FormatSplitter.Component;
import java.util.List;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

/**
 * @apiNote From Mekanism
 */
public abstract class ConvertibleLanguageProvider extends LanguageProvider {

    public ConvertibleLanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    public abstract void convert(String key, List<Component> splitEnglish);

    @Override
    protected void addTranslations() {
    }
}