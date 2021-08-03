package cy.jdkdigital.trophymanager.common.tileentity;

import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.init.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TrophyBlockEntity extends TileEntity
{
    private static final Map<Integer, Entity> cachedEntities = new HashMap<>();

    public String trophyType; // item, entity
    public ItemStack item = null;
    public CompoundNBT entity = null;
    public double offsetY;
    public float scale;
    public ResourceLocation baseBlock;
    private String name;

    public TrophyBlockEntity() {
        super(ModBlockEntities.TROPHY.get());
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        BlockPos pos = getBlockPos();
        return new AxisAlignedBB(pos, pos.offset(1, 2, 1));
    }

    @Override
    public void load(@Nonnull BlockState blockState, @Nonnull CompoundNBT tag) {
        super.load(blockState, tag);

        loadData(tag.getCompound("TrophyData"));
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT tag) {
        super.save(tag);

        CompoundNBT trophyTag = new CompoundNBT();

        trophyTag.putString("TrophyType", trophyType);

        if (item != null) {
            trophyTag.put("TrophyItem", item.save(new CompoundNBT()));
        }

        if (entity != null) {
            trophyTag.put("TrophyEntity", entity);
        }

        trophyTag.putDouble("OffsetY", offsetY);
        trophyTag.putFloat("Scale", scale);
        trophyTag.putString("BaseBlock", baseBlock.toString());
        if (name != null) {
            trophyTag.putString("Name", name);
        }

        tag.put("TrophyData", trophyTag);

        return tag;
    }

    public void loadData(CompoundNBT tag) {
        trophyType = tag.contains("TrophyType") ? tag.getString("TrophyType") : "item";

        if (tag.contains("TrophyItem")) {
            CompoundNBT itemTag = tag.getCompound("TrophyItem");
            if (!itemTag.contains("Count")) {
                itemTag.putDouble("Count", 1D);
            }
            item = ItemStack.of(itemTag);
        } else if (this.trophyType.equals("item")) {
            // Default
            item = new ItemStack(Items.ENCHANTED_GOLDEN_APPLE);
        }

        if (tag.contains("TrophyEntity")) {
            entity = tag.getCompound("TrophyEntity");
        }

        if (tag.contains("Scale")) {
            scale = tag.getFloat("Scale");
        } else {
            scale = 0.5f;
        }

        if (tag.contains("OffsetY")) {
            offsetY = tag.getDouble("OffsetY");
        } else {
            offsetY = 0.5d;
        }

        if (tag.contains("BaseBlock")) {
            baseBlock = new ResourceLocation(tag.getString("BaseBlock"));
        } else {
            baseBlock = new ResourceLocation("smooth_stone_slab");
        }

        if (tag.contains("Name")) {
            name = tag.getString("Name");
        }
    }

    public Entity getCachedEntity() {
        int key = entity.hashCode();
        if (!cachedEntities.containsKey(key)) {
            Entity cachedEntity = createEntity(TrophyManager.proxy.getWorld(), entity);
            if (cachedEntity instanceof IAngerable && entity.contains("AngerTime")) {
                ((IAngerable) cachedEntity).setRemainingPersistentAngerTime(entity.getInt("AngerTime"));
            } else if (cachedEntity instanceof ShulkerEntity && entity.contains("Peek")) {
                ((ShulkerEntity) cachedEntity).setRawPeekAmount(entity.getInt("Peek"));
            }
            TrophyBlockEntity.cachedEntities.put(key, cachedEntity);
        }
        return cachedEntities.getOrDefault(key, null);
    }

    public static Entity createEntity(World world, CompoundNBT tag) {
        EntityType<?> type = EntityType.byString(tag.getString("entityType")).orElse(null);
        if (type != null) {
            Entity loadedEntity = type.create(world);
            if (loadedEntity != null) {
                loadedEntity.load(tag);
                return loadedEntity;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getBlockPos(), -1, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(null, pkt.getTag());
    }

    @Override
    @Nonnull
    public CompoundNBT getUpdateTag() {
        return this.serializeNBT();
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        deserializeNBT(tag);
    }
}
