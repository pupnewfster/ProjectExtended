package gg.galaxygaming.projectextended.common.registries;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.block.AlchemicalBarrel;
import gg.galaxygaming.projectextended.common.block.InterdictionLantern;
import moze_intel.projecte.gameObjs.registration.impl.BlockDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class ProjectExtendedBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(ProjectExtended.MODID);

    public static final BlockRegistryObject<AlchemicalBarrel, BlockItem> ALCHEMICAL_BARREL = BLOCKS.register("alchemical_barrel",
          () -> new AlchemicalBarrel(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                .strength(10, 3_600_000)));
    public static final BlockRegistryObject<InterdictionLantern, BlockItem> INTERDICTION_LANTERN = BLOCKS.register("interdiction_lantern",
          () -> new InterdictionLantern(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).forceSolidOn().requiresCorrectToolForDrops().strength(3.5F)
                .sound(SoundType.LANTERN).lightLevel(state -> 15).noOcclusion().pushReaction(PushReaction.DESTROY)));
}