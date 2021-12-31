package gg.galaxygaming.projectextended.common.tag;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.ProjectExtendedTags;
import javax.annotation.Nullable;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ProjectExtendedItemTagProvider extends ItemTagsProvider {

    public ProjectExtendedItemTagProvider(DataGenerator generator, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, blockTagsProvider, ProjectExtended.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(ProjectExtendedTags.Items.BLACKLIST_CONDENSER);
        tag(ProjectExtendedTags.Items.BLACKLIST_LEARNING);
    }
}