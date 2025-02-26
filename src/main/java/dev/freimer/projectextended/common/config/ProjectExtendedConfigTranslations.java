package dev.freimer.projectextended.common.config;

import dev.freimer.projectextended.ProjectExtended;
import moze_intel.projecte.config.IConfigTranslation;
import net.minecraft.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ProjectExtendedConfigTranslations implements IConfigTranslation {
    SERVER_SHOW_MISSING_STAGES("server.show_missing.stages", "Show missing Game Stages",
          "Set to false to not display missing Game Stages in tooltips when a player cannot learn or condense an item."),
    ;

    private final String key;
    private final String title;
    private final String tooltip;
    @Nullable
    private final String button;

    ProjectExtendedConfigTranslations(String path, String title, String tooltip) {
        this(path, title, tooltip, false);
    }

    ProjectExtendedConfigTranslations(String path, String title, String tooltip, boolean isSection) {
        this(path, title, tooltip, IConfigTranslation.getSectionTitle(title, isSection));
    }

    ProjectExtendedConfigTranslations(String path, String title, String tooltip, @Nullable String button) {
        this.key = Util.makeDescriptionId("configuration", ProjectExtended.rl(path));
        this.title = title;
        this.tooltip = tooltip;
        this.button = button;
    }

    @NotNull
    @Override
    public String getTranslationKey() {
        return key;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public String tooltip() {
        return tooltip;
    }

    @Nullable
    @Override
    public String button() {
        return button;
    }
}