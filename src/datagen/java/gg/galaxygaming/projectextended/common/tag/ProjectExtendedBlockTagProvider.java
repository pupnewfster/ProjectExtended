package gg.galaxygaming.projectextended.common.tag;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlocks;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProjectExtendedBlockTagProvider extends BlockTagsProvider {

    public ProjectExtendedBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ProjectExtended.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        tag(Tags.Blocks.BARRELS).add(
              ProjectExtendedBlocks.ALCHEMICAL_BARREL.getBlock()
        );
        tag(BlockTags.GUARDED_BY_PIGLINS).add(
              ProjectExtendedBlocks.ALCHEMICAL_BARREL.getBlock()
        );
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
              ProjectExtendedBlocks.ALCHEMICAL_BARREL.getBlock(),
              ProjectExtendedBlocks.INTERDICTION_LANTERN.getBlock()
        );
    }
}