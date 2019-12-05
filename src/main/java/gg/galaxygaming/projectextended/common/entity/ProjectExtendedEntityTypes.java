package gg.galaxygaming.projectextended.common.entity;

import gg.galaxygaming.projectextended.ProjectExtended;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ProjectExtendedEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES, ProjectExtended.MODID);

    public static final RegistryObject<EntityType<PETridentEntity>> PE_TRIDENT = ENTITY_TYPES.register("pe_trident",
          () -> EntityType.Builder.<PETridentEntity>create(PETridentEntity::new, EntityClassification.MISC).size(0.5F, 0.5F).immuneToFire().build("pe_trident"));
}