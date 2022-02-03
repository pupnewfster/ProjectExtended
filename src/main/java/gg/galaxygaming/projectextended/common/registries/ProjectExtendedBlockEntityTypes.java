package gg.galaxygaming.projectextended.common.registries;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.block_entity.AlchemicalBarrelBlockEntity;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;

public class ProjectExtendedBlockEntityTypes {

    public static final BlockEntityTypeDeferredRegister BLOCK_ENTITY_TYPES = new BlockEntityTypeDeferredRegister(ProjectExtended.MODID);

    public static final BlockEntityTypeRegistryObject<AlchemicalBarrelBlockEntity> ALCHEMICAL_BARREL = BLOCK_ENTITY_TYPES.builder(ProjectExtendedBlocks.ALCHEMICAL_BARREL,
          AlchemicalBarrelBlockEntity::new).clientTicker(AlchemicalBarrelBlockEntity::tickClient).serverTicker(AlchemicalBarrelBlockEntity::tickServer).build();
}