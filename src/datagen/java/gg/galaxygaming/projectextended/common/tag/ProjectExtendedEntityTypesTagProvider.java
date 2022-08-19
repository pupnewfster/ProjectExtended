package gg.galaxygaming.projectextended.common.tag;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedEntityTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ProjectExtendedEntityTypesTagProvider extends EntityTypeTagsProvider {

    public ProjectExtendedEntityTypesTagProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, ProjectExtended.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(EntityTypeTags.IMPACT_PROJECTILES).add(ProjectExtendedEntityTypes.PE_TRIDENT.get());
    }
}