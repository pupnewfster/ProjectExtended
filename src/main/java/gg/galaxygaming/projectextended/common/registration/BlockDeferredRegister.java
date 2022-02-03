package gg.galaxygaming.projectextended.common.registration;

import gg.galaxygaming.projectextended.ProjectExtended;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BlockDeferredRegister extends moze_intel.projecte.gameObjs.registration.impl.BlockDeferredRegister {

    public BlockDeferredRegister() {
        super(ProjectExtended.MODID);
    }

    @Override
    public <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerDefaultProperties(String name, Supplier<? extends BLOCK> blockSupplier,
          BiFunction<BLOCK, Item.Properties, ITEM> itemCreator) {
        return register(name, blockSupplier, block -> itemCreator.apply(block, ItemDeferredRegister.getBaseProperties()));
    }
}