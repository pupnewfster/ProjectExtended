package dev.freimer.projectextended.client;

import dev.freimer.projectextended.ProjectExtended;
import dev.freimer.projectextended.common.integration.ProjectExtendedHooks;
import dev.freimer.projectextended.common.integration.gamestages.BlacklistManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = ProjectExtended.MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        //Note: The player is null on integrated server startup
        if (event.getPlayer() != null && ProjectExtendedHooks.gameStagesLoaded) {
            BlacklistManager.clearBlacklist();
        }
    }
}