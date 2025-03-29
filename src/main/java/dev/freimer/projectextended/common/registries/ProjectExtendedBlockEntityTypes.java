package dev.freimer.projectextended.common.registries;

import dev.freimer.projectextended.ProjectExtended;
import dev.freimer.projectextended.common.block_entity.AlchemicalBarrelBlockEntity;
import dev.freimer.projectextended.common.block_entity.InterdictionLanternBlockEntity;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;

public class ProjectExtendedBlockEntityTypes {

    public static final BlockEntityTypeDeferredRegister BLOCK_ENTITY_TYPES = new BlockEntityTypeDeferredRegister(ProjectExtended.MODID);

    public static final BlockEntityTypeRegistryObject<AlchemicalBarrelBlockEntity> ALCHEMICAL_BARREL = BLOCK_ENTITY_TYPES.builder(ProjectExtendedBlocks.ALCHEMICAL_BARREL, AlchemicalBarrelBlockEntity::new)
          .clientTicker(AlchemicalBarrelBlockEntity::tickClient)
          .serverTicker(AlchemicalBarrelBlockEntity::tickServer)
          .with(PECapabilities.EMC_STORAGE_CAPABILITY, (be, side) -> be)
          .with(ItemHandler.BLOCK, AlchemicalBarrelBlockEntity::getInventory)
          .build();
    public static final BlockEntityTypeRegistryObject<InterdictionLanternBlockEntity> INTERDICTION_LANTERN = BLOCK_ENTITY_TYPES.builder(ProjectExtendedBlocks.INTERDICTION_LANTERN, InterdictionLanternBlockEntity::new)
          .commonTicker(InterdictionLanternBlockEntity::tick)
          .build();
}