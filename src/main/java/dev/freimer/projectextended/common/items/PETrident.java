package dev.freimer.projectextended.common.items;

import com.mojang.serialization.Codec;
import dev.freimer.projectextended.common.ProjectExtendedLang;
import dev.freimer.projectextended.common.entity.PETridentEntity;
import dev.freimer.projectextended.common.items.PETrident.TridentMode;
import dev.freimer.projectextended.common.registries.ProjectExtendedDataComponentTypes;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.items.IBarHelper;
import moze_intel.projecte.gameObjs.items.IHasConditionalAttributes;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.gameObjs.items.IModeEnum;
import moze_intel.projecte.utils.ToolHelper;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import net.minecraft.SharedConstants;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//TODO - 1.21: Doesn't trigger the shot trident advancement
public class PETrident extends TridentItem implements IItemCharge, IItemMode<TridentMode>, IBarHelper, IHasConditionalAttributes {

    private static ItemAttributeModifiers createAttributes(EnumMatterType matterType, float damage) {
        return ItemAttributeModifiers.builder().add(
                    Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(BASE_ATTACK_DAMAGE_ID, damage + matterType.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND
              ).add(
                    Attributes.ATTACK_SPEED,
                    new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.7F + 0.2F * matterType.getMatterTier(), AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND
              ).build();
    }

    private final EnumMatterType matterType;
    private final int numCharges;
    private final float attackDamage;

    public PETrident(EnumMatterType matterType, int numCharges, float damage, Properties props) {
        super(props.rarity(Rarity.EPIC)
              .attributes(createAttributes(matterType, damage))
              .component(DataComponents.TOOL, createToolProperties())
        );
        this.matterType = matterType;
        this.numCharges = numCharges;
        //TODO - 1.21: Get this from the attributes instead
        this.attackDamage = matterType.getAttackDamageBonus() + damage;
    }

    public float getAttackDamage(ItemStack stack) {
        return attackDamage + getCharge(stack);
    }

    public int getMatterTier() {
        return matterType.getMatterTier();
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(@NotNull ItemStack stack, @NotNull ItemStack book) {
        return false;
    }

    @Override
    public boolean isPrimaryItemFor(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        return false;
    }

    @Override
    public boolean supportsEnchantment(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        return false;
    }

    @Override
    public <T extends LivingEntity> int damageItem(@NotNull ItemStack stack, int amount, T entity, @NotNull Consumer<Item> onBroken) {
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
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> list, @NotNull TooltipFlag flags) {
        list.add(getToolTip(stack));
    }

    @Override
    public void adjustAttributes(ItemAttributeModifierEvent event) {
        ToolHelper.applyChargeAttributes(event);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level world, @NotNull LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player && getUseDuration(stack, entity) - timeLeft >= THROW_THRESHOLD_TIME) {
            float tridentSpinStrength = EnchantmentHelper.getTridentSpinAttackStrength(stack, player);
            if (tridentSpinStrength > 0 && !canUseRiptide(player)) {
                //If it is riptide, and we can't use it, then don't
                return;
            }
            Holder<SoundEvent> soundEvent = EnchantmentHelper.pickHighestLevel(stack, EnchantmentEffectComponents.TRIDENT_SOUND)
                  .orElse(SoundEvents.TRIDENT_THROW);
            if (!world.isClientSide && tridentSpinStrength == 0) {
                //Modify what trident entity is actually created by super
                PETridentEntity trident = new PETridentEntity(world, player, stack);
                //TODO - 1.21: Where is the +0.5 from
                trident.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F + 0.5F, 1.0F);
                if (player.isCreative()) {
                    trident.pickup = Pickup.CREATIVE_ONLY;
                }

                world.addFreshEntity(trident);
                world.playSound(null, trident, soundEvent.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                if (!player.isCreative()) {
                    player.getInventory().removeItem(stack);
                }
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            if (tridentSpinStrength > 0) {
                float toRadians = (float) Math.PI / 180F;
                float yaw = player.getYRot() * toRadians;
                float pitch = player.getXRot() * toRadians;
                float xVelocity = -Mth.sin(yaw) * Mth.cos(pitch);
                float yVelocity = -Mth.sin(pitch);
                float zVelocity = Mth.cos(yaw) * Mth.cos(pitch);
                float velocity = Mth.sqrt(xVelocity * xVelocity + yVelocity * yVelocity + zVelocity * zVelocity);
                float velocityModifier = tridentSpinStrength / velocity;
                player.push(xVelocity * velocityModifier, yVelocity * velocityModifier, zVelocity * velocityModifier);
                //Modify vanilla call to start the spin attack with the damage our trident applies
                player.startAutoSpinAttack(SharedConstants.TICKS_PER_SECOND, getAttackDamage(stack), stack);
                if (player.onGround()) {
                    player.move(MoverType.SELF, new Vec3(0.0D, 1.1999999F, 0.0D));
                }
                world.playSound(null, player, soundEvent.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
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
        if (getMode(stack) == TridentMode.RIPTIDE && !canUseRiptide(player)) {
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @NotNull
    @Override
    public Projectile asProjectile(@NotNull Level level, @NotNull Position pos, @NotNull ItemStack stack, @NotNull Direction direction) {
        ThrownTrident trident = new PETridentEntity(level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1));
        trident.pickup = AbstractArrow.Pickup.ALLOWED;
        return trident;
    }

    @Override
    public int getEnchantmentLevel(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        if (stack.isEmpty()) {
            return 0;
        }
        TridentMode mode = getMode(stack);
        if (mode.providesLoyalty && enchantment.is(Enchantments.LOYALTY) || mode.enchantment != null && enchantment.is(mode.enchantment)) {
            return Math.max(getCharge(stack) + 1, super.getEnchantmentLevel(stack, enchantment));
        }
        return super.getEnchantmentLevel(stack, enchantment);
    }

    @NotNull
    @Override
    public ItemEnchantments getAllEnchantments(@NotNull ItemStack stack, @NotNull HolderLookup.RegistryLookup<Enchantment> lookup) {
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(super.getAllEnchantments(stack, lookup));
        TridentMode mode = getMode(stack);
        int level = getCharge(stack) + 1;
        if (mode.providesLoyalty) {
            addEnchantment(mutable, lookup, Enchantments.LOYALTY, level);
        }
        if (mode.enchantment != null) {
            addEnchantment(mutable, lookup, mode.enchantment, level);
        }
        return mutable.toImmutable();
    }

    private void addEnchantment(ItemEnchantments.Mutable mutable, HolderLookup.RegistryLookup<Enchantment> lookup, ResourceKey<Enchantment> key, int level) {
        Optional<Holder.Reference<Enchantment>> enchantment = lookup.get(key);
        //noinspection OptionalIsPresent - Capturing lambda
        if (enchantment.isPresent()) {
            mutable.upgrade(enchantment.get(), level);
        }
    }

    @Override
    public DataComponentType<TridentMode> getDataComponentType() {
        return ProjectExtendedDataComponentTypes.TRIDENT_MODE.get();
    }

    @Override
    public TridentMode getDefaultMode() {
        return TridentMode.NORMAL;
    }

    public enum TridentMode implements IModeEnum<TridentMode> {
        /**
         * Acts as a normal trident with a loyalty level based on charge + 1
         */
        NORMAL(ProjectExtendedLang.TRIDENT_MODE_NORMAL, true, null),
        /**
         * Similar to the channeling enchantment, but will also summon lightning even if no entity was hit. Number of lightning bolts is charge + 1
         *
         * @implNote  The red matter trident can summon lightning even when it is not raining. Costs 64 EMC per bolt of lightning
         */
        CHANNELING(ProjectExtendedLang.TRIDENT_MODE_CHANNELING, true, Enchantments.CHANNELING),
        /**
         * Similar to riptide enchantment, but the level is calculated based on charge + 1.
         *
         * @implNote The red matter trident is also able to use riptide when not in the water and not raining.
         */
        RIPTIDE(ProjectExtendedLang.TRIDENT_MODE_RIPTIDE, false, Enchantments.RIPTIDE),
        /**
         * Creates a shockwave when the trident lands (or hits an entity). Damage based on trident's damage, and AOE sized based on charge + 1
         *
         * @implNote  Only hits hostile mobs
         */
        SHOCKWAVE(ProjectExtendedLang.TRIDENT_MODE_SHOCKWAVE, true, null);

        public static final Codec<TridentMode> CODEC = StringRepresentable.fromEnum(TridentMode::values);
        public static final IntFunction<TridentMode> BY_ID = ByIdMap.continuous(TridentMode::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, TridentMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, TridentMode::ordinal);

        @Nullable
        private final ResourceKey<Enchantment> enchantment;
        private final IHasTranslationKey langEntry;
        private final String serializedName;
        private final boolean providesLoyalty;

        TridentMode(IHasTranslationKey langEntry, boolean providesLoyalty, @Nullable ResourceKey<Enchantment> enchantment) {
            this.serializedName = name().toLowerCase(Locale.ROOT);
            this.providesLoyalty = providesLoyalty;
            this.enchantment = enchantment;
            this.langEntry = langEntry;
        }

        @NotNull
        @Override
        public String getSerializedName() {
            return serializedName;
        }

        @Override
        public String getTranslationKey() {
            return langEntry.getTranslationKey();
        }

        @Override
        public TridentMode next(ItemStack stack) {
            return switch (this) {
                case NORMAL -> CHANNELING;
                case CHANNELING -> RIPTIDE;
                case RIPTIDE -> SHOCKWAVE;
                case SHOCKWAVE -> NORMAL;
            };
        }
    }
}