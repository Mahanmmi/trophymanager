package cy.jdkdigital.trophymanager.init;

import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.common.block.TrophyBlock;
import cy.jdkdigital.trophymanager.common.item.TrophyItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TrophyManager.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TrophyManager.MODID);

    public static final RegistryObject<Block> TROPHY = createBlock("trophy", () -> new TrophyBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 6.0F).noOcclusion()), CreativeModeTab.TAB_DECORATIONS);

    public static <B extends Block> RegistryObject<B> createBlock(String name, Supplier<? extends B> supplier, CreativeModeTab itemGroup) {
        RegistryObject<B> block = BLOCKS.register(name, supplier);

        ITEMS.register(name, () -> new TrophyItem(block.get(), new Item.Properties().tab(itemGroup)));

        return block;
    }
}
