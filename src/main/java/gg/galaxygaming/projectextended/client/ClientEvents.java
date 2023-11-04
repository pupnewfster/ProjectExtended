package gg.galaxygaming.projectextended.client;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.integration.ProjectExtendedHooks;
import gg.galaxygaming.projectextended.common.integration.gamestages.EMCGameStageHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ProjectExtended.MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        //Note: The player is null on integrated server startup
        if (event.getPlayer() != null && ProjectExtendedHooks.gameStagesLoaded) {
            EMCGameStageHelper.clearBlacklist();
        }
    }
}