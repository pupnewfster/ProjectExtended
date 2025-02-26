package dev.freimer.projectextended.common.integration;

import java.util.function.Predicate;
import net.neoforged.fml.ModList;

public class ProjectExtendedHooks {

    public static final String GAMESTAGES_ID = "gamestages";

    public static boolean gameStagesLoaded;

    public static void checkModsLoaded() {
        ModList modList = ModList.get();
        //Note: The modlist is null when running tests
        Predicate<String> loadedCheck = modList == null ? modid -> false : modList::isLoaded;
        gameStagesLoaded = loadedCheck.test(GAMESTAGES_ID);
    }
}