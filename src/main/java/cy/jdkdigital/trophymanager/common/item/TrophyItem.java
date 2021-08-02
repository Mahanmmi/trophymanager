package cy.jdkdigital.trophymanager.common.item;

import cy.jdkdigital.trophymanager.TrophyManager;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

public class TrophyItem extends BlockItem
{
    public TrophyItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Nonnull
    @Override
    public ITextComponent getName(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("Name")) {
            return new TranslationTextComponent(tag.getString("Name"));
        }
        return super.getName(stack);
    }
}
