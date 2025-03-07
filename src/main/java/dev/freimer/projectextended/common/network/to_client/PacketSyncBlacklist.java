package dev.freimer.projectextended.common.network.to_client;

import dev.freimer.projectextended.ProjectExtended;
import dev.freimer.projectextended.common.BlacklistType;
import dev.freimer.projectextended.common.integration.gamestages.BlacklistManager;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PacketSyncBlacklist(Map<BlacklistType, Map<ItemInfo, Set<String>>> blacklists) implements IPEPacket {

	public static final CustomPacketPayload.Type<PacketSyncBlacklist> TYPE = new CustomPacketPayload.Type<>(ProjectExtended.rl("sync_blacklist"));
	private static final StreamCodec<RegistryFriendlyByteBuf, Map<BlacklistType, Map<ItemInfo, Set<String>>>> BLACKLIST_STREAM_CODEC = ByteBufCodecs.map(
		ignored -> new EnumMap<>(BlacklistType.class),
		BlacklistType.STREAM_CODEC,
		BlacklistManager.BLACKLIST_STREAM_CODEC
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, PacketSyncBlacklist> STREAM_CODEC = BLACKLIST_STREAM_CODEC.map(
		PacketSyncBlacklist::new, PacketSyncBlacklist::blacklists
	);

	@Override
	public void handle(IPayloadContext context) {
		BlacklistManager.handleSyncPacket(blacklists);
	}

	@NotNull
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}