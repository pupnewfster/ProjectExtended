package gg.galaxygaming.projectextended.common.loot;

import java.util.List;
import java.util.Set;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class ProjectExtendedLootProvider extends LootTableProvider {

	public ProjectExtendedLootProvider(PackOutput output) {
		super(output, Set.of(), List.of(new SubProviderEntry(ProjectExtendedBlockLootTable::new, LootContextParamSets.BLOCK)));
	}
}