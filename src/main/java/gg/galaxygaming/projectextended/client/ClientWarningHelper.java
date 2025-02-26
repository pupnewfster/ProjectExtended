package gg.galaxygaming.projectextended.client;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.BlacklistType;
import moze_intel.projecte.gameObjs.container.CondenserContainer;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = ProjectExtended.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class ClientWarningHelper {

    @SubscribeEvent
    public static void tooltipEvent(ItemTooltipEvent event) {
        Player player = event.getEntity();
        if (player != null) {
            BlacklistType blacklistType;
            if (player.containerMenu instanceof CondenserContainer) {
                blacklistType = BlacklistType.CONDENSER;
            } else if (player.containerMenu instanceof TransmutationContainer) {
                blacklistType = BlacklistType.LEARNING;
            } else {
                return;
            }
            blacklistType.addBlacklistWarnings(player, event.getItemStack(), event.getToolTip());
        }
    }
}