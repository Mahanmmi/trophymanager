package cy.jdkdigital.trophymanager.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import cy.jdkdigital.trophymanager.common.tileentity.TrophyBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class TrophyBlockEntityRenderer implements BlockEntityRenderer<TrophyBlockEntity>
{
    private PlayerInfo playerInfo;
    PlayerModel<Player> playerModelRegular;
    PlayerModel<Player> playerModelSlim;

    public TrophyBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.playerModelRegular = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false);
        this.playerModelSlim = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
    }

    @Override
    public void render(@Nonnull TrophyBlockEntity trophyTileEntity, float v, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        if (trophyTileEntity.trophyType != null) {
            if (trophyTileEntity.isOnHead) {
                poseStack.translate(0,0.4f, 0);
            }
            if (trophyTileEntity.trophyType.equals("item") && trophyTileEntity.item != null) {
                renderItem(trophyTileEntity, poseStack, buffer, combinedLightIn, combinedOverlayIn);
            } else if (trophyTileEntity.trophyType.equals("entity")) {
                Entity entity = trophyTileEntity.getCachedEntity();
                if (entity != null) {
                    renderEntity(trophyTileEntity, poseStack, buffer, combinedLightIn);
                }
            } else if (trophyTileEntity.trophyType.equals("player")) {
                Entity entity = trophyTileEntity.getCachedEntity();
                if (entity != null) {
                    renderPlayer(trophyTileEntity, poseStack, buffer, combinedLightIn);
                }
            }
        }

        renderBase(trophyTileEntity, poseStack, buffer, combinedLightIn, combinedOverlayIn);
    }

    private void renderBase(TrophyBlockEntity trophyTileEntity, PoseStack poseStack, @Nonnull MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        Block baseBlock = trophyTileEntity.isOnHead ? Blocks.AIR : trophyTileEntity.getBaseBlock();
        if (baseBlock != null) {
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(baseBlock.defaultBlockState(), poseStack, buffer, combinedLightIn, combinedOverlayIn);
        }
    }

    private void renderItem(TrophyBlockEntity trophyTileEntity, PoseStack poseStack, @Nonnull MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        double tick = System.currentTimeMillis() / 800.0D;

        poseStack.pushPose();
        poseStack.translate(0.5f, trophyTileEntity.offsetY + 0.5D + Math.sin(tick / 25f) / 15f, 0.5f);
        poseStack.mulPose(Vector3f.YP.rotationDegrees((float) ((tick * 40.0D) % 360)));
        poseStack.scale(trophyTileEntity.scale, trophyTileEntity.scale, trophyTileEntity.scale);
        Minecraft.getInstance().getItemRenderer().renderStatic(trophyTileEntity.item, ItemTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, poseStack, buffer, 0);
        poseStack.popPose();
    }

    private void renderEntity(TrophyBlockEntity trophyTileEntity, PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int combinedLightIn) {
        float angle = 0;
        if (trophyTileEntity.getLevel() != null) {
            Direction facing = trophyTileEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
            if (facing == Direction.NORTH) {
                angle = 180f;
            } else if (facing == Direction.SOUTH) {
                angle = 0f;
            } else if (facing == Direction.EAST) {
                angle = 90f;
            } else if (facing == Direction.WEST) {
                angle = 270f;
            }
        }

        matrixStack.pushPose();
        matrixStack.translate(0.5f, trophyTileEntity.offsetY, 0.5f);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(angle));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(trophyTileEntity.rotX));
        matrixStack.scale(trophyTileEntity.scale, trophyTileEntity.scale, trophyTileEntity.scale);

        if (trophyTileEntity.entity.getString("entityType").equals("minecraft:ender_dragon")) {
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180f));
        }

        EntityRenderDispatcher entityRendererManager = Minecraft.getInstance().getEntityRenderDispatcher();
        entityRendererManager.setRenderShadow(false);
        Entity cachedEntity = trophyTileEntity.getCachedEntity();
        if (cachedEntity != null) {
            entityRendererManager.render(cachedEntity, 0, 0, 0., Minecraft.getInstance().getFrameTime(), 1, matrixStack, buffer, combinedLightIn);
            renderPassengers(cachedEntity, entityRendererManager, matrixStack, buffer, combinedLightIn);
        }

        matrixStack.popPose();
    }

    private static void renderPassengers(Entity entity, EntityRenderDispatcher entityRendererManager, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn) {
        if (entity.isVehicle()) {
            for(Entity rider : entity.getPassengers()) {
                entity.positionRider(rider);
                entityRendererManager.render(rider, rider.getX(), rider.getY(), rider.getZ(), Minecraft.getInstance().getFrameTime(), 1, matrixStack, buffer, combinedLightIn);
                renderPassengers(rider, entityRendererManager, matrixStack, buffer, combinedLightIn);
            }
        }
    }

    private void renderPlayer(TrophyBlockEntity trophyTileEntity, PoseStack poseStack, @Nonnull MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
//        double tick = System.currentTimeMillis() / 800.0D;
//
//        poseStack.pushPose();
//        poseStack.translate(0.5f, trophyTileEntity.offsetY + 0.5D + Math.sin(tick / 25f) / 15f, 0.5f);
//        poseStack.mulPose(Vector3f.YP.rotationDegrees((float) ((tick * 40.0D) % 360)));
//        poseStack.scale(trophyTileEntity.scale, trophyTileEntity.scale, trophyTileEntity.scale);
//        Minecraft.getInstance().getItemRenderer().renderStatic(trophyTileEntity.item, ItemTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, poseStack, buffer, 0);
//        poseStack.popPose();
    }

    @Nullable
    protected PlayerInfo getPlayerInfo(UUID uuid) {
        if (this.playerInfo == null) {
            this.playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(uuid);
        }

        return this.playerInfo;
    }

    public boolean isSkinLoaded(UUID uuid) {
        PlayerInfo playerinfo = this.getPlayerInfo(uuid);
        return playerinfo != null && playerinfo.isSkinLoaded();
    }

    public ResourceLocation getSkinTextureLocation(UUID uuid) {
        PlayerInfo playerinfo = this.getPlayerInfo(uuid);
        return playerinfo == null ? DefaultPlayerSkin.getDefaultSkin(uuid) : playerinfo.getSkinLocation();
    }
}
