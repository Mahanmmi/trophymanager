package cy.jdkdigital.trophymanager.init;

import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.common.tileentity.TrophyBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class ModBlockEntities
{
    public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, TrophyManager.MODID);

    public static final RegistryObject<TileEntityType<TrophyBlockEntity>> TROPHY = register("trophy", TrophyBlockEntity::new, ModBlocks.TROPHY);

    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<T> factory, Supplier<Block> block) {
        return BLOCK_ENTITY_TYPES.register(name, () ->
            TileEntityType.Builder.of(factory, block.get()).build(null)
        );
    }
}
