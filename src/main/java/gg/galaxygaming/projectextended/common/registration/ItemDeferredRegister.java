package gg.galaxygaming.projectextended.common.registration;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.ProjectExtendedLang;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedItems;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import moze_intel.projecte.gameObjs.registration.impl.ItemRegistryObject;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemDeferredRegister extends moze_intel.projecte.gameObjs.registration.impl.ItemDeferredRegister {

    private static final CreativeModeTab creativeTab = new CreativeModeTab(ProjectExtended.MODID) {
        @NotNull
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ProjectExtendedItems.DARK_MATTER_TRIDENT);
        }

        @NotNull
        @Override
        public Component getDisplayName() {
            //Overwrite the lang key to match the one representing ProjectExtended
            return ProjectExtendedLang.PROJECT_EXTENDED.translate();
        }
    };

    public ItemDeferredRegister() {
        super(ProjectExtended.MODID);
    }

    public static Item.Properties getBaseProperties() {
        return new Item.Properties().tab(creativeTab);
    }

    @Override
    public <ITEM extends Item> ItemRegistryObject<ITEM> register(String name, Function<Item.Properties, ITEM> sup, UnaryOperator<Item.Properties> propertyModifier) {
        return register(name, () -> sup.apply(propertyModifier.apply(getBaseProperties())));
    }
}