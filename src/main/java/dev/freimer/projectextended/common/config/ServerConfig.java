package dev.freimer.projectextended.common.config;

import dev.freimer.projectextended.ProjectExtended;
import moze_intel.projecte.config.BasePEConfig;
import moze_intel.projecte.config.value.CachedBooleanValue;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * For config options that the server has absolute say over
 */
public final class ServerConfig extends BasePEConfig {

	private final ModConfigSpec configSpec;

	public final CachedBooleanValue showMissingGameStages;

	ServerConfig() {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		showMissingGameStages = CachedBooleanValue.wrap(this, ProjectExtendedConfigTranslations.SERVER_SHOW_MISSING_STAGES.applyToBuilder(builder)
			.define("showMissingGameStages", true));
		configSpec = builder.build();
	}

	@Override
	public String getFileName() {
		return ProjectExtended.MODID;
	}

	@Override
	public String getTranslation() {
		return ProjectExtended.MOD_NAME + " Config";
	}

	@Override
	public ModConfigSpec getConfigSpec() {
		return configSpec;
	}

	@Override
	public ModConfig.Type getConfigType() {
		return ModConfig.Type.SERVER;
	}
}