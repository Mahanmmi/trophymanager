package cy.jdkdigital.trophymanager.client.render.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import cy.jdkdigital.trophymanager.common.tileentity.TrophyBlockEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

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

        TileEntityRendererDispatcher.instance.renderItem(blockEntity, matrixStack, buffer, packedLightIn, packedUV);
        matrixStack.popPose();
    }
}
