package gg.galaxygaming.projectextended.common;

import gg.galaxygaming.projectextended.ProjectExtended;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag.Named;
import net.minecraft.world.item.Item;

public class ProjectExtendedTags {

    public static class Items {

        /**
         * Items in this tag will be blacklisted from being set as a target in the condenser.
         */
        public static final Named<Item> BLACKLIST_CONDENSER = tag("blacklist_condenser");
        /**
         * Items in this tag will be blacklisted from being learned in a transmutation table.
         */
        public static final Named<Item> BLACKLIST_LEARNING = tag("blacklist_learning");

        private static Named<Item> tag(String name) {
            return ItemTags.bind(ProjectExtended.rl(name).toString());
        }
    }
}