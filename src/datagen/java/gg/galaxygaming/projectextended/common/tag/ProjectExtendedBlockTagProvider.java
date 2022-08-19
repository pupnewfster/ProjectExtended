package gg.galaxygaming.projectextended.common.tag;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ProjectExtendedBlockTagProvider extends BlockTagsProvider {

    public ProjectExtendedBlockTagProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, ProjectExtended.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(Tags.Blocks.BARRELS).add(
              ProjectExtendedBlocks.ALCHEMICAL_BARREL.getBlock()
        );
        tag(BlockTags.GUARDED_BY_PIGLINS).add(
              ProjectExtendedBlocks.ALCHEMICAL_BARREL.getBlock()
        );
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
              ProjectExtendedBlocks.ALCHEMICAL_BARREL.getBlock()
        );
    }
}