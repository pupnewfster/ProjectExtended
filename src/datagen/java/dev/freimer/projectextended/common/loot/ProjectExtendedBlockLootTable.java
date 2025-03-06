package dev.freimer.projectextended.common.loot;

import dev.freimer.projectextended.common.registries.ProjectExtendedBlocks;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

public class ProjectExtendedBlockLootTable extends BlockLootSubProvider {

	private final Set<Block> knownBlocks = new HashSet<>();

	public ProjectExtendedBlockLootTable(HolderLookup.Provider registries) {
		super(Collections.emptySet(), FeatureFlags.VANILLA_SET, registries);
	}

	@Override
	protected void generate() {
		dropSelf(ProjectExtendedBlocks.ALCHEMICAL_BARREL.getBlock());
		dropSelf(ProjectExtendedBlocks.INTERDICTION_LANTERN.getBlock());
	}

	@NotNull
	@Override
	public LootTable.Builder createSingleItemTable(@NotNull ItemLike item) {
		//Override so that we can name the loot table
		return LootTable.lootTable().withPool(applyExplosionCondition(item, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.name("main")
				.add(LootItem.lootTableItem(item))
		));
	}

	@Override
	protected void add(@NotNull Block block, @NotNull LootTable.Builder table) {
		//Overwrite the core register method to add to our list of known blocks
		super.add(block, table);
		knownBlocks.add(block);
	}

	@NotNull
	@Override
	protected Iterable<Block> getKnownBlocks() {
		return knownBlocks;
	}
}