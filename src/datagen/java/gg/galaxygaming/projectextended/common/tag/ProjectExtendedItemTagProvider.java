package gg.galaxygaming.projectextended.common.tag;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.BlacklistType;
import gg.galaxygaming.projectextended.common.ProjectExtendedTags;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlocks;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
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

    @SuppressWarnings("unchecked")
    private void addTools() {
        tag(Tags.Items.TOOLS_SHIELDS).addTags(
              makeTag(ProjectExtendedTags.Items.TOOLS_SHIELDS_DARK_MATTER, ProjectExtendedItems.DARK_MATTER_SHIELD),
              makeTag(ProjectExtendedTags.Items.TOOLS_SHIELDS_RED_MATTER, ProjectExtendedItems.RED_MATTER_SHIELD)
        );
        tag(Tags.Items.TOOLS_TRIDENTS).addTags(
              makeTag(ProjectExtendedTags.Items.TOOLS_TRIDENTS_DARK_MATTER, ProjectExtendedItems.DARK_MATTER_TRIDENT),
              makeTag(ProjectExtendedTags.Items.TOOLS_TRIDENTS_RED_MATTER, ProjectExtendedItems.RED_MATTER_TRIDENT)
        );
    }

    private TagKey<Item> makeTag(TagKey<Item> tag, ItemLike item) {
        tag(tag).add(item.asItem());
        return tag;
    }
}