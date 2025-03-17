package dev.freimer.projectextended.common.registries;

import dev.freimer.projectextended.ProjectExtended;
import dev.freimer.projectextended.common.entity.PETridentEntity;
import moze_intel.projecte.gameObjs.registration.impl.EntityTypeDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.EntityTypeRegistryObject;
import net.minecraft.SharedConstants;

public class ProjectExtendedEntityTypes {

    public static final EntityTypeDeferredRegister ENTITY_TYPES = new EntityTypeDeferredRegister(ProjectExtended.MODID);

    public static final EntityTypeRegistryObject<PETridentEntity> PE_TRIDENT = ENTITY_TYPES.registerMisc("pe_trident", PETridentEntity::new, builder -> builder
          .sized(0.5F, 0.5F)
          .eyeHeight(0.13F)
          .clientTrackingRange(4)
          .updateInterval(SharedConstants.TICKS_PER_SECOND)
          .fireImmune()
    );
}