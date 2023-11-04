package gg.galaxygaming.projectextended.common.network.to_client;

import gg.galaxygaming.projectextended.common.BlacklistType;
import gg.galaxygaming.projectextended.common.integration.gamestages.EMCGameStageHelper;
import gg.galaxygaming.projectextended.common.integration.gamestages.EMCGameStageHelper.GameStageBlacklist;
import java.util.EnumMap;
import java.util.Map;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record PacketSyncBlacklist(Map<BlacklistType, GameStageBlacklist> blacklists) implements IPEPacket {

	private static final BlacklistType[] BLACKLIST_TYPES = BlacklistType.values();

	@Override
	public void handle(NetworkEvent.Context context) {
		EMCGameStageHelper.handleSyncPacket(blacklists);
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		for (BlacklistType blacklistType : BLACKLIST_TYPES) {
			GameStageBlacklist blacklist = blacklists.get(blacklistType);
			if (blacklist == null) {
				//Write that the size of the map is zero
				buffer.writeVarInt(0);
			} else {
				blacklist.writeToBuffer(buffer);
			}
		}

	}

	public static PacketSyncBlacklist decode(FriendlyByteBuf buffer) {
		Map<BlacklistType, GameStageBlacklist> blacklists = new EnumMap<>(BlacklistType.class);
		for (BlacklistType blacklistType : BLACKLIST_TYPES) {
			GameStageBlacklist blacklist = GameStageBlacklist.read(buffer);
			if (blacklist != null) {
				blacklists.put(blacklistType, blacklist);
			}
		}
		return new PacketSyncBlacklist(blacklists);
	}
}