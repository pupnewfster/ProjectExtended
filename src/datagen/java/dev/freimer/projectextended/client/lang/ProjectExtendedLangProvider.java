package dev.freimer.projectextended.client.lang;

import dev.freimer.projectextended.ProjectExtended;
import dev.freimer.projectextended.common.ProjectExtendedLang;
import dev.freimer.projectextended.common.config.ProjectExtendedConfig;
import dev.freimer.projectextended.common.config.ProjectExtendedConfigTranslations;
import dev.freimer.projectextended.common.registries.ProjectExtendedBlocks;
import dev.freimer.projectextended.common.registries.ProjectExtendedEntityTypes;
import dev.freimer.projectextended.common.registries.ProjectExtendedItems;
import moze_intel.projecte.PECore;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

public class ProjectExtendedLangProvider extends BaseLanguageProvider {

    public ProjectExtendedLangProvider(PackOutput output) {
        super(output, ProjectExtended.MODID, ProjectExtended.MOD_NAME);
    }

    @Override
    protected void addTranslations() {
        addConfigs();
        addBlocks();
        addEntityTypes();
        addItems();
        addModes();
        //Misc stuff
        addPackData(ProjectExtendedLang.PROJECT_EXTENDED, ProjectExtendedLang.PACK_DESCRIPTION);
        addModInfo(modName + " is a mod that adds features to " + PECore.MODNAME + " that EE2 would probably have had it been made in modern times.");
        add(ProjectExtendedLang.LIST_ELEMENT, " - %1$s");

        add(ProjectExtendedLang.WARNING_BLACKLIST_CONDENSER, "WARNING: This item is blacklisted from the condenser. It cannot be used as the target lock, but can be used for EMC.");
        add(ProjectExtendedLang.WARNING_BLACKLIST_CONDENSER_STAGES, "Missing the following Game Stages in order to use this item as a target lock:");
        add(ProjectExtendedLang.WARNING_BLACKLIST_TRANSMUTATION, "WARNING: This item is blacklisted from the transmutation table. It cannot be learned, but can be consumed for EMC.");
        add(ProjectExtendedLang.WARNING_BLACKLIST_TRANSMUTATION_STAGES, "Missing the following Game Stages in order to learn this item:");

        add(ProjectExtendedLang.ADVANCEMENTS_ALCHEMICAL_BARREL, "Barrelled Storage!");
        add(ProjectExtendedLang.ADVANCEMENTS_ALCHEMICAL_BARREL_DESCRIPTION, "A \"little\" barrel upgrade.");
    }

    private void addConfigs() {
        addConfigs(ProjectExtendedConfig.getConfigs());
        addConfigs(ProjectExtendedConfigTranslations.values());
    }

    private void addBlocks() {
        add(ProjectExtendedBlocks.ALCHEMICAL_BARREL, "Alchemical Barrel");
        add(ProjectExtendedBlocks.INTERDICTION_LANTERN, "Interdiction Lantern");
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

    private void addShield(Holder<Item> shield, String name) {
        String baseTranslationKey = shield.value().getDescriptionId();
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
        add(ProjectExtendedLang.TRIDENT_MODE_NORMAL, "Normal");
        add(ProjectExtendedLang.TRIDENT_MODE_CHANNELING, "Channeling");
        add(ProjectExtendedLang.TRIDENT_MODE_RIPTIDE, "Riptide");
        add(ProjectExtendedLang.TRIDENT_MODE_SHOCKWAVE, "Shockwave");
    }
}