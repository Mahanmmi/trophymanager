package cy.jdkdigital.trophymanager.init;

import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.common.block.TrophyBlock;
import cy.jdkdigital.trophymanager.common.item.TrophyItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, TrophyManager.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, TrophyManager.MODID);

    public static final DeferredHolder<Block, ? extends Block> TROPHY = createBlock("trophy", () -> new TrophyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_STONE_SLAB).noOcclusion()));

    public static DeferredHolder<Block, ? extends Block> createBlock(String name, Supplier<? extends Block> supplier) {
        var block = BLOCKS.register(name, supplier);

        ITEMS.register(name, () -> new TrophyItem(block.get(), new Item.Properties()));

        return block;
    }
}
