package gg.galaxygaming.projectextended.client.lang;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.ProjectExtendedLang;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedEntityTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.DyeColor;
import net.minecraft.util.IItemProvider;

public class ProjectExtendedLangProvider extends BaseLanguageProvider {

    public ProjectExtendedLangProvider(DataGenerator gen) {
        super(gen, ProjectExtended.MODID);
    }

    @Override
    protected void addTranslations() {
        addEntityTypes();
        addItems();
        addModes();
        //Misc stuff
        add(ProjectExtendedLang.PROJECT_EXTENDED, ProjectExtended.MOD_NAME);
    }

    private void addEntityTypes() {
        add(ProjectExtendedEntityTypes.PE_TRIDENT, "Matter Trident");
    }

    private void addItems() {
        addShield(ProjectExtendedItems.DARK_MATTER_SHIELD, "Dark Matter Shield");
        addShield(ProjectExtendedItems.RED_MATTER_SHIELD, "Red Matter Shield");
        addItem(ProjectExtendedItems.DARK_MATTER_TRIDENT, "Dark Matter Trident");
        addItem(ProjectExtendedItems.RED_MATTER_TRIDENT, "Red Matter Trident");
    }

    private void addShield(IItemProvider shield, String name) {
        String baseTranslationKey = shield.asItem().getTranslationKey();
        add(baseTranslationKey, name);
        addShield(baseTranslationKey, DyeColor.BLACK, "Black", name);
        addShield(baseTranslationKey, DyeColor.RED, "Red", name);
        addShield(baseTranslationKey, DyeColor.GREEN, "Green", name);
        addShield(baseTranslationKey, DyeColor.BLUE, "Blue", name);
        addShield(baseTranslationKey, DyeColor.BROWN, "Brown", name);
        addShield(baseTranslationKey, DyeColor.PURPLE, "Purple", name);
        addShield(baseTranslationKey, DyeColor.CYAN, "Cyan", name);
        addShield(baseTranslationKey, DyeColor.LIGHT_GRAY, "Light Gray", name);
        addShield(baseTranslationKey, DyeColor.GRAY, "Gray", name);
        addShield(baseTranslationKey, DyeColor.PINK, "Pink", name);
        addShield(baseTranslationKey, DyeColor.LIME, "Lime", name);
        addShield(baseTranslationKey, DyeColor.YELLOW, "Yellow", name);
        addShield(baseTranslationKey, DyeColor.LIGHT_BLUE, "Light Blue", name);
        addShield(baseTranslationKey, DyeColor.MAGENTA, "Magenta", name);
        addShield(baseTranslationKey, DyeColor.ORANGE, "Orange", name);
        addShield(baseTranslationKey, DyeColor.WHITE, "White", name);
    }

    private void addShield(String baseTranslationKey, DyeColor color, String colorName, String name) {
        add(baseTranslationKey + "." + color, colorName + " " + name);
    }

    private void addModes() {
        add(ProjectExtendedLang.MODE_TRIDENT_1, "Normal");
        add(ProjectExtendedLang.MODE_TRIDENT_2, "Channeling");
        add(ProjectExtendedLang.MODE_TRIDENT_3, "Riptide");
        add(ProjectExtendedLang.MODE_TRIDENT_4, "Shockwave");
    }
}