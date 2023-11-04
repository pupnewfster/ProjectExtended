package gg.galaxygaming.projectextended.common.network;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.network.to_client.PacketSyncBlacklist;
import java.util.Optional;
import java.util.function.Function;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

public final class PacketHandler {

	private static final String PROTOCOL_VERSION = Integer.toString(1);
	private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
			.named(ProjectExtended.rl("main_channel"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();
	private static int index;

	public static void register() {
		//Client to server messages

		//Server to client messages
		registerServerToClient(PacketSyncBlacklist.class, PacketSyncBlacklist::decode);
	}

	private static <MSG extends IPEPacket> void registerClientToServer(Class<MSG> type, Function<FriendlyByteBuf, MSG> decoder) {
		registerMessage(type, decoder, NetworkDirection.PLAY_TO_SERVER);
	}

	private static <MSG extends IPEPacket> void registerServerToClient(Class<MSG> type, Function<FriendlyByteBuf, MSG> decoder) {
		registerMessage(type, decoder, NetworkDirection.PLAY_TO_CLIENT);
	}

	private static <MSG extends IPEPacket> void registerMessage(Class<MSG> type, Function<FriendlyByteBuf, MSG> decoder, NetworkDirection networkDirection) {
		HANDLER.registerMessage(index++, type, IPEPacket::encode, decoder, IPEPacket::handle, Optional.of(networkDirection));
	}

	private static boolean isLocal(ServerPlayer player) {
		return player.server.isSingleplayerOwner(player.getGameProfile());
	}

	public static <MSG extends IPEPacket> void sendNonLocal(MSG msg, ServerPlayer player) {
		if (!isLocal(player)) {
			sendTo(msg, player);
		}
	}

	public static <MSG extends IPEPacket> void sendToAllNonLocal(MSG msg) {
		if (ServerLifecycleHooks.getCurrentServer() != null) {
			for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
				sendNonLocal(msg, player);
			}
		}
	}

	/**
	 * Sends a packet to the server.<br> Must be called Client side.
	 */
	public static <MSG extends IPEPacket> void sendToServer(MSG msg) {
		HANDLER.sendToServer(msg);
	}

	/**
	 * Send a packet to a specific player.<br> Must be called Server side.
	 */
	public static <MSG extends IPEPacket> void sendTo(MSG msg, ServerPlayer player) {
		if (!(player instanceof FakePlayer)) {
			HANDLER.send(PacketDistributor.PLAYER.with(() -> player), msg);
		}
	}
}