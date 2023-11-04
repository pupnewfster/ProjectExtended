package gg.galaxygaming.projectextended.common.integration;

import net.minecraftforge.fml.ModList;

public class ProjectExtendedHooks {

    private ProjectExtendedHooks() {
    }

    public static final String GAMESTAGES_ID = "gamestages";

    public static boolean gameStagesLoaded;

    public static void hookCommon() {
        gameStagesLoaded = ModList.get().isLoaded(GAMESTAGES_ID);
    }
}