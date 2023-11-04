package gg.galaxygaming.projectextended.client;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlocks;
import moze_intel.projecte.utils.RegistryUtils;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ProjectExtendedBlockStateProvider extends BlockStateProvider {

	public ProjectExtendedBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, ProjectExtended.MODID, existingFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		BlockModelBuilder barrelModel = models().cubeBottomTop(getName(ProjectExtendedBlocks.ALCHEMICAL_BARREL),
			ProjectExtended.rl("block/alchemical_barrel_side"),
			ProjectExtended.rl("block/alchemical_barrel_bottom"),
			ProjectExtended.rl("block/alchemical_barrel_top")
		);
		BlockModelBuilder openBarrel = models().getBuilder(getName(ProjectExtendedBlocks.ALCHEMICAL_BARREL) + "_open").parent(barrelModel)
			.texture("top", ProjectExtended.rl("block/alchemical_barrel_top_open"));
		directionalBlock(ProjectExtendedBlocks.ALCHEMICAL_BARREL.getBlock(), state -> state.getValue(BlockStateProperties.OPEN) ? openBarrel : barrelModel);

		BlockModelBuilder lantern = models().getBuilder(getName(ProjectExtendedBlocks.INTERDICTION_LANTERN))
			.parent(models().getExistingFile(new ResourceLocation("template_lantern")))
			.texture("lantern", ProjectExtended.rl("block/interdiction_lantern"))
			.renderType("cutout");
		BlockModelBuilder hangingLantern = models().getBuilder(getName(ProjectExtendedBlocks.INTERDICTION_LANTERN) + "_hanging")
			.parent(models().getExistingFile(new ResourceLocation("template_hanging_lantern")))
			.texture("lantern", ProjectExtended.rl("block/interdiction_lantern"))
			.renderType("cutout");

		getVariantBuilder(ProjectExtendedBlocks.INTERDICTION_LANTERN.getBlock())
			.forAllStatesExcept(state -> ConfiguredModel.builder()
				.modelFile(state.getValue(BlockStateProperties.HANGING) ? hangingLantern : lantern)
				.build(), BlockStateProperties.WATERLOGGED);
	}

	private static String getName(ItemLike itemProvider) {
		return RegistryUtils.getPath(itemProvider.asItem());
	}
}