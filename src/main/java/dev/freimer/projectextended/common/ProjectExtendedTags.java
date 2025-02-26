package dev.freimer.projectextended.common;

import dev.freimer.projectextended.ProjectExtended;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ProjectExtendedTags {

    private ProjectExtendedTags() {
    }

    public static class Items {

        private Items() {
        }

        /**
         * Items in this tag will be blacklisted from being set as a target in the condenser.
         */
        public static final TagKey<Item> BLACKLIST_CONDENSER = tag("blacklist_condenser");
        /**
         * Items in this tag will be blacklisted from being learned in a transmutation table.
         */
        public static final TagKey<Item> BLACKLIST_LEARNING = tag("blacklist_learning");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ProjectExtended.rl(name));
        }
    }
}