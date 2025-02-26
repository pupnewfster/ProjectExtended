package gg.galaxygaming.projectextended.client.lang;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.client.lang.FormatSplitter.Component;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.PECore;
import moze_intel.projecte.config.IConfigTranslation;
import moze_intel.projecte.config.IPEConfig;
import moze_intel.projecte.integration.recipe_viewer.alias.IAliasedTranslation;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.NotNull;

/**
 * @apiNote From Mekanism
 */
public abstract class BaseLanguageProvider extends LanguageProvider {

    private final ConvertibleLanguageProvider[] altProviders;
    protected final String modName;
    protected final String basicModName;
    private final String modid;

    public BaseLanguageProvider(PackOutput output, String modid, String modName) {
        super(output, modid, "en_us");
        this.modid = modid;
        this.modName = modName;
        this.basicModName = modName.replaceAll(":", "");
        altProviders = new ConvertibleLanguageProvider[]{
              new UpsideDownLanguageProvider(output, modid),
              new NonAmericanLanguageProvider(output, modid, "en_au"),
              new NonAmericanLanguageProvider(output, modid, "en_gb")
        };
    }

    protected void addPackData(IHasTranslationKey name, IHasTranslationKey packDescription) {
        add(name, modName);
        add(packDescription, "Resources used for " + modName);
    }

    protected void addModInfo(String description) {
        add("fml.menu.mods.info.description." + modid, description);
    }

    private String getConfigSectionTranslationPath(IPEConfig config) {
        String baseConfigFolder = PECore.MODNAME.toLowerCase(Locale.ROOT);
        String fileName = config.getFileName().replaceAll("[^a-zA-Z0-9]+", ".").toLowerCase(Locale.ROOT);
        return modid + ".configuration.section." + baseConfigFolder + "." + fileName + ".toml";
    }

    protected void addConfigs(Collection<IPEConfig> configs) {
        add(modid + ".configuration.title", modName + " Config");
        for (IPEConfig config : configs) {
            String key = getConfigSectionTranslationPath(config);
            add(key, config.getTranslation());
            add(key + ".title", modName + " - " + config.getTranslation());
        }
    }

    protected void addConfigs(IConfigTranslation... translations) {
        for (IConfigTranslation translation : translations) {
            add(translation, translation.title());
            add(translation.getTranslationKey() + ".tooltip", translation.tooltip());
            String button = translation.button();
            if (button != null) {
                add(translation.getTranslationKey() + ".button", button);
            }
        }
    }

    protected void addAliases(IAliasedTranslation... translations) {
        for (IAliasedTranslation translation : translations) {
            add(translation, translation.getAlias());
        }
    }

    protected void addAlias(String path, String translation) {
        add(Util.makeDescriptionId("alias", ResourceLocation.fromNamespaceAndPath(modid, path)), translation);
    }

    protected void add(IHasTranslationKey key, String value) {
        add(key.getTranslationKey(), value);
    }

    @Override
    public void add(@NotNull String key, @NotNull String value) {
        if (value.contains("%s")) {
            //throw new IllegalArgumentException("Values containing substitutions should use explicit numbered indices: " + key + " - " + value);
            //TODO - 1.21: Switch to exception
            ProjectExtended.LOGGER.error("Values containing substitutions should use explicit numbered indices: {} - {}", key, value);
        }
        super.add(key, value);
        if (altProviders.length > 0) {
            List<Component> splitEnglish = FormatSplitter.split(value);
            for (ConvertibleLanguageProvider provider : altProviders) {
                provider.convert(key, value, splitEnglish);
            }
        }
    }

    @NotNull
    @Override
    public CompletableFuture<?> run(@NotNull CachedOutput cache) {
        CompletableFuture<?> future = super.run(cache);
        if (altProviders.length > 0) {
            CompletableFuture<?>[] futures = new CompletableFuture[altProviders.length + 1];
            futures[0] = future;
            for (int i = 0; i < altProviders.length; i++) {
                futures[i + 1] = altProviders[i].run(cache);
            }
            return CompletableFuture.allOf(futures);
        }
        return future;
    }
}