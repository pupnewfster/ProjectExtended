package gg.galaxygaming.projectextended.common.tag;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedEntityTypes;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProjectExtendedEntityTypesTagProvider extends EntityTypeTagsProvider {

    public ProjectExtendedEntityTypesTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ProjectExtended.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        tag(EntityTypeTags.IMPACT_PROJECTILES).add(ProjectExtendedEntityTypes.PE_TRIDENT.get());
    }
}