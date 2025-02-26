package dev.freimer.projectextended.common.registries;

import dev.freimer.projectextended.ProjectExtended;
import dev.freimer.projectextended.common.block_entity.AlchemicalBarrelBlockEntity;
import dev.freimer.projectextended.common.container.AlchemicalBarrelContainer;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;

public class ProjectExtendedContainerTypes {

    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister(ProjectExtended.MODID);

    public static final ContainerTypeRegistryObject<AlchemicalBarrelContainer> ALCHEMICAL_BARREL_CONTAINER = CONTAINER_TYPES.register(ProjectExtendedBlocks.ALCHEMICAL_BARREL, AlchemicalBarrelBlockEntity.class, AlchemicalBarrelContainer::new);
}