package cy.jdkdigital.trophymanager.client.render.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.common.tileentity.TrophyBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class TrophyBlockEntityRenderer extends TileEntityRenderer<TrophyBlockEntity>
{
    public TrophyBlockEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(@Nonnull TrophyBlockEntity trophyTileEntity, float v, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if (trophyTileEntity.trophyType != null) {
            if (trophyTileEntity.trophyType.equals("item") && trophyTileEntity.item != null) {
                renderItem(trophyTileEntity, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
            } else if (trophyTileEntity.trophyType.equals("entity")) {
                Entity entity = trophyTileEntity.getCachedEntity();
                if (entity != null) {
                    renderEntity(trophyTileEntity, matrixStack, buffer, combinedLightIn);
                }
            }
        }

        renderBase(trophyTileEntity, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
    }

    private void renderBase(TrophyBlockEntity trophyTileEntity, MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        Block baseBlock = ForgeRegistries.BLOCKS.getValue(trophyTileEntity.baseBlock);
        if (baseBlock != null) {
            Minecraft.getInstance().getBlockRenderer().renderBlock(baseBlock.defaultBlockState(), matrixStack, buffer, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
        }
    }

    private void renderItem(TrophyBlockEntity trophyTileEntity, MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        double tick = System.currentTimeMillis() / 800.0D;

        matrixStack.pushPose();
        matrixStack.translate(0.5f, trophyTileEntity.offsetY + 0.5D + Math.sin(tick / 25f) / 15f, 0.5f);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) ((tick * 40.0D) % 360)));
        matrixStack.scale(trophyTileEntity.scale, trophyTileEntity.scale, trophyTileEntity.scale);
        Minecraft.getInstance().getItemRenderer().renderStatic(trophyTileEntity.item, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStack, buffer);
        matrixStack.popPose();
    }

    private void renderEntity(TrophyBlockEntity trophyTileEntity, MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int combinedLightIn) {
        float angle = 0;
        if (trophyTileEntity.getLevel() != null) {
            Direction facing = trophyTileEntity.getBlockState().getValue(HorizontalBlock.FACING);
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
        matrixStack.scale(trophyTileEntity.scale, trophyTileEntity.scale, trophyTileEntity.scale);

        if (trophyTileEntity.entity.getString("entityType").equals("minecraft:ender_dragon")) {
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180f));
        }

        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
        entityrenderermanager.setRenderShadow(false);
        entityrenderermanager.render(trophyTileEntity.getCachedEntity(), 0, 0, 0., Minecraft.getInstance().getFrameTime(), 1, matrixStack, buffer, combinedLightIn);

        matrixStack.popPose();
    }
}
