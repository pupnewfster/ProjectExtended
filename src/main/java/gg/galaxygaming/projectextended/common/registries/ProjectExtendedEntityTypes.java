package gg.galaxygaming.projectextended.common.registries;

import gg.galaxygaming.projectextended.common.entity.PETridentEntity;
import gg.galaxygaming.projectextended.common.registration.EntityTypeDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.EntityTypeRegistryObject;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ProjectExtendedEntityTypes {

    public static final EntityTypeDeferredRegister ENTITY_TYPES = new EntityTypeDeferredRegister();

    public static final EntityTypeRegistryObject<PETridentEntity> PE_TRIDENT = ENTITY_TYPES.register("pe_trident", EntityType.Builder.<PETridentEntity>of(PETridentEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune());
}