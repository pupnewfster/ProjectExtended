package dev.freimer.projectextended.client;

import dev.freimer.projectextended.ProjectExtended;
import dev.freimer.projectextended.common.registries.ProjectExtendedBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ProjectExtendedBlockStateProvider extends BlockStateProvider {

	public ProjectExtendedBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, ProjectExtended.MODID, existingFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		BlockModelBuilder barrelModel = models().cubeBottomTop(ProjectExtendedBlocks.ALCHEMICAL_BARREL.getName(),
			ProjectExtended.rl("block/alchemical_barrel_side"),
			ProjectExtended.rl("block/alchemical_barrel_bottom"),
			ProjectExtended.rl("block/alchemical_barrel_top")
		);
		BlockModelBuilder openBarrel = models().getBuilder(ProjectExtendedBlocks.ALCHEMICAL_BARREL.getName() + "_open")
			.parent(barrelModel)
			.texture("top", ProjectExtended.rl("block/alchemical_barrel_top_open"));
		directionalBlock(ProjectExtendedBlocks.ALCHEMICAL_BARREL.getBlock(), state -> state.getValue(BlockStateProperties.OPEN) ? openBarrel : barrelModel);

		BlockModelBuilder lantern = models().getBuilder(ProjectExtendedBlocks.INTERDICTION_LANTERN.getName())
			.parent(models().getExistingFile(ResourceLocation.withDefaultNamespace("template_lantern")))
			.texture("lantern", ProjectExtended.rl("block/interdiction_lantern"))
			.renderType("cutout");
		BlockModelBuilder hangingLantern = models().getBuilder(ProjectExtendedBlocks.INTERDICTION_LANTERN.getName() + "_hanging")
			.parent(models().getExistingFile(ResourceLocation.withDefaultNamespace("template_hanging_lantern")))
			.texture("lantern", ProjectExtended.rl("block/interdiction_lantern"))
			.renderType("cutout");

		getVariantBuilder(ProjectExtendedBlocks.INTERDICTION_LANTERN.getBlock())
			.forAllStatesExcept(state -> ConfiguredModel.builder()
				.modelFile(state.getValue(BlockStateProperties.HANGING) ? hangingLantern : lantern)
				.build(), BlockStateProperties.WATERLOGGED);
	}
}