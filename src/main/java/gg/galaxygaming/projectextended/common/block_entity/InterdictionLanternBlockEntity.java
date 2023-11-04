package gg.galaxygaming.projectextended.common.block_entity;

import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlockEntityTypes;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class InterdictionLanternBlockEntity extends BlockEntity {

	public InterdictionLanternBlockEntity(BlockPos pos, BlockState state) {
		super(ProjectExtendedBlockEntityTypes.INTERDICTION_LANTERN.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, InterdictionLanternBlockEntity lantern) {
		//Note: The interdiction lantern's ticker needs to be run on both sides to ensure it renders properly
		// when it deflects things like projectiles
		WorldHelper.repelEntitiesInterdiction(level, new AABB(pos.offset(-8, -8, -8), pos.offset(8, 8, 8)),
				pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
	}
}