package dev.freimer.projectextended.common;

import dev.freimer.projectextended.ProjectExtended;
import dev.freimer.projectextended.common.config.ProjectExtendedConfig;
import dev.freimer.projectextended.common.integration.ProjectExtendedHooks;
import dev.freimer.projectextended.common.integration.gamestages.EMCGameStageHelper;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.IntFunction;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.utils.text.ILangEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public enum BlacklistType {
    CONDENSER("condenser", ProjectExtendedTags.Items.BLACKLIST_CONDENSER, ProjectExtendedLang.WARNING_BLACKLIST_CONDENSER,
          ProjectExtendedLang.WARNING_BLACKLIST_CONDENSER_STAGES),
    LEARNING("learning", ProjectExtendedTags.Items.BLACKLIST_LEARNING, ProjectExtendedLang.WARNING_BLACKLIST_TRANSMUTATION,
          ProjectExtendedLang.WARNING_BLACKLIST_TRANSMUTATION_STAGES);

    //TODO - 1.21: Remove
    //public static final Codec<BlacklistType> CODEC = StringRepresentable.fromEnum(BlacklistType::values);
    public static final IntFunction<BlacklistType> BY_ID = ByIdMap.continuous(BlacklistType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, BlacklistType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, BlacklistType::ordinal);

    private final ResourceLocation blacklistFile;
    private final TagKey<Item> blacklist;
    private final ILangEntry warning, gameStageWarning;
    private final String name;

    BlacklistType(String name, TagKey<Item> blacklist, ILangEntry warning, ILangEntry gameStageWarning) {
        this.name = name;
        this.blacklistFile = ProjectExtended.rl(name + "_blacklist.json");
        this.blacklist = blacklist;
        this.warning = warning;
        this.gameStageWarning = gameStageWarning;
    }

    public ResourceLocation getBlacklistFile() {
        return blacklistFile;
    }

    public String getName() {
        return name;
    }

    public TagKey<Item> getBlacklist() {
        return blacklist;
    }

    private Component getWarning() {
        return warning.translateColored(ChatFormatting.YELLOW);
    }

    public boolean isBlacklisted(Player player, ItemInfo sourceInfo, ItemInfo reducedInfo) {
        if (reducedInfo.getItem().is(blacklist)) {
            return true;
        } else if (ProjectExtendedHooks.gameStagesLoaded) {
            //TODO: Eventually maybe we want to use source info to try and support more arbitrary parts of NBT
            return EMCGameStageHelper.isBlacklisted(player, reducedInfo, this);
        }
        return false;
    }

    public void addBlacklistWarnings(Player player, ItemStack stack, List<Component> tooltips) {
        if (stack.is(blacklist)) {
            tooltips.add(getWarning());
        } else if (ProjectExtendedHooks.gameStagesLoaded) {
            //TODO: Eventually maybe we want to use source info to try and support more arbitrary parts of NBT
            ItemInfo sourceInfo = ItemInfo.fromStack(stack);
            ItemInfo reducedInfo = IEMCProxy.INSTANCE.getPersistentInfo(sourceInfo);
            List<String> missingStages = EMCGameStageHelper.getMissingStages(player, reducedInfo, this);
            if (!missingStages.isEmpty()) {
                tooltips.add(getWarning());
                if (ProjectExtendedConfig.server.showMissingGameStages.get()) {
                    tooltips.add(gameStageWarning.translateColored(ChatFormatting.YELLOW));
                    for (String missingStage : missingStages) {
                        tooltips.add(ProjectExtendedLang.LIST_ELEMENT.translateColored(ChatFormatting.YELLOW, missingStage));
                    }
                }
            }
        }
    }
}