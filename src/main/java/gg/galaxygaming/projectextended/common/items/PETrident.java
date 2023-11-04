package gg.galaxygaming.projectextended.common.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import gg.galaxygaming.projectextended.client.rendering.ISTERProvider;
import gg.galaxygaming.projectextended.common.ProjectExtendedLang;
import gg.galaxygaming.projectextended.common.entity.PETridentEntity;
import java.util.List;
import java.util.function.Consumer;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.capability.ChargeItemCapabilityWrapper;
import moze_intel.projecte.capability.ItemCapabilityWrapper;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.items.IBarHelper;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.utils.ToolHelper.ChargeAttributeCache;
import moze_intel.projecte.utils.text.ILangEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PETrident extends TridentItem implements IItemCharge, IItemMode, IBarHelper {

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

    private final ChargeAttributeCache attributeCache = new ChargeAttributeCache();
    private final Multimap<Attribute, AttributeModifier> baseAttributes;
    private final EnumMatterType matterType;
    private final ILangEntry[] modeDesc;
    private final int numCharges;
    private final float attackDamage;

    public PETrident(EnumMatterType matterType, int numCharges, float damage, Properties props) {
        super(props);
        this.matterType = matterType;
        this.numCharges = numCharges;
        this.modeDesc = new ILangEntry[]{
              ProjectExtendedLang.MODE_TRIDENT_1,
              ProjectExtendedLang.MODE_TRIDENT_2,
              ProjectExtendedLang.MODE_TRIDENT_3,
              ProjectExtendedLang.MODE_TRIDENT_4
        };
        this.attackDamage = matterType.getAttackDamageBonus() + damage;
        float attackSpeed = -2.7F + 0.2F * getMatterTier();
        Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", attackSpeed, AttributeModifier.Operation.ADDITION));
        this.baseAttributes = builder.build();
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(ISTERProvider.trident());
    }

    public float getDamage() {
        return attackDamage;
    }

    public int getMatterTier() {
        return matterType.getMatterTier();
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return 0;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public float getWidthForBar(ItemStack stack) {
        return 1 - getChargePercent(stack);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return getScaledBarWidth(stack);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return getColorForBar(stack);
    }

    @Override
    public int getNumCharges(@NotNull ItemStack itemStack) {
        return numCharges;
    }

    @Override
    public ILangEntry[] getModeLangEntries() {
        return modeDesc;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> list, @NotNull TooltipFlag flags) {
        list.add(getToolTip(stack));
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return new ItemCapabilityWrapper(stack, new ChargeItemCapabilityWrapper(), new ModeChangerItemCapabilityWrapper());
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level world, @NotNull LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            int i = this.getUseDuration(stack) - timeLeft;
            if (i >= 10) {
                byte mode = getMode(stack);
                if (mode == RIPTIDE && !canUseRiptide(player)) {
                    //If it is riptide, and we can't use it, then don't
                    return;
                }
                if (!world.isClientSide && mode != RIPTIDE) {
                    //If we are on the server and the mode is not riptide then spawn a trident entity
                    PETridentEntity trident = new PETridentEntity(world, player, stack);
                    trident.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F + 0.5F, 1.0F);
                    if (player.isCreative()) {
                        trident.pickup = Pickup.CREATIVE_ONLY;
                    }

                    world.addFreshEntity(trident);
                    world.playSound(null, trident, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                    if (!player.isCreative()) {
                        player.getInventory().removeItem(stack);
                    }
                }

                player.awardStat(Stats.ITEM_USED.get(this));
                if (mode == RIPTIDE) {
                    int riptideLevel = getCharge(stack) + 1;
                    float toRadians = (float) Math.PI / 180F;
                    float yaw = player.getYRot() * toRadians;
                    float pitch = player.getXRot() * toRadians;
                    float xVelocity = -Mth.sin(yaw) * Mth.cos(pitch);
                    float yVelocity = -Mth.sin(pitch);
                    float zVelocity = Mth.cos(yaw) * Mth.cos(pitch);
                    float velocity = Mth.sqrt(xVelocity * xVelocity + yVelocity * yVelocity + zVelocity * zVelocity);
                    float velocityModifier = (0.75F + 0.75F * riptideLevel) / velocity;
                    player.push(xVelocity * velocityModifier, yVelocity * velocityModifier, zVelocity * velocityModifier);
                    player.startAutoSpinAttack(20);
                    if (player.onGround()) {
                        player.move(MoverType.SELF, new Vec3(0.0D, 1.1999999F, 0.0D));
                    }
                    SoundEvent soundevent;
                    if (riptideLevel >= 3) {
                        soundevent = SoundEvents.TRIDENT_RIPTIDE_3;
                    } else if (riptideLevel == 2) {
                        soundevent = SoundEvents.TRIDENT_RIPTIDE_2;
                    } else {
                        soundevent = SoundEvents.TRIDENT_RIPTIDE_1;
                    }
                    world.playSound(null, player, soundevent, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        }
    }

    private boolean canUseRiptide(Player player) {
        //Only allow riptide to work when the player is wet, or it is a higher tier trident than dark matter
        return getMatterTier() > 0 || player.isInWaterOrRain();
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (getMode(stack) == RIPTIDE && !canUseRiptide(player)) {
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @NotNull
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot equipmentSlot) {
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            //Note: We just entirely bypass the TridentItem's modifiers to set them with our own values for the main hand
            return baseAttributes;
        }
        return super.getDefaultAttributeModifiers(equipmentSlot);
    }

    @NotNull
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@NotNull EquipmentSlot slot, ItemStack stack) {
        return attributeCache.addChargeAttributeModifier(super.getAttributeModifiers(slot, stack), slot, stack);
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }
}