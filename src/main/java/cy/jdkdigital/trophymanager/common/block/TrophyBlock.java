package cy.jdkdigital.trophymanager.common.block;

import cy.jdkdigital.trophymanager.common.tileentity.TrophyBlockEntity;
import cy.jdkdigital.trophymanager.init.ModBlockEntities;
import cy.jdkdigital.trophymanager.init.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TrophyBlock extends Block implements IWaterLoggable
{
    protected static final VoxelShape BOTTOM_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);

    public TrophyBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, Boolean.FALSE).setValue(HorizontalBlock.FACING, Direction.NORTH));
    }

    @Override
    public boolean useShapeForLightOcclusion(@Nonnull BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
        state.add(BlockStateProperties.WATERLOGGED).add(HorizontalBlock.FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(BeehiveBlock.FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return BOTTOM_AABB;
    }

    @Override
    public BlockRenderType getRenderShape(@Nonnull BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean isPathfindable(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull PathType pathType) {
        return false;
    }

    @Override
    public void setPlacedBy(World world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity player, @Nonnull ItemStack stack) {
        // Read data from stack
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TrophyBlockEntity) {
            CompoundNBT tag = stack.getOrCreateTag();
            ((TrophyBlockEntity) tileEntity).loadData(tag);
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModBlockEntities.TROPHY.get().create();
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        ItemStack stack = new ItemStack(ModBlocks.TROPHY.get());
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TrophyBlockEntity) {
            CompoundNBT tag = tileEntity.save(new CompoundNBT());
            stack.setTag(tag.getCompound("TrophyData"));
        }
        return stack;
    }

    @Override
    public ActionResultType use(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult traceResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.getItem() instanceof BlockItem) {
            Block heldBlock = ((BlockItem) heldItem.getItem()).getBlock();
            if (heldBlock instanceof SlabBlock) {
                TileEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof TrophyBlockEntity) {
                    ((TrophyBlockEntity) blockEntity).baseBlock = heldBlock.getRegistryName();
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public void fillItemCategory(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        // TODO fill with vanilla entity trophies
    }
}
