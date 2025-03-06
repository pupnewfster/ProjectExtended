package dev.freimer.projectextended.common.integration.gamestages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.freimer.projectextended.ProjectExtended;
import dev.freimer.projectextended.common.BlacklistType;
import dev.freimer.projectextended.common.network.to_client.PacketSyncBlacklist;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlacklistManager extends SimplePreparableReloadListener<Map<@Nullable BlacklistType, List<JsonElement>>> {

    public static final BlacklistManager INSTANCE = new BlacklistManager();

    /**
     * @apiNote DO NOT MODIFY.
     */
    private static final BlacklistType[] BLACKLIST_TYPES = BlacklistType.values();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final ResourceLocation GLOBAL_FILE = ProjectExtended.rl("global_blacklist.json");
    private static final Codec<Map<String, List<NSSItem>>> BLACKLIST_CODEC = Codec.unboundedMap(
          ExtraCodecs.NON_EMPTY_STRING,
          ExtraCodecs.nonEmptyList(NSSItem.CODEC.codec().listOf())
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Map<ItemInfo, Set<String>>> BLACKLIST_STREAM_CODEC = ByteBufCodecs.map(
          HashMap::new,
          ItemInfo.STREAM_CODEC,
          ByteBufCodecs.collection(HashSet::new, ByteBufCodecs.STRING_UTF8)
    );

    private Map<BlacklistType, Map<ItemInfo, Set<String>>> blacklists = Collections.emptyMap();

    private BlacklistManager() {
    }

    @NotNull
    @Override
    protected Map<@Nullable BlacklistType, List<JsonElement>> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        //noinspection MapReplaceableByEnumMap - We have null keys
        Map<@Nullable BlacklistType, List<JsonElement>> map = new Object2ObjectArrayMap<>(BLACKLIST_TYPES.length + 1);
        for (BlacklistType blacklistType : BLACKLIST_TYPES) {
            map.put(blacklistType, scanResources(resourceManager, blacklistType));
        }
        map.put(null, scanResources(resourceManager, null));
        return map;
    }

    private List<JsonElement> scanResources(@NotNull ResourceManager resourceManager, @Nullable BlacklistType blacklistType) {
        List<JsonElement> elements = new ArrayList<>();
        for (Resource resource : resourceManager.getResourceStack(blacklistType == null ? GLOBAL_FILE : blacklistType.getBlacklistFile())) {
            try (Reader reader = resource.openAsReader()) {
                elements.add(GsonHelper.fromJson(GSON, reader, JsonElement.class));
            } catch (IllegalArgumentException | IOException | JsonParseException e) {
                ProjectExtended.LOGGER.error("Could not load resource {}", blacklistType == null ? GLOBAL_FILE : blacklistType.getBlacklistFile(), e);
            }
        }
        return elements;
    }

    @Override
    protected void apply(@NotNull Map<@Nullable BlacklistType, List<JsonElement>> elements, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        RegistryOps<JsonElement> serializationContext = getRegistryLookup().createSerializationContext(JsonOps.INSTANCE);
        Map<BlacklistType, Map<ItemInfo, Set<String>>> blacklists = new EnumMap<>(BlacklistType.class);
        TriConsumer<@Nullable BlacklistType, NormalizedSimpleStack, String> nssConsumer = (type, nss, stage) -> {
            if (nss instanceof NSSItem asItem) {
                ItemInfo itemInfo = ItemInfo.fromNSS(asItem);
                if (itemInfo != null) {//Validate it isn't a tag
                    if (type == null) {
                        for (BlacklistType blacklistType : BLACKLIST_TYPES) {
                            blacklist(blacklists, blacklistType, itemInfo, stage);
                        }
                    } else {
                        blacklist(blacklists, type, itemInfo, stage);
                    }
                }
            }
        };
        for (Map.Entry<@Nullable BlacklistType, List<JsonElement>> entry : elements.entrySet()) {
            @Nullable
            BlacklistType blacklistType = entry.getKey();
            String description = (blacklistType == null ? "global" : blacklistType.getName()) + " blacklist";
            for (JsonElement jsonElement : entry.getValue()) {
                DataResult<Map<String, List<NSSItem>>> result = BLACKLIST_CODEC.parse(serializationContext, jsonElement);
                if (result.isError()) {
                    PECore.LOGGER.error("Couldn't parse {}: {}", description, result.error().orElseThrow().message());
                    continue;
                }
                for (Map.Entry<String, List<NSSItem>> blacklistEntry : result.getOrThrow().entrySet()) {
                    for (NSSItem nssItem : blacklistEntry.getValue()) {
                        nssItem.forSelfAndEachElement(blacklistType, blacklistEntry.getKey(), nssConsumer);
                    }
                }
            }
        }
        //TODO - 1.21: Make it immutable?? And if we don't have anything blacklisted leave it as an empty map?
        this.blacklists = blacklists;
    }

    private void blacklist(Map<BlacklistType, Map<ItemInfo, Set<String>>> blacklists, BlacklistType blacklistType, ItemInfo itemInfo, String stage) {
        Map<ItemInfo, Set<String>> itemToGameStage = blacklists.computeIfAbsent(blacklistType, type -> new HashMap<>());
        if (!itemToGameStage.computeIfAbsent(itemInfo, item -> new HashSet<>()).add(stage)) {
            ProjectExtended.LOGGER.warn("Item: {} has duplicate blacklists for stage: {}", itemInfo, stage);
        }
    }

    //TODO - 1.21: Re-evaluate these methods
    @Internal
    public void handleSyncPacket(Map<BlacklistType, Map<ItemInfo, Set<String>>> blacklists) {
        this.blacklists = blacklists;
    }

    @Internal
    public static PacketSyncBlacklist syncPacket() {
        return new PacketSyncBlacklist(INSTANCE.blacklists);
    }

    @Internal
    public static void clearBlacklist() {
        INSTANCE.blacklists.clear();
    }

    public Map<ItemInfo, Set<String>> getBlacklists(BlacklistType blacklistType) {
        return blacklists.getOrDefault(blacklistType, Collections.emptyMap());
    }
}