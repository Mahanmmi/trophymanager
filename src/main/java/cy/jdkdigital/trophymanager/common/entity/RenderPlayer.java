package cy.jdkdigital.trophymanager.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class RenderPlayer extends Zombie
{
    public RenderPlayer(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    static final EntityDataAccessor<String> DATA_UUID = SynchedEntityData.defineId(RenderPlayer.class, EntityDataSerializers.STRING);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        super.getEntityData().define(DATA_UUID, "");
    }

    public void setUUIDData(String uuid) {
        this.getEntityData().set(DATA_UUID, uuid);
    }

    public String getUUIDData() {
        return this.getEntityData().get(DATA_UUID);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("uuid")) {
            setUUIDData(tag.getString("uuid"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (!this.getUUIDData().isEmpty()) {
            tag.putString("uuid", this.getUUIDData());
        }
    }
}
