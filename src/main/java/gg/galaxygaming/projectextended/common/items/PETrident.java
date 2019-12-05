package gg.galaxygaming.projectextended.common.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import gg.galaxygaming.projectextended.client.rendering.item.TridentTEISR;
import gg.galaxygaming.projectextended.common.entity.PETridentEntity;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.capability.ChargeItemCapabilityWrapper;
import moze_intel.projecte.capability.ItemCapabilityWrapper;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.utils.ToolHelper;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class PETrident extends TridentItem implements IItemCharge, IItemMode {

    /**
     * Acts as a normal trident with a loyalty level based on charge + 1
     *
     * Note: All below modes except for riptide have the "loyalty" effect
     */
    public static final byte NORMAL = 0;
    /**
     * Similar to the channeling enchantment, but will also summon lightning even if no entity was hit. Number of lightning bolts is charge + 1
     *
     * Note: The red matter trident can summon lightning even when it is not raining. Costs 64 EMC per bolt of lightning
     */
    public static final byte CHANNELING = 1;
    /**
     * Similar to riptide enchantment, but the level is calculated based on charge + 1.
     *
     * Note: The red matter trident is also able to use riptide when not in the water and not raining.
     */
    public static final byte RIPTIDE = 2;
    /**
     * Creates a shockwave when the trident lands (or hits an entity). Damage based on trident's damage, and AOE sized based on charge + 1
     *
     * Note: Only hits hostile mobs
     */
    public static final byte SHOCKWAVE = 3;

    private final EnumMatterType matterType;
    private final String[] modeDesc;
    private final int numCharges;
    private final float attackDamage;
    private final float attackSpeed;

    public PETrident(EnumMatterType matterType, int numCharges, float damage, Properties props) {
        super(props.setTEISR(PETrident::getTEISR));
        this.matterType = matterType;
        this.numCharges = numCharges;
        this.modeDesc = new String[]{
              "projectextended.trident.normal",
              "projectextended.trident.channeling",
              "projectextended.trident.riptide",
              "projectextended.trident.shockwave"
        };
        this.attackDamage = matterType.getAttackDamage() + damage;
        this.attackSpeed = -2.7F + 0.2F * getMatterTier();
    }

    @OnlyIn(Dist.CLIENT)
    private static Callable<ItemStackTileEntityRenderer> getTEISR() {
        return TridentTEISR::new;
    }

    public float getDamage() {
        return attackDamage;
    }

    public int getMatterTier() {
        return matterType.getMatterTier();
    }

    @Override
    public boolean isBookEnchantable(@Nonnull ItemStack stack, @Nonnull ItemStack book) {
        return false;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return 0;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0D - getChargePercent(stack);
    }

    @Override
    public int getNumCharges(@Nonnull ItemStack itemStack) {
        return numCharges;
    }

    @Override
    public String[] getModeTranslationKeys() {
        return modeDesc;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flags) {
        list.add(getToolTip(stack));
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new ItemCapabilityWrapper(stack, new ChargeItemCapabilityWrapper(), new ModeChangerItemCapabilityWrapper());
    }

    @Override
    public void onPlayerStoppedUsing(@Nonnull ItemStack stack, @Nonnull World world, LivingEntity entity, int timeLeft) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            int i = this.getUseDuration(stack) - timeLeft;
            if (i >= 10) {
                byte mode = getMode(stack);
                if (mode == RIPTIDE && !canUseRiptide(player)) {
                    //If it is riptide and we can't use it, then don't
                    return;
                }
                if (!world.isRemote && mode != RIPTIDE) {
                    //If we are on the server and the mode is not riptide then spawn a trident entity
                    PETridentEntity trident = new PETridentEntity(world, player, stack);
                    trident.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 2.5F + 0.5F, 1.0F);
                    if (player.abilities.isCreativeMode) {
                        trident.pickupStatus = PickupStatus.CREATIVE_ONLY;
                    }

                    world.addEntity(trident);
                    world.playMovingSound(null, trident, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    if (!player.abilities.isCreativeMode) {
                        player.inventory.deleteStack(stack);
                    }
                }

                player.addStat(Stats.ITEM_USED.get(this));
                if (mode == RIPTIDE) {
                    int riptideLevel = getCharge(stack) + 1;
                    float toRadians = (float) Math.PI / 180F;
                    float yaw = player.rotationYaw * toRadians;
                    float pitch = player.rotationPitch * toRadians;
                    float xVelocity = -MathHelper.sin(yaw) * MathHelper.cos(pitch);
                    float yVelocity = -MathHelper.sin(pitch);
                    float zVelocity = MathHelper.cos(yaw) * MathHelper.cos(pitch);
                    float velocity = MathHelper.sqrt(xVelocity * xVelocity + yVelocity * yVelocity + zVelocity * zVelocity);
                    float velocityModifier = (0.75F + 0.75F * riptideLevel) / velocity;
                    player.addVelocity(xVelocity * velocityModifier, yVelocity * velocityModifier, zVelocity * velocityModifier);
                    player.startSpinAttack(20);
                    if (player.onGround) {
                        player.move(MoverType.SELF, new Vec3d(0.0D, 1.1999999F, 0.0D));
                    }
                    SoundEvent soundevent;
                    if (riptideLevel >= 3) {
                        soundevent = SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
                    } else if (riptideLevel == 2) {
                        soundevent = SoundEvents.ITEM_TRIDENT_RIPTIDE_2;
                    } else {
                        soundevent = SoundEvents.ITEM_TRIDENT_RIPTIDE_1;
                    }
                    world.playMovingSound(null, player, soundevent, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }
            }
        }
    }

    private boolean canUseRiptide(PlayerEntity player) {
        //Only allow riptide to work when the player is wet or it is a higher tier trident than dark matter
        return getMatterTier() > 0 || player.isWet();
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (getMode(stack) == RIPTIDE && !canUseRiptide(player)) {
            return new ActionResult<>(ActionResultType.FAIL, stack);
        }
        player.setActiveHand(hand);
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Nonnull
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        //Note: We just entirely bypass the TridentItem's modifiers to set them with our own values
        Multimap<String, AttributeModifier> attributes = HashMultimap.create();
        if (equipmentSlot == EquipmentSlotType.MAINHAND) {
            attributes.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", attackDamage, Operation.ADDITION));
            attributes.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", attackSpeed, Operation.ADDITION));
        }
        return attributes;
    }

    @Nonnull
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlotType slot, ItemStack stack) {
        return ToolHelper.addChargeAttributeModifier(super.getAttributeModifiers(slot, stack), slot, stack);
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }
}