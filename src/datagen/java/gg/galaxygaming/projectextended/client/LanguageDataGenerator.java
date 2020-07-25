package gg.galaxygaming.projectextended.client;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.items.ProjectExtendedItems;
import java.util.function.Supplier;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraftforge.common.data.LanguageProvider;

public class LanguageDataGenerator extends LanguageProvider {

    public LanguageDataGenerator(DataGenerator gen) {
        super(gen, ProjectExtended.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addShield(ProjectExtendedItems.DARK_MATTER_SHIELD, "Dark Matter Shield");
        addShield(ProjectExtendedItems.RED_MATTER_SHIELD, "Red Matter Shield");

        addItem(ProjectExtendedItems.DARK_MATTER_TRIDENT, "Dark Matter Trident");
        addItem(ProjectExtendedItems.RED_MATTER_TRIDENT, "Red Matter Trident");
        //Modes
        add("projectextended.trident.normal", "Normal");
        add("projectextended.trident.channeling", "Channeling");
        add("projectextended.trident.riptide", "Riptide");
        add("projectextended.trident.shockwave", "Shockwave");
        add("itemGroup.projectextended", ProjectExtended.MOD_NAME);
    }

    private void addShield(Supplier<? extends Item> shield, String name) {
        String baseTranslationKey = shield.get().getTranslationKey();
        add(baseTranslationKey, name);
        add(baseTranslationKey + ".black", "Black " + name);
        add(baseTranslationKey + ".red", "Red " + name);
        add(baseTranslationKey + ".green", "Green " + name);
        add(baseTranslationKey + ".blue", "Blue " + name);
        add(baseTranslationKey + ".brown", "Brown " + name);
        add(baseTranslationKey + ".purple", "Purple " + name);
        add(baseTranslationKey + ".cyan", "Cyan " + name);
        add(baseTranslationKey + ".light_gray", "Light Gray " + name);
        add(baseTranslationKey + ".gray", "Gray " + name);
        add(baseTranslationKey + ".pink", "Pink " + name);
        add(baseTranslationKey + ".lime", "Lime " + name);
        add(baseTranslationKey + ".yellow", "Yellow " + name);
        add(baseTranslationKey + ".light_blue", "Light Blue " + name);
        add(baseTranslationKey + ".magenta", "Magenta " + name);
        add(baseTranslationKey + ".orange", "Orange " + name);
        add(baseTranslationKey + ".white", "White " + name);
    }
}