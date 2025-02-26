package dev.freimer.projectextended.client;

import dev.freimer.projectextended.ProjectExtended;
import dev.freimer.projectextended.common.BlacklistType;
import dev.freimer.projectextended.common.integration.ProjectExtendedHooks;
import dev.freimer.projectextended.common.integration.gamestages.BlacklistManager;
import moze_intel.projecte.gameObjs.container.CondenserContainer;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = ProjectExtended.MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        //Note: The player is null on integrated server startup
        if (event.getPlayer() != null && ProjectExtendedHooks.gameStagesLoaded) {
            BlacklistManager.clearBlacklist();
        }
    }

    @SubscribeEvent
    public static void tooltipEvent(ItemTooltipEvent event) {
        Player player = event.getEntity();
        if (player != null) {
            BlacklistType blacklistType = switch (player.containerMenu) {
                case CondenserContainer condenserContainer -> BlacklistType.CONDENSER;
                case TransmutationContainer transmutationContainer -> BlacklistType.LEARNING;
                default -> null;
            };
            if (blacklistType != null) {
                blacklistType.addBlacklistWarnings(player, event.getItemStack(), event.getToolTip());
            }
        }
    }
}