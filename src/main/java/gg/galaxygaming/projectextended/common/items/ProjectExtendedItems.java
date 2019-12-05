package gg.galaxygaming.projectextended.common.items;

import gg.galaxygaming.projectextended.ProjectExtended;
import moze_intel.projecte.gameObjs.EnumMatterType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ProjectExtendedItems {

    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, ProjectExtended.MODID);

    private static final ItemGroup cTab = new ItemGroup(ProjectExtended.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(DARK_MATTER_TRIDENT.get());
        }
    };

    private static Item.Properties ip() {
        return new Item.Properties().maxStackSize(1).group(cTab);
    }

    public static final RegistryObject<PETrident> DARK_MATTER_TRIDENT = ITEMS.register("dark_matter_trident", () -> new PETrident(EnumMatterType.DARK_MATTER, 2, 11, ip()));
    public static final RegistryObject<PEShield> DARK_MATTER_SHIELD = ITEMS.register("dark_matter_shield", () -> new PEShield(EnumMatterType.DARK_MATTER, ip()));

    public static final RegistryObject<PETrident> RED_MATTER_TRIDENT = ITEMS.register("red_matter_trident", () -> new PETrident(EnumMatterType.RED_MATTER, 3, 14, ip()));
    public static final RegistryObject<PEShield> RED_MATTER_SHIELD = ITEMS.register("red_matter_shield", () -> new PEShield(EnumMatterType.RED_MATTER, ip()));;
}