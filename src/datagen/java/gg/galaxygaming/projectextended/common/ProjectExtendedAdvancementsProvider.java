package gg.galaxygaming.projectextended.common;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlocks;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ProjectExtendedAdvancementsProvider extends AdvancementProvider {

	public ProjectExtendedAdvancementsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, existingFileHelper);
	}

	@Override
	protected void registerAdvancements(@Nonnull Consumer<Advancement> advancementConsumer, @Nonnull ExistingFileHelper fileHelper) {
		Advancement.Builder.advancement()
			.parent(PECore.rl("alchemical_chest"))
			.display(ProjectExtendedBlocks.ALCHEMICAL_BARREL, ProjectExtendedLang.ADVANCEMENTS_ALCHEMICAL_BARREL.translate(),
				ProjectExtendedLang.ADVANCEMENTS_ALCHEMICAL_BARREL_DESCRIPTION.translate(), null, FrameType.TASK, true, true, false)
			.addCriterion("alchemical_barrel", InventoryChangeTrigger.TriggerInstance.hasItems(ProjectExtendedBlocks.ALCHEMICAL_BARREL))
			.save(advancementConsumer, ProjectExtended.rl("alchemical_barrel"), fileHelper);
	}
}