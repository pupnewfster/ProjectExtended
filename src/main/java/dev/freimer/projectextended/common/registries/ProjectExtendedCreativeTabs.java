package dev.freimer.projectextended.common.registries;

import dev.freimer.projectextended.ProjectExtended;
import dev.freimer.projectextended.common.ProjectExtendedLang;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.impl.CreativeTabDeferredRegister;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class ProjectExtendedCreativeTabs {

	public static final CreativeTabDeferredRegister CREATIVE_TABS = new CreativeTabDeferredRegister(ProjectExtended.MODID, ProjectExtendedCreativeTabs::addToExistingTabs);

	public static final PEDeferredHolder<CreativeModeTab, CreativeModeTab> PROJECT_EXTENDED = CREATIVE_TABS.registerMain(ProjectExtendedLang.PROJECT_EXTENDED, ProjectExtendedItems.DARK_MATTER_TRIDENT, builder ->
			builder.displayItems((displayParameters, output) -> {
				output.accept(ProjectExtendedItems.DARK_MATTER_TRIDENT);
				output.accept(ProjectExtendedItems.DARK_MATTER_SHIELD);
				output.accept(ProjectExtendedItems.RED_MATTER_TRIDENT);
				output.accept(ProjectExtendedItems.RED_MATTER_SHIELD);

				output.accept(ProjectExtendedBlocks.ALCHEMICAL_BARREL);
				output.accept(ProjectExtendedBlocks.INTERDICTION_LANTERN);
			})
	);

	private static void addToExistingTabs(BuildCreativeModeTabContentsEvent event) {
		ResourceKey<CreativeModeTab> tabKey = event.getTabKey();
		if (tabKey == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
			event.accept(ProjectExtendedBlocks.ALCHEMICAL_BARREL);
			event.accept(ProjectExtendedBlocks.INTERDICTION_LANTERN);
		} else if (tabKey == CreativeModeTabs.REDSTONE_BLOCKS) {
			event.accept(ProjectExtendedBlocks.ALCHEMICAL_BARREL);
		} else if (tabKey == CreativeModeTabs.COMBAT) {
			event.accept(ProjectExtendedItems.DARK_MATTER_TRIDENT.get());
			event.accept(ProjectExtendedItems.DARK_MATTER_SHIELD.get());
			event.accept(ProjectExtendedItems.RED_MATTER_TRIDENT.get());
			event.accept(ProjectExtendedItems.RED_MATTER_SHIELD.get());
		}
	}
}