package cy.jdkdigital.trophymanager.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import cy.jdkdigital.trophymanager.common.entity.TrophyMinecartEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;

public class TrophyMinecartRenderer extends MinecartRenderer<TrophyMinecartEntity>
{
    public TrophyMinecartRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    protected void renderMinecartContents(TrophyMinecartEntity entity, float partialTicks, BlockState blockState, MatrixStack poseStack, IRenderTypeBuffer renderTypeBuffer, int packedLight) {
        ItemStack trophy = ItemStack.of(entity.getTrophy());
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.scale(2.25f, 2.25f, 2.25f);
        Minecraft.getInstance().getItemRenderer().renderStatic(trophy, ItemCameraTransforms.TransformType.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, renderTypeBuffer);
    }
}
