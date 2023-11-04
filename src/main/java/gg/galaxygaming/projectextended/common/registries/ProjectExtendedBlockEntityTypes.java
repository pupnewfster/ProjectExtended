package gg.galaxygaming.projectextended.common.registries;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.block_entity.AlchemicalBarrelBlockEntity;
import gg.galaxygaming.projectextended.common.block_entity.InterdictionLanternBlockEntity;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;

public class ProjectExtendedBlockEntityTypes {

    public static final BlockEntityTypeDeferredRegister BLOCK_ENTITY_TYPES = new BlockEntityTypeDeferredRegister(ProjectExtended.MODID);

    public static final BlockEntityTypeRegistryObject<AlchemicalBarrelBlockEntity> ALCHEMICAL_BARREL = BLOCK_ENTITY_TYPES.builder(ProjectExtendedBlocks.ALCHEMICAL_BARREL,
          AlchemicalBarrelBlockEntity::new).clientTicker(AlchemicalBarrelBlockEntity::tickClient).serverTicker(AlchemicalBarrelBlockEntity::tickServer).build();
    public static final BlockEntityTypeRegistryObject<InterdictionLanternBlockEntity> INTERDICTION_LANTERN = BLOCK_ENTITY_TYPES.builder(ProjectExtendedBlocks.INTERDICTION_LANTERN,
          InterdictionLanternBlockEntity::new).commonTicker(InterdictionLanternBlockEntity::tick).build();
}