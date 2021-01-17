package gg.galaxygaming.projectextended.common;

import gg.galaxygaming.projectextended.ProjectExtended;
import moze_intel.projecte.utils.text.ILangEntry;
import net.minecraft.util.Util;

public enum ProjectExtendedLang implements ILangEntry {
    PROJECT_EXTENDED("misc", "mod_name"),
    MODE_TRIDENT_1("mode", "trident.1"),
    MODE_TRIDENT_2("mode", "trident.2"),
    MODE_TRIDENT_3("mode", "trident.3"),
    MODE_TRIDENT_4("mode", "trident.4");

    private final String key;

    ProjectExtendedLang(String type, String path) {
        this(Util.makeTranslationKey(type, ProjectExtended.rl(path)));
    }

    ProjectExtendedLang(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }
}