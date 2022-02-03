package gg.galaxygaming.projectextended.common.registries;

import gg.galaxygaming.projectextended.common.block.AlchemicalBarrel;
import gg.galaxygaming.projectextended.common.registration.BlockDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class ProjectExtendedBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister();

    public static final BlockRegistryObject<AlchemicalBarrel, BlockItem> ALCHEMICAL_BARREL = BLOCKS.register("alchemical_barrel", () -> new AlchemicalBarrel(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(10, 3_600_000)));
}