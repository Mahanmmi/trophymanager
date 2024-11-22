package cy.jdkdigital.trophymanager.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import cy.jdkdigital.trophymanager.common.blockentity.TrophyBlockEntity;
import cy.jdkdigital.trophymanager.init.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class TrophyItemStackRenderer extends BlockEntityWithoutLevelRenderer
{
    TrophyBlockEntity blockEntity = null;

    public TrophyItemStackRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(@Nonnull ItemStack stack, @Nonnull ItemDisplayContext transformType, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int packedLightIn, int packedUV) {
        if (!stack.has(DataComponents.CUSTOM_DATA)) {
            return;
        }
        if (blockEntity == null) {
            blockEntity = new TrophyBlockEntity(BlockPos.ZERO, ModBlocks.TROPHY.get().defaultBlockState());
        }
        blockEntity.loadData(stack.get(DataComponents.CUSTOM_DATA).copyTag(), Minecraft.getInstance().level.registryAccess());
        blockEntity.scale = 0.5f;
//        matrixStack.mulPose(Axis.YP.rotationDegrees(180f));

        blockEntity.isOnHead = false;
        if (transformType.equals(ItemDisplayContext.HEAD)) {
            blockEntity.isOnHead = true;
        }

        matrixStack.pushPose();

        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(blockEntity, matrixStack, buffer, packedLightIn, packedUV);

        matrixStack.popPose();
    }
}
