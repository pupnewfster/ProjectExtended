package gg.galaxygaming.projectextended.common;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlocks;
import java.util.function.Consumer;
import moze_intel.projecte.PECore;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider.AdvancementGenerator;
import org.jetbrains.annotations.NotNull;

public class ProjectExtendedAdvancementsGenerator implements AdvancementGenerator {

	@Override
	public void generate(@NotNull HolderLookup.Provider registries, @NotNull Consumer<Advancement> advancementConsumer, @NotNull ExistingFileHelper fileHelper) {
		Advancement.Builder.advancement()
			.parent(PECore.rl("alchemical_chest"))
			.display(ProjectExtendedBlocks.ALCHEMICAL_BARREL, ProjectExtendedLang.ADVANCEMENTS_ALCHEMICAL_BARREL.translate(),
				ProjectExtendedLang.ADVANCEMENTS_ALCHEMICAL_BARREL_DESCRIPTION.translate(), null, FrameType.TASK, true, true, false)
			.addCriterion("alchemical_barrel", InventoryChangeTrigger.TriggerInstance.hasItems(ProjectExtendedBlocks.ALCHEMICAL_BARREL))
			.save(advancementConsumer, ProjectExtended.rl("alchemical_barrel"), fileHelper);
	}
}