package dev.freimer.projectextended.common.entity;

import dev.freimer.projectextended.common.items.PETrident;
import dev.freimer.projectextended.common.items.PETrident.TridentMode;
import dev.freimer.projectextended.common.registries.ProjectExtendedEntityTypes;
import dev.freimer.projectextended.common.registries.ProjectExtendedItems;
import java.util.function.Predicate;
import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PETridentEntity extends ThrownTrident {

    private static final Predicate<Entity> SLAY_MOB = entity -> !entity.isSpectator() && entity instanceof Enemy;
    private static final EntityDataAccessor<Integer> ID_MATTER_TIER = SynchedEntityData.defineId(PETridentEntity.class, EntityDataSerializers.INT);
    private TridentMode mode = TridentMode.NORMAL;
    private boolean noReturn;
    private int charge;
    private boolean playSoundLouder;

    public PETridentEntity(EntityType<? extends PETridentEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    public PETridentEntity(Level world, LivingEntity thrower, ItemStack stack) {
        super(world, thrower, stack);
        setMatterPropertiesFromItem(stack);
    }

    public PETridentEntity(Level level, double x, double y, double z, ItemStack stack) {
        super(level, x, y, z, stack);
        setMatterPropertiesFromItem(stack);
    }

    @NotNull
    @Override
    public EntityType<PETridentEntity> getType() {
        return ProjectExtendedEntityTypes.PE_TRIDENT.get();
    }

    @Override
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_MATTER_TIER, 0);
    }

    private void setMatterPropertiesFromItem(ItemStack stack) {
        if (stack.getItem() instanceof PETrident trident) {
            this.entityData.set(ID_MATTER_TIER, trident.getMatterTier());
            charge = trident.getCharge(stack);
            mode = trident.getMode(stack);
        } else {
            this.entityData.set(ID_MATTER_TIER, 0);
            charge = 0;
            mode = TridentMode.NORMAL;
        }
    }

    public int getMatterTier() {
        return entityData.get(ID_MATTER_TIER);
    }

    @Override
    public void tick() {
        if (inGroundTime > 4 && !isAcceptibleReturnOwner()) {
            noReturn = true;
            entityData.set(ID_LOYALTY, (byte) 0);
        }
        super.tick();
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        ItemStack tridentStack = getWeaponItem();
        Entity hitEntity = result.getEntity();
        Entity thrower = getOwner();
        DamageSource damageSource = damageSources().trident(this, thrower == null ? this : thrower);
        float damage = PETrident.getAttackDamage(tridentStack);
        if (level() instanceof ServerLevel serverLevel) {
            //Even though we can't be enchanted normally, apply enchantment modifiers anyway
            damage = EnchantmentHelper.modifyDamage(serverLevel, tridentStack, hitEntity, damageSource, damage);
        }

        float volume = 1.0F;
        SoundEvent sound = SoundEvents.TRIDENT_HIT;
        dealtDamage = true;
        if (hitEntity.hurt(damageSource, damage)) {
            //Vanilla's trident exits on endermen here, we allow hitting them instead

            if (level() instanceof ServerLevel serverLevel) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, hitEntity, damageSource, tridentStack);
                if (mode.canUseSpecialAbility(serverLevel, thrower, getMatterTier())) {
                    if (mode == TridentMode.SHOCKWAVE) {
                        createShockwave(serverLevel, charge, result.getLocation(), damage, thrower instanceof LivingEntity living ? living : null, ParticleTypes.CRIT);
                        volume = 5.0F;
                    } else if (mode == TridentMode.CHANNELING) {
                        //Note: Channeling explicitly checks for it happening on vanilla's trident entity, so we have to handle it manually here
                        // We also use the hit result's location instead of the hit entity's position
                        trySummonLightning(serverLevel, charge + 1, result.getLocation(), thrower instanceof ServerPlayer ? (ServerPlayer) thrower : null);
                    }
                }
            }

            if (hitEntity instanceof LivingEntity livingHit) {
                doKnockback(livingHit, damageSource);
                doPostHurtEffects(livingHit);
            }
        }
        setDeltaMovement(getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        playSound(sound, volume, 1.0F);
    }

    @Override
    protected void hitBlockEnchantmentEffects(@NotNull ServerLevel level, @NotNull BlockHitResult hitResult, @NotNull ItemStack stack) {
        super.hitBlockEnchantmentEffects(level, hitResult, stack);
        //Note: This runs before updating the position of the entity rather than afterward like it used to
        Entity thrower = getOwner();
        if (mode.canUseSpecialAbility(level, thrower, getMatterTier())) {
            ItemStack tridentStack = getWeaponItem();
            Vec3 hitTarget = hitResult.getBlockPos().clampLocationWithin(hitResult.getLocation());
            if (mode == TridentMode.SHOCKWAVE) {
                BlockPos hitPos = hitResult.getBlockPos();
                createShockwave(level, charge, hitTarget, PETrident.getAttackDamage(tridentStack), thrower instanceof LivingEntity living ? living : null,
                      new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(hitPos)).setPos(hitPos));
                playSoundLouder = true;
            } else if (mode == TridentMode.CHANNELING) {
                //Note: Channeling explicitly checks for it happening on vanilla's trident entity, so we have to handle it manually here
                trySummonLightning(level, charge + 1, hitTarget, thrower instanceof ServerPlayer ? (ServerPlayer) thrower : null);
            }
        }
    }

    @Override
    public void playSound(@NotNull SoundEvent sound, float volume, float pitch) {
        if (playSoundLouder && sound == getHitGroundSoundEvent()) {
            playSoundLouder = false;
            volume = 5;
            pitch = 1;
        }
        super.playSound(sound, volume, pitch);
    }

    /**
     * Similar to logic that {@link net.minecraft.world.item.enchantment.Enchantments#CHANNELING} uses via
     * {@link net.minecraft.world.item.enchantment.effects.SummonEntityEffect}
     */
    private void trySummonLightning(ServerLevel level, int bolts, Vec3 hitTarget, @Nullable ServerPlayer thrower) {
        BlockPos hitPos = BlockPos.containing(hitTarget);
        //Note: uses canBlockSeeSky instead of isSkyLightMax like the vanilla trident does to fix not being able
        // to cause lightning to come down on fish or in the water
        if (Level.isInSpawnableBounds(hitPos) && level.canSeeSkyFromBelowWater(hitPos)) {
            boolean hasPlayed = false;
            for (int i = 0; i < bolts; i++) {
                //Note: We use getWeaponItem so that we take the fuel from the source stack
                if (thrower != null && !ItemPE.consumeFuel(thrower, getWeaponItem(), 64, true)) {
                    //If we failed to consume EMC but needed EMC just break out early as we won't have the required EMC for any of the future bolts
                    return;
                }
                EntityType.LIGHTNING_BOLT.spawn(level, lightning -> {
                    //Note: Unlike vanilla in SummonEntityEffect, we do this in the consumer,
                    // so that it has the proper values set before adding it to the level
                    lightning.moveTo(hitTarget);
                    lightning.setCause(thrower);
                }, hitPos, MobSpawnType.TRIGGERED, false, false);
                if (!hasPlayed) {
                    hasPlayed = true;
                    if (!isSilent()) {
                        level.playSound(null, hitTarget.x(), hitTarget.y(), hitTarget.z(), SoundEvents.TRIDENT_THUNDER, getSoundSource(), 5, 1);
                    }
                }
            }
        }
    }

    private void createShockwave(ServerLevel level, int charge, Vec3 hitTarget, float damage, @Nullable LivingEntity thrower, ParticleOptions particleOptions) {
        //Note: This used to bypass armor but no longer does. Eventually we may want that back but for now it seems reasonable enough to not do so
        DamageSource src = damageSources().trident(this, thrower == null ? this : thrower);
        int distance = charge + 1;
        for (Entity entity : level.getEntities(thrower, getBoundingBox().inflate(distance), SLAY_MOB)) {
            entity.hurt(src, damage);
        }
        double radius = 0.1 * distance;
        level.sendParticles(particleOptions, hitTarget.x(), hitTarget.y(), hitTarget.z(), 40 * distance, radius, 0.3, radius, 0);
    }

    @Override
    protected void setPickupItemStack(@NotNull ItemStack stack) {
        super.setPickupItemStack(stack);
        setMatterPropertiesFromItem(getPickupItemStackOrigin());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        noReturn = compound.getBoolean("no_return");
        if (noReturn) {
            entityData.set(ID_LOYALTY, (byte) 0);
        }
        setMatterPropertiesFromItem(getPickupItemStackOrigin());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("no_return", noReturn);
    }

    @Override
    public void tickDespawn() {
        if (this.pickup != Pickup.ALLOWED) {
            super.tickDespawn();
        } else if (noReturn && !level().isClientSide) {
            //Drop the item if we despawned after not having been able to return
            spawnAtLocation(getPickupItem(), 0.1F);
            discard();
        }
    }

    @NotNull
    @Override
    protected ItemStack getDefaultPickupItem() {
        if (getMatterTier() > 0) {
            return ProjectExtendedItems.RED_MATTER_TRIDENT.asStack();
        }
        return ProjectExtendedItems.DARK_MATTER_TRIDENT.asStack();
    }

    @Override
    protected float getWaterInertia() {
        return super.getWaterInertia() + 0.5F * (getMatterTier() + 1);
    }

    @Override
    public ItemStack getPickedResult(@NotNull HitResult target) {
        return getPickupItem();
    }
}