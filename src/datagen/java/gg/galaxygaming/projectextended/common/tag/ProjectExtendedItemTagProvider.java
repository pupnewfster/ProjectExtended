package gg.galaxygaming.projectextended.common.tag;

import gg.galaxygaming.projectextended.ProjectExtended;
import gg.galaxygaming.projectextended.common.ProjectExtendedTags;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedBlocks;
import gg.galaxygaming.projectextended.common.registries.ProjectExtendedItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ProjectExtendedItemTagProvider extends ItemTagsProvider {

    public ProjectExtendedItemTagProvider(DataGenerator generator, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, blockTagsProvider, ProjectExtended.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(ProjectExtendedTags.Items.BLACKLIST_CONDENSER);
        tag(ProjectExtendedTags.Items.BLACKLIST_LEARNING);
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