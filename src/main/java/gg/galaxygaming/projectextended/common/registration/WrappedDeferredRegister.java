package gg.galaxygaming.projectextended.common.registration;

import gg.galaxygaming.projectextended.ProjectExtended;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.registration.WrappedRegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;

public class WrappedDeferredRegister<T extends IForgeRegistryEntry<T>> {

    @Nonnull
    protected final DeferredRegister<T> internal;

    protected WrappedDeferredRegister(IForgeRegistry<T> registry) {
        internal = DeferredRegister.create(registry, ProjectExtended.MODID);
    }

    protected <I extends T, W extends WrappedRegistryObject<I>> W register(String name, Supplier<? extends I> sup, Function<RegistryObject<I>, W> objectWrapper) {
        return objectWrapper.apply(internal.register(name, sup));
    }

    public void register(IEventBus bus) {
        internal.register(bus);
    }
}