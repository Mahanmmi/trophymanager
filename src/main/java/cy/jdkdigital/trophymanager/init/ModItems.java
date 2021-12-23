package cy.jdkdigital.trophymanager.init;

import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.client.render.item.TrophyItemStackRenderer;
import cy.jdkdigital.trophymanager.common.block.TrophyBlock;
import cy.jdkdigital.trophymanager.common.item.TrophyItem;
import cy.jdkdigital.trophymanager.common.item.TrophyMinecartItem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TrophyManager.MODID);

    public static final RegistryObject<Item> TROPHY_MINECART = ITEMS.register("trophy_minecart", () -> new TrophyMinecartItem(new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
}
