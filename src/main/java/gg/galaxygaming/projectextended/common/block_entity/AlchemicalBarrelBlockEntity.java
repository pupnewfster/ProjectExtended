package gg.galaxygaming.projectextended.common.block_entity;

import gg.galaxygaming.projectextended.common.container.AlchemicalBarrelContainer;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlockEntityTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlocks;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.capability.managing.BasicCapabilityResolver;
import moze_intel.projecte.gameObjs.block_entities.CapabilityEmcBlockEntity;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class AlchemicalBarrelBlockEntity extends CapabilityEmcBlockEntity implements MenuProvider {

    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
            AlchemicalBarrelBlockEntity.this.playSound(state, SoundEvents.BARREL_OPEN);
            AlchemicalBarrelBlockEntity.this.updateBlockState(state, true);
        }

        @Override
        protected void onClose(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
            AlchemicalBarrelBlockEntity.this.playSound(state, SoundEvents.BARREL_CLOSE);
            AlchemicalBarrelBlockEntity.this.updateBlockState(state, false);
        }

        @Override
        protected void openerCountChanged(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, int oldCount, int openCount) {
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            return player.containerMenu instanceof AlchemicalBarrelContainer container && container.blockEntityMatches(AlchemicalBarrelBlockEntity.this);
        }
    };
    private final BarrelInventory inventory = new BarrelInventory();
    private boolean inventoryChanged;

    public AlchemicalBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectExtendedBlockEntityTypes.ALCHEMICAL_BARREL, pos, state);
        itemHandlerResolver = BasicCapabilityResolver.getBasicItemHandlerResolver(inventory);
    }

    public static void tickClient(Level level, BlockPos pos, BlockState state, AlchemicalBarrelBlockEntity barrel) {
        for (int i = 0; i < barrel.inventory.getSlots(); i++) {
            ItemStack stack = barrel.inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                stack.getCapability(PECapabilities.ALCH_CHEST_ITEM_CAPABILITY).ifPresent(alchChestItem -> alchChestItem.updateInAlchChest(level, pos, stack));
            }
        }
    }

    public static void tickServer(Level level, BlockPos pos, BlockState state, AlchemicalBarrelBlockEntity barrel) {
        for (int i = 0; i < barrel.inventory.getSlots(); i++) {
            ItemStack stack = barrel.inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                int slotId = i;
                stack.getCapability(PECapabilities.ALCH_CHEST_ITEM_CAPABILITY).ifPresent(alchChestItem -> {
                    if (alchChestItem.updateInAlchChest(level, pos, stack)) {
                        barrel.inventory.onContentsChanged(slotId);
                    }
                });
            }
        }
        if (barrel.inventoryChanged) {
            //If the inventory changed, resync so that the client can tick things properly
            barrel.inventoryChanged = false;
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
        }
        barrel.updateComparators();
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.merge(inventory.serializeNBT());
    }

    public void startOpen(Player player) {
        if (!isRemoved() && !player.isSpectator() && level != null) {
            openersCounter.incrementOpeners(player, level, getBlockPos(), getBlockState());
        }
    }

    public void stopOpen(Player player) {
        if (!isRemoved() && !player.isSpectator() && level != null) {
            openersCounter.decrementOpeners(player, level, getBlockPos(), getBlockState());
        }
    }

    public void recheckOpen() {
        if (!isRemoved() && level != null) {
            openersCounter.recheckOpeners(level, getBlockPos(), getBlockState());
        }
    }

    void updateBlockState(BlockState state, boolean open) {
        if (level != null) {
            level.setBlockAndUpdate(getBlockPos(), state.setValue(BarrelBlock.OPEN, open));
        }
    }

    void playSound(BlockState state, SoundEvent sound) {
        Vec3i vec3i = state.getValue(BarrelBlock.FACING).getNormal();
        double d0 = (double) this.worldPosition.getX() + 0.5D + (double) vec3i.getX() / 2.0D;
        double d1 = (double) this.worldPosition.getY() + 0.5D + (double) vec3i.getY() / 2.0D;
        double d2 = (double) this.worldPosition.getZ() + 0.5D + (double) vec3i.getZ() / 2.0D;
        this.level.playSound(null, d0, d1, d2, sound, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
    }

    @NotNull
    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player playerIn) {
        return new AlchemicalBarrelContainer(windowId, playerInventory, this);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return TextComponentUtil.build(ProjectExtendedBlocks.ALCHEMICAL_BARREL);
    }

    private class BarrelInventory extends StackHandler {

        protected BarrelInventory() {
            super(104);
        }

        @Override
        public void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (level != null && !level.isClientSide) {
                inventoryChanged = true;
            }
        }
    }
}