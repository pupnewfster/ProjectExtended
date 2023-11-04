package gg.galaxygaming.projectextended.common.config;

import gg.galaxygaming.projectextended.ProjectExtended;
import moze_intel.projecte.config.BasePEConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.fml.config.ModConfig;

/**
 * For config options that the server has absolute say over
 */
public final class ServerConfig extends BasePEConfig {

	private final ForgeConfigSpec configSpec;

	//Note: Use a normal boolean value, so we don't have to clear the cache as it will be cleared on forge's end
	public final BooleanValue showMissingGameStages;

	ServerConfig() {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.comment("All of the config options in this file are server side and will be synced from server to client. ProjectExtended uses one \"server\" config file for " +
						"all worlds, for convenience in going from one world to another, but makes it be a \"server\" config file so that forge will automatically sync it when " +
						"we connect to a multiplayer server.")
				.push("server");
		showMissingGameStages = builder
			.comment("Set to false to not display missing Game Stages in tooltips when a player cannot learn or condense an item.")
			.define("showMissingGameStages", true);
		builder.pop();
		configSpec = builder.build();
	}

	@Override
	public String getFileName() {
		return ProjectExtended.MODID;
	}

	@Override
	public ForgeConfigSpec getConfigSpec() {
		return configSpec;
	}

	@Override
	public ModConfig.Type getConfigType() {
		return ModConfig.Type.SERVER;
	}
}