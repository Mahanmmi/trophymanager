package cy.jdkdigital.trophymanager.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import cy.jdkdigital.trophymanager.common.tileentity.TrophyBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;

public class TrophyItemStackRenderer extends BlockEntityWithoutLevelRenderer
{
    TrophyBlockEntity blockEntity = null;

    public TrophyItemStackRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(@Nonnull ItemStack stack, @Nonnull ItemTransforms.TransformType transformType, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int packedLightIn, int packedUV) {
        if (blockEntity == null) {
            blockEntity = new TrophyBlockEntity(BlockPos.ZERO, Blocks.CHEST.defaultBlockState());
        }
        blockEntity.loadData(stack.getOrCreateTag());
        matrixStack.pushPose();

        if (transformType.equals(ItemTransforms.TransformType.GUI)) {
            matrixStack.translate(0.42f, 0.4f, 0.42f);
            matrixStack.scale(0.4f, 0.4f, 0.4f);
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(35.0F));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(-45.0F));
        } else {
            matrixStack.translate(0.32f, 0.3f, 0.32f);
            matrixStack.scale(0.3f, 0.3f, 0.3f);
        }

        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(blockEntity, matrixStack, buffer, packedLightIn, packedUV);

        matrixStack.popPose();
    }
}
