package cy.jdkdigital.trophymanager.common.entity;

import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.init.ModBlocks;
import cy.jdkdigital.trophymanager.init.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public class TrophyMinecartEntity extends AbstractMinecartEntity
{
    public static final DataParameter<CompoundNBT> TROPHY_STACK = EntityDataManager.defineId(TrophyMinecartEntity.class, DataSerializers.COMPOUND_TAG);
    public CompoundNBT serializedTrophy = null;

    public TrophyMinecartEntity(EntityType<?> entityType, World level) {
        super(entityType, level);
    }

    public TrophyMinecartEntity(World level, double xPos, double yPos, double zPos) {
        super(ModEntities.TROPHY_MINECART.get(), level, xPos, yPos, zPos);
    }

    @Override
    public Type getMinecartType() {
        return Type.RIDEABLE;
    }

    @Override
    public int getDefaultDisplayOffset() {
        return 0;
    }

    @Override
    protected void applyNaturalSlowdown() {
        double d0 = 0.997D;
        this.setDeltaMovement(this.getDeltaMovement().multiply(d0, 0.0D, d0));
    }

    @Override
    public void destroy(DamageSource source) {
        double d0 = getHorizontalDistanceSqr(this.getDeltaMovement());
        if (!source.isFire() && !source.isExplosion() && !(d0 >= (double)0.01F)) {
            super.destroy(source);
            ItemStack trophy = ItemStack.of(getTrophy());
            this.spawnAtLocation(trophy);
        }
    }

    @Override
    public BlockState getDisplayBlockState() {
        Direction dir = getMotionDirection();
        return ModBlocks.TROPHY.get().defaultBlockState().setValue(HorizontalBlock.FACING, dir);
    }

    public void setTrophy(CompoundNBT tag) {
        this.entityData.set(TROPHY_STACK, tag);
    }

    public CompoundNBT getTrophy() {
        return this.entityData.get(TROPHY_STACK);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TROPHY_STACK, new CompoundNBT());
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        tag.put("trophy", getTrophy());
    }


    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        setTrophy(tag.getCompound("trophy"));
    }

    @Nonnull
    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
