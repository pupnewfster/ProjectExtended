package gg.galaxygaming.projectextended.client;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlocks;
import moze_intel.projecte.utils.RegistryUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ProjectExtendedBlockStateProvider extends BlockStateProvider {

	public ProjectExtendedBlockStateProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, ProjectExtended.MODID, existingFileHelper);
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
	}

	private static String getName(ItemLike itemProvider) {
		return RegistryUtils.getPath(itemProvider.asItem());
	}
}