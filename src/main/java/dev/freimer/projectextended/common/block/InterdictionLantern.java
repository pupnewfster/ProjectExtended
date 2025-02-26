package dev.freimer.projectextended.common.block;

import dev.freimer.projectextended.common.registries.ProjectExtendedBlockEntityTypes;
import moze_intel.projecte.gameObjs.block_entities.InterdictionBlockEntity;
import moze_intel.projecte.gameObjs.blocks.PEEntityBlock;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InterdictionLantern extends LanternBlock implements PEEntityBlock<InterdictionBlockEntity> {

    public InterdictionLantern(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntityTypeRegistryObject<InterdictionBlockEntity> getType() {
        return ProjectExtendedBlockEntityTypes.INTERDICTION_LANTERN;
    }

    @Override
    @Deprecated
    public boolean triggerEvent(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        return triggerBlockEntityEvent(state, level, pos, id, param);
    }
}