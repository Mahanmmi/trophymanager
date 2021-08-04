package cy.jdkdigital.trophymanager.common.item;

import cy.jdkdigital.trophymanager.client.render.item.TrophyItemStackRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class TrophyItem extends BlockItem
{
    public TrophyItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Nonnull
    @Override
    public Component getName(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("Name")) {
            return new TranslatableComponent(tag.getString("Name"));
        }
        return super.getName(stack);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties()
        {
            final BlockEntityWithoutLevelRenderer myRenderer = new TrophyItemStackRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer()
            {
                return myRenderer;
            }
        });
    }
}
