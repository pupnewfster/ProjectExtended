package gg.galaxygaming.projectextended.common;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.config.ProjectExtendedConfig;
import gg.galaxygaming.projectextended.common.integration.ProjectExtendedHooks;
import gg.galaxygaming.projectextended.common.integration.gamestages.EMCGameStageHelper;
import java.util.List;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.utils.LazyTagLookup;
import moze_intel.projecte.utils.text.ILangEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public enum BlacklistType {
    CONDENSER("condenser", ProjectExtendedTags.Items.BLACKLIST_CONDENSER, ProjectExtendedLang.WARNING_BLACKLIST_CONDENSER,
          ProjectExtendedLang.WARNING_BLACKLIST_CONDENSER_STAGES),
    LEARNING("learning", ProjectExtendedTags.Items.BLACKLIST_LEARNING, ProjectExtendedLang.WARNING_BLACKLIST_TRANSMUTATION,
          ProjectExtendedLang.WARNING_BLACKLIST_TRANSMUTATION_STAGES);

    private final ResourceLocation blacklistFile;
    private final TagKey<Item> blacklist;
    private final LazyTagLookup<Item> lookup;
    private final ILangEntry warning, gameStageWarning;

    BlacklistType(String name, TagKey<Item> blacklist, ILangEntry warning, ILangEntry gameStageWarning) {
        this.blacklistFile = ProjectExtended.rl(name + "_blacklist.json");
        this.blacklist = blacklist;
        this.lookup = LazyTagLookup.create(ForgeRegistries.ITEMS, this.blacklist);
        this.warning = warning;
        this.gameStageWarning = gameStageWarning;
    }

    public ResourceLocation getBlacklistFile() {
        return blacklistFile;
    }

    public TagKey<Item> getBlacklist() {
        return blacklist;
    }

    private Component getWarning() {
        return warning.translateColored(ChatFormatting.YELLOW);
    }

    public boolean isBlacklisted(Player player, ItemInfo sourceInfo, ItemInfo reducedInfo) {
        if (lookup.contains(reducedInfo.getItem())) {
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