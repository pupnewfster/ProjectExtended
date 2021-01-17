package gg.galaxygaming.projectextended.common.registration;

import moze_intel.projecte.gameObjs.registration.impl.EntityTypeRegistryObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTypeDeferredRegister extends WrappedDeferredRegister<EntityType<?>> {

    public EntityTypeDeferredRegister() {
        super(ForgeRegistries.ENTITIES);
    }

    public <ENTITY extends Entity> EntityTypeRegistryObject<ENTITY> register(String name, EntityType.Builder<ENTITY> builder) {
        return register(name, () -> builder.build(name), EntityTypeRegistryObject::new);
    }
}