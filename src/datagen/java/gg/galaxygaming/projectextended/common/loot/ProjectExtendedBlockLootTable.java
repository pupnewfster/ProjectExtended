package gg.galaxygaming.projectextended.common.loot;

import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlocks;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

public class ProjectExtendedBlockLootTable extends BlockLoot {

	private final Set<Block> knownBlocks = new HashSet<>();

	@Override
	protected void addTables() {
		dropSelf(ProjectExtendedBlocks.ALCHEMICAL_BARREL.getBlock());
	}

	@Override
	public void dropOther(@NotNull Block block, @NotNull ItemLike drop) {
		//Override to use our own dropping method that names the loot table
		add(block, dropping(drop));
	}

	protected static LootTable.Builder dropping(ItemLike item) {
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