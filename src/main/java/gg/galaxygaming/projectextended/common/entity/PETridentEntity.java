package gg.galaxygaming.projectextended.common.entity;

import gg.galaxygaming.projectextended.common.items.PETrident;
import gg.galaxygaming.projectextended.common.items.PETrident.TridentMode;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedDataComponentTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedEntityTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedItems;
import java.util.function.Predicate;
import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//TODO - 1.21: See what other methods we should override
public class PETridentEntity extends ThrownTrident implements IEntityWithComplexSpawn {

    private static final Predicate<Entity> SLAY_MOB = entity -> !entity.isSpectator() && entity instanceof Enemy;
    private boolean noReturn;
    private int matterTier;

    public PETridentEntity(EntityType<? extends PETridentEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    public PETridentEntity(Level world, LivingEntity thrower, ItemStack stack) {
        super(world, thrower, stack);
        if (stack.getItem() instanceof PETrident trident) {
            matterTier = trident.getMatterTier();
        }
    }

    public PETridentEntity(Level level, double x, double y, double z, ItemStack stack) {
        super(level, x, y, z, stack);
        if (stack.getItem() instanceof PETrident trident) {
            matterTier = trident.getMatterTier();
        }
    }

    @NotNull
    @Override
    public EntityType<PETridentEntity> getType() {
        return ProjectExtendedEntityTypes.PE_TRIDENT.get();
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
    protected void onHitEntity(EntityHitResult result) {
        Entity hitEntity = result.getEntity();
        Entity thrower = getOwner();
        DamageSource damagesource = damageSources().trident(this, thrower == null ? this : thrower);
        ItemStack tridentStack = getWeaponItem();

        PETrident trident = (PETrident) tridentStack.getItem();
        int charge = trident.getCharge(tridentStack);
        float damage = trident.getDamage() + charge;
        if (level() instanceof ServerLevel serverLevel) {
            //Even though we can't be enchanted normally, apply enchantment modifiers anyway
            damage = EnchantmentHelper.modifyDamage(serverLevel, tridentStack, hitEntity, damagesource, damage);
        }

        dealtDamage = true;
        if (hitEntity.hurt(damagesource, damage)) {
            //Vanilla's trident exits on endermen here, we allow hitting them instead

            if (level() instanceof ServerLevel serverLevel) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, hitEntity, damagesource, tridentStack);
            }

            if (hitEntity instanceof LivingEntity livingHit) {
                doKnockback(livingHit, damagesource);
                doPostHurtEffects(livingHit);
            }
        }
        setDeltaMovement(getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        float volume = 1.0F;
        SoundEvent sound = SoundEvents.TRIDENT_HIT;
        TridentMode mode = trident.getMode(tridentStack);
        if (mode == TridentMode.SHOCKWAVE) {
            if (tryCreateShockwave(charge, trident.getDamage(), thrower instanceof LivingEntity ? (LivingEntity) thrower : null)) {
                volume = 5.0F;
            }
        } else if (mode == TridentMode.CHANNELING) {
            //TODO - 1.21: Let this happen through the gameplay enchantment
            // The only difference is that we do charge + 1 for how many bolts vs vanilla does a singular bolt
            /*if (trySummonLightning(charge + 1, hitEntity.blockPosition(), thrower instanceof ServerPlayer ? (ServerPlayer) thrower : null)) {
                sound = SoundEvents.TRIDENT_THUNDER;
                volume = 5.0F;
            }*/
        }
        playSound(sound, volume, 1.0F);
    }

    @Override
    protected void hitBlockEnchantmentEffects(@NotNull ServerLevel level, @NotNull BlockHitResult hitResult, @NotNull ItemStack stack) {
        super.hitBlockEnchantmentEffects(level, hitResult, stack);
        //TODO - 1.21: Move onHitBlock impl to here
        //Note: This runs before updating the position of the entity rather than afterward like it used to
        //SoundEvent sound = getHitGroundSoundEvent();
        //float volume = 1.0F;
        //float pitch = 1.2F / (random.nextFloat() * 0.2F + 0.9F);

        Entity thrower = getOwner();
        //If we hit a block try
        ItemStack tridentStack = getWeaponItem();
        PETrident trident = (PETrident) tridentStack.getItem();
        int charge = trident.getCharge(tridentStack);
        TridentMode mode = tridentStack.getOrDefault(ProjectExtendedDataComponentTypes.TRIDENT_MODE, TridentMode.NORMAL);
        if (mode == TridentMode.SHOCKWAVE) {
            if (tryCreateShockwave(charge, trident.getDamage(), thrower instanceof LivingEntity ? (LivingEntity) thrower : null)) {
                //volume = 5.0F;
                //pitch = 1.0F;
                //TODO - 1.21: Play the hit ground sound louder?
            }
        } else if (mode == TridentMode.CHANNELING) {
            //TODO - 1.21: Let this happen through the gameplay enchantment
            // The only difference is that we do charge + 1 for how many bolts vs vanilla does a singular bolt
            /*if (trySummonLightning(charge + 1, hitResult.getBlockPos().above(), thrower instanceof ServerPlayer ? (ServerPlayer) thrower : null)) {
                sound = SoundEvents.TRIDENT_THUNDER;
                volume = 5.0F;
                pitch = 1.0F;
            }*/
        }
    }

    private boolean trySummonLightning(int bolts, BlockPos hitPos, @Nullable ServerPlayer thrower) {
        if (level() instanceof ServerLevel) {
            //Allow for channeling to take place if we are red matter, or it is thundering
            if (matterTier > 0 || level().isThundering()) {
                //Note: uses canBlockSeeSky instead of isSkyLightMax like the vanilla trident does to fix not being able
                // to cause lightning to come down on fish or in the water
                if (level().canSeeSkyFromBelowWater(hitPos)) {
                    boolean hasAction = false;
                    for (int i = 0; i < bolts; i++) {
                        //TODO - 1.21: Should this be getWeaponItem or getPickupItem (aka do we want to act on the source stack)
                        if (thrower == null || ItemPE.consumeFuel(thrower, getWeaponItem(), 64, true)) {
                            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level());
                            if (lightning != null) {
                                lightning.moveTo(Vec3.atBottomCenterOf(hitPos));
                                lightning.setCause(thrower);
                                level().addFreshEntity(lightning);
                            }
                            hasAction = true;
                        } else {
                            //If we failed to consume EMC but needed EMC just break out early as we won't have the required EMC for any of the future bolts
                            break;
                        }
                    }
                    return hasAction;
                }
            }
        }
        return false;
    }

    private boolean tryCreateShockwave(int charge, float damage, @Nullable LivingEntity thrower) {
        if (level() instanceof ServerLevel) {
            //Allow for shockwave to be created if we are red matter or the thrower is wet
            if (matterTier > 0 || thrower != null && thrower.isInWaterOrRain()) {
                //Note: This used to bypass armor but no longer does. Eventually we may want that back but for now it seems reasonable enough to not do so
                DamageSource src = damageSources().trident(this, thrower == null ? this : thrower);
                float damageToDo = damage + charge;
                int distance = charge + 1;
                for (Entity entity : level().getEntities(thrower, getBoundingBox().inflate(distance), SLAY_MOB)) {
                    entity.hurt(src, damageToDo);
                }
                AreaEffectCloud particle = new AreaEffectCloud(level(), getX(), getY(), getZ());
                particle.setOwner(thrower);
                particle.setParticle(ParticleTypes.CRIT);
                particle.setRadius(distance);
                particle.setDuration(0);
                level().addFreshEntity(particle);
                return true;
            }
        }
        return false;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        noReturn = compound.getBoolean("no_return");
        if (noReturn) {
            entityData.set(ID_LOYALTY, (byte) 0);
        }
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
        return ProjectExtendedItems.DARK_MATTER_TRIDENT.asStack();
    }

    @Override
    protected float getWaterInertia() {
        return super.getWaterInertia() + 0.5F * (matterTier + 1);
    }

    @Override
    public void writeSpawnData(@NotNull RegistryFriendlyByteBuf buffer) {
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, getWeaponItem());
    }

    @Override
    public void readSpawnData(@NotNull RegistryFriendlyByteBuf buffer) {
        ItemStack trident = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
        setPickupItemStack(trident);
        if (trident.getItem() instanceof PETrident tridentItem) {
            matterTier = tridentItem.getMatterTier();
        }
    }

    @Override
    public ItemStack getPickedResult(@NotNull HitResult target) {
        return getPickupItem();
    }
}