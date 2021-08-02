package cy.jdkdigital.trophymanager.client.render.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import cy.jdkdigital.trophymanager.common.tileentity.TrophyBlockEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;

public class TrophyItemStackRenderer extends ItemStackTileEntityRenderer
{
    TrophyBlockEntity blockEntity = null;

    @Override
    public void renderByItem(@Nonnull ItemStack stack, @Nonnull ItemCameraTransforms.TransformType transformType, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int packedLightIn, int packedUV) {
        if (blockEntity == null) {
            blockEntity = new TrophyBlockEntity();
        }
        blockEntity.loadData(stack.getOrCreateTag());
        matrixStack.pushPose();

        if (transformType.equals(ItemCameraTransforms.TransformType.GUI)) {
            matrixStack.translate(0.42f, 0.4f, 0.42f);
            matrixStack.scale(0.4f, 0.4f, 0.4f);
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(35.0F));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(-45.0F));
        } else {
            matrixStack.translate(0.32f, 0.3f, 0.32f);
            matrixStack.scale(0.3f, 0.3f, 0.3f);
        }

        TileEntityRendererDispatcher.instance.renderItem(blockEntity, matrixStack, buffer, packedLightIn, packedUV);
        matrixStack.popPose();
    }
}
