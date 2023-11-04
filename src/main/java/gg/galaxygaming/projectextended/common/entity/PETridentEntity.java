package gg.galaxygaming.projectextended.common.entity;

import gg.galaxygaming.projectextended.common.items.PETrident;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedEntityTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedItems;
import java.util.function.Predicate;
import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
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
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Modified copy of vanilla's {@link ThrownTrident} so as to cut some things out, but also allow for overriding things and then being able to properly still call super
 * without it then just going back into {@link ThrownTrident} that we were changing anyway.
 */
public class PETridentEntity extends AbstractArrow implements IEntityAdditionalSpawnData {

    private static final Predicate<Entity> SLAY_MOB = entity -> !entity.isSpectator() && entity instanceof Enemy;
    private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(PETridentEntity.class, EntityDataSerializers.BOOLEAN);
    private ItemStack tridentItem = new ItemStack(ProjectExtendedItems.DARK_MATTER_TRIDENT);
    private boolean dealtDamage;
    private boolean noReturn;
    private int loyaltyLevel;
    private int matterTier;
    private int clientSideReturnTridentTickCount;

    public PETridentEntity(EntityType<? extends PETridentEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    public PETridentEntity(Level world, LivingEntity thrower, ItemStack thrownStackIn) {
        super(ProjectExtendedEntityTypes.PE_TRIDENT.get(), thrower, world);
        setStackAndLoyalty(thrownStackIn.copy());
        this.entityData.set(ID_FOIL, thrownStackIn.hasFoil());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_FOIL, false);
    }

    private void setStackAndLoyalty(@NotNull ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof PETrident trident) {
            tridentItem = stack;
            matterTier = trident.getMatterTier();
            loyaltyLevel = trident.getCharge(stack) + 1;
        }
    }

    public int getMatterTier() {
        return matterTier;
    }

    public boolean isFoil() {
        return this.entityData.get(ID_FOIL);
    }

    @Override
    public void tick() {
        if (inGroundTime > 4) {
            dealtDamage = true;
            noReturn = !isAcceptableReturnOwner();
        }
        //If we aren't told to not do any return logic AND we either landed or are already on the return trip
        if (!noReturn && (dealtDamage || isNoPhysics())) {
            Entity entity = getOwner();
            //Make it return to the thrower
            if (entity != null) {
                if (isAcceptableReturnOwner()) {
                    setNoPhysics(true);
                    Vec3 returnVector = entity.getEyePosition().subtract(position());
                    setPosRaw(getX(), getY() + returnVector.y * 0.015D * loyaltyLevel, getZ());
                    if (level().isClientSide) {
                        yOld = getY();
                    }
                    setDeltaMovement(getDeltaMovement().scale(0.95D).add(returnVector.normalize().scale(0.05D * loyaltyLevel)));
                    if (clientSideReturnTridentTickCount == 0) {
                        //Play the return sound if this is our first tick returning
                        playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
                    }
                    clientSideReturnTridentTickCount++;
                } else {
                    //If we shouldn't return to the thrower, but we already are on the way back just drop the item
                    if (!level().isClientSide && pickup == Pickup.ALLOWED) {
                        spawnAtLocation(getPickupItem(), 0.1F);
                    }
                    discard();
                }
            }
        }
        super.tick();
    }

    private boolean isAcceptableReturnOwner() {
        Entity entity = getOwner();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayer) || !entity.isSpectator();
        }
        return false;
    }

    @NotNull
    @Override
    protected ItemStack getPickupItem() {
        return this.tridentItem.copy();
    }

    @Nullable
    @Override
    protected EntityHitResult findHitEntity(@NotNull Vec3 startVec, @NotNull Vec3 endVec) {
        return this.dealtDamage ? null : super.findHitEntity(startVec, endVec);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity hitEntity = result.getEntity();
        PETrident trident = (PETrident) tridentItem.getItem();
        int charge = trident.getCharge(tridentItem);
        float damage = trident.getDamage() + charge;
        if (hitEntity instanceof LivingEntity livingHit) {
            //Even though we can't be enchanted normally, apply enchantment modifiers anyway
            damage += EnchantmentHelper.getDamageBonus(this.tridentItem, livingHit.getMobType());
        }
        Entity thrower = getOwner();
        DamageSource damagesource = damageSources().trident(this, thrower == null ? this : thrower);
        dealtDamage = true;
        if (hitEntity.hurt(damagesource, damage)) {
            //Vanilla's trident exits on endermen here, we allow hitting them instead
            if (hitEntity instanceof LivingEntity livingHit) {
                if (thrower instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingHit, thrower);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) thrower, livingHit);
                }
                this.doPostHurtEffects(livingHit);
            }
        }
        setDeltaMovement(getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        float volume = 1.0F;
        SoundEvent sound = SoundEvents.TRIDENT_HIT;
        byte mode = trident.getMode(tridentItem);
        if (mode == PETrident.CHANNELING) {
            if (trySummonLightning(charge + 1, hitEntity.blockPosition(), thrower instanceof ServerPlayer ? (ServerPlayer) thrower : null)) {
                sound = SoundEvents.TRIDENT_THUNDER;
                volume = 5.0F;
            }
        } else if (mode == PETrident.SHOCKWAVE) {
            if (tryCreateShockwave(charge, trident.getDamage(), thrower instanceof LivingEntity ? (LivingEntity) thrower : null)) {
                volume = 5.0F;
            }
        }
        playSound(sound, volume, 1.0F);
    }

    @Override//onBlockHit
    protected void onHitBlock(BlockHitResult result) {
        lastState = level().getBlockState(result.getBlockPos());
        lastState.onProjectileHit(level(), lastState, result, this);
        Vec3 motion = result.getLocation().subtract(getX(), getY(), getZ());
        setDeltaMovement(motion);
        Vec3 vec3d1 = motion.normalize().scale(0.05F);
        setPosRaw(getX() - vec3d1.x, getY() - vec3d1.y, getZ() - vec3d1.z);
        //Vanilla Copy end

        SoundEvent sound = getHitGroundSoundEvent();
        float volume = 1.0F;
        float pitch = 1.2F / (random.nextFloat() * 0.2F + 0.9F);

        BlockPos hitPosition = result.getBlockPos();
        Entity thrower = getOwner();
        //If we hit a block try
        PETrident trident = (PETrident) tridentItem.getItem();
        int charge = trident.getCharge(tridentItem);
        byte mode = trident.getMode(tridentItem);
        if (mode == PETrident.CHANNELING) {
            if (trySummonLightning(charge + 1, hitPosition.above(), thrower instanceof ServerPlayer ? (ServerPlayer) thrower : null)) {
                sound = SoundEvents.TRIDENT_THUNDER;
                volume = 5.0F;
                pitch = 1.0F;
            }
        } else if (mode == PETrident.SHOCKWAVE) {
            if (tryCreateShockwave(charge, trident.getDamage(), thrower instanceof LivingEntity ? (LivingEntity) thrower : null)) {
                volume = 5.0F;
                pitch = 1.0F;
            }
        }

        //Vanilla Copy continue
        playSound(sound, volume, pitch);
        inGround = true;
        shakeTime = 7;
        setCritArrow(false);
        setPierceLevel((byte) 0);
        setSoundEvent(SoundEvents.ARROW_HIT);
        setShotFromCrossbow(false);
        resetPiercedEntities();
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
                        if (thrower == null || ItemPE.consumeFuel(thrower, tridentItem, 64, true)) {
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
    protected boolean tryPickup(@NotNull Player player) {
        return super.tryPickup(player) || isNoPhysics() && ownedBy(player) && player.getInventory().add(getPickupItem());
    }

    @NotNull
    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    public void playerTouch(@NotNull Player entity) {
        if (ownedBy(entity) || getOwner() == null) {
            super.playerTouch(entity);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Trident", Tag.TAG_COMPOUND)) {
            setStackAndLoyalty(ItemStack.of(compound.getCompound("Trident")));
        }
        dealtDamage = compound.getBoolean("DealtDamage");
        noReturn = compound.getBoolean("NoReturn");
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("Trident", tridentItem.serializeNBT());
        compound.putBoolean("DealtDamage", dealtDamage);
        compound.putBoolean("NoReturn", noReturn);
    }

    @Override
    protected void tickDespawn() {
        if (this.pickup != Pickup.ALLOWED) {
            super.tickDespawn();
        } else if (noReturn && !level().isClientSide) {
            //Drop the item if we despawned after not having been able to return
            spawnAtLocation(getPickupItem(), 0.1F);
            discard();
        }
    }

    @Override
    protected float getWaterInertia() {
        return 0.99F + 0.5F * (getMatterTier() + 1);
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @NotNull
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeItem(tridentItem);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        setStackAndLoyalty(buffer.readItem());
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return tridentItem.copy();
    }
}