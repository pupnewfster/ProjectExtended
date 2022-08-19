package gg.galaxygaming.projectextended.common;

import gg.galaxygaming.projectextended.ProjectExtended;
import moze_intel.projecte.utils.LazyTagLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class ProjectExtendedTags {

    public static class Items {

        /**
         * Items in this tag will be blacklisted from being set as a target in the condenser.
         */
        public static final TagKey<Item> BLACKLIST_CONDENSER = tag("blacklist_condenser");
        public static final LazyTagLookup<Item> BLACKLIST_CONDENSER_LOOKUP = LazyTagLookup.create(ForgeRegistries.ITEMS, BLACKLIST_CONDENSER);
        /**
         * Items in this tag will be blacklisted from being learned in a transmutation table.
         */
        public static final TagKey<Item> BLACKLIST_LEARNING = tag("blacklist_learning");
        public static final LazyTagLookup<Item> BLACKLIST_LEARNING_LOOKUP = LazyTagLookup.create(ForgeRegistries.ITEMS, BLACKLIST_LEARNING);

        //Tool Tags
        public static final TagKey<Item> TOOLS_SHIELDS_DARK_MATTER = forgeTag("tools/shields/dark_matter");
        public static final TagKey<Item> TOOLS_SHIELDS_RED_MATTER = forgeTag("tools/shields/red_matter");

        public static final TagKey<Item> TOOLS_TRIDENTS_DARK_MATTER = forgeTag("tools/tridents/dark_matter");
        public static final TagKey<Item> TOOLS_TRIDENTS_RED_MATTER = forgeTag("tools/tridents/red_matter");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ProjectExtended.rl(name));
        }

        private static TagKey<Item> forgeTag(String name) {
            return ItemTags.create(new ResourceLocation("forge", name));
        }
    }
}