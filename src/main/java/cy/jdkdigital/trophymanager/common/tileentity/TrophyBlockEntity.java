package cy.jdkdigital.trophymanager.common.tileentity;

import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TrophyBlockEntity extends BlockEntity
{
    private static final Map<Integer, Entity> cachedEntities = new HashMap<>();

    public String trophyType; // item, entity
    public ItemStack item = null;
    public CompoundTag entity = null;
    public double offsetY;
    public float rotX;
    public float scale;
    public ResourceLocation baseBlock;
    private String name;

    public TrophyBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TROPHY.get(), pos, state);
    }

    @Override
    public AABB getRenderBoundingBox() {
        BlockPos pos = getBlockPos();
        return new AABB(pos, pos.offset(1, 2, 1));
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);

        loadData(tag.getCompound("TrophyData"));
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag tag) {
        super.save(tag);

        CompoundTag trophyTag = new CompoundTag();

        trophyTag.putString("TrophyType", trophyType);

        if (item != null) {
            trophyTag.put("TrophyItem", item.save(new CompoundTag()));
        }

        if (entity != null) {
            trophyTag.put("TrophyEntity", entity);
        }

        trophyTag.putDouble("OffsetY", offsetY);
        trophyTag.putFloat("RotX", rotX);
        trophyTag.putFloat("Scale", scale);
        trophyTag.putString("BaseBlock", baseBlock.toString());
        if (name != null) {
            trophyTag.putString("Name", name);
        }

        tag.put("TrophyData", trophyTag);

        return tag;
    }

    public void loadData(CompoundTag tag) {
        trophyType = tag.contains("TrophyType") ? tag.getString("TrophyType") : "item";

        if (tag.contains("TrophyItem")) {
            CompoundTag itemTag = tag.getCompound("TrophyItem");
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

        if (tag.contains("RotX")) {
            rotX = tag.getFloat("RotX");
        } else {
            rotX = 0.0f;
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
            if (cachedEntity != null) {
                if (cachedEntity instanceof NeutralMob && entity.contains("AngerTime")) {
                    ((NeutralMob) cachedEntity).setRemainingPersistentAngerTime(entity.getInt("AngerTime"));
                } else if (cachedEntity instanceof Shulker && entity.contains("Peek")) {
                    ((Shulker) cachedEntity).setRawPeekAmount(entity.getInt("Peek"));
                }
                try {
                    addPassengers(cachedEntity, entity);
                } catch (Exception e) {
                    // user can fuck it up here, so don't crash
                }
            }
            TrophyBlockEntity.cachedEntities.put(key, cachedEntity);
        }
        return cachedEntities.getOrDefault(key, null);
    }

    private static Entity createEntity(Level world, CompoundTag tag) {
        return createEntity(world, tag.getString("entityType"), tag);
    }

    private static Entity createEntity(Level world, String entityType, CompoundTag tag) {
        EntityType<?> type = EntityType.byString(entityType).orElse(null);
        if (type != null) {
            try {
                Entity loadedEntity = type.create(world);
                if (loadedEntity != null) {
                    loadedEntity.load(tag);
                    return loadedEntity;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private static void addPassengers(Entity vehicle, CompoundTag entityTag) {
        if (entityTag.contains("Passengers")) {
            ListTag passengers = entityTag.getList("Passengers", 10);
            for (int l = 0; l < passengers.size(); ++l) {
                CompoundTag riderTag = passengers.getCompound(l);
                Entity rider = createEntity(TrophyManager.proxy.getWorld(), riderTag.getString("id"), riderTag);
                if (rider != null) {
                    rider.startRiding(vehicle);
                    addPassengers(rider, riderTag);
                }
            }
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.getBlockPos(), -1, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleUpdateTag(pkt.getTag());
    }

    @Override
    @Nonnull
    public CompoundTag getUpdateTag() {
        return this.serializeNBT();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        deserializeNBT(tag);
    }
}
