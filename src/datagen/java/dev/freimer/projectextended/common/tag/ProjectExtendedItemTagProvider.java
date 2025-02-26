package dev.freimer.projectextended.common.tag;

import dev.freimer.projectextended.ProjectExtended;
import dev.freimer.projectextended.common.BlacklistType;
import dev.freimer.projectextended.common.registries.ProjectExtendedBlocks;
import dev.freimer.projectextended.common.registries.ProjectExtendedItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProjectExtendedItemTagProvider extends ItemTagsProvider {

    public ProjectExtendedItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
          CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,@Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, ProjectExtended.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        for (BlacklistType blacklistType : BlacklistType.values()) {
            tag(blacklistType.getBlacklist());
        }
        tag(Tags.Items.BARRELS).add(
              ProjectExtendedBlocks.ALCHEMICAL_BARREL.asItem()
        );
        addTools();
    }

    private void addTools() {
        tag(Tags.Items.TOOLS_SHIELD).add(ProjectExtendedItems.DARK_MATTER_SHIELD.get(), ProjectExtendedItems.RED_MATTER_SHIELD.get());
        tag(Tags.Items.TOOLS_SPEAR).add(ProjectExtendedItems.DARK_MATTER_TRIDENT.get(), ProjectExtendedItems.RED_MATTER_TRIDENT.get());
    }
}