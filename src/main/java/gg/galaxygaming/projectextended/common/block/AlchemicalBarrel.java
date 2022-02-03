package gg.galaxygaming.projectextended.common.block;

import gg.galaxygaming.projectextended.common.block_entity.AlchemicalBarrelBlockEntity;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlockEntityTypes;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.blocks.PEEntityBlock;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;

public class AlchemicalBarrel extends DirectionalBlock implements PEEntityBlock<AlchemicalBarrelBlockEntity> {

    public AlchemicalBarrel(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.OPEN, false));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> props) {
        super.createBlockStateDefinition(props);
        props.add(FACING, BlockStateProperties.OPEN);
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection().getOpposite());
    }

    @Nonnull
    @Override
    @Deprecated
    public InteractionResult use(@Nonnull BlockState state, Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand,
          @Nonnull BlockHitResult rtr) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        AlchemicalBarrelBlockEntity barrel = WorldHelper.getBlockEntity(AlchemicalBarrelBlockEntity.class, level, pos);
        if (barrel != null) {
            NetworkHooks.openGui((ServerPlayer) player, barrel, pos);
            player.awardStat(Stats.OPEN_BARREL);
            PiglinAi.angerNearbyPiglins(player, true);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    @Deprecated
    public void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = WorldHelper.getBlockEntity(level, pos);
            if (blockEntity != null) {
                blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> WorldHelper.dropInventory(inv, level, pos));
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    @Deprecated
    public boolean triggerEvent(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        return triggerBlockEntityEvent(state, level, pos, id, param);
    }

    @Override
    @Deprecated
    public void tick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull Random random) {
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
    public boolean hasAnalogOutputSignal(@Nonnull BlockState state) {
        return true;
    }

    @Override
    @Deprecated
    public int getAnalogOutputSignal(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos) {
        BlockEntity blockEntity = WorldHelper.getBlockEntity(level, pos);
        if (blockEntity != null) {
            return blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(ItemHandlerHelper::calcRedstoneFromInventory).orElse(0);
        }
        return 0;
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}