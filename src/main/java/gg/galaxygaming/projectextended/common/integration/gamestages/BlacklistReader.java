package gg.galaxygaming.projectextended.common.integration.gamestages;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.BlacklistType;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.nss.NSSItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public class BlacklistReader {

    /**
     * @apiNote DO NOT MODIFY.
     */
    private static final BlacklistType[] BLACKLIST_TYPES = BlacklistType.values();
    private static final ResourceLocation GLOBAL_FILE = ProjectExtended.rl("global_blacklist.json");

    private static DataResult<CompoundTag> readNbt(String nbtAsString) {
        try {
            return DataResult.success(TagParser.parseTag(nbtAsString));
        } catch (CommandSyntaxException e) {
            return DataResult.error(() -> "Not valid nbt: " + nbtAsString + " " + e.getMessage());
        }
    }

    private static DataResult<Item> readItem(String itemName) {
        return ResourceLocation.read(itemName)
              .flatMap(registryName -> BuiltInRegistries.ITEM.getOptional(registryName)
                    .map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "Unknown item: " + registryName))
              );
    }

    //TODO - 1.20.2+: Debate replacing this with either and alternative? And then avoid creating the NSSItem variant at all (especially for tags)
    private static final Codec<NSSItem> NSS_ITEM_CODEC = Codec.STRING.comapFlatMap(itemName -> {
        if (itemName.startsWith("#")) {
            return ResourceLocation.read(itemName.substring(1))
                  .map(NSSItem::createTag);
        }
        int nbtStart = itemName.indexOf('{');
        if (nbtStart == -1) {
            return readItem(itemName)
                  .map(NSSItem::createItem);
        }
        return readItem(itemName.substring(0, nbtStart))
              .apply2(NSSItem::createItem, readNbt(itemName.substring(nbtStart)));
    }, NSSItem::json);

    private static final Codec<Map<String, List<NSSItem>>> BLACKLIST_CODEC = Codec.unboundedMap(
          Codec.STRING,
          ExtraCodecs.nonEmptyList(NSS_ITEM_CODEC.listOf())
    );

    public static void readDataPacks(ResourceManager resourceManager) {
        //Start by clearing the old blacklist
        EMCGameStageHelper.clearBlacklist();
        for (BlacklistType blacklistType : BLACKLIST_TYPES) {
            readResources(resourceManager.getResourceStack(blacklistType.getBlacklistFile()), blacklistType);
        }
        readResources(resourceManager.getResourceStack(GLOBAL_FILE), null);
    }

    private static void readResources(List<Resource> resources, @Nullable BlacklistType blacklistType) {
        //Iterate through all copies of this blacklist, from lowest to highest priority datapack, merging the results together
        for (Resource resource : resources) {
            try (Reader reader = resource.openAsReader()) {
                JsonElement element = JsonParser.parseReader(reader);
                Map<String, List<NSSItem>> blacklistInfo = BLACKLIST_CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, element))
                      .getOrThrow(false, ProjectExtended.LOGGER::error);
                blacklistInfo.forEach((stage, blacklist) -> {
                    for (NSSItem nssItem : blacklist) {
                        if (nssItem.representsTag()) {
                            nssItem.forEachElement(nss -> {
                                if (nss instanceof NSSItem asItem) {
                                    blacklist(blacklistType, asItem, stage);
                                }
                            });
                        } else {
                            blacklist(blacklistType, nssItem, stage);
                        }
                    }
                });
            } catch (IOException e) {
                ProjectExtended.LOGGER.error("Could not load resource {}", blacklistType == null ? GLOBAL_FILE : blacklistType.getBlacklistFile(), e);
            } catch (JsonParseException e) {
                ProjectExtended.LOGGER.error("Malformed JSON in file: {}", blacklistType == null ? GLOBAL_FILE : blacklistType.getBlacklistFile(), e);
            }
        }
    }

    private static void blacklist(@Nullable BlacklistType blacklistType, NSSItem nssItem, String stage) {
        ItemInfo itemInfo = ItemInfo.fromNSS(nssItem);
        if (itemInfo != null) {//This should never be null
            blacklist(blacklistType, itemInfo, stage);
        }
    }

    private static void blacklist(@Nullable BlacklistType blacklistType, ItemInfo itemInfo, String stage) {
        if (blacklistType == null) {
            for (BlacklistType type : BLACKLIST_TYPES) {
                EMCGameStageHelper.blacklist(type, itemInfo, stage);
            }
        } else {
            EMCGameStageHelper.blacklist(blacklistType, itemInfo, stage);
        }
    }
}