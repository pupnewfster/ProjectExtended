package gg.galaxygaming.projectextended.common;

import gg.galaxygaming.projectextended.ProjectExtended;
import moze_intel.projecte.utils.LazyTagLookup;
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

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ProjectExtended.rl(name));
        }
    }
}