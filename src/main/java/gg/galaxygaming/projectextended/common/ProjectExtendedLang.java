package gg.galaxygaming.projectextended.common;

import gg.galaxygaming.projectextended.ProjectExtended;
import moze_intel.projecte.utils.text.ILangEntry;
import net.minecraft.Util;

public enum ProjectExtendedLang implements ILangEntry {
    PROJECT_EXTENDED("misc", "mod_name"),
    PACK_DESCRIPTION("misc", "pack_description"),
    LIST_ELEMENT("misc", "list_element"),
    WARNING_BLACKLIST_CONDENSER("warning", "blacklist.condenser"),
    WARNING_BLACKLIST_CONDENSER_STAGES("warning", "blacklist.condenser.stage"),
    WARNING_BLACKLIST_TRANSMUTATION("warning", "blacklist.transmutation"),
    WARNING_BLACKLIST_TRANSMUTATION_STAGES("warning", "blacklist.transmutation.stage"),
    TRIDENT_MODE_NORMAL("mode", "trident.normal"),
    TRIDENT_MODE_CHANNELING("mode", "trident.channeling"),
    TRIDENT_MODE_RIPTIDE("mode", "trident.riptide"),
    TRIDENT_MODE_SHOCKWAVE("mode", "trident.shockwave"),
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