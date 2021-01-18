package gg.galaxygaming.projectextended.common;

import gg.galaxygaming.projectextended.ProjectExtended;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;

public class ProjectExtendedTags {

    public static class Items {

        /**
         * Items in this tag will be blacklisted from being set as a target in the condenser.
         */
        public static final INamedTag<Item> BLACKLIST_CONDENSER = tag("blacklist_condenser");
        /**
         * Items in this tag will be blacklisted from being learned in a transmutation table.
         */
        public static final INamedTag<Item> BLACKLIST_LEARNING = tag("blacklist_learning");

        private static INamedTag<Item> tag(String name) {
            return ItemTags.makeWrapperTag(ProjectExtended.rl(name).toString());
        }
    }
}