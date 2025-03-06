package dev.freimer.projectextended.common.entity;

import dev.freimer.projectextended.common.items.PETrident;
import dev.freimer.projectextended.common.items.PETrident.TridentMode;
import dev.freimer.projectextended.common.registries.ProjectExtendedEntityTypes;
import dev.freimer.projectextended.common.registries.ProjectExtendedItems;
import java.util.function.Predicate;
import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.AreaEffectCloud;
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
        DamageSource damagesource = damageSources().trident(this, thrower == null ? this : thrower);
        float damage = PETrident.getAttackDamage(tridentStack);
        if (level() instanceof ServerLevel serverLevel) {
            //Even though we can't be enchanted normally, apply enchantment modifiers anyway
            damage = EnchantmentHelper.modifyDamage(serverLevel, tridentStack, hitEntity, damagesource, damage);
        }

        float volume = 1.0F;
        SoundEvent sound = SoundEvents.TRIDENT_HIT;
        dealtDamage = true;
        if (hitEntity.hurt(damagesource, damage)) {
            //Vanilla's trident exits on endermen here, we allow hitting them instead

            if (level() instanceof ServerLevel serverLevel) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, hitEntity, damagesource, tridentStack);
                if (mode.canUseSpecialAbility(serverLevel, thrower, getMatterTier())) {
                    if (mode == TridentMode.SHOCKWAVE) {
                        createShockwave(serverLevel, charge, damage, thrower instanceof LivingEntity living ? living : null);
                        volume = 5.0F;
                    } else if (mode == TridentMode.CHANNELING) {
                        //TODO - 1.21: Let this happen through the gameplay enchantment
                        // The only difference is that we do charge + 1 for how many bolts vs vanilla does a singular bolt
                        // and also the reqs for when it works (red matter allows working at other times)
                        /*if (trySummonLightning(serverLevel, charge + 1, hitEntity.blockPosition(), thrower instanceof ServerPlayer ? (ServerPlayer) thrower : null)) {
                            sound = SoundEvents.TRIDENT_THUNDER;
                            volume = 5.0F;}
                        }*/
                    }
                }
            }

            if (hitEntity instanceof LivingEntity livingHit) {
                doKnockback(livingHit, damagesource);
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
        //SoundEvent sound = getHitGroundSoundEvent();
        //float volume = 1.0F;
        //float pitch = 1.2F / (random.nextFloat() * 0.2F + 0.9F);

        Entity thrower = getOwner();
        if (mode.canUseSpecialAbility(level, thrower, getMatterTier())) {
            ItemStack tridentStack = getWeaponItem();
            if (mode == TridentMode.SHOCKWAVE) {
                createShockwave(level, charge, PETrident.getAttackDamage(tridentStack), thrower instanceof LivingEntity living ? living : null);
                //volume = 5.0F;
                //pitch = 1.0F;
                //TODO - 1.21: Play the hit ground sound louder?
            } else if (mode == TridentMode.CHANNELING) {
                //TODO - 1.21: Let this happen through the gameplay enchantment
                // The only difference is that we do charge + 1 for how many bolts vs vanilla does a singular bolt
                // and also the reqs for when it works (red matter allows working at other times)
                /*if (trySummonLightning(level, charge + 1, hitResult.getBlockPos().above(), thrower instanceof ServerPlayer ? (ServerPlayer) thrower : null)) {
                    sound = SoundEvents.TRIDENT_THUNDER;
                    volume = 5.0F;
                    pitch = 1.0F;
                }*/
            }
        }
    }

    private boolean trySummonLightning(ServerLevel level, int bolts, Vec3 hitTarget, @Nullable ServerPlayer thrower) {
        //TODO - 1.21: Allow this for when the enchantment effect is happening/make sure we do it anyway?
        // such as if it is not thundering but we have a red matter trident
        BlockPos hitPos = BlockPos.containing(hitTarget);
        //Note: uses canBlockSeeSky instead of isSkyLightMax like the vanilla trident does to fix not being able
        // to cause lightning to come down on fish or in the water
        if (Level.isInSpawnableBounds(hitPos) && level.canSeeSkyFromBelowWater(hitPos)) {
            boolean hasAction = false;
            for (int i = 0; i < bolts; i++) {
                //Note: We use getWeaponItem so that we take the fuel from the source stack
                if (thrower == null || ItemPE.consumeFuel(thrower, getWeaponItem(), 64, true)) {
                    EntityType.LIGHTNING_BOLT.spawn(level, lightning -> {
                        //Note: Unlike vanilla in SummonEntityEffect, we do this in the consumer,
                        // so that it has the proper values set before adding it to the level
                        lightning.moveTo(hitTarget);
                        lightning.setCause(thrower);
                    }, hitPos, MobSpawnType.TRIGGERED, false, false);
                    hasAction = true;
                } else {
                    //If we failed to consume EMC but needed EMC just break out early as we won't have the required EMC for any of the future bolts
                    break;
                }
            }
            return hasAction;
        }
        return false;
    }

    private void createShockwave(ServerLevel level, int charge, float damage, @Nullable LivingEntity thrower) {
        //Note: This used to bypass armor but no longer does. Eventually we may want that back but for now it seems reasonable enough to not do so
        DamageSource src = damageSources().trident(this, thrower == null ? this : thrower);
        int distance = charge + 1;
        for (Entity entity : level.getEntities(thrower, getBoundingBox().inflate(distance), SLAY_MOB)) {
            entity.hurt(src, damage);
        }
        AreaEffectCloud particle = new AreaEffectCloud(level, getX(), getY(), getZ());
        particle.setOwner(thrower);
        particle.setParticle(ParticleTypes.CRIT);
        particle.setRadius(distance);
        particle.setDuration(0);
        level.addFreshEntity(particle);
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