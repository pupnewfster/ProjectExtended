package gg.galaxygaming.projectextended.common;

import gg.galaxygaming.projectextended.ProjectExtended;
import moze_intel.projecte.utils.text.ILangEntry;
import net.minecraft.Util;

public enum ProjectExtendedLang implements ILangEntry {
    PROJECT_EXTENDED("misc", "mod_name"),
    WARNING_BLACKLIST_CONDENSER("warning", "blacklist.condenser"),
    WARNING_BLACKLIST_TRANSMUTATION("warning", "blacklist.transmutation"),
    MODE_TRIDENT_1("mode", "trident.1"),
    MODE_TRIDENT_2("mode", "trident.2"),
    MODE_TRIDENT_3("mode", "trident.3"),
    MODE_TRIDENT_4("mode", "trident.4"),
    ADVANCEMENTS_ALCHEMICAL_BARREL("advancements", "alchemical_barrel"),
    ADVANCEMENTS_ALCHEMICAL_BARREL_DESCRIPTION("advancements", "alchemical_barrel.description"),
    ;

    private final String key;

    ProjectExtendedLang(String type, String path) {
        this(Util.makeDescriptionId(type, ProjectExtended.rl(path)));
    }

    ProjectExtendedLang(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }
}