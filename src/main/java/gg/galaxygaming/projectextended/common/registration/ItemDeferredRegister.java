package gg.galaxygaming.projectextended.common.registration;

import gg.galaxygaming.projectextended.common.ProjectExtendedLang;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedItems;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registration.impl.ItemRegistryObject;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemDeferredRegister extends WrappedDeferredRegister<Item> {

    private static final CreativeModeTab creativeTab = new CreativeModeTab(PECore.MODID) {
        @Nonnull
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ProjectExtendedItems.DARK_MATTER_TRIDENT);
        }

        @Nonnull
        @Override
        public Component getDisplayName() {
            //Overwrite the lang key to match the one representing ProjectExtended
            return ProjectExtendedLang.PROJECT_EXTENDED.translate();
        }
    };

    public ItemDeferredRegister() {
        super(ForgeRegistries.ITEMS);
    }

    public static Item.Properties getBaseProperties() {
        return new Item.Properties().tab(creativeTab);
    }

    public <ITEM extends Item> ItemRegistryObject<ITEM> registerNoStackFireImmune(String name, Function<Item.Properties, ITEM> sup) {
        return register(name, sup, properties -> properties.stacksTo(1).fireResistant());
    }

    public <ITEM extends Item> ItemRegistryObject<ITEM> register(String name, Function<Item.Properties, ITEM> sup, UnaryOperator<Item.Properties> propertyModifier) {
        return register(name, () -> sup.apply(propertyModifier.apply(getBaseProperties())));
    }

    public <ITEM extends Item> ItemRegistryObject<ITEM> register(String name, Supplier<? extends ITEM> sup) {
        return register(name, sup, ItemRegistryObject::new);
    }
}