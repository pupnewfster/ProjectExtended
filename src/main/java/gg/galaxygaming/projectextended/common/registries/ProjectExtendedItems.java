package gg.galaxygaming.projectextended.common.registries;

import gg.galaxygaming.projectextended.common.items.PEShield;
import gg.galaxygaming.projectextended.common.items.PETrident;
import gg.galaxygaming.projectextended.common.registration.ItemDeferredRegister;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.registration.impl.ItemRegistryObject;

public class ProjectExtendedItems {

    public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister();

    public static final ItemRegistryObject<PETrident> DARK_MATTER_TRIDENT = ITEMS.registerNoStackFireImmune("dark_matter_trident", properties -> new PETrident(EnumMatterType.DARK_MATTER, 2, 11, properties));
    public static final ItemRegistryObject<PEShield> DARK_MATTER_SHIELD = ITEMS.registerNoStackFireImmune("dark_matter_shield", properties -> new PEShield(EnumMatterType.DARK_MATTER, properties));

    public static final ItemRegistryObject<PETrident> RED_MATTER_TRIDENT = ITEMS.registerNoStackFireImmune("red_matter_trident", properties -> new PETrident(EnumMatterType.RED_MATTER, 3, 14, properties));
    public static final ItemRegistryObject<PEShield> RED_MATTER_SHIELD = ITEMS.registerNoStackFireImmune("red_matter_shield", properties -> new PEShield(EnumMatterType.RED_MATTER, properties));
}