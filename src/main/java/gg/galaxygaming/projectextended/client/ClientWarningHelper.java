package gg.galaxygaming.projectextended.client;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.ProjectExtendedLang;
import gg.galaxygaming.projectextended.common.ProjectExtendedTags;
import moze_intel.projecte.gameObjs.container.CondenserContainer;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.utils.text.ILangEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ProjectExtended.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientWarningHelper {

    @SubscribeEvent
    public static void tooltipEvent(ItemTooltipEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player != null) {
            if (isCondenser(player.openContainer)) {
                addTooltip(event, ProjectExtendedTags.Items.BLACKLIST_CONDENSER, ProjectExtendedLang.WARNING_BLACKLIST_CONDENSER);
            } else if (isTransmutation(player.openContainer)) {
                addTooltip(event, ProjectExtendedTags.Items.BLACKLIST_LEARNING, ProjectExtendedLang.WARNING_BLACKLIST_TRANSMUTATION);
            }
        }
    }

    private static boolean isCondenser(Container openContainer) {
        return openContainer instanceof CondenserContainer;
    }

    private static boolean isTransmutation(Container openContainer) {
        return openContainer instanceof TransmutationContainer;
    }

    private static void addTooltip(ItemTooltipEvent event, ITag<Item> blacklistTag, ILangEntry langEntry) {
        if (event.getItemStack().getItem().isIn(blacklistTag)) {
            event.getToolTip().add(langEntry.translateColored(TextFormatting.YELLOW));
        }
    }
}