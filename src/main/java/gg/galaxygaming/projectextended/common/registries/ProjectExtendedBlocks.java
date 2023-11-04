package gg.galaxygaming.projectextended.common.registries;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.block.AlchemicalBarrel;
import moze_intel.projecte.gameObjs.registration.impl.BlockDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class ProjectExtendedBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(ProjectExtended.MODID);

    public static final BlockRegistryObject<AlchemicalBarrel, BlockItem> ALCHEMICAL_BARREL = BLOCKS.register("alchemical_barrel", () -> new AlchemicalBarrel(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(10, 3_600_000)));
}