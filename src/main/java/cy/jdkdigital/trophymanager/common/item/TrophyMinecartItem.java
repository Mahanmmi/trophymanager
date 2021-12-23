package cy.jdkdigital.trophymanager.common.item;

import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.common.entity.TrophyMinecartEntity;
import cy.jdkdigital.trophymanager.init.ModBlocks;
import cy.jdkdigital.trophymanager.init.ModItems;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.MinecartItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TrophyMinecartItem extends MinecartItem
{
    public TrophyMinecartItem(Properties properties) {
        super(AbstractMinecartEntity.Type.RIDEABLE, properties);
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

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = world.getBlockState(blockpos);
        if (!blockstate.is(BlockTags.RAILS)) {
            return ActionResultType.FAIL;
        } else {
            ItemStack itemstack = context.getItemInHand();
            if (!world.isClientSide) {
                RailShape railshape = blockstate.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock)blockstate.getBlock()).getRailDirection(blockstate, world, blockpos, null) : RailShape.NORTH_SOUTH;
                double d0 = 0.0D;
                if (railshape.isAscending()) {
                    d0 = 0.5D;
                }

                TrophyMinecartEntity minecartEntity = createMinecart(world, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.0625D + d0, (double)blockpos.getZ() + 0.5D);
                if (itemstack.hasCustomHoverName()) {
                    minecartEntity.setCustomName(itemstack.getHoverName());
                }

                ItemStack trophy = new ItemStack(ModBlocks.TROPHY.get());
                trophy.setTag(itemstack.getTag());

                minecartEntity.setTrophy(trophy.serializeNBT());

                world.addFreshEntity(minecartEntity);
            }

            itemstack.shrink(1);
            return ActionResultType.sidedSuccess(world.isClientSide);
        }
    }

    public static TrophyMinecartEntity createMinecart(World level, double xPos, double yPos, double zPos) {
        return new TrophyMinecartEntity(level, xPos, yPos, zPos);
    }

}
