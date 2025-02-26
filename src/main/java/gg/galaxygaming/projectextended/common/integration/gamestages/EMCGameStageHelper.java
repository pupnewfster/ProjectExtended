package gg.galaxygaming.projectextended.common.integration.gamestages;

import com.google.common.collect.Sets;
import gg.galaxygaming.projectextended.common.BlacklistType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import moze_intel.projecte.api.ItemInfo;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.world.entity.player.Player;

public class EMCGameStageHelper {

    private EMCGameStageHelper() {
    }

    public static boolean isBlacklisted(Player player, ItemInfo reducedInfo, BlacklistType blacklistType) {
        Map<ItemInfo, Set<String>> blacklist = BlacklistManager.INSTANCE.getBlacklists(blacklistType);
        for (String stage : getRequiredStages(blacklist, reducedInfo)) {
            if (!GameStageHelper.hasStage(player, stage)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getMissingStages(Player player, ItemInfo reducedInfo, BlacklistType blacklistType) {
        Map<ItemInfo, Set<String>> blacklist = BlacklistManager.INSTANCE.getBlacklists(blacklistType);
        Set<String> requiredStages = getRequiredStages(blacklist, reducedInfo);
        if (requiredStages.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>(requiredStages.size());
        for (String stage : requiredStages) {
            if (!GameStageHelper.hasStage(player, stage)) {
                list.add(stage);
            }
        }
        Collections.sort(list);
        return list;
    }

    private static Set<String> getRequiredStages(Map<ItemInfo, Set<String>> itemToGameStage, ItemInfo itemInfo) {
        if (itemToGameStage.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> stages = itemToGameStage.getOrDefault(itemInfo, Collections.emptySet());
        if (itemInfo.hasModifiedComponents()) {
            //Check if there is a restriction that has no NBT involved
            Set<String> baseStages = itemToGameStage.getOrDefault(itemInfo.itemOnly(), Collections.emptySet());
            if (stages.isEmpty()) {
                //No stages, so we only care about the base stages
                return baseStages;
            } else if (!baseStages.isEmpty()) {
                //We have both stages and base stages, we need to merge them in our returned result
                //Note: This is not likely to happen often but in cases it does, we want to filter out
                // any duplicates (even though they are also rather unlikely) so that we don't have to
                // in order to display tooltips in cases when we aren't combining things
                return Sets.union(stages, baseStages);
            }
        }
        return stages;
    }
}