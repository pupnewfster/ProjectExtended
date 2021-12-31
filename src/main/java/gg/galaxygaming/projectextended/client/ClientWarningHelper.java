package gg.galaxygaming.projectextended.client;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.ProjectExtendedLang;
import gg.galaxygaming.projectextended.common.ProjectExtendedTags;
import moze_intel.projecte.gameObjs.container.CondenserContainer;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.utils.text.ILangEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ProjectExtended.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientWarningHelper {

    @SubscribeEvent
    public static void tooltipEvent(ItemTooltipEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            if (isCondenser(player.containerMenu)) {
                addTooltip(event, ProjectExtendedTags.Items.BLACKLIST_CONDENSER, ProjectExtendedLang.WARNING_BLACKLIST_CONDENSER);
            } else if (isTransmutation(player.containerMenu)) {
                addTooltip(event, ProjectExtendedTags.Items.BLACKLIST_LEARNING, ProjectExtendedLang.WARNING_BLACKLIST_TRANSMUTATION);
            }
        }
    }

    private static boolean isCondenser(AbstractContainerMenu openContainer) {
        return openContainer instanceof CondenserContainer;
    }

    private static boolean isTransmutation(AbstractContainerMenu openContainer) {
        return openContainer instanceof TransmutationContainer;
    }

    private static void addTooltip(ItemTooltipEvent event, Tag<Item> blacklistTag, ILangEntry langEntry) {
        if (blacklistTag.contains(event.getItemStack().getItem())) {
            event.getToolTip().add(langEntry.translateColored(ChatFormatting.YELLOW));
        }
    }
}