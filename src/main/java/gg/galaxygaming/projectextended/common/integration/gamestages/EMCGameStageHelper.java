package gg.galaxygaming.projectextended.common.integration.gamestages;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.BlacklistType;
import gg.galaxygaming.projectextended.common.network.to_client.PacketSyncBlacklist;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import moze_intel.projecte.api.ItemInfo;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class EMCGameStageHelper {

    private EMCGameStageHelper() {
    }

    private static final Map<BlacklistType, GameStageBlacklist> BLACKLISTS = new EnumMap<>(BlacklistType.class);

    public static void handleSyncPacket(Map<BlacklistType, GameStageBlacklist> blacklists) {
        clearBlacklist();
        BLACKLISTS.putAll(blacklists);
    }

    public static PacketSyncBlacklist syncPacket() {
        return new PacketSyncBlacklist(BLACKLISTS);
    }

    public static void clearBlacklist() {
        BLACKLISTS.clear();
    }

    public static void blacklist(BlacklistType blacklistType, ItemInfo itemInfo, String stage) {
        BLACKLISTS.computeIfAbsent(blacklistType, type -> new GameStageBlacklist())
              .blacklist(itemInfo, stage);
    }

    public static boolean isBlacklisted(Player player, ItemInfo reducedInfo, BlacklistType blacklistType) {
        GameStageBlacklist blacklist = BLACKLISTS.get(blacklistType);
        return blacklist != null && !blacklist.getRequiredStages(reducedInfo)
              .allMatch(stage -> GameStageHelper.hasStage(player, stage));
    }

    public static List<String> getMissingStages(Player player, ItemInfo reducedInfo, BlacklistType blacklistType) {
        GameStageBlacklist blacklist = BLACKLISTS.get(blacklistType);
        if (blacklist == null) {
            return List.of();
        }
        return blacklist.getRequiredStages(reducedInfo)
              .filter(stage -> !GameStageHelper.hasStage(player, stage))
              .sorted()
              .toList();
    }

    public static class GameStageBlacklist {

        @Nullable
        public static GameStageBlacklist read(FriendlyByteBuf buffer) {
            Map<ItemInfo, Set<String>> itemToGameStage = buffer.readMap(buf -> ItemInfo.fromItem(buf.readRegistryIdSafe(Item.class), buf.readNbt()),
                  buf -> buf.readCollection(HashSet::new, FriendlyByteBuf::readUtf));
            if (itemToGameStage.isEmpty()) {
                return null;
            }
            GameStageBlacklist blacklist = new GameStageBlacklist();
            blacklist.itemToGameStage.putAll(itemToGameStage);
            return blacklist;
        }

        private final Map<ItemInfo, Set<String>> itemToGameStage = new HashMap<>();

        private GameStageBlacklist() {
        }

        void blacklist(ItemInfo itemInfo, String stage) {
            if (!itemToGameStage.computeIfAbsent(itemInfo, item -> new HashSet<>()).add(stage)) {
                ProjectExtended.LOGGER.warn("Item: {} has duplicate blacklists for stage: {}", itemInfo, stage);
            }
        }

        public void writeToBuffer(FriendlyByteBuf buffer) {
            buffer.writeMap(itemToGameStage, (buf, itemInfo) -> {
                buf.writeRegistryId(ForgeRegistries.ITEMS, itemInfo.getItem());
                buf.writeNbt(itemInfo.getNBT());
            }, (buf, val) -> buf.writeCollection(val, FriendlyByteBuf::writeUtf));
        }

        public Stream<String> getRequiredStages(ItemInfo itemInfo) {
            Set<String> stages = itemToGameStage.getOrDefault(itemInfo, Set.of());
            if (itemInfo.hasNBT()) {
                //Check if there is a restriction that has no NBT involved
                Set<String> baseStages = itemToGameStage.getOrDefault(ItemInfo.fromItem(itemInfo.getItem()), Set.of());
                if (stages.isEmpty()) {
                    //No stages, so we only care about the base stages
                    return baseStages.stream();
                } else if (!baseStages.isEmpty()) {
                    //We have both stages and base stages, we need to merge them in our returned result
                    //Note: This is not likely to happen often but in cases it does, we want to filter out
                    // any duplicates (even though they are also rather unlikely) so that we don't have to
                    // in order to display tooltips in cases when we aren't combining things
                    return Stream.concat(stages.stream(), baseStages.stream()).distinct();
                }
            }
            return stages.stream();
        }
    }
}