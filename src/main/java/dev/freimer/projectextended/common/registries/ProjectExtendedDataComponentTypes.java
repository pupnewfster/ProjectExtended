package dev.freimer.projectextended.common.registries;

import dev.freimer.projectextended.ProjectExtended;
import dev.freimer.projectextended.common.items.PETrident.TridentMode;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.impl.DataComponentTypeDeferredRegister;
import net.minecraft.core.component.DataComponentType;

public class ProjectExtendedDataComponentTypes {

    public static final DataComponentTypeDeferredRegister DATA_COMPONENT_TYPES = new DataComponentTypeDeferredRegister(ProjectExtended.MODID);

    public static final PEDeferredHolder<DataComponentType<?>, DataComponentType<TridentMode>> TRIDENT_MODE = DATA_COMPONENT_TYPES.simple("trident_mode",
          builder -> builder.persistent(TridentMode.CODEC).networkSynchronized(TridentMode.STREAM_CODEC));
}