package gg.galaxygaming.projectextended.common.entity;

import gg.galaxygaming.projectextended.common.items.PETrident;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedEntityTypes;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedItems;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * Modified copy of vanilla's {@link TridentEntity} so as to cut some things out, but also allow for overriding things and then being able to properly still call super
 * without it then just going back into {@link TridentEntity} that we were changing anyways.
 */
public class PETridentEntity extends AbstractArrowEntity implements IEntityAdditionalSpawnData {

    private static final Predicate<Entity> SLAY_MOB = entity -> !entity.isSpectator() && entity instanceof IMob;
    private static final DataParameter<Boolean> ENCHANTED = EntityDataManager.createKey(PETridentEntity.class, DataSerializers.BOOLEAN);
    private ItemStack thrownStack = new ItemStack(ProjectExtendedItems.DARK_MATTER_TRIDENT);
    private boolean landed;
    private boolean noReturn;
    private int loyaltyLevel;
    private int matterTier;
    public int returningTicks;

    public PETridentEntity(EntityType<? extends PETridentEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public PETridentEntity(World world, LivingEntity thrower, ItemStack thrownStackIn) {
        super(ProjectExtendedEntityTypes.PE_TRIDENT.get(), thrower, world);
        setStackAndLoyalty(thrownStackIn.copy());
        this.dataManager.set(ENCHANTED, thrownStackIn.hasEffect());
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(ENCHANTED, false);
    }

    private void setStackAndLoyalty(@Nonnull ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof PETrident) {
            PETrident trident = (PETrident) stack.getItem();
            thrownStack = stack;
            matterTier = trident.getMatterTier();
            loyaltyLevel = trident.getCharge(stack) + 1;
        }
    }

    public int getMatterTier() {
        return matterTier;
    }

    public boolean hasEffect() {
        return this.dataManager.get(ENCHANTED);
    }

    @Override
    public void tick() {
        if (timeInGround > 4) {
            landed = true;
            noReturn = !shouldReturnToThrower();
        }
        //If we aren't told to not do any return logic AND we either landed or are already on the return trip
        if (!noReturn && (landed || getNoClip())) {
            Entity entity = func_234616_v_();
            //Make it return to the thrower
            if (entity != null) {
                if (shouldReturnToThrower()) {
                    setNoClip(true);
                    Vector3d returnVector = new Vector3d(entity.getPosX() - getPosX(), entity.getPosYEye() - getPosY(), entity.getPosZ() - getPosZ());
                    setRawPosition(getPosX(), getPosY() + returnVector.y * 0.015D * loyaltyLevel, getPosZ());
                    if (world.isRemote) {
                        lastTickPosY = getPosY();
                    }
                    setMotion(getMotion().scale(0.95D).add(returnVector.normalize().scale(0.05D * loyaltyLevel)));
                    if (returningTicks == 0) {
                        //Play the return sound if this is our first tick returning
                        playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
                    }
                    returningTicks++;
                } else {
                    //If we shouldn't return to the thrower but we already are on the way back just drop the item
                    if (!world.isRemote && pickupStatus == PickupStatus.ALLOWED) {
                        entityDropItem(getArrowStack(), 0.1F);
                    }
                    remove();
                }
            }
        }
        super.tick();
    }

    private boolean shouldReturnToThrower() {
        Entity entity = func_234616_v_();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayerEntity) || !entity.isSpectator();
        }
        return false;
    }

    @Nonnull
    @Override
    protected ItemStack getArrowStack() {
        return this.thrownStack.copy();
    }

    @Nullable
    @Override
    protected EntityRayTraceResult rayTraceEntities(@Nonnull Vector3d startVec, @Nonnull Vector3d endVec) {
        return this.landed ? null : super.rayTraceEntities(startVec, endVec);
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult result) {
        Entity hitEntity = result.getEntity();
        PETrident trident = (PETrident) thrownStack.getItem();
        int charge = trident.getCharge(thrownStack);
        float damage = trident.getDamage() + charge;
        if (hitEntity instanceof LivingEntity) {
            LivingEntity livingHit = (LivingEntity) hitEntity;
            //Even though we can't be enchanted normally, apply enchantment modifiers anyways
            damage += EnchantmentHelper.getModifierForCreature(this.thrownStack, livingHit.getCreatureAttribute());
        }
        Entity thrower = func_234616_v_();
        DamageSource damagesource = DamageSource.causeTridentDamage(this, thrower == null ? this : thrower);
        landed = true;
        if (hitEntity.attackEntityFrom(damagesource, damage)) {
            //Vanilla's trident exits on endermen here, we allow hitting them instead
            if (hitEntity instanceof LivingEntity) {
                LivingEntity livingHit = (LivingEntity) hitEntity;
                if (thrower instanceof LivingEntity) {
                    EnchantmentHelper.applyThornEnchantments(livingHit, thrower);
                    EnchantmentHelper.applyArthropodEnchantments((LivingEntity) thrower, livingHit);
                }
                this.arrowHit(livingHit);
            }
        }
        setMotion(getMotion().mul(-0.01D, -0.1D, -0.01D));
        float volume = 1.0F;
        SoundEvent sound = SoundEvents.ITEM_TRIDENT_HIT;
        byte mode = trident.getMode(thrownStack);
        if (mode == PETrident.CHANNELING) {
            if (trySummonLightning(charge + 1, hitEntity.getPosition(), thrower instanceof ServerPlayerEntity ? (ServerPlayerEntity) thrower : null)) {
                sound = SoundEvents.ITEM_TRIDENT_THUNDER;
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
    protected void func_230299_a_(BlockRayTraceResult result) {
        inBlockState = world.getBlockState(result.getPos());
        inBlockState.onProjectileCollision(world, inBlockState, result, this);
        Vector3d motion = result.getHitVec().subtract(getPosX(), getPosY(), getPosZ());
        setMotion(motion);
        Vector3d vec3d1 = motion.normalize().scale(0.05F);
        setRawPosition(getPosX() - vec3d1.x, getPosY() - vec3d1.y, getPosZ() - vec3d1.z);
        //Vanilla Copy end

        SoundEvent sound = getHitGroundSound();
        float volume = 1.0F;
        float pitch = 1.2F / (rand.nextFloat() * 0.2F + 0.9F);

        BlockPos hitPosition = result.getPos();
        Entity thrower = func_234616_v_();
        //If we hit a block try
        PETrident trident = (PETrident) thrownStack.getItem();
        int charge = trident.getCharge(thrownStack);
        byte mode = trident.getMode(thrownStack);
        if (mode == PETrident.CHANNELING) {
            if (trySummonLightning(charge + 1, hitPosition.up(), thrower instanceof ServerPlayerEntity ? (ServerPlayerEntity) thrower : null)) {
                sound = SoundEvents.ITEM_TRIDENT_THUNDER;
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
        arrowShake = 7;
        setIsCritical(false);
        setPierceLevel((byte) 0);
        setHitSound(SoundEvents.ENTITY_ARROW_HIT);
        setShotFromCrossbow(false);
        func_213870_w();
    }

    private boolean trySummonLightning(int bolts, BlockPos hitPos, @Nullable ServerPlayerEntity thrower) {
        if (world instanceof ServerWorld) {
            //Allow for channeling to take place if we are red matter or it is thundering
            if (matterTier > 0 || world.isThundering()) {
                //Note: uses canBlockSeeSky instead of isSkyLightMax like the vanilla trident does to fix not being able
                // to cause lightning to come down on fish or in the water
                if (world.canBlockSeeSky(hitPos)) {
                    boolean hasAction = false;
                    for (int i = 0; i < bolts; i++) {
                        if (thrower == null || ItemPE.consumeFuel(thrower, thrownStack, 64, true)) {
                            LightningBoltEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
                            if (lightning != null) {
                                lightning.moveForced(Vector3d.copyCenteredHorizontally(hitPos));
                                lightning.setCaster(thrower);
                                world.addEntity(lightning);
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
        if (world instanceof ServerWorld) {
            //Allow for shockwave to be created if we are red matter or the thrower is wet
            if (matterTier > 0 || thrower != null && thrower.isWet()) {
                DamageSource src = DamageSource.causeTridentDamage(this, thrower == null ? this : thrower).setDamageBypassesArmor();
                float damageToDo = damage + charge;
                int distance = charge + 1;
                for (Entity entity : world.getEntitiesInAABBexcluding(thrower, getBoundingBox().grow(distance), SLAY_MOB)) {
                    entity.attackEntityFrom(src, damageToDo);
                }
                AreaEffectCloudEntity particle = new AreaEffectCloudEntity(world, getPosX(), getPosY(), getPosZ());
                particle.setOwner(thrower);
                particle.setParticleData(ParticleTypes.CRIT);
                particle.setRadius(distance);
                particle.setDuration(0);
                world.addEntity(particle);
                return true;
            }
        }
        return false;
    }

    @Nonnull
    @Override
    protected SoundEvent getHitEntitySound() {
        return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
    }

    @Override
    public void onCollideWithPlayer(@Nonnull PlayerEntity entityIn) {
        Entity entity = func_234616_v_();
        if (entity == null || entity.getUniqueID() == entityIn.getUniqueID()) {
            super.onCollideWithPlayer(entityIn);
        }
    }

    @Override
    public void readAdditional(@Nonnull CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("Trident", NBT.TAG_COMPOUND)) {
            setStackAndLoyalty(ItemStack.read(compound.getCompound("Trident")));
        }
        landed = compound.getBoolean("Landed");
        noReturn = compound.getBoolean("NoReturn");
    }

    @Override
    public void writeAdditional(@Nonnull CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.put("Trident", thrownStack.write(new CompoundNBT()));
        compound.putBoolean("Landed", landed);
        compound.putBoolean("NoReturn", noReturn);
    }

    @Override
    protected void func_225516_i_() {
        if (this.pickupStatus != PickupStatus.ALLOWED) {
            super.func_225516_i_();
        } else if (noReturn && !world.isRemote) {
            //Drop the item if we despawned after not having been able to return
            entityDropItem(getArrowStack(), 0.1F);
            remove();
        }
    }

    @Override
    protected float getWaterDrag() {
        return 0.99F + 0.5F * (getMatterTier() + 1);
    }

    //Only used on client
    @Override
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return true;
    }

    @Nonnull
    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeItemStack(thrownStack);
    }

    @Override
    public void readSpawnData(PacketBuffer buffer) {
        setStackAndLoyalty(buffer.readItemStack());
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return thrownStack.copy();
    }
}