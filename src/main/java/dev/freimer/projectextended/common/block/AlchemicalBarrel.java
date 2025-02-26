package dev.freimer.projectextended.common.block;

import com.mojang.serialization.MapCodec;
import dev.freimer.projectextended.common.block_entity.AlchemicalBarrelBlockEntity;
import dev.freimer.projectextended.common.registries.ProjectExtendedBlockEntityTypes;
import moze_intel.projecte.gameObjs.blocks.PEEntityBlock;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AlchemicalBarrel extends DirectionalBlock implements PEEntityBlock<AlchemicalBarrelBlockEntity> {

    private static final MapCodec<AlchemicalBarrel> CODEC = simpleCodec(AlchemicalBarrel::new);

    public AlchemicalBarrel(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.OPEN, false));
    }

    @NotNull
    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> props) {
        super.createBlockStateDefinition(props);
        props.add(FACING, BlockStateProperties.OPEN);
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection().getOpposite());
    }

    @NotNull
    @Override
    @Deprecated
    public InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult rtr) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        AlchemicalBarrelBlockEntity barrel = WorldHelper.getBlockEntity(AlchemicalBarrelBlockEntity.class, level, pos);
        if (barrel != null) {
            player.openMenu(barrel, pos);
            player.awardStat(Stats.OPEN_BARREL);
            PiglinAi.angerNearbyPiglins(player, true);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    @Deprecated
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            IItemHandler handler = WorldHelper.getCapability(level, ItemHandler.BLOCK, pos, state, null, null);
            WorldHelper.dropInventory(handler, level, pos);
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    @Deprecated
    public void attack(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player) {
        if (!level.isClientSide) {
            ItemStack stack = player.getMainHandItem();
            if (!stack.isEmpty() && stack.is(PEItems.PHILOSOPHERS_STONE)) {
                level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(FACING, player.getDirection().getOpposite()));
            }
        }
    }

    @Override
    @Deprecated
    public boolean triggerEvent(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        return triggerBlockEntityEvent(state, level, pos, id, param);
    }

    @Override
    @Deprecated
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        AlchemicalBarrelBlockEntity barrel = WorldHelper.getBlockEntity(AlchemicalBarrelBlockEntity.class, level, pos);
        if (barrel != null) {
            barrel.recheckOpen();
        }
    }

    @Nullable
    @Override
    public BlockEntityTypeRegistryObject<? extends AlchemicalBarrelBlockEntity> getType() {
        return ProjectExtendedBlockEntityTypes.ALCHEMICAL_BARREL;
    }

    @Override
    @Deprecated
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    @Deprecated
    public int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return ItemHandlerHelper.calcRedstoneFromInventory(WorldHelper.getCapability(level, ItemHandler.BLOCK, pos, null));
    }

    @NotNull
    @Override
    @Deprecated
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @NotNull
    @Override
    @Deprecated
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}