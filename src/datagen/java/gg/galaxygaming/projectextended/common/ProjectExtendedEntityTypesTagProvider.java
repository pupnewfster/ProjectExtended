package gg.galaxygaming.projectextended.common;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedEntityTypes;
import javax.annotation.Nullable;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ProjectExtendedEntityTypesTagProvider extends EntityTypeTagsProvider {

    public ProjectExtendedEntityTypesTagProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, ProjectExtended.MODID, existingFileHelper);
    }

    @Override
    protected void registerTags() {
        getOrCreateBuilder(EntityTypeTags.IMPACT_PROJECTILES).add(ProjectExtendedEntityTypes.PE_TRIDENT.get());
    }
}