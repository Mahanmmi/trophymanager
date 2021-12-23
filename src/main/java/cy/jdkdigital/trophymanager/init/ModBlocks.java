package cy.jdkdigital.trophymanager.init;

import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.client.render.item.TrophyItemStackRenderer;
import cy.jdkdigital.trophymanager.common.block.TrophyBlock;
import cy.jdkdigital.trophymanager.common.item.TrophyItem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TrophyManager.MODID);

    public static final RegistryObject<Block> TROPHY = createBlock("trophy", () -> new TrophyBlock(Block.Properties.copy(Blocks.STONE_SLAB).noOcclusion()), ItemGroup.TAB_DECORATIONS);

    public static <B extends Block> RegistryObject<B> createBlock(String name, Supplier<? extends B> supplier, ItemGroup itemGroup) {
        RegistryObject<B> block = BLOCKS.register(name, supplier);

        Item.Properties properties = new Item.Properties().tab(itemGroup).setISTER(() -> TrophyItemStackRenderer::new);

        ModItems.ITEMS.register(name, () -> new TrophyItem(block.get(), properties));

        return block;
    }
}
