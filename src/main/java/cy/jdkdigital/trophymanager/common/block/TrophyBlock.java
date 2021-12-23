package cy.jdkdigital.trophymanager.common.block;

import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.TrophyManagerConfig;
import cy.jdkdigital.trophymanager.common.tileentity.TrophyBlockEntity;
import cy.jdkdigital.trophymanager.init.ModBlockEntities;
import cy.jdkdigital.trophymanager.init.ModBlocks;
import cy.jdkdigital.trophymanager.init.ModTags;
import cy.jdkdigital.trophymanager.network.Networking;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TrophyBlock extends Block implements IWaterLoggable
{
    protected static final VoxelShape SLAB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.9D, 16.0D);

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
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(BeehiveBlock.FACING, context.getHorizontalDirection().getOpposite()).setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return SLAB;
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
        if (!world.isClientSide() && tileEntity instanceof TrophyBlockEntity) {
            ((TrophyBlockEntity) tileEntity).loadData(stack.getOrCreateTag());
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
            try {
                CompoundNBT tag = tileEntity.save(new CompoundNBT());
                stack.setTag(tag.getCompound("TrophyData"));
            } catch (Exception e) {
                // client side can crash if data is not yet received
            }
        }
        return stack;
    }

    @Override
    public ActionResultType use(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult traceResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.getItem() instanceof BlockItem) {
            Block heldBlock = ((BlockItem) heldItem.getItem()).getBlock();
            if (heldBlock.is(ModTags.TROPHY_BASE)) {
                TileEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof TrophyBlockEntity) {
                    ((TrophyBlockEntity) blockEntity).baseBlock = heldBlock.getRegistryName();
                    return ActionResultType.SUCCESS;
                }
            }
        }

        if (!world.isClientSide() && heldItem.getItem() instanceof ArmorItem) {
            TileEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TrophyBlockEntity) {
                return ((TrophyBlockEntity) blockEntity).equipArmor(heldItem);
            }
        }

        if (!world.isClientSide() && (heldItem.getItem() instanceof ToolItem || heldItem.getItem() instanceof ShieldItem)) {
            TileEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TrophyBlockEntity) {
                return ((TrophyBlockEntity) blockEntity).equipTool(heldItem);
            }
        }

        if (!world.isClientSide() && player instanceof ServerPlayerEntity) {
            if (TrophyManagerConfig.GENERAL.allowNonOpEdit.get() || player.hasPermissions(2)) {
                final TileEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof TrophyBlockEntity) {
                    Networking.sendToClient(new Networking.PacketOpenGui(blockEntity.getBlockPos()), (ServerPlayerEntity) player);
                    return ActionResultType.SUCCESS;
                }
            }
        }

        return ActionResultType.PASS;
    }

    @Override
    public void neighborChanged(BlockState state, World level, BlockPos pos, Block block, BlockPos otherPos, boolean condition) {
        if (!level.isClientSide) {
            if (level.hasNeighborSignal(pos)) {
                TileEntity te = level.getBlockEntity(pos);
                if (te instanceof TrophyBlockEntity) {
                    if (((TrophyBlockEntity) te).trophyType.equals("entity")) {
                        String entity = ((TrophyBlockEntity) te).entityTag.getString("entityType");
                        switch (entity) {
                            case "minecraft:creeper":
                                level.playSound(null, pos, SoundEvents.CREEPER_PRIMED, SoundCategory.HOSTILE, 1.0F, 1.0F);
                                break;
                            case "minecraft:iron_golem":
                                level.playSound(null, pos, SoundEvents.IRON_GOLEM_DAMAGE, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                                break;
                            case "minecraft:snow_golem":
                                level.playSound(null, pos, SoundEvents.SNOW_GOLEM_SHOOT, SoundCategory.HOSTILE, 1.0F, 1.0F);
                                break;
                            case "minecraft:ender_dragon":
                                level.playSound(null, pos, SoundEvents.ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 1.0F, 1.0F);
                                break;
                            case "minecraft:bee":
                                level.playSound(null, pos, SoundEvents.BEEHIVE_WORK, SoundCategory.HOSTILE, 1.0F, 1.0F);
                                break;
                            case "minecraft:hoglin":
                                level.playSound(null, pos, SoundEvents.HOGLIN_ANGRY, SoundCategory.HOSTILE, 1.0F, 1.0F);
                                break;
                            case "minecraft:zoglin":
                                level.playSound(null, pos, SoundEvents.ZOGLIN_ANGRY, SoundCategory.HOSTILE, 1.0F, 1.0F);
                                break;
                            case "minecraft:slime":
                                level.playSound(null, pos, SoundEvents.SLIME_SQUISH, SoundCategory.HOSTILE, 1.0F, 1.0F);
                                break;
                            case "minecraft:turtle":
                                level.playSound(null, pos, SoundEvents.TURTLE_AMBIENT_LAND, SoundCategory.HOSTILE, 1.0F, 1.0F);
                                break;
                            case "minecraft:llama":
                            case "minecraft:trader_llama":
                                if (level.random.nextInt(10) == 1) {
                                    level.playSound(null, pos, SoundEvents.LLAMA_SPIT, SoundCategory.HOSTILE, 1.0F, 1.0F);
                                    break;
                                }
                            case "minecraft:tropical_fish":
                            case "minecraft:pufferfish":
                                level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundCategory.HOSTILE, 1.0F, 1.0F);
                                break;
                            case "minecraft:ghast":
                                if (level.random.nextInt(10) == 1) {
                                    level.playSound(null, pos, SoundEvents.GHAST_SHOOT, SoundCategory.HOSTILE, 1.0F, 1.0F);
                                } else {
                                    level.playSound(null, pos, SoundEvents.GHAST_WARN, SoundCategory.HOSTILE, 1.0F, 1.0F);
                                }
                                break;
                            default:
                                Entity e = ((TrophyBlockEntity) te).getEntity();
                                if (e instanceof MobEntity) {
                                    SoundEvent sound = ((MobEntity) e).getAmbientSound();
                                    if (sound != null) {
                                        level.playSound(null, pos, sound, SoundCategory.HOSTILE, 1.0F, 1.0F);
                                    }
                                }
                                break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void fillItemCategory(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        String[] entities = {"bat", "bee", "blaze", "cat", "cave_spider", "chicken", "cow", "creeper", "dolphin", "donkey", "drowned", "elder_guardian", "ender_dragon", "enderman", "endermite", "evoker", "fox", "ghast", "guardian", "hoglin", "horse", "husk", "illusioner", "iron_golem", "llama", "magma_cube", "mule", "mooshroom", "ocelot", "panda", "parrot", "phantom", "pig", "piglin", "piglin_brute", "pillager", "polar_bear", "pufferfish", "rabbit", "ravager", "sheep", "shulker", "silverfish", "skeleton", "skeleton_horse", "slime", "snow_golem", "spider", "squid", "stray", "strider", "trader_llama", "tropical_fish", "turtle", "vex", "villager", "vindicator", "wandering_trader", "witch", "wither", "wither_skeleton", "wolf", "zoglin", "zombie", "zombie_horse", "zombie_villager", "zombified_piglin"};
        for (String entityId: entities) {
            items.add(createTrophy("minecraft:" + entityId, new CompoundNBT(), idToName("minecraft:" + entityId)));
        }
    }

    public static ItemStack createTrophy(Entity entity, CompoundNBT tag) {
        ITextComponent name = entity.getDisplayName();
        return createTrophy(entity.getEncodeId(), tag, name.getString());
    }

    public static ItemStack createTrophy(String entityId, CompoundNBT tag, String name) {
        String identifier = entityId;
        CompoundNBT entityTag = new CompoundNBT();
        for (String s : TrophyManagerConfig.GENERAL.nbtMap.get()) {
            String[] nbtInfo = s.split(":");
            if (nbtInfo.length == 3 && (nbtInfo[0] + ":" + nbtInfo[1]).equals(entityId) && tag.contains(nbtInfo[2]) && tag.get(nbtInfo[2]) != null) {
                entityTag.put(nbtInfo[2], tag.get(nbtInfo[2]));
                identifier = identifier + ":" + tag.get(nbtInfo[2]).getAsString();
            }
        }

        CompoundNBT trophyTag = new CompoundNBT();
        ItemStack trophy = new ItemStack(ModBlocks.TROPHY.get());
        trophyTag.putString("TrophyType", "entity");
        entityTag.putString("entityType", entityId);
        switch (entityId) {
            case "minecraft:ender_dragon":
                trophyTag.putFloat("Scale", 0.1f);
                trophyTag.putDouble("OffsetY", 0.8d);
                break;
            case "minecraft:ghast":
                trophyTag.putFloat("Scale", 0.4f);
                trophyTag.putDouble("OffsetY", 1.4d);
                break;
            case "minecraft:bee":
            case "minecraft:vex":
            case "minecraft:phantom":
                trophyTag.putDouble("OffsetY", 0.8d);
                break;
            case "minecraft:pufferfish":
                entityTag.putInt("PuffState", 1);
                break;
            case "minecraft:shulker":
                entityTag.putInt("Color", 2);
                entityTag.putInt("Peek", 30);
                break;
        }

        trophyTag.put("TrophyEntity", entityTag);
        trophyTag.putString("Name", name + " trophy");
        trophyTag.putString("identifier", identifier);

        trophy.setTag(trophyTag);

        return trophy;
    }

    private static String idToName(String id) {
        int start = id.indexOf(":") + 1;
        return id.substring(start, start + 1).toUpperCase() + id.substring(start + 1).replace("_", " ");
    }
}
