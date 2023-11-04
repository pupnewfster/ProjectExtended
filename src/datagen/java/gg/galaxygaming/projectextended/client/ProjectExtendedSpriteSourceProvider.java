package gg.galaxygaming.projectextended.client;

import gg.galaxygaming.projectextended.ProjectExtended;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

public class ProjectExtendedSpriteSourceProvider extends SpriteSourceProvider {

    private final Set<ResourceLocation> trackedSingles = new HashSet<>();

    public ProjectExtendedSpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, fileHelper, ProjectExtended.MODID);
    }

    @Override
    protected void addSources() {
        SourceList shieldAtlas = atlas(SHIELD_PATTERNS_ATLAS);
        addFiles(shieldAtlas, ProjectExtended.rl("entity/dark_matter_shield"));
        addFiles(shieldAtlas, ProjectExtended.rl("entity/red_matter_shield"));
    }

    protected void addFiles(SourceList atlas, ResourceLocation... resourceLocations) {
        for (ResourceLocation rl : resourceLocations) {
            //Only add this source if we haven't already added it as a direct single file source
            if (trackedSingles.add(rl)) {
                atlas.addSource(new SingleFile(rl, Optional.empty()));
            }
        }
    }

    protected void addDirectory(SourceList atlas, String directory, String spritePrefix) {
        atlas.addSource(new DirectoryLister(directory, spritePrefix));
    }
}