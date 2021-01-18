package gg.galaxygaming.projectextended.common.tag;

import gg.galaxygaming.projectextended.ProjectExtended;
import javax.annotation.Nullable;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ProjectExtendedBlockTagProvider extends BlockTagsProvider {

    public ProjectExtendedBlockTagProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, ProjectExtended.MODID, existingFileHelper);
    }

    @Override
    protected void registerTags() {
    }
}