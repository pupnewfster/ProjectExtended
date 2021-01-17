package gg.galaxygaming.projectextended.client.lang;

import gg.galaxygaming.projectextended.client.lang.FormatSplitter.Component;
import java.io.IOException;
import java.util.List;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraftforge.common.data.LanguageProvider;

/**
 * @apiNote From Mekanism
 */
public abstract class BaseLanguageProvider extends LanguageProvider {

    private final ConvertibleLanguageProvider[] altProviders;

    public BaseLanguageProvider(DataGenerator gen, String modid) {
        super(gen, modid, "en_us");
        altProviders = new ConvertibleLanguageProvider[]{
              new UpsideDownLanguageProvider(gen, modid)
        };
    }

    protected void add(IHasTranslationKey key, String value) {
        add(key.getTranslationKey(), value);
    }

    @Override
    public void add(String key, String value) {
        super.add(key, value);
        if (altProviders.length > 0) {
            List<Component> splitEnglish = FormatSplitter.split(value);
            for (ConvertibleLanguageProvider provider : altProviders) {
                provider.convert(key, splitEnglish);
            }
        }
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        super.act(cache);
        if (altProviders.length > 0) {
            for (ConvertibleLanguageProvider provider : altProviders) {
                provider.act(cache);
            }
        }
    }
}